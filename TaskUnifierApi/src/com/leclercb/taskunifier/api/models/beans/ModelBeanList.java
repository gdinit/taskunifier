package com.leclercb.taskunifier.api.models.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.ModelList;
import com.leclercb.taskunifier.api.models.ModelType;
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
	public <M extends Model> ModelList<M> toModelList(
			ModelList<M> list,
			ModelType type) {
		for (ModelId model : this.models) {
			M m = (M) ModelFactoryUtils.getModel(type, model);
			if (m == null)
				m = (M) ModelFactoryUtils.getFactory(type).createShell(model);
			
			list.add(m);
		}
		
		return list;
	}
	
}
