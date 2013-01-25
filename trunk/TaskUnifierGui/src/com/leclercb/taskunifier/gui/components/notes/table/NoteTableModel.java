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
package com.leclercb.taskunifier.gui.components.notes.table;

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
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.commons.undoableedit.NoteEditUndoableEdit;
import com.leclercb.taskunifier.gui.components.notes.NoteColumnList;
import com.leclercb.taskunifier.gui.utils.UndoSupport;

public class NoteTableModel extends AbstractTableModel implements ListChangeListener, PropertyChangeListener {
	
	private UndoSupport undoSupport;
	
	public NoteTableModel(UndoSupport undoSupport) {
		CheckUtils.isNotNull(undoSupport);
		this.undoSupport = undoSupport;
		
		NoteFactory.getInstance().addListChangeListener(
				new WeakListChangeListener(NoteFactory.getInstance(), this));
		NoteFactory.getInstance().addPropertyChangeListener(
				new WeakPropertyChangeListener(NoteFactory.getInstance(), this));
	}
	
	public Note getNote(int row) {
		return NoteFactory.getInstance().get(row);
	}
	
	public PropertyAccessor<Note> getNoteColumn(int col) {
		return NoteColumnList.getInstance().getAccessor(col);
	}
	
	@Override
	public int getColumnCount() {
		return NoteColumnList.getInstance().getSize();
	}
	
	@Override
	public int getRowCount() {
		return NoteFactory.getInstance().size();
	}
	
	@Override
	public String getColumnName(int col) {
		return this.getNoteColumn(col).getLabel();
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return this.getNoteColumn(col).getType().getType();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		Note note = NoteFactory.getInstance().get(row);
		return this.getNoteColumn(col).getProperty(note);
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return this.getNoteColumn(col).isEditable();
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		Note note = NoteFactory.getInstance().get(row);
		PropertyAccessor<Note> column = this.getNoteColumn(col);
		
		Object oldValue = column.getProperty(note);
		
		if (!EqualsUtils.equals(oldValue, value)) {
			column.setProperty(note, value);
			this.undoSupport.postEdit(new NoteEditUndoableEdit(
					note.getModelId(),
					column,
					value,
					oldValue));
		}
	}
	
	@Override
	public void listChange(ListChangeEvent event) {
		if (event.getChangeType() == ListChangeEvent.VALUE_ADDED) {
			this.fireTableRowsInserted(event.getIndex(), event.getIndex());
		} else if (event.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
			this.fireTableRowsDeleted(event.getIndex(), event.getIndex());
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(BasicModel.PROP_MODEL_STATUS)) {
			ModelStatus oldStatus = (ModelStatus) event.getOldValue();
			ModelStatus newStatus = (ModelStatus) event.getNewValue();
			
			if (oldStatus.isEndUserStatus() != newStatus.isEndUserStatus())
				this.fireTableDataChanged();
		} else {
			int index = NoteFactory.getInstance().getIndexOf(
					(Note) event.getSource());
			this.fireTableRowsUpdated(index, index);
		}
	}
	
}
