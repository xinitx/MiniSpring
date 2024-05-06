package org.init.test;

public class TestTargetBean implements TestInterface{
    TestInterface testProxyCircle;
    public void test()
    {
        System.out.println("org/init/test");
    }

    public TestInterface getTestProxyCircle() {
        return testProxyCircle;
    }

    public void setTestProxyCircle(TestInterface testProxyCircle) {
        this.testProxyCircle = testProxyCircle;
    }
}
