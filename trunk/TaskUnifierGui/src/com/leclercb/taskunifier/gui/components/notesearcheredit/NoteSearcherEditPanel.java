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
package com.leclercb.taskunifier.gui.components.notesearcheredit;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcher;
import com.leclercb.taskunifier.gui.components.notesearcheredit.filter.NoteFilterEditPanel;
import com.leclercb.taskunifier.gui.components.notesearcheredit.grouper.NoteGrouperPanel;
import com.leclercb.taskunifier.gui.components.notesearcheredit.searcher.NoteSearcherPanel;
import com.leclercb.taskunifier.gui.components.notesearcheredit.sorter.NoteSorterPanel;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;

public class NoteSearcherEditPanel extends JPanel {
	
	private NoteSearcher searcher;
	
	private NoteSearcherPanel searcherPanel;
	private NoteGrouperPanel grouperPanel;
	private NoteSorterPanel sorterPanel;
	private NoteFilterEditPanel filterEditPanel;
	
	public NoteSearcherEditPanel(NoteSearcher searcher, boolean showInfoPanel) {
		CheckUtils.isNotNull(searcher);
		this.searcher = searcher;
		
		this.initialize(showInfoPanel);
	}
	
	private void initialize(boolean showInfoPanel) {
		this.setLayout(new BorderLayout());
		
		this.searcherPanel = new NoteSearcherPanel(this.searcher);
		this.grouperPanel = new NoteGrouperPanel(this.searcher.getGrouper());
		this.sorterPanel = new NoteSorterPanel(this.searcher.getSorter());
		this.filterEditPanel = new NoteFilterEditPanel();
		this.filterEditPanel.setFilter(this.searcher.getFilter());
		
		this.searcherPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.grouperPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.sorterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.filterEditPanel.setBorder(BorderFactory.createEmptyBorder(
				5,
				5,
				5,
				5));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		if (showInfoPanel) {
			tabbedPane.addTab(
					Translations.getString("searcheredit.tab.general"),
					this.searcherPanel);
		}
		
		// TODO: PRO
		if (Main.isTmpProVersion()) {
			tabbedPane.addTab(
					Translations.getString("searcheredit.tab.grouper"),
					this.grouperPanel);
		}
		
		tabbedPane.addTab(
				Translations.getString("searcheredit.tab.sorter"),
				this.sorterPanel);
		
		tabbedPane.addTab(
				Translations.getString("searcheredit.tab.filter"),
				this.filterEditPanel);
		
		this.add(tabbedPane, BorderLayout.CENTER);
	}
	
	public void close() {
		this.searcherPanel.saveChanges();
		this.filterEditPanel.saveChanges();
	}
	
}
