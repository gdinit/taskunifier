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
package com.leclercb.taskunifier.api.models.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.ModelList;
import com.leclercb.taskunifier.api.models.utils.ModelFactoryUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("modellist")
public class ModelBeanList implements Cloneable, Serializable, Iterable<ModelId> {
	
	@XStreamImplicit
	private List<ModelId> models;
	
	public ModelBeanList() {
		this.models = new ArrayList<ModelId>();
	}
	
	@Override
	protected ModelBeanList clone() {
		ModelBeanList list = new ModelBeanList();
		list.models.addAll(this.models);
		return list;
	}
	
	@Override
	public Iterator<ModelId> iterator() {
		return this.models.iterator();
	}
	
	public List<ModelId> getList() {
		return new ArrayList<ModelId>(this.models);
	}
	
	public void add(ModelId model) {
		if (model == null)
			return;
		
		if (this.models.contains(model))
			return;
		
		this.models.add(model);
	}
	
	public void addAll(Collection<ModelId> models) {
		if (models == null)
			return;
		
		for (ModelId model : models)
			this.add(model);
	}
	
	public void addAll(ModelBeanList modelBeanList) {
		this.addAll(modelBeanList.getList());
	}
	
	public void remove(ModelId model) {
		this.models.remove(model);
	}
	
	public void clear() {
		for (ModelId model : this.getList())
			this.remove(model);
	}
	
	public int size() {
		return this.models.size();
	}
	
	public boolean contains(ModelId modelId) {
		return this.models.contains(modelId);
	}
	
	public int getIndexOf(ModelId model) {
		return this.models.indexOf(model);
	}
	
	public ModelId get(int index) {
		return this.models.get(index);
	}
	
	@SuppressWarnings("unchecked")
	public <M extends Model> ModelList<M> toModelList(ModelList<M> list) {
		for (ModelId model : this.models) {
			M m = (M) ModelFactoryUtils.getModel(list.getModelType(), model);
			if (m == null)
				m = (M) ModelFactoryUtils.getFactory(list.getModelType()).createShell(
						model);
			
			list.add(m);
		}
		
		return list;
	}
	
}
