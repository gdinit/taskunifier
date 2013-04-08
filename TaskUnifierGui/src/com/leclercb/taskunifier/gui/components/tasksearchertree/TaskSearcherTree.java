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
package com.leclercb.taskunifier.gui.components.tasksearchertree;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.tree.TreeNode;

import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.properties.events.WeakSavePropertiesListener;
import com.leclercb.commons.gui.utils.TreeUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.Tag;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.commons.events.TaskSearcherSelectionListener;
import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.components.tasksearchertree.draganddrop.TaskSearcherTransferHandler;
import com.leclercb.taskunifier.gui.components.tasksearchertree.nodes.ModelItem;
import com.leclercb.taskunifier.gui.components.tasksearchertree.nodes.SearcherCategory;
import com.leclercb.taskunifier.gui.components.tasksearchertree.nodes.TagItem;
import com.leclercb.taskunifier.gui.components.tasksearchertree.nodes.TaskSearcherProvider;
import com.leclercb.taskunifier.gui.main.Main;

public class TaskSearcherTree extends JTree implements TaskSearcherView, PropertyChangeListener, SavePropertiesListener {
	
	private String settingsPrefix;
	
	public TaskSearcherTree(String settingsPrefix) {
		this.settingsPrefix = settingsPrefix;
		this.initialize();
	}
	
	private void initialize() {
		Main.getSettings().addSavePropertiesListener(
				new WeakSavePropertiesListener(Main.getSettings(), this));
		
		this.setOpaque(false);
		this.setRootVisible(false);
		this.setLargeModel(true);
		this.setShowsRootHandles(true);
		this.setRowHeight(20);
		
		this.setSelectionModel(new TaskSearcherTreeSelectionModel());
		this.setModel(new TaskSearcherTreeModel(this.settingsPrefix, this));
		this.setUI(new TaskSearcherTreeUI());
		
		this.initializeToolTipText();
		this.initializeDragAndDrop();
		this.initializeCopyAndPaste();
		this.initializeExpandedState();
		
		Synchronizing.getInstance().addPropertyChangeListener(
				new WeakPropertyChangeListener(
						Synchronizing.getInstance(),
						this));
	}
	
	public TaskSearcherTreeModel getSearcherModel() {
		return (TaskSearcherTreeModel) this.getModel();
	}
	
	@Override
	public void addTaskSearcherSelectionChangeListener(
			TaskSearcherSelectionListener listener) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void removeTaskSearcherSelectionChangeListener(
			TaskSearcherSelectionListener listener) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Task[] getExtraTasks() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addExtraTasks(Task[] tasks) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setExtraTasks(Task[] tasks) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getSearchFilter() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setSearchFilter(String filter) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean selectTaskSearcher(TaskSearcher searcher) {
		TreeNode node = this.getSearcherModel().findItemFromSearcher(searcher);
		
		if (node == null)
			return false;
		
		this.setSelectionPath(TreeUtils.getPath(node));
		return true;
	}
	
	@Override
	public boolean selectModel(Model model) {
		TreeNode node = this.getSearcherModel().findItemFromModel(model);
		
		if (node == null)
			return false;
		
		this.setSelectionPath(TreeUtils.getPath(node));
		return true;
	}
	
	@Override
	public boolean selectTag(Tag tag) {
		TreeNode node = this.getSearcherModel().findItemFromTag(tag);
		
		if (node == null)
			return false;
		
		this.setSelectionPath(TreeUtils.getPath(node));
		return true;
	}
	
	@Override
	public void selectDefaultTaskSearcher() {
		TreeNode node = this.getSearcherModel().getDefaultSearcher();
		this.setSelectionPath(TreeUtils.getPath(node));
	}
	
	public Model getSelectedModel() {
		if (this.getSelectionPath() == null)
			return null;
		
		TreeNode node = (TreeNode) this.getSelectionPath().getLastPathComponent();
		
		if (node == null || !(node instanceof ModelItem))
			return null;
		
		return ((ModelItem) node).getModel();
	}
	
	@Override
	public TaskSearcher getSelectedTaskSearcher() {
		if (this.getSelectionPath() == null)
			return null;
		
		TreeNode node = (TreeNode) this.getSelectionPath().getLastPathComponent();
		
		if (node == null || !(node instanceof TaskSearcherProvider))
			return null;
		
		return ((TaskSearcherProvider) node).getTaskSearcher();
	}
	
	@Override
	public TaskSearcher getSelectedOriginalTaskSearcher() {
		return this.getSelectedTaskSearcher();
	}
	
	public Tag getSelectedTag() {
		if (this.getSelectionPath() == null)
			return null;
		
		TreeNode node = (TreeNode) this.getSelectionPath().getLastPathComponent();
		
		if (node == null || !(node instanceof TagItem))
			return null;
		
		return ((TagItem) node).getTag();
	}
	
	@Override
	public void refreshTaskSearcher() {
		this.updateBadges();
	}
	
	public void updateBadges() {
		this.getSearcherModel().updateBadges();
	}
	
	private void initializeDragAndDrop() {
		this.setDragEnabled(true);
		this.setTransferHandler(new TaskSearcherTransferHandler());
		this.setDropMode(DropMode.ON_OR_INSERT);
	}
	
	private void initializeToolTipText() {
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	private void initializeCopyAndPaste() {
		ActionMap amap = this.getActionMap();
		amap.put(
				TransferHandler.getCutAction().getValue(Action.NAME),
				TransferHandler.getCutAction());
		amap.put(
				TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());
		amap.put(
				TransferHandler.getPasteAction().getValue(Action.NAME),
				TransferHandler.getPasteAction());
		
		InputMap imap = this.getInputMap();
		imap.put(
				KeyStroke.getKeyStroke(
						KeyEvent.VK_X,
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				TransferHandler.getCutAction().getValue(Action.NAME));
		imap.put(
				KeyStroke.getKeyStroke(
						KeyEvent.VK_C,
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				TransferHandler.getCopyAction().getValue(Action.NAME));
		imap.put(
				KeyStroke.getKeyStroke(
						KeyEvent.VK_V,
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				TransferHandler.getPasteAction().getValue(Action.NAME));
	}
	
	private void initializeExpandedState() {
		TreeUtils.expandAll(this, true);
		
		Main.getSettings().addPropertyChangeListener(
				new WeakPropertyChangeListener(Main.getSettings(), this));
		
		this.updateExpandedState();
	}
	
	private void updateExpandedState() {
		SearcherCategory[] categories = this.getSearcherModel().getCategories();
		for (SearcherCategory category : categories) {
			if (category.getExpandedPropertyName() != null) {
				this.setExpandedState(
						TreeUtils.getPath(category),
						Main.getSettings().getBooleanProperty(
								category.getExpandedPropertyName(),
								false));
			}
		}
	}
	
	@Override
	public void saveProperties() {
		SearcherCategory[] categories = this.getSearcherModel().getCategories();
		for (SearcherCategory category : categories) {
			if (category.getExpandedPropertyName() != null) {
				Main.getSettings().setBooleanProperty(
						category.getExpandedPropertyName(),
						this.isExpanded(TreeUtils.getPath(category)));
			}
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Synchronizing.PROP_SYNCHRONIZING)) {
			if (!(Boolean) evt.getNewValue())
				this.updateBadges();
		}
		
		if (evt.getPropertyName().equals(this.settingsPrefix + ".category")) {
			this.updateExpandedState();
		}
	}
	
}
