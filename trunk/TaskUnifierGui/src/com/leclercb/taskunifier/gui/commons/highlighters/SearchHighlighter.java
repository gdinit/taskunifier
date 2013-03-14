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
package com.leclercb.taskunifier.gui.commons.highlighters;

import java.awt.Component;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.renderer.JRendererLabel;

import com.leclercb.taskunifier.gui.commons.painters.SearchPainter;
import com.leclercb.taskunifier.gui.components.tasks.table.highlighters.TaskSearchHighlightPredicate;
import com.leclercb.taskunifier.gui.swing.TUModelListLabel;

public class SearchHighlighter extends AbstractHighlighter {
	
	private SearchPainter painter;
	
	public SearchHighlighter(HighlightPredicate predicate) {
		super(predicate);
		
		this.painter = new SearchPainter();
	}
	
	public String getSearchText() {
		return ((TaskSearchHighlightPredicate) this.getHighlightPredicate()).getSearchText();
	}
	
	public void setSearchText(String searchText) {
		((TaskSearchHighlightPredicate) this.getHighlightPredicate()).setSearchText(searchText);
	}
	
	@Override
	protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
		if (adapter.isSelected())
			return renderer;
		
		if (renderer instanceof JRendererLabel) {
			JRendererLabel r = (JRendererLabel) renderer;
			this.painter.setSearchText(this.getSearchText());
			r.setPainter(this.painter);
		}
		
		if (renderer instanceof TUModelListLabel) {
			TUModelListLabel r = (TUModelListLabel) renderer;
			r.highlightSearchText(this.getSearchText());
		}
		
		return renderer;
	}
	
}
