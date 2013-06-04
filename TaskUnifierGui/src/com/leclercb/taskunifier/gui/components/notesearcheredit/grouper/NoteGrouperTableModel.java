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
package com.leclercb.taskunifier.gui.components.notesearcheredit.grouper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.DefaultTableModel;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.searchers.groupers.NoteGrouper;
import com.leclercb.taskunifier.gui.api.searchers.groupers.NoteGrouperElement;
import com.leclercb.taskunifier.gui.translations.Translations;

public class NoteGrouperTableModel extends DefaultTableModel implements ListChangeListener, PropertyChangeListener {
	
	private NoteGrouper grouper;
	
	public NoteGrouperTableModel(NoteGrouper grouper) {
		this.grouper = grouper;
		this.grouper.addListChangeListener(new WeakListChangeListener(
				this.grouper,
				this));
	}
	
	public NoteGrouper getNoteGrouper() {
		return this.grouper;
	}
	
	public NoteGrouperElement getNoteGrouperElement(int row) {
		return this.grouper.getElement(row);
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public int getRowCount() {
		if (this.grouper == null)
			return 0;
		
		return this.grouper.getElementCount();
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0:
				return Translations.getString("grouper.order");
			case 1:
				return Translations.getString("grouper.column");
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
				return this.grouper.getElement(row).getProperty();
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
				this.grouper.getElement(row).setProperty(
						(PropertyAccessor<Note>) value);
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
