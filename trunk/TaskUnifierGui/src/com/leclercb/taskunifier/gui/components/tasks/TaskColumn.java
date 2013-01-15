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
package com.leclercb.taskunifier.gui.components.tasks;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.leclercb.commons.api.event.propertychange.PropertyChangeSupport;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.Context;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.Location;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelList;
import com.leclercb.taskunifier.api.models.TagList;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.Timer;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.gui.api.models.GuiTask;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.table.TUColumn;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.TaskUtils;

public enum TaskColumn implements TUColumn<Task>, PropertyChangeListener, PropertyChangeSupported {
	
	MODEL(Task.class, Task.PROP_MODEL_ID, Translations.getString("general.task.id"), false, true),
	MODEL_EDIT(Void.class, null, Translations.getString("general.edit"), false, false),
	MODEL_CREATION_DATE(Calendar.class, Task.PROP_MODEL_CREATION_DATE, Translations.getString("general.creation_date"), false, true),
	MODEL_UPDATE_DATE(Calendar.class, Task.PROP_MODEL_UPDATE_DATE, Translations.getString("general.update_date"), false, true),
	SHOW_CHILDREN(Boolean.class, GuiTask.PROP_SHOW_CHILDREN, Translations.getString("general.task.show_children"), true, false),
	TITLE(String.class, Task.PROP_TITLE, Translations.getString("general.task.title"), true, true),
	ORDER(Integer.class, Task.PROP_ORDER, Translations.getString("general.task.order"), false, false),
	TAGS(String.class, Task.PROP_TAGS, Translations.getString("general.task.tags"), true, true),
	FOLDER(Folder.class, Task.PROP_FOLDER, Translations.getString("general.task.folder"), true, true),
	CONTEXTS(Context.class, Task.PROP_CONTEXTS, Translations.getString("general.task.context"), true, true),
	GOALS(Goal.class, Task.PROP_GOALS, Translations.getString("general.task.goal"), true, true),
	LOCATIONS(Location.class, Task.PROP_LOCATIONS, Translations.getString("general.task.location"), true, true),
	PARENT(Task.class, Task.PROP_PARENT, Translations.getString("general.task.parent"), false, true),
	PROGRESS(Double.class, Task.PROP_PROGRESS, Translations.getString("general.task.progress"), true, true),
	COMPLETED(Boolean.class, Task.PROP_COMPLETED, Translations.getString("general.task.completed"), true, true),
	COMPLETED_ON(Calendar.class, Task.PROP_COMPLETED_ON, Translations.getString("general.task.completed_on"), false, true),
	DUE_DATE(Calendar.class, Task.PROP_DUE_DATE, Translations.getString("general.task.due_date"), true, true),
	START_DATE(Calendar.class, Task.PROP_START_DATE, Translations.getString("general.task.start_date"), true, true),
	DUE_DATE_REMINDER(Integer.class, Task.PROP_DUE_DATE_REMINDER, Translations.getString("general.task.due_date_reminder"), true, true),
	START_DATE_REMINDER(Integer.class, Task.PROP_START_DATE_REMINDER, Translations.getString("general.task.start_date_reminder"), true, true),
	REPEAT(String.class, Task.PROP_REPEAT, Translations.getString("general.task.repeat"), true, true),
	REPEAT_FROM(TaskRepeatFrom.class, Task.PROP_REPEAT_FROM, Translations.getString("general.task.repeat_from"), true, true),
	STATUS(String.class, Task.PROP_STATUS, Translations.getString("general.task.status"), true, true),
	LENGTH(Integer.class, Task.PROP_LENGTH, Translations.getString("general.task.length"), true, true),
	TIMER(Timer.class, Task.PROP_TIMER, Translations.getString("general.task.timer"), true, true),
	PRIORITY(TaskPriority.class, Task.PROP_PRIORITY, Translations.getString("general.task.priority"), true, true),
	STAR(Boolean.class, Task.PROP_STAR, Translations.getString("general.task.star"), true, true),
	IMPORTANCE(Integer.class, null, Translations.getString("general.task.importance"), false, true),
	NOTE(String.class, Task.PROP_NOTE, Translations.getString("general.task.note"), false, true),
	CONTACTS(String.class, Task.PROP_CONTACTS, Translations.getString("general.task.contacts"), false, true),
	TASKS(String.class, Task.PROP_TASKS, Translations.getString("general.task.tasks"), false, true),
	FILES(String.class, Task.PROP_FILES, Translations.getString("general.task.files"), false, true);
	
	public static final String PROP_USED = "used";
	
	public static final Boolean MULTIPLE_CONTEXTS = Main.getSettings().getBooleanProperty(
			"theme.task.field.contexts.multiple");
	public static final Boolean MULTIPLE_GOALS = Main.getSettings().getBooleanProperty(
			"theme.task.field.goals.multiple");
	public static final Boolean MULTIPLE_LOCATIONS = Main.getSettings().getBooleanProperty(
			"theme.task.field.locations.multiple");
	
	public static TaskColumn parsePropertyName(String propertyName) {
		if (propertyName == null)
			return null;
		
		for (TaskColumn column : values()) {
			if (EqualsUtils.equals(column.getPropertyName(), propertyName))
				return column;
		}
		
		return null;
	}
	
	public static TaskColumn[] getUsableColumns() {
		return getUsedColumns(true);
	}
	
	public static TaskColumn[] getUsableColumns(boolean includeNote) {
		List<TaskColumn> columns = new ArrayList<TaskColumn>();
		
		for (TaskColumn column : values()) {
			if (column.isUsable())
				columns.add(column);
		}
		
		if (!includeNote)
			columns.remove(TaskColumn.NOTE);
		
		return columns.toArray(new TaskColumn[0]);
	}
	
	public static TaskColumn[] getUsedColumns() {
		return getUsedColumns(true);
	}
	
	public static TaskColumn[] getUsedColumns(boolean includeNote) {
		List<TaskColumn> columns = new ArrayList<TaskColumn>();
		
		for (TaskColumn column : values()) {
			if (column.isUsable() && column.isUsed())
				columns.add(column);
		}
		
		if (!includeNote)
			columns.remove(TaskColumn.NOTE);
		
		return columns.toArray(new TaskColumn[0]);
	}
	
	private PropertyChangeSupport propertyChangeSupport;
	
	private Class<?> type;
	private String propertyName;
	private String label;
	private boolean editable;
	private boolean usable;
	private boolean used;
	
	private TaskColumn(
			Class<?> type,
			String propertyName,
			String label,
			boolean editable,
			boolean usable) {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		
		this.setType(type);
		this.setPropertyName(propertyName);
		this.setLabel(label);
		this.setEditable(editable);
		this.setUsable(usable);
		
		this.setUsed(Main.getSettings().getBooleanProperty(
				"task.field." + this.name().toLowerCase() + ".used",
				true));
		
		Main.getSettings().addPropertyChangeListener(
				"task.field." + this.name().toLowerCase() + ".used",
				new WeakPropertyChangeListener(Main.getSettings(), this));
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
				"task.field." + this.name().toLowerCase() + ".used",
				used);
		
		this.propertyChangeSupport.firePropertyChange(PROP_USED, oldUsed, used);
	}
	
	@Override
	public String toString() {
		return this.label;
	}
	
	@Override
	public Object getProperty(Task task) {
		if (task == null)
			return null;
		
		switch (this) {
			case MODEL:
				return task;
			case MODEL_EDIT:
				return null;
			case MODEL_CREATION_DATE:
				return task.getModelCreationDate();
			case MODEL_UPDATE_DATE:
				return task.getModelUpdateDate();
			case SHOW_CHILDREN:
				return ((GuiTask) task).isShowChildren();
			case TITLE:
				return task.getTitle();
			case ORDER:
				return task.getOrder();
			case TAGS:
				return task.getTags().toString();
			case FOLDER:
				return task.getFolder();
			case CONTEXTS:
				if (MULTIPLE_CONTEXTS)
					return task.getContexts();
				else if (task.getContexts().size() > 0)
					return task.getContexts().get(0);
				else
					return null;
			case GOALS:
				if (MULTIPLE_GOALS)
					return task.getGoals();
				else if (task.getGoals().size() > 0)
					return task.getGoals().get(0);
				else
					return null;
			case LOCATIONS:
				if (MULTIPLE_LOCATIONS)
					return task.getLocations();
				else if (task.getLocations().size() > 0)
					return task.getLocations().get(0);
				else
					return null;
			case PARENT:
				return task.getParent();
			case PROGRESS:
				return task.getProgress();
			case COMPLETED:
				return task.isCompleted();
			case COMPLETED_ON:
				return task.getCompletedOn();
			case DUE_DATE:
				return task.getDueDate();
			case START_DATE:
				return task.getStartDate();
			case DUE_DATE_REMINDER:
				return task.getDueDateReminder();
			case START_DATE_REMINDER:
				return task.getStartDateReminder();
			case REPEAT:
				return task.getRepeat();
			case REPEAT_FROM:
				return task.getRepeatFrom();
			case STATUS:
				return task.getStatus();
			case LENGTH:
				return task.getLength();
			case TIMER:
				return task.getTimer();
			case PRIORITY:
				return task.getPriority();
			case STAR:
				return task.isStar();
			case NOTE:
				return task.getNote();
			case IMPORTANCE:
				return TaskUtils.getImportance(task);
			case CONTACTS:
				return task.getContacts().toString();
			case TASKS:
				return task.getTasks().toString();
			case FILES:
				return task.getFiles().toString();
			default:
				return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setProperty(Task task, Object value) {
		if (task == null)
			return;
		
		switch (this) {
			case MODEL:
				break;
			case MODEL_EDIT:
				break;
			case MODEL_CREATION_DATE:
				break;
			case MODEL_UPDATE_DATE:
				break;
			case SHOW_CHILDREN:
				((GuiTask) task).setShowChildren((Boolean) value);
				break;
			case TITLE:
				task.setTitle((String) value);
				break;
			case ORDER:
				task.setOrder((Integer) value);
				break;
			case TAGS:
				task.setTags(TagList.fromString((String) value));
				break;
			case FOLDER:
				task.setFolder((Folder) value);
				break;
			case CONTEXTS:
				if (value instanceof Model) {
					task.getContexts().clear();
					task.getContexts().add((Context) value);
				} else {
					task.setContexts((ModelList<Context>) value);
				}
				
				break;
			case GOALS:
				if (value instanceof Model) {
					task.getGoals().clear();
					task.getGoals().add((Goal) value);
				} else {
					task.setGoals((ModelList<Goal>) value);
				}
				
				break;
			case LOCATIONS:
				if (value instanceof Model) {
					task.getLocations().clear();
					task.getLocations().add((Location) value);
				} else {
					task.setLocations((ModelList<Location>) value);
				}
				
				break;
			case PARENT:
				task.setParent((Task) value);
				break;
			case PROGRESS:
				task.setProgress((Double) value);
				break;
			case COMPLETED:
				task.setCompleted((Boolean) value);
				break;
			case COMPLETED_ON:
				task.setCompletedOn((Calendar) value);
				break;
			case DUE_DATE:
				task.setDueDate((Calendar) value);
				break;
			case START_DATE:
				task.setStartDate((Calendar) value);
				break;
			case DUE_DATE_REMINDER:
				if (value == null || !(value instanceof Integer))
					task.setDueDateReminder(0);
				else
					task.setDueDateReminder((Integer) value);
				break;
			case START_DATE_REMINDER:
				if (value == null || !(value instanceof Integer))
					task.setStartDateReminder(0);
				else
					task.setStartDateReminder((Integer) value);
				break;
			case REPEAT:
				task.setRepeat((String) value);
				break;
			case REPEAT_FROM:
				task.setRepeatFrom((TaskRepeatFrom) value);
				break;
			case STATUS:
				task.setStatus((String) value);
				break;
			case LENGTH:
				if (value == null || !(value instanceof Integer))
					task.setLength(0);
				else
					task.setLength((Integer) value);
				break;
			case TIMER:
				task.setTimer((Timer) value);
				break;
			case PRIORITY:
				task.setPriority((TaskPriority) value);
				break;
			case STAR:
				task.setStar((Boolean) value);
				break;
			case NOTE:
				task.setNote((String) value);
				break;
			case IMPORTANCE:
				break;
			case CONTACTS:
				break;
			case TASKS:
				break;
			case FILES:
				break;
		}
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
