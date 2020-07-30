/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.web.servlet.view.xml;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

import org.springframework.oxm.Marshaller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Spring-MVC {@link View} that allows for response context to be rendered as the result
 * of marshalling by a {@link Marshaller}.
 *
 * <p>The Object to be marshalled is supplied as a parameter in the model and then
 * {@linkplain #locateToBeMarshalled(Map) detected} during response rendering. Users can
 * either specify a specific entry in the model via the {@link #setModelKey(String) sourceKey}
 * property or have Spring locate the Source object.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 */
// 表示一个XML类型视图，我们可以在Spring配置该视图来进行xml的渲染
public class MarshallingView extends AbstractView {

	// Default content type. Overridable as bean property.
	public static final String DEFAULT_CONTENT_TYPE = "application/xml";
	// MarshallingView使用Marshaller将模型数据转换为XML
	private Marshaller marshaller;
	// 默认情况下，MarshallingView会将所有模型数据的所有属性输出为XML，由于模型属性会包含很多隐式数据，所以我们通过modelKey指定模型中的哪些属性输出为MXL
	private String modelKey;


	public MarshallingView() {
		setContentType(DEFAULT_CONTENT_TYPE);
		setExposePathVariables(false);
	}
	public MarshallingView(Marshaller marshaller) {
		this();
		Assert.notNull(marshaller, "Marshaller must not be null");
		this.marshaller = marshaller;
	}



	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	@Override
	protected void initApplicationContext() {
		Assert.notNull(this.marshaller, "Property 'marshaller' is required");
	}
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Object toBeMarshalled = locateToBeMarshalled(model);
		if (toBeMarshalled == null) {
			throw new ServletException("Unable to locate object to be marshalled in model: " + model);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		this.marshaller.marshal(toBeMarshalled, new StreamResult(baos));

		setResponseContentType(request, response);
		response.setContentLength(baos.size());
		baos.writeTo(response.getOutputStream());
	}
	/**
	 * Locate the object to be marshalled.
	 * <p>The default implementation first attempts to look under the configured
	 * {@linkplain #setModelKey(String) model key}, if any, before attempting to
	 * locate an object of {@linkplain Marshaller#supports(Class) supported type}.
	 * @param model the model Map
	 * @return the Object to be marshalled (or {@code null} if none found)
	 * @throws ServletException if the model object specified by the
	 * {@linkplain #setModelKey(String) model key} is not supported by the marshaller
	 * @see #setModelKey(String)
	 */
	protected Object locateToBeMarshalled(Map<String, Object> model) throws ServletException {
		if (this.modelKey != null) {
			Object obj = model.get(this.modelKey);
			if (obj == null) {
				throw new ServletException("Model contains no object with key [" + this.modelKey + "]");
			}
			if (!this.marshaller.supports(obj.getClass())) {
				throw new ServletException("Model object [" + obj + "] retrieved via key [" +
						this.modelKey + "] is not supported by the Marshaller");
			}
			return obj;
		}
		for (Object obj : model.values()) {
			if (obj != null && (model.size() == 1 || !(obj instanceof BindingResult)) &&
					this.marshaller.supports(obj.getClass())) {
				return obj;
			}
		}
		return null;
	}

}
