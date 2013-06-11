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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.leclercb.commons.gui.utils.TreeUtils;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.gui.api.searchers.filters.FilterLink;
import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilter;
import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilterElement;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.ModelCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.StringCondition;
import com.leclercb.taskunifier.gui.components.notes.NoteColumnList;
import com.leclercb.taskunifier.gui.components.notesearcheredit.filter.actions.NoteFilterActions;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.translations.TranslationsUtils;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class NoteFilterTree extends JTree implements NoteFilterActions {
	
	public NoteFilterTree() {
		this.initialize();
	}
	
	public NoteFilter getFilter() {
		return this.getNoteFilterTreeModel().getFilter();
	}
	
	public void setFilter(NoteFilter filter) {
		this.getNoteFilterTreeModel().setFilter(filter);
	}
	
	private NoteFilterTreeModel getNoteFilterTreeModel() {
		return (NoteFilterTreeModel) this.getModel();
	}
	
	private void initialize() {
		this.setModel(new NoteFilterTreeModel());
		this.setLargeModel(true);
		this.setRootVisible(true);
		
		this.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(ImageUtils.getResourceImage("tree_leaf.png"));
		renderer.setOpenIcon(ImageUtils.getResourceImage("tree_open.png"));
		renderer.setClosedIcon(ImageUtils.getResourceImage("tree_closed.png"));
		this.setCellRenderer(renderer);
		
		this.initializePopupMenu();
		
		for (int i = 0; i < this.getRowCount(); i++)
			this.expandRow(i);
	}
	
	private void initializePopupMenu() {
		this.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent event) {
				// Or BUTTON3 due to a bug with OSX
				if (event.isPopupTrigger()
						|| event.getButton() == MouseEvent.BUTTON3) {
					TreePath path = NoteFilterTree.this.getPathForLocation(
							event.getX(),
							event.getY());
					final TreeNode node = (TreeNode) path.getLastPathComponent();
					
					NoteFilterTree.this.setSelectionPath(path);
					
					JPopupMenu popup = new JPopupMenu();
					
					if (node instanceof NoteFilterTreeNode) {
						ButtonGroup group = new ButtonGroup();
						
						JRadioButtonMenuItem itemAnd = new JRadioButtonMenuItem(
								TranslationsUtils.translateFilterLink(FilterLink.AND));
						JRadioButtonMenuItem itemOr = new JRadioButtonMenuItem(
								TranslationsUtils.translateFilterLink(FilterLink.OR));
						
						group.add(itemAnd);
						group.add(itemOr);
						
						popup.add(itemAnd);
						popup.add(itemOr);
						
						if (((NoteFilterTreeNode) node).getFilter().getLink().equals(
								FilterLink.AND))
							itemAnd.setSelected(true);
						else
							itemOr.setSelected(true);
						
						itemAnd.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent evt) {
								((NoteFilterTreeNode) node).getFilter().setLink(
										FilterLink.AND);
							}
							
						});
						
						itemOr.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent evt) {
								((NoteFilterTreeNode) node).getFilter().setLink(
										FilterLink.OR);
							}
							
						});
					}
					
					JButton deleteButton = new JButton();
					deleteButton.setText(Translations.getString("general.delete"));
					deleteButton.setIcon(ImageUtils.getResourceImage(
							"remove.png",
							16,
							16));
					deleteButton.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent event) {
							
						}
						
					});
					
					popup.show(event.getComponent(), event.getX(), event.getY());
				}
			}
			
		});
	}
	
	@Override
	public void actionAutoFill() {
		this.getFilter().clearElement();
		this.getFilter().clearFilters();
		
		this.getFilter().setLink(FilterLink.OR);
		
		Note[] notes = ViewUtils.getSelectedNotes();
		for (Note note : notes) {
			this.getFilter().addElement(
					new NoteFilterElement(
							NoteColumnList.getInstance().get(
									NoteColumnList.MODEL),
							ModelCondition.EQUALS,
							note,
							false));
		}
	}
	
	@Override
	public void actionAddElement() {
		TreeNode node = (TreeNode) this.getLastSelectedPathComponent();
		
		if (node == null || !(node instanceof NoteFilterTreeNode))
			return;
		
		NoteFilterElement element = new NoteFilterElement(
				NoteColumnList.getInstance().get(NoteColumnList.TITLE),
				StringCondition.EQUALS,
				"",
				false);
		
		((NoteFilterTreeNode) node).getFilter().addElement(element);
		
		TreeUtils.expandAll(this, true);
	}
	
	@Override
	public void actionAddFilter() {
		TreeNode node = (TreeNode) this.getLastSelectedPathComponent();
		
		if (node == null || !(node instanceof NoteFilterTreeNode))
			return;
		
		((NoteFilterTreeNode) node).getFilter().addFilter(new NoteFilter());
		
		TreeUtils.expandAll(this, true);
	}
	
	@Override
	public void actionRemove() {
		TreeNode node = (TreeNode) this.getLastSelectedPathComponent();
		
		if (node == null)
			return;
		
		if (node instanceof NoteFilterTreeNode) {
			((NoteFilterTreeNode) node).getFilter().getParent().removeFilter(
					((NoteFilterTreeNode) node).getFilter());
		} else if (node instanceof NoteFilterElementTreeNode) {
			((NoteFilterElementTreeNode) node).getElement().getParent().removeElement(
					((NoteFilterElementTreeNode) node).getElement());
		}
	}
	
}
