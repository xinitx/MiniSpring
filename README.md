# 前言

手动实现一个轻量级Spring框架，深入理解Spring，[Spring源码分析](https://init33.top/blog/框架组件/Spring/)

# 概要设计

## SpringIOC

参照[ClassPathXmlApplicationContext](https://init33.top/blog/框架组件/Spring-ApplicationContext/)、DefaultListableBeanFactory，实现SpringIOC功能

- 目标：xml文件 -> Resource -> BeanDefinition -> Bean
- 流程：因为重点是AbstractApplicationContext的refresh，本ClassPathXmlApplicationContext将传入的路径通过dom4j直接解析成Document类对象，通过XmlBeanDefinitionReader对比标签进行相应的操作，本来不同的命名空间由对应模块的spring.handlers指定的handler进行处理，本XmlBeanDefinitionReader没做命名空间。参照DefaultListableBeanFactory，核心功能getBean，包括三级缓存、Bean创建、Bean生命周期

## SpringAOP

- 目标：Bean -> ProxyBean
- 流程：通过实现了InstantiationAwareBeanPostProcessor接口的DefaultAutoProxyCreator对非内部非aop相关的Bean 与AspectJPointcutAdvisor增强器中的AspectJExpressionPointcut切入点匹配，增强器类中包含了advice通知类和pointcut切入点类，最后将advice织入。

## SpringJDBC

- 目标：DataSource、JdbcTemplate、PooledConnection、PooledDataSource、PoolState
- 流程：将DataSource依赖注入JdbcTemplate，使用JdbcTemplate函数式编程，PoolState实际分类存储PooledConnection，PooledDataSource从PoolState获取空闲的或创建PooledConnection，PooledConnection包装了从UnpooledDataSource获取的Connection进行代理close方法

## SpringMVC

- 目标：DispatcherServlet、RequestMappingHandlerMapping、RequestMappingHandlerAdapter、InternalResourceViewResolver、XmlWebApplicationContext
- 流程：先执行ContextLoaderListener的contextInitialized会创建一个XmlWebApplicationContext，再初始化DispatcherServlet也会创建一个XmlWebApplicationContext，当请求来时服务器转发到FrameworkServlet.service -> FrameworkServlet.processRequest-> FrameworkServlet.doService -> DispatcherServlet.doService -> DispatcherServlet.doDispatch，然后安照HandlerMapping、HandlerAdapter、ViewResolver的顺序处理

# 遇到的问题

## 模块循环依赖

**java: Annotation processing is not supported for module cycles. Please ensure that all modules from cycle [AOP,IOC] are excluded from annotation processing**

1. mvn dependency:analyze识别循环依赖
2. 重构代码：**拆分模块**、**共享模块**、**接口抽象**

## Tomcat配置

1. Project Structure：Modules + web
2. Artifacts：Web Application (exploded)
3. [官网](https://tomcat.apache.org/)下载
4. 添加Run/Debug Configurations：Tomcat Local
5. 选择Deployment：Artifact，注意清除Application context选项
6. 问题：生成的out目录里没有web.xml，只能手动复制过去，尚不清楚原因，需要加<load-on-startup>1</load-on-startup>才能在启动时加载servlet

