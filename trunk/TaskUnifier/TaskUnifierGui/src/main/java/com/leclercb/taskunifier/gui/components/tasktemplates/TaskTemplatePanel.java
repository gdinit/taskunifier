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
package com.leclercb.taskunifier.gui.components.tasktemplates;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.ComboBoxAdapter;
import com.jgoodies.binding.adapter.SpinnerAdapterFactory;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.value.BindingConverter;
import com.jgoodies.binding.value.ConverterValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.leclercb.commons.gui.swing.panels.ScrollablePanel;
import com.leclercb.commons.gui.utils.FormatterUtils;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.api.models.templates.TaskTemplate;
import com.leclercb.taskunifier.gui.actions.ActionManageModels;
import com.leclercb.taskunifier.gui.commons.converters.TemplateTimeConverter;
import com.leclercb.taskunifier.gui.commons.models.*;
import com.leclercb.taskunifier.gui.commons.values.StringValueMinutes;
import com.leclercb.taskunifier.gui.components.modelnote.HTMLEditorInterface;
import com.leclercb.taskunifier.gui.components.modelnote.editors.WysiwygHTMLEditorPane;
import com.leclercb.taskunifier.gui.components.models.ModelConfigurationTab;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.ShortcutKey;
import com.leclercb.taskunifier.gui.swing.TUModelListField;
import com.leclercb.taskunifier.gui.swing.TURepeatField;
import com.leclercb.taskunifier.gui.swing.TUShortcutField;
import com.leclercb.taskunifier.gui.swing.TUSpinnerTimeEditor;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.FormBuilder;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TaskTemplatePanel extends ScrollablePanel {

    private BeanAdapter<TaskTemplate> adapter;

    private TaskTemplate template;

    private JTextField templateTitle;
    private TUShortcutField templateShortcut;

    private JTextField taskTitle;
    private JTextField taskTags;
    private JComboBox taskFolder;
    private TUModelListField<Context> taskContexts;
    private TUModelListField<Goal> taskGoals;
    private TUModelListField<Location> taskLocations;
    private JSpinner taskProgress;
    private JCheckBox taskCompleted;
    private JFormattedTextField taskStartDate;
    private JSpinner taskStartTime;
    private JFormattedTextField taskDueDate;
    private JSpinner taskDueTime;
    private JComboBox taskStartDateReminder;
    private JComboBox taskDueDateReminder;
    private TURepeatField taskRepeat;
    private JComboBox taskRepeatFrom;
    private JComboBox taskStatus;
    private JSpinner taskLength;
    private JComboBox taskPriority;
    private JCheckBox taskStar;
    private WysiwygHTMLEditorPane taskNote;

    public TaskTemplatePanel() {
        this.initialize();
        this.setTemplate(null);
    }

    public JTextField getTemplateTitle() {
        return this.templateTitle;
    }

    public TaskTemplate getTemplate() {
        return this.template;
    }

    public void setTemplate(TaskTemplate template) {
        this.template = template;

        this.adapter.setBean(template != null ? template : null);

        if (template != null) {
            this.templateShortcut.setShortcutKey(template.getProperties().getObjectProperty("shortcut", ShortcutKey.class));
        } else {
            this.templateShortcut.setShortcutKey(null);
        }

        TaskTemplatePanel.this.templateTitle.setEnabled(template != null);
        TaskTemplatePanel.this.templateShortcut.setEnabled(template != null);
        TaskTemplatePanel.this.taskTitle.setEnabled(template != null);
        TaskTemplatePanel.this.taskTags.setEnabled(template != null);
        TaskTemplatePanel.this.taskFolder.setEnabled(template != null);
        TaskTemplatePanel.this.taskContexts.setEnabled(template != null);
        TaskTemplatePanel.this.taskGoals.setEnabled(template != null);
        TaskTemplatePanel.this.taskLocations.setEnabled(template != null);
        TaskTemplatePanel.this.taskProgress.setEnabled(template != null);
        TaskTemplatePanel.this.taskCompleted.setEnabled(template != null);
        TaskTemplatePanel.this.taskDueDate.setEnabled(template != null);
        TaskTemplatePanel.this.taskDueTime.setEnabled(template != null);
        TaskTemplatePanel.this.taskStartDate.setEnabled(template != null);
        TaskTemplatePanel.this.taskStartTime.setEnabled(template != null);
        TaskTemplatePanel.this.taskDueDateReminder.setEnabled(template != null);
        TaskTemplatePanel.this.taskStartDateReminder.setEnabled(template != null);
        TaskTemplatePanel.this.taskRepeat.setEnabled(template != null);
        TaskTemplatePanel.this.taskRepeatFrom.setEnabled(template != null);
        TaskTemplatePanel.this.taskStatus.setEnabled(template != null);
        TaskTemplatePanel.this.taskLength.setEnabled(template != null);
        TaskTemplatePanel.this.taskPriority.setEnabled(template != null);
        TaskTemplatePanel.this.taskStar.setEnabled(template != null);
        TaskTemplatePanel.this.taskNote.setEnabled(template != null);
    }

    private void initialize() {
        this.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        this.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);

        this.templateTitle = new JTextField();
        this.templateShortcut = new TUShortcutField();

        this.taskTitle = new JTextField();
        this.taskTags = new JTextField();
        this.taskFolder = ComponentFactory.createModelComboBox(null, true);
        this.taskContexts = new TUModelListField<Context>(ModelType.CONTEXT);
        this.taskGoals = new TUModelListField<Goal>(ModelType.GOAL);
        this.taskLocations = new TUModelListField<Location>(ModelType.LOCATION);
        this.taskProgress = new JSpinner();
        this.taskCompleted = new JCheckBox();
        this.taskStartDate = new JFormattedTextField(
                FormatterUtils.getIntegerFormatter());
        this.taskStartTime = new JSpinner();
        this.taskDueDate = new JFormattedTextField(
                FormatterUtils.getIntegerFormatter());
        this.taskDueTime = new JSpinner();
        this.taskStartDateReminder = new JComboBox();
        this.taskDueDateReminder = new JComboBox();
        this.taskRepeat = new TURepeatField();
        this.taskRepeatFrom = ComponentFactory.createTaskRepeatFromComboBox(
                null,
                true);
        this.taskStatus = ComponentFactory.createModelComboBox(null, true);
        this.taskLength = new JSpinner();
        this.taskPriority = ComponentFactory.createTaskPriorityComboBox(
                null,
                true);
        this.taskStar = new JCheckBox();
        this.taskNote = new WysiwygHTMLEditorPane("", false, null);

        this.setLayout(new BorderLayout());

        FormBuilder builder = new FormBuilder(
                "right:pref, 4dlu, fill:default:grow, "
                        + "10dlu, "
                        + "right:pref, 4dlu, fill:default:grow");

        // Disable
        this.templateTitle.setEnabled(false);
        this.templateShortcut.setEnabled(false);
        this.taskTitle.setEnabled(false);
        this.taskTags.setEnabled(false);
        this.taskFolder.setEnabled(false);
        this.taskContexts.setEnabled(false);
        this.taskGoals.setEnabled(false);
        this.taskLocations.setEnabled(false);
        this.taskProgress.setEnabled(false);
        this.taskCompleted.setEnabled(false);
        this.taskDueDate.setEnabled(false);
        this.taskDueTime.setEnabled(false);
        this.taskStartDate.setEnabled(false);
        this.taskStartTime.setEnabled(false);
        this.taskDueDateReminder.setEnabled(false);
        this.taskStartDateReminder.setEnabled(false);
        this.taskRepeat.setEnabled(false);
        this.taskRepeatFrom.setEnabled(false);
        this.taskStatus.setEnabled(false);
        this.taskLength.setEnabled(false);
        this.taskPriority.setEnabled(false);
        this.taskStar.setEnabled(false);
        this.taskNote.setEnabled(false);

        // Template title
        builder.appendI15d("general.template.title", true);
        builder.getBuilder().append(this.templateTitle, 5);

        // Template shortcut
        builder.appendI15d(
                "general.template.shortcut",
                true,
                this.templateShortcut);

        // Empty
        builder.getBuilder().append("", new JLabel());

        // Separator
        builder.getBuilder().appendSeparator();

        // Task Title
        builder.appendI15d("general.task.title", true);
        builder.getBuilder().append(this.taskTitle, 5);

        // Task Star
        this.taskStar.setIcon(ImageUtils.getResourceImage(
                "checkbox_star.png",
                16,
                16));
        this.taskStar.setSelectedIcon(ImageUtils.getResourceImage(
                "checkbox_star_selected.png",
                16,
                16));

        builder.appendI15d("general.task.star", true, this.taskStar);

        // Task Completed
        builder.appendI15d("general.task.completed", true, this.taskCompleted);

        // Task Priority
        builder.appendI15d("general.task.priority", true, this.taskPriority);

        // Task Tags
        builder.appendI15d("general.task.tags", true, this.taskTags);

        // Task Status
        builder.appendI15d("general.task.status", true, this.taskStatus);

        // Task Progress
        builder.appendI15d("general.task.progress", true, this.taskProgress);

        // Separator
        builder.getBuilder().appendSeparator();

        // Task Folder
        builder.appendI15d("general.task.folder", true, this.createPanel(
                this.taskFolder,
                new JButton(new ActionManageModels(
                        16,
                        16,
                        ModelConfigurationTab.FOLDERS))));

        // Task Goal
        builder.appendI15d("general.task.goal", true, this.createPanel(
                this.taskGoals,
                new JButton(new ActionManageModels(
                        16,
                        16,
                        ModelConfigurationTab.GOALS))));

        // Task Context
        builder.appendI15d("general.task.context", true, this.createPanel(
                this.taskContexts,
                new JButton(new ActionManageModels(
                        16,
                        16,
                        ModelConfigurationTab.CONTEXTS))));

        // Task Location
        builder.appendI15d("general.task.location", true, this.createPanel(
                this.taskLocations,
                new JButton(new ActionManageModels(
                        16,
                        16,
                        ModelConfigurationTab.LOCATIONS))));

        // Separator
        builder.getBuilder().appendSeparator();

        // Task Start Date
        JPanel taskStartDatePanel = new JPanel(new BorderLayout(10, 0));

        builder.appendI15d("general.task.start_date", true, taskStartDatePanel);

        taskStartDatePanel.add(this.taskStartDate, BorderLayout.CENTER);
        taskStartDatePanel.add(this.taskStartTime, BorderLayout.EAST);

        // Task Due Date
        JPanel taskDueDatePanel = new JPanel(new BorderLayout(10, 0));

        builder.appendI15d("general.task.due_date", true, taskDueDatePanel);

        taskDueDatePanel.add(this.taskDueDate, BorderLayout.CENTER);
        taskDueDatePanel.add(this.taskDueTime, BorderLayout.EAST);

        // Task Start Date Reminder
        this.taskStartDateReminder.setRenderer(new DefaultListRenderer(
                StringValueMinutes.INSTANCE));

        this.taskStartDateReminder.setEditable(true);

        builder.appendI15d(
                "general.task.start_date_reminder",
                true,
                this.taskStartDateReminder);

        // Task Due Date Reminder
        this.taskDueDateReminder.setRenderer(new DefaultListRenderer(
                StringValueMinutes.INSTANCE));

        this.taskDueDateReminder.setEditable(true);

        builder.appendI15d(
                "general.task.due_date_reminder",
                true,
                this.taskDueDateReminder);

        // Task Length
        builder.appendI15d("general.task.length", true, this.taskLength);

        // Task Repeat
        builder.appendI15d("general.task.repeat", true, this.taskRepeat);

        // Task Repeat From
        builder.appendI15d(
                "general.task.repeat_from",
                true,
                this.taskRepeatFrom);

        // Empty
        builder.getBuilder().append("", new JLabel());

        // Separator
        builder.getBuilder().appendSeparator();

        // Task Note
        this.taskNote.getComponent().setBorder(
                BorderFactory.createLineBorder(Color.GRAY));

        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        notePanel.add(this.taskNote.getComponent(), BorderLayout.CENTER);

        notePanel.setPreferredSize(new Dimension(100, 120));

        // Lay out the panel
        this.add(builder.getPanel(), BorderLayout.NORTH);
        this.add(notePanel, BorderLayout.CENTER);

        this.initializeAdapter();
    }

    private void initializeAdapter() {
        this.adapter = new BeanAdapter<TaskTemplate>((TaskTemplate) null, true);

        ValueModel titleModel = this.adapter.getValueModel(BasicModel.PROP_TITLE);
        Bindings.bind(this.templateTitle, titleModel);

        //ValueModel shortcutModel = this.adapter.getValueModel(TUShortcutField.PROP_SHORTCUT_KEY);
        //TemplateShortcutKeyConverter shortcutConverter = new TemplateShortcutKeyConverter(shortcutModel);
        //Bindings.bind(TaskTemplatePanel.this.templateShortcut, new ConverterValueModel(shortcutModel, shortcutConverter));

        this.templateShortcut.addPropertyChangeListener(TUShortcutField.PROP_SHORTCUT_KEY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (TaskTemplatePanel.this.template == null)
                    return;

                TaskTemplatePanel.this.template.getProperties().setObjectProperty(
                        "shortcut",
                        ShortcutKey.class,
                        (ShortcutKey) propertyChangeEvent.getNewValue());
            }

        });

        ValueModel taskTitleModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_TITLE);
        Bindings.bind(TaskTemplatePanel.this.taskTitle, taskTitleModel);

        ValueModel taskTagsModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_TAGS);
        Bindings.bind(TaskTemplatePanel.this.taskTags, taskTagsModel);

        ValueModel taskFolderModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_FOLDER);
        TaskTemplatePanel.this.taskFolder.setModel(new ComboBoxAdapter<Folder>(
                new FolderModel(true, false),
                taskFolderModel));

        ValueModel taskContextsModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_CONTEXTS);
        Bindings.bind(
                TaskTemplatePanel.this.taskContexts,
                TUModelListField.PROP_MODELLIST,
                taskContextsModel);

        ValueModel taskGoalsModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_GOALS);
        Bindings.bind(
                TaskTemplatePanel.this.taskGoals,
                TUModelListField.PROP_MODELLIST,
                taskGoalsModel);

        ValueModel taskLocationsModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_LOCATIONS);
        Bindings.bind(
                TaskTemplatePanel.this.taskLocations,
                TUModelListField.PROP_MODELLIST,
                taskLocationsModel);

        ValueModel taskProgressModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_PROGRESS);
        SpinnerNumberModel taskProgressSpinnerModel = SpinnerAdapterFactory.createNumberAdapter(
                taskProgressModel,
                new Double(0.00),
                new Double(0.00),
                new Double(1.00),
                new Double(0.10));

        TaskTemplatePanel.this.taskProgress.setModel(taskProgressSpinnerModel);
        TaskTemplatePanel.this.taskProgress.setEditor(new JSpinner.NumberEditor(
                TaskTemplatePanel.this.taskProgress,
                "##0%"));

        ValueModel taskCompletedModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_COMPLETED);
        Bindings.bind(TaskTemplatePanel.this.taskCompleted, taskCompletedModel);

        ValueModel taskDueDateModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_DUE_DATE);
        Bindings.bind(TaskTemplatePanel.this.taskDueDate, taskDueDateModel);

        ValueModel taskDueTimeModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_DUE_TIME);
        TemplateTimeConverter taskDueTimeConverter = new TemplateTimeConverter();
        SpinnerDateModel taskDueTimeSpinnerModel = SpinnerAdapterFactory.createDateAdapter(
                new ConverterValueModel(taskDueTimeModel, taskDueTimeConverter),
                taskDueTimeConverter.targetValue(0));

        TaskTemplatePanel.this.taskDueTime.setModel(taskDueTimeSpinnerModel);
        TaskTemplatePanel.this.taskDueTime.setEditor(new JSpinner.DateEditor(
                TaskTemplatePanel.this.taskDueTime,
                Main.getSettings().getStringProperty("date.time_format")));

        ValueModel taskStartDateModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_START_DATE);
        Bindings.bind(TaskTemplatePanel.this.taskStartDate, taskStartDateModel);

        ValueModel taskStartTimeModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_START_TIME);
        TemplateTimeConverter taskStartTimeConverter = new TemplateTimeConverter();
        SpinnerDateModel taskStartTimeSpinnerModel = SpinnerAdapterFactory.createDateAdapter(
                new ConverterValueModel(taskStartTimeModel, taskStartTimeConverter),
                taskStartTimeConverter.targetValue(0));

        TaskTemplatePanel.this.taskStartTime.setModel(taskStartTimeSpinnerModel);
        TaskTemplatePanel.this.taskStartTime.setEditor(new JSpinner.DateEditor(
                TaskTemplatePanel.this.taskStartTime,
                Main.getSettings().getStringProperty("date.time_format")));

        ValueModel taskDueDateReminderModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_DUE_DATE_REMINDER);
        StringToIntegerConverter taskDueDateReminderConverter = new StringToIntegerConverter();
        TaskTemplatePanel.this.taskDueDateReminder.setModel(new ComboBoxAdapter<Integer>(
                new MinutesModel(),
                new ConverterValueModel(taskDueDateReminderModel, taskDueDateReminderConverter)));

        ValueModel taskStartDateReminderModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_START_DATE_REMINDER);
        StringToIntegerConverter taskStartDateReminderConverter = new StringToIntegerConverter();
        TaskTemplatePanel.this.taskStartDateReminder.setModel(new ComboBoxAdapter<Integer>(
                new MinutesModel(),
                new ConverterValueModel(taskStartDateReminderModel, taskStartDateReminderConverter)));

        ValueModel taskRepeatModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_REPEAT);
        Bindings.bind(
                TaskTemplatePanel.this.taskRepeat,
                TURepeatField.PROP_REPEAT,
                taskRepeatModel);

        ValueModel taskRepeatFromModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_REPEAT_FROM);
        TaskTemplatePanel.this.taskRepeatFrom.setModel(new ComboBoxAdapter<TaskRepeatFrom>(
                new TaskRepeatFromModel(true),
                taskRepeatFromModel));

        ValueModel taskStatusModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_STATUS);
        TaskTemplatePanel.this.taskStatus.setModel(new ComboBoxAdapter<TaskStatus>(
                new TaskStatusModel(true),
                taskStatusModel));

        ValueModel taskLengthModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_LENGTH);
        SpinnerNumberModel taskLengthSpinnerModel = SpinnerAdapterFactory.createNumberAdapter(
                taskLengthModel,
                0,
                0,
                5760,
                1);

        TaskTemplatePanel.this.taskLength.setModel(taskLengthSpinnerModel);
        TaskTemplatePanel.this.taskLength.setEditor(new TUSpinnerTimeEditor(
                TaskTemplatePanel.this.taskLength));

        ValueModel taskPriorityModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_PRIORITY);
        TaskTemplatePanel.this.taskPriority.setModel(new ComboBoxAdapter<TaskPriority>(
                new TaskPriorityModel(true),
                taskPriorityModel));

        ValueModel taskStarModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_STAR);
        Bindings.bind(TaskTemplatePanel.this.taskStar, taskStarModel);

        ValueModel taskNoteModel = this.adapter.getValueModel(TaskTemplate.PROP_TASK_NOTE);
        Bindings.bind(
                TaskTemplatePanel.this.taskNote,
                HTMLEditorInterface.PROP_TEXT,
                taskNoteModel);
    }

    private JPanel createPanel(JComponent component, JButton button) {
        button.setText("");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);

        return panel;
    }

    private static class StringToIntegerConverter implements BindingConverter<Integer, Object> {

        @Override
        public Integer sourceValue(Object value) {
            if (value == null) {
                return null;
            }

            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException exc) {
                return null;
            }
        }

        @Override
        public Object targetValue(Integer value) {
            return value;
        }

    }

}
