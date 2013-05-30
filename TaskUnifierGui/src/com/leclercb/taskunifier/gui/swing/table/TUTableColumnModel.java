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
package com.leclercb.taskunifier.gui.swing.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.table.TableColumn;

import org.jdesktop.swingx.table.DefaultTableColumnModelExt;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.CompareUtils;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties.TableColumnProperties;

public class TUTableColumnModel<T> extends DefaultTableColumnModelExt {
	
	private TableColumnInstantiator<T> instantiator;
	private TUTableProperties<T> tableProperties;
	
	public TUTableColumnModel(TUTableProperties<T> tableProperties) {
		this(null, tableProperties);
	}
	
	public TUTableColumnModel(
			TableColumnInstantiator<T> instantiator,
			TUTableProperties<T> tableProperties) {
		CheckUtils.isNotNull(tableProperties);
		
		this.instantiator = instantiator;
		this.tableProperties = tableProperties;
		
		this.initialize();
	}
	
	private void initialize() {
		List<PropertyAccessor<T>> columns = this.tableProperties.getColumns().getAccessors();
		Collections.sort(columns, new Comparator<PropertyAccessor<T>>() {
			
			@Override
			public int compare(PropertyAccessor<T> c1, PropertyAccessor<T> c2) {
				return CompareUtils.compare(
						TUTableColumnModel.this.tableProperties.get(c1).getOrder(),
						TUTableColumnModel.this.tableProperties.get(c2).getOrder());
			}
			
		});
		
		for (PropertyAccessor<T> column : columns) {
			if (this.instantiator != null)
				this.addColumn(this.instantiator.newTableColumnInstance(this.tableProperties.get(column)));
			else
				this.addColumn(new TUTableColumn<T>(
						this.tableProperties.get(column)));
		}
	}
	
	@Override
	public void moveColumn(int columnIndex, int newIndex) {
		super.moveColumn(columnIndex, newIndex);
		
		int i = 1;
		Enumeration<TableColumn> columns = this.getColumns();
		while (columns.hasMoreElements()) {
			@SuppressWarnings("unchecked")
			PropertyAccessor<T> column = (PropertyAccessor<T>) columns.nextElement().getIdentifier();
			TableColumnProperties<T> properties = this.tableProperties.get(column);
			properties.setOrder(i++);
		}
	}
	
	public static interface TableColumnInstantiator<T> {
		
		public abstract TUTableColumn<T> newTableColumnInstance(
				TableColumnProperties<T> column);
		
	}
	
}
