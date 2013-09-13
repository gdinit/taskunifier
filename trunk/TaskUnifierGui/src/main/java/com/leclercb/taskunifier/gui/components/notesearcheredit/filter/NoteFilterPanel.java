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
package com.leclercb.taskunifier.gui.components.notesearcheredit.filter;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.tree.TreeNode;

import com.leclercb.commons.gui.utils.TreeUtils;
import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilter;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;

public class NoteFilterPanel extends JPanel {
	
	private NoteFilterTree tree;
	
	public NoteFilterPanel() {
		this.initialize();
	}
	
	public NoteFilter getFilter() {
		return this.tree.getFilter();
	}
	
	public void setFilter(NoteFilter filter) {
		this.tree.setFilter(filter);
		
		if (this.tree.getModel().getRoot() != null)
			this.tree.setSelectionPath(TreeUtils.getPath((TreeNode) this.tree.getModel().getRoot()));
	}
	
	public NoteFilterTree getTree() {
		return this.tree;
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		this.tree = new NoteFilterTree();
		
		this.add(
				ComponentFactory.createJScrollPane(this.tree, true),
				BorderLayout.CENTER);
		
		this.initializeButtons();
	}
	
	private void initializeButtons() {
		JButton addElementButton = new JButton(this.tree.getAddElementAction());
		JButton addFilterButton = new JButton(this.tree.getAddFilterAction());
		JButton removeButton = new JButton(this.tree.getRemoveAction());
		JButton autoFillButton = new JButton(this.tree.getAutoFillAction());
		
		// Do not show the auto fill button
		autoFillButton.setVisible(false);
		
		JPanel panel = new TUButtonsPanel(
				addElementButton,
				addFilterButton,
				removeButton,
				autoFillButton);
		
		this.add(panel, BorderLayout.SOUTH);
	}
	
}
