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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.ListChangeSupport;
import com.leclercb.commons.api.event.listchange.ListChangeSupported;
import com.leclercb.commons.api.logger.ApiLogger;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.beans.ModelBeanList;

public class ModelList<M extends Model> implements Cloneable, Serializable, Iterable<M>, ListChangeSupported, PropertyChangeListener {
	
	private ListChangeSupport listChangeSupport;
	
	private ModelType type;
	private List<M> models;
	
	public ModelList(ModelType type) {
		CheckUtils.isNotNull(type);
		this.type = type;
		
		this.listChangeSupport = new ListChangeSupport(this);
		
		this.models = new ArrayList<M>();
	}
	
	public ModelType getModelType() {
		return this.type;
	}
	
	@Override
	protected ModelList<M> clone() {
		ModelList<M> list = new ModelList<M>(this.type);
		list.models.addAll(this.models);
		return list;
	}
	
	@Override
	public Iterator<M> iterator() {
		return this.models.iterator();
	}
	
	public List<M> getList() {
		return new ArrayList<M>(this.models);
	}
	
	public void add(M model) {
		CheckUtils.isNotNull(model);
		
		if (model.getModelStatus().equals(ModelStatus.TO_DELETE)
				|| model.getModelStatus().equals(ModelStatus.DELETED)) {
			ApiLogger.getLogger().severe("You cannot assign a deleted model");
			return;
		}
		
		if (this.models.contains(model))
			return;
		
		this.models.add(model);
		model.addPropertyChangeListener(this);
		int index = this.models.indexOf(model);
		this.listChangeSupport.fireListChange(
				ListChangeEvent.VALUE_ADDED,
				index,
				model);
	}
	
	public void addAll(Collection<M> models) {
		if (models == null)
			return;
		
		for (M model : models)
			this.add(model);
	}
	
	public void addAll(ModelList<M> modelList) {
		this.addAll(modelList.getList());
	}
	
	public void remove(M model) {
		CheckUtils.isNotNull(model);
		
		int index = this.models.indexOf(model);
		if (this.models.remove(model)) {
			model.removePropertyChangeListener(this);
			this.listChangeSupport.fireListChange(
					ListChangeEvent.VALUE_REMOVED,
					index,
					model);
		}
	}
	
	public void clear() {
		for (M model : this.getList())
			this.remove(model);
	}
	
	public int size() {
		return this.models.size();
	}
	
	public boolean contains(Model model) {
		return this.models.contains(model);
	}
	
	public int getIndexOf(M model) {
		return this.models.indexOf(model);
	}
	
	public M get(int index) {
		return this.models.get(index);
	}
	
	@Override
	public String toString() {
		return StringUtils.join(this.models, ", ");
	}
	
	public ModelBeanList toModelBeanList() {
		ModelBeanList list = new ModelBeanList();
		
		for (M model : this.models)
			list.add(model.getModelId());
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(BasicModel.PROP_MODEL_STATUS)) {
			M model = (M) event.getSource();
			
			if (model.getModelStatus().equals(ModelStatus.TO_DELETE)
					|| model.getModelStatus().equals(ModelStatus.DELETED))
				this.remove(model);
		}
	}
	
	@Override
	public void addListChangeListener(ListChangeListener listener) {
		this.listChangeSupport.addListChangeListener(listener);
	}
	
	@Override
	public void removeListChangeListener(ListChangeListener listener) {
		this.listChangeSupport.removeListChangeListener(listener);
	}
	
}
