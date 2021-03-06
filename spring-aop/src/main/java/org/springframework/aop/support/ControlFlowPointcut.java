/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.support;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.core.ControlFlow;
import org.springframework.core.ControlFlowFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Pointcut and method matcher for use in simple <b>cflow</b>-style pointcut.
 * Note that evaluating such pointcuts is 10-15 times slower than evaluating
 * normal pointcuts, but they are useful in some cases.
 *
 * @author Rod Johnson
 * @author Rob Harrop
 * @see org.springframework.core.ControlFlow
 */
@SuppressWarnings("serial")
public class ControlFlowPointcut implements Pointcut, ClassFilter, MethodMatcher, Serializable {

	private Class clazz;
	private String methodName;
	// 用于记录拦截了多少次，该属性主要用于优化
	private int evaluations;

	// 构造器
	public ControlFlowPointcut(Class clazz) {
		this(clazz, null);
	}
	public ControlFlowPointcut(Class clazz, String methodName) {
		Assert.notNull(clazz, "Class must not be null");
		this.clazz = clazz;
		this.methodName = methodName;
	}

	public boolean matches(Class clazz) {
		return true;
	}
	public boolean matches(Method method, Class targetClass) {
		return true;
	}
	public boolean isRuntime() {
		return true;
	}
	public boolean matches(Method method, Class targetClass, Object[] args) {
		++this.evaluations;
		ControlFlow cflow = ControlFlowFactory.createControlFlow();
		return (this.methodName != null) ? cflow.under(this.clazz, this.methodName) : cflow.under(this.clazz);
	}
	public int getEvaluations() {
		return evaluations;
	}
	public ClassFilter getClassFilter() {
		return this;
	}
	public MethodMatcher getMethodMatcher() {
		return this;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ControlFlowPointcut)) {
			return false;
		}
		ControlFlowPointcut that = (ControlFlowPointcut) other;
		return (this.clazz.equals(that.clazz)) && ObjectUtils.nullSafeEquals(that.methodName, this.methodName);
	}
	@Override
	public int hashCode() {
		int code = 17;
		code = 37 * code + this.clazz.hashCode();
		if (this.methodName != null) {
			code = 37 * code + this.methodName.hashCode();
		}
		return code;
	}

}
