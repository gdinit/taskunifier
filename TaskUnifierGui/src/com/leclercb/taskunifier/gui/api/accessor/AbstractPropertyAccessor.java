/*
 * TaskUnifier
 * Copyright (c) 2011, Benjamin Leclerc
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
package com.leclercb.taskunifier.gui.api.accessor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.leclercb.commons.api.event.propertychange.PropertyChangeSupport;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.table.TUColumn;

public abstract class AbstractPropertyAccessor<M extends Model> implements PropertyAccessor<M>, TUColumn<M>, PropertyChangeListener, PropertyChangeSupported {
	
	public static final String PROP_USED = "used";
	
	private PropertyChangeSupport propertyChangeSupport;
	
	private String fieldSettingsPropertyName;
	private Class<?> type;
	private String propertyName;
	private String label;
	private boolean editable;
	private boolean usable;
	private boolean used;
	
	public AbstractPropertyAccessor(
			String fieldSettingsPropertyName,
			Class<?> type,
			String propertyName,
			String label,
			boolean editable,
			boolean usable) {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		
		this.setFieldSettingsPropertyName(fieldSettingsPropertyName);
		this.setType(type);
		this.setPropertyName(propertyName);
		this.setLabel(label);
		this.setEditable(editable);
		this.setUsable(usable);
		
		this.setUsed(Main.getSettings().getBooleanProperty(
				fieldSettingsPropertyName + ".used",
				true));
		
		Main.getSettings().addPropertyChangeListener(
				fieldSettingsPropertyName + ".used",
				new WeakPropertyChangeListener(Main.getSettings(), this));
	}
	
	public String getFieldSettingsPropertyName() {
		return this.fieldSettingsPropertyName;
	}
	
	public void setFieldSettingsPropertyName(String fieldSettingsPropertyName) {
		this.fieldSettingsPropertyName = fieldSettingsPropertyName;
	}
	
	public String getPropertyName() {
		return this.propertyName;
	}
	
	private void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	
	@Override
	public Class<?> getType() {
		return this.type;
	}
	
	private void setType(Class<?> type) {
		this.type = type;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}
	
	private void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public boolean isEditable() {
		return this.editable;
	}
	
	private void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public boolean isUsable() {
		return this.usable;
	}
	
	public void setUsable(boolean usable) {
		this.usable = usable;
	}
	
	public boolean isUsed() {
		return this.used;
	}
	
	public void setUsed(boolean used) {
		if (used == this.isUsed())
			return;
		
		boolean oldUsed = this.isUsed();
		this.used = used;
		
		Main.getSettings().setBooleanProperty(
				this.fieldSettingsPropertyName + ".used",
				used);
		
		this.propertyChangeSupport.firePropertyChange(PROP_USED, oldUsed, used);
	}
	
	@Override
	public String toString() {
		return this.label;
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
	public void propertyChange(PropertyChangeEvent evt) {
		this.setUsed(Boolean.parseBoolean(evt.getNewValue().toString()));
	}
	
}
