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
package com.leclercb.taskunifier.gui.swing.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import com.leclercb.commons.api.event.propertychange.PropertyChangeSupport;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorList;
import com.leclercb.taskunifier.gui.main.Main;

public class TUTableProperties<T> implements PropertyChangeListener {
	
	private PropertyAccessorList<T> list;
	private String propertyName;
	private boolean readOnly;
	private Map<PropertyAccessor<T>, TableColumnProperties<T>> columns;
	
	public TUTableProperties(
			PropertyAccessorList<T> list,
			String propertyName,
			boolean readOnly) {
		CheckUtils.isNotNull(list);
		
		this.list = list;
		this.columns = new HashMap<PropertyAccessor<T>, TableColumnProperties<T>>();
		
		for (PropertyAccessor<T> column : this.list.getAccessors()) {
			this.columns.put(column, new TableColumnProperties<T>(this, column));
		}
		
		this.setPropertyName(propertyName);
		this.setReadOnly(readOnly);
	}
	
	public PropertyAccessorList<T> getColumns() {
		return this.list;
	}
	
	public TableColumnProperties<T> get(PropertyAccessor<T> column) {
		return this.columns.get(column);
	}
	
	public String getPropertyName() {
		return this.propertyName;
	}
	
	public void setPropertyName(String propertyName) {
		CheckUtils.isNotNull(propertyName);
		
		Main.getSettings().removePropertyChangeListener(this);
		
		this.propertyName = propertyName;
		
		for (PropertyAccessor<T> column : this.list.getAccessors()) {
			TableColumnProperties<T> properties = this.columns.get(column);
			
			properties.setOrder(Main.getSettings().getIntegerProperty(
					properties.getOrderPropertyName(),
					0));
			
			properties.setWidth(Main.getSettings().getIntegerProperty(
					properties.getWidthPropertyName(),
					100));
			
			properties.setVisible(Main.getSettings().getBooleanProperty(
					properties.getVisiblePropertyName(),
					true));
		}
		
		Main.getSettings().addPropertyChangeListener(
				new WeakPropertyChangeListener(Main.getSettings(), this));
	}
	
	public boolean isReadOnly() {
		return this.readOnly;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().startsWith(this.propertyName)) {
			if (evt.getNewValue() == null)
				return;
			
			for (PropertyAccessor<T> column : this.list.getAccessors()) {
				TableColumnProperties<T> properties = this.columns.get(column);
				
				if (evt.getPropertyName().equals(
						properties.getOrderPropertyName()))
					properties.setOrder(Integer.parseInt(evt.getNewValue().toString()));
				
				if (evt.getPropertyName().equals(
						properties.getWidthPropertyName()))
					properties.setWidth(Integer.parseInt(evt.getNewValue().toString()));
				
				if (evt.getPropertyName().equals(
						properties.getVisiblePropertyName()))
					properties.setVisible(Boolean.parseBoolean(evt.getNewValue().toString()));
			}
		}
	}
	
	public static class TableColumnProperties<T> implements PropertyChangeSupported {
		
		public static final String PROP_ORDER = "order";
		public static final String PROP_WIDTH = "width";
		public static final String PROP_VISIBLE = "visible";
		
		private PropertyChangeSupport propertyChangeSupport;
		
		private TUTableProperties<T> tableProperties;
		private PropertyAccessor<T> column;
		
		private int order;
		private int width;
		private boolean visible;
		
		private TableColumnProperties(
				TUTableProperties<T> tableProperties,
				PropertyAccessor<T> column) {
			this.propertyChangeSupport = new PropertyChangeSupport(this);
			
			CheckUtils.isNotNull(column);
			CheckUtils.isNotNull(tableProperties);
			
			this.tableProperties = tableProperties;
			this.column = column;
		}
		
		public TUTableProperties<T> getTableProperties() {
			return this.tableProperties;
		}
		
		public PropertyAccessor<T> getColumn() {
			return this.column;
		}
		
		public String getOrderPropertyName() {
			return this.tableProperties.getPropertyName()
					+ ".column."
					+ this.column.getId().toLowerCase()
					+ ".order";
		}
		
		public int getOrder() {
			return this.order;
		}
		
		public void setOrder(int order) {
			if (order == this.getOrder())
				return;
			
			int oldOrder = this.getOrder();
			this.order = order;
			
			if (!this.tableProperties.isReadOnly()) {
				Main.getSettings().setIntegerProperty(
						this.getOrderPropertyName(),
						order);
			}
			
			this.propertyChangeSupport.firePropertyChange(
					PROP_ORDER,
					oldOrder,
					order);
		}
		
		public String getWidthPropertyName() {
			return this.tableProperties.getPropertyName()
					+ ".column."
					+ this.column.getId().toLowerCase()
					+ ".width";
		}
		
		public int getWidth() {
			return this.width;
		}
		
		public void setWidth(int width) {
			if (width == this.getWidth())
				return;
			
			int oldWidth = this.getWidth();
			this.width = width;
			
			if (!this.tableProperties.isReadOnly()) {
				Main.getSettings().setIntegerProperty(
						this.getWidthPropertyName(),
						width);
			}
			
			this.propertyChangeSupport.firePropertyChange(
					PROP_WIDTH,
					oldWidth,
					width);
		}
		
		public String getVisiblePropertyName() {
			return this.tableProperties.getPropertyName()
					+ ".column."
					+ this.column.getId().toLowerCase()
					+ ".visible";
		}
		
		public boolean isVisible() {
			return this.visible;
		}
		
		public void setVisible(boolean visible) {
			if (visible == this.isVisible())
				return;
			
			boolean oldVisible = this.isVisible();
			this.visible = visible;
			
			if (!this.tableProperties.isReadOnly()) {
				Main.getSettings().setBooleanProperty(
						this.getVisiblePropertyName(),
						visible);
			}
			
			this.propertyChangeSupport.firePropertyChange(
					PROP_VISIBLE,
					oldVisible,
					visible);
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
		
	}
	
}
