package org.init.beans;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.init.beans.factory.config.ConstructorArgumentValue;
import org.init.beans.factory.config.PropertyValue;
import org.init.beans.factory.support.BeanDefinitionRegistry;
import org.init.core.env.Environment;
import org.init.core.io.ClassPathResource;
import org.init.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XmlBeanDefinitionReader {
    private final BeanDefinitionRegistry beanFactory;
    private static volatile int count = - 1;

    public XmlBeanDefinitionReader(BeanDefinitionRegistry beanFactory) {
        this.beanFactory = beanFactory;
    }
    public void loadBeanDefinitions(String location) {
        loadBeanDefinitions(new ClassPathResource(location));
    }
    public void loadBeanDefinitions(Resource res) {
        try {
            // 获取资源输入流
            InputStream inputStream = res.getInputStream();
            // 创建SAXReader实例
            SAXReader reader = new SAXReader();
            // 使用InputStream读取XML，源代码中会将InputStream包装成InputSource
            Document doc = reader.read(inputStream);
            //源代码是DefaultBeanDefinitionDocumentReader处理
            registerBeanDefinitions(doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据XML文档注册BeanDefinition和aop配置
     * 源代码中是BeanDefinitionDocumentReader和AopNamespaceHandler处理
     * @param doc
     */
    private void registerBeanDefinitions(Document doc) {
        Element rootElement = doc.getRootElement();
        Iterator res = rootElement.elementIterator();
        while (res.hasNext()) {
            Element element = (Element)res.next();
            String beanID=element.attributeValue("id");
            if("pointcut".equals(element.getName())){
                pointcutHandler(element, beanID);
                continue;
            }
            if("aspect".equals(element.getName())){
                aspectHandler(element);
                continue;
            }
            String beanClassName=element.attributeValue("class");
            BeanDefinition beanDefinition=new BeanDefinition(beanID,beanClassName);

            List<Element> constructorElements = element.elements("constructor-arg");
            List<ConstructorArgumentValue> constructorArgumentValues = new ArrayList<ConstructorArgumentValue>();
            for (Element e : constructorElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                constructorArgumentValues.add(new ConstructorArgumentValue(pType,pName,pValue));
            }
            beanDefinition.setConstructorArgumentValues(constructorArgumentValues);

            List<Element> propertyElements = element.elements("property");
            List<PropertyValue> propertyValues = new ArrayList<PropertyValue>();
            List<String> refs = new ArrayList<>();
            for (Element e : propertyElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                System.out.println(pValue);
                String pRef = e.attributeValue("ref");
                String pV = "";
                boolean isRef = false;
                if (pValue != null && !pValue.equals("")) {
                    isRef = false;
                    pV = pValue;
                } else if (pRef != null && !pRef.equals("")) {
                    isRef = true;
                    pV = pRef;
                    refs.add(pRef);
                }
                propertyValues.add(new PropertyValue(pType, pName, pV, isRef));
            }
            beanDefinition.setPropertyValues(propertyValues);
            String[] refArray = refs.toArray(new String[0]);
            beanDefinition.setDependsOn(refArray);
            this.beanFactory.registerBeanDefinition(beanID,beanDefinition);
        }
    }

    private void aspectHandler(Element element) {
        String ref = element.attributeValue("ref");
        String beanID;
        BeanDefinition beanDefinition;
        String beanClassName = "org.init.aop.aspectj.AspectJPointcutAdvisor";
        Iterator<Element> iterator = element.elementIterator();

        while(iterator.hasNext()){
            Element e = iterator.next();
            beanID ="org.init.aop.aspectj.AspectJPointcutAdvisor#" + ++this.count;

            String adviceClassName = "";
            String adviceId = "";
            BeanDefinition adviceBeanDefinition = new BeanDefinition(adviceId,adviceClassName);
            try {
                adviceBeanDefinition.setResolvedTargetType(Class.forName("org.init.aop.aspectj.AbstractAspectJAdvice"));
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            if ("before".equals(e.getName())){
                adviceClassName = "org.init.aop.aspectj.AspectJMethodBeforeAdvice";
            }
            if ("after".equals(e.getName())){
                adviceClassName = "org.init.aop.aspectj.AspectJAfterAdvice";
            }
            adviceBeanDefinition.setClassName(adviceClassName);
            adviceBeanDefinition.setId(adviceClassName + "#" + this.count);
            List<ConstructorArgumentValue> adviceConstructorArgs = new ArrayList<ConstructorArgumentValue>();
            List<PropertyValue> advicePvs = new ArrayList<>();
            String pMethod = e.attributeValue("method");
            String pointcutRef = e.attributeValue("pointcut-ref");



            BeanDefinition methodFactoryBeanDefinition = new BeanDefinition("methodFactoryInnerBean#" + this.count,"org.init.aop.config.MethodLocatingFactoryBean");
            List<PropertyValue> methodFactoryPvs = new ArrayList<PropertyValue>();
            methodFactoryPvs.add(new PropertyValue("String", "targetBeanName", ref, false));
            methodFactoryPvs.add(new PropertyValue("String", "methodName", pMethod, false));
            methodFactoryBeanDefinition.setPropertyValues(methodFactoryPvs);
            try {
                methodFactoryBeanDefinition.setResolvedTargetType(Class.forName("java.lang.reflect.Method"));
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }

            BeanDefinition aspectFactoryBeanDefinition = new BeanDefinition("aspectFactoryInnerBean#" + this.count,"org.init.aop.config.SimpleBeanFactoryAwareAspectInstanceFactory");
            List<PropertyValue> aspectFactoryPvs = new ArrayList<PropertyValue>();
            aspectFactoryPvs.add(new PropertyValue("String", "aspectBeanName", ref, false));
            try {
                aspectFactoryBeanDefinition.setResolvedTargetType(Class.forName("org.init.aop.aspectj.AspectInstanceFactory"));
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            aspectFactoryBeanDefinition.setPropertyValues(aspectFactoryPvs);


            adviceConstructorArgs.add(new ConstructorArgumentValue("java.lang.reflect.Method","&methodName" , methodFactoryBeanDefinition));
            adviceConstructorArgs.add(new ConstructorArgumentValue("org.init.aop.aspectj.AspectJExpressionPointcut", pointcutRef, null));
            adviceConstructorArgs.add(new ConstructorArgumentValue("BeanDefinition", null, aspectFactoryBeanDefinition));
            advicePvs.add(new PropertyValue("String", "aspectName", ref, false));
            adviceBeanDefinition.setPropertyValues(advicePvs);
            adviceBeanDefinition.setConstructorArgumentValues(adviceConstructorArgs);



            beanDefinition = new BeanDefinition(beanID, beanClassName);
            List<ConstructorArgumentValue> constructorArgumentValues = new ArrayList<ConstructorArgumentValue>();
            constructorArgumentValues.add(new ConstructorArgumentValue("BeanDefinition",adviceId, adviceBeanDefinition));
            beanDefinition.setConstructorArgumentValues(constructorArgumentValues);
            this.beanFactory.registerBeanDefinition(beanID,beanDefinition);
        }
    }

    private void pointcutHandler(Element element,String beanID) {
        String beanClassName="org.init.aop.aspectj.AspectJExpressionPointcut";
        BeanDefinition beanDefinition=new BeanDefinition(beanID,beanClassName);
        List<PropertyValue> propertyValues = new ArrayList<PropertyValue>();
        propertyValues.add(new PropertyValue(null, "expression", element.attributeValue("expression"), false));
        beanDefinition.setPropertyValues(propertyValues);
        this.beanFactory.registerBeanDefinition(beanID,beanDefinition);
    }

    public BeanDefinitionRegistry getBeanFactory() {
        return beanFactory;
    }
}
