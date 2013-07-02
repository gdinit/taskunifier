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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.event.ListEventListener;

import com.leclercb.commons.api.glazedlists.ListEventSupported;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;
import com.leclercb.taskunifier.gui.api.accessor.TaskPropertyAccessor;
import com.leclercb.taskunifier.gui.main.Main;

public final class TaskCustomColumnList implements ListEventSupported<PropertyAccessor<Task>> {
	
	private static TaskCustomColumnList INSTANCE;
	
	public static TaskCustomColumnList getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskCustomColumnList();
		
		return INSTANCE;
	}
	
	private List<PropertyAccessor<Task>> initialItems;
	private EventList<PropertyAccessor<Task>> items;
	
	private TaskCustomColumnList() {
		this.items = new BasicEventList<PropertyAccessor<Task>>();
		
		this.initialize();
		
		this.initialItems = this.getPropertyAccessors();
	}
	
	private void initialize() {
		Pattern pattern = null;
		
		Set<Object> keys = null;
		
		pattern = Pattern.compile("task\\.custom_field\\.([a-z0-9-]+)\\.([a-z0-9_]+)");
		
		keys = Main.getSettings().keySet();
		for (Object key : keys) {
			String k = (String) key;
			
			Matcher matcher = pattern.matcher(k);
			
			if (!matcher.matches())
				continue;
			
			String id = matcher.group(1);
			PropertyAccessorType type = PropertyAccessorType.valueOf(matcher.group(
					2).toUpperCase());
			
			PropertyAccessor<Task> accessor = new TaskPropertyAccessor(
					id,
					"task.field." + id,
					type,
					"task.custom_field." + id + "." + type.name().toLowerCase(),
					Main.getSettings().getStringProperty(k, "Untitled"),
					true,
					true,
					false);
			
			if (!this.items.contains(accessor))
				this.items.add(accessor);
		}
		
		pattern = Pattern.compile("task\\.custom_field\\.([a-z0-9-]+)\\.([a-z0-9_]+)");
		
		List<Task> tasks = TaskFactory.getInstance().getList();
		for (Task task : tasks) {
			keys = task.getProperties().keySet();
			for (Object key : keys) {
				String k = (String) key;
				
				Matcher matcher = pattern.matcher(k);
				
				if (!matcher.matches())
					continue;
				
				String id = matcher.group(1);
				PropertyAccessorType type = PropertyAccessorType.valueOf(matcher.group(
						2).toUpperCase());
				
				PropertyAccessor<Task> accessor = new TaskPropertyAccessor(
						id,
						"task.field." + id,
						type,
						"task.custom_field."
								+ id
								+ "."
								+ type.name().toLowerCase(),
						Main.getSettings().getStringProperty(
								"task." + k,
								"Untitled"),
						true,
						true,
						false);
				
				if (!this.items.contains(accessor))
					this.items.add(accessor);
			}
		}
	}
	
	public EventList<PropertyAccessor<Task>> getEventList() {
		return GlazedLists.readOnlyList(this.items);
	}
	
	public List<PropertyAccessor<Task>> getInitialPropertyAccessors() {
		return new ArrayList<PropertyAccessor<Task>>(this.initialItems);
	}
	
	public List<PropertyAccessor<Task>> getPropertyAccessors() {
		return new ArrayList<PropertyAccessor<Task>>(this.items);
	}
	
	public void addColumn(PropertyAccessorType type, String label) {
		CheckUtils.isNotNull(type);
		CheckUtils.isNotNull(label);
		
		String uuid = UUID.randomUUID().toString();
		
		Main.getSettings().setStringProperty(
				"task.custom_field." + uuid + "." + type.name().toLowerCase(),
				label);
		
		List<Task> tasks = TaskFactory.getInstance().getList();
		for (Task task : tasks) {
			task.getProperties().setStringProperty(
					"task.custom_field." + uuid + "." + type.name().toLowerCase(),
					null);
		}
		
		PropertyAccessor<Task> accessor = new TaskPropertyAccessor(
				uuid,
				"task.field." + uuid,
				type,
				"task.custom_field." + uuid + "." + type.name().toLowerCase(),
				label,
				true,
				true,
				false);
		
		this.items.add(accessor);
	}
	
	public void renameColumn(PropertyAccessor<Task> accessor, String label) {
		CheckUtils.isNotNull(accessor);
		CheckUtils.isNotNull(label);
		
		if (EqualsUtils.equals(accessor.getLabel(), label))
			return;
		
		String uuid = accessor.getId();
		String type = accessor.getType().name().toLowerCase();
		
		Main.getSettings().setStringProperty(
				"task.custom_field." + uuid + "." + type,
				label);
		
		this.items.remove(accessor);
		
		accessor = new TaskPropertyAccessor(
				accessor.getId(),
				accessor.getFieldSettingsPropertyName(),
				accessor.getType(),
				accessor.getPropertyName(),
				label,
				accessor.isEditable(),
				accessor.isUsable(),
				accessor.isSortable());
		
		this.items.add(accessor);
	}
	
	public void removeColumn(PropertyAccessor<Task> accessor) {
		CheckUtils.isNotNull(accessor);
		
		String uuid = accessor.getId();
		String type = accessor.getType().name().toLowerCase();
		
		Main.getSettings().remove("task.custom_field." + uuid + "." + type);
		
		List<Task> tasks = TaskFactory.getInstance().getList();
		for (Task task : tasks) {
			task.getProperties().remove("task.custom_field." + uuid + "." + type);
		}
		
		this.items.remove(accessor);
	}
	
	@Override
	public void addListEventListener(
			ListEventListener<PropertyAccessor<Task>> listener) {
		this.items.addListEventListener(listener);
	}
	
	@Override
	public void removeListEventListener(
			ListEventListener<PropertyAccessor<Task>> listener) {
		this.items.removeListEventListener(listener);
	}
	
}
