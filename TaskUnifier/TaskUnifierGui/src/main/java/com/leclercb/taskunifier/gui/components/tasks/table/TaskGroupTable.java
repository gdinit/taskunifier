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
package com.leclercb.taskunifier.gui.components.tasks.table;

import com.explodingpixels.macwidgets.SourceListStandardColorScheme;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.commons.events.*;
import com.leclercb.taskunifier.gui.components.print.PrintUtils;
import com.leclercb.taskunifier.gui.components.print.TablePrintable;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.components.tasks.TaskTableView;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.table.TUTableProperties;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.TaskGrouperUtils;
import com.leclercb.taskunifier.gui.utils.TaskUtils;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXCollapsiblePane.Direction;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.Painter;

import javax.swing.*;
import javax.swing.JTable.PrintMode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskGroupTable extends JXPanel implements TaskTableView, ModelSelectionChangeSupported, ModelSelectionListener, PropertyChangeSupported, PropertyChangeListener {

    private int level;

    private TUTableProperties<Task> tableProperties;
    private List<TaskTableView> tables;
    private TaskSearcher searcher;
    private List<TaskSearcher> searchers;
    private ModelSelectionChangeSupport modelSelectionChangeSupport;
    private boolean isSelectionAdjusting;

    public TaskGroupTable(TUTableProperties<Task> tableProperties) {
        this(tableProperties, 0);
    }

    private TaskGroupTable(TUTableProperties<Task> tableProperties, int level) {
        this.level = level;

        this.tableProperties = tableProperties;
        this.tables = new ArrayList<TaskTableView>();
        this.searcher = null;
        this.searchers = null;
        this.modelSelectionChangeSupport = new ModelSelectionChangeSupport(this);
        this.isSelectionAdjusting = false;

        this.initialize();
    }

    private void initialize() {
        if (this.level == 0) {
            this.setOpaque(true);

            this.setBackgroundPainter(new Painter<JPanel>() {

                private SourceListStandardColorScheme scheme = new SourceListStandardColorScheme();

                @Override
                public void paint(
                        Graphics2D g,
                        JPanel obj,
                        int width,
                        int height) {
                    Graphics2D g2d = (Graphics2D) g.create();

                    Paint p = new GradientPaint(
                            0,
                            0,
                            this.scheme.getActiveBackgroundColor(),
                            width,
                            0,
                            Color.WHITE);

                    g2d.setPaint(p);
                    g2d.fillRect(0, 0, width, height);
                    g2d.dispose();
                }

            });
        } else {
            this.setOpaque(false);
        }

        this.updateTables();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public Task[] getSelectedTasks() {
        List<Task> tasks = new ArrayList<Task>();

        for (TaskTableView table : this.tables) {
            tasks.addAll(Arrays.asList(table.getSelectedTasks()));
        }

        return tasks.toArray(new Task[0]);
    }

    @Override
    public void setSelectedTasks(Task[] tasks) {
        for (TaskTableView table : this.tables) {
            table.setSelectedTasks(tasks);
        }
    }

    @Override
    public void addModelSelectionChangeListener(ModelSelectionListener listener) {
        this.modelSelectionChangeSupport.addModelSelectionChangeListener(listener);
    }

    @Override
    public void removeModelSelectionChangeListener(
            ModelSelectionListener listener) {
        this.modelSelectionChangeSupport.removeModelSelectionChangeListener(listener);
    }

    @Override
    public void taskSearcherSelectionChange(
            TaskSearcherSelectionChangeEvent event) {
        this.searcher = event.getSelectedTaskSearcher();
        this.searchers = TaskGrouperUtils.getFilters(this.searcher);
        this.updateTables();
    }

    @Override
    public Task[] getTasks() {
        List<Task> tasks = new ArrayList<Task>();

        for (TaskTableView table : this.tables) {
            tasks.addAll(Arrays.asList(table.getTasks()));
        }

        return tasks.toArray(new Task[0]);
    }

    @Override
    public int getTaskCount() {
        int count = 0;

        for (TaskTableView table : this.tables) {
            count += table.getTaskCount();
        }

        return count;
    }

    @Override
    public void setSelectedTaskAndStartEdit(Task task) {
        for (TaskTableView table : this.tables) {
            table.setSelectedTaskAndStartEdit(task);
        }
    }

    @Override
    public void refreshTasks() {
        for (TaskTableView table : this.tables) {
            table.refreshTasks();
        }
    }

    @Override
    public void setSearchText(String searchText) {
        for (TaskTableView table : this.tables) {
            table.setSearchText(searchText);
        }
    }

    @Override
    public void printTasks(boolean selection) throws Exception {
        Task[] tasks = null;

        if (selection)
            tasks = this.getSelectedTasks();
        else
            tasks = this.getTasks();

        TablePrintable tablePrintable = new TablePrintable(
                new TaskPrintTable(new TUTableProperties<Task>(
                        TaskColumnList.getInstance(),
                        this.tableProperties.getPropertyName() + ".print",
                        false), tasks),
                PrintMode.NORMAL,
                0.7,
                new MessageFormat(Constants.TITLE
                        + " - "
                        + this.searcher.getTitle()),
                new MessageFormat(this.getTaskCount() + " tasks | Page - {0}"));

        PrintUtils.printTable("view.tasks.print", tablePrintable);
    }

    @Override
    public void pasteTask() {
        this.tables.get(0).pasteTask();
    }

    @Override
    public void commitChanges() {
        for (TaskTableView table : this.tables) {
            table.commitChanges();
        }
    }

    public void updateTables() {
        for (TaskTableView table : this.tables) {
            table.removeModelSelectionChangeListener(this);
            table.removePropertyChangeListener(this);
        }

        this.tables.clear();

        this.removeAll();
        this.setLayout(new BorderLayout());
        this.validate();
        this.repaint();

        if (this.searchers == null
                || this.searchers.size() == 0
                || !this.doesContainDisplayedTasks(this.searcher)) {
            if (this.level == 0) {
                TaskTable table = new TaskTable(this.tableProperties);

                if (this.searcher != null)
                    table.taskSearcherSelectionChange(new TaskSearcherSelectionChangeEvent(
                            this,
                            this.searcher));

                this.add(
                        ComponentFactory.createJScrollPane(table, false),
                        BorderLayout.CENTER);

                table.addModelSelectionChangeListener(this);
                this.tables.add(table);
            }

            this.validate();
            this.repaint();

            this.firePropertyChange(PROP_TASK_COUNT, null, this.getTaskCount());

            return;
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout("insets 0 0 0 0"));
        mainPanel.setOpaque(false);

        for (TaskSearcher searcher : this.searchers) {
            if (this.doesContainDisplayedTasks(searcher)) {
                JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                titlePanel.setOpaque(false);

                String spacer = "";
                for (int i = 0; i < this.level; i++) {
                    spacer += "     ";
                }

                final JXCollapsiblePane collapsiblePane = new JXCollapsiblePane(
                        Direction.UP);
                collapsiblePane.setLayout(new BorderLayout());
                collapsiblePane.setOpaque(false);
                collapsiblePane.setAnimated(false);

                if (collapsiblePane.getContentPane() instanceof JPanel)
                    ((JPanel) collapsiblePane.getContentPane()).setOpaque(false);

                final JButton button = new JButton();
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setIcon(ImageUtils.getResourceImage(
                        "collapse.png",
                        12,
                        12));
                button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent event) {
                        collapsiblePane.setCollapsed(!collapsiblePane.isCollapsed());

                        if (collapsiblePane.isCollapsed())
                            button.setIcon(ImageUtils.getResourceImage(
                                    "expand.png",
                                    12,
                                    12));
                        else
                            button.setIcon(ImageUtils.getResourceImage(
                                    "collapse.png",
                                    12,
                                    12));
                    }

                });

                titlePanel.add(new JLabel(spacer));
                titlePanel.add(button);
                titlePanel.add(new JLabel(searcher.getTitle()));

                mainPanel.add(titlePanel, "grow, wrap");
                mainPanel.add(collapsiblePane, "wrap");

                if (searcher.getGrouper().getElementCount() == 0) {
                    JPanel tablePanel = new JPanel(new BorderLayout());
                    tablePanel.setOpaque(false);
                    TaskTable table = new TaskTable(this.tableProperties);
                    table.taskSearcherSelectionChange(new TaskSearcherSelectionChangeEvent(
                            this,
                            searcher));
                    tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
                    tablePanel.add(table, BorderLayout.CENTER);
                    collapsiblePane.add(tablePanel, BorderLayout.CENTER);

                    table.addModelSelectionChangeListener(this);
                    this.tables.add(table);
                } else {
                    TaskGroupTable table = new TaskGroupTable(
                            this.tableProperties,
                            this.level + 1);
                    table.taskSearcherSelectionChange(new TaskSearcherSelectionChangeEvent(
                            this,
                            searcher));
                    collapsiblePane.add(table, BorderLayout.CENTER);

                    table.addModelSelectionChangeListener(this);
                    this.tables.add(table);
                }
            }
        }

        if (this.level == 0) {
            JScrollPane scrollPane = ComponentFactory.createJScrollPane(
                    mainPanel,
                    false);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.getVerticalScrollBar().setUnitIncrement(10);

            this.add(scrollPane, BorderLayout.CENTER);
        } else {
            this.add(mainPanel, BorderLayout.CENTER);
        }

        this.validate();
        this.repaint();

        this.firePropertyChange(PROP_TASK_COUNT, null, this.getTaskCount());
    }

    private boolean doesContainDisplayedTasks(TaskSearcher searcher) {
        List<Task> tasks = TaskFactory.getInstance().getList();

        boolean indentSubtasks = Main.getSettings().getBooleanProperty(
                "task.indent_subtasks");

        for (Task task : tasks) {
            if (indentSubtasks) {
                if (TaskUtils.showTask(task, null, searcher.getFilter())) {
                    return true;
                }
            } else {
                if (TaskUtils.showUnindentTask(
                        task,
                        null,
                        searcher.getFilter(),
                        false)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void modelSelectionChange(ModelSelectionChangeEvent event) {
        if (this.isSelectionAdjusting)
            return;

        this.isSelectionAdjusting = true;

        for (TaskTableView table : this.tables) {
            if (table != event.getSource())
                table.setSelectedTasks(new Task[0]);
        }

        this.isSelectionAdjusting = false;

        this.modelSelectionChangeSupport.fireModelSelectionChange(event.getSelectedModels());
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(TaskTableView.PROP_TASK_COUNT)) {
            this.firePropertyChange(PROP_TASK_COUNT, null, this.getTaskCount());
        }
    }

}
