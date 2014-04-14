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

import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.utils.ModelFactoryUtils;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcher;
import com.leclercb.taskunifier.gui.api.searchers.filters.FilterLink;
import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilter;
import com.leclercb.taskunifier.gui.api.searchers.filters.NoteFilterElement;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.DaysCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.ModelCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.NumberCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.StringCondition;
import com.leclercb.taskunifier.gui.api.searchers.groupers.NoteGrouperElement;
import com.leclercb.taskunifier.gui.commons.values.StringValueMinutes;
import com.leclercb.taskunifier.gui.commons.values.StringValuePercentage;
import com.leclercb.taskunifier.gui.translations.Translations;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class NoteGrouperUtils {

    private NoteGrouperUtils() {

    }

    public static List<NoteSearcher> getFilters(NoteSearcher searcher) {
        List<NoteSearcher> searchers = new ArrayList<NoteSearcher>();

        if (searcher == null || searcher.getGrouper().getElementCount() == 0)
            return searchers;

        NoteGrouperElement element = searcher.getGrouper().getElement(0);
        searcher.getGrouper().removeElement(element);

        NoteSearcher s;

        switch (element.getProperty().getType()) {
            case CALENDAR_DATE:
            case CALENDAR_DATE_TIME:
                s = searcher.clone();
                setTitle(
                        element,
                        s,
                        Translations.getString("grouper.label.before_yesterday"));
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        DaysCondition.LESS_THAN,
                        -1,
                        false));
                searchers.add(s);

                s = searcher.clone();
                setTitle(
                        element,
                        s,
                        Translations.getString("grouper.label.yesterday"));
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        DaysCondition.EQUALS,
                        -1,
                        false));
                searchers.add(s);

                s = searcher.clone();
                setTitle(
                        element,
                        s,
                        Translations.getString("grouper.label.today"));
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        DaysCondition.EQUALS,
                        0,
                        false));
                searchers.add(s);

                s = searcher.clone();
                setTitle(
                        element,
                        s,
                        Translations.getString("grouper.label.tomorrow"));
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        DaysCondition.EQUALS,
                        1,
                        false));
                searchers.add(s);

                s = searcher.clone();
                setTitle(
                        element,
                        s,
                        Translations.getString("grouper.label.later_this_week"));
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        DaysCondition.GREATER_THAN,
                        1,
                        false), new NoteFilterElement(
                        element.getProperty(),
                        DaysCondition.WEEK_EQUALS,
                        0,
                        false));
                searchers.add(s);

                s = searcher.clone();
                setTitle(
                        element,
                        s,
                        Translations.getString("grouper.label.next_week"));
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        DaysCondition.WEEK_EQUALS,
                        1,
                        false));
                searchers.add(s);

                s = searcher.clone();
                setTitle(
                        element,
                        s,
                        Translations.getString("grouper.label.in_the_future"));
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        DaysCondition.GREATER_THAN,
                        0,
                        false), new NoteFilterElement(
                        element.getProperty(),
                        DaysCondition.WEEK_NOT_EQUALS,
                        0,
                        false), new NoteFilterElement(
                        element.getProperty(),
                        DaysCondition.WEEK_NOT_EQUALS,
                        1,
                        false));
                searchers.add(s);

                s = searcher.clone();
                setTitle(element, s, Translations.getString("general.no_value"));
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        DaysCondition.EQUALS,
                        null,
                        false));
                searchers.add(s);

                break;
            case MINUTES:
                for (int i = 0; i <= 105; i += 15) {
                    String startMn = StringValueMinutes.INSTANCE.getString(i + 1);
                    NumberCondition condition = NumberCondition.GREATER_THAN;

                    if (i == 0) {
                        startMn = StringValueMinutes.INSTANCE.getString(i);
                        condition = NumberCondition.GREATER_THAN_OR_EQUALS;
                    }

                    s = searcher.clone();
                    setTitle(element, s, startMn
                            + " - "
                            + StringValueMinutes.INSTANCE.getString(i + 15));
                    addMainFilter(
                            s,
                            new NoteFilterElement(
                                    element.getProperty(),
                                    condition,
                                    i,
                                    false),
                            new NoteFilterElement(
                                    element.getProperty(),
                                    NumberCondition.LESS_THAN,
                                    i + 15,
                                    false));
                    searchers.add(s);
                }

                s = searcher.clone();
                setTitle(element, s, Translations.getString(
                        "grouper.label.more_than",
                        StringValueMinutes.INSTANCE.getString(120)));
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        NumberCondition.GREATER_THAN,
                        120,
                        false));
                searchers.add(s);

                break;
            case PERCENTAGE:
                for (int i = 0; i <= 80; i += 20) {
                    String startPrc = StringValuePercentage.INSTANCE.getString(i + 1);
                    NumberCondition condition = NumberCondition.GREATER_THAN;

                    if (i == 0) {
                        startPrc = StringValuePercentage.INSTANCE.getString(i);
                        condition = NumberCondition.GREATER_THAN_OR_EQUALS;
                    }

                    s = searcher.clone();
                    setTitle(element, s, startPrc
                            + " - "
                            + StringValuePercentage.INSTANCE.getString(i + 20));
                    addMainFilter(
                            s,
                            new NoteFilterElement(
                                    element.getProperty(),
                                    condition,
                                    i,
                                    false),
                            new NoteFilterElement(
                                    element.getProperty(),
                                    NumberCondition.LESS_THAN_OR_EQUALS,
                                    i + 20,
                                    false));
                    searchers.add(s);
                }

                break;
            case CONTACT:
            case CONTEXT:
            case CONTEXTS:
            case FOLDER:
            case GOAL:
            case GOALS:
            case LOCATION:
            case LOCATIONS:
            case NOTE:
            case TASK:
            case TASK_STATUS:
                List<?> models = null;

                switch (element.getProperty().getType()) {
                    case CONTACT:
                        models = ModelFactoryUtils.getFactory(ModelType.CONTACT).getList();
                        break;
                    case CONTEXT:
                    case CONTEXTS:
                        models = ModelFactoryUtils.getFactory(ModelType.CONTEXT).getList();
                        break;
                    case FOLDER:
                        models = ModelFactoryUtils.getFactory(ModelType.FOLDER).getList();
                        break;
                    case GOAL:
                    case GOALS:
                        models = ModelFactoryUtils.getFactory(ModelType.GOAL).getList();
                        break;
                    case LOCATION:
                    case LOCATIONS:
                        models = ModelFactoryUtils.getFactory(
                                ModelType.LOCATION).getList();
                        break;
                    case NOTE:
                        models = ModelFactoryUtils.getFactory(ModelType.NOTE).getList();
                        break;
                    case TASK:
                        models = ModelFactoryUtils.getFactory(ModelType.TASK).getList();
                        break;
                    case TASK_STATUS:
                        models = ModelFactoryUtils.getFactory(ModelType.TASK_STATUS).getList();
                        break;
                    default:
                        break;
                }

                if (models != null) {
                    for (Object o : models) {
                        s = searcher.clone();
                        setTitle(element, s, o.toString());
                        addMainFilter(
                                s,
                                new NoteFilterElement(
                                        element.getProperty(),
                                        ModelCondition.EQUALS,
                                        o,
                                        false));
                        searchers.add(s);
                    }
                }

                s = searcher.clone();
                setTitle(element, s, Translations.getString("general.no_value"));
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        ModelCondition.EQUALS,
                        null,
                        false));
                searchers.add(s);

                break;
            case STAR:
            case BOOLEAN:
                s = searcher.clone();
                setTitle(element, s, "True");
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        StringCondition.EQUALS,
                        "true",
                        false));
                searchers.add(s);

                s = searcher.clone();
                setTitle(element, s, "False");
                addMainFilter(s, new NoteFilterElement(
                        element.getProperty(),
                        StringCondition.EQUALS,
                        "false",
                        false));
                searchers.add(s);

                break;
            default:
                s = searcher.clone();
                setTitle(element, s, element.getProperty().toString());
                searchers.add(s);

                break;
        }

        if (element.getSortOrder() == SortOrder.DESCENDING)
            Collections.reverse(searchers);

        return searchers;
    }

    private static void addMainFilter(
            NoteSearcher searcher,
            NoteFilterElement... elements) {
        NoteFilter filter = new NoteFilter();
        filter.setLink(FilterLink.AND);

        for (NoteFilterElement element : elements) {
            filter.addElement(element);
        }

        filter.addFilter(searcher.getFilter());
        searcher.setFilter(filter);
    }

    private static void setTitle(
            NoteGrouperElement element,
            NoteSearcher searcher,
            String title) {
        searcher.setTitle(element.getProperty().getLabel() + ": " + title);
    }

}
