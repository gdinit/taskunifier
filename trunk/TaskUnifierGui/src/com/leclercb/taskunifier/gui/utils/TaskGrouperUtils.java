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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SortOrder;

import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.Tag;
import com.leclercb.taskunifier.api.models.TagList;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.api.models.utils.ModelFactoryUtils;
import com.leclercb.taskunifier.api.models.utils.TaskTagList;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.api.searchers.filters.FilterLink;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilterElement;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.DaysCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.EnumCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.ModelCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.NumberCondition;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.StringCondition;
import com.leclercb.taskunifier.gui.api.searchers.groupers.TaskGrouperElement;
import com.leclercb.taskunifier.gui.commons.values.StringValueMinutes;
import com.leclercb.taskunifier.gui.commons.values.StringValuePercentage;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.translations.TranslationsUtils;

public final class TaskGrouperUtils {
	
	private TaskGrouperUtils() {
		
	}
	
	public static List<TaskSearcher> getFilters(TaskSearcher searcher) {
		List<TaskSearcher> searchers = new ArrayList<TaskSearcher>();
		
		if (searcher == null || searcher.getGrouper().getElementCount() == 0)
			return searchers;
		
		TaskGrouperElement element = searcher.getGrouper().getElement(0);
		searcher.getGrouper().removeElement(element);
		
		TaskSearcher s;
		
		switch (element.getProperty().getType()) {
			case CALENDAR_DATE:
			case CALENDAR_DATE_TIME:
				s = searcher.clone();
				setTitle(
						element,
						s,
						Translations.getString("grouper.label.before_yesterday"));
				addMainFilter(s, new TaskFilterElement(
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
				addMainFilter(s, new TaskFilterElement(
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
				addMainFilter(s, new TaskFilterElement(
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
				addMainFilter(s, new TaskFilterElement(
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
				addMainFilter(s, new TaskFilterElement(
						element.getProperty(),
						DaysCondition.GREATER_THAN,
						1,
						false), new TaskFilterElement(
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
				addMainFilter(s, new TaskFilterElement(
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
				addMainFilter(s, new TaskFilterElement(
						element.getProperty(),
						DaysCondition.GREATER_THAN,
						0,
						false), new TaskFilterElement(
						element.getProperty(),
						DaysCondition.WEEK_NOT_EQUALS,
						0,
						false), new TaskFilterElement(
						element.getProperty(),
						DaysCondition.WEEK_NOT_EQUALS,
						1,
						false));
				searchers.add(s);
				
				s = searcher.clone();
				setTitle(element, s, Translations.getString("general.no_value"));
				addMainFilter(s, new TaskFilterElement(
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
							new TaskFilterElement(
									element.getProperty(),
									condition,
									i,
									false),
							new TaskFilterElement(
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
				addMainFilter(s, new TaskFilterElement(
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
							new TaskFilterElement(
									element.getProperty(),
									condition,
									i,
									false),
							new TaskFilterElement(
									element.getProperty(),
									NumberCondition.LESS_THAN_OR_EQUALS,
									i + 20,
									false));
					searchers.add(s);
				}
				
				break;
			case TASK_PRIORITY:
				for (TaskPriority p : TaskPriority.values()) {
					s = searcher.clone();
					setTitle(
							element,
							s,
							TranslationsUtils.translateTaskPriority(p));
					addMainFilter(
							s,
							new TaskFilterElement(
									element.getProperty(),
									EnumCondition.EQUALS,
									p,
									false));
					searchers.add(s);
				}
				
				break;
			case TASK_REPEAT_FROM:
				for (TaskRepeatFrom r : TaskRepeatFrom.values()) {
					s = searcher.clone();
					setTitle(
							element,
							s,
							TranslationsUtils.translateTaskRepeatFrom(r));
					addMainFilter(
							s,
							new TaskFilterElement(
									element.getProperty(),
									EnumCondition.EQUALS,
									r,
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
				List<?> models = null;
				
				switch (element.getProperty().getType()) {
					case CONTACT:
						models = ModelFactoryUtils.getFactory(ModelType.CONTACT).getList();
						break;
					case CONTEXT:
						models = ModelFactoryUtils.getFactory(ModelType.CONTEXT).getList();
						break;
					case FOLDER:
						models = ModelFactoryUtils.getFactory(ModelType.FOLDER).getList();
						break;
					case GOAL:
						models = ModelFactoryUtils.getFactory(ModelType.GOAL).getList();
						break;
					case LOCATION:
						models = ModelFactoryUtils.getFactory(
								ModelType.LOCATION).getList();
						break;
					case NOTE:
						models = ModelFactoryUtils.getFactory(ModelType.NOTE).getList();
						break;
					case TASK:
						models = ModelFactoryUtils.getFactory(ModelType.TASK).getList();
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
								new TaskFilterElement(
										element.getProperty(),
										ModelCondition.EQUALS,
										o,
										false));
						searchers.add(s);
					}
				}
				
				s = searcher.clone();
				setTitle(element, s, Translations.getString("general.no_value"));
				addMainFilter(s, new TaskFilterElement(
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
				addMainFilter(s, new TaskFilterElement(
						element.getProperty(),
						StringCondition.EQUALS,
						"true",
						false));
				searchers.add(s);
				
				s = searcher.clone();
				setTitle(element, s, "False");
				addMainFilter(s, new TaskFilterElement(
						element.getProperty(),
						StringCondition.EQUALS,
						"false",
						false));
				searchers.add(s);
				
				break;
			case TAGS:
				TagList tags = TaskTagList.getInstance().getTags();
				
				for (Tag tag : tags) {
					s = searcher.clone();
					setTitle(element, s, tag.toString());
					addMainFilter(
							s,
							new TaskFilterElement(
									element.getProperty(),
									StringCondition.CONTAINS,
									tag.toString(),
									false));
					searchers.add(s);
				}
				
				s = searcher.clone();
				setTitle(element, s, Translations.getString("general.no_value"));
				addMainFilter(s, new TaskFilterElement(
						element.getProperty(),
						StringCondition.EQUALS,
						null,
						false));
				searchers.add(s);
				
				break;
			case STRING:
				if (element.getProperty().equals(
						TaskColumnList.getInstance().get(TaskColumnList.STATUS))) {
					List<String> statuses = TaskStatusList.getInstance().getStatuses();
					
					for (String status : statuses) {
						s = searcher.clone();
						setTitle(element, s, status);
						addMainFilter(
								s,
								new TaskFilterElement(
										element.getProperty(),
										StringCondition.EQUALS,
										status,
										false));
						searchers.add(s);
					}
					
					s = searcher.clone();
					setTitle(
							element,
							s,
							Translations.getString("general.no_value"));
					addMainFilter(
							s,
							new TaskFilterElement(
									element.getProperty(),
									StringCondition.EQUALS,
									null,
									false));
					searchers.add(s);
					
					break;
				}
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
			TaskSearcher searcher,
			TaskFilterElement... elements) {
		TaskFilter filter = new TaskFilter();
		filter.setLink(FilterLink.AND);
		
		for (TaskFilterElement element : elements) {
			filter.addElement(element);
		}
		
		filter.addFilter(searcher.getFilter());
		searcher.setFilter(filter);
	}
	
	private static void setTitle(
			TaskGrouperElement element,
			TaskSearcher searcher,
			String title) {
		searcher.setTitle(element.getProperty().getLabel() + ": " + title);
	}
	
}
