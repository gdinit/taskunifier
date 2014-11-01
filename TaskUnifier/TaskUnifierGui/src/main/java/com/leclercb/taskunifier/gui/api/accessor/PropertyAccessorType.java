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
package com.leclercb.taskunifier.gui.api.accessor;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.CompareUtils;
import com.leclercb.commons.api.utils.DateUtils;
import com.leclercb.taskunifier.api.models.*;
import com.leclercb.taskunifier.api.models.Timer;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.api.models.repeat.Repeat;
import com.leclercb.taskunifier.gui.commons.comparators.BasicModelComparator;
import com.leclercb.taskunifier.gui.commons.editors.*;
import com.leclercb.taskunifier.gui.commons.renderers.ModelListRenderer;
import com.leclercb.taskunifier.gui.commons.values.*;
import com.leclercb.taskunifier.gui.translations.Translations;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.MappedValue;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.Calendar;
import java.util.Comparator;

public enum PropertyAccessorType {

    STRING(String.class, true),
    FILE(String.class, false),
    BOOLEAN(Boolean.class, true),
    CALENDAR_DATE(Calendar.class, true),
    CALENDAR_DATE_TIME(Calendar.class, true),
    CONTACT(Contact.class, true),
    CONTEXT(Context.class, true),
    CONTEXTS(ModelList.class, true),
    DOUBLE(Double.class, false),
    FOLDER(Folder.class, true),
    GOAL(Goal.class, true),
    GOALS(ModelList.class, true),
    INTEGER(Integer.class, false),
    LOCATION(Location.class, true),
    LOCATIONS(ModelList.class, true),
    MINUTES(Integer.class, true),
    MODEL(Model.class, false),
    NOTE(Note.class, true),
    ORDER(Integer.class, false),
    PERCENTAGE(Double.class, true),
    TASK_PRIORITY(TaskPriority.class, true),
    TASK_REPEAT_FROM(TaskRepeatFrom.class, true),
    TASK_STATUS(TaskStatus.class, true),
    STAR(Boolean.class, true),
    TASK(Task.class, true),
    TAGS(String.class, true),
    TIME(Integer.class, false),
    TIMER(Timer.class, false),
    REPEAT(Repeat.class, false),
    VOID(Void.class, false);

    private Class<?> type;
    private boolean groupable;
    private Comparator<?> comparator;
    private TableCellRenderer renderer;
    private TableCellEditor editor;

    private PropertyAccessorType(Class<?> type, boolean groupable) {
        this.setType(type);
        this.setGroupable(groupable);
    }

    public Class<?> getType() {
        return this.type;
    }

    private void setType(Class<?> type) {
        CheckUtils.isNotNull(type);
        this.type = type;
    }

    public boolean isGroupable() {
        return this.groupable;
    }

    private void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }

    public Object getDefaultValue() {
        switch (this) {
            case BOOLEAN:
                return false;
            case STAR:
                return false;
            case CALENDAR_DATE:
                return null;
            case CALENDAR_DATE_TIME:
                return null;
            case DOUBLE:
                return new Double(0);
            case PERCENTAGE:
                return new Double(0);
            case INTEGER:
                return new Integer(0);
            case MINUTES:
                return new Integer(0);
            case ORDER:
                return new Integer(0);
            case TIME:
                return new Integer(0);
            case TASK_PRIORITY:
                return null;
            case TASK_REPEAT_FROM:
                return null;
            case STRING:
                return null;
            case FILE:
                return null;
            case TAGS:
                return null;
            case TIMER:
                return null;
            case REPEAT:
                return null;
            case CONTACT:
                return null;
            case CONTEXT:
                return null;
            case CONTEXTS:
                return null;
            case FOLDER:
                return null;
            case GOAL:
                return null;
            case GOALS:
                return null;
            case LOCATION:
                return null;
            case LOCATIONS:
                return null;
            case MODEL:
                return null;
            case NOTE:
                return null;
            case TASK:
                return null;
            case VOID:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String convertPropertyToString(Object value) {
        switch (this) {
            case BOOLEAN:
                return StringValueBoolean.INSTANCE.getString(value);
            case STAR:
                return StringValueBoolean.INSTANCE.getString(value);
            case CALENDAR_DATE:
                return StringValueCalendar.INSTANCE_DATE.getString(value);
            case CALENDAR_DATE_TIME:
                return StringValueCalendar.INSTANCE_DATE_TIME.getString(value);
            case DOUBLE:
                return (value == null ? null : value.toString());
            case PERCENTAGE:
                return StringValuePercentage.INSTANCE.getString(value);
            case INTEGER:
                return (value == null ? null : value.toString());
            case MINUTES:
                return StringValueMinutes.INSTANCE.getString(value);
            case ORDER:
                return StringValueModelOrder.INSTANCE.getString(value);
            case TIME:
                return StringValueTime.INSTANCE.getString(value);
            case TASK_PRIORITY:
                return StringValueTaskPriority.INSTANCE.getString(value);
            case TASK_REPEAT_FROM:
                return StringValueTaskRepeatFrom.INSTANCE.getString(value);
            case TASK_STATUS:
                return StringValueModel.INSTANCE.getString(value);
            case STRING:
                return (value == null ? null : value.toString());
            case FILE:
                return (value == null ? null : value.toString());
            case TAGS:
                return (value == null ? null : value.toString());
            case TIMER:
                return StringValueTimer.INSTANCE.getString(value);
            case REPEAT:
                return StringValueRepeat.INSTANCE.getString(value);
            case CONTACT:
                return StringValueModel.INSTANCE.getString(value);
            case CONTEXT:
                return StringValueModel.INSTANCE.getString(value);
            case CONTEXTS:
                return StringValueModelList.INSTANCE.getString(value);
            case FOLDER:
                return StringValueModel.INSTANCE.getString(value);
            case GOAL:
                return StringValueModel.INSTANCE.getString(value);
            case GOALS:
                return StringValueModelList.INSTANCE.getString(value);
            case LOCATION:
                return StringValueModel.INSTANCE.getString(value);
            case LOCATIONS:
                return StringValueModelList.INSTANCE.getString(value);
            case MODEL:
                return StringValueModel.INSTANCE.getString(value);
            case NOTE:
                return StringValueModel.INSTANCE.getString(value);
            case TASK:
                return StringValueModel.INSTANCE.getString(value);
            case VOID:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    protected Comparator<?> getComparator() {
        if (this.comparator != null)
            return this.comparator;

        switch (this) {
            case BOOLEAN:
                this.comparator = null;
                break;
            case STAR:
                this.comparator = null;
                break;
            case CALENDAR_DATE:
                this.comparator = null;
                break;
            case CALENDAR_DATE_TIME:
                this.comparator = null;
                break;
            case DOUBLE:
                this.comparator = null;
                break;
            case PERCENTAGE:
                this.comparator = null;
                break;
            case INTEGER:
                this.comparator = null;
                break;
            case MINUTES:
                this.comparator = null;
                break;
            case ORDER:
                this.comparator = null;
                break;
            case TIME:
                this.comparator = null;
                break;
            case TASK_PRIORITY:
                this.comparator = null;
                break;
            case TASK_REPEAT_FROM:
                this.comparator = null;
                break;
            case TASK_STATUS:
                this.comparator = BasicModelComparator.INSTANCE_NULL_LAST;
                break;
            case STRING:
                this.comparator = null;
                break;
            case FILE:
                this.comparator = null;
                break;
            case TAGS:
                this.comparator = null;
                break;
            case TIMER:
                this.comparator = null;
                break;
            case REPEAT:
                this.comparator = null;
                break;
            case CONTACT:
                this.comparator = BasicModelComparator.INSTANCE_NULL_LAST;
                break;
            case CONTEXT:
                this.comparator = BasicModelComparator.INSTANCE_NULL_LAST;
                break;
            case CONTEXTS:
                this.comparator = null;
                break;
            case FOLDER:
                this.comparator = BasicModelComparator.INSTANCE_NULL_LAST;
                break;
            case GOAL:
                this.comparator = BasicModelComparator.INSTANCE_NULL_LAST;
                break;
            case GOALS:
                this.comparator = null;
                break;
            case LOCATION:
                this.comparator = BasicModelComparator.INSTANCE_NULL_LAST;
                break;
            case LOCATIONS:
                this.comparator = null;
                break;
            case MODEL:
                this.comparator = BasicModelComparator.INSTANCE_NULL_LAST;
                break;
            case NOTE:
                this.comparator = BasicModelComparator.INSTANCE_NULL_LAST;
                break;
            case TASK:
                this.comparator = BasicModelComparator.INSTANCE_NULL_LAST;
                break;
            case VOID:
                this.comparator = null;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return this.comparator;
    }

    public TableCellRenderer getCellRenderer() {
        if (this.renderer != null)
            return this.renderer;

        switch (this) {
            case BOOLEAN:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        null,
                        IconValueSelected.INSTANCE,
                        BooleanValueBoolean.INSTANCE), SwingConstants.CENTER);
                break;
            case STAR:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        null,
                        IconValueStar.INSTANCE,
                        BooleanValueBoolean.INSTANCE), SwingConstants.CENTER);
                break;
            case CALENDAR_DATE:
                this.renderer = new DefaultTableRenderer(
                        StringValueCalendar.INSTANCE_DATE);
                break;
            case CALENDAR_DATE_TIME:
                this.renderer = new DefaultTableRenderer(
                        StringValueCalendar.INSTANCE_DATE_TIME);
                break;
            case DOUBLE:
                this.renderer = new DefaultTableRenderer();
                break;
            case PERCENTAGE:
                this.renderer = new DefaultTableRenderer(
                        StringValuePercentage.INSTANCE);
                break;
            case INTEGER:
                this.renderer = new DefaultTableRenderer();
                break;
            case MINUTES:
                this.renderer = new DefaultTableRenderer(
                        StringValueMinutes.INSTANCE);
                break;
            case ORDER:
                this.renderer = new DefaultTableRenderer(
                        StringValueModelOrder.INSTANCE);
                break;
            case TIME:
                this.renderer = new DefaultTableRenderer(
                        StringValueTime.INSTANCE);
                break;
            case TASK_PRIORITY:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueTaskPriority.INSTANCE,
                        IconValueTaskPriority.INSTANCE));
                break;
            case TASK_REPEAT_FROM:
                this.renderer = new DefaultTableRenderer(
                        StringValueTaskRepeatFrom.INSTANCE);
                break;
            case TASK_STATUS:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueModel.INSTANCE,
                        IconValueModel.INSTANCE));
                break;
            case STRING:
                this.renderer = new DefaultTableRenderer();
                break;
            case FILE:
                this.renderer = new DefaultTableRenderer(new StringValueTitle(
                        Translations.getString("general.no_value")));
                break;
            case TAGS:
                this.renderer = new DefaultTableRenderer();
                break;
            case TIMER:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueTimer.INSTANCE,
                        IconValueTimer.INSTANCE));
                break;
            case REPEAT:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueRepeat.INSTANCE,
                        IconValueRepeat.INSTANCE));
                break;
            case CONTACT:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueModel.INSTANCE,
                        IconValueModel.INSTANCE));
                break;
            case CONTEXT:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueModel.INSTANCE,
                        IconValueModel.INSTANCE));
                break;
            case CONTEXTS:
                this.renderer = new ModelListRenderer<Context>(
                        ModelType.CONTEXT);
                break;
            case FOLDER:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueModel.INSTANCE,
                        IconValueModel.INSTANCE));
                break;
            case GOAL:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueModel.INSTANCE,
                        IconValueModel.INSTANCE));
                break;
            case GOALS:
                this.renderer = new ModelListRenderer<Goal>(ModelType.GOAL);
                break;
            case LOCATION:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueModel.INSTANCE,
                        IconValueModel.INSTANCE));
                break;
            case LOCATIONS:
                this.renderer = new ModelListRenderer<Location>(
                        ModelType.LOCATION);
                break;
            case MODEL:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueModel.INSTANCE_INDENTED,
                        IconValueModel.INSTANCE));
                break;
            case NOTE:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueModel.INSTANCE_NO_VALUE,
                        IconValueModel.INSTANCE));
                break;
            case TASK:
                this.renderer = new DefaultTableRenderer(new MappedValue(
                        StringValueModel.INSTANCE_NO_VALUE,
                        IconValueModel.INSTANCE));
                break;
            case VOID:
                this.renderer = new DefaultTableRenderer();
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return this.renderer;
    }

    public TableCellEditor getCellEditor() {
        if (this.editor != null)
            return this.editor;

        switch (this) {
            case BOOLEAN:
                this.editor = new JXTable.BooleanEditor();
                break;
            case STAR:
                this.editor = new JXTable.BooleanEditor();
                break;
            case CALENDAR_DATE:
                this.editor = new DateEditor(false);
                break;
            case CALENDAR_DATE_TIME:
                this.editor = new DateEditor(true);
                break;
            case DOUBLE:
                this.editor = new DoubleEditor();
                break;
            case PERCENTAGE:
                this.editor = new PercentageEditor();
                break;
            case INTEGER:
                this.editor = new IntegerEditor();
                break;
            case MINUTES:
                this.editor = new MinutesEditor();
                break;
            case ORDER:
                this.editor = null;
                break;
            case TIME:
                this.editor = new TimeEditor();
                break;
            case TASK_PRIORITY:
                this.editor = new TaskPriorityEditor();
                break;
            case TASK_REPEAT_FROM:
                this.editor = new TaskRepeatFromEditor();
                break;
            case TASK_STATUS:
                this.editor = new TaskStatusEditor();
                break;
            case STRING:
                this.editor = new JXTable.GenericEditor();
                break;
            case FILE:
                this.editor = new FileEditor();
                break;
            case TAGS:
                this.editor = new TagsEditor();
                break;
            case TIMER:
                this.editor = new TimerEditor();
                break;
            case REPEAT:
                this.editor = new RepeatEditor();
                break;
            case CONTACT:
                this.editor = new ContactEditor();
                break;
            case CONTEXT:
                this.editor = new ContextEditor();
                break;
            case CONTEXTS:
                this.editor = new ContextsEditor();
                break;
            case FOLDER:
                this.editor = new FolderEditor();
                break;
            case GOAL:
                this.editor = new GoalEditor();
                break;
            case GOALS:
                this.editor = new GoalsEditor();
                break;
            case LOCATION:
                this.editor = new LocationEditor();
                break;
            case LOCATIONS:
                this.editor = new LocationsEditor();
                break;
            case MODEL:
                this.editor = null;
                break;
            case NOTE:
                this.editor = new NoteEditor();
                break;
            case TASK:
                this.editor = new TaskEditor();
                break;
            case VOID:
                this.editor = null;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return this.editor;
    }

    public int compare(Object o1, Object o2) {
        switch (this) {
            case BOOLEAN:
            case STAR:
                return CompareUtils.compare((Boolean) o1, (Boolean) o2);
            case CALENDAR_DATE:
                return this.compareCalendars(
                        (Calendar) o1,
                        (Calendar) o2,
                        true,
                        false);
            case CALENDAR_DATE_TIME:
                return this.compareCalendars(
                        (Calendar) o1,
                        (Calendar) o2,
                        false,
                        false);
            case DOUBLE:
            case PERCENTAGE:
                return CompareUtils.compare((Double) o1, (Double) o2);
            case INTEGER:
            case MINUTES:
            case ORDER:
            case TIME:
                return CompareUtils.compare((Integer) o1, (Integer) o2);
            case TASK_PRIORITY:
                return CompareUtils.compare(
                        (TaskPriority) o1,
                        (TaskPriority) o2);
            case TASK_REPEAT_FROM:
                return CompareUtils.compare(
                        (TaskRepeatFrom) o1,
                        (TaskRepeatFrom) o2);
            case FILE:
            case STRING:
            case TAGS:
                return CompareUtils.compareLocalizedString(
                        (o1 == null ? null : o1.toString()),
                        (o2 == null ? null : o2.toString()));
            case TIMER:
                return CompareUtils.compare(((Timer) o1), ((Timer) o2));
            case CONTACT:
            case CONTEXT:
            case FOLDER:
            case GOAL:
            case LOCATION:
            case MODEL:
            case NOTE:
            case TASK:
            case TASK_STATUS:
                return this.compareModels(((Model) o1), ((Model) o2));
            case GOALS:
            case CONTEXTS:
            case LOCATIONS:
                return CompareUtils.compareLocalizedString(
                        (o1 == null ? null : o1.toString()),
                        (o2 == null ? null : o2.toString()));
            case VOID:
                return 0;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private int compareModels(Model model1, Model model2) {
        if (model1 == null && model2 == null)
            return 0;

        if (model1 == null)
            return 1;

        if (model2 == null)
            return -1;

        return CompareUtils.compareLocalizedString(
                model1.getTitle(),
                model2.getTitle());
    }

    private int compareCalendars(
            Calendar calendar1,
            Calendar calendar2,
            boolean dateOnly,
            boolean raw) {
        if (calendar1 == null && calendar2 == null)
            return 0;

        if (calendar1 == null)
            return 1;

        if (calendar2 == null)
            return -1;

        calendar1 = DateUtils.cloneCalendar(calendar1);
        calendar2 = DateUtils.cloneCalendar(calendar2);

        if (dateOnly) {
            calendar1.set(Calendar.HOUR_OF_DAY, 0);
            calendar1.set(Calendar.MINUTE, 0);

            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
        }

        if (!raw) {
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.MILLISECOND, 0);

            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.MILLISECOND, 0);
        }

        return CompareUtils.compare(calendar1, calendar2);
    }

}
