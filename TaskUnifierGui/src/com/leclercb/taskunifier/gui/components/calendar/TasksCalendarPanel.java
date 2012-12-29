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
package com.leclercb.taskunifier.gui.components.calendar;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import lu.tudor.santec.bizcal.EventModel;
import lu.tudor.santec.bizcal.NamedCalendar;
import lu.tudor.santec.bizcal.listeners.NamedCalendarListener;
import lu.tudor.santec.bizcal.resources.BizCalTranslations;
import lu.tudor.santec.bizcal.util.ObservableEventList;
import lu.tudor.santec.bizcal.views.DayViewPanel;
import lu.tudor.santec.bizcal.views.MonthViewPanel;
import lu.tudor.santec.bizcal.views.WeekListViewPanel;
import bizcal.common.DayViewConfig;
import bizcal.common.Event;
import bizcal.swing.CalendarListener.CalendarAdapter;
import bizcal.util.DateInterval;
import bizcal.util.TimeOfDay;

import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.properties.events.WeakSavePropertiesListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.actions.ActionEditTasks;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.commons.events.ModelSelectionChangeSupport;
import com.leclercb.taskunifier.gui.commons.events.ModelSelectionListener;
import com.leclercb.taskunifier.gui.commons.events.TaskSearcherSelectionChangeEvent;
import com.leclercb.taskunifier.gui.commons.events.TaskSearcherSelectionListener;
import com.leclercb.taskunifier.gui.components.tasksearchertree.TaskSearcherPanel;
import com.leclercb.taskunifier.gui.components.tasksearchertree.TaskSearcherView;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.TaskUtils;

public class TasksCalendarPanel extends JPanel implements TaskCalendarView, SavePropertiesListener, PropertyChangeListener {
	
	private boolean skipRefresh;
	
	private ModelSelectionChangeSupport modelSelectionChangeSupport;
	
	private ObservableEventList eventDataList;
	
	private DayViewPanel dayViewPanel;
	private DayViewPanel weekViewPanel;
	private WeekListViewPanel listViewPanel;
	private MonthViewPanel monthViewPanel;
	
	private TasksCalendar[] tasksCalendars = new TasksCalendar[] {
			new TasksStartDateCalendar(this),
			new TasksDueDateCalendar(this) };
	
	private CalendarPanel calendarPanel;
	
	public TasksCalendarPanel() {
		this.skipRefresh = false;
		this.modelSelectionChangeSupport = new ModelSelectionChangeSupport(this);
		
		Main.getSettings().addSavePropertiesListener(
				new WeakSavePropertiesListener(Main.getSettings(), this));
		
		this.initialize();
		
		this.refreshTasks();
	}
	
	@Override
	public TaskSearcherView getTaskSearcherView() {
		return this.calendarPanel.getTaskSearcherPanel();
	}
	
	private void initialize() {
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		
		BizCalTranslations.setLocale(Translations.getLocale());
		
		this.calendarPanel = new CalendarPanel();
		this.eventDataList = new ObservableEventList();
		
		DayViewConfig config = new DayViewConfig();
		
		config.setShowTime(Main.getSettings().getBooleanProperty(
				"date.use_start_time")
				|| Main.getSettings().getBooleanProperty("date.use_due_time"));
		
		config.setDayFormat(new SimpleDateFormat("E "
				+ Main.getSettings().getStringProperty("date.date_format")));
		config.setWeekDateFormat(new SimpleDateFormat("E "
				+ Main.getSettings().getStringProperty("date.date_format")));
		config.setTimeFormat(Main.getSettings().getSimpleDateFormatProperty(
				"date.time_format"));
		
		config.setStartView(new TimeOfDay(
				Main.getSettings().getIntegerProperty("date.day_start_hour"),
				0));
		config.setEndView(new TimeOfDay(Main.getSettings().getIntegerProperty(
				"date.day_end_hour"), 0));
		config.setDefaultDayStartHour(Main.getSettings().getIntegerProperty(
				"date.day_start_hour"));
		config.setDayBreak(Main.getSettings().getIntegerProperty(
				"date.day_break_hour"));
		config.setDefaultDayEndHour(Main.getSettings().getIntegerProperty(
				"date.day_end_hour"));
		
		EventModel dayModel = new EventModel(
				this.eventDataList,
				EventModel.TYPE_DAY);
		EventModel weekModel = new EventModel(
				this.eventDataList,
				EventModel.TYPE_WEEK);
		EventModel listModel = new EventModel(
				this.eventDataList,
				EventModel.TYPE_WEEK);
		EventModel monthModel = new EventModel(
				this.eventDataList,
				EventModel.TYPE_MONTH);
		
		this.dayViewPanel = new DayViewPanel(dayModel, config);
		this.weekViewPanel = new DayViewPanel(weekModel, config);
		this.listViewPanel = new WeekListViewPanel(listModel, config);
		this.monthViewPanel = new MonthViewPanel(monthModel, config);
		
		TasksCalendarListener calListener = new TasksCalendarListener();
		
		this.dayViewPanel.addCalendarListener(calListener);
		this.weekViewPanel.addCalendarListener(calListener);
		this.listViewPanel.addCalendarListener(calListener);
		this.monthViewPanel.addCalendarListener(calListener);
		
		this.calendarPanel.addCalendarView(this.dayViewPanel);
		this.calendarPanel.addCalendarView(this.weekViewPanel);
		this.calendarPanel.addCalendarView(this.listViewPanel);
		this.calendarPanel.addCalendarView(this.monthViewPanel);
		
		String selectedView = Main.getSettings().getStringProperty(
				"calendar.view.selected");
		
		if (selectedView == null)
			this.calendarPanel.showView(this.weekViewPanel.getViewName());
		else
			this.calendarPanel.showView(selectedView);
		
		boolean foundSelected = false;
		String selectedCalendar = Main.getSettings().getStringProperty(
				"calendar.selected");
		
		for (TasksCalendar calendar : this.tasksCalendars) {
			try {
				boolean active = Main.getSettings().getBooleanProperty(
						"calendar." + calendar.getId() + ".active");
				
				calendar.setActive(active);
			} catch (Throwable t) {
				
			}
			
			this.calendarPanel.addNamedCalendar(calendar);
			
			if (EqualsUtils.equals(
					selectedCalendar,
					calendar.getId().toString())) {
				foundSelected = true;
				this.calendarPanel.setSelectedCalendar(calendar);
			}
		}
		
		if (!foundSelected)
			this.calendarPanel.setSelectedCalendar(this.tasksCalendars[1]);
		
		this.calendarPanel.addNamedCalendarListener(new NamedCalendarListener() {
			
			@Override
			public void activeCalendarsChanged(
					Collection<NamedCalendar> calendars) {
				if (calendars == null || calendars.size() < 1) {
					TasksCalendarPanel.this.eventDataList.clear();
					return;
				}
				
				TasksCalendarPanel.this.refreshTasks();
			}
			
			@Override
			public void selectedCalendarChanged(NamedCalendar selectedCalendar) {
				TasksCalendarPanel.this.refreshTasks();
			}
			
		});
		
		this.calendarPanel.getTaskSearcherPanel().addTaskSearcherSelectionChangeListener(
				new TaskSearcherSelectionListener() {
					
					@Override
					public void taskSearcherSelectionChange(
							TaskSearcherSelectionChangeEvent event) {
						TasksCalendarPanel.this.refreshTasks();
					}
					
				});
		
		this.calendarPanel.getTaskSearcherPanel().addPropertyChangeListener(
				TaskSearcherPanel.PROP_TITLE_FILTER,
				new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						TasksCalendarPanel.this.refreshTasks();
					}
					
				});
		
		Main.getSettings().addPropertyChangeListener(
				new WeakPropertyChangeListener(Main.getSettings(), this));
		
		int zoom = Main.getSettings().getIntegerProperty(
				"view.calendar.zoom",
				80);
		this.calendarPanel.getZoomSlider().setValue(zoom);
		
		Main.getSettings().addPropertyChangeListener(
				new WeakPropertyChangeListener(Main.getSettings(), this));
		
		this.add(this.calendarPanel, BorderLayout.CENTER);
		
		this.calendarPanel.setDate(Calendar.getInstance().getTime());
	}
	
	@Override
	public Task[] getSelectedTasks() {
		Event[] events = this.calendarPanel.getCurrentView().getView().getSelectedEvents();
		return TasksCalendar.getTasks(Arrays.asList(events));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void refreshTasks() {
		if (this.skipRefresh)
			return;
		
		for (TasksCalendar calendar : this.tasksCalendars)
			calendar.updateEvents();
		
		List<Event> allEvents = new ArrayList<Event>();
		
		for (NamedCalendar nc : this.calendarPanel.getCalendars()) {
			allEvents.addAll(nc.getEvents(null, null));
		}
		
		try {
			this.dayViewPanel.getView().deselect();
		} catch (Exception e) {}
		
		try {
			this.weekViewPanel.getView().deselect();
		} catch (Exception e) {}
		
		try {
			this.monthViewPanel.getView().deselect();
		} catch (Exception e) {}
		
		try {
			this.listViewPanel.getView().deselect();
		} catch (Exception e) {}
		
		TasksCalendarPanel.this.modelSelectionChangeSupport.fireModelSelectionChange(new Task[0]);
		
		Collections.sort(allEvents);
		
		this.eventDataList.clear();
		this.eventDataList.addAll(allEvents);
	}
	
	@Override
	public boolean shouldBeDisplayed(Task task) {
		TaskSearcher searcher = this.calendarPanel.getTaskSearcherPanel().getSelectedTaskSearcher();
		
		if (searcher != null
				&& !TaskUtils.showUnindentTask(task, searcher.getFilter()))
			return false;
		
		return true;
	}
	
	private class TasksCalendarListener extends CalendarAdapter {
		
		public TasksCalendarListener() {
			
		}
		
		@Override
		public void eventsSelected(List<Event> events) {
			TasksCalendarPanel.this.modelSelectionChangeSupport.fireModelSelectionChange(TasksCalendar.getTasks(events));
		}
		
		@Override
		public void eventDoubleClick(
				Object id,
				Event event,
				MouseEvent mouseEvent) {
			this.showEvent(id, event);
		}
		
		@Override
		public void showEvent(Object id, Event event) {
			Task[] tasks = new Task[] { TasksCalendar.getTask(event) };
			ActionEditTasks.editTasks(tasks, true);
			
			TasksCalendarPanel.this.refreshTasks();
		}
		
		@Override
		public void newEvent(Object id, DateInterval interval) throws Exception {
			TasksCalendarPanel.this.skipRefresh = true;
			
			try {
				interval.setStartDate(this.roundMinutes(interval.getStartDate()));
				interval.setEndDate(this.roundMinutes(interval.getEndDate()));
				
				for (TasksCalendar calendar : TasksCalendarPanel.this.tasksCalendars) {
					if (calendar.isSelected()) {
						TasksCalendarPanel.this.eventDataList.add(calendar.newEvent(interval));
					}
				}
			} finally {
				TasksCalendarPanel.this.skipRefresh = false;
			}
			
			TasksCalendarPanel.this.refreshTasks();
		}
		
		@Override
		public void deleteEvents(List<Event> events) {
			Task[] tasks = TasksCalendar.getTasks(events);
			for (Task task : tasks) {
				TaskFactory.getInstance().markToDelete(task);
			}
			
			TasksCalendarPanel.this.refreshTasks();
		}
		
		@Override
		public void moved(
				Event event,
				Object oldCalId,
				Date oldDate,
				Object newCalId,
				Date newDate) throws Exception {
			TasksCalendarPanel.this.skipRefresh = true;
			
			try {
				newDate = this.roundMinutes(newDate);
				
				for (TasksCalendar calendar : TasksCalendarPanel.this.tasksCalendars) {
					if (calendar.getId().equals(
							event.get(NamedCalendar.CALENDAR_ID))) {
						event = calendar.moved(event, oldDate, newDate);
						TasksCalendarPanel.this.eventDataList.remove(event);
						TasksCalendarPanel.this.eventDataList.add(event);
					}
				}
			} finally {
				TasksCalendarPanel.this.skipRefresh = false;
			}
			
			TasksCalendarPanel.this.refreshTasks();
		}
		
		@Override
		public void resized(
				Event event,
				Object oldCalId,
				Date oldEndDate,
				Date newEndDate) throws Exception {
			TasksCalendarPanel.this.skipRefresh = true;
			
			try {
				newEndDate = this.roundMinutes(newEndDate);
				
				for (TasksCalendar calendar : TasksCalendarPanel.this.tasksCalendars) {
					if (calendar.getId().equals(
							event.get(NamedCalendar.CALENDAR_ID))) {
						event = calendar.resized(event, oldEndDate, newEndDate);
						TasksCalendarPanel.this.eventDataList.remove(event);
						TasksCalendarPanel.this.eventDataList.add(event);
					}
				}
			} finally {
				TasksCalendarPanel.this.skipRefresh = false;
			}
			
			TasksCalendarPanel.this.refreshTasks();
		}
		
		public Date roundMinutes(Date date) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			
			int minutes = calendar.get(Calendar.MINUTE);
			
			int mod = minutes % 15;
			if (mod < 7.5)
				minutes -= mod;
			else
				minutes += 15 - mod;
			
			calendar.set(Calendar.MINUTE, minutes);
			return calendar.getTime();
		}
		
	}
	
	@Override
	public void saveProperties() {
		Main.getSettings().setStringProperty(
				"calendar.view.selected",
				this.calendarPanel.getCurrentView().getViewName());
		
		Main.getSettings().remove("calendar.selected");
		
		for (TasksCalendar calendar : this.tasksCalendars) {
			Main.getSettings().setBooleanProperty(
					"calendar." + calendar.getId() + ".active",
					calendar.isActive());
			
			if (calendar.isSelected())
				Main.getSettings().setStringProperty(
						"calendar.selected",
						calendar.getId().toString());
		}
		
		Main.getSettings().setIntegerProperty(
				"view.calendar.zoom",
				this.calendarPanel.getZoomSlider().getValue());
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals("tasksearcher.show_completed_tasks")) {
			this.refreshTasks();
		}
		
		if (event.getPropertyName().equals("view.calendar.zoom")) {
			int zoom = Main.getSettings().getIntegerProperty(
					"view.calendar.zoom",
					80);
			
			this.calendarPanel.getZoomSlider().setValue(zoom);
		}
	}
	
	@Override
	public void addModelSelectionChangeListener(ModelSelectionListener listener) {
		this.modelSelectionChangeSupport.addModelSelectionChangeListener(listener);
	}
	
	@Override
	public void removeModelSelectionChangeListener(
			ModelSelectionListener listener) {
		this.modelSelectionChangeSupport.removeModelSelectionChangeListener(listener);
	}
	
}
