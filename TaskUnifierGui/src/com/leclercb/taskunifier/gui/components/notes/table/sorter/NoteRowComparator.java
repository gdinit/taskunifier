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
package com.leclercb.taskunifier.gui.components.notes.table.sorter;

import java.util.Comparator;

import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcher;
import com.leclercb.taskunifier.gui.commons.comparators.NoteComparator;

public class NoteRowComparator implements Comparator<Note> {
	
	private static NoteRowComparator INSTANCE;
	
	public static NoteRowComparator getInstance() {
		if (INSTANCE == null)
			INSTANCE = new NoteRowComparator();
		
		return INSTANCE;
	}
	
	private NoteSearcher searcher;
	private NoteComparator comparator;
	
	public NoteRowComparator() {
		this.searcher = null;
		this.comparator = new NoteComparator();
	}
	
	public NoteSearcher getNoteSearcher() {
		return this.searcher;
	}
	
	public void setNoteSearcher(NoteSearcher searcher) {
		this.searcher = searcher;
		
		if (this.searcher == null)
			this.comparator.setNoteSorter(null);
		else
			this.comparator.setNoteSorter(this.searcher.getSorter());
	}
	
	@Override
	public int compare(Note note1, Note note2) {
		return this.comparator.compare(note1, note2);
	}
	
}