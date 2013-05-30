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
package com.leclercb.taskunifier.gui.utils;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.event.ListEventListener;

import com.leclercb.commons.api.event.propertychange.PropertyChangeSupport;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.glazedlists.ListEventSupported;
import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.properties.events.WeakSavePropertiesListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.TaskPostponeList.PostponeItem;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public final class TaskPostponeList implements ListEventSupported<PostponeItem>, SavePropertiesListener {
	
	private static TaskPostponeList INSTANCE;
	
	public static TaskPostponeList getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskPostponeList();
		
		return INSTANCE;
	}
	
	@XStreamAlias("postponelist")
	private EventList<PostponeItem> items;
	
	public TaskPostponeList() {
		ObservableElementList.Connector<PostponeItem> connector = GlazedLists.beanConnector(PostponeItem.class);
		
		this.items = new ObservableElementList<PostponeItem>(
				new BasicEventList<PostponeItem>(),
				connector);
		
		this.initialize();
		
		Main.getUserSettings().addSavePropertiesListener(
				new WeakSavePropertiesListener(Main.getUserSettings(), this));
	}
	
	private void initialize() {
		String input = Main.getSettings().getStringProperty("taskpostponelist");
		
		if (input == null)
			return;
		
		try {
			String[] items = input.split(";");
			for (String item : items) {
				if (item.length() == 0)
					continue;
				
				String[] values = item.split("\\|");
				this.add(new PostponeItem(
						Integer.parseInt(values[0]),
						Integer.parseInt(values[1])));
			}
		} catch (Throwable t) {
			GuiLogger.getLogger().log(
					Level.WARNING,
					"Cannot initialize task postpone list",
					t);
		}
	}
	
	public EventList<PostponeItem> getEventList() {
		return GlazedLists.readOnlyList(this.items);
	}
	
	public List<PostponeItem> getPostponeItems() {
		return new ArrayList<PostponeItem>(this.items);
	}
	
	public void add(PostponeItem item) {
		CheckUtils.isNotNull(item);
		
		if (this.items.contains(item))
			return;
		
		this.items.add(item);
	}
	
	public void remove(PostponeItem item) {
		CheckUtils.isNotNull(item);
		this.items.remove(item);
	}
	
	@Override
	public void addListEventListener(ListEventListener<PostponeItem> listener) {
		this.items.addListEventListener(listener);
	}
	
	@Override
	public void removeListEventListener(ListEventListener<PostponeItem> listener) {
		this.items.removeListEventListener(listener);
	}
	
	@Override
	public void saveProperties() {
		StringBuffer value = new StringBuffer();
		for (PostponeItem item : this.items) {
			value.append(item.getField() + "|" + item.getAmount() + ";");
		}
		
		Main.getSettings().setStringProperty(
				"taskpostponelist",
				value.toString());
	}
	
	@XStreamAlias("postponeitem")
	public static class PostponeItem implements TimeValue, PropertyChangeSupported {
		
		public static final String PROP_FIELD = "field";
		public static final String PROP_AMOUNT = "amount";
		
		private transient PropertyChangeSupport propertyChangeSupport;
		
		@XStreamAlias("field")
		private int field;
		
		@XStreamAlias("amount")
		private int amount;
		
		public PostponeItem() {
			this(Calendar.DAY_OF_MONTH, 0);
		}
		
		public PostponeItem(int field, int amount) {
			this.propertyChangeSupport = new PropertyChangeSupport(this);
			
			this.setField(field);
			this.setAmount(amount);
		}
		
		@Override
		public int getField() {
			return this.field;
		}
		
		@Override
		public void setField(int field) {
			if (field != Calendar.MINUTE
					&& field != Calendar.HOUR_OF_DAY
					&& field != Calendar.DAY_OF_MONTH
					&& field != Calendar.WEEK_OF_YEAR
					&& field != Calendar.MONTH
					&& field != Calendar.YEAR)
				throw new IllegalArgumentException();
			
			int oldField = this.field;
			this.field = field;
			this.propertyChangeSupport.firePropertyChange(
					PROP_FIELD,
					oldField,
					field);
		}
		
		@Override
		public int getAmount() {
			return this.amount;
		}
		
		@Override
		public void setAmount(int amount) {
			CheckUtils.isPositive(amount);
			int oldAmount = this.amount;
			this.amount = amount;
			this.propertyChangeSupport.firePropertyChange(
					PROP_AMOUNT,
					oldAmount,
					amount);
		}
		
		@Override
		public String getLabel() {
			if (this.amount == 0)
				return Translations.getString("date.today");
			
			if (this.amount == 1) {
				if (this.field == Calendar.MINUTE)
					return Translations.getString("date.1_minute");
				
				if (this.field == Calendar.HOUR_OF_DAY)
					return Translations.getString("date.1_hour");
				
				if (this.field == Calendar.DAY_OF_MONTH)
					return Translations.getString("date.1_day");
				
				if (this.field == Calendar.WEEK_OF_YEAR)
					return Translations.getString("date.1_week");
				
				if (this.field == Calendar.MONTH)
					return Translations.getString("date.1_month");
				
				if (this.field == Calendar.YEAR)
					return Translations.getString("date.1_year");
			} else {
				if (this.field == Calendar.MINUTE)
					return Translations.getString("date.x_minutes", this.amount);
				
				if (this.field == Calendar.HOUR_OF_DAY)
					return Translations.getString("date.x_hours", this.amount);
				
				if (this.field == Calendar.DAY_OF_MONTH)
					return Translations.getString("date.x_days", this.amount);
				
				if (this.field == Calendar.WEEK_OF_YEAR)
					return Translations.getString("date.x_weeks", this.amount);
				
				if (this.field == Calendar.MONTH)
					return Translations.getString("date.x_months", this.amount);
				
				if (this.field == Calendar.YEAR)
					return Translations.getString("date.x_years", this.amount);
			}
			
			return null;
		}
		
		@Override
		public String toString() {
			return this.getLabel();
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
