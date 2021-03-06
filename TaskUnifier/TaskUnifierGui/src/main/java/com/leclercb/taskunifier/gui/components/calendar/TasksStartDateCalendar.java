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
package com.leclercb.taskunifier.gui.components.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bizcal.common.Event;
import bizcal.util.DateInterval;

import com.leclercb.commons.api.utils.DateUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.actions.ActionAddTask;
import com.leclercb.taskunifier.gui.actions.ActionEditTasks;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.TaskUtils;

public class TasksStartDateCalendar extends TasksCalendar {
	
	public TasksStartDateCalendar(TaskCalendarView view) {
		super(
				view,
				Translations.getString("calendar.tasks_by_start_date"),
				Translations.getString("calendar.tasks_by_start_date"),
				null);
		this.setId("tasksstartdatecalendar");
	}
	
	@Override
	public void updateEvents() {
		this.events.clear();
		
		boolean showCompletedTasks = Main.getSettings().getBooleanProperty(
				"tasksearcher.show_completed_tasks");
		
		List<Task> tasks = TaskFactory.getInstance().getList();
		for (Task task : tasks) {
			Event event = this.taskToEvent(task, null);
			
			if (event != null)
				this.events.add(event);
		}
	}
	
	private Event taskToEvent(Task task, Event event) {
		if (!task.getModelStatus().isEndUserStatus())
			return null;
		
		boolean showCompletedTasks = Main.getSettings().getBooleanProperty(
				"tasksearcher.show_completed_tasks");
		
		if (!showCompletedTasks && task.isCompleted())
			return null;
		
		if (task.getStartDate() == null)
			return null;
		
		TaskSearcher searcher = this.view.getTaskSearcherView().getSelectedTaskSearcher();
		
		if (searcher != null
				&& !TaskUtils.showUnindentTask(
						task,
						null,
						searcher.getFilter(),
						false))
			return null;
		
		Calendar startDate = task.getStartDate();
		
		int length = task.getLength();
		
		if (length < 15)
			length = 15;
		
		if (!Main.getSettings().getBooleanProperty("date.use_start_time")) {
			startDate.set(
					Calendar.HOUR_OF_DAY,
					Main.getSettings().getIntegerProperty("date.day_start_hour"));
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);
			startDate.set(Calendar.MILLISECOND, 0);
		} else if (startDate.get(Calendar.HOUR_OF_DAY) == 0
				&& startDate.get(Calendar.MINUTE) == 0) {
			startDate.set(
					Calendar.HOUR_OF_DAY,
					Main.getSettings().getIntegerProperty("date.day_start_hour"));
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);
			startDate.set(Calendar.MILLISECOND, 0);
			
			length = (Main.getSettings().getIntegerProperty("date.day_end_hour") - Main.getSettings().getIntegerProperty(
					"date.day_start_hour")) * 60;
		}
		
		Calendar dueDate = DateUtils.cloneCalendar(startDate);
		dueDate.add(Calendar.MINUTE, length);
		
		if (event == null)
			event = new Event();
		
		event.setShowHeader(false);
		event.setId(task.getModelId());
		event.set(CALENDAR_ID, this.getId());
		event.setEditable(true);
		event.setSelectable(true);
		event.setDescription(task.getTitle());
		event.setToolTip(task.getNote());
		event.setStart(startDate.getTime());
		event.setEnd(dueDate.getTime());
		event.setColor(Main.getSettings().getColorProperty(
				"theme.color.importance." + TaskUtils.getImportance(task)));
		
		if (task.isCompleted())
			event.setIcon(ImageUtils.getResourceImage(
					"checkbox_selected.png",
					16,
					16));
		else
			event.setIcon(ImageUtils.getResourceImage(
					"warning_blue.png",
					16,
					16));
		
		return event;
	}
	
	@Override
	public List<Event> getEvents(Date from, Date to) {
		return this.events;
	}
	
	@Override
	public Event newEvent(DateInterval interval) throws Exception {
		Task task = ActionAddTask.addTask((String) null, false);
		
		long diff = interval.getDuration();
		diff = diff / (60 * 1000);
		
		int length = (int) diff;
		
		if (length < 15)
			length = 15;
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(interval.getStartDate());
		
		task.setLength(length);
		task.setStartDate(startDate);
		
		if (!ActionEditTasks.editTasks(new Task[] { task }, true))
			TaskFactory.getInstance().markDeleted(task);
		
		return this.taskToEvent(task, null);
	}
	
	@Override
	public Event moved(Event event, Date oldDate, Date newDate)
			throws Exception {
		Task task = TasksCalendar.getTask(event);
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(newDate);
		
		task.setStartDate(startDate);
		
		return this.taskToEvent(task, event);
	}
	
	@Override
	public Event resized(Event event, Date oldEndDate, Date newEndDate)
			throws Exception {
		Task task = TasksCalendar.getTask(event);
		
		long diff = oldEndDate.getTime() - newEndDate.getTime();
		diff = diff / (60 * 1000);
		
		int length = task.getLength() - (int) diff;
		
		if (length < 15)
			length = 15;
		
		Calendar dueDate = Calendar.getInstance();
		dueDate.setTime(newEndDate);
		
		task.setLength(length);
		
		return this.taskToEvent(task, event);
	}
	
}
