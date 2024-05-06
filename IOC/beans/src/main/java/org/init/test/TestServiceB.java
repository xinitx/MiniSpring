package org.init.test;

public class TestServiceB {
    private TestServiceA testService;

    public TestServiceA getTestService() {
        return testService;
    }

    public void setTestService(TestServiceA testService) {
        this.testService = testService;
    }
    public void sayHello() {
        System.out.println("ServiceB says hello");

    }
}
