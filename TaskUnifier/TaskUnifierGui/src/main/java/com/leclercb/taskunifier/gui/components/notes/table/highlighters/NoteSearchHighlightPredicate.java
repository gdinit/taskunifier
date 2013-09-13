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
package com.leclercb.taskunifier.gui.components.notes.table.highlighters;

import java.awt.Component;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.renderer.JRendererLabel;

import com.leclercb.commons.api.utils.StringUtils;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.commons.highlighters.SearchHighlightPredicate;
import com.leclercb.taskunifier.gui.components.notes.NoteColumnList;

public class NoteSearchHighlightPredicate implements SearchHighlightPredicate {
	
	private String searchText;
	
	public NoteSearchHighlightPredicate() {
		
	}
	
	@Override
	public String getSearchText() {
		return this.searchText;
	}
	
	@Override
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	
	@Override
	public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
		if (this.searchText == null || this.searchText.length() == 0)
			return false;
		
		PropertyAccessor<Note> column = (PropertyAccessor<Note>) adapter.getColumnIdentifierAt(adapter.convertColumnIndexToModel(adapter.column));
		
		if (!column.equals(NoteColumnList.getInstance().get(
				NoteColumnList.TITLE))
				&& !column.equals(NoteColumnList.getInstance().get(
						NoteColumnList.NOTE))
				&& !column.equals(NoteColumnList.getInstance().get(
						NoteColumnList.FOLDER)))
			return false;
		
		if (renderer instanceof JRendererLabel) {
			JRendererLabel r = (JRendererLabel) renderer;
			return StringUtils.containsLocalized(r.getText(), this.searchText);
		}
		
		return false;
	}
	
}
