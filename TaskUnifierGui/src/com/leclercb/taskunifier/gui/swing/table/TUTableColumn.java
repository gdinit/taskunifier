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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.table.TableColumnExt;

import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties.TableColumnProperties;

public class TUTableColumn<T> extends TableColumnExt implements PropertyChangeListener {
	
	protected TableColumnProperties<T> column;
	
	public TUTableColumn(TableColumnProperties<T> column) {
		super(column.getTableProperties().getColumns().indexOf(
				column.getColumn()));
		
		CheckUtils.isNotNull(column);
		this.column = column;
		
		this.setIdentifier(column.getColumn());
		this.setHeaderValue(column.getColumn().getLabel());
		
		this.setPreferredWidth(column.getWidth());
		this.setVisible(column.isVisible());
		
		this.column.addPropertyChangeListener(new WeakPropertyChangeListener(
				this.column,
				this));
	}
	
	@Override
	public Comparator<?> getComparator() {
		return this.column.getColumn().getComparator();
	}
	
	@Override
	public boolean isSortable() {
		return this.column.getColumn().isSortable();
	}
	
	@Override
	public TableCellRenderer getCellRenderer() {
		return this.column.getColumn().getCellRenderer();
	}
	
	@Override
	public TableCellEditor getCellEditor() {
		return this.column.getColumn().getCellEditor();
	}
	
	@Override
	public void setPreferredWidth(int preferredWidth) {
		this.column.setWidth(preferredWidth);
		super.setPreferredWidth(preferredWidth);
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.column.setVisible(visible);
		super.setVisible(visible);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(TableColumnProperties.PROP_VISIBLE)) {
			this.setVisible((Boolean) evt.getNewValue());
		}
		
		if (evt.getPropertyName().equals(TableColumnProperties.PROP_WIDTH)) {
			this.setPreferredWidth((Integer) evt.getNewValue());
		}
	}
	
}
