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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.tree.TreeNode;

import com.leclercb.commons.gui.utils.TreeUtils;
import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilter;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class NoteFilterPanel extends JPanel {
	
	private NoteFilter filter;
	private NoteFilterTree tree;
	
	private JButton autoFillButton;
	private JButton addElementButton;
	private JButton addFilterButton;
	private JButton removeButton;
	
	public NoteFilterPanel() {
		this.initialize();
	}
	
	public NoteFilter getFilter() {
		return this.tree.getFilter();
	}
	
	public void setFilter(NoteFilter filter) {
		this.filter = filter;
		this.tree.setFilter(filter);
		
		if (this.tree.getModel().getRoot() != null)
			this.tree.setSelectionPath(TreeUtils.getPath((TreeNode) this.tree.getModel().getRoot()));
		
		this.autoFillButton.setEnabled(filter != null);
		this.addElementButton.setEnabled(filter != null);
		this.addFilterButton.setEnabled(filter != null);
		this.removeButton.setEnabled(filter != null);
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
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("AUTO_FILL")) {
					NoteFilterPanel.this.tree.actionAutoFill();
				} else if (event.getActionCommand().equals("ADD_ELEMENT")) {
					NoteFilterPanel.this.tree.actionAddElement();
				} else if (event.getActionCommand().equals("ADD_FILTER")) {
					NoteFilterPanel.this.tree.actionAddFilter();
				} else if (event.getActionCommand().equals("REMOVE")) {
					NoteFilterPanel.this.tree.actionRemove();
				}
			}
			
		};
		
		this.addElementButton = new JButton(
				Translations.getString("searcheredit.add_element"),
				ImageUtils.getResourceImage("add.png", 16, 16));
		this.addElementButton.setActionCommand("ADD_ELEMENT");
		this.addElementButton.addActionListener(listener);
		this.addElementButton.setEnabled(false);
		
		this.addFilterButton = new JButton(
				Translations.getString("searcheredit.add_filter"),
				ImageUtils.getResourceImage("add.png", 16, 16));
		this.addFilterButton.setActionCommand("ADD_FILTER");
		this.addFilterButton.addActionListener(listener);
		this.addFilterButton.setEnabled(false);
		
		this.removeButton = new JButton(ImageUtils.getResourceImage(
				"remove.png",
				16,
				16));
		this.removeButton.setActionCommand("REMOVE");
		this.removeButton.addActionListener(listener);
		this.removeButton.setEnabled(false);
		
		this.autoFillButton = new JButton(
				Translations.getString("searcheredit.clear_and_auto_fill_with_selected_notes"),
				ImageUtils.getResourceImage("synchronize.png", 16, 16));
		this.autoFillButton.setActionCommand("AUTO_FILL");
		this.autoFillButton.addActionListener(listener);
		this.autoFillButton.setEnabled(true);
		
		// Do not show the auto fill button
		this.autoFillButton.setVisible(false);
		
		JPanel panel = new TUButtonsPanel(
				this.addElementButton,
				this.addFilterButton,
				this.removeButton,
				this.autoFillButton);
		
		this.add(panel, BorderLayout.SOUTH);
	}
	
}
