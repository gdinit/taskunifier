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
import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilter;

public class NoteFilterEditPanel extends JPanel implements TreeSelectionListener {
	
	private NoteFilterLinkPanel filterLinkPanel;
	private NoteFilterElementPanel elementPanel;
	private NoteFilterPanel filterPanel;
	
	private JPanel southPanel;
	
	public NoteFilterEditPanel() {
		this.initialize();
	}
	
	public NoteFilter getFilter() {
		return this.filterPanel.getFilter();
	}
	
	public void setFilter(NoteFilter filter) {
		this.filterPanel.setFilter(filter);
	}
	
	public boolean isAllowCompareModel() {
		return this.elementPanel.isAllowCompareModel();
	}
	
	public void setAllowCompareModel(boolean allowCompareModel) {
		this.elementPanel.setAllowCompareModel(allowCompareModel);
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout(5, 5));
		
		this.southPanel = new JPanel(new CardLayout());
		
		this.elementPanel = new NoteFilterElementPanel();
		this.southPanel.add(this.elementPanel, "ELEMENT");
		
		this.filterLinkPanel = new NoteFilterLinkPanel();
		this.southPanel.add(this.filterLinkPanel, "LINK");
		
		this.filterPanel = new NoteFilterPanel();
		this.filterPanel.getTree().addTreeSelectionListener(this);
		
		this.add(this.filterPanel, BorderLayout.CENTER);
		this.add(this.southPanel, BorderLayout.SOUTH);
		
		((CardLayout) this.southPanel.getLayout()).show(
				this.southPanel,
				"ELEMENT");
	}
	
	public void saveChanges() {
		this.elementPanel.saveElement();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent evt) {
		this.elementPanel.saveElement();
		
		if (this.filterPanel.getTree().getSelectionCount() != 0) {
			TreeNode node = (TreeNode) this.filterPanel.getTree().getLastSelectedPathComponent();
			
			if (node instanceof NoteFilterElementTreeNode) {
				((CardLayout) this.southPanel.getLayout()).show(
						this.southPanel,
						"ELEMENT");
				this.elementPanel.setElement(((NoteFilterElementTreeNode) node).getElement());
				return;
			}
			
			if (node instanceof NoteFilterTreeNode) {
				((CardLayout) this.southPanel.getLayout()).show(
						this.southPanel,
						"LINK");
				this.filterLinkPanel.setFilter(((NoteFilterTreeNode) node).getFilter());
				return;
			}
		}
		
		this.elementPanel.setElement(null);
	}
	
}
