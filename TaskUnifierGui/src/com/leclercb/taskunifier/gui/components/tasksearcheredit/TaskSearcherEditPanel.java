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
package com.leclercb.taskunifier.gui.components.tasksearcheredit;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.components.tasksearcheredit.filter.TaskFilterEditPanel;
import com.leclercb.taskunifier.gui.components.tasksearcheredit.searcher.TaskSearcherPanel;
import com.leclercb.taskunifier.gui.components.tasksearcheredit.sorter.TaskSorterPanel;
import com.leclercb.taskunifier.gui.translations.Translations;

public class TaskSearcherEditPanel extends JPanel {
	
	private TaskSearcher searcher;
	
	private TaskSearcherPanel searcherPanel;
	private TaskSorterPanel sorterPanel;
	private TaskFilterEditPanel filterEditPanel;
	
	public TaskSearcherEditPanel(TaskSearcher searcher, boolean showInfoPanel) {
		CheckUtils.isNotNull(searcher);
		this.searcher = searcher;
		
		this.initialize(showInfoPanel);
	}
	
	private void initialize(boolean showInfoPanel) {
		this.setLayout(new BorderLayout());
		
		this.searcherPanel = new TaskSearcherPanel(this.searcher);
		this.sorterPanel = new TaskSorterPanel(this.searcher.getSorter());
		this.filterEditPanel = new TaskFilterEditPanel();
		this.filterEditPanel.setFilter(this.searcher.getFilter());
		
		this.searcherPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
