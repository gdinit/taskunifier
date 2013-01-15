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

import java.util.Calendar;

import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.Context;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.Location;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelList;
import com.leclercb.taskunifier.api.models.ModelNote;
import com.leclercb.taskunifier.api.models.ModelParent;
import com.leclercb.taskunifier.api.models.TagList;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.Timer;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.gui.api.models.GuiTask;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.TaskUtils;

public class TaskColumnList extends PropertyAccessorList<Task, TaskColumn> {
	
	private static TaskColumnList INSTANCE;
	
	public static TaskColumnList getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskColumnList();
		
		return INSTANCE;
	}
	
	public static final Boolean MULTIPLE_CONTEXTS = Main.getSettings().getBooleanProperty(
			"theme.task.field.contexts.multiple");
	public static final Boolean MULTIPLE_GOALS = Main.getSettings().getBooleanProperty(
			"theme.task.field.goals.multiple");
	public static final Boolean MULTIPLE_LOCATIONS = Main.getSettings().getBooleanProperty(
			"theme.task.field.locations.multiple");
	
	private TaskColumnList() {
		this.initialize();
	}
	
	private void initialize() {
		this.add("MODEL", new TaskColumn(
				"task.field.model",
				Task.class,
				BasicModel.PROP_MODEL_ID,
				Translations.getString("general.task.id"),
				false,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model;
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				
			}
			
		});
		
		this.add("MODEL_EDIT", new TaskColumn(
				"task.field.model_edit",
				Void.class,
				null,
				Translations.getString("general.edit"),
				false,
				false) {
			
			@Override
			public Object getProperty(Task model) {
				return null;
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				
			}
			
		});
		
		this.add("MODEL_CREATION_DATE", new TaskColumn(
				"task.field.model_creation_date",
				Calendar.class,
				BasicModel.PROP_MODEL_CREATION_DATE,
				Translations.getString("general.creation_date"),
				false,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getModelCreationDate();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				
			}
			
		});
		
		this.add("MODEL_UPDATE_DATE", new TaskColumn(
				"task.field.model_update_date",
				Calendar.class,
				BasicModel.PROP_MODEL_UPDATE_DATE,
				Translations.getString("general.update_date"),
				false,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getModelUpdateDate();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				
			}
			
		});
		
		this.add("SHOW_CHILDREN", new TaskColumn(
				"task.field.show_children",
				Boolean.class,
				GuiTask.PROP_SHOW_CHILDREN,
				Translations.getString("general.task.show_children"),
				true,
				false) {
			
			@Override
			public Object getProperty(Task model) {
				return ((GuiTask) model).isShowChildren();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				((GuiTask) model).setShowChildren((Boolean) value);
			}
			
		});
		
		this.add("TITLE", new TaskColumn(
				"task.field.title",
				String.class,
				BasicModel.PROP_TITLE,
				Translations.getString("general.task.title"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getTitle();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setTitle((String) value);
			}
			
		});
		
		this.add("ORDER", new TaskColumn(
				"task.field.order",
				Integer.class,
				Model.PROP_ORDER,
				Translations.getString("general.task.order"),
				false,
				false) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getOrder();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setOrder((Integer) value);
			}
			
		});
		
		this.add("TAGS", new TaskColumn(
				"task.field.tags",
				String.class,
				Task.PROP_TAGS,
				Translations.getString("general.task.tags"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getTags().toString();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setTags(TagList.fromString((String) value));
			}
			
		});
		
		this.add("FOLDER", new TaskColumn(
				"task.field.folder",
				Folder.class,
				Task.PROP_FOLDER,
				Translations.getString("general.task.folder"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getFolder();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setFolder((Folder) value);
			}
			
		});
		
		this.add("CONTEXTS", new TaskColumn(
				"task.field.contexts",
				Context.class,
				Task.PROP_CONTEXTS,
				Translations.getString("general.task.context"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				if (MULTIPLE_CONTEXTS)
					return model.getContexts();
				else if (model.getContexts().size() > 0)
					return model.getContexts().get(0);
				else
					return null;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void setProperty(Task model, Object value) {
				if (value instanceof Model) {
					model.getContexts().clear();
					model.getContexts().add((Context) value);
				} else {
					model.setContexts((ModelList<Context>) value);
				}
			}
			
		});
		
		this.add("GOALS", new TaskColumn(
				"task.field.goals",
				Goal.class,
				Task.PROP_GOALS,
				Translations.getString("general.task.goal"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				if (MULTIPLE_GOALS)
					return model.getGoals();
				else if (model.getGoals().size() > 0)
					return model.getGoals().get(0);
				else
					return null;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void setProperty(Task model, Object value) {
				if (value instanceof Model) {
					model.getGoals().clear();
					model.getGoals().add((Goal) value);
				} else {
					model.setGoals((ModelList<Goal>) value);
				}
			}
			
		});
		
		this.add("LOCATIONS", new TaskColumn(
				"task.field.locations",
				Location.class,
				Task.PROP_LOCATIONS,
				Translations.getString("general.task.location"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				if (MULTIPLE_LOCATIONS)
					return model.getLocations();
				else if (model.getLocations().size() > 0)
					return model.getLocations().get(0);
				else
					return null;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void setProperty(Task model, Object value) {
				if (value instanceof Model) {
					model.getLocations().clear();
					model.getLocations().add((Location) value);
				} else {
					model.setLocations((ModelList<Location>) value);
				}
			}
			
		});
		
		this.add("PARENT", new TaskColumn(
				"task.field.parent",
				Task.class,
				ModelParent.PROP_PARENT,
				Translations.getString("general.task.parent"),
				false,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getParent();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setParent((Task) value);
			}
			
		});
		
		this.add("PROGRESS", new TaskColumn(
				"task.field.progress",
				Double.class,
				Task.PROP_PROGRESS,
				Translations.getString("general.task.progress"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getProgress();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setProgress((Double) value);
			}
			
		});
		
		this.add("COMPLETED", new TaskColumn(
				"task.field.completed",
				Boolean.class,
				Task.PROP_COMPLETED,
				Translations.getString("general.task.completed"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.isCompleted();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setCompleted((Boolean) value);
			}
			
		});
		
		this.add("COMPLETED_ON", new TaskColumn(
				"task.field.completed_on",
				Calendar.class,
				Task.PROP_COMPLETED_ON,
				Translations.getString("general.task.completed_on"),
				false,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getCompletedOn();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setCompletedOn((Calendar) value);
			}
			
		});
		
		this.add("DUE_DATE", new TaskColumn(
				"task.field.due_date",
				Calendar.class,
				Task.PROP_DUE_DATE,
				Translations.getString("general.task.due_date"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getDueDate();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setDueDate((Calendar) value);
			}
			
		});
		
		this.add("START_DATE", new TaskColumn(
				"task.field.start_date",
				Calendar.class,
				Task.PROP_START_DATE,
				Translations.getString("general.task.start_date"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getStartDate();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setStartDate((Calendar) value);
			}
			
		});
		
		this.add("DUE_DATE_REMINDER", new TaskColumn(
				"task.field.due_date_reminder",
				Integer.class,
				Task.PROP_DUE_DATE_REMINDER,
				Translations.getString("general.task.due_date_reminder"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getDueDateReminder();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				if (value == null || !(value instanceof Integer))
					model.setDueDateReminder(0);
				else
					model.setDueDateReminder((Integer) value);
			}
			
		});
		
		this.add("START_DATE_REMINDER", new TaskColumn(
				"task.field.start_date_reminder",
				Integer.class,
				Task.PROP_START_DATE_REMINDER,
				Translations.getString("general.task.start_date_reminder"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getStartDateReminder();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				if (value == null || !(value instanceof Integer))
					model.setStartDateReminder(0);
				else
					model.setStartDateReminder((Integer) value);
			}
			
		});
		
		this.add("REPEAT", new TaskColumn(
				"task.field.repeat",
				String.class,
				Task.PROP_REPEAT,
				Translations.getString("general.task.repeat"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getRepeat();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setRepeat((String) value);
			}
			
		});
		
		this.add("REPEAT_FROM", new TaskColumn(
				"task.field.repeat_from",
				TaskRepeatFrom.class,
				Task.PROP_REPEAT_FROM,
				Translations.getString("general.task.repeat_from"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getRepeatFrom();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setRepeatFrom((TaskRepeatFrom) value);
			}
			
		});
		
		this.add("STATUS", new TaskColumn(
				"task.field.status",
				String.class,
				Task.PROP_STATUS,
				Translations.getString("general.task.status"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getStatus();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setStatus((String) value);
			}
			
		});
		
		this.add("LENGTH", new TaskColumn(
				"task.field.length",
				Integer.class,
				Task.PROP_LENGTH,
				Translations.getString("general.task.length"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getLength();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				if (value == null || !(value instanceof Integer))
					model.setLength(0);
				else
					model.setLength((Integer) value);
			}
			
		});
		
		this.add("TIMER", new TaskColumn(
				"task.field.timer",
				Timer.class,
				Task.PROP_TIMER,
				Translations.getString("general.task.timer"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getTimer();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setTimer((Timer) value);
			}
			
		});
		
		this.add("PRIORITY", new TaskColumn(
				"task.field.priority",
				TaskPriority.class,
				Task.PROP_PRIORITY,
				Translations.getString("general.task.priority"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getPriority();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setPriority((TaskPriority) value);
			}
			
		});
		
		this.add("STAR", new TaskColumn(
				"task.field.star",
				Boolean.class,
				Task.PROP_STAR,
				Translations.getString("general.task.star"),
				true,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.isStar();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setStar((Boolean) value);
			}
			
		});
		
		this.add("IMPORTANCE", new TaskColumn(
				"task.field.importance",
				Integer.class,
				null,
				Translations.getString("general.task.importance"),
				false,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return TaskUtils.getImportance(model);
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				
			}
			
		});
		
		this.add("NOTE", new TaskColumn(
				"task.field.note",
				String.class,
				ModelNote.PROP_NOTE,
				Translations.getString("general.task.note"),
				false,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getNote();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				model.setNote((String) value);
			}
			
		});
		
		this.add("CONTACTS", new TaskColumn(
				"task.field.contacts",
				String.class,
				Task.PROP_CONTACTS,
				Translations.getString("general.task.contacts"),
				false,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getContacts().toString();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				
			}
			
		});
		
		this.add("TASKS", new TaskColumn(
				"task.field.tasks",
				String.class,
				Task.PROP_TASKS,
				Translations.getString("general.task.tasks"),
				false,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getTasks().toString();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				
			}
			
		});
		
		this.add("FILES", new TaskColumn(
				"task.field.files",
				String.class,
				Task.PROP_FILES,
				Translations.getString("general.task.files"),
				false,
				true) {
			
			@Override
			public Object getProperty(Task model) {
				return model.getFiles().toString();
			}
			
			@Override
			public void setProperty(Task model, Object value) {
				
			}
			
		});
	}
	
}
