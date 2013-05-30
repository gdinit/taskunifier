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
package com.leclercb.taskunifier.gui.components.tasks.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.AbstractTableModel;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelParent;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.models.GuiTask;
import com.leclercb.taskunifier.gui.commons.undoableedit.TaskEditUndoableEdit;
import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.utils.UndoSupport;

public class TaskTableModel extends AbstractTableModel implements ListChangeListener, PropertyChangeListener {
	
	private UndoSupport undoSupport;
	
	public TaskTableModel(UndoSupport undoSupport) {
		CheckUtils.isNotNull(undoSupport);
		this.undoSupport = undoSupport;
		
		TaskFactory.getInstance().addListChangeListener(
				new WeakListChangeListener(TaskFactory.getInstance(), this));
		
		TaskFactory.getInstance().addPropertyChangeListener(
				new WeakPropertyChangeListener(TaskFactory.getInstance(), this));
		
		Synchronizing.getInstance().addPropertyChangeListener(
				Synchronizing.PROP_SYNCHRONIZING,
				new WeakPropertyChangeListener(
						Synchronizing.getInstance(),
						this));
	}
	
	public Task getTask(int row) {
		return TaskFactory.getInstance().get(row);
	}
	
	public PropertyAccessor<Task> getTaskColumn(int col) {
		return TaskColumnList.getInstance().getAccessor(col);
	}
	
	@Override
	public int getColumnCount() {
		return TaskColumnList.getInstance().getSize();
	}
	
	@Override
	public int getRowCount() {
		return TaskFactory.getInstance().size();
	}
	
	@Override
	public String getColumnName(int col) {
		return this.getTaskColumn(col).getLabel();
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return this.getTaskColumn(col).getType().getType();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		Task task = TaskFactory.getInstance().get(row);
		return this.getTaskColumn(col).getProperty(task);
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return this.getTaskColumn(col).isEditable();
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		Task task = TaskFactory.getInstance().get(row);
		PropertyAccessor<Task> column = this.getTaskColumn(col);
		
		Object oldValue = column.getProperty(task);
		
		if (!EqualsUtils.equals(oldValue, value)) {
			column.setProperty(task, value);
			this.undoSupport.postEdit(new TaskEditUndoableEdit(
					task.getModelId(),
					column,
					value,
					oldValue));
		}
	}
	
	@Override
	public void listChange(ListChangeEvent event) {
		if (Synchronizing.getInstance().isSynchronizing())
			return;
		
		if (event.getChangeType() == ListChangeEvent.VALUE_ADDED) {
			this.fireTableRowsInserted(event.getIndex(), event.getIndex());
		} else if (event.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
			this.fireTableRowsDeleted(event.getIndex(), event.getIndex());
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() instanceof Synchronizing) {
			if (!(Boolean) event.getNewValue())
				TaskTableModel.this.fireTableDataChanged();
		}
		
		if (event.getSource() instanceof Task) {
			if (Synchronizing.getInstance().isSynchronizing())
				return;
			
			if (event.getPropertyName().equals(BasicModel.PROP_MODEL_STATUS)) {
				ModelStatus oldStatus = (ModelStatus) event.getOldValue();
				ModelStatus newStatus = (ModelStatus) event.getNewValue();
				
				if (oldStatus.isEndUserStatus() != newStatus.isEndUserStatus())
					this.fireTableDataChanged();
			} else if (event.getPropertyName().equals(
					GuiTask.PROP_SHOW_CHILDREN)
					|| event.getPropertyName().equals(ModelParent.PROP_PARENT)
					|| event.getPropertyName().equals(Model.PROP_ORDER)) {
				this.fireTableDataChanged();
			} else {
				int index = TaskFactory.getInstance().getIndexOf(
						(Task) event.getSource());
				this.fireTableRowsUpdated(index, index);
			}
		}
	}
	
}
