package com.whz.spring.jmx.exportmbean;

public interface IJmxTestBean {
    public int add(int x, int y);

    public long operation();

    public int getAge();

    public void setAge(int age);

    public void setName(String name);

    public String getName();
} 