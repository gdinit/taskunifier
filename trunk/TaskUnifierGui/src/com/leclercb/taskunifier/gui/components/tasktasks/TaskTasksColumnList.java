/*
 * NoteUnifier
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
package com.leclercb.taskunifier.gui.components.tasktasks;

import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.MappedValue;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskList.TaskItem;
import com.leclercb.taskunifier.gui.api.accessor.DefaultPropertyAccessor;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorList;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;
import com.leclercb.taskunifier.gui.commons.editors.TaskTaskLinkEditor;
import com.leclercb.taskunifier.gui.commons.values.IconValueEdit;
import com.leclercb.taskunifier.gui.commons.values.IconValueSelect;
import com.leclercb.taskunifier.gui.translations.Translations;

public class TaskTasksColumnList extends PropertyAccessorList<TaskItem> {
	
	public static final String LINK = "LINK";
	public static final String TASK = "TASK";
	public static final String EDIT = "EDIT";
	public static final String SELECT = "SELECT";
	
	private static TaskTasksColumnList INSTANCE;
	
	public static TaskTasksColumnList getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskTasksColumnList();
		
		return INSTANCE;
	}
	
	private TaskTasksColumnList() {
		this.initialize();
	}
	
	private void initialize() {
		this.add(new DefaultPropertyAccessor<TaskItem>(
				"LINK",
				null,
				PropertyAccessorType.STRING,
				null,
				Translations.getString("general.task.link"),
				true,
				true,
				true) {
			
			private TableCellEditor editor;
			
			@Override
			public TableCellEditor getCellEditor() {
				if (this.editor == null) {
					this.editor = new TaskTaskLinkEditor();
				}
				
				return this.editor;
			}
			
			@Override
			public Object getProperty(TaskItem item) {
				return item.getLink();
			}
			
			@Override
			public void setProperty(TaskItem item, Object value) {
				item.setLink((String) value);
			}
			
		});
		
		this.add(new DefaultPropertyAccessor<TaskItem>(
				"TASK",
				null,
				PropertyAccessorType.TASK,
				null,
				Translations.getString("general.task"),
				true,
				true,
				true) {
			
			@Override
			public Object getProperty(TaskItem item) {
				return item.getTask();
			}
			
			@Override
			public void setProperty(TaskItem item, Object value) {
				item.setTask((Task) value);
			}
			
		});
		
		this.add(new DefaultPropertyAccessor<TaskItem>(
				"EDIT",
				null,
				PropertyAccessorType.VOID,
				null,
				Translations.getString("general.edit"),
				false,
				true,
				true) {
			
			private TableCellRenderer renderer;
			
			@Override
			public TableCellRenderer getCellRenderer() {
				if (this.renderer == null) {
					this.renderer = new DefaultTableRenderer(new MappedValue(
							null,
							IconValueEdit.INSTANCE), SwingConstants.CENTER);
				}
				
				return this.renderer;
			}
			
			@Override
			public Object getProperty(TaskItem item) {
				return null;
			}
			
			@Override
			public void setProperty(TaskItem item, Object value) {
				
			}
			
		});
		
		this.add(new DefaultPropertyAccessor<TaskItem>(
				"SELECT",
				null,
				PropertyAccessorType.VOID,
				null,
				Translations.getString("general.select"),
				false,
				true,
				true) {
			
			private TableCellRenderer renderer;
			
			@Override
			public TableCellRenderer getCellRenderer() {
				if (this.renderer == null) {
					this.renderer = new DefaultTableRenderer(new MappedValue(
							null,
							IconValueSelect.INSTANCE), SwingConstants.CENTER);
				}
				
				return this.renderer;
			}
			
			@Override
			public Object getProperty(TaskItem item) {
				return null;
			}
			
			@Override
			public void setProperty(TaskItem item, Object value) {
				
			}
			
		});
	}
	
}
