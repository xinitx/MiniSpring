package org.init.mvc.servlet;

import org.init.beans.BeansException;
import org.init.context.ApplicationContext;
import org.init.mvc.context.WebApplicationContext;
import org.init.mvc.servlet.method.HandlerMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends FrameworkServlet  {
    public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";
    public static final String HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";
    public static final String VIEW_RESOLVER_BEAN_NAME = "viewResolver";
    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";
    private List<String> packageNames = new ArrayList<String>();
    private HandlerMapping handlerMapping;
    private HandlerAdapter handlerAdapter;
    private ViewResolver viewResolver;
    public DispatcherServlet() {
    }
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.getWebApplicationContext());
        try {
            doDispatch(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        }
    }
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest processedRequest = request;
        HandlerMethod handlerMethod = null;
        ModelAndView mv = null;
        //源码中是HandlerExecutionChain包装了Handler实现拦截器链，这里简化成一个handlerMethod
        //源码是遍历handlerMapping列表，找到一个Handler，包装成HandlerExecutionChain
        handlerMethod = this.handlerMapping.getHandler(processedRequest);
        if (handlerMethod == null) {
            return;
        }
        //源码是遍历handlerAdapter列表，找到一个支持Handler的Adapter
        HandlerAdapter ha = this.handlerAdapter;
        //则会检查请求的Last-Modified时间，并根据情况进行Not Modified的处理
        //进行实际的执行，并获取处理结果
        mv = ha.handle(processedRequest, response, handlerMethod);

        render(processedRequest, response, mv);
    }
    protected void render(HttpServletRequest request, HttpServletResponse response,ModelAndView mv) throws Exception {
        if (mv.getView() == null) {
            response.getWriter().flush();
            response.getWriter().close();
            return;
        }
        String sTarget = mv.getViewName();
        Map<String, Object> modelMap = mv.getModel();
        View view = resolveViewName(sTarget, modelMap, request);
        view.render(modelMap, request, response);

    }
    protected View resolveViewName(String viewName, Map<String, Object> model,
                                   HttpServletRequest request) throws Exception {
        //源码是遍历viewResolver列表，找到一个支持viewName的Resolver
        if (this.viewResolver != null) {
            View view = viewResolver.resolveViewName(viewName);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    protected void onRefresh(ApplicationContext context) {
        this.initStrategies(context);
    }
    protected void initStrategies(ApplicationContext context) {
        System.out.println("DispatcherServlet init");
        initHandlerMappings((WebApplicationContext) context);
        initHandlerAdapters((WebApplicationContext) context);
        initViewResolvers((WebApplicationContext) context);
    }
    protected void initHandlerMappings(WebApplicationContext wac) {
        try {
            this.handlerMapping = (HandlerMapping) wac.getBean(HANDLER_MAPPING_BEAN_NAME);
        } catch (BeansException e) {
            e.printStackTrace();
        }

    }
    protected void initHandlerAdapters(WebApplicationContext wac) {
        try {
            this.handlerAdapter = (HandlerAdapter) wac.getBean(HANDLER_ADAPTER_BEAN_NAME);
        } catch (BeansException e) {
            e.printStackTrace();
        }

    }
    protected void initViewResolvers(WebApplicationContext wac) {
        try {
            this.viewResolver = (ViewResolver) wac.getBean(VIEW_RESOLVER_BEAN_NAME);
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }
}
