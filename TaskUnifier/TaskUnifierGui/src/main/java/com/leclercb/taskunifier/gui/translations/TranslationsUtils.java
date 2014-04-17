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
package com.leclercb.taskunifier.gui.translations;

import com.leclercb.commons.api.license.LicenseType;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.enums.GoalLevel;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.api.models.repeat.*;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerChoice;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;
import com.leclercb.taskunifier.gui.api.searchers.filters.FilterLink;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.*;
import com.leclercb.taskunifier.gui.commons.values.StringValueDayOfWeek;
import com.leclercb.taskunifier.gui.commons.values.StringValueWeekOfMonth;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public final class TranslationsUtils {

    private TranslationsUtils() {

    }

    public static String translateLicenseType(LicenseType type) {
        switch (type) {
            case LIFETIME:
                return Translations.getString("license.type.lifetime");
            case MANUAL:
                return Translations.getString("license.type.manual");
            case SINGLE_VERSION:
                return Translations.getString("license.type.single_version");
            case TRIAL:
                return Translations.getString("license.type.trial");
        }

        return "#" + type.name() + "#";
    }

    public static String translateModelType(ModelType type, boolean plurial) {
        if (plurial) {
            switch (type) {
                case CONTACT:
                    return Translations.getString("general.contacts");
                case CONTEXT:
                    return Translations.getString("general.contexts");
                case FOLDER:
                    return Translations.getString("general.folders");
                case GOAL:
                    return Translations.getString("general.goals");
                case LOCATION:
                    return Translations.getString("general.locations");
                case NOTE:
                    return Translations.getString("general.notes");
                case TASK:
                    return Translations.getString("general.tasks");
                case TASK_STATUS:
                    return Translations.getString("general.task_statuses");
            }
        }

        switch (type) {
            case CONTACT:
                return Translations.getString("general.contact");
            case CONTEXT:
                return Translations.getString("general.context");
            case FOLDER:
                return Translations.getString("general.folder");
            case GOAL:
                return Translations.getString("general.goal");
            case LOCATION:
                return Translations.getString("general.location");
            case NOTE:
                return Translations.getString("general.note");
            case TASK:
                return Translations.getString("general.task");
            case TASK_STATUS:
                return Translations.getString("general.task_status");
        }

        return "#" + type.name() + "#";
    }

    public static String translateSynchronizerChoice(SynchronizerChoice choice) {
        switch (choice) {
            case KEEP_APPLICATION:
                return Translations.getString("general.synchronizer.choice.keep_application");
            case KEEP_LAST_UPDATED:
                return Translations.getString("general.synchronizer.choice.keep_last_updated");
            case KEEP_API:
                return Translations.getString(
                        "general.synchronizer.choice.keep_api",
                        SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName());
        }

        return "#" + choice.name() + "#";
    }

    public static String translateGoalLevel(GoalLevel level) {
        switch (level) {
            case LIFE_TIME:
                return Translations.getString("general.goal.level.life_time");
            case LONG_TERM:
                return Translations.getString("general.goal.level.long_term");
            case SHORT_TERM:
                return Translations.getString("general.goal.level.short_term");
        }

        return "#" + level.name() + "#";
    }

    public static String translateTaskPriority(TaskPriority priority) {
        switch (priority) {
            case NEGATIVE:
                return Translations.getString("general.task.priority.negative");
            case LOW:
                return Translations.getString("general.task.priority.low");
            case MEDIUM:
                return Translations.getString("general.task.priority.medium");
            case HIGH:
                return Translations.getString("general.task.priority.high");
            case TOP:
                return Translations.getString("general.task.priority.top");
        }

        return "#" + priority.name() + "#";
    }

    public static String translateTaskRepeatFrom(TaskRepeatFrom repeatFrom) {
        switch (repeatFrom) {
            case DUE_DATE:
                return Translations.getString("general.task.repeat_from.due_date");
            case COMPLETION_DATE:
                return Translations.getString("general.task.repeat_from.completion_date");
        }

        return "#" + repeatFrom.name() + "#";
    }

    public static String translateBoolean(Boolean bool) {
        if (bool != null && bool)
            return Translations.getString("general.yes");

        return Translations.getString("general.no");
    }

    public static String translateFilterLink(FilterLink link) {
        switch (link) {
            case AND:
                return Translations.getString("general.and");
            case OR:
                return Translations.getString("general.or");
        }

        return "#" + link.name() + "#";
    }

    public static String translateSortOrder(SortOrder sortOrder) {
        switch (sortOrder) {
            case ASCENDING:
                return Translations.getString("general.sort_order.ascending");
            case DESCENDING:
                return Translations.getString("general.sort_order.descending");
            case UNSORTED:
                return Translations.getString("general.sort_order.unsorted");
        }

        return "#" + sortOrder.name() + "#";
    }

    public static String translateFilterCondition(Condition<?, ?> condition) {
        if (condition instanceof CalendarCondition) {
            switch ((CalendarCondition) condition) {
                case AFTER:
                    return Translations.getString("filter_condition.after");
                case BEFORE:
                    return Translations.getString("filter_condition.before");
                case EQUALS:
                    return Translations.getString("filter_condition.equals");
            }
        }

        if (condition instanceof DaysCondition) {
            switch ((DaysCondition) condition) {
                case TODAY:
                    return Translations.getString("filter_condition.today");
                case EQUALS:
                    return Translations.getString("filter_condition.equals");
                case GREATER_THAN:
                    return Translations.getString("filter_condition.after");
                case GREATER_THAN_OR_EQUALS:
                    return Translations.getString("filter_condition.after_or_equals");
                case GREATER_THAN_USING_TIME:
                    return Translations.getString("filter_condition.after_using_time");
                case LESS_THAN:
                    return Translations.getString("filter_condition.before");
                case LESS_THAN_OR_EQUALS:
                    return Translations.getString("filter_condition.before_or_equals");
                case LESS_THAN_USING_TIME:
                    return Translations.getString("filter_condition.before_using_time");
                case MONTH_EQUALS:
                    return Translations.getString("filter_condition.month_equals");
                case MONTH_NOT_EQUALS:
                    return Translations.getString("filter_condition.month_not_equals");
                case NOT_EQUALS:
                    return Translations.getString("filter_condition.not_equals");
                case WEEK_EQUALS:
                    return Translations.getString("filter_condition.week_equals");
                case WEEK_NOT_EQUALS:
                    return Translations.getString("filter_condition.week_not_equals");
            }
        }

        if (condition instanceof EnumCondition) {
            switch ((EnumCondition) condition) {
                case EQUALS:
                    return Translations.getString("filter_condition.equals");
                case LESS_THAN:
                    return Translations.getString("filter_condition.less_than");
                case LESS_THAN_OR_EQUALS:
                    return Translations.getString("filter_condition.less_than_or_equals");
                case GREATER_THAN:
                    return Translations.getString("filter_condition.greater_than");
                case GREATER_THAN_OR_EQUALS:
                    return Translations.getString("filter_condition.greater_than_or_equals");
                case NOT_EQUALS:
                    return Translations.getString("filter_condition.not_equals");
            }
        }

        if (condition instanceof ModelCondition) {
            switch ((ModelCondition) condition) {
                case EQUALS:
                    return Translations.getString("filter_condition.equals");
                case NOT_EQUALS:
                    return Translations.getString("filter_condition.not_equals");
            }
        }

        if (condition instanceof NumberCondition) {
            switch ((NumberCondition) condition) {
                case EQUALS:
                    return Translations.getString("filter_condition.equals");
                case LESS_THAN:
                    return Translations.getString("filter_condition.less_than");
                case LESS_THAN_OR_EQUALS:
                    return Translations.getString("filter_condition.less_than_or_equals");
                case GREATER_THAN:
                    return Translations.getString("filter_condition.greater_than");
                case GREATER_THAN_OR_EQUALS:
                    return Translations.getString("filter_condition.greater_than_or_equals");
                case NOT_EQUALS:
                    return Translations.getString("filter_condition.not_equals");
            }
        }

        if (condition instanceof StringCondition) {
            switch ((StringCondition) condition) {
                case CONTAINS:
                    return Translations.getString("filter_condition.contains");
                case DOES_NOT_CONTAIN:
                    return Translations.getString("filter_condition.does_not_contain");
                case DOES_NOT_END_WITH:
                    return Translations.getString("filter_condition.does_not_end_with");
                case DOES_NOT_START_WITH:
                    return Translations.getString("filter_condition.does_not_start_with");
                case EQUALS:
                    return Translations.getString("filter_condition.equals");
                case ENDS_WITH:
                    return Translations.getString("filter_condition.ends_with");
                case NOT_EQUALS:
                    return Translations.getString("filter_condition.not_equals");
                case STARTS_WITH:
                    return Translations.getString("filter_condition.starts_with");
            }
        }

        return "#" + condition.name() + "#";
    }

    public static String translatePropertyAccessorType(PropertyAccessorType type) {
        switch (type) {
            case BOOLEAN:
                return Translations.getString("property_accessor_type.boolean");
            case CALENDAR_DATE:
                return Translations.getString("property_accessor_type.calendar_date");
            case CALENDAR_DATE_TIME:
                return Translations.getString("property_accessor_type.calendar_date_time");
            case CONTACT:
                return Translations.getString("property_accessor_type.contact");
            case CONTEXT:
                return Translations.getString("property_accessor_type.context");
            case CONTEXTS:
                return Translations.getString("property_accessor_type.contexts");
            case DOUBLE:
                return Translations.getString("property_accessor_type.double");
            case FILE:
                return Translations.getString("property_accessor_type.file");
            case FOLDER:
                return Translations.getString("property_accessor_type.folder");
            case GOAL:
                return Translations.getString("property_accessor_type.goal");
            case GOALS:
                return Translations.getString("property_accessor_type.goals");
            case INTEGER:
                return Translations.getString("property_accessor_type.integer");
            case LOCATION:
                return Translations.getString("property_accessor_type.location");
            case LOCATIONS:
                return Translations.getString("property_accessor_type.locations");
            case MINUTES:
                return Translations.getString("property_accessor_type.minutes");
            case MODEL:
                return Translations.getString("property_accessor_type.model");
            case NOTE:
                return Translations.getString("property_accessor_type.note");
            case ORDER:
                return Translations.getString("property_accessor_type.order");
            case PERCENTAGE:
                return Translations.getString("property_accessor_type.percentage");
            case STAR:
                return Translations.getString("property_accessor_type.star");
            case STRING:
                return Translations.getString("property_accessor_type.string");
            case TAGS:
                return Translations.getString("property_accessor_type.tags");
            case TASK:
                return Translations.getString("property_accessor_type.task");
            case TASK_PRIORITY:
                return Translations.getString("property_accessor_type.task_priority");
            case TASK_REPEAT_FROM:
                return Translations.getString("property_accessor_type.task_repeat_from");
            case TASK_STATUS:
                return Translations.getString("property_accessor_type.task_status");
            case TIME:
                return Translations.getString("property_accessor_type.time");
            case TIMER:
                return Translations.getString("property_accessor_type.timer");
            case VOID:
                return Translations.getString("property_accessor_type.void");
        }

        return "#" + type.name() + "#";
    }

    public static String translateRepeat(Repeat repeat) {
        if (repeat instanceof RepeatWithParent) {
            return Translations.getString("repeat.with_parent");
        }

        if (repeat instanceof RepeatEveryX) {
            if (((RepeatEveryX) repeat).getType() == Calendar.DAY_OF_MONTH)
                return Translations.getString("repeat.every_x_days", ((RepeatEveryX) repeat).getValue());
            if (((RepeatEveryX) repeat).getType() == Calendar.WEEK_OF_YEAR)
                return Translations.getString("repeat.every_x_weeks", ((RepeatEveryX) repeat).getValue());
            if (((RepeatEveryX) repeat).getType() == Calendar.MONTH)
                return Translations.getString("repeat.every_x_months", ((RepeatEveryX) repeat).getValue());
            if (((RepeatEveryX) repeat).getType() == Calendar.YEAR)
                return Translations.getString("repeat.every_x_years", ((RepeatEveryX) repeat).getValue());
        }

        if (repeat instanceof RepeatEveryXWeekOnDays) {
            List<String> days = new ArrayList<String>();

            for (int i = 0; i < ((RepeatEveryXWeekOnDays) repeat).getDays().length; i++)
                days.add(StringValueDayOfWeek.INSTANCE.getString(((RepeatEveryXWeekOnDays) repeat).getDays()[i]));

            return Translations.getString(
                    "repeat.every_x_weeks_on",
                    ((RepeatEveryXWeekOnDays) repeat).getValue(),
                    StringUtils.join(days));
        }

        if (repeat instanceof RepeatEveryXMonthOnDayX) {
            return Translations.getString(
                    "repeat.every_x_months_day_x",
                    ((RepeatEveryXMonthOnDayX) repeat).getValue(),
                    ((RepeatEveryXMonthOnDayX) repeat).getDay());
        }

        if (repeat instanceof RepeatEveryXMonthOnWeekX) {
            return Translations.getString(
                    "repeat.every_x_months_day_of_week",
                    ((RepeatEveryXMonthOnWeekX) repeat).getValue(),
                    StringValueDayOfWeek.INSTANCE.getString(((RepeatEveryXMonthOnWeekX) repeat).getDay()),
                    StringValueWeekOfMonth.INSTANCE.getString(((RepeatEveryXMonthOnWeekX) repeat).getWeek()));
        }

        return "#" + repeat.getClass().getSimpleName() + "#";
    }

}
