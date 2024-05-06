package org.init.mvc.context;



import org.init.beans.BeansException;
import org.init.mvc.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextLoaderListener implements ServletContextListener {
	public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
	private WebApplicationContext context;
	
	public ContextLoaderListener() {
	}
	
	public ContextLoaderListener(WebApplicationContext context) {
		this.context = context;
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		initWebApplicationContext(event.getServletContext());
	}

	private void initWebApplicationContext(ServletContext servletContext) {
		String sContextLocation = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);

		XmlWebApplicationContext wac = new XmlWebApplicationContext();
		wac.setServletContext(servletContext);
		wac.setConfigLocation(sContextLocation);
		this.context = wac;
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
		try {
			wac.refresh();
		} catch (BeansException e) {
			throw new RuntimeException(e);
		}
	}
	

}
