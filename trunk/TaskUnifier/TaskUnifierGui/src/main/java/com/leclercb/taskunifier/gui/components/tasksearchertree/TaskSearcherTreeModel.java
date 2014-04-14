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

package com.leclercb.taskunifier.gui.components.tasksearchertree;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.WeakListChangeListener;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.DateUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.gui.utils.TreeUtils;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.utils.TaskTagList;
import com.leclercb.taskunifier.gui.api.models.GuiModel;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcherFactory;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcherType;
import com.leclercb.taskunifier.gui.commons.comparators.BasicModelComparator;
import com.leclercb.taskunifier.gui.commons.comparators.TaskSearcherComparator;
import com.leclercb.taskunifier.gui.commons.comparators.TaskSearcherFolderComparator;
import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.components.tasksearchertree.nodes.*;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
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

public class TaskSearcherTreeModel extends DefaultTreeModel implements ListChangeListener, PropertyChangeListener {

    private TaskSearcherTree tree;

    private String settingsPrefix;

    private SearcherItem defaultSearcher;
    private SearcherCategory generalCategory;
    private SearcherCategory contextCategory;
    private SearcherCategory folderCategory;
    private SearcherCategory goalCategory;
    private SearcherCategory locationCategory;
    private SearcherCategory tagCategory;
    private SearcherCategory personalCategory;

    private Calendar updateBadgesNeeded;

    public TaskSearcherTreeModel(String settingsPrefix, TaskSearcherTree tree) {
        super(new SearcherCategory(TaskSearcherType.DEFAULT, null));

        this.settingsPrefix = settingsPrefix;

        this.tree = tree;

        this.updateBadgesNeeded = null;

        this.initializeDefaultSearcher();
        this.initializeGeneralCategory();
        this.initializeContextCategory();
        this.initializeFolderCategory();
        this.initializeGoalCategory();
        this.initializeLocationCategory();
        this.initializeTagCategory();
        this.initializePersonalCategory();

        TaskFactory.getInstance().addListChangeListener(
                new WeakListChangeListener(TaskFactory.getInstance(), this));
        TaskFactory.getInstance().addPropertyChangeListener(
                new WeakPropertyChangeListener(TaskFactory.getInstance(), this));

        TaskSearcherFactory.getInstance().addListChangeListener(
                new WeakListChangeListener(
                        TaskSearcherFactory.getInstance(),
                        this));
        TaskSearcherFactory.getInstance().addPropertyChangeListener(
                new WeakPropertyChangeListener(
                        TaskSearcherFactory.getInstance(),
                        this));

        Main.getSettings().addPropertyChangeListener(
                new WeakPropertyChangeListener(Main.getSettings(), this));

        new UpdateBadgesThread().start();
    }

    public SearcherItem getDefaultSearcher() {
        return this.defaultSearcher;
    }

    public SearcherCategory[] getCategories() {
        return new SearcherCategory[]{
                this.generalCategory,
                this.contextCategory,
                this.folderCategory,
                this.goalCategory,
                this.locationCategory,
                this.tagCategory,
                this.personalCategory};
    }

    private void initializeDefaultSearcher() {
        this.defaultSearcher = new SearcherItem(Constants.getMainTaskSearcher());
        ((DefaultMutableTreeNode) this.getRoot()).add(this.defaultSearcher);
    }

    private void initializeGeneralCategory() {
        if (this.generalCategory == null) {
            this.generalCategory = new SearcherCategory(
                    TaskSearcherType.GENERAL,
                    this.settingsPrefix + ".category.general.expanded");
            ((DefaultMutableTreeNode) this.getRoot()).add(this.generalCategory);
        }

        boolean expanded = this.tree.isExpanded(TreeUtils.getPath(this.generalCategory));

        this.generalCategory.removeAllChildren();

        this.nodeStructureChanged(this.generalCategory);

        List<TaskSearcher> searchers = new ArrayList<TaskSearcher>(
                TaskSearcherFactory.getInstance().getList());

        Collections.sort(searchers, TaskSearcherFolderComparator.INSTANCE);

        for (TaskSearcher searcher : searchers) {
            if (searcher.getType() == TaskSearcherType.GENERAL) {
                this.getCategoryFromTaskSearcherType(
                        searcher.getType(),
                        searcher.getFolders(),
                        true);
            }
        }

        Collections.sort(searchers, TaskSearcherComparator.INSTANCE);

        for (TaskSearcher searcher : searchers) {
            if (searcher.getType() == TaskSearcherType.GENERAL) {
                SearcherCategory category = this.getCategoryFromTaskSearcherType(
                        searcher.getType(),
                        searcher.getFolders(),
                        true);
                category.add(new SearcherItem(searcher));
            }
        }

        this.nodeStructureChanged(this.generalCategory);

        if (expanded)
            TreeUtils.expandAll(
                    this.tree,
                    TreeUtils.getPath(this.generalCategory),
                    true);
    }

    private void initializeContextCategory() {
        boolean init = this.contextCategory == null;

        if (this.contextCategory == null) {
            this.contextCategory = new SearcherCategory(
                    TaskSearcherType.CONTEXT,
                    this.settingsPrefix + ".category.context.expanded");
            ((DefaultMutableTreeNode) this.getRoot()).add(this.contextCategory);
        }

        boolean expanded = this.tree.isExpanded(TreeUtils.getPath(this.contextCategory));

        this.contextCategory.removeAllChildren();

        this.nodeStructureChanged(this.contextCategory);

        this.contextCategory.add(new ModelItem(ModelType.CONTEXT, null));

        List<Context> contexts = new ArrayList<Context>(
                ContextFactory.getInstance().getList());

        Collections.sort(contexts, BasicModelComparator.INSTANCE_NULL_FIRST);

        for (Context context : contexts) {
            if (context.getModelStatus().isEndUserStatus()) {
                DefaultMutableTreeNode node = null;

                if (context.getParent() != null)
                    node = this.findItemFromModel(context.getParent());

                if (node == null)
                    node = this.contextCategory;

                node.add(new ModelItem(ModelType.CONTEXT, context));
            }
        }

        if (init) {
            ContextFactory.getInstance().addListChangeListener(
                    new WeakListChangeListener(
                            ContextFactory.getInstance(),
                            this));
            ContextFactory.getInstance().addPropertyChangeListener(
                    new WeakPropertyChangeListener(
                            ContextFactory.getInstance(),
                            this));
        }

        this.nodeStructureChanged(this.contextCategory);

        if (expanded)
            TreeUtils.expandAll(
                    this.tree,
                    TreeUtils.getPath(this.contextCategory),
                    true);
    }

    private void initializeFolderCategory() {
        boolean init = this.folderCategory == null;

        if (this.folderCategory == null) {
            this.folderCategory = new SearcherCategory(
                    TaskSearcherType.FOLDER,
                    this.settingsPrefix + ".category.folder.expanded");
            ((DefaultMutableTreeNode) this.getRoot()).add(this.folderCategory);
        }

        boolean expanded = this.tree.isExpanded(TreeUtils.getPath(this.folderCategory));

        this.folderCategory.removeAllChildren();

        this.nodeStructureChanged(this.folderCategory);

        this.folderCategory.add(new ModelItem(ModelType.FOLDER, null));

        List<Folder> folders = new ArrayList<Folder>(
                FolderFactory.getInstance().getList());

        Collections.sort(folders, BasicModelComparator.INSTANCE_NULL_FIRST);

        for (Folder folder : folders) {
            if (folder.getModelStatus().isEndUserStatus()) {
                if (!folder.isSelfOrParentArchived()) {
                    DefaultMutableTreeNode node = null;

                    if (folder.getParent() != null)
                        node = this.findItemFromModel(folder.getParent());

                    if (node == null)
                        node = this.folderCategory;

                    node.add(new ModelItem(ModelType.FOLDER, folder));
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

    private void initializeGoalCategory() {
        boolean init = this.goalCategory == null;

        if (this.goalCategory == null) {
            this.goalCategory = new SearcherCategory(
                    TaskSearcherType.GOAL,
                    this.settingsPrefix + ".category.goal.expanded");
            ((DefaultMutableTreeNode) this.getRoot()).add(this.goalCategory);
        }

        boolean expanded = this.tree.isExpanded(TreeUtils.getPath(this.goalCategory));

        this.goalCategory.removeAllChildren();

        this.nodeStructureChanged(this.goalCategory);

        this.goalCategory.add(new ModelItem(ModelType.GOAL, null));

        List<Goal> goals = new ArrayList<Goal>(
                GoalFactory.getInstance().getList());

        Collections.sort(goals, BasicModelComparator.INSTANCE_NULL_FIRST);

        for (Goal goal : goals) {
            if (goal.getModelStatus().isEndUserStatus()) {
                if (!goal.isSelfOrParentArchived()) {
                    DefaultMutableTreeNode node = null;

                    if (goal.getParent() != null)
                        node = this.findItemFromModel(goal.getParent());

                    if (node == null)
                        node = this.goalCategory;

                    node.add(new ModelItem(ModelType.GOAL, goal));
                }
            }
        }

        if (init) {
            GoalFactory.getInstance().addListChangeListener(
                    new WeakListChangeListener(GoalFactory.getInstance(), this));
            GoalFactory.getInstance().addPropertyChangeListener(
                    new WeakPropertyChangeListener(
                            GoalFactory.getInstance(),
                            this));
        }

        this.nodeStructureChanged(this.goalCategory);

        if (expanded)
            TreeUtils.expandAll(
                    this.tree,
                    TreeUtils.getPath(this.goalCategory),
                    true);
    }

    private void initializeLocationCategory() {
        boolean init = this.locationCategory == null;

        if (this.locationCategory == null) {
            this.locationCategory = new SearcherCategory(
                    TaskSearcherType.LOCATION,
                    this.settingsPrefix + ".category.location.expanded");
            ((DefaultMutableTreeNode) this.getRoot()).add(this.locationCategory);
        }

        boolean expanded = this.tree.isExpanded(TreeUtils.getPath(this.locationCategory));

        this.locationCategory.removeAllChildren();

        this.nodeStructureChanged(this.locationCategory);

        this.locationCategory.add(new ModelItem(ModelType.LOCATION, null));

        List<Location> locations = new ArrayList<Location>(
                LocationFactory.getInstance().getList());

        Collections.sort(locations, BasicModelComparator.INSTANCE_NULL_FIRST);

        for (Location location : locations)
            if (location.getModelStatus().isEndUserStatus())
                this.locationCategory.add(new ModelItem(
                        ModelType.LOCATION,
                        location));

        if (init) {
            LocationFactory.getInstance().addListChangeListener(
                    new WeakListChangeListener(
                            LocationFactory.getInstance(),
                            this));
            LocationFactory.getInstance().addPropertyChangeListener(
                    new WeakPropertyChangeListener(
                            LocationFactory.getInstance(),
                            this));
        }

        this.nodeStructureChanged(this.locationCategory);

        if (expanded)
            TreeUtils.expandAll(
                    this.tree,
                    TreeUtils.getPath(this.locationCategory),
                    true);
    }

    private void initializeTagCategory() {
        boolean init = this.tagCategory == null;

        if (this.tagCategory == null) {
            this.tagCategory = new SearcherCategory(
                    TaskSearcherType.TAG,
                    this.settingsPrefix + ".category.tag.expanded");
            ((DefaultMutableTreeNode) this.getRoot()).add(this.tagCategory);
        }

        boolean expanded = this.tree.isExpanded(TreeUtils.getPath(this.tagCategory));

        this.tagCategory.removeAllChildren();

        this.nodeStructureChanged(this.tagCategory);

        TagList tags = TaskTagList.getInstance().getTags();

        for (Tag tag : tags)
            this.tagCategory.add(new TagItem(tag));

        if (init) {
            TaskTagList.getInstance().addListChangeListener(
                    new WeakListChangeListener(TaskTagList.getInstance(), this));
        }

        this.nodeStructureChanged(this.tagCategory);

        if (expanded)
            TreeUtils.expandAll(
                    this.tree,
                    TreeUtils.getPath(this.tagCategory),
                    true);
    }

    private void initializePersonalCategory() {
        if (this.personalCategory == null) {
            this.personalCategory = new SearcherCategory(
                    TaskSearcherType.PERSONAL,
                    this.settingsPrefix + ".category.personal.expanded");
            ((DefaultMutableTreeNode) this.getRoot()).add(this.personalCategory);
        }

        boolean expanded = this.tree.isExpanded(TreeUtils.getPath(this.personalCategory));

        this.personalCategory.removeAllChildren();

        this.nodeStructureChanged(this.personalCategory);

        List<TaskSearcher> searchers = new ArrayList<TaskSearcher>(
                TaskSearcherFactory.getInstance().getList());

        Collections.sort(searchers, TaskSearcherFolderComparator.INSTANCE);

        for (TaskSearcher searcher : searchers) {
            if (searcher.getType() == TaskSearcherType.PERSONAL) {
                this.getCategoryFromTaskSearcherType(
                        searcher.getType(),
                        searcher.getFolders(),
                        true);
            }
        }

        Collections.sort(searchers, TaskSearcherComparator.INSTANCE);

        for (TaskSearcher searcher : searchers) {
            if (searcher.getType() == TaskSearcherType.PERSONAL) {
                SearcherCategory category = this.getCategoryFromTaskSearcherType(
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

    public ModelItem findItemFromModel(Model model) {
        DefaultMutableTreeNode category = this.getCategoryFromModelType(model.getModelType());
        return this.findModelItem(category, model);
    }

    private ModelItem findModelItem(TreeNode parent, Model model) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            TreeNode node = parent.getChildAt(i);
            if (node instanceof ModelItem) {
                if (EqualsUtils.equals(((ModelItem) node).getModel(), model)) {
                    return (ModelItem) node;
                }

                ModelItem item = this.findModelItem(node, model);
                if (item != null)
                    return item;
            }
        }

        return null;
    }

    public SearcherItem findItemFromSearcher(TaskSearcher searcher) {
        return this.findItemFromSearcher(
                searcher,
                searcher.getType(),
                searcher.getFolders());
    }

    private SearcherItem findItemFromSearcher(
            TaskSearcher searcher,
            TaskSearcherType type,
            String[] folders) {
        SearcherCategory category = this.getCategoryFromTaskSearcherType(
                type,
                folders,
                false);

        for (int i = 0; i < category.getChildCount(); i++) {
            TreeNode node = category.getChildAt(i);
            if (node instanceof SearcherItem) {
                if (EqualsUtils.equals(
                        ((SearcherItem) node).getTaskSearcher(),
                        searcher)) {
                    return (SearcherItem) node;
                }
            }
        }

        return null;
    }

    public TagItem findItemFromTag(Tag tag) {
        for (int i = 0; i < this.tagCategory.getChildCount(); i++) {
            TreeNode node = this.tagCategory.getChildAt(i);
            if (node instanceof TagItem) {
                if (((TagItem) node).getTag().equals(tag)) {
                    return (TagItem) node;
                }
            }
        }

        return null;
    }

    private SearcherCategory getCategoryFromTaskSearcherType(
            TaskSearcherType type,
            String[] folders,
            boolean create) {
        switch (type) {
            case DEFAULT:
                return (SearcherCategory) this.getRoot();
            case GENERAL:
                return this.getCategoryFromFolder(
                        this.generalCategory,
                        folders,
                        0,
                        create);
            case CONTEXT:
                return this.contextCategory;
            case FOLDER:
                return this.folderCategory;
            case GOAL:
                return this.goalCategory;
            case LOCATION:
                return this.locationCategory;
            case TAG:
                return this.tagCategory;
            case PERSONAL:
                return this.getCategoryFromFolder(
                        this.personalCategory,
                        folders,
                        0,
                        create);
        }

        return null;
    }

    private SearcherCategory getCategoryFromModelType(ModelType type) {
        switch (type) {
            case CONTEXT:
                return this.contextCategory;
            case FOLDER:
                return this.folderCategory;
            case GOAL:
                return this.goalCategory;
            case LOCATION:
                return this.locationCategory;
            default:
                return null;
        }
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
                        TaskSearcher.getFolder(ArrayUtils.subarray(
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
                    TaskSearcher.getFolder(ArrayUtils.subarray(
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
            return;
        }

        if (event.getValue() instanceof Task) {
            if (!Synchronizing.getInstance().isSynchronizing())
                this.updateBadgesNeeded();

            return;
        }

        if (event.getValue() instanceof Model) {
            switch (((Model) event.getValue()).getModelType()) {
                case CONTEXT:
                    this.initializeContextCategory();
                    break;
                case FOLDER:
                    this.initializeFolderCategory();
                    break;
                case GOAL:
                    this.initializeGoalCategory();
                    break;
                case LOCATION:
                    this.initializeLocationCategory();
                    break;
                default:
                    break;
            }
        }

        if (event.getValue() instanceof TaskSearcher) {
            switch (((TaskSearcher) event.getValue()).getType()) {
                case GENERAL:
                    this.initializeGeneralCategory();
                    break;
                case PERSONAL:
                    this.initializePersonalCategory();
                    break;
                default:
                    break;
            }
        }

        if (event.getValue() instanceof Tag) {
            this.initializeTagCategory();
        }

        this.updateSelection(null);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (Synchronizing.getInstance().isSynchronizing()) {
            return;
        }

        if (event.getSource() instanceof Note) {
            return;
        }

        if (event.getSource() instanceof Task) {
            if (!event.getPropertyName().equals(ModelNote.PROP_NOTE))
                this.updateBadgesNeeded();

            return;
        }

        if (event.getPropertyName().equals("tasksearcher.show_completed_tasks")) {
            this.updateBadgesNeeded();

            return;
        }

        if (event.getSource() instanceof Model) {
            boolean statusUpdated = false;

            if (event.getPropertyName().equals(BasicModel.PROP_MODEL_STATUS)
                    && ((ModelStatus) event.getOldValue()).isEndUserStatus() != ((ModelStatus) event.getNewValue()).isEndUserStatus())
                statusUpdated = true;

            if (statusUpdated
                    || event.getPropertyName().equals(ModelParent.PROP_PARENT)
                    || event.getPropertyName().equals(
                    ModelArchive.PROP_ARCHIVED)) {
                Model model = this.tree.getSelectedModel();

                switch (((Model) event.getSource()).getModelType()) {
                    case CONTEXT:
                        this.initializeContextCategory();
                        break;
                    case FOLDER:
                        this.initializeFolderCategory();
                        break;
                    case GOAL:
                        this.initializeGoalCategory();
                        break;
                    case LOCATION:
                        this.initializeLocationCategory();
                        break;
                    default:
                        break;
                }

                if (model != null)
                    this.updateSelection(this.findItemFromModel(model));
                else
                    this.updateSelection(null);
            }

            if (event.getPropertyName().equals(BasicModel.PROP_TITLE)
                    || event.getPropertyName().equals(GuiModel.PROP_COLOR)) {
                this.nodeChanged(this.findItemFromModel((Model) event.getSource()));
            }

            return;
        }

        if (event.getSource() instanceof TaskSearcher) {
            if (event.getPropertyName().equals(TaskSearcher.PROP_TYPE)) {
                TaskSearcher searcher = this.tree.getSelectedTaskSearcher();

                this.initializeGeneralCategory();
                this.initializePersonalCategory();

                if (searcher != null)
                    this.updateSelection(this.findItemFromSearcher(searcher));
                else
                    this.updateSelection(null);
            }

            if (event.getPropertyName().equals(TaskSearcher.PROP_FOLDER)) {
                TaskSearcher searcher = this.tree.getSelectedTaskSearcher();

                switch (((TaskSearcher) event.getSource()).getType()) {
                    case GENERAL:
                        this.initializeGeneralCategory();
                        break;
                    case PERSONAL:
                        this.initializePersonalCategory();
                        break;
                    default:
                        break;
                }

                if (searcher != null)
                    this.updateSelection(this.findItemFromSearcher(searcher));
                else
                    this.updateSelection(null);
            }

            if (event.getPropertyName().equals(TaskSearcher.PROP_TITLE)
                    || event.getPropertyName().equals(TaskSearcher.PROP_ICON)) {
                this.nodeChanged(this.findItemFromSearcher((TaskSearcher) event.getSource()));
            }

            return;
        }
    }

    public void update() {
        TreeNode node = (TreeNode) this.tree.getSelectionPath().getLastPathComponent();

        Object selectedObject = null;

        if (node instanceof SearcherItem)
            selectedObject = ((SearcherItem) node).getTaskSearcher();
        if (node instanceof ModelItem)
            selectedObject = ((ModelItem) node).getModel();
        if (node instanceof TagItem)
            selectedObject = ((TagItem) node).getTag();

        this.initializeGeneralCategory();
        this.initializeContextCategory();
        this.initializeFolderCategory();
        this.initializeGoalCategory();
        this.initializeLocationCategory();
        this.initializePersonalCategory();

        this.updateBadgesNeeded();

        if (selectedObject instanceof TaskSearcher)
            selectedObject = this.findItemFromSearcher((TaskSearcher) selectedObject);
        if (selectedObject instanceof Model)
            selectedObject = this.findItemFromModel((Model) selectedObject);
        if (selectedObject instanceof Tag)
            selectedObject = this.findItemFromTag((Tag) selectedObject);

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
                TaskSearcherTreeModel.this.nodeChanged(node);
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
                    if (TaskSearcherTreeModel.this.updateBadgesNeeded != null &&
                            DateUtils.getDiffInSeconds(
                                    TaskSearcherTreeModel.this.updateBadgesNeeded,
                                    Calendar.getInstance()) > 3) {
                        TaskSearcherTreeModel.this.updateBadgesNeeded = null;

                        TaskSearcherTreeModel.this.updateBadges();
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
        }
    }

}
