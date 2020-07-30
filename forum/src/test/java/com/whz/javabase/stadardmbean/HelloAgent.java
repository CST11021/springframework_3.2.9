package com.whz.javabase.stadardmbean;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class HelloAgent {

    public static void main(String[] args) throws IOException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName helloName = null;
        try {
            helloName = new ObjectName("jmxBean:name=hello");
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

        //create mbean and register mbean
        Hello hello = null;
        try {
            hello = new Hello();
            server.registerMBean(hello, helloName);
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        }

        for (;;) {
            System.in.read();
            System.out.println(hello);
        }
    }

}