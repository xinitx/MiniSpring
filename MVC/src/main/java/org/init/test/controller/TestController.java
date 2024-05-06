package org.init.test.controller;

import org.init.mvc.bind.annotation.RequestMapping;
import org.init.mvc.bind.annotation.ResponseBody;
import org.init.test.service.TestService;

public class TestController {
    TestService testService;

    public void setTestService(TestService testService) {
        this.testService = testService;
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "666";
    }
}
