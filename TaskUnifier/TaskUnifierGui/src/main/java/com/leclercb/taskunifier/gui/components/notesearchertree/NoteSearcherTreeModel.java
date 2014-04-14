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

package com.leclercb.taskunifier.gui.components.notesearchertree;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.DateUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.gui.utils.TreeUtils;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.gui.api.models.GuiModel;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcher;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcherFactory;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcherType;
import com.leclercb.taskunifier.gui.commons.comparators.BasicModelComparator;
import com.leclercb.taskunifier.gui.commons.comparators.NoteSearcherComparator;
import com.leclercb.taskunifier.gui.commons.comparators.NoteSearcherFolderComparator;
import com.leclercb.taskunifier.gui.components.notesearchertree.nodes.FolderItem;
import com.leclercb.taskunifier.gui.components.notesearchertree.nodes.SearcherCategory;
import com.leclercb.taskunifier.gui.components.notesearchertree.nodes.SearcherItem;
import com.leclercb.taskunifier.gui.components.notesearchertree.nodes.SearcherNode;
import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.processes.ProcessUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class NoteSearcherTreeModel extends DefaultTreeModel implements ListChangeListener, PropertyChangeListener {

    private NoteSearcherTree tree;

    private String settingsPrefix;

    private SearcherItem defaultSearcher;
    private SearcherCategory folderCategory;
    private SearcherCategory personalCategory;

    private Calendar updateBadgesNeeded;

    public NoteSearcherTreeModel(String settingsPrefix, NoteSearcherTree tree) {
        super(new SearcherCategory(NoteSearcherType.DEFAULT, null));

        this.settingsPrefix = settingsPrefix;

        this.tree = tree;

        this.updateBadgesNeeded = null;

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

        new UpdateBadgesThread().start();
    }

    public SearcherItem getDefaultSearcher() {
        return this.defaultSearcher;
    }

    public SearcherCategory[] getCategories() {
        return new SearcherCategory[]{
                this.folderCategory,
                this.personalCategory};
    }

    private void initializeDefaultSearcher() {
        this.defaultSearcher = new SearcherItem(Constants.getMainNoteSearcher());
        ((DefaultMutableTreeNode) this.getRoot()).add(this.defaultSearcher);
    }

    private void initializeFolderCategory() {
        boolean init = this.folderCategory == null;

        if (this.folderCategory == null) {
            this.folderCategory = new SearcherCategory(
                    NoteSearcherType.FOLDER,
                    this.settingsPrefix + ".category.folder.expanded");
            ((DefaultMutableTreeNode) this.getRoot()).add(this.folderCategory);
        }

        boolean expanded = this.tree.isExpanded(TreeUtils.getPath(this.folderCategory));

        this.folderCategory.removeAllChildren();

        this.nodeStructureChanged(this.folderCategory);

        this.folderCategory.add(new FolderItem(null));

        List<Folder> folders = new ArrayList<Folder>(
                FolderFactory.getInstance().getList());

        Collections.sort(folders, BasicModelComparator.INSTANCE_NULL_LAST);

        for (Folder folder : folders) {
            if (folder.getModelStatus().isEndUserStatus()) {
                if (!folder.isSelfOrParentArchived()) {
                    DefaultMutableTreeNode node = null;

                    if (folder.getParent() != null)
                        node = this.findItemFromFolder(folder.getParent());

                    if (node == null)
                        node = this.folderCategory;

                    node.add(new FolderItem(folder));
                }
            }
        }

        if (init) {
            FolderFactory.getInstance().addListChangeListener(
                    new WeakListChangeListener(
                            FolderFactory.getInstance(),
                            this));
            FolderFactory.getInstance().addPropertyChangeListener(
                    new WeakPropertyChangeListener(
                            FolderFactory.getInstance(),
                            this));
        }

        this.nodeStructureChanged(this.folderCategory);

        if (expanded)
            TreeUtils.expandAll(
                    this.tree,
                    TreeUtils.getPath(this.folderCategory),
                    true);
    }

    private void initializePersonalCategory() {
        if (this.personalCategory == null) {
            this.personalCategory = new SearcherCategory(
                    NoteSearcherType.PERSONAL,
                    this.settingsPrefix + ".category.personal.expanded");
            ((DefaultMutableTreeNode) this.getRoot()).add(this.personalCategory);
        }

        boolean expanded = this.tree.isExpanded(TreeUtils.getPath(this.personalCategory));

        this.personalCategory.removeAllChildren();

        this.nodeStructureChanged(this.personalCategory);

        List<NoteSearcher> searchers = new ArrayList<NoteSearcher>(
                NoteSearcherFactory.getInstance().getList());

        Collections.sort(searchers, NoteSearcherFolderComparator.INSTANCE);

        for (NoteSearcher searcher : searchers) {
            if (searcher.getType() == NoteSearcherType.PERSONAL) {
                this.getCategoryFromNoteSearcherType(
                        searcher.getType(),
                        searcher.getFolders(),
                        true);
            }
        }

        Collections.sort(searchers, NoteSearcherComparator.INSTANCE);

        for (NoteSearcher searcher : searchers) {
            if (searcher.getType() == NoteSearcherType.PERSONAL) {
                SearcherCategory category = this.getCategoryFromNoteSearcherType(
                        searcher.getType(),
                        searcher.getFolders(),
                        true);
                category.add(new SearcherItem(searcher));
            }
        }

        this.nodeStructureChanged(this.personalCategory);

        if (expanded)
            TreeUtils.expandAll(
                    this.tree,
                    TreeUtils.getPath(this.personalCategory),
                    true);
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
                if (EqualsUtils.equalsStringIgnoreCase(
                        ((SearcherCategory) node).getFolder(),
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

            category.add(c);

            return this.getCategoryFromFolder(c, folders, index + 1, create);
        }

        return category;
    }

    @Override
    public void listChange(ListChangeEvent event) {
        if (event.getValue() instanceof Note) {
            if (!Synchronizing.getInstance().isSynchronizing())
                this.updateBadgesNeeded();

            return;
        }

        if (event.getValue() instanceof Task) {
            return;
        }

        if (event.getValue() instanceof Folder) {
            this.initializeFolderCategory();
        }

        if (event.getValue() instanceof NoteSearcher) {
            this.initializePersonalCategory();
        }

        this.updateSelection(null);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (Synchronizing.getInstance().isSynchronizing()) {
            return;
        }

        if (event.getSource() instanceof Note) {
            this.updateBadgesNeeded();

            return;
        }

        if (event.getSource() instanceof Task) {
            return;
        }

        if (event.getSource() instanceof Folder) {
            boolean statusUpdated = false;

            if (event.getPropertyName().equals(BasicModel.PROP_MODEL_STATUS)
                    && ((ModelStatus) event.getOldValue()).isEndUserStatus() != ((ModelStatus) event.getNewValue()).isEndUserStatus())
                statusUpdated = true;

            if (statusUpdated
                    || event.getPropertyName().equals(ModelParent.PROP_PARENT)
                    || event.getPropertyName().equals(
                    ModelArchive.PROP_ARCHIVED)) {
                Folder folder = this.tree.getSelectedFolder();

                this.initializeFolderCategory();

                if (folder != null)
                    this.updateSelection(this.findItemFromFolder(folder));
                else
                    this.updateSelection(null);
            }

            if (event.getPropertyName().equals(BasicModel.PROP_TITLE)
                    || event.getPropertyName().equals(GuiModel.PROP_COLOR)) {
                this.nodeChanged(this.findItemFromFolder((Folder) event.getSource()));
            }

            return;
        }

        if (event.getSource() instanceof NoteSearcher) {
            if (event.getPropertyName().equals(NoteSearcher.PROP_TYPE)
                    || event.getPropertyName().equals(NoteSearcher.PROP_FOLDER)) {
                NoteSearcher searcher = this.tree.getSelectedNoteSearcher();

                this.initializePersonalCategory();

                if (searcher != null)
                    this.updateSelection(this.findItemFromSearcher(searcher));
                else
                    this.updateSelection(null);
            }

            if (event.getPropertyName().equals(NoteSearcher.PROP_TITLE)
                    || event.getPropertyName().equals(NoteSearcher.PROP_ICON)) {
                this.nodeChanged(this.findItemFromSearcher((NoteSearcher) event.getSource()));
            }

            return;
        }
    }

    public void update() {
        TreeNode node = (TreeNode) this.tree.getSelectionPath().getLastPathComponent();

        Object selectedObject = null;

        if (node instanceof SearcherItem)
            selectedObject = ((SearcherItem) node).getNoteSearcher();
        if (node instanceof FolderItem)
            selectedObject = ((FolderItem) node).getFolder();

        this.initializeFolderCategory();
        this.initializePersonalCategory();

        this.updateBadgesNeeded();

        if (selectedObject instanceof NoteSearcher)
            selectedObject = this.findItemFromSearcher((NoteSearcher) selectedObject);
        if (selectedObject instanceof Folder)
            selectedObject = this.findItemFromFolder((Folder) selectedObject);

        if (selectedObject instanceof SearcherNode)
            this.updateSelection((SearcherNode) selectedObject);
        else
            this.updateSelection(null);
    }

    public void updateBadgesNeeded() {
        this.updateBadgesNeeded = Calendar.getInstance();
    }

    private void updateBadges() {
        this.updateBadges(this.defaultSearcher);

        SearcherCategory[] categories = this.getCategories();
        for (SearcherCategory category : categories) {
            for (int i = 0; i < category.getChildCount(); i++) {
                if (category.getChildAt(i) instanceof SearcherNode)
                    this.updateBadges((SearcherNode) category.getChildAt(i));

                if (category.getChildAt(i) instanceof SearcherCategory)
                    this.updateBadges((SearcherCategory) category.getChildAt(i));
            }
        }
    }

    private void updateBadges(SearcherCategory category) {
        for (int i = 0; i < category.getChildCount(); i++) {
            if (category.getChildAt(i) instanceof SearcherNode)
                this.updateBadges((SearcherNode) category.getChildAt(i));

            if (category.getChildAt(i) instanceof SearcherCategory)
                this.updateBadges((SearcherCategory) category.getChildAt(i));
        }
    }

    private void updateBadges(final SearcherNode node) {
        node.updateBadgeCount();

        ProcessUtils.executeOrInvokeAndWait(new Runnable() {

            @Override
            public void run() {
                NoteSearcherTreeModel.this.nodeChanged(node);
            }

        });

        for (int i = 0; i < node.getChildCount(); i++) {
            if (node.getChildAt(i) instanceof SearcherNode)
                this.updateBadges((SearcherNode) node.getChildAt(i));
        }
    }

    private void updateSelection(TreeNode node) {
        if (node != null)
            this.tree.getSelectionModel().setSelectionPath(
                    TreeUtils.getPath(node));

        if (this.tree.getSelectionModel().getSelectionPath() == null)
            this.tree.getSelectionModel().setSelectionPath(
                    TreeUtils.getPath(this.getDefaultSearcher()));
    }

    private class UpdateBadgesThread extends Thread {

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    if (NoteSearcherTreeModel.this.updateBadgesNeeded != null &&
                            DateUtils.getDiffInSeconds(
                                    NoteSearcherTreeModel.this.updateBadgesNeeded,
                                    Calendar.getInstance()) > 3) {
                        NoteSearcherTreeModel.this.updateBadgesNeeded = null;

                        NoteSearcherTreeModel.this.updateBadges();
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
        }
    }

}
