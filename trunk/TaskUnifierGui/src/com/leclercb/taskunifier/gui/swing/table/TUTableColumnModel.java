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
package com.leclercb.taskunifier.gui.swing.table;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;

import javax.swing.table.TableColumn;

import org.jdesktop.swingx.table.DefaultTableColumnModelExt;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.CompareUtils;
import com.leclercb.taskunifier.gui.components.tasktasks.TaskTasksColumn;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties.TableColumnProperties;

public abstract class TUTableColumnModel<E extends Enum<?>> extends DefaultTableColumnModelExt {
	
	private TableColumnInstantiator<E> instantiator;
	private TUTableProperties<E> tableProperties;
	
	public TUTableColumnModel(
			TableColumnInstantiator<E> instantiator,
			TUTableProperties<E> tableProperties) {
		CheckUtils.isNotNull(instantiator);
		CheckUtils.isNotNull(tableProperties);
		
		this.instantiator = instantiator;
		this.tableProperties = tableProperties;
		
		this.initialize();
	}
	
	private void initialize() {
		E[] columns = this.tableProperties.getColumns();
		Arrays.sort(columns, new Comparator<E>() {
			
			@Override
			public int compare(E c1, E c2) {
				return CompareUtils.compare(
						TUTableColumnModel.this.tableProperties.get(c1).getOrder(),
						TUTableColumnModel.this.tableProperties.get(c2).getOrder());
			}
			
		});
		
		for (E column : columns)
			this.addColumn(this.instantiator.newTableColumnInstance(this.tableProperties.get(column)));
	}
	
	public TaskTasksColumn getTaskTasksColumn(int col) {
		return (TaskTasksColumn) this.getColumn(col).getIdentifier();
	}
	
	@Override
	public void moveColumn(int columnIndex, int newIndex) {
		super.moveColumn(columnIndex, newIndex);
		
		int i = 1;
		Enumeration<TableColumn> columns = this.getColumns();
		while (columns.hasMoreElements()) {
			@SuppressWarnings("unchecked")
			E column = (E) columns.nextElement().getIdentifier();
			TableColumnProperties<E> properties = this.tableProperties.get(column);
			properties.setOrder(i++);
		}
	}
	
	public static interface TableColumnInstantiator<E extends Enum<?>> {
		
		public abstract TUTableColumn<E> newTableColumnInstance(
				TableColumnProperties<E> column);
		
	}
	
}
