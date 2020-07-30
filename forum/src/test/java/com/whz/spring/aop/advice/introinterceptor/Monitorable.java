package com.whz.spring.aop.advice.introinterceptor;

/**
 * 该接口用来控制是否开启监视器
 *
 * Created by wb-whz291815 on 2017/7/27.
 */
public interface Monitorable {

    void setMonitorActive(boolean active);

}
