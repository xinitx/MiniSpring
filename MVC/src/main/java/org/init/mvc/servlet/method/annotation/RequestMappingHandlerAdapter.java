package org.init.mvc.servlet.method.annotation;



import org.init.beans.BeansException;
import org.init.beans.factory.InitializingBean;
import org.init.context.ApplicationContext;
import org.init.context.ApplicationContextAware;
import org.init.core.http.converter.HttpMessageConverter;
import org.init.core.lang.Nullable;
import org.init.mvc.bind.WebDataBinder;
import org.init.mvc.bind.annotation.ResponseBody;
import org.init.mvc.bind.support.DefaultDataBinderFactory;
import org.init.mvc.bind.support.WebBindingInitializer;
import org.init.mvc.bind.support.WebDataBinderFactory;
import org.init.mvc.context.support.InvocableHandlerMethod;
import org.init.mvc.servlet.HandlerAdapter;
import org.init.mvc.servlet.ModelAndView;
import org.init.mvc.servlet.method.HandlerMethod;
import org.init.mvc.servlet.method.support.HandlerMethodArgumentResolver;
import org.init.mvc.servlet.method.support.HandlerMethodArgumentResolverComposite;
import org.init.mvc.servlet.method.support.HandlerMethodReturnValueHandler;
import org.init.mvc.servlet.method.support.HandlerMethodReturnValueHandlerComposite;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class RequestMappingHandlerAdapter implements HandlerAdapter, ApplicationContextAware, InitializingBean {
	private ApplicationContext applicationContext= null;
	private WebBindingInitializer webBindingInitializer = null;
	private HttpMessageConverter messageConverter = null;
	@Nullable
	private HandlerMethodArgumentResolverComposite argumentResolvers;

	@Nullable
	private HandlerMethodReturnValueHandlerComposite returnValueHandlers;
	public HttpMessageConverter getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(HttpMessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public RequestMappingHandlerAdapter() {
	}
	public void afterPropertiesSet() {
		List handlers;
		if (this.argumentResolvers == null) {
			handlers = this.getDefaultArgumentResolvers();
			this.argumentResolvers = (new HandlerMethodArgumentResolverComposite()).addResolvers(handlers);
		}
		if (this.returnValueHandlers == null) {
			handlers = this.getDefaultReturnValueHandlers();
			this.returnValueHandlers = (new HandlerMethodReturnValueHandlerComposite()).addHandlers(handlers);
		}
	}
	private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList();
		resolvers.add(new SimpleArgumentResolver());
		return resolvers;
	}
	private List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
		List<HandlerMethodReturnValueHandler> handlers = new ArrayList();
		handlers.add(new SimpleReturnValueHandler(this.messageConverter));
		return handlers;
	}
	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return handleInternal(request, response, (HandlerMethod) handler);
	}

	private ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handler) {
		ModelAndView mv = null;
		try {
			 mv = invokeHandlerMethod(request, response, handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	protected ModelAndView invokeHandlerMethod(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

			WebDataBinderFactory binderFactory = new DefaultDataBinderFactory(this.webBindingInitializer);
			
			Parameter[] methodParameters = handlerMethod.getMethod().getParameters();
			Object[] methodParamObjs = new Object[methodParameters.length];
			InvocableHandlerMethod invocableMethod = new InvocableHandlerMethod(handlerMethod);
			invocableMethod.setDataBinderFactory(binderFactory);
			if (this.argumentResolvers != null) {
				invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
			}

			if (this.returnValueHandlers != null) {
				invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
			}
			ModelAndView mav = new ModelAndView();
			invocableMethod.invokeAndHandle(request, response, mav, new Object[0]);
			return mav;
	}


	public WebBindingInitializer getWebBindingInitializer() {
		return webBindingInitializer;
	}

	public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}


}
