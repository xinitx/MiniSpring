package org.init.mvc.bind;

import org.init.beans.ConfigurablePropertyAccessor;
import org.init.beans.MutablePropertyValues;
import org.init.beans.PropertyValues;
import org.init.core.lang.Nullable;
import org.init.beans.validation.AbstractPropertyBindingResult;
import org.init.beans.validation.BeanPropertyBindingResult;
import org.init.mvc.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebDataBinder {
	private Object target;
	private Class<?> clz;

	private String objectName;
	@Nullable
	private AbstractPropertyBindingResult bindingResult;


	public WebDataBinder(Object target) {
		this(target,"");
	}
	public WebDataBinder(Object target, String targetName) {
		this.target = target;
		this.objectName = targetName;
		this.clz = this.target.getClass();
	}


    public void bind(HttpServletRequest request, HttpServletResponse response) {
		MutablePropertyValues mpvs = new MutablePropertyValues(WebUtils.getParametersStartingWith(request, null));
		addBindValues(mpvs, request);
		doBind(mpvs);
	}
	private void doBind(PropertyValues mpvs) {
		applyPropertyValues(mpvs);
	}
	protected void applyPropertyValues(PropertyValues mpvs) {
		getPropertyAccessor().setPropertyValues(mpvs);
	}
	protected ConfigurablePropertyAccessor getPropertyAccessor() {
		return this.getInternalBindingResult().getPropertyAccessor();
	}
	protected void addBindValues(PropertyValues mpvs, HttpServletRequest request) {
	}
	protected AbstractPropertyBindingResult getInternalBindingResult() {
		if (this.bindingResult == null) {
			this.bindingResult = new BeanPropertyBindingResult(this.target);
		}
		return this.bindingResult;
	}
}
