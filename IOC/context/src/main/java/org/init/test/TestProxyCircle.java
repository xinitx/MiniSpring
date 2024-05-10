package org.init.test;

public class TestProxyCircle implements TestInterface{
    TestInterface testTargetBean;
    public void test(){
         testTargetBean.test();
    }

    public TestInterface getTestTargetBean() {
        return testTargetBean;
    }

    public void setTestTargetBean(TestInterface testTargetBean) {
        this.testTargetBean = testTargetBean;
    }
}
