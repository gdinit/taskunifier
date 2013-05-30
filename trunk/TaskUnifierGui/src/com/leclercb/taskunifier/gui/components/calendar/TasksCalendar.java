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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lu.tudor.santec.bizcal.NamedCalendar;
import bizcal.common.Event;
import bizcal.util.DateInterval;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;

public abstract class TasksCalendar extends NamedCalendar {
	
	protected List<Event> events;
	protected TaskCalendarView view;
	
	public TasksCalendar(TaskCalendarView view, String name) {
		this(view, name, null, null);
	}
	
	public TasksCalendar(TaskCalendarView view, String name, String description) {
		this(view, name, description, null);
	}
	
	public TasksCalendar(
			TaskCalendarView view,
			String name,
			String description,
			Color color) {
		super(name, description, color);
		
		CheckUtils.isNotNull(view);
		this.view = view;
		
		this.events = new ArrayList<Event>();
	}
	
	@Override
	public List<Event> addEvent(String clientId, Event event) {
		return null;
	}
	
	@Override
	public void deleteEvent(String clientId, Event event) {
		
	}
	
	@Override
	public List<Event> saveEvent(
			String clientId,
			Event event,
			boolean userInteraction) {
		return null;
	}
	
	public abstract void updateEvents();
	
	public abstract Event newEvent(DateInterval interval) throws Exception;
	
	public abstract Event moved(Event event, Date oldDate, Date newDate)
			throws Exception;
	
	public abstract Event resized(Event event, Date oldEndDate, Date newEndDate)
			throws Exception;
	
	public static Task getTask(Event event) {
		if (event == null)
			return null;
		
		return TaskFactory.getInstance().get((ModelId) event.getId());
	}
	
	public static Task[] getTasks(List<Event> events) {
		List<Task> tasks = new ArrayList<Task>();
		
		for (Event event : events) {
			Task task = getTask(event);
			
			if (task == null || tasks.contains(task))
				continue;
			
			tasks.add(task);
		}
		
		return tasks.toArray(new Task[0]);
	}
	
}
