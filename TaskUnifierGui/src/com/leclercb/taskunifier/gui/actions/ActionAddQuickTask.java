package com.leclercb.taskunifier.gui.actions;

import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Context;
import com.leclercb.taskunifier.api.models.ContextFactory;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.Location;
import com.leclercb.taskunifier.api.models.LocationFactory;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.TagList;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskStatus;
import com.leclercb.taskunifier.api.models.templates.TaskTemplate;
import com.leclercb.taskunifier.api.models.templates.TaskTemplateFactory;
import com.leclercb.taskunifier.gui.components.views.ViewType;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.translations.TranslationsUtils;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ActionAddQuickTask extends AbstractAction {
	
	public ActionAddQuickTask() {
		this(32, 32);
	}
	
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
		
		TaskTemplate searcherTemplate = null;
		
		if (viewType != ViewType.TASKS && viewType != ViewType.CALENDAR) {
			ViewUtils.setMainTaskView();
			viewType = ViewUtils.getCurrentViewType();
		} else {
			searcherTemplate = ViewUtils.getSelectedTaskSearcher().getTemplate();
		}
		
		TaskBean bean = new TaskBean();
		
		if (template != null)
			template.applyTo(bean);
		
		if (searcherTemplate != null)
			searcherTemplate.applyTo(bean);
		
		task = task.trim();
		
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
				findModel(s, bean);
			} else if (c == '*') { // Priority, Status
				findStatusPriority(s, bean);
			} else if (c == '>') { // Start Date
				findDate(s, true, bean);
			} else if (c == '<') { // Due Date
				findDate(s, false, bean);
			}
		}
		
		return ActionAddTask.addTask(bean, edit);
	}
	
	private static void findModel(String title, TaskBean bean) {
		Model model = null;
		
		if (bean.getContext() == null) {
			List<Context> contexts = ContextFactory.getInstance().getList();
			for (Context context : contexts) {
				if (context.getTitle().equalsIgnoreCase(title)) {
					bean.setContext(context.getModelId());
					return;
				}
				
				if (model == null
						&& context.getTitle().toLowerCase().startsWith(
								title.toLowerCase())) {
					model = context;
					break;
				}
			}
		}
		
		if (bean.getFolder() == null) {
			List<Folder> folders = FolderFactory.getInstance().getList();
			for (Folder folder : folders) {
				if (folder.getTitle().equalsIgnoreCase(title)) {
					bean.setFolder(folder.getModelId());
					return;
				}
				
				if (model == null
						&& folder.getTitle().toLowerCase().startsWith(
								title.toLowerCase())) {
					model = folder;
					break;
				}
			}
		}
		
		if (bean.getGoal() == null) {
			List<Goal> goals = GoalFactory.getInstance().getList();
			for (Goal goal : goals) {
				if (goal.getTitle().equalsIgnoreCase(title)) {
					bean.setGoal(goal.getModelId());
					return;
				}
				
				if (model == null
						&& goal.getTitle().toLowerCase().startsWith(
								title.toLowerCase())) {
					model = goal;
					break;
				}
			}
		}
		
		if (bean.getLocation() == null) {
			List<Location> locations = LocationFactory.getInstance().getList();
			for (Location location : locations) {
				if (location.getTitle().equalsIgnoreCase(title)) {
					bean.setLocation(location.getModelId());
					return;
				}
				
				if (model == null
						&& location.getTitle().toLowerCase().startsWith(
								title.toLowerCase())) {
					model = location;
					break;
				}
			}
		}
		
		if (model instanceof Context)
			bean.setContext(model.getModelId());
		else if (model instanceof Folder)
			bean.setFolder(model.getModelId());
		else if (model instanceof Goal)
			bean.setGoal(model.getModelId());
		else if (model instanceof Location)
			bean.setLocation(model.getModelId());
	}
	
	private static void findStatusPriority(String title, TaskBean bean) {
		for (TaskStatus status : TaskStatus.values()) {
			String s = TranslationsUtils.translateTaskStatus(status);
			
			if (s.toLowerCase().startsWith(title.toLowerCase())) {
				bean.setStatus(status);
				return;
			}
		}
		
		for (TaskPriority priority : TaskPriority.values()) {
			String p = TranslationsUtils.translateTaskPriority(priority);
			
			if (p.toLowerCase().startsWith(title.toLowerCase())) {
				bean.setPriority(priority);
				return;
			}
		}
	}
	
	private static void findDate(String title, boolean startDate, TaskBean bean) {
		String dateFormat = Main.getSettings().getStringProperty(
				"date.date_format");
		String timeFormat = Main.getSettings().getStringProperty(
				"date.time_format");
		dateFormat = dateFormat.replace("yyyy", "yy");
		
		Pattern pattern = Pattern.compile("([+-]?)([0-9]*)(d|w|m|y|tt|t)(.*)");
		Matcher matcher = pattern.matcher(title);
		
		if (matcher.find()) {
			int num = 0;
			String type = "d";
			
			try {
				num = Integer.parseInt(matcher.group(2));
			} catch (NumberFormatException e) {
				
			}
			
			type = matcher.group(3);
			
			if (matcher.group(1).equals("-"))
				num *= -1;
			
			Calendar date = Calendar.getInstance();
			if ("d".equals(type))
				date.add(Calendar.DAY_OF_MONTH, num);
			else if ("w".equals(type))
				date.add(Calendar.WEEK_OF_YEAR, num);
			else if ("m".equals(type))
				date.add(Calendar.MONTH, num);
			else if ("y".equals(type))
				date.add(Calendar.YEAR, num);
			else if ("t".equals(type))
				date.add(Calendar.DAY_OF_MONTH, 0);
			else if ("tt".equals(type))
				date.add(Calendar.DAY_OF_MONTH, 1);
			
			title = matcher.group(4).trim();
			
			try {
				SimpleDateFormat format = new SimpleDateFormat(timeFormat);
				Calendar time = Calendar.getInstance();
				time.setTime(format.parse(title));
				
				date.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
				date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
			} catch (ParseException e) {
				
			}
			
			if (startDate)
				bean.setStartDate(date);
			else
				bean.setDueDate(date);
			
			return;
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
