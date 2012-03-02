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
package com.leclercb.taskunifier.gui.components.tasks.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;

import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.table.TableColumnExt;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.commons.values.BooleanValueBoolean;
import com.leclercb.taskunifier.gui.commons.values.IconValueCompleted;
import com.leclercb.taskunifier.gui.commons.values.IconValueEdit;
import com.leclercb.taskunifier.gui.commons.values.IconValueModel;
import com.leclercb.taskunifier.gui.commons.values.IconValueNote;
import com.leclercb.taskunifier.gui.commons.values.IconValueReminder;
import com.leclercb.taskunifier.gui.commons.values.IconValueRepeat;
import com.leclercb.taskunifier.gui.commons.values.IconValueStar;
import com.leclercb.taskunifier.gui.commons.values.IconValueTaskContacts;
import com.leclercb.taskunifier.gui.commons.values.IconValueTaskFiles;
import com.leclercb.taskunifier.gui.commons.values.IconValueTaskPriority;
import com.leclercb.taskunifier.gui.commons.values.IconValueTaskTasks;
import com.leclercb.taskunifier.gui.commons.values.IconValueTimer;
import com.leclercb.taskunifier.gui.commons.values.StringValueCalendar;
import com.leclercb.taskunifier.gui.commons.values.StringValueModel;
import com.leclercb.taskunifier.gui.commons.values.StringValueModelId;
import com.leclercb.taskunifier.gui.commons.values.StringValueModelOrder;
import com.leclercb.taskunifier.gui.commons.values.StringValueTaskLength;
import com.leclercb.taskunifier.gui.commons.values.StringValueTaskPriority;
import com.leclercb.taskunifier.gui.commons.values.StringValueTaskProgress;
import com.leclercb.taskunifier.gui.commons.values.StringValueTaskReminder;
import com.leclercb.taskunifier.gui.commons.values.StringValueTaskRepeat;
import com.leclercb.taskunifier.gui.commons.values.StringValueTaskRepeatFrom;
import com.leclercb.taskunifier.gui.commons.values.StringValueTaskStatus;
import com.leclercb.taskunifier.gui.commons.values.StringValueTaskTitle;
import com.leclercb.taskunifier.gui.commons.values.StringValueTimer;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumn;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnsProperties;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnsProperties.TaskColumnProperties;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.ContextEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.DateEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.FolderEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.GoalEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.LengthEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.LocationEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.PriorityEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.ProgressEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.ReminderEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.RepeatEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.RepeatFromEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.StatusEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.TagsEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.TimerEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.editors.TitleEditor;
import com.leclercb.taskunifier.gui.components.tasks.table.renderers.ShowChildrenRenderer;
import com.leclercb.taskunifier.gui.components.tasks.table.sorter.TaskRowComparator;
import com.leclercb.taskunifier.gui.main.Main;

public class TaskTableColumn extends TableColumnExt {
	
	private static final TableCellRenderer GENERIC_RENDERER;
	
	private static final TableCellRenderer CONTACTS_RENDERER;
	private static final TableCellRenderer COMPLETED_RENDERER;
	private static final TableCellRenderer COMPLETED_ON_RENDERER;
	private static final TableCellRenderer DUE_DATE_RENDERER;
	private static final TableCellRenderer FILES_RENDERER;
	private static final TableCellRenderer LENGTH_RENDERER;
	private static final TableCellRenderer MODEL_ID_RENDERER;
	private static final TableCellRenderer MODEL_RENDERER;
	private static final TableCellRenderer MODEL_EDIT_RENDERER;
	private static final TableCellRenderer MODEL_CREATION_DATE_RENDERER;
	private static final TableCellRenderer MODEL_UPDATE_DATE_RENDERER;
	private static final TableCellRenderer NOTE_RENDERER;
	private static final TableCellRenderer ORDER_RENDERER;
	private static final TableCellRenderer PROGRESS_RENDERER;
	private static final TableCellRenderer REMINDER_RENDERER;
	private static final TableCellRenderer REPEAT_RENDERER;
	private static final TableCellRenderer SHOW_CHILDREN_RENDERER;
	private static final TableCellRenderer STAR_RENDERER;
	private static final TableCellRenderer START_DATE_RENDERER;
	private static final TableCellRenderer PRIORITY_RENDERER;
	private static final TableCellRenderer REPEAT_FROM_RENDERER;
	private static final TableCellRenderer STATUS_RENDERER;
	private static final TableCellRenderer TASKS_RENDERER;
	private static final TableCellRenderer TIMER_RENDERER;
	private static final TableCellRenderer TITLE_RENDERER;
	
	private static final TableCellEditor BOOLEAN_EDITOR;
	private static final TableCellEditor CONTEXT_EDITOR;
	private static final TableCellEditor DUE_DATE_EDITOR;
	private static final TableCellEditor FOLDER_EDITOR;
	private static final TableCellEditor GOAL_EDITOR;
	private static final TableCellEditor LENGTH_EDITOR;
	private static final TableCellEditor LOCATION_EDITOR;
	private static final TableCellEditor PROGRESS_EDITOR;
	private static final TableCellEditor REMINDER_EDITOR;
	private static final TableCellEditor REPEAT_EDITOR;
	private static final TableCellEditor START_DATE_EDITOR;
	private static final TableCellEditor TAGS_EDITOR;
	private static final TableCellEditor PRIORITY_EDITOR;
	private static final TableCellEditor REPEAT_FROM_EDITOR;
	private static final TableCellEditor STATUS_EDITOR;
	private static final TableCellEditor TIMER_EDITOR;
	private static final TableCellEditor TITLE_EDITOR;
	
	static {
		GENERIC_RENDERER = new DefaultTableRenderer();
		
		CONTACTS_RENDERER = new DefaultTableRenderer(new MappedValue(
				null,
				IconValueTaskContacts.INSTANCE), SwingConstants.CENTER);
		
		COMPLETED_RENDERER = new DefaultTableRenderer(new MappedValue(
				null,
				IconValueCompleted.INSTANCE,
				BooleanValueBoolean.INSTANCE), SwingConstants.CENTER);
		
		COMPLETED_ON_RENDERER = new DefaultTableRenderer(
				StringValueCalendar.INSTANCE_DATE_TIME);
		
		DUE_DATE_RENDERER = new DefaultTableRenderer(
				(Main.getSettings().getBooleanProperty("date.use_due_time") ? StringValueCalendar.INSTANCE_DATE_TIME : StringValueCalendar.INSTANCE_DATE));
		
		FILES_RENDERER = new DefaultTableRenderer(new MappedValue(
				null,
				IconValueTaskFiles.INSTANCE), SwingConstants.CENTER);
		
		LENGTH_RENDERER = new DefaultTableRenderer(
				StringValueTaskLength.INSTANCE);
		
		MODEL_ID_RENDERER = new DefaultTableRenderer(
				StringValueModelId.INSTANCE);
		
		MODEL_RENDERER = new DefaultTableRenderer(new MappedValue(
				StringValueModel.INSTANCE,
				IconValueModel.INSTANCE));
		
		MODEL_EDIT_RENDERER = new DefaultTableRenderer(new MappedValue(
				null,
				IconValueEdit.INSTANCE), SwingConstants.CENTER);
		
		((DefaultTableRenderer) MODEL_EDIT_RENDERER).getComponentProvider().setHorizontalAlignment(
				SwingConstants.CENTER);
		
		MODEL_CREATION_DATE_RENDERER = new DefaultTableRenderer(
				StringValueCalendar.INSTANCE_DATE_TIME);
		
		MODEL_UPDATE_DATE_RENDERER = new DefaultTableRenderer(
				StringValueCalendar.INSTANCE_DATE_TIME);
		
		NOTE_RENDERER = new DefaultTableRenderer(new MappedValue(
				null,
				IconValueNote.INSTANCE), SwingConstants.CENTER);
		
		((DefaultTableRenderer) NOTE_RENDERER).getComponentProvider().setHorizontalAlignment(
				SwingConstants.CENTER);
		
		ORDER_RENDERER = new DefaultTableRenderer(
				StringValueModelOrder.INSTANCE);
		
		PROGRESS_RENDERER = new DefaultTableRenderer(
				StringValueTaskProgress.INSTANCE);
		
		REMINDER_RENDERER = new DefaultTableRenderer(new MappedValue(
				StringValueTaskReminder.INSTANCE,
				IconValueReminder.INSTANCE));
		
		REPEAT_RENDERER = new DefaultTableRenderer(new MappedValue(
				StringValueTaskRepeat.INSTANCE,
				IconValueRepeat.INSTANCE));
		
		SHOW_CHILDREN_RENDERER = new ShowChildrenRenderer();
		
		STAR_RENDERER = new DefaultTableRenderer(new MappedValue(
				null,
				IconValueStar.INSTANCE,
				BooleanValueBoolean.INSTANCE), SwingConstants.CENTER);
		
		START_DATE_RENDERER = new DefaultTableRenderer(
				(Main.getSettings().getBooleanProperty("date.use_start_time") ? StringValueCalendar.INSTANCE_DATE_TIME : StringValueCalendar.INSTANCE_DATE));
		
		PRIORITY_RENDERER = new DefaultTableRenderer(new MappedValue(
				StringValueTaskPriority.INSTANCE,
				IconValueTaskPriority.INSTANCE));
		
		REPEAT_FROM_RENDERER = new DefaultTableRenderer(
				StringValueTaskRepeatFrom.INSTANCE);
		
		STATUS_RENDERER = new DefaultTableRenderer(
				StringValueTaskStatus.INSTANCE);
		
		TASKS_RENDERER = new DefaultTableRenderer(new MappedValue(
				null,
				IconValueTaskTasks.INSTANCE), SwingConstants.CENTER);
		
		TIMER_RENDERER = new DefaultTableRenderer(new MappedValue(
				StringValueTimer.INSTANCE,
				IconValueTimer.INSTANCE));
		
		TITLE_RENDERER = new DefaultTableRenderer(StringValueTaskTitle.INSTANCE);
		
		BOOLEAN_EDITOR = new JXTable.BooleanEditor();
		CONTEXT_EDITOR = new ContextEditor();
		DUE_DATE_EDITOR = new DateEditor(Main.getSettings().getBooleanProperty(
				"date.use_due_time"));
		FOLDER_EDITOR = new FolderEditor();
		GOAL_EDITOR = new GoalEditor();
		LENGTH_EDITOR = new LengthEditor();
		LOCATION_EDITOR = new LocationEditor();
		PROGRESS_EDITOR = new ProgressEditor();
		REMINDER_EDITOR = new ReminderEditor();
		REPEAT_EDITOR = new RepeatEditor();
		START_DATE_EDITOR = new DateEditor(
				Main.getSettings().getBooleanProperty("date.use_start_time"));
		TAGS_EDITOR = new TagsEditor();
		PRIORITY_EDITOR = new PriorityEditor();
		REPEAT_FROM_EDITOR = new RepeatFromEditor();
		STATUS_EDITOR = new StatusEditor();
		TIMER_EDITOR = new TimerEditor();
		TITLE_EDITOR = new TitleEditor();
	}
	
	private TaskColumnProperties taskColumn;
	
	public TaskTableColumn(TaskColumnProperties taskColumn) {
		super(taskColumn.getColumn().ordinal());
		
		CheckUtils.isNotNull(taskColumn);
		
		this.taskColumn = taskColumn;
		
		this.setIdentifier(taskColumn.getColumn());
		this.setHeaderValue(taskColumn.getColumn().getLabel());
		this.setPreferredWidth(taskColumn.getWidth());
		this.setVisible(taskColumn.isVisible());
		
		this.taskColumn.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(
						TaskColumnsProperties.PROP_VISIBLE)) {
					TaskTableColumn.this.setVisible((Boolean) evt.getNewValue());
				}
				
				if (evt.getPropertyName().equals(
						TaskColumnsProperties.PROP_WIDTH)) {
					TaskTableColumn.this.setPreferredWidth((Integer) evt.getNewValue());
				}
			}
			
		});
	}
	
	@Override
	public Comparator<?> getComparator() {
		if (this.taskColumn.getColumn() == TaskColumn.MODEL)
			return TaskRowComparator.getInstance();
		
		return super.getComparator();
	}
	
	@Override
	public boolean isSortable() {
		if (this.taskColumn.getColumn() == TaskColumn.MODEL)
			return true;
		
		return false;
	}
	
	@Override
	public void setPreferredWidth(int preferredWidth) {
		this.taskColumn.setWidth(preferredWidth);
		super.setPreferredWidth(preferredWidth);
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.taskColumn.setVisible(visible);
		super.setVisible(visible);
	}
	
	@Override
	public TableCellRenderer getCellRenderer() {
		switch (this.taskColumn.getColumn()) {
			case MODEL:
				return MODEL_ID_RENDERER;
			case MODEL_EDIT:
				return MODEL_EDIT_RENDERER;
			case MODEL_CREATION_DATE:
				return MODEL_CREATION_DATE_RENDERER;
			case MODEL_UPDATE_DATE:
				return MODEL_UPDATE_DATE_RENDERER;
			case SHOW_CHILDREN:
				return SHOW_CHILDREN_RENDERER;
			case TITLE:
				return TITLE_RENDERER;
			case ORDER:
				return ORDER_RENDERER;
			case CONTACTS:
				return CONTACTS_RENDERER;
			case TASKS:
				return TASKS_RENDERER;
			case FILES:
				return FILES_RENDERER;
			case PROGRESS:
				return PROGRESS_RENDERER;
			case COMPLETED:
				return COMPLETED_RENDERER;
			case CONTEXT:
			case FOLDER:
			case GOAL:
			case LOCATION:
				return MODEL_RENDERER;
			case COMPLETED_ON:
				return COMPLETED_ON_RENDERER;
			case DUE_DATE:
				return DUE_DATE_RENDERER;
			case START_DATE:
				return START_DATE_RENDERER;
			case DUE_DATE_REMINDER:
			case START_DATE_REMINDER:
				return REMINDER_RENDERER;
			case LENGTH:
				return LENGTH_RENDERER;
			case TIMER:
				return TIMER_RENDERER;
			case STAR:
				return STAR_RENDERER;
			case PRIORITY:
				return PRIORITY_RENDERER;
			case REPEAT:
				return REPEAT_RENDERER;
			case REPEAT_FROM:
				return REPEAT_FROM_RENDERER;
			case STATUS:
				return STATUS_RENDERER;
			case NOTE:
				return NOTE_RENDERER;
			default:
				return GENERIC_RENDERER;
		}
	}
	
	@Override
	public TableCellEditor getCellEditor() {
		switch (this.taskColumn.getColumn()) {
			case SHOW_CHILDREN:
				return BOOLEAN_EDITOR;
			case TITLE:
				return TITLE_EDITOR;
			case TAGS:
				return TAGS_EDITOR;
			case FOLDER:
				return FOLDER_EDITOR;
			case CONTEXT:
				return CONTEXT_EDITOR;
			case GOAL:
				return GOAL_EDITOR;
			case LOCATION:
				return LOCATION_EDITOR;
			case PROGRESS:
				return PROGRESS_EDITOR;
			case COMPLETED:
				return BOOLEAN_EDITOR;
			case DUE_DATE:
				return DUE_DATE_EDITOR;
			case START_DATE:
				return START_DATE_EDITOR;
			case REPEAT:
				return REPEAT_EDITOR;
			case DUE_DATE_REMINDER:
			case START_DATE_REMINDER:
				return REMINDER_EDITOR;
			case REPEAT_FROM:
				return REPEAT_FROM_EDITOR;
			case STATUS:
				return STATUS_EDITOR;
			case LENGTH:
				return LENGTH_EDITOR;
			case TIMER:
				return TIMER_EDITOR;
			case PRIORITY:
				return PRIORITY_EDITOR;
			case STAR:
				return BOOLEAN_EDITOR;
			default:
				return super.getCellEditor();
		}
	}
	
}
