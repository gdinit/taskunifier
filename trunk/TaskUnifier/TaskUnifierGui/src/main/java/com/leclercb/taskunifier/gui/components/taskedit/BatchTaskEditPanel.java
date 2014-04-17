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
package com.leclercb.taskunifier.gui.components.taskedit;

import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.commons.gui.swing.panels.ScrollablePanel;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.Timer;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.gui.actions.ActionManageModels;
import com.leclercb.taskunifier.gui.actions.ActionPostponeTaskBeans;
import com.leclercb.taskunifier.gui.api.models.beans.GuiTaskBean;
import com.leclercb.taskunifier.gui.commons.models.*;
import com.leclercb.taskunifier.gui.commons.values.StringValueMinutes;
import com.leclercb.taskunifier.gui.components.modelnote.HTMLEditorInterface;
import com.leclercb.taskunifier.gui.components.modelnote.editors.WysiwygHTMLEditorPane;
import com.leclercb.taskunifier.gui.components.models.ModelConfigurationTab;
import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.components.taskedit.propertytable.TaskPropertyTable;
import com.leclercb.taskunifier.gui.components.taskedit.propertytable.TaskPropertyTableModel.TaskPropertyItem;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.swing.*;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.*;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;

public class BatchTaskEditPanel extends ScrollablePanel {

    private boolean changed;
    private Task[] tasks;

    private JTabbedPane tabbedPane;

    private JCheckBox taskTitleCheckBox;
    private JCheckBox taskTagsCheckBox;
    private JCheckBox taskFolderCheckBox;
    private JCheckBox taskContextCheckBox;
    private JCheckBox taskGoalCheckBox;
    private JCheckBox taskLocationCheckBox;
    private JCheckBox taskParentCheckBox;
    private JCheckBox taskProgressCheckBox;
    private JCheckBox taskCompletedCheckBox;
    private JCheckBox taskStartDateCheckBox;
    private JCheckBox taskDueDateCheckBox;
    private JCheckBox taskStartDateReminderCheckBox;
    private JCheckBox taskDueDateReminderCheckBox;
    private JCheckBox taskRepeatCheckBox;
    private JCheckBox taskRepeatFromCheckBox;
    private JCheckBox taskStatusCheckBox;
    private JCheckBox taskLengthCheckBox;
    private JCheckBox taskTimerCheckBox;
    private JCheckBox taskPriorityCheckBox;
    private JCheckBox taskStarCheckBox;
    private JCheckBox taskNoteCheckBox;

    private JTextField taskTitle;
    private TUTagList taskTags;
    private JComboBox taskFolder;
    private JComboBox taskContext;
    private TUModelListField<Context> taskContexts;
    private JComboBox taskGoal;
    private TUModelListField<Goal> taskGoals;
    private JComboBox taskLocation;
    private TUModelListField<Location> taskLocations;
    private JComboBox taskParent;
    private JSpinner taskProgress;
    private JCheckBox taskCompleted;
    private JDateChooser taskStartDate;
    private JButton taskStartDatePostponeButton;
    private JDateChooser taskDueDate;
    private JButton taskDueDatePostponeButton;
    private JComboBox taskStartDateReminder;
    private JComboBox taskDueDateReminder;
    private TURepeatField taskRepeat;
    private JComboBox taskRepeatFrom;
    private JComboBox taskStatus;
    private JSpinner taskLength;
    private TUTimerField taskTimer;
    private JComboBox taskPriority;
    private JCheckBox taskStar;
    private HTMLEditorInterface taskNote;
    private TaskPropertyTable taskPropertyTable;

    public BatchTaskEditPanel() {
        this.changed = false;
        this.tasks = null;

        this.initialize();
        this.reinitializeFields(null);
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void editTasks() {
        Synchronizing.getInstance().setSynchronizing(true);

        try {

            if (this.taskTitleCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setTitle(this.taskTitle.getText());
                }
            }

            if (this.taskTagsCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setTags(this.taskTags.getTags());
                }
            }

            if (this.taskFolderCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setFolder((Folder) this.taskFolder.getSelectedItem());
                }
            }

            if (this.taskContextCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    if (TaskColumnList.MULTIPLE_CONTEXTS) {
                        task.setContexts(this.taskContexts.getModelList());
                    } else {
                        task.getContexts().clear();

                        if (this.taskContext.getSelectedItem() != null)
                            task.getContexts().add(
                                    (Context) this.taskContext.getSelectedItem());
                    }
                }
            }

            if (this.taskGoalCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    if (TaskColumnList.MULTIPLE_GOALS) {
                        task.setGoals(this.taskGoals.getModelList());
                    } else {
                        task.getGoals().clear();

                        if (this.taskGoal.getSelectedItem() != null)
                            task.getGoals().add(
                                    (Goal) this.taskGoal.getSelectedItem());
                    }
                }
            }

            if (this.taskLocationCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    if (TaskColumnList.MULTIPLE_LOCATIONS) {
                        task.setLocations(this.taskLocations.getModelList());
                    } else {
                        task.getLocations().clear();

                        if (this.taskLocation.getSelectedItem() != null)
                            task.getLocations().add(
                                    (Location) this.taskLocation.getSelectedItem());
                    }
                }
            }

            if (this.taskParentCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    try {
                        task.setParent((Task) this.taskParent.getSelectedItem());
                    } catch (IllegalArgumentException exc) {

                    }
                }
            }

            if (this.taskProgressCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setProgress((Double) this.taskProgress.getValue());
                }
            }

            if (this.taskStartDateCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setStartDate(this.taskStartDate.getCalendar());
                }
            }

            if (this.taskDueDateCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setDueDate(this.taskDueDate.getCalendar());
                }
            }

            if (this.taskStartDateReminderCheckBox.isSelected()) {
                try {
                    int reminder = Integer.parseInt(this.taskStartDateReminder.getSelectedItem().toString());

                    for (Task task : this.tasks) {
                        task.setStartDateReminder(reminder);
                    }
                } catch (NumberFormatException exc) {

                }
            }

            if (this.taskDueDateReminderCheckBox.isSelected()) {
                try {
                    int reminder = Integer.parseInt(this.taskDueDateReminder.getSelectedItem().toString());

                    for (Task task : this.tasks) {
                        task.setDueDateReminder(reminder);
                    }
                } catch (NumberFormatException exc) {

                }
            }

            if (this.taskRepeatCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setRepeat(this.taskRepeat.getRepeat());
                }
            }

            if (this.taskRepeatFromCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setRepeatFrom((TaskRepeatFrom) this.taskRepeatFrom.getSelectedItem());
                }
            }

            if (this.taskStatusCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setStatus((TaskStatus) this.taskStatus.getSelectedItem());
                }
            }

            if (this.taskLengthCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setLength((Integer) this.taskLength.getValue());
                }
            }

            if (this.taskTimerCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setTimer(this.taskTimer.getTimer());
                }
            }

            if (this.taskPriorityCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setPriority((TaskPriority) this.taskPriority.getSelectedItem());
                }
            }

            if (this.taskStarCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setStar(this.taskStar.isSelected());
                }
            }

            if (this.taskNoteCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setNote(this.taskNote.getText());
                }
            }

            if (this.taskCompletedCheckBox.isSelected()) {
                for (Task task : this.tasks) {
                    task.setCompleted(this.taskCompleted.isSelected());
                }
            }

            for (TaskPropertyItem item : this.taskPropertyTable.getTaskPropertyTableModel().getTaskPropertyItems()) {
                if (item.isEdit()) {
                    for (Task task : this.tasks) {
                        item.getAccessor().setProperty(task, item.getValue());
                    }
                }
            }
        } catch (Throwable t) {
            GuiLogger.getLogger().log(
                    Level.SEVERE,
                    "Error while setting task field value",
                    t);
        } finally {
            Synchronizing.getInstance().setSynchronizing(false);
        }
    }

    public Task[] getTasks() {
        return this.tasks;
    }

    public void setTasks(Task[] tasks) {
        this.tasks = tasks;

        if (tasks != null && tasks.length == 1) {
            this.reinitializeFields(tasks[0]);
            this.taskTitle.requestFocus();
            this.taskTitle.selectAll();
        } else {
            this.reinitializeFields(null);
        }
    }

    private void initialize() {
        this.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        this.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);

        String dateFormat = Main.getSettings().getStringProperty(
                "date.date_format");
        String timeFormat = Main.getSettings().getStringProperty(
                "date.time_format");

        String dueDateFormat = null;
        String dueDateMask = null;

        String startDateFormat = null;
        String startDateMask = null;

        if (Main.getSettings().getBooleanProperty("date.use_due_time")) {
            dueDateFormat = dateFormat + " " + timeFormat;
            dueDateMask = DateTimeFormatUtils.getMask(dateFormat)
                    + " "
                    + DateTimeFormatUtils.getMask(timeFormat);
        } else {
            dueDateFormat = dateFormat;
            dueDateMask = DateTimeFormatUtils.getMask(dateFormat);
        }

        if (Main.getSettings().getBooleanProperty("date.use_start_time")) {
            startDateFormat = dateFormat + " " + timeFormat;
            startDateMask = DateTimeFormatUtils.getMask(dateFormat)
                    + " "
                    + DateTimeFormatUtils.getMask(timeFormat);
        } else {
            startDateFormat = dateFormat;
            startDateMask = DateTimeFormatUtils.getMask(dateFormat);
        }

        final String finalDueDateMask = dueDateMask;
        final String finalStartDateMask = startDateMask;

        // Task Start/Due Date Listener
        ActionListener postponeListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (!(evt.getSource() instanceof ActionPostponeTaskBeans))
                    return;

                ActionPostponeTaskBeans action = (ActionPostponeTaskBeans) evt.getSource();

                GuiTaskBean bean = new GuiTaskBean();
                bean.setStartDate(BatchTaskEditPanel.this.taskStartDate.getCalendar());
                bean.setDueDate(BatchTaskEditPanel.this.taskDueDate.getCalendar());

                action.postponeTaskBeans(new TaskBean[]{bean});

                BatchTaskEditPanel.this.taskStartDate.setCalendar(bean.getStartDate());
                BatchTaskEditPanel.this.taskDueDate.setCalendar(bean.getDueDate());
            }

        };

        this.setLayout(new BorderLayout(5, 5));

        this.taskTitleCheckBox = new JCheckBox("", true);
        this.taskTagsCheckBox = new JCheckBox("", true);
        this.taskFolderCheckBox = new JCheckBox("", true);
        this.taskContextCheckBox = new JCheckBox("", true);
        this.taskGoalCheckBox = new JCheckBox("", true);
        this.taskLocationCheckBox = new JCheckBox("", true);
        this.taskParentCheckBox = new JCheckBox("", true);
        this.taskProgressCheckBox = new JCheckBox("", true);
        this.taskCompletedCheckBox = new JCheckBox("", true);
        this.taskStartDateCheckBox = new JCheckBox("", true);
        this.taskDueDateCheckBox = new JCheckBox("", true);
        this.taskStartDateReminderCheckBox = new JCheckBox("", true);
        this.taskDueDateReminderCheckBox = new JCheckBox("", true);
        this.taskRepeatCheckBox = new JCheckBox("", true);
        this.taskRepeatFromCheckBox = new JCheckBox("", true);
        this.taskStatusCheckBox = new JCheckBox("", true);
        this.taskLengthCheckBox = new JCheckBox("", true);
        this.taskTimerCheckBox = new JCheckBox("", true);
        this.taskPriorityCheckBox = new JCheckBox("", true);
        this.taskStarCheckBox = new JCheckBox("", true);
        this.taskNoteCheckBox = new JCheckBox("", true);

        this.taskTitle = new JTextField();
        this.taskTags = new TUTagList();
        this.taskFolder = ComponentFactory.createModelComboBox(null, true);
        this.taskContext = ComponentFactory.createModelComboBox(null, true);
        this.taskContexts = new TUModelListField<Context>(ModelType.CONTEXT);
        this.taskGoal = ComponentFactory.createModelComboBox(null, true);
        this.taskGoals = new TUModelListField<Goal>(ModelType.GOAL);
        this.taskLocation = ComponentFactory.createModelComboBox(null, true);
        this.taskLocations = new TUModelListField<Location>(ModelType.LOCATION);
        this.taskParent = ComponentFactory.createModelComboBox(null, true);
        this.taskProgress = new JSpinner();
        this.taskCompleted = new JCheckBox();
        TUPostponeCalendar taskStartDateCal = new TUPostponeCalendar(true);
        this.taskStartDate = new JDateChooser(
                taskStartDateCal,
                null,
                null,
                new JTextFieldDateEditor(startDateFormat, null, '_') {

                    @Override
                    public String createMaskFromDatePattern(String datePattern) {
                        return finalStartDateMask;
                    }

                });
        taskStartDateCal.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (EqualsUtils.equals(
                        TUPostponeCalendar.NO_DATE_COMMAND,
                        evt.getActionCommand())) {
                    BatchTaskEditPanel.this.taskStartDate.setDate(null);
                }
            }

        });
        this.taskStartDatePostponeButton = ComponentFactory.createPostponeButton(
                16,
                16,
                postponeListener);
        TUPostponeCalendar taskDueDateCal = new TUPostponeCalendar(true);
        this.taskDueDate = new JDateChooser(
                taskDueDateCal,
                null,
                null,
                new JTextFieldDateEditor(dueDateFormat, null, '_') {

                    @Override
                    public String createMaskFromDatePattern(String datePattern) {
                        return finalDueDateMask;
                    }

                });
        taskDueDateCal.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (EqualsUtils.equals(
                        TUPostponeCalendar.NO_DATE_COMMAND,
                        evt.getActionCommand())) {
                    BatchTaskEditPanel.this.taskDueDate.setDate(null);
                }
            }

        });
        this.taskDueDatePostponeButton = ComponentFactory.createPostponeButton(
                16,
                16,
                postponeListener);
        this.taskStartDateReminder = new JComboBox();
        this.taskDueDateReminder = new JComboBox();
        this.taskRepeat = new TURepeatField();
        this.taskRepeatFrom = ComponentFactory.createTaskRepeatFromComboBox(
                null,
                true);
        this.taskStatus = ComponentFactory.createModelComboBox(null, true);
        this.taskLength = new JSpinner();
        this.taskTimer = new TUTimerField(true);
        this.taskPriority = ComponentFactory.createTaskPriorityComboBox(
                null,
                true);
        this.taskStar = new JCheckBox();
        this.taskNote = new WysiwygHTMLEditorPane("", false, null);
        this.taskPropertyTable = new TaskPropertyTable();

        this.taskTitle.getDocument().addDocumentListener(new ChangesListener());
        // this.taskTags.addActionListener(new ChangeListener());
        this.taskFolder.addItemListener(new ChangesListener());
        this.taskContext.addItemListener(new ChangesListener());
        this.taskGoal.addItemListener(new ChangesListener());
        this.taskLocation.addItemListener(new ChangesListener());
        this.taskParent.addItemListener(new ChangesListener());
        this.taskProgress.addChangeListener(new ChangesListener());
        this.taskCompleted.addItemListener(new ChangesListener());
        this.taskStartDate.addPropertyChangeListener(
                "date",
                new ChangesListener());
        this.taskDueDate.addPropertyChangeListener(
                "date",
                new ChangesListener());
        this.taskStartDateReminder.addItemListener(new ChangesListener());
        this.taskDueDateReminder.addItemListener(new ChangesListener());
        this.taskRepeat.addPropertyChangeListener(TURepeatField.PROP_REPEAT, new ChangesListener());
        this.taskRepeatFrom.addItemListener(new ChangesListener());
        this.taskStatus.addItemListener(new ChangesListener());
        this.taskLength.addChangeListener(new ChangesListener());
        this.taskTimer.addChangeListener(new ChangesListener());
        this.taskPriority.addItemListener(new ChangesListener());
        this.taskStar.addItemListener(new ChangesListener());
        this.taskNote.addPropertyChangeListener(
                HTMLEditorInterface.PROP_TEXT,
                new ChangesListener());

        this.taskTitleCheckBox.addItemListener(new EnabledActionListener(
                this.taskTitle));
        this.taskTagsCheckBox.addItemListener(new EnabledActionListener(
                this.taskTags));
        this.taskFolderCheckBox.addItemListener(new EnabledActionListener(
                this.taskFolder));
        this.taskContextCheckBox.addItemListener(new EnabledActionListener(
                this.taskContext));
        this.taskContextCheckBox.addItemListener(new EnabledActionListener(
                this.taskContexts));
        this.taskGoalCheckBox.addItemListener(new EnabledActionListener(
                this.taskGoal));
        this.taskGoalCheckBox.addItemListener(new EnabledActionListener(
                this.taskGoals));
        this.taskLocationCheckBox.addItemListener(new EnabledActionListener(
                this.taskLocation));
        this.taskLocationCheckBox.addItemListener(new EnabledActionListener(
                this.taskLocations));
        this.taskParentCheckBox.addItemListener(new EnabledActionListener(
                this.taskParent));
        this.taskProgressCheckBox.addItemListener(new EnabledActionListener(
                this.taskProgress));
        this.taskCompletedCheckBox.addItemListener(new EnabledActionListener(
                this.taskCompleted));
        this.taskStartDateCheckBox.addItemListener(new EnabledActionListener(
                this.taskStartDate));
        this.taskStartDateCheckBox.addItemListener(new EnabledActionListener(
                this.taskStartDatePostponeButton));
        this.taskDueDateCheckBox.addItemListener(new EnabledActionListener(
                this.taskDueDate));
        this.taskDueDateCheckBox.addItemListener(new EnabledActionListener(
                this.taskDueDatePostponeButton));
        this.taskStartDateReminderCheckBox.addItemListener(new EnabledActionListener(
                this.taskStartDateReminder));
        this.taskDueDateReminderCheckBox.addItemListener(new EnabledActionListener(
                this.taskDueDateReminder));
        this.taskRepeatCheckBox.addItemListener(new EnabledActionListener(
                this.taskRepeat));
        this.taskRepeatFromCheckBox.addItemListener(new EnabledActionListener(
                this.taskRepeatFrom));
        this.taskStatusCheckBox.addItemListener(new EnabledActionListener(
                this.taskStatus));
        this.taskLengthCheckBox.addItemListener(new EnabledActionListener(
                this.taskLength));
        this.taskTimerCheckBox.addItemListener(new EnabledActionListener(
                this.taskTimer));
        this.taskPriorityCheckBox.addItemListener(new EnabledActionListener(
                this.taskPriority));
        this.taskStarCheckBox.addItemListener(new EnabledActionListener(
                this.taskStar));
        this.taskNoteCheckBox.addItemListener(new EnabledActionListener(
                this.taskNote.getComponent()));

        FormBuilder builder = new FormBuilder(
                "right:pref, 4dlu, pref, 4dlu, fill:default:grow, "
                        + "10dlu, "
                        + "right:pref, 4dlu, pref, 4dlu, fill:default:grow");

        int nbInserted = 0;

        // Task Title
        if (TaskColumnList.getInstance().get(TaskColumnList.TITLE).isUsed()) {
            builder.appendI15d(
                    "general.task.title",
                    true,
                    this.taskTitleCheckBox);
            builder.getBuilder().append(this.taskTitle, 7);
        }

        // Task Star
        this.taskStar.setIcon(ImageUtils.getResourceImage(
                "checkbox_star.png",
                16,
                16));
        this.taskStar.setSelectedIcon(ImageUtils.getResourceImage(
                "checkbox_star_selected.png",
                16,
                16));

        if (TaskColumnList.getInstance().get(TaskColumnList.STAR).isUsed()) {
            nbInserted++;
            builder.appendI15d("general.task.star", true, this.taskStarCheckBox);
            builder.append(this.taskStar);
        }

        // Task Completed
        if (TaskColumnList.getInstance().get(TaskColumnList.COMPLETED).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.completed",
                    true,
                    this.taskCompletedCheckBox);
            builder.append(this.taskCompleted);
        }

        // Insert Empty Space
        if (nbInserted % 2 != 0) {
            nbInserted++;
            builder.getBuilder().append(new JLabel(), 5);
        }

        // Task Priority
        this.taskPriority.setModel(new TaskPriorityModel(false));

        if (TaskColumnList.getInstance().get(TaskColumnList.PRIORITY).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.priority",
                    true,
                    this.taskPriorityCheckBox);
            builder.append(this.taskPriority);
        }

        // Task Tags
        if (TaskColumnList.getInstance().get(TaskColumnList.TAGS).isUsed()) {
            nbInserted++;
            builder.appendI15d("general.task.tags", true, this.taskTagsCheckBox);
            builder.append(this.taskTags);
        }

        // Task Status
        this.taskStatus.setModel(new TaskStatusModel(true));

        if (TaskColumnList.getInstance().get(TaskColumnList.STATUS).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.status",
                    true,
                    this.taskStatusCheckBox);
            builder.append(this.taskStatus);
        }

        // Task Progress
        this.taskProgress.setModel(new SpinnerNumberModel(
                new Double(0.00),
                new Double(0.00),
                new Double(1.00),
                new Double(0.10)));

        this.taskProgress.setEditor(new JSpinner.NumberEditor(
                this.taskProgress,
                "##0%"));

        if (TaskColumnList.getInstance().get(TaskColumnList.PROGRESS).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.progress",
                    true,
                    this.taskProgressCheckBox);
            builder.append(this.taskProgress);
        }

        // Insert Empty Space
        if (nbInserted % 2 != 0) {
            nbInserted++;
            builder.getBuilder().append(new JLabel(), 5);
        }

        // Separator
        builder.getBuilder().appendSeparator();

        // Task Parent
        this.taskParent.setModel(new TaskModel(true));

        if (TaskColumnList.getInstance().get(TaskColumnList.PARENT).isUsed()) {
            builder.appendI15d(
                    "general.task.parent",
                    true,
                    this.taskParentCheckBox);
            builder.getBuilder().append(this.taskParent, 7);
        }

        // Task Folder
        this.taskFolder.setModel(new FolderModel(true, false));

        if (TaskColumnList.getInstance().get(TaskColumnList.FOLDER).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.folder",
                    true,
                    this.taskFolderCheckBox);
            builder.append(this.createPanel(this.taskFolder, new JButton(
                    new ActionManageModels(
                            16,
                            16,
                            ModelConfigurationTab.FOLDERS))));
        }

        // Task Goal
        if (TaskColumnList.getInstance().get(TaskColumnList.GOALS).isUsed()) {
            nbInserted++;
            builder.appendI15d("general.task.goal", true, this.taskGoalCheckBox);

            if (TaskColumnList.MULTIPLE_GOALS) {
                builder.append(this.createPanel(this.taskGoals, new JButton(
                        new ActionManageModels(
                                16,
                                16,
                                ModelConfigurationTab.GOALS))));
            } else {
                this.taskGoal.setModel(new GoalModel(true, false));
                builder.append(this.createPanel(this.taskGoal, new JButton(
                        new ActionManageModels(
                                16,
                                16,
                                ModelConfigurationTab.GOALS))));
            }
        }

        // Task Context
        if (TaskColumnList.getInstance().get(TaskColumnList.CONTEXTS).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.context",
                    true,
                    this.taskContextCheckBox);

            if (TaskColumnList.MULTIPLE_CONTEXTS) {
                builder.append(this.createPanel(this.taskContexts, new JButton(
                        new ActionManageModels(
                                16,
                                16,
                                ModelConfigurationTab.CONTEXTS))));
            } else {
                this.taskContext.setModel(new ContextModel(true));
                builder.append(this.createPanel(this.taskContext, new JButton(
                        new ActionManageModels(
                                16,
                                16,
                                ModelConfigurationTab.CONTEXTS))));
            }
        }

        // Task Location
        if (TaskColumnList.getInstance().get(TaskColumnList.LOCATIONS).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.location",
                    true,
                    this.taskLocationCheckBox);

            if (TaskColumnList.MULTIPLE_LOCATIONS) {
                builder.append(this.createPanel(
                        this.taskLocations,
                        new JButton(new ActionManageModels(
                                16,
                                16,
                                ModelConfigurationTab.LOCATIONS))));
            } else {
                this.taskLocation.setModel(new LocationModel(true));
                builder.append(this.createPanel(this.taskLocation, new JButton(
                        new ActionManageModels(
                                16,
                                16,
                                ModelConfigurationTab.LOCATIONS))));
            }
        }

        // Insert Empty Space
        if (nbInserted % 2 != 0) {
            nbInserted++;
            builder.getBuilder().append(new JLabel(), 5);
        }

        // Separator
        builder.getBuilder().appendSeparator();

        // Task Start Date
        if (TaskColumnList.getInstance().get(TaskColumnList.START_DATE).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.start_date",
                    true,
                    this.taskStartDateCheckBox);
            builder.append(this.createPanel(
                    this.taskStartDate,
                    this.taskStartDatePostponeButton));
        }

        // Task Due Date
        if (TaskColumnList.getInstance().get(TaskColumnList.DUE_DATE).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.due_date",
                    true,
                    this.taskDueDateCheckBox);
            builder.append(this.createPanel(
                    this.taskDueDate,
                    this.taskDueDatePostponeButton));
        }

        // Task Start Date Reminder
        this.taskStartDateReminder.setModel(new MinutesModel());

        this.taskStartDateReminder.setRenderer(new DefaultListRenderer(
                StringValueMinutes.INSTANCE));

        this.taskStartDateReminder.setEditable(true);

        if (TaskColumnList.getInstance().get(TaskColumnList.START_DATE_REMINDER).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.start_date_reminder",
                    true,
                    this.taskStartDateReminderCheckBox);
            builder.append(this.taskStartDateReminder);
        }

        // Task Due Date Reminder
        this.taskDueDateReminder.setModel(new MinutesModel());

        this.taskDueDateReminder.setRenderer(new DefaultListRenderer(
                StringValueMinutes.INSTANCE));

        this.taskDueDateReminder.setEditable(true);

        if (TaskColumnList.getInstance().get(TaskColumnList.DUE_DATE_REMINDER).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.due_date_reminder",
                    true,
                    this.taskDueDateReminderCheckBox);
            builder.append(this.taskDueDateReminder);
        }

        // Task Length
        this.taskLength.setModel(new TUSpinnerTimeModel());
        this.taskLength.setEditor(new TUSpinnerTimeEditor(this.taskLength));

        if (TaskColumnList.getInstance().get(TaskColumnList.LENGTH).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.length",
                    true,
                    this.taskLengthCheckBox);
            builder.append(this.taskLength);
        }

        // Task Timer
        if (TaskColumnList.getInstance().get(TaskColumnList.TIMER).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.timer",
                    true,
                    this.taskTimerCheckBox);
            builder.append(this.taskTimer);
        }

        // Task Repeat
        if (TaskColumnList.getInstance().get(TaskColumnList.REPEAT).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.repeat",
                    true,
                    this.taskRepeatCheckBox);
            builder.append(this.taskRepeat);
        }

        // Task Repeat From
        this.taskRepeatFrom.setModel(new TaskRepeatFromModel(false));

        if (TaskColumnList.getInstance().get(TaskColumnList.REPEAT_FROM).isUsed()) {
            nbInserted++;
            builder.appendI15d(
                    "general.task.repeat_from",
                    true,
                    this.taskRepeatFromCheckBox);
            builder.append(this.taskRepeatFrom);
        }

        // Insert Empty Space
        if (nbInserted % 2 != 0) {
            nbInserted++;
            builder.getBuilder().append(new JLabel(), 5);
        }

        // Separator
        builder.getBuilder().appendSeparator();

        // Task Note
        this.taskNote.getComponent().setBorder(
                BorderFactory.createLineBorder(Color.GRAY));

        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        notePanel.add(this.taskNoteCheckBox, BorderLayout.WEST);
        notePanel.add(this.taskNote.getComponent(), BorderLayout.CENTER);

        notePanel.setPreferredSize(new Dimension(100, 120));

        // Lay out the panel
        JPanel tabMain = new JPanel();
        tabMain.setLayout(new BorderLayout());

        tabMain.add(builder.getPanel(), BorderLayout.NORTH);

        if (TaskColumnList.getInstance().get(TaskColumnList.NOTE).isUsed()) {
            tabMain.add(notePanel, BorderLayout.CENTER);
        }

        if (TaskCustomColumnList.getInstance().getInitialPropertyAccessors().size() != 0) {
            JPanel taskPropertyPanel = new JPanel();
            taskPropertyPanel.setLayout(new BorderLayout());
            taskPropertyPanel.add(
                    this.taskPropertyTable.getTableHeader(),
                    BorderLayout.NORTH);
            taskPropertyPanel.add(this.taskPropertyTable, BorderLayout.CENTER);

            JPanel tabProperties = new JPanel();
            tabProperties.setLayout(new BorderLayout());
            tabProperties.add(
                    ComponentFactory.createJScrollPane(taskPropertyPanel, true),
                    BorderLayout.CENTER);

            tabMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            tabProperties.setBorder(BorderFactory.createEmptyBorder(
                    10,
                    10,
                    10,
                    10));

            this.tabbedPane = new JTabbedPane();

            this.tabbedPane.addTab(
                    Translations.getString("general.main"),
                    tabMain);
            this.tabbedPane.addTab(
                    Translations.getString("general.custom_columns"),
                    tabProperties);
            this.add(this.tabbedPane, BorderLayout.CENTER);
        } else {
            this.add(tabMain, BorderLayout.CENTER);
        }

    }

    public void reinitializeFields(Task task) {
        boolean visible = true;
        boolean selected = false;

        if (task == null) {
            visible = true;
            selected = false;

            this.taskTitle.setText("");
            this.taskTags.setTags("");
            this.taskFolder.setSelectedItem(null);
            this.taskContext.setSelectedItem(null);
            this.taskContexts.setModelList(null);
            this.taskGoal.setSelectedItem(null);
            this.taskGoals.setModelList(null);
            this.taskLocation.setSelectedItem(null);
            this.taskLocations.setModelList(null);
            this.taskParent.setSelectedItem(null);
            this.taskProgress.setValue(0.0);
            this.taskCompleted.setSelected(false);
            this.taskStartDate.setCalendar(null);
            this.taskDueDate.setCalendar(null);
            this.taskStartDateReminder.setSelectedItem(0);
            this.taskDueDateReminder.setSelectedItem(0);
            this.taskRepeat.setRepeat(null);
            this.taskRepeatFrom.setSelectedItem(TaskRepeatFrom.DUE_DATE);
            this.taskStatus.setSelectedItem(null);
            this.taskLength.setValue(0);
            this.taskTimer.setTimer(new Timer());
            this.taskPriority.setSelectedItem(TaskPriority.NEGATIVE);
            this.taskStar.setSelected(false);

            this.taskNote.setText("", true, true);

            this.taskPropertyTable.setTask(task);
        } else {
            visible = false;
            selected = true;

            this.taskTitle.setText(task.getTitle());
            this.taskTags.setTags(task.getTags());
            this.taskFolder.setSelectedItem(task.getFolder());

            if (TaskColumnList.MULTIPLE_CONTEXTS) {
                this.taskContexts.setModelList(task.getContexts());
            } else {
                if (task.getContexts().size() > 0)
                    this.taskContext.setSelectedItem(task.getContexts().get(0));
                else
                    this.taskContext.setSelectedItem(null);
            }

            if (TaskColumnList.MULTIPLE_GOALS) {
                this.taskGoals.setModelList(task.getGoals());
            } else {
                if (task.getGoals().size() > 0)
                    this.taskGoal.setSelectedItem(task.getGoals().get(0));
                else
                    this.taskGoal.setSelectedItem(null);
            }

            if (TaskColumnList.MULTIPLE_LOCATIONS) {
                this.taskLocations.setModelList(task.getLocations());
            } else {
                if (task.getLocations().size() > 0)
                    this.taskLocation.setSelectedItem(task.getLocations().get(0));
                else
                    this.taskLocation.setSelectedItem(null);
            }

            this.taskParent.setSelectedItem(task.getParent());
            this.taskProgress.setValue(task.getProgress());
            this.taskCompleted.setSelected(task.isCompleted());
            this.taskStartDate.setCalendar(task.getStartDate());
            this.taskDueDate.setCalendar(task.getDueDate());
            this.taskStartDateReminder.setSelectedItem(task.getStartDateReminder());
            this.taskDueDateReminder.setSelectedItem(task.getDueDateReminder());
            this.taskRepeat.setRepeat(task.getRepeat());
            this.taskRepeatFrom.setSelectedItem(task.getRepeatFrom());
            this.taskStatus.setSelectedItem(task.getStatus());
            this.taskLength.setValue(task.getLength());
            this.taskTimer.setTimer(task.getTimer());
            this.taskPriority.setSelectedItem(task.getPriority());
            this.taskStar.setSelected(task.isStar());

            this.taskNote.setText(task.getNote(), true, true);

            this.taskPropertyTable.setTask(task);
        }

        this.taskTitleCheckBox.setSelected(selected);
        this.taskTagsCheckBox.setSelected(selected);
        this.taskFolderCheckBox.setSelected(selected);
        this.taskContextCheckBox.setSelected(selected);
        this.taskGoalCheckBox.setSelected(selected);
        this.taskLocationCheckBox.setSelected(selected);
        this.taskParentCheckBox.setSelected(selected);
        this.taskProgressCheckBox.setSelected(selected);
        this.taskCompletedCheckBox.setSelected(selected);
        this.taskStartDateCheckBox.setSelected(selected);
        this.taskDueDateCheckBox.setSelected(selected);
        this.taskStartDateReminderCheckBox.setSelected(selected);
        this.taskDueDateReminderCheckBox.setSelected(selected);
        this.taskRepeatCheckBox.setSelected(selected);
        this.taskRepeatFromCheckBox.setSelected(selected);
        this.taskStatusCheckBox.setSelected(selected);
        this.taskLengthCheckBox.setSelected(selected);
        this.taskTimerCheckBox.setSelected(selected);
        this.taskPriorityCheckBox.setSelected(selected);
        this.taskStarCheckBox.setSelected(selected);
        this.taskNoteCheckBox.setSelected(selected);

        this.taskTitleCheckBox.setVisible(visible);
        this.taskTagsCheckBox.setVisible(visible);
        this.taskFolderCheckBox.setVisible(visible);
        this.taskContextCheckBox.setVisible(visible);
        this.taskGoalCheckBox.setVisible(visible);
        this.taskLocationCheckBox.setVisible(visible);
        this.taskParentCheckBox.setVisible(visible);
        this.taskProgressCheckBox.setVisible(visible);
        this.taskCompletedCheckBox.setVisible(visible);
        this.taskStartDateCheckBox.setVisible(visible);
        this.taskDueDateCheckBox.setVisible(visible);
        this.taskStartDateReminderCheckBox.setVisible(visible);
        this.taskDueDateReminderCheckBox.setVisible(visible);
        this.taskRepeatCheckBox.setVisible(visible);
        this.taskRepeatFromCheckBox.setVisible(visible);
        this.taskStatusCheckBox.setVisible(visible);
        this.taskLengthCheckBox.setVisible(visible);
        this.taskTimerCheckBox.setVisible(visible);
        this.taskPriorityCheckBox.setVisible(visible);
        this.taskStarCheckBox.setVisible(visible);
        this.taskNoteCheckBox.setVisible(visible);

        this.changed = false;
    }

    private JPanel createPanel(JComponent component, JButton button) {
        button.setText("");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);

        return panel;
    }

    private class EnabledActionListener extends ChangesListener {

        private JComponent component;

        public EnabledActionListener(JComponent component) {
            this.component = component;
        }

        @Override
        public void itemStateChanged(ItemEvent event) {
            super.itemStateChanged(event);

            if (event.getStateChange() == ItemEvent.SELECTED)
                this.component.setEnabled(true);
            else if (event.getStateChange() == ItemEvent.DESELECTED)
                this.component.setEnabled(false);
        }

    }

    private class ChangesListener implements ActionListener, ChangeListener, DocumentListener, ItemListener, PropertyChangeListener {

        public ChangesListener() {

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BatchTaskEditPanel.this.changed = true;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            BatchTaskEditPanel.this.changed = true;
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            BatchTaskEditPanel.this.changed = true;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            BatchTaskEditPanel.this.changed = true;
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            BatchTaskEditPanel.this.changed = true;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            BatchTaskEditPanel.this.changed = true;
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            BatchTaskEditPanel.this.changed = true;
        }

    }

}
