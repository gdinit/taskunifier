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
package com.leclercb.taskunifier.gui.components.tasksearcheredit.sorter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.searchers.sorters.TaskSorter;
import com.leclercb.taskunifier.gui.api.searchers.sorters.TaskSorterElement;
import com.leclercb.taskunifier.gui.translations.Translations;

public class TaskSorterTableModel extends DefaultTableModel implements ListChangeListener, PropertyChangeListener {
	
	private TaskSorter sorter;
	
	public TaskSorterTableModel(TaskSorter sorter) {
		this.sorter = sorter;
		this.sorter.addListChangeListener(new WeakListChangeListener(
				this.sorter,
				this));
		this.sorter.addPropertyChangeListener(new WeakPropertyChangeListener(
				this.sorter,
				this));
	}
	
	public TaskSorter getTaskSorter() {
		return this.sorter;
	}
	
	public TaskSorterElement getTaskSorterElement(int row) {
		return this.sorter.getElement(row);
	}
	
	@Override
	public int getColumnCount() {
		return 3;
	}
	
	@Override
	public int getRowCount() {
		if (this.sorter == null)
			return 0;
		
		return this.sorter.getElementCount();
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0:
				return Translations.getString("sorter.order");
			case 1:
				return Translations.getString("sorter.column");
			case 2:
				return Translations.getString("sorter.sort_order");
			default:
				return null;
		}
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		switch (col) {
			case 0:
				return Integer.class;
			case 1:
				return PropertyAccessor.class;
			case 2:
				return SortOrder.class;
			default:
				return null;
		}
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		switch (col) {
			case 0:
				return row + 1;
			case 1:
				return this.sorter.getElement(row).getProperty();
			case 2:
				return this.sorter.getElement(row).getSortOrder();
			default:
				return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 0)
			return false;
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValueAt(Object value, int row, int col) {
		switch (col) {
			case 0:
				break;
			case 1:
				this.sorter.getElement(row).setProperty(
						(PropertyAccessor<Task>) value);
				break;
			case 2:
				this.sorter.getElement(row).setSortOrder((SortOrder) value);
				break;
		}
	}
	
	@Override
	public void listChange(ListChangeEvent event) {
		this.fireTableDataChanged();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		this.fireTableDataChanged();
	}
	
}
