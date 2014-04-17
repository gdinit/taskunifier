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
package com.leclercb.taskunifier.gui.components.tasks;

import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.Timer;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.api.models.repeat.Repeat;
import com.leclercb.taskunifier.gui.api.accessor.DefaultPropertyAccessor;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorList;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;
import com.leclercb.taskunifier.gui.api.models.GuiTask;
import com.leclercb.taskunifier.gui.commons.editors.TitleEditor;
import com.leclercb.taskunifier.gui.commons.renderers.TaskShowChildrenRenderer;
import com.leclercb.taskunifier.gui.commons.values.*;
import com.leclercb.taskunifier.gui.components.modelnote.converters.HTML2Text;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.TaskCustomColumnList;
import com.leclercb.taskunifier.gui.utils.TaskUtils;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValues;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.Calendar;
import java.util.List;

public class TaskColumnList extends PropertyAccessorList<Task> {

    public static final String MODEL = "MODEL";
    public static final String MODEL_EDIT = "MODEL_EDIT";
    public static final String MODEL_CREATION_DATE = "MODEL_CREATION_DATE";
    public static final String MODEL_UPDATE_DATE = "MODEL_UPDATE_DATE";
    public static final String SHOW_CHILDREN = "SHOW_CHILDREN";
    public static final String TITLE = "TITLE";
    public static final String ORDER = "ORDER";
    public static final String TAGS = "TAGS";
    public static final String FOLDER = "FOLDER";
    public static final String CONTEXTS = "CONTEXTS";
    public static final String GOALS = "GOALS";
    public static final String LOCATIONS = "LOCATIONS";
    public static final String PARENT = "PARENT";
    public static final String PROGRESS = "PROGRESS";
    public static final String COMPLETED = "COMPLETED";
    public static final String COMPLETED_ON = "COMPLETED_ON";
    public static final String DUE_DATE = "DUE_DATE";
    public static final String START_DATE = "START_DATE";
    public static final String DUE_DATE_REMINDER = "DUE_DATE_REMINDER";
    public static final String START_DATE_REMINDER = "START_DATE_REMINDER";
    public static final String REPEAT = "REPEAT";
    public static final String REPEAT_FROM = "REPEAT_FROM";
    public static final String STATUS = "STATUS";
    public static final String LENGTH = "LENGTH";
    public static final String TIMER = "TIMER";
    public static final String PRIORITY = "PRIORITY";
    public static final String STAR = "STAR";
    public static final String IMPORTANCE = "IMPORTANCE";
    public static final String NOTE = "NOTE";
    public static final String CONTACTS = "CONTACTS";
    public static final String TASKS = "TASKS";
    public static final String FILES = "FILES";

    private static TaskColumnList INSTANCE;

    public static TaskColumnList getInstance() {
        if (INSTANCE == null)
            INSTANCE = new TaskColumnList();

        return INSTANCE;
    }

    public static final Boolean MULTIPLE_CONTEXTS = Main.getSettings().getBooleanProperty(
            "theme.task.field.contexts.multiple");
    public static final Boolean MULTIPLE_GOALS = Main.getSettings().getBooleanProperty(
            "theme.task.field.goals.multiple");
    public static final Boolean MULTIPLE_LOCATIONS = Main.getSettings().getBooleanProperty(
            "theme.task.field.locations.multiple");

    private TaskColumnList() {
        super(NOTE);

        this.initialize();
    }

    private void initialize() {
        this.add(new DefaultPropertyAccessor<Task>(
                "MODEL",
                "task.field.model",
                PropertyAccessorType.TASK,
                BasicModel.PROP_MODEL_ID,
                Translations.getString("general.task.id"),
                false,
                true,
                true) {

            private TableCellRenderer renderer;

            @Override
            public TableCellRenderer getCellRenderer() {
                if (this.renderer == null) {
                    this.renderer = new DefaultTableRenderer(
                            StringValueModelId.INSTANCE);
                }

                return this.renderer;
            }

            @Override
            public Object getProperty(Task model) {
                return model;
            }

            @Override
            public void setProperty(Task model, Object value) {

            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "MODEL_EDIT",
                "task.field.model_edit",
                PropertyAccessorType.VOID,
                null,
                Translations.getString("general.edit"),
                false,
                false,
                false) {

            private TableCellRenderer renderer;

            @Override
            public TableCellRenderer getCellRenderer() {
                if (this.renderer == null) {
                    this.renderer = new DefaultTableRenderer(new MappedValue(
                            null,
                            IconValueEdit.INSTANCE), SwingConstants.CENTER);
                }

                return this.renderer;
            }

            @Override
            public Object getProperty(Task model) {
                return null;
            }

            @Override
            public void setProperty(Task model, Object value) {

            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "MODEL_CREATION_DATE",
                "task.field.model_creation_date",
                PropertyAccessorType.CALENDAR_DATE_TIME,
                BasicModel.PROP_MODEL_CREATION_DATE,
                Translations.getString("general.creation_date"),
                false,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getModelCreationDate();
            }

            @Override
            public void setProperty(Task model, Object value) {

            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "MODEL_UPDATE_DATE",
                "task.field.model_update_date",
                PropertyAccessorType.CALENDAR_DATE_TIME,
                BasicModel.PROP_MODEL_UPDATE_DATE,
                Translations.getString("general.update_date"),
                false,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getModelUpdateDate();
            }

            @Override
            public void setProperty(Task model, Object value) {

            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "SHOW_CHILDREN",
                "task.field.show_children",
                PropertyAccessorType.BOOLEAN,
                GuiTask.PROP_SHOW_CHILDREN,
                Translations.getString("general.task.show_children"),
                true,
                false,
                false) {

            private TableCellRenderer renderer;

            @Override
            public TableCellRenderer getCellRenderer() {
                if (this.renderer == null) {
                    this.renderer = new TaskShowChildrenRenderer();
                }

                return this.renderer;
            }

            @Override
            public Object getProperty(Task model) {
                return ((GuiTask) model).isShowChildren();
            }

            @Override
            public void setProperty(Task model, Object value) {
                ((GuiTask) model).setShowChildren((Boolean) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "SUB_TASK_COUNT",
                "task.field.sub_task_count",
                PropertyAccessorType.INTEGER,
                null,
                Translations.getString("general.task.sub_task_count"),
                false,
                false,
                false) {

            @Override
            public Object getProperty(Task model) {
                int count = 0;
                List<Task> children = model.getChildren();

                for (Task child : children)
                    if (child.getModelStatus().isEndUserStatus())
                        count++;

                return count;
            }

            @Override
            public void setProperty(Task model, Object value) {

            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "PARENT_TASK_COUNT",
                "task.field.parent_task_count",
                PropertyAccessorType.INTEGER,
                null,
                Translations.getString("general.task.parent_task_count"),
                false,
                false,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getAllParents().size();
            }

            @Override
            public void setProperty(Task model, Object value) {

            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "TITLE",
                "task.field.title",
                PropertyAccessorType.STRING,
                BasicModel.PROP_TITLE,
                Translations.getString("general.task.title"),
                true,
                true,
                false) {

            private TableCellRenderer renderer;
            private TableCellEditor editor;

            @Override
            public TableCellRenderer getCellRenderer() {
                if (this.renderer == null) {
                    this.renderer = new DefaultTableRenderer(
                            StringValueTaskTitle.INSTANCE);
                }

                return this.renderer;
            }

            @Override
            public TableCellEditor getCellEditor() {
                if (this.editor == null) {
                    this.editor = new TitleEditor();
                }

                return this.editor;
            }

            @Override
            public Object getProperty(Task model) {
                return model.getTitle();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setTitle((String) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "ORDER",
                "task.field.order",
                PropertyAccessorType.ORDER,
                Model.PROP_ORDER,
                Translations.getString("general.task.order"),
                false,
                false,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getOrder();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setOrder((Integer) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "TAGS",
                "task.field.tags",
                PropertyAccessorType.TAGS,
                Task.PROP_TAGS,
                Translations.getString("general.task.tags"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getTags().toString();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setTags(TagList.fromString((String) value));
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "FOLDER",
                "task.field.folder",
                PropertyAccessorType.FOLDER,
                Task.PROP_FOLDER,
                Translations.getString("general.task.folder"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getFolder();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setFolder((Folder) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "CONTEXTS",
                "task.field.contexts",
                MULTIPLE_CONTEXTS ? PropertyAccessorType.CONTEXTS : PropertyAccessorType.CONTEXT,
                Task.PROP_CONTEXTS,
                Translations.getString("general.task.context"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                if (MULTIPLE_CONTEXTS)
                    return model.getContexts();
                else if (model.getContexts().size() > 0)
                    return model.getContexts().get(0);
                else
                    return null;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void setProperty(Task model, Object value) {
                if (value instanceof Model) {
                    model.getContexts().clear();
                    model.getContexts().add((Context) value);
                } else {
                    model.setContexts((ModelList<Context>) value);
                }
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "GOALS",
                "task.field.goals",
                MULTIPLE_GOALS ? PropertyAccessorType.GOALS : PropertyAccessorType.GOAL,
                Task.PROP_GOALS,
                Translations.getString("general.task.goal"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                if (MULTIPLE_GOALS)
                    return model.getGoals();
                else if (model.getGoals().size() > 0)
                    return model.getGoals().get(0);
                else
                    return null;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void setProperty(Task model, Object value) {
                if (value instanceof Model) {
                    model.getGoals().clear();
                    model.getGoals().add((Goal) value);
                } else {
                    model.setGoals((ModelList<Goal>) value);
                }
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "LOCATIONS",
                "task.field.locations",
                MULTIPLE_LOCATIONS ? PropertyAccessorType.LOCATIONS : PropertyAccessorType.LOCATION,
                Task.PROP_LOCATIONS,
                Translations.getString("general.task.location"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                if (MULTIPLE_LOCATIONS)
                    return model.getLocations();
                else if (model.getLocations().size() > 0)
                    return model.getLocations().get(0);
                else
                    return null;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void setProperty(Task model, Object value) {
                if (value instanceof Model) {
                    model.getLocations().clear();
                    model.getLocations().add((Location) value);
                } else {
                    model.setLocations((ModelList<Location>) value);
                }
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "PARENT",
                "task.field.parent",
                PropertyAccessorType.TASK,
                ModelParent.PROP_PARENT,
                Translations.getString("general.task.parent"),
                false,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getParent();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setParent((Task) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "PROGRESS",
                "task.field.progress",
                PropertyAccessorType.PERCENTAGE,
                Task.PROP_PROGRESS,
                Translations.getString("general.task.progress"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getProgress();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setProgress((Double) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "COMPLETED",
                "task.field.completed",
                PropertyAccessorType.BOOLEAN,
                Task.PROP_COMPLETED,
                Translations.getString("general.task.completed"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.isCompleted();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setCompleted((Boolean) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "COMPLETED_ON",
                "task.field.completed_on",
                PropertyAccessorType.CALENDAR_DATE_TIME,
                Task.PROP_COMPLETED_ON,
                Translations.getString("general.task.completed_on"),
                false,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getCompletedOn();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setCompletedOn((Calendar) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "DUE_DATE",
                "task.field.due_date",
                Main.getSettings().getBooleanProperty("date.use_due_time") ? PropertyAccessorType.CALENDAR_DATE_TIME : PropertyAccessorType.CALENDAR_DATE,
                Task.PROP_DUE_DATE,
                Translations.getString("general.task.due_date"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getDueDate();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setDueDate((Calendar) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "START_DATE",
                "task.field.start_date",
                Main.getSettings().getBooleanProperty("date.use_start_time") ? PropertyAccessorType.CALENDAR_DATE_TIME : PropertyAccessorType.CALENDAR_DATE,
                Task.PROP_START_DATE,
                Translations.getString("general.task.start_date"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getStartDate();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setStartDate((Calendar) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "DUE_DATE_REMINDER",
                "task.field.due_date_reminder",
                PropertyAccessorType.MINUTES,
                Task.PROP_DUE_DATE_REMINDER,
                Translations.getString("general.task.due_date_reminder"),
                true,
                true,
                false) {

            private TableCellRenderer renderer;

            @Override
            public TableCellRenderer getCellRenderer() {
                if (this.renderer == null) {
                    this.renderer = new DefaultTableRenderer(new MappedValue(
                            StringValueMinutes.INSTANCE,
                            IconValueReminder.INSTANCE));
                }

                return this.renderer;
            }

            @Override
            public Object getProperty(Task model) {
                return model.getDueDateReminder();
            }

            @Override
            public void setProperty(Task model, Object value) {
                if (value == null || !(value instanceof Integer))
                    model.setDueDateReminder(0);
                else
                    model.setDueDateReminder((Integer) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "START_DATE_REMINDER",
                "task.field.start_date_reminder",
                PropertyAccessorType.MINUTES,
                Task.PROP_START_DATE_REMINDER,
                Translations.getString("general.task.start_date_reminder"),
                true,
                true,
                false) {

            private TableCellRenderer renderer;

            @Override
            public TableCellRenderer getCellRenderer() {
                if (this.renderer == null) {
                    this.renderer = new DefaultTableRenderer(new MappedValue(
                            StringValueMinutes.INSTANCE,
                            IconValueReminder.INSTANCE));
                }

                return this.renderer;
            }

            @Override
            public Object getProperty(Task model) {
                return model.getStartDateReminder();
            }

            @Override
            public void setProperty(Task model, Object value) {
                if (value == null || !(value instanceof Integer))
                    model.setStartDateReminder(0);
                else
                    model.setStartDateReminder((Integer) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "REPEAT",
                "task.field.repeat",
                PropertyAccessorType.REPEAT,
                Task.PROP_REPEAT,
                Translations.getString("general.task.repeat"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getRepeat();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setRepeat((Repeat) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "REPEAT_FROM",
                "task.field.repeat_from",
                PropertyAccessorType.TASK_REPEAT_FROM,
                Task.PROP_REPEAT_FROM,
                Translations.getString("general.task.repeat_from"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getRepeatFrom();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setRepeatFrom((TaskRepeatFrom) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "STATUS",
                "task.field.status",
                PropertyAccessorType.TASK_STATUS,
                Task.PROP_STATUS,
                Translations.getString("general.task.status"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getStatus();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setStatus((TaskStatus) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "LENGTH",
                "task.field.length",
                PropertyAccessorType.TIME,
                Task.PROP_LENGTH,
                Translations.getString("general.task.length"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getLength();
            }

            @Override
            public void setProperty(Task model, Object value) {
                if (value == null || !(value instanceof Integer))
                    model.setLength(0);
                else
                    model.setLength((Integer) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "TIMER",
                "task.field.timer",
                PropertyAccessorType.TIMER,
                Task.PROP_TIMER,
                Translations.getString("general.task.timer"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getTimer();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setTimer((Timer) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "PRIORITY",
                "task.field.priority",
                PropertyAccessorType.TASK_PRIORITY,
                Task.PROP_PRIORITY,
                Translations.getString("general.task.priority"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.getPriority();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setPriority((TaskPriority) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "STAR",
                "task.field.star",
                PropertyAccessorType.STAR,
                Task.PROP_STAR,
                Translations.getString("general.task.star"),
                true,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return model.isStar();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setStar((Boolean) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "IMPORTANCE",
                "task.field.importance",
                PropertyAccessorType.INTEGER,
                null,
                Translations.getString("general.task.importance"),
                false,
                true,
                false) {

            @Override
            public Object getProperty(Task model) {
                return TaskUtils.getImportance(model);
            }

            @Override
            public void setProperty(Task model, Object value) {

            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "NOTE",
                "task.field.note",
                PropertyAccessorType.STRING,
                ModelNote.PROP_NOTE,
                Translations.getString("general.task.note"),
                false,
                true,
                false) {

            private TableCellRenderer renderer;

            @Override
            public TableCellRenderer getCellRenderer() {
                if (this.renderer == null) {
                    this.renderer = new DefaultTableRenderer(new MappedValue(
                            StringValues.EMPTY,
                            IconValueNote.INSTANCE), SwingConstants.CENTER);
                }

                return this.renderer;
            }

            @Override
            public String getPropertyAsString(Task model) {
                Object value = this.getProperty(model);
                return (value == null ? null : HTML2Text.convertToPlainText((String) value));
            }

            @Override
            public Object getProperty(Task model) {
                return model.getNote();
            }

            @Override
            public void setProperty(Task model, Object value) {
                model.setNote((String) value);
            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "CONTACTS",
                "task.field.contacts",
                PropertyAccessorType.STRING,
                Task.PROP_CONTACTS,
                Translations.getString("general.task.contacts"),
                false,
                true,
                false) {

            private TableCellRenderer renderer;

            @Override
            public TableCellRenderer getCellRenderer() {
                if (this.renderer == null) {
                    this.renderer = new DefaultTableRenderer(
                            new MappedValue(
                                    null,
                                    IconValueTaskContacts.INSTANCE),
                            SwingConstants.CENTER);
                }

                return this.renderer;
            }

            @Override
            public Object getProperty(Task model) {
                return model.getContacts().toString();
            }

            @Override
            public void setProperty(Task model, Object value) {

            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "TASKS",
                "task.field.tasks",
                PropertyAccessorType.STRING,
                Task.PROP_TASKS,
                Translations.getString("general.task.tasks"),
                false,
                true,
                false) {

            private TableCellRenderer renderer;

            @Override
            public TableCellRenderer getCellRenderer() {
                if (this.renderer == null) {
                    this.renderer = new DefaultTableRenderer(new MappedValue(
                            null,
                            IconValueTaskTasks.INSTANCE), SwingConstants.CENTER);
                }

                return this.renderer;
            }

            @Override
            public Object getProperty(Task model) {
                return model.getTasks().toString();
            }

            @Override
            public void setProperty(Task model, Object value) {

            }

        });

        this.add(new DefaultPropertyAccessor<Task>(
                "FILES",
                "task.field.files",
                PropertyAccessorType.STRING,
                Task.PROP_FILES,
                Translations.getString("general.task.files"),
                false,
                true,
                false) {

            private TableCellRenderer renderer;

            @Override
            public TableCellRenderer getCellRenderer() {
                if (this.renderer == null) {
                    this.renderer = new DefaultTableRenderer(new MappedValue(
                            null,
                            IconValueTaskFiles.INSTANCE), SwingConstants.CENTER);
                }

                return this.renderer;
            }

            @Override
            public Object getProperty(Task model) {
                return model.getFiles().toString();
            }

            @Override
            public void setProperty(Task model, Object value) {

            }

        });

        List<PropertyAccessor<Task>> accessors = TaskCustomColumnList.getInstance().getInitialPropertyAccessors();

        for (PropertyAccessor<Task> accessor : accessors) {
            this.add(accessor);
        }
    }

}
