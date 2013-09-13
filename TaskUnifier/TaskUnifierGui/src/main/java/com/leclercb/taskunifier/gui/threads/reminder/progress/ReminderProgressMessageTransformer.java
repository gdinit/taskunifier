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
package com.leclercb.taskunifier.gui.threads.reminder.progress;

import com.leclercb.commons.api.progress.ProgressMessageTransformer;
import com.leclercb.commons.api.progress.event.ProgressMessageAddedEvent;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.commons.values.StringValueCalendar;
import com.leclercb.taskunifier.gui.utils.TaskUtils;

public class ReminderProgressMessageTransformer implements ProgressMessageTransformer {
	
	private static ReminderProgressMessageTransformer INSTANCE;
	
	public static ReminderProgressMessageTransformer getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ReminderProgressMessageTransformer();
		
		return INSTANCE;
	}
	
	private ReminderProgressMessageTransformer() {
		
	}
	
	@Override
	public boolean acceptsEvent(ProgressMessageAddedEvent event) {
		if (event.getMessage() instanceof ReminderDefaultProgressMessage) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public Object getEventValue(ProgressMessageAddedEvent event, String key) {
		if (event.getMessage() instanceof ReminderDefaultProgressMessage) {
			ReminderDefaultProgressMessage m = (ReminderDefaultProgressMessage) event.getMessage();
			
			if (key != null && key.equalsIgnoreCase("description"))
				return getDescription(m.getTask());
			else
				return m.getTask().getTitle();
		}
		
		return null;
	}
	
	private static String getDescription(Task task) {
		StringBuffer description = new StringBuffer();
		
		if (TaskUtils.isInDueDateReminderZone(task)) {
			description.append("Due by ");
			description.append(StringValueCalendar.INSTANCE_DATE_TIME.getString(task.getDueDate()));
		} else if (TaskUtils.isInStartDateReminderZone(task)) {
			description.append("Starts on ");
			description.append(StringValueCalendar.INSTANCE_DATE_TIME.getString(task.getStartDate()));
		}
		
		return description.toString();
	}
	
}
