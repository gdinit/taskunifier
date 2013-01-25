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
package com.leclercb.taskunifier.gui.commons.comparators;

import java.util.Comparator;
import java.util.List;

import javax.swing.SortOrder;

import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.searchers.sorters.NoteSorter;
import com.leclercb.taskunifier.gui.api.searchers.sorters.NoteSorterElement;
import com.leclercb.taskunifier.gui.components.notes.NoteColumnList;

public class NoteComparator implements Comparator<Note> {
	
	private NoteSorter sorter;
	
	public NoteComparator() {
		this.sorter = null;
	}
	
	public NoteSorter getNoteSorter() {
		return this.sorter;
	}
	
	public void setNoteSorter(NoteSorter sorter) {
		this.sorter = sorter;
	}
	
	@Override
	public int compare(Note note1, Note note2) {
		if (this.sorter == null)
			return 0;
		
		List<NoteSorterElement> elements = this.sorter.getElements();
		
		for (NoteSorterElement element : elements) {
			Object o1 = element.getProperty().getProperty(note1);
			Object o2 = element.getProperty().getProperty(note2);
			
			int result = this.compare(
					element.getProperty(),
					o1,
					o2,
					element.getSortOrder());
			
			if (result != 0)
				return result;
		}
		
		Object o1 = NoteColumnList.getInstance().get(
				NoteColumnList.MODEL_CREATION_DATE).getProperty(note1);
		Object o2 = NoteColumnList.getInstance().get(
				NoteColumnList.MODEL_CREATION_DATE).getProperty(note2);
		
		int result = this.compare(
				NoteColumnList.getInstance().get(
						NoteColumnList.MODEL_CREATION_DATE),
				o1,
				o2,
				SortOrder.ASCENDING);
		
		if (result != 0)
			return result;
		
		return this.compare(
				NoteColumnList.getInstance().get(NoteColumnList.MODEL),
				note1,
				note2,
				SortOrder.ASCENDING);
	}
	
	private int compare(
			PropertyAccessor<Note> column,
			Object o1,
			Object o2,
			SortOrder sortOrder) {
		if (o1 == null && o2 == null)
			return 0;
		
		if (o1 == null)
			return 1;
		
		if (o2 == null)
			return -1;
		
		int result = column.getType().compare(o1, o2);
		
		return (sortOrder.equals(SortOrder.ASCENDING) ? 1 : -1) * result;
	}
	
}
