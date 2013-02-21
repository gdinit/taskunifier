/*
 * TaskUnifier
 * Copyright (c) 2011, Benjamin Leclerc
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
package com.leclercb.taskunifier.gui.actions;

import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Context;
import com.leclercb.taskunifier.api.models.ContextFactory;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.Location;
import com.leclercb.taskunifier.api.models.LocationFactory;
import com.leclercb.taskunifier.api.models.TagList;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.beans.ModelBeanList;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.templates.TaskTemplate;
import com.leclercb.taskunifier.api.models.templates.TaskTemplateFactory;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumnList;
import com.leclercb.taskunifier.gui.components.views.ViewType;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.translations.TranslationsUtils;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.TaskStatusList;

public class ActionAddQuickTask extends AbstractViewAction {
	
	public ActionAddQuickTask(int width, int height) {
		super(
				Translations.getString("action.add_task"),
				ImageUtils.getResourceImage("task.png", width, height));
		
		this.putValue(
				SHORT_DESCRIPTION,
				Translations.getString("action.add_task"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String task = e.getActionCommand();
		ActionAddQuickTask.addQuickTask(task, true);
	}
	
	public static Task addQuickTask(String task, boolean edit) {
		return addQuickTask(
				TaskTemplateFactory.getInstance().getDefaultTemplate(),
				task,
				edit);
	}
	
	public static Task addQuickTask(
			TaskTemplate template,
			String task,
			boolean edit) {
		CheckUtils.isNotNull(task);
		
		ViewType viewType = ViewUtils.getCurrentViewType();
		
		if (viewType != ViewType.TASKS && viewType != ViewType.CALENDAR) {
			ViewUtils.setTaskView(true);
			viewType = ViewUtils.getCurrentViewType();
		}
		
		TaskBean bean = new TaskBean();
		
		if (template != null)
			template.applyTo(bean);
		
		task = task.trim();
		
		if (task.length() == 0) {
			bean.setTitle("");
		} else {
			Pattern pattern = Pattern.compile("[^&@*<>]+");
			Matcher matcher = pattern.matcher(task);
			
			if (!matcher.find())
				return null;
			
			String title = matcher.group();
			
			bean.setTitle(title.trim());
			
			char lastChar = title.charAt(title.length() - 1);
			
			pattern = Pattern.compile("[&@*<>][^&@*<>]+");
			matcher = pattern.matcher(task);
			
			while (matcher.find()) {
				String s = matcher.group();
				
				if (lastChar != ' ') {
					lastChar = s.charAt(s.length() - 1);
					bean.setTitle(bean.getTitle() + s.trim());
					continue;
				}
				
				lastChar = s.charAt(s.length() - 1);
				s = s.trim();
				
				char c = s.charAt(0);
				s = s.substring(1).trim();
				
				if (c == '&') { // Tag
					if (bean.getTags() != null)
						bean.getTags().addTag(s);
					else
						bean.setTags(TagList.fromString(s));
				} else if (c == '@') { // Context, Folder, Goal, Location
					findModel(s.toLowerCase(), bean);
				} else if (c == '*') { // Priority, Status
					findStatusPriority(s.toLowerCase(), bean);
				} else if (c == '>') { // Start Date
					findDate(s, true, bean);
				} else if (c == '<') { // Due Date
					findDate(s, false, bean);
				}
			}
		}
		
		return ActionAddTask.addTask(bean, edit);
	}
	
	private static void findModel(String title, TaskBean bean) {
		List<Context> contexts = ContextFactory.getInstance().getList();
		for (Context context : contexts) {
			if (context.getModelStatus().isEndUserStatus()) {
				if (context.getTitle().toLowerCase().startsWith(title)) {
					if (bean.getContexts() == null)
						bean.setContexts(new ModelBeanList());
					
					if (!TaskColumnList.MULTIPLE_CONTEXTS)
						bean.getContexts().clear();
					
					bean.getContexts().add(context.getModelId());
				}
			}
		}
		
		List<Folder> folders = FolderFactory.getInstance().getList();
		for (Folder folder : folders) {
			if (folder.getModelStatus().isEndUserStatus()) {
				if (folder.getTitle().toLowerCase().startsWith(title)) {
					bean.setFolder(folder.getModelId());
				}
			}
		}
		
		List<Goal> goals = GoalFactory.getInstance().getList();
		for (Goal goal : goals) {
			if (goal.getModelStatus().isEndUserStatus()) {
				if (goal.getTitle().toLowerCase().startsWith(title)) {
					if (bean.getGoals() == null)
						bean.setGoals(new ModelBeanList());
					
					if (!TaskColumnList.MULTIPLE_GOALS)
						bean.getGoals().clear();
					
					bean.getGoals().add(goal.getModelId());
				}
			}
		}
		
		List<Location> locations = LocationFactory.getInstance().getList();
		for (Location location : locations) {
			if (location.getModelStatus().isEndUserStatus()) {
				if (location.getTitle().toLowerCase().startsWith(title)) {
					if (bean.getLocations() == null)
						bean.setLocations(new ModelBeanList());
					
					if (!TaskColumnList.MULTIPLE_LOCATIONS)
						bean.getLocations().clear();
					
					bean.getLocations().add(location.getModelId());
				}
			}
		}
	}
	
	private static void findStatusPriority(String title, TaskBean bean) {
		for (String status : TaskStatusList.getInstance().getStatuses()) {
			if (status.toLowerCase().startsWith(title)) {
				if (bean.getStatus() == null
						|| status.toLowerCase().equals(title))
					bean.setStatus(status);
			}
		}
		
		for (TaskPriority priority : TaskPriority.values()) {
			String p = TranslationsUtils.translateTaskPriority(priority);
			
			if (p.toLowerCase().startsWith(title)) {
				if (bean.getPriority() == null || p.toLowerCase().equals(title))
					bean.setPriority(priority);
			}
		}
	}
	
	private static void findDate(String title, boolean startDate, TaskBean bean) {
		String dateFormat = Main.getSettings().getStringProperty(
				"date.date_format");
		String timeFormat = Main.getSettings().getStringProperty(
				"date.time_format");
		dateFormat = dateFormat.replace("yyyy", "yy");
		
		Pattern pattern = Pattern.compile("([+-]?)([0-9]*)([a-zA-Z]{1,3})(.*)");
		Matcher matcher = pattern.matcher(title);
		
		if (matcher.find()) {
			int num = 0;
			String type = Translations.getString("date.short_day");
			
			try {
				num = Integer.parseInt(matcher.group(2));
			} catch (NumberFormatException e) {
				
			}
			
			type = matcher.group(3);
			
			if (matcher.group(1).equals("-"))
				num *= -1;
			
			boolean skip = false;
			
			Calendar date = Calendar.getInstance();
			if (Translations.getString("date.short_day").equalsIgnoreCase(type))
				date.add(Calendar.DAY_OF_MONTH, num);
			else if (Translations.getString("date.short_week").equalsIgnoreCase(
					type))
				date.add(Calendar.WEEK_OF_YEAR, num);
			else if (Translations.getString("date.short_month").equalsIgnoreCase(
					type))
				date.add(Calendar.MONTH, num);
			else if (Translations.getString("date.short_year").equalsIgnoreCase(
					type))
				date.add(Calendar.YEAR, num);
			else if (Translations.getString("date.short_today").equalsIgnoreCase(
					type))
				date.add(Calendar.DAY_OF_MONTH, 0);
			else if (Translations.getString("date.short_tomorrow").equalsIgnoreCase(
					type))
				date.add(Calendar.DAY_OF_MONTH, 1);
			else
				skip = true;
			
			if (!skip) {
				title = matcher.group(4).trim();
				
				try {
					SimpleDateFormat format = new SimpleDateFormat(timeFormat);
					Calendar time = Calendar.getInstance();
					time.setTime(format.parse(title));
					
					date.set(
							Calendar.HOUR_OF_DAY,
							time.get(Calendar.HOUR_OF_DAY));
					date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
				} catch (ParseException e) {
					
				}
				
				if (startDate)
					bean.setStartDate(date);
				else
					bean.setDueDate(date);
				
				return;
			}
		}
		
		String[] weekdays = new String[] {
				"date.short_sunday",
				"date.short_monday",
				"date.short_tuesday",
				"date.short_wednesday",
				"date.short_thursday",
				"date.short_friday",
				"date.short_saturday" };
		
		for (int j = 0; j < weekdays.length; j++) {
			if (weekdays[j].equalsIgnoreCase(title)) {
				Calendar date = Calendar.getInstance();
				date.set(Calendar.DAY_OF_WEEK, j + 1);
				
				if (startDate)
					bean.setStartDate(date);
				else
					bean.setDueDate(date);
				
				return;
			}
		}
		
		SimpleDateFormat[] formats = {
				new SimpleDateFormat(dateFormat + " " + timeFormat),
				new SimpleDateFormat(dateFormat) };
		
		for (SimpleDateFormat format : formats) {
			try {
				Calendar date = Calendar.getInstance();
				date.setTime(format.parse(title));
				
				if (startDate)
					bean.setStartDate(date);
				else
					bean.setDueDate(date);
				
				return;
			} catch (ParseException e) {
				
			}
		}
	}
	
}
