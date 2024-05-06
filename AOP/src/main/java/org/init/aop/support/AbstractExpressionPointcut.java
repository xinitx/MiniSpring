package org.init.aop.support;

import java.io.Serializable;

public abstract class AbstractExpressionPointcut implements ExpressionPointcut, Serializable {
    private String expression;
    private String location;

    public void setLocation( String location) {
        this.location = location;
    }
    public String getLocation() {
        return this.location;
    }

    @Override
    public String getExpression() {
        return expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
        try {
            this.onSetExpression(expression);
        } catch (IllegalArgumentException var3) {
            throw new IllegalArgumentException("Invalid expression at location [" + this.location + "]: " + var3);
        }
    }
    protected void onSetExpression(String expression) throws IllegalArgumentException {
    }
}
