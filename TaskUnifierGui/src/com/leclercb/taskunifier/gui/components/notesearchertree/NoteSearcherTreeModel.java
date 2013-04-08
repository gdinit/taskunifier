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
package com.leclercb.taskunifier.gui.components.notesearchertree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.lang3.ArrayUtils;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.gui.utils.TreeUtils;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelParent;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.gui.api.models.GuiModel;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcher;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcherFactory;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcherType;
import com.leclercb.taskunifier.gui.commons.comparators.BasicModelComparator;
import com.leclercb.taskunifier.gui.commons.comparators.NoteSearcherComparator;
import com.leclercb.taskunifier.gui.components.notesearchertree.nodes.FolderItem;
import com.leclercb.taskunifier.gui.components.notesearchertree.nodes.SearcherCategory;
import com.leclercb.taskunifier.gui.components.notesearchertree.nodes.SearcherItem;
import com.leclercb.taskunifier.gui.components.notesearchertree.nodes.SearcherNode;
import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.constants.Constants;

public class NoteSearcherTreeModel extends DefaultTreeModel implements ListChangeListener, PropertyChangeListener {
	
	private JTree tree;
	
	private String settingsPrefix;
	
	private SearcherItem defaultSearcher;
	private SearcherCategory folderCategory;
	private SearcherCategory personalCategory;
	
	public NoteSearcherTreeModel(String settingsPrefix, JTree tree) {
		super(new SearcherCategory(NoteSearcherType.DEFAULT, null));
		
		this.settingsPrefix = settingsPrefix;
		
		this.tree = tree;
		
		this.initializeDefaultSearcher();
		this.initializeFolderCategory();
		this.initializePersonalCategory();
		
		NoteFactory.getInstance().addListChangeListener(
				new WeakListChangeListener(NoteFactory.getInstance(), this));
		NoteFactory.getInstance().addPropertyChangeListener(
				new WeakPropertyChangeListener(NoteFactory.getInstance(), this));
		
		NoteSearcherFactory.getInstance().addListChangeListener(
				new WeakListChangeListener(
						NoteSearcherFactory.getInstance(),
						this));
		NoteSearcherFactory.getInstance().addPropertyChangeListener(
				new WeakPropertyChangeListener(
						NoteSearcherFactory.getInstance(),
						this));
	}
	
	public SearcherItem getDefaultSearcher() {
		return this.defaultSearcher;
	}
	
	public SearcherCategory[] getCategories() {
		return new SearcherCategory[] {
				this.folderCategory,
				this.personalCategory };
	}
	
	private void initializeDefaultSearcher() {
		this.defaultSearcher = new SearcherItem(Constants.getMainNoteSearcher());
		((DefaultMutableTreeNode) this.getRoot()).add(this.defaultSearcher);
	}
	
	private void initializeFolderCategory() {
		this.folderCategory = new SearcherCategory(
				NoteSearcherType.FOLDER,
				this.settingsPrefix + ".category.folder.expanded");
		((DefaultMutableTreeNode) this.getRoot()).add(this.folderCategory);
		
		this.folderCategory.add(new FolderItem(null));
		
		List<Folder> folders = new ArrayList<Folder>(
				FolderFactory.getInstance().getList());
		Collections.sort(folders, BasicModelComparator.INSTANCE_NULL_LAST);
		
		for (Folder folder : folders) {
			if (folder.getModelStatus().isEndUserStatus()) {
				if (!folder.isSelfOrParentArchived()) {
					DefaultMutableTreeNode node = this.folderCategory;
					
					if (folder.getParent() != null)
						node = this.findItemFromFolder(folder.getParent());
					
					node.add(new FolderItem(folder));
				}
			}
		}
		
		FolderFactory.getInstance().addListChangeListener(
				new WeakListChangeListener(FolderFactory.getInstance(), this));
		FolderFactory.getInstance().addPropertyChangeListener(
				new WeakPropertyChangeListener(
						FolderFactory.getInstance(),
						this));
	}
	
	private void initializePersonalCategory() {
		this.personalCategory = new SearcherCategory(
				NoteSearcherType.PERSONAL,
				this.settingsPrefix + ".category.personal.expanded");
		((DefaultMutableTreeNode) this.getRoot()).add(this.personalCategory);
		
		List<NoteSearcher> searchers = new ArrayList<NoteSearcher>(
				NoteSearcherFactory.getInstance().getList());
		Collections.sort(searchers, NoteSearcherComparator.INSTANCE);
		
		for (NoteSearcher searcher : searchers) {
			if (searcher.getType() == NoteSearcherType.PERSONAL) {
				this.getCategoryFromNoteSearcherType(
						searcher.getType(),
						searcher.getFolders(),
						true);
			}
		}
		
		for (NoteSearcher searcher : searchers) {
			if (searcher.getType() == NoteSearcherType.PERSONAL) {
				SearcherCategory category = this.getCategoryFromNoteSearcherType(
						searcher.getType(),
						searcher.getFolders(),
						true);
				category.add(new SearcherItem(searcher));
			}
		}
	}
	
	public int findNewIndexInFolderCategory(TreeNode parent, Folder folder) {
		List<Folder> folders = new ArrayList<Folder>();
		for (int i = 0; i < parent.getChildCount(); i++) {
			TreeNode node = parent.getChildAt(i);
			if (node != null && node instanceof FolderItem) {
				folders.add(((FolderItem) node).getFolder());
			}
		}
		
		folders.add(folder);
		Collections.sort(folders, BasicModelComparator.INSTANCE_NULL_FIRST);
		
		return folders.indexOf(folder);
	}
	
	public FolderItem findItemFromFolder(Folder folder) {
		return this.findFolderItem(this.folderCategory, folder);
	}
	
	private FolderItem findFolderItem(TreeNode parent, Folder folder) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			TreeNode node = parent.getChildAt(i);
			if (node instanceof FolderItem) {
				if (EqualsUtils.equals(((FolderItem) node).getFolder(), folder)) {
					return (FolderItem) node;
				}
				
				FolderItem item = this.findFolderItem(node, folder);
				if (item != null)
					return item;
			}
		}
		
		return null;
	}
	
	public SearcherItem findItemFromSearcher(NoteSearcher searcher) {
		return this.findItemFromSearcher(
				searcher,
				searcher.getType(),
				searcher.getFolders());
	}
	
	private SearcherItem findItemFromSearcher(
			NoteSearcher searcher,
			NoteSearcherType type,
			String[] folders) {
		SearcherCategory category = this.getCategoryFromNoteSearcherType(
				type,
				folders,
				false);
		
		for (int i = 0; i < category.getChildCount(); i++) {
			TreeNode node = category.getChildAt(i);
			if (node instanceof SearcherItem) {
				if (EqualsUtils.equals(
						((SearcherItem) node).getNoteSearcher(),
						searcher)) {
					return (SearcherItem) node;
				}
			}
		}
		
		return null;
	}
	
	private SearcherCategory getCategoryFromNoteSearcherType(
			NoteSearcherType type,
			String[] folders,
			boolean create) {
		switch (type) {
			case DEFAULT:
				return (SearcherCategory) this.getRoot();
			case FOLDER:
				return this.folderCategory;
			case PERSONAL:
				return this.getCategoryFromFolder(
						this.personalCategory,
						folders,
						0,
						create);
		}
		
		return null;
	}
	
	private SearcherCategory getCategoryFromFolder(
			SearcherCategory category,
			String[] folders,
			int index,
			boolean create) {
		if (folders == null || folders.length == 0 || index >= folders.length)
			return category;
		
		for (int i = 0; i < category.getChildCount(); i++) {
			TreeNode node = category.getChildAt(i);
			if (node instanceof SearcherCategory) {
				if (((SearcherCategory) node).getFolder().equals(
						NoteSearcher.getFolder(ArrayUtils.subarray(
								folders,
								0,
								index + 1)))) {
					return this.getCategoryFromFolder(
							(SearcherCategory) node,
							folders,
							index + 1,
							create);
				}
			}
		}
		
		if (create) {
			SearcherCategory c = new SearcherCategory(
					category.getType(),
					NoteSearcher.getFolder(ArrayUtils.subarray(
							folders,
							0,
							index + 1)), null);
			
			category.insert(c, 0);
			
			return this.getCategoryFromFolder(c, folders, index + 1, create);
		}
		
		return category;
	}
	
	@Override
	public void listChange(ListChangeEvent event) {
		if (event.getValue() instanceof Note) {
			if (!Synchronizing.getInstance().isSynchronizing())
				this.updateBadges();
			return;
		}
		
		if (event.getValue() instanceof Folder) {
			Folder folder = (Folder) event.getValue();
			
			DefaultMutableTreeNode category = null;
			
			if (folder.getParent() != null) {
				category = this.findItemFromFolder(folder.getParent());
			}
			
			if (category == null) {
				category = this.folderCategory;
			}
			
			if (event.getChangeType() == ListChangeEvent.VALUE_ADDED) {
				if (!folder.getModelStatus().isEndUserStatus())
					return;
				
				if (folder.isSelfOrParentArchived())
					return;
				
				FolderItem item = new FolderItem(folder);
				
				try {
					this.insertNodeInto(
							item,
							category,
							this.findNewIndexInFolderCategory(category, folder));
				} catch (Exception e) {
					this.insertNodeInto(item, category, 0);
				}
			} else if (event.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
				FolderItem item = this.findItemFromFolder(folder);
				
				if (item != null)
					this.removeNodeFromParent(item);
			}
			
			this.updateSelection();
			return;
		}
		
		if (event.getValue() instanceof NoteSearcher) {
			NoteSearcher searcher = (NoteSearcher) event.getValue();
			SearcherCategory category = this.getCategoryFromNoteSearcherType(
					searcher.getType(),
					searcher.getFolders(),
					true);
			
			if (event.getChangeType() == ListChangeEvent.VALUE_ADDED) {
				SearcherItem item = new SearcherItem(searcher);
				
				this.insertNodeInto(item, category, category.getChildCount());
			} else if (event.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
				SearcherItem item = this.findItemFromSearcher(searcher);
				
				if (item != null)
					this.removeNodeFromParent(item);
			}
			
			this.updateSelection();
			return;
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() instanceof Note) {
			if (!Synchronizing.getInstance().isSynchronizing())
				this.updateBadges();
			return;
		}
		
		if (event.getSource() instanceof Folder) {
			Folder folder = (Folder) event.getSource();
			
			DefaultMutableTreeNode category = null;
			
			if (folder.getParent() != null) {
				category = this.findItemFromFolder(folder.getParent());
			}
			
			if (category == null) {
				category = this.folderCategory;
			}
			
			FolderItem item = this.findItemFromFolder(folder);
			
			if (!((Model) event.getSource()).getModelStatus().isEndUserStatus()) {
				if (item != null)
					this.removeNodeFromParent(item);
			} else if (folder.isSelfOrParentArchived()) {
				if (item != null)
					this.removeNodeFromParent(item);
			} else if (item == null) {
				item = new FolderItem(folder);
				
				try {
					this.insertNodeInto(
							item,
							category,
							this.findNewIndexInFolderCategory(category, folder));
				} catch (Exception e) {
					this.insertNodeInto(item, category, 0);
				}
				
				if (!folder.isSelfOrParentArchived()) {
					category = this.findItemFromFolder(folder.getParent());
					for (Folder child : folder.getAllChildren()) {
						category = this.findItemFromFolder(child.getParent());
						item = new FolderItem(child);
						
						try {
							this.insertNodeInto(
									item,
									category,
									this.findNewIndexInFolderCategory(
											category,
											child));
						} catch (Exception e) {
							this.insertNodeInto(item, category, 0);
						}
					}
				}
			} else if (event.getPropertyName().equals(BasicModel.PROP_TITLE)
					|| event.getPropertyName().equals(ModelParent.PROP_PARENT)) {
				this.removeNodeFromParent(item);
				
				try {
					this.insertNodeInto(
							item,
							category,
							this.findNewIndexInFolderCategory(category, folder));
				} catch (Exception e) {
					this.insertNodeInto(item, category, 0);
				}
			} else if (event.getPropertyName().equals(GuiModel.PROP_COLOR)) {
				this.nodeChanged(item);
			}
			
			this.updateSelection();
			return;
		}
		
		if (event.getSource() instanceof NoteSearcher) {
			NoteSearcher searcher = (NoteSearcher) event.getSource();
			
			if (event.getPropertyName().equals(NoteSearcher.PROP_TITLE)
					|| event.getPropertyName().equals(NoteSearcher.PROP_ICON)) {
				SearcherItem item = this.findItemFromSearcher(searcher);
				this.nodeChanged(item);
			}
			
			if (event.getPropertyName().equals(NoteSearcher.PROP_TYPE)
					|| event.getPropertyName().equals(NoteSearcher.PROP_FOLDER)) {
				SearcherItem item = null;
				
				if (event.getPropertyName().equals(NoteSearcher.PROP_TYPE))
					item = this.findItemFromSearcher(
							searcher,
							(NoteSearcherType) event.getOldValue(),
							searcher.getFolders());
				
				if (event.getPropertyName().equals(NoteSearcher.PROP_FOLDER))
					item = this.findItemFromSearcher(
							searcher,
							searcher.getType(),
							NoteSearcher.getFolders((String) event.getOldValue()));
				
				if (item != null) {
					this.removeNodeFromParent(item);
					this.cleanCategoryFolders();
				}
				
				SearcherCategory category = this.getCategoryFromNoteSearcherType(
						searcher.getType(),
						searcher.getFolders(),
						true);
				
				item = new SearcherItem(searcher);
				
				this.insertNodeInto(item, category, category.getChildCount());
				
				this.tree.setSelectionPath(TreeUtils.getPath(item));
			}
			
			this.updateSelection();
			return;
		}
	}
	
	private void cleanCategoryFolders() {
		// TODO
	}
	
	public void updateBadges() {
		this.defaultSearcher.updateBadgeCount();
		
		SearcherCategory[] categories = this.getCategories();
		for (SearcherCategory category : categories) {
			for (int i = 0; i < category.getChildCount(); i++) {
				if (category.getChildAt(i) instanceof SearcherNode)
					this.updateBadges((SearcherNode) category.getChildAt(i));
				
				if (category.getChildAt(i) instanceof SearcherCategory)
					this.updateBadges((SearcherCategory) category.getChildAt(i));
			}
		}
		
		this.nodeChanged((TreeNode) this.getRoot());
	}
	
	private void updateBadges(SearcherCategory category) {
		for (int i = 0; i < category.getChildCount(); i++) {
			if (category.getChildAt(i) instanceof SearcherNode)
				this.updateBadges((SearcherNode) category.getChildAt(i));
			
			if (category.getChildAt(i) instanceof SearcherCategory)
				this.updateBadges((SearcherCategory) category.getChildAt(i));
		}
	}
	
	private void updateBadges(SearcherNode node) {
		node.updateBadgeCount();
		
		for (int i = 0; i < node.getChildCount(); i++) {
			if (node.getChildAt(i) instanceof SearcherNode)
				this.updateBadges((SearcherNode) node.getChildAt(i));
		}
	}
	
	private void updateSelection() {
		if (this.tree.getSelectionModel().getSelectionPath() == null)
			this.tree.getSelectionModel().setSelectionPath(
					TreeUtils.getPath(this.getDefaultSearcher()));
	}
	
}
