/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.beans.factory.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

/**
 * A {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor}
 * implementation that allows for convenient registration of custom autowire
 * qualifier types.
 *
 * <pre class="code">
 * &lt;bean id="customAutowireConfigurer" class="org.springframework.beans.factory.annotation.CustomAutowireConfigurer"&gt;
 *   &lt;property name="customQualifierTypes"&gt;
 *     &lt;set&gt;
 *       &lt;value&gt;mypackage.MyQualifier&lt;/value&gt;
 *     &lt;/set&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.annotation.Qualifier
 */
public class CustomAutowireConfigurer implements BeanFactoryPostProcessor, BeanClassLoaderAware, Ordered {

	// default: same as non-Ordered
	private int order = Ordered.LOWEST_PRECEDENCE;
	// 该集合保存的自定义类型必须是注解类型
	private Set customQualifierTypes;
	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	// 实现BeanFactoryPostProcessor#postProcessBeanFactory接口：在解析完配置文件后，实例化Bean之前被调用
	@SuppressWarnings("unchecked")
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (this.customQualifierTypes != null) {

			// 1、如果beanFactory不是DefaultListableBeanFactory的对象则抛出异常
			if (!(beanFactory instanceof DefaultListableBeanFactory)) {
				throw new IllegalStateException("CustomAutowireConfigurer needs to operate on a DefaultListableBeanFactory");
			}

			// 2、判断BeanFactory#AutowireCandidateResolver 是否为 QualifierAnnotationAutowireCandidateResolver 的对象
			// 如果不是设置一个QualifierAnnotationAutowireCandidateResolver实例
			DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
			if (!(dlbf.getAutowireCandidateResolver() instanceof QualifierAnnotationAutowireCandidateResolver)) {
				dlbf.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());
			}
			QualifierAnnotationAutowireCandidateResolver resolver =
					(QualifierAnnotationAutowireCandidateResolver) dlbf.getAutowireCandidateResolver();

			for (Object value : this.customQualifierTypes) {
				Class customType = null;
				if (value instanceof Class) {
					customType = (Class) value;
				}
				else if (value instanceof String) {
					String className = (String) value;
					customType = ClassUtils.resolveClassName(className, this.beanClassLoader);
				}
				else {
					throw new IllegalArgumentException(
							"Invalid value [" + value + "] for custom qualifier type: needs to be Class or String.");
				}

				// 判断这个customType是否为一个注解类型，如果不是则抛出异常
				if (!Annotation.class.isAssignableFrom(customType)) {
					throw new IllegalArgumentException(
							"Qualifier type [" + customType.getName() + "] needs to be annotation type");
				}
				resolver.addQualifierType(customType);
			}
		}
	}

	public void setOrder(int order) {
		this.order = order;
	}
	public int getOrder() {
		return this.order;
	}
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}
	public void setCustomQualifierTypes(Set customQualifierTypes) {
		this.customQualifierTypes = customQualifierTypes;
	}
}
