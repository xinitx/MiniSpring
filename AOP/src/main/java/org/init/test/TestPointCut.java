package org.init.test;

public class TestPointCut {
    public void before(){
        System.out.println("before");
    }
    public void around(){
        System.out.println("around");
    }
    public void after(){
        System.out.println("after");
    }
    public void afterReturning(){
        System.out.println("afterReturning");
    }
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

}
