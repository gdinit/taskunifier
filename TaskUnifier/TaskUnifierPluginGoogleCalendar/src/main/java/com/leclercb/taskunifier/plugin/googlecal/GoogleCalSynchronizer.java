/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.model.Event.Reminders;
import com.leclercb.commons.api.progress.ProgressMonitor;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.DateUtils;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.api.synchronizer.Synchronizer;
import com.leclercb.taskunifier.api.synchronizer.SynchronizerChoice;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerMainProgressMessage;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerMainProgressMessage.ProgressMessageType;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerUpdatedModelsProgressMessage;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.googlecal.translations.PluginTranslations;

import java.util.*;
import java.util.Calendar;

public class GoogleCalSynchronizer implements Synchronizer {
	
	private GoogleCalConnection connection;
	
	GoogleCalSynchronizer(GoogleCalConnection connection) {
		CheckUtils.isNotNull(connection);
		
		this.connection = connection;
	}
	
	public GoogleCalConnection getConnection() {
		return this.connection;
	}
	
	@Override
	public void publish() throws SynchronizerException {
		this.publish(null);
	}
	
	@Override
	public void publish(ProgressMonitor monitor) throws SynchronizerException {
		if (!this.connection.isConnected())
			throw new SynchronizerException(
					false,
					"Cannot publish if not connected");
		
		if (monitor != null)
			monitor.addMessage(new SynchronizerMainProgressMessage(
					PluginApi.getPlugin(GoogleCalPlugin.ID),
					ProgressMessageType.PUBLISHER_START));
		
		try {
			this.publishTasks(monitor);
		} catch (SynchronizerException e) {
			throw e;
		} catch (Exception e) {
			throw new SynchronizerException(
					false,
					PluginTranslations.getString("error.try_again"),
					e);
		}
		
		if (monitor != null)
			monitor.addMessage(new SynchronizerMainProgressMessage(
					PluginApi.getPlugin(GoogleCalPlugin.ID),
					ProgressMessageType.PUBLISHER_END));
	}
	
	private void publishTasks(ProgressMonitor monitor) throws Exception {
		List<Task> tasks = new ArrayList<Task>(
				TaskFactory.getInstance().getList());
		
		com.google.api.services.calendar.Calendar service = this.connection.getService();
		
		int publishBeforeNow = PluginApi.getUserSettings().getIntegerProperty(
				"plugin.googlecal.publish_before_now",
				7);
		
		int publishAfterNow = PluginApi.getUserSettings().getIntegerProperty(
				"plugin.googlecal.publish_after_now",
				15);
		
		// Compute action count
		int actionCount = 0;
		
		for (Task task : tasks) {
			if (!task.getModelStatus().isEndUserStatus())
				continue;
			
			if (task.isCompleted())
				continue;
			
			if (task.getStartDate() == null && task.getDueDate() == null)
				continue;
			
			boolean before = true;
			if (task.getStartDate() != null) {
				double days = DateUtils.getDiffInDays(
						task.getStartDate(),
						Calendar.getInstance(),
						true);
				if (days < -publishBeforeNow || days > publishAfterNow)
					before = false;
			}
			
			boolean after = true;
			if (task.getDueDate() != null) {
				double days = DateUtils.getDiffInDays(
						task.getDueDate(),
						Calendar.getInstance(),
						true);
				if (days < -publishBeforeNow || days > publishAfterNow)
					after = false;
			}
			
			if (!before && !after)
				continue;
			
			actionCount++;
		}
		
		if (monitor != null && actionCount > 0)
			monitor.addMessage(new SynchronizerUpdatedModelsProgressMessage(
					PluginApi.getPlugin(GoogleCalPlugin.ID),
					ProgressMessageType.PUBLISHER_START,
					ModelType.TASK,
					actionCount));
		
		String calendarName = PluginApi.getUserSettings().getStringProperty(
				"plugin.googlecal.calendar_name",
				"TaskUnifier");
		
		// Find calendar
		String calendarId = null;
		
		CalendarList calendarList = service.calendarList().list().execute();
		
		main: while (true) {
			for (CalendarListEntry calendarListEntry : calendarList.getItems()) {
				if (calendarName.equals(calendarListEntry.getSummary())) {
					calendarId = calendarListEntry.getId();
					break main;
				}
			}
			
			String pageToken = calendarList.getNextPageToken();
			
			if (pageToken != null && !pageToken.isEmpty()) {
				calendarList = service.calendarList().list().setPageToken(
						pageToken).execute();
			} else {
				break;
			}
		}
		
		// Retrieve events
		List<Event> events = new ArrayList<Event>();
		
		if (calendarId != null) {
			Events e = service.events().list(calendarId).execute();
			
			if (e.getItems() != null)
				events.addAll(e.getItems());
			
			while (true) {
				String pageToken = e.getNextPageToken();
				
				if (pageToken != null && !pageToken.isEmpty()) {
					e = service.events().list(calendarId).setPageToken(
							pageToken).execute();
					
					if (e.getItems() != null)
						events.addAll(e.getItems());
				} else {
					break;
				}
			}
		}
		
		// Create calendar
		if (calendarId == null) {
			com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
			calendar.setSummary(calendarName);
			calendar.setTimeZone(TimeZone.getDefault().getID());
			
			calendar = service.calendars().insert(calendar).execute();
			calendarId = calendar.getId();
		}
		
		boolean dayEventIfSameDay = PluginApi.getUserSettings().getBooleanProperty(
				"plugin.googlecal.day_event_if_same_day",
				true);
		
		boolean publishStartDateEvents = PluginApi.getUserSettings().getBooleanProperty(
				"plugin.googlecal.publish_start_date_events",
				true);
		
		boolean publishEndDateEvents = PluginApi.getUserSettings().getBooleanProperty(
				"plugin.googlecal.publish_end_date_events",
				true);
		
		// Synchronize tasks
		for (Task task : tasks) {
			if (!task.getModelStatus().isEndUserStatus())
				continue;
			
			if (task.isCompleted())
				continue;
			
			if (task.getStartDate() == null && task.getDueDate() == null)
				continue;
			
			if (dayEventIfSameDay
					&& !PluginApi.getSettings().getBooleanProperty(
							"date.use_start_time")
					&& !PluginApi.getSettings().getBooleanProperty(
							"date.use_due_time")
					&& (task.getStartDate() == null
							|| task.getDueDate() == null || DateUtils.getDiffInDays(
							task.getStartDate(),
							task.getDueDate(),
							false) == 0)) {
				Calendar date = task.getStartDate();
				
				if (date == null)
					date = task.getDueDate();
				
				double days = DateUtils.getDiffInDays(
						date,
						Calendar.getInstance(),
						true);
				if (days < -publishBeforeNow || days > publishAfterNow)
					continue;
				
				DateUtils.removeTime(date);
				
				String colorId = PluginApi.getUserSettings().getStringProperty(
						"plugin.googlecal.due_event_color",
						"2");
				
				this.publishTask(
						service,
						events,
						calendarId,
						task,
						"googlecal_day",
						date,
						date,
						colorId,
						true,
						true,
						true);
			} else {
				if (publishStartDateEvents && task.getStartDate() != null) {
					double days = DateUtils.getDiffInDays(
							task.getStartDate(),
							Calendar.getInstance(),
							true);
					if (days < -publishBeforeNow || days > publishAfterNow)
						continue;
					
					Calendar startDate = task.getStartDate();
					
					if (!PluginApi.getSettings().getBooleanProperty(
							"date.use_start_time")) {
						DateUtils.removeTime(startDate);
						startDate.set(
								Calendar.HOUR_OF_DAY,
								PluginApi.getSettings().getIntegerProperty(
										"date.day_start_hour"));
					}
					
					int length = task.getLength();
					
					if (length < 30)
						length = 30;
					
					Calendar dueDate = DateUtils.cloneCalendar(startDate);
					dueDate.add(Calendar.MINUTE, length);
					
					String colorId = PluginApi.getUserSettings().getStringProperty(
							"plugin.googlecal.start_event_color",
							"1");
					
					this.publishTask(
							service,
							events,
							calendarId,
							task,
							"googlecal_start",
							startDate,
							dueDate,
							colorId,
							false,
							true,
							false);
				}
				
				if (publishEndDateEvents && task.getDueDate() != null) {
					double days = DateUtils.getDiffInDays(
							task.getDueDate(),
							Calendar.getInstance(),
							true);
					if (days < -publishBeforeNow || days > publishAfterNow)
						continue;
					
					Calendar dueDate = task.getDueDate();
					
					if (!PluginApi.getSettings().getBooleanProperty(
							"date.use_due_time")) {
						DateUtils.removeTime(dueDate);
						dueDate.set(
								Calendar.HOUR_OF_DAY,
								PluginApi.getSettings().getIntegerProperty(
										"date.day_end_hour"));
					}
					
					int length = task.getLength();
					
					if (length < 30)
						length = 30;
					
					Calendar startDate = DateUtils.cloneCalendar(dueDate);
					startDate.add(Calendar.MINUTE, -length);
					
					String colorId = PluginApi.getUserSettings().getStringProperty(
							"plugin.googlecal.due_event_color",
							"2");
					
					this.publishTask(
							service,
							events,
							calendarId,
							task,
							"googlecal_due",
							startDate,
							dueDate,
							colorId,
							false,
							false,
							true);
				}
			}
		}
		
		if (monitor != null && actionCount > 0)
			monitor.addMessage(new SynchronizerUpdatedModelsProgressMessage(
					PluginApi.getPlugin(GoogleCalPlugin.ID),
					ProgressMessageType.PUBLISHER_END,
					ModelType.TASK,
					actionCount));
		
		if (monitor != null && events.size() > 0)
			monitor.addMessage(new SynchronizerUpdatedModelsProgressMessage(
					PluginApi.getPlugin(GoogleCalPlugin.ID),
					ProgressMessageType.PUBLISHER_START,
					ModelType.TASK,
					events.size()));
		
		// Delete events
		for (Event event : events) {
			service.events().delete(calendarId, event.getId()).execute();
			
			PluginLogger.getLogger().info("Delete event: " + event.getSummary());
		}
		
		if (monitor != null && events.size() > 0)
			monitor.addMessage(new SynchronizerUpdatedModelsProgressMessage(
					PluginApi.getPlugin(GoogleCalPlugin.ID),
					ProgressMessageType.PUBLISHER_END,
					ModelType.TASK,
					events.size()));
	}
	
	private void publishTask(
			com.google.api.services.calendar.Calendar service,
			List<Event> events,
			String calendarId,
			Task task,
			String referenceKey,
			Calendar startDate,
			Calendar dueDate,
			String colorId,
			boolean dateOnly,
			boolean startDateReminder,
			boolean dueDateReminder) throws Exception {
		Event existingEvent = null;
		
		for (Event e : events) {
			if (e.getId().equals(task.getModelReferenceId(referenceKey))) {
				existingEvent = e;
				break;
			}
		}
		
		if (existingEvent == null) {
			for (Event e : events) {
				if (this.equalEvents(e, this.fillEvent(
						task,
						startDate,
						dueDate,
						colorId,
						dateOnly,
						startDateReminder,
						dueDateReminder,
						null))) {
					existingEvent = e;
					break;
				}
			}
		}
		
		events.remove(existingEvent);
		
		Event event = null;
		
		if (existingEvent == null) {
			event = new Event();
		} else {
			event = (Event) existingEvent.clone();
		}
		
		this.fillEvent(
				task,
				startDate,
				dueDate,
				colorId,
				dateOnly,
				startDateReminder,
				dueDateReminder,
				event);
		
		if (this.equalEvents(existingEvent, event)) {
			return;
		}
		
		if (existingEvent == null) {
			event = service.events().insert(calendarId, event).execute();
			task.addModelReferenceId(referenceKey, event.getId());
			
			PluginLogger.getLogger().info("Insert event: " + task.getTitle());
		} else {
			service.events().update(calendarId, event.getId(), event).execute();
			
			PluginLogger.getLogger().info("Update event: " + task.getTitle());
		}
	}
	
	private Event fillEvent(
			Task task,
			Calendar startDate,
			Calendar dueDate,
			String colorId,
			boolean dateOnly,
			boolean startDateReminder,
			boolean dueDateReminder,
			Event event) throws Exception {
		if (event == null) {
			event = new Event();
		}
		
		event.setSummary(task.getTitle());
		event.setColorId(colorId);
		
		boolean transparency = PluginApi.getUserSettings().getBooleanProperty(
				"plugin.googlecal.event_transparency",
				true);
		
		if (transparency) {
			event.setTransparency("opaque");
		} else {
			event.setTransparency("transparent");
		}
		
		if (task.getLocations().size() != 0) {
			event.setLocation(task.getLocations().get(0).getTitle());
		}
		
		List<EventReminder> reminders = new ArrayList<EventReminder>();
		
		String startDateReminderMethod = PluginApi.getUserSettings().getStringProperty(
				"plugin.googlecal.start_date_reminder_method",
				"email");

        if (task.getStartDateReminder() != 0 && !EqualsUtils.equals("none", startDateReminderMethod)) {
            EventReminder reminder = new EventReminder();
			reminder.setMethod(startDateReminderMethod);
			reminder.setMinutes(task.getStartDateReminder());
			reminders.add(reminder);
		}
		
		String dueDateReminderMethod = PluginApi.getUserSettings().getStringProperty(
				"plugin.googlecal.due_date_reminder_method",
				"email");

        if (task.getDueDateReminder() != 0 && !EqualsUtils.equals("none", dueDateReminderMethod)) {
            EventReminder reminder = new EventReminder();
			reminder.setMethod(dueDateReminderMethod);
			reminder.setMinutes(task.getDueDateReminder());
			reminders.add(reminder);
		}
		
		if (reminders.size() != 0) {
			Reminders r = new Reminders();
			r.setUseDefault(false);
			r.setOverrides(reminders);
			
			event.setReminders(r);
		}
		
		if (dateOnly) {
			startDate.set(Calendar.HOUR_OF_DAY, 12);
			dueDate.set(Calendar.HOUR_OF_DAY, 12);
			
			DateTime start = new DateTime(
					true,
					startDate.getTimeInMillis(),
					null);
			
			DateTime end = new DateTime(true, dueDate.getTimeInMillis(), null);
			
			event.setStart(new EventDateTime().setDate(start));
			event.setEnd(new EventDateTime().setDate(end));
		} else {
			DateTime start = new DateTime(
					startDate.getTime(),
					TimeZone.getDefault());
			
			DateTime end = new DateTime(
					dueDate.getTime(),
					TimeZone.getDefault());
			
			event.setStart(new EventDateTime().setDateTime(start));
			event.setEnd(new EventDateTime().setDateTime(end));
		}
		
		return event;
	}
	
	private boolean equalEvents(Event existingEvent, Event newEvent) {
		if (existingEvent == null)
			return false;
		
		if (!EqualsUtils.equalsString(
				existingEvent.getSummary(),
				newEvent.getSummary()))
			return false;
		
		if (!EqualsUtils.equalsString(
				existingEvent.getColorId(),
				newEvent.getColorId()))
			return false;
		
		if (!EqualsUtils.equalsString(
				existingEvent.getLocation(),
				newEvent.getLocation()))
			return false;
		
		List<EventReminder> existingReminders = new ArrayList<EventReminder>();
		List<EventReminder> newReminders = new ArrayList<EventReminder>();
		
		if (existingEvent.getReminders() != null
				&& existingEvent.getReminders().getOverrides() != null)
			existingReminders = existingEvent.getReminders().getOverrides();
		
		if (newEvent.getReminders() != null
				&& newEvent.getReminders().getOverrides() != null)
			newReminders = newEvent.getReminders().getOverrides();
		
		if (existingReminders.size() != newReminders.size())
			return false;
		
		for (int i = 0; i < existingReminders.size(); i++) {
			EventReminder existingReminder = existingReminders.get(i);
			EventReminder newReminder = newReminders.get(i);
			
			if (!EqualsUtils.equalsString(
					existingReminder.getMethod(),
					newReminder.getMethod()))
				return false;
			
			if (!EqualsUtils.equals(
					existingReminder.getMinutes(),
					newReminder.getMinutes()))
				return false;
		}
		
		Object newStart = null;
		Object existingStart = null;
		
		if (newEvent.getStart() != null
				&& newEvent.getStart().getDate() != null)
			newStart = newEvent.getStart().getDate();
		
		if (existingEvent.getStart() != null
				&& existingEvent.getStart().getDate() != null)
			existingStart = existingEvent.getStart().getDate();
		
		if (newEvent.getStart() != null
				&& newEvent.getStart().getDateTime() != null)
			newStart = newEvent.getStart().getDateTime().getValue() / 1000;
		
		if (existingEvent.getStart() != null
				&& existingEvent.getStart().getDateTime() != null)
			existingStart = existingEvent.getStart().getDateTime().getValue() / 1000;
		
		if (!EqualsUtils.equals(newStart, existingStart))
			return false;
		
		Object newEnd = null;
		Object existingEnd = null;
		
		if (newEvent.getEnd() != null && newEvent.getEnd().getDate() != null)
			newEnd = newEvent.getEnd().getDate();
		
		if (existingEvent.getEnd() != null
				&& existingEvent.getEnd().getDate() != null)
			existingEnd = existingEvent.getEnd().getDate();
		
		if (newEvent.getEnd() != null
				&& newEvent.getEnd().getDateTime() != null)
			newEnd = newEvent.getEnd().getDateTime().getValue() / 1000;
		
		if (existingEvent.getEnd() != null
				&& existingEvent.getEnd().getDateTime() != null)
			existingEnd = existingEvent.getEnd().getDateTime().getValue() / 1000;
		
		if (!EqualsUtils.equals(newEnd, existingEnd))
			return false;
		
		return true;
	}
	
	@Override
	public void synchronize() throws SynchronizerException {
		
	}
	
	@Override
	public void synchronize(SynchronizerChoice choice)
			throws SynchronizerException {
		
	}
	
	@Override
	public void synchronize(ProgressMonitor monitor)
			throws SynchronizerException {
		
	}
	
	@Override
	public void synchronize(SynchronizerChoice choice, ProgressMonitor monitor)
			throws SynchronizerException {
		
	}
	
	@Override
	public void loadParameters(Properties properties) {
		
	}
	
	@Override
	public void saveParameters(Properties properties) {
		
	}
	
}
