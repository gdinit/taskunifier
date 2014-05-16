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
package com.leclercb.taskunifier.gui.utils;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.DateUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.models.GuiTask;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilterElement;
import com.leclercb.taskunifier.gui.api.searchers.sorters.TaskSorter;
import com.leclercb.taskunifier.gui.components.synchronize.Synchronizing;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.main.Main;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public final class TaskUtils {

    private TaskUtils() {

    }

    public static boolean isSortByOrder(TaskSorter sorter) {
        if (sorter.getElementCount() >= 1)
            if (EqualsUtils.equals(
                    sorter.getElement(0).getProperty(),
                    TaskColumnList.getInstance().get(TaskColumnList.ORDER))
                    && sorter.getElement(0).getSortOrder() == SortOrder.ASCENDING)
                return true;

        return false;
    }

    public static void updateOrder(
            int index,
            Task[] tasksToOrder,
            Task[] displayedTasks) {
        Synchronizing.getInstance().setSynchronizing(true);

        try {
            int newOrder = 0;

            if (index > 0 && index <= displayedTasks.length)
                newOrder = displayedTasks[index - 1].getOrder() + 1;

            List<Task> tasks = TaskFactory.getInstance().getList();
            main:
            for (Task task : tasks) {
                if (!task.getModelStatus().isEndUserStatus())
                    continue;

                for (Task t : tasksToOrder)
                    if (EqualsUtils.equals(task, t))
                        continue main;

                for (Task t : displayedTasks)
                    if (EqualsUtils.equals(task, t))
                        continue main;

                if (task.getOrder() >= newOrder)
                    task.setOrder(task.getOrder() + 1 + tasksToOrder.length);
            }

            for (int i = 0; i < displayedTasks.length; i++) {
                Task task = displayedTasks[i];

                if (task == null)
                    continue;

                if (i >= index)
                    task.setOrder(task.getOrder() + 1 + tasksToOrder.length);
            }

            for (int i = 0; i < tasksToOrder.length; i++) {
                Task task = tasksToOrder[i];

                if (task == null)
                    continue;

                task.setOrder(newOrder + i);
            }
        } finally {
            Synchronizing.getInstance().setSynchronizing(false);
        }
    }

    public static boolean isInStartDateReminderZone(Task task) {
        if (task.getStartDate() == null)
            return false;

        if (task.isCompleted())
            return false;

        if (task.getStartDateReminder() == 0
                && !Main.getSettings().getBooleanProperty(
                "reminder.always_show_reminder"))
            return false;

        Calendar startDate = task.getStartDate();

        if (!Main.getSettings().getBooleanProperty("date.use_start_time")) {
            startDate.set(
                    startDate.get(Calendar.YEAR),
                    startDate.get(Calendar.MONTH),
                    startDate.get(Calendar.DAY_OF_MONTH),
                    0,
                    0,
                    0);
        }

        Calendar now = Calendar.getInstance();
        Calendar startDateReminder = DateUtils.cloneCalendar(startDate);
        startDateReminder.add(Calendar.MINUTE, -task.getStartDateReminder());

        if (now.compareTo(startDateReminder) >= 0)
            return true;

        Calendar exitDate = Main.getSettings().getCalendarProperty(
                "general.last_exit_date");

        if (exitDate != null)
            if (now.compareTo(startDateReminder) >= 0
                    && exitDate.compareTo(startDateReminder) <= 0)
                return true;

        return false;
    }

    public static boolean isInDueDateReminderZone(Task task) {
        if (task.getDueDate() == null)
            return false;

        if (task.isCompleted())
            return false;

        if (task.getDueDateReminder() == 0
                && !Main.getSettings().getBooleanProperty(
                "reminder.always_show_reminder")
                && !Main.getSettings().getBooleanProperty(
                "reminder.show_overdue_tasks"))
            return false;

        Calendar dueDate = task.getDueDate();

        if (!Main.getSettings().getBooleanProperty("date.use_due_time")) {
            dueDate.set(
                    dueDate.get(Calendar.YEAR),
                    dueDate.get(Calendar.MONTH),
                    dueDate.get(Calendar.DAY_OF_MONTH),
                    0,
                    0,
                    0);
        }

        Calendar now = Calendar.getInstance();
        Calendar dueDateReminder = DateUtils.cloneCalendar(dueDate);
        dueDateReminder.add(Calendar.MINUTE, -task.getDueDateReminder());

        if (now.compareTo(dueDateReminder) >= 0)
            if (now.compareTo(dueDate) <= 0
                    || Main.getSettings().getBooleanProperty("reminder.show_overdue_tasks"))
                return true;

        Calendar exitDate = Main.getSettings().getCalendarProperty(
                "general.last_exit_date");

        if (exitDate != null)
            if (now.compareTo(dueDateReminder) >= 0
                    && exitDate.compareTo(dueDateReminder) <= 0)
                return true;

        return false;
    }

    public static int getOverdueTaskCount() {
        int count = 0;

        List<Task> tasks = TaskFactory.getInstance().getList();
        for (Task task : tasks) {
            if (!task.getModelStatus().isEndUserStatus())
                continue;

            if (task.isCompleted())
                continue;

            if (!task.isOverDue(false))
                continue;

            count++;
        }

        return count;
    }

    public static String toText(
            Task[] tasks,
            List<PropertyAccessor<Task>> columns,
            boolean html) {
        return toText(tasks, columns, html, null, null);
    }

    public static String toText(
            Task[] tasks,
            List<PropertyAccessor<Task>> columns,
            boolean html,
            String header,
            String footer) {
        String[][] data = toStringData(tasks, columns);
        StringBuffer buffer = new StringBuffer();

        if (data == null)
            return null;

        if (html)
            buffer.append("<html>");

        if (header != null)
            buffer.append(header);

        int i = 0;
        for (String[] row : data) {
            if (i == 0) {
                i++;
                continue;
            }

            for (int j = 0; j < row.length; j++) {
                if (!html) {
                    buffer.append(data[0][j] + ": ");
                    buffer.append(row[j]);
                } else {
                    buffer.append("<b>" + data[0][j] + ":</b> ");

                    String text = row[j];

                    text = StringEscapeUtils.escapeHtml3(text);

                    buffer.append(text);
                }

                if (!html)
                    buffer.append(System.getProperty("line.separator"));
                else
                    buffer.append("<br />");
            }

            if (!html)
                buffer.append(System.getProperty("line.separator"));
            else
                buffer.append("<hr /><br />");

            i++;
        }

        if (footer != null)
            buffer.append(footer);

        if (html)
            buffer.append("</html>");

        return buffer.toString();
    }

    public static String toHtml(
            Task[] tasks,
            List<PropertyAccessor<Task>> columns) {
        String[][] data = toStringData(tasks, columns);
        StringBuffer buffer = new StringBuffer();

        if (data == null)
            return null;

        buffer.append("<html>");
        buffer.append("<table>");

        int i = 0;
        for (String[] row : data) {
            if (i == 0)
                buffer.append("<tr style=\"font-weight:bold;\">");
            else
                buffer.append("<tr>");

            for (int j = 0; j < row.length; j++) {
                String text = row[j];

                text = StringEscapeUtils.escapeHtml3(text);

                buffer.append("<td>" + text + "</td>");
            }

            buffer.append("</tr>");

            i++;
        }

        buffer.append("</table>");
        buffer.append("</html>");

        return buffer.toString();
    }

    public static String[][] toStringData(
            Task[] tasks,
            List<PropertyAccessor<Task>> columns) {
        CheckUtils.isNotNull(tasks);
        CheckUtils.isNotNull(columns);

        List<String[]> data = new ArrayList<String[]>();

        int i = 0;
        String[] row = new String[columns.size()];

        for (PropertyAccessor<Task> column : columns) {
            if (column == null)
                continue;

            row[i++] = column.getLabel();
        }

        data.add(row);

        for (Task task : tasks) {
            if (task == null)
                continue;

            i = 0;
            row = new String[columns.size()];

            for (PropertyAccessor<Task> column : columns) {
                if (column == null)
                    continue;

                row[i++] = toString(task, column);
            }

            data.add(row);
        }

        return data.toArray(new String[0][]);
    }

    public static String toString(Task task, PropertyAccessor<Task> column) {
        String content = column.getPropertyAsString(task);

        if (content == null)
            content = "";

        return content;
    }

    public static int getImportance(Task task) {
        CheckUtils.isNotNull(task);

        int importance = 2;

        switch (task.getPriority()) {
            case NEGATIVE:
                importance += -1;
                break;
            case LOW:
                importance += 0;
                break;
            case MEDIUM:
                importance += 1;
                break;
            case HIGH:
                importance += 2;
                break;
            case TOP:
                importance += 3;
                break;
        }

        importance += (task.isStar() ? 1 : 0);

        if (task.getDueDate() != null) {
            boolean useTime = Main.getSettings().getBooleanProperty(
                    "date.use_due_time");

            double diffDays = DateUtils.getDiffInDays(
                    Calendar.getInstance(),
                    task.getDueDate(),
                    useTime);

            if (diffDays > 14)
                importance += 0;
            else if (diffDays >= 7)
                importance += 1;
            else if (diffDays >= 2)
                importance += 2;
            else if (diffDays >= 1)
                importance += 3;
            else if (diffDays >= 0)
                importance += 5;
            else
                importance += 6;
        }

        return importance;
    }

    public static boolean badgeTask(
            Task task,
            Task comparedTask,
            TaskFilter filter) {
        return showTask(
                task,
                comparedTask,
                filter,
                filterContains(
                        filter,
                        TaskColumnList.getInstance().get(
                                TaskColumnList.COMPLETED)),
                true,
                true,
                true,
                false);
    }

    public static boolean showTask(
            Task task,
            Task comparedTask,
            TaskFilter filter) {
        return showTask(
                task,
                comparedTask,
                filter,
                filterContains(
                        filter,
                        TaskColumnList.getInstance().get(
                                TaskColumnList.COMPLETED)),
                false,
                false,
                true,
                false);
    }

    public static boolean showUnindentTask(
            Task task,
            Task comparedTask,
            TaskFilter filter,
            boolean skipShowCompleted) {
        return showTask(
                task,
                comparedTask,
                filter,
                filterContains(
                        filter,
                        TaskColumnList.getInstance().get(
                                TaskColumnList.COMPLETED)),
                true,
                true,
                true,
                skipShowCompleted);
    }

    private static boolean showTask(
            Task task,
            Task comparedTask,
            TaskFilter filter,
            boolean containsCompleted,
            boolean skipParentCheck,
            boolean skipShowChildren,
            boolean skipShowIfParentShown,
            boolean skipShowCompleted) {
        if (!task.getModelStatus().isEndUserStatus()) {
            return false;
        }

        // If a filtered parent task has non filtered children,
        // it must be displayed
        if (!skipParentCheck) {
            boolean result = false;
            List<Task> children = task.getAllChildren();
            for (Task child : children) {
                if (showTask(
                        child,
                        comparedTask,
                        filter,
                        containsCompleted,
                        false,
                        true,
                        skipShowIfParentShown,
                        skipShowCompleted)) {
                    result = true;
                    break;
                }
            }

            if (result) {
                if (!skipShowChildren) {
                    for (Task parent : task.getAllParents())
                        if (!((GuiTask) parent).isShowChildren())
                            return false;
                }

                return true;
            }
        }

        if (!skipShowChildren) {
            for (Task parent : task.getAllParents())
                if (!((GuiTask) parent).isShowChildren())
                    return false;
        }

        if (!skipShowCompleted) {
            if (!Main.getSettings().getBooleanProperty(
                    "tasksearcher.show_completed_tasks")) {
                if (task.isCompleted() && !containsCompleted)
                    return false;
            }
        }

        if (!skipShowIfParentShown && task.getParent() != null) {
            if (showTask(
                    task.getParent(),
                    comparedTask,
                    filter,
                    containsCompleted,
                    true,
                    skipShowChildren,
                    skipShowIfParentShown,
                    skipShowCompleted))
                return true;
        }

        if (filter == null)
            return true;

        return filter.include(task, comparedTask);
    }

    public static boolean filterContains(
            TaskFilter filter,
            PropertyAccessor<Task> column) {
        if (filter == null)
            return false;

        List<TaskFilterElement> elements = filter.getElements();
        List<TaskFilter> filters = filter.getFilters();

        for (TaskFilterElement e : elements) {
            if (EqualsUtils.equals(e.getProperty(), column)) {
                return true;
            }
        }

        for (TaskFilter f : filters) {
            if (filterContains(f, column))
                return true;
        }

        return false;
    }

}
