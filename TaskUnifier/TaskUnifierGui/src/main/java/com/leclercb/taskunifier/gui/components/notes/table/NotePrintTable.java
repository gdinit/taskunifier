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
package com.leclercb.taskunifier.gui.components.notes.table;

import javax.swing.JTable;

import org.jdesktop.swingx.JXTable;

import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.gui.components.notes.table.highlighters.NoteTitleHighlighter;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties;

public class NotePrintTable extends JXTable {
	
	public NotePrintTable(TUTableProperties<Note> tableProperties, Note[] notes) {
		this.initialize(tableProperties, notes);
	}
	
	private void initialize(
			final TUTableProperties<Note> tableProperties,
			final Note[] notes) {
		NotePrintTableColumnModel columnModel = new NotePrintTableColumnModel(
				tableProperties);
		NotePrintTableModel tableModel = new NotePrintTableModel(notes);
		
		this.setModel(tableModel);
		this.setColumnModel(columnModel);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setShowGrid(true, false);
		this.setColumnControlVisible(true);
		
		this.initializeHighlighters();
		
		this.packAll();
	}
	
	private void initializeHighlighters() {
		this.setHighlighters(new NoteTitleHighlighter());
	}
	
}
