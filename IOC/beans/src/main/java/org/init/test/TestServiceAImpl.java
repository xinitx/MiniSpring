package org.init.test;

public class TestServiceAImpl implements TestServiceA {
    private String property1;
    private int property2;
    private String property3;
    private String property4;

    private TestServiceB ref1;
    public TestServiceAImpl() {
    }
    public TestServiceAImpl(String property1, int property2) {
        this.property1 = property1;
        this.property2 = property2;
    }

    @Override
    public void sayHello() {
        System.out.println(this.property3 + " " + this.property4);
    }

    public String getProperty3() {
        return property3;
    }

    public void setProperty3(String property3) {
        this.property3 = property3;
    }

    public String getProperty4() {
        return property4;
    }

    public void setProperty4(String property4) {
        this.property4 = property4;
    }

    public TestServiceB getRef1() {
        return ref1;
    }

    public void setRef1(TestServiceB ref1) {
        this.ref1 = ref1;
    }
}
