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
package com.leclercb.taskunifier.gui.components.taskedit.propertytable;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXTable;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;

public class TaskPropertyTable extends JXTable {
	
	public TaskPropertyTable() {
		this.initialize();
	}
	
	public Task getTask() {
		return this.getTaskPropertyTableModel().getTask();
	}
	
	public void setTask(Task task) {
		this.getTaskPropertyTableModel().setTask(task);
	}
	
	public TaskPropertyTableModel getTaskPropertyTableModel() {
		return (TaskPropertyTableModel) this.getModel();
	}
	
	private void initialize() {
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		TaskPropertyTableModel tableModel = new TaskPropertyTableModel();
		
		this.setModel(tableModel);
		this.setRowHeight(24);
		this.getTableHeader().setReorderingAllowed(false);
		
		this.setSortable(false);
		this.setColumnControlVisible(false);
	}
	
	@Override
	public TableCellEditor getCellEditor(int row, int col) {
		switch (col) {
			case 0:
				return PropertyAccessorType.BOOLEAN.getCellEditor();
			case 1:
				return PropertyAccessorType.STRING.getCellEditor();
			case 2:
				return this.getTaskPropertyTableModel().getTaskPropertyItem(row).getAccessor().getCellEditor();
			default:
				return super.getCellEditor(row, col);
		}
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int col) {
		switch (col) {
			case 0:
				return PropertyAccessorType.BOOLEAN.getCellRenderer();
			case 1:
				return PropertyAccessorType.STRING.getCellRenderer();
			case 2:
				return this.getTaskPropertyTableModel().getTaskPropertyItem(row).getAccessor().getCellRenderer();
			default:
				return super.getCellRenderer(row, col);
		}
	}
	
}
