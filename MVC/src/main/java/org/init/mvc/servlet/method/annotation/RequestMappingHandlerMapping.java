package org.init.mvc.servlet.method.annotation;



import org.init.beans.BeansException;
import org.init.context.ApplicationContext;
import org.init.context.ApplicationContextAware;
import org.init.mvc.bind.annotation.RequestMapping;
import org.init.mvc.servlet.HandlerMapping;
import org.init.mvc.servlet.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class RequestMappingHandlerMapping implements HandlerMapping, ApplicationContextAware {
	ApplicationContext applicationContext;
	private MappingRegistry mappingRegistry = null;
	
	public RequestMappingHandlerMapping() {
	}
	
    protected void initMappings() {
    	Class<?> clz = null;
    	Object obj = null;
    	String[] controllerNames = this.applicationContext.getBeanDefinitionNames();
    	for (String controllerName : controllerNames) {
			try {
				obj = this.applicationContext.getBean(controllerName);
				clz = obj.getClass();
			} catch (BeansException e) {
				e.printStackTrace();
			}
			Method[] methods = clz.getDeclaredMethods();
    		if(methods!=null){
    			for(Method method : methods){
    				boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
    				if (isRequestMapping){
    					String methodName = method.getName();
    					String urlmapping = method.getAnnotation(RequestMapping.class).value();
    					this.mappingRegistry.getUrlMappingNames().add(urlmapping);
    					this.mappingRegistry.getMappingObjs().put(urlmapping, obj);
    					this.mappingRegistry.getMappingMethods().put(urlmapping, method);
    					this.mappingRegistry.getMappingMethodNames().put(urlmapping, methodName);
    					this.mappingRegistry.getMappingClasses().put(urlmapping, clz);
    				}
    			}
    		}
    	}

    }


	@Override
	public HandlerMethod getHandler(HttpServletRequest request) throws Exception {
		if (this.mappingRegistry == null) { //to do initialization
			this.mappingRegistry = new MappingRegistry();
			initMappings();
		}
		String sPath = request.getServletPath();
		if (!this.mappingRegistry.getUrlMappingNames().contains(sPath)) {
			return null;
		}
		Method method = this.mappingRegistry.getMappingMethods().get(sPath);
		Object obj = this.mappingRegistry.getMappingObjs().get(sPath);
		Class<?> clz = this.mappingRegistry.getMappingClasses().get(sPath);
		String methodName = this.mappingRegistry.getMappingMethodNames().get(sPath);
	
		HandlerMethod handlerMethod = new HandlerMethod(method, obj, clz, methodName);
		
		return handlerMethod;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
