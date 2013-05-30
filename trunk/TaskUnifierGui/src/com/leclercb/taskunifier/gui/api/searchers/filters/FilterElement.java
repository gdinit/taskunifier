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
package com.leclercb.taskunifier.gui.api.searchers.filters;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import com.leclercb.commons.api.event.propertychange.PropertyChangeSupport;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.logger.ApiLogger;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.Condition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.converter.ConditionValueConverter;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.translations.TranslationsUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("element")
public abstract class FilterElement<M extends Model, F extends Filter<M, F, ? extends FilterElement<M, F>>> implements PropertyChangeSupported, PropertyChangeListener {
	
	public static final String PROP_PROPERTY = "property";
	public static final String PROP_CONDITION = "condition";
	public static final String PROP_VALUE = "value";
	public static final String PROP_COMPARE_MODEL = "compareModel";
	
	@XStreamOmitField
	private transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	
	@XStreamAlias("parent")
	private F parent;
	
	@XStreamAlias("property")
	private PropertyAccessor<M> property;
	
	@XStreamAlias("condition")
	private Condition<?, ?> condition;
	
	@XStreamAlias("value")
	@XStreamConverter(ConditionValueConverter.class)
	private Object value;
	
	@XStreamAlias("comparemodel")
	private boolean compareModel;
	
	public FilterElement() {
		
	}
	
	public FilterElement(
			PropertyAccessor<M> property,
			Condition<?, ?> condition,
			Object value,
			boolean compareModel) {
		this.checkAndSet(property, condition, value, compareModel);
	}
	
	public void checkAndSet(
			PropertyAccessor<M> property,
			Condition<?, ?> condition,
			Object value,
			boolean compareModel) {
		CheckUtils.isNotNull(property);
		CheckUtils.isNotNull(condition);
		
		this.check(property, condition, value);
		
		List<PropertyChangeEvent> events = new ArrayList<PropertyChangeEvent>();
		
		events.add(this.setProperty(property));
		events.add(this.setCondition(condition));
		events.add(this.setValue(value));
		events.add(this.setCompareModel(compareModel));
		
		for (PropertyChangeEvent event : events)
			this.propertyChangeSupport.firePropertyChange(event);
	}
	
	public F getParent() {
		return this.parent;
	}
	
	protected void setParent(F parent) {
		this.parent = parent;
	}
	
	public PropertyAccessor<M> getProperty() {
		return this.property;
	}
	
	private PropertyChangeEvent setProperty(PropertyAccessor<M> property) {
		CheckUtils.isNotNull(property);
		PropertyAccessor<M> oldProperty = this.property;
		this.property = property;
		return new PropertyChangeEvent(
				this,
				PROP_PROPERTY,
				oldProperty,
				property);
	}
	
	public Condition<?, ?> getCondition() {
		return this.condition;
	}
	
	private PropertyChangeEvent setCondition(Condition<?, ?> condition) {
		CheckUtils.isNotNull(condition);
		Condition<?, ?> oldCondition = this.condition;
		this.condition = condition;
		return new PropertyChangeEvent(
				this,
				PROP_CONDITION,
				oldCondition,
				condition);
	}
	
	public Object getValue() {
		return this.value;
	}
	
	private PropertyChangeEvent setValue(Object value) {
		if (value != null && value instanceof Model) {
			Model model = (Model) value;
			if (model.getModelStatus().equals(ModelStatus.TO_DELETE)
					|| model.getModelStatus().equals(ModelStatus.DELETED)) {
				ApiLogger.getLogger().severe(
						"You cannot assign a deleted model");
				value = null;
			}
		}
		
		if (this.value != null && this.value instanceof Model) {
			Model model = (Model) this.value;
			model.removePropertyChangeListener(this);
		}
		
		Object oldValue = this.value;
		this.value = value;
		
		if (this.value != null && this.value instanceof Model) {
			Model model = (Model) this.value;
			model.addPropertyChangeListener(this);
		}
		
		return new PropertyChangeEvent(this, PROP_VALUE, oldValue, value);
	}
	
	public boolean isCompareModel() {
		return this.compareModel;
	}
	
	private PropertyChangeEvent setCompareModel(boolean compareModel) {
		boolean oldCompareModel = this.compareModel;
		this.compareModel = compareModel;
		return new PropertyChangeEvent(
				this,
				PROP_COMPARE_MODEL,
				oldCompareModel,
				compareModel);
	}
	
	private void check(
			PropertyAccessor<M> property,
			Condition<?, ?> condition,
			Object value) {
		if (value != null && !condition.getValueType().isInstance(value))
			throw new IllegalArgumentException("Value is not an instance of "
					+ condition.getValueType());
		
		if (!condition.getModelValueType().isAssignableFrom(
				property.getType().getType()))
			throw new IllegalArgumentException(
					"The property is incompatible with this condition");
	}
	
	public boolean include(M model, M comparedModel) {
		Object taskValue = this.property.getProperty(model);
		
		Object value = this.value;
		
		if (this.compareModel && comparedModel != null) {
			value = this.getComparedModelValue(comparedModel);
		}
		
		return this.condition.include(this.property, value, taskValue);
	}
	
	public abstract Object getComparedModelValue(M comparedModel);
	
	@Override
	public String toString() {
		String value = (this.getValue() == null ? "" : this.getValue().toString());
		
		if (this.isCompareModel())
			value = Translations.getString("filter.updated_task_value");
		
		return this.getProperty()
				+ " "
				+ TranslationsUtils.translateFilterCondition(this.getCondition())
				+ " \""
				+ value
				+ "\"";
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	@Override
	public void addPropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(
				propertyName,
				listener);
	}
	
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	@Override
	public void removePropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(
				propertyName,
				listener);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() instanceof Model
				&& event.getPropertyName().equals(BasicModel.PROP_MODEL_STATUS)) {
			Model model = (Model) event.getSource();
			
			if (model.getModelStatus().equals(ModelStatus.TO_DELETE)
					|| model.getModelStatus().equals(ModelStatus.DELETED))
				this.setValue(null);
		}
	}
	
}
