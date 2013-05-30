/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *   - Neither the name of TaskUnifier or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.leclercb.taskunifier.api.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.beans.ModelBean;

public abstract class AbstractModel extends AbstractBasicModel implements Model {
	
	private Map<String, String> modelReferenceIds;
	private int order;
	
	public AbstractModel(ModelId modelId, String title) {
		super(modelId, title);
		
		this.setOrder(0);
		this.setModelReferenceIds(new HashMap<String, String>());
	}
	
	/**
	 * Returns the factory of the model.
	 * 
	 * @return the factory of the model
	 */
	public abstract AbstractModelFactory<?, ?, ?, ?> getFactory();
	
	/**
	 * Loads the data from the bean into the model.
	 * 
	 * @param bean
	 *            the bean
	 */
	@Override
	public void loadBean(ModelBean bean, boolean loadReferenceIds) {
		CheckUtils.isNotNull(bean);
		
		this.setTitle(bean.getTitle());
		this.setOrder(bean.getOrder());
		this.addProperties(bean.getProperties());
		
		if (loadReferenceIds) {
			this.modelReferenceIds.clear();
			
			if (bean.getModelReferenceIds() != null) {
				Map<String, String> beanReferenceIds = bean.getModelReferenceIds();
				for (String key : beanReferenceIds.keySet()) {
					this.addModelReferenceId(key, beanReferenceIds.get(key));
				}
			}
		}
		
		this.setModelStatus(bean.getModelStatus());
		this.setModelCreationDate(bean.getModelCreationDate());
		this.setModelUpdateDate(bean.getModelUpdateDate());
	}
	
	/**
	 * Convert the data from the model into a bean.
	 * 
	 * @return the bean
	 */
	@Override
	public ModelBean toBean() {
		ModelBean bean = this.getFactory().createBean();
		
		bean.setTitle(this.getTitle());
		bean.setOrder(this.getOrder());
		bean.setProperties(this.getProperties().clone());
		
		bean.setModelId(this.getModelId());
		bean.setModelReferenceIds(this.getModelReferenceIds());
		bean.setModelStatus(this.getModelStatus());
		bean.setModelCreationDate(this.getModelCreationDate());
		bean.setModelUpdateDate(this.getModelUpdateDate());
		
		return bean;
	}
	
	/**
	 * Sets the id of the model. Property name : {@link Model#PROP_MODEL_ID}
	 * 
	 * @param modelId
	 *            the id of the model
	 * @exception IllegalArgumentException
	 *                if another model with the same id is found in the factory
	 */
	@Override
	protected final void setModelId(ModelId modelId) {
		Model newModel = this.getFactory().get(modelId);
		
		if (newModel != null && newModel != this)
			throw new IllegalArgumentException(
					"The model id you try to assign to this model already exists in the factory");
		
		super.setModelId(modelId);
	}
	
	@Override
	public Map<String, String> getModelReferenceIds() {
		return Collections.unmodifiableMap(this.modelReferenceIds);
	}
	
	private void setModelReferenceIds(Map<String, String> modelReferenceIds) {
		CheckUtils.isNotNull(modelReferenceIds);
		this.modelReferenceIds = modelReferenceIds;
	}
	
	@Override
	public String getModelReferenceId(String key) {
		CheckUtils.isNotNull(key);
		return this.modelReferenceIds.get(key);
	}
	
	@Override
	public void addModelReferenceId(String key, String referenceId) {
		CheckUtils.isNotNull(key);
		CheckUtils.isNotNull(referenceId);
		this.modelReferenceIds.put(key, referenceId);
	}
	
	@Override
	public void removeModelReferenceId(String key) {
		CheckUtils.isNotNull(key);
		this.modelReferenceIds.remove(key);
	}
	
	/**
	 * Returns the order of the model.
	 * 
	 * @return the order of the model
	 */
	@Override
	public int getOrder() {
		return this.order;
	}
	
	/**
	 * Sets the order of the model. Property name : {@link Model#PROP_ORDER}
	 * 
	 * @param order
	 *            the order of the model
	 */
	@Override
	public void setOrder(int order) {
		if (!this.checkBeforeSet(this.getOrder(), order))
			return;
		
		int oldOrder = this.order;
		this.order = order;
		this.updateProperty(PROP_ORDER, oldOrder, order);
	}
	
}
