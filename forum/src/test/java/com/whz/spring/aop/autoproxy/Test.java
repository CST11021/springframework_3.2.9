package com.whz.spring.aop.autoproxy;


import com.whz.spring.aop.advice.Waiter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    @org.junit.Test
    public void TestAutoProxy() {
        String configPath = "com/whz/spring/aop/autoproxy/spring-autoproxy.xml";
        ApplicationContext ctx = new ClassPathXmlApplicationContext(configPath);
        Waiter waiter = (Waiter) ctx.getBean("waiterTarget");
        waiter.greetTo("Peter");
        waiter.serveTo("Peter");
    }

}
