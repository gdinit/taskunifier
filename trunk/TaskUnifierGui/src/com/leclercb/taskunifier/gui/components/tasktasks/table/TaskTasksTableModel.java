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
package com.leclercb.taskunifier.gui.components.tasktasks.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;

import javax.swing.table.AbstractTableModel;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.api.models.TaskList;
import com.leclercb.taskunifier.api.models.TaskList.TaskItem;
import com.leclercb.taskunifier.gui.components.tasktasks.TaskTasksColumn;

public class TaskTasksTableModel extends AbstractTableModel implements ListChangeListener, PropertyChangeListener {
	
	private TaskList tasks;
	
	public TaskTasksTableModel() {
		this.tasks = new TaskList();
	}
	
	public TaskList getTaskGroup() {
		return this.tasks;
	}
	
	public void setTaskGroup(TaskList tasks) {
		if (EqualsUtils.equals(this.tasks, tasks))
			return;
		
		if (this.tasks != null) {
			this.tasks.removeListChangeListener(this);
			this.tasks.removePropertyChangeListener(this);
		}
		
		this.tasks = tasks;
		
		if (this.tasks != null) {
			this.tasks.addListChangeListener(new WeakListChangeListener(
					this.tasks,
					this));
			this.tasks.addPropertyChangeListener(new WeakPropertyChangeListener(
					this.tasks,
					this));
		}
		
		try {
			this.fireTableDataChanged();
		} catch (Exception e) {
			
		}
	}
	
	public TaskItem getTaskItem(int row) {
		return this.tasks.get(row);
	}
	
	public TaskTasksColumn getTaskTasksColumn(int col) {
		return TaskTasksColumn.values()[col];
	}
	
	@Override
	public int getColumnCount() {
		return TaskTasksColumn.values().length;
	}
	
	@Override
	public int getRowCount() {
		if (this.tasks == null)
			return 0;
		
		return this.tasks.size();
	}
	
	@Override
	public String getColumnName(int col) {
		return TaskTasksColumn.values()[col].getLabel();
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return TaskTasksColumn.values()[col].getType();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		TaskItem item = this.tasks.get(row);
		return TaskTasksColumn.values()[col].getProperty(item);
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return TaskTasksColumn.values()[col].isEditable();
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		TaskItem item = this.tasks.get(row);
		TaskTasksColumn column = TaskTasksColumn.values()[col];
		
		Object oldValue = column.getProperty(item);
		
		if (!EqualsUtils.equals(oldValue, value)) {
			column.setProperty(item, value);
		}
	}
	
	@Override
	public void listChange(ListChangeEvent event) {
		try {
			if (event.getChangeType() == ListChangeEvent.VALUE_ADDED) {
				this.fireTableRowsInserted(event.getIndex(), event.getIndex());
			} else if (event.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
				this.fireTableRowsDeleted(event.getIndex(), event.getIndex());
			}
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		int index = this.tasks.getIndexOf((TaskItem) event.getSource());
		this.fireTableRowsUpdated(index, index);
	}
	
}
