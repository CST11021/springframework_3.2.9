package com.whz.spring.aop.advice.introinterceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

/**
 * 这里实现增强的织入
 *
 * Created by wb-whz291815 on 2017/7/27.
 */
public class ControllablePerformanceMonitor extends DelegatingIntroductionInterceptor implements Monitorable {

    private ThreadLocal<Boolean> MonitorStatusMap = new ThreadLocal<Boolean>();

    public void setMonitorActive(boolean active) {
        MonitorStatusMap.set(active);
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        Object obj;
        // 如果开启监视器，则织入增强，否则直接调用目标方法
        if (MonitorStatusMap.get() != null && MonitorStatusMap.get()) {
            PerformanceMonitor.begin(mi.getClass().getName() + "." + mi.getMethod().getName());
            obj = super.invoke(mi);
            PerformanceMonitor.end();
        } else {
            obj = super.invoke(mi);
        }
        return obj;
    }

}
