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
package com.leclercb.taskunifier.gui.components.taskedit.propertytable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.utils.TaskCustomColumnList;

public class TaskPropertyTableModel extends AbstractTableModel {
	
	private Task task;
	
	private List<TaskPropertyItem> items;
	
	public TaskPropertyTableModel() {
		this.items = new ArrayList<TaskPropertyItem>();
		
		for (PropertyAccessor<Task> accessor : TaskCustomColumnList.getInstance().getInitialPropertyAccessors()) {
			this.items.add(new TaskPropertyItem(accessor));
		}
	}
	
	public Task getTask() {
		return this.task;
	}
	
	public void setTask(Task task) {
		this.task = task;
		
		for (TaskPropertyItem item : this.items) {
			item.setEdit(false);
			
			if (this.task == null)
				item.setValue(null);
			else
				item.setValue(item.getAccessor().getProperty(this.task));
		}
	}
	
	public TaskPropertyItem getTaskPropertyItem(int index) {
		return this.items.get(index);
	}
	
	@Override
	public int getColumnCount() {
		return 3;
	}
	
	@Override
	public int getRowCount() {
		return this.items.size();
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0:
				return "Edit";
			case 1:
				return "Property";
			case 2:
				return "Value";
			default:
				return null;
		}
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		switch (col) {
			case 0:
				return Boolean.class;
			case 1:
				return String.class;
			case 2:
				return Object.class;
			default:
				return null;
		}
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		switch (col) {
			case 0:
				return this.items.get(row).isEdit();
			case 1:
				return this.items.get(row).getAccessor().getLabel();
			case 2:
				return this.items.get(row).getValue();
			default:
				return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		switch (col) {
			case 0:
				return true;
			case 1:
				return false;
			case 2:
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		switch (col) {
			case 0:
				this.items.get(row).setEdit((Boolean) value);
			case 1:
				break;
			case 2:
				this.items.get(row).setValue(value);
			default:
				break;
		}
	}
	
	public static class TaskPropertyItem {
		
		private boolean edit;
		private PropertyAccessor<Task> accessor;
		private Object value;
		
		public TaskPropertyItem(PropertyAccessor<Task> accessor) {
			this.setEdit(false);
			this.setAccessor(accessor);
			this.setValue(null);
		}
		
		public boolean isEdit() {
			return this.edit;
		}
		
		public void setEdit(boolean edit) {
			this.edit = edit;
		}
		
		public PropertyAccessor<Task> getAccessor() {
			return this.accessor;
		}
		
		private void setAccessor(PropertyAccessor<Task> accessor) {
			CheckUtils.isNotNull(accessor);
			this.accessor = accessor;
		}
		
		public Object getValue() {
			return this.value;
		}
		
		public void setValue(Object value) {
			this.value = value;
		}
		
	}
	
}
