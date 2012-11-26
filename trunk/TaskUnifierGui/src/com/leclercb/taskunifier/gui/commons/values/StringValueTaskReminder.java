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
package com.leclercb.taskunifier.gui.commons.values;

import org.jdesktop.swingx.renderer.StringValue;

import com.leclercb.taskunifier.gui.translations.Translations;

public class StringValueTaskReminder implements StringValue {
	
	public static final StringValueTaskReminder INSTANCE = new StringValueTaskReminder();
	
	private StringValueTaskReminder() {
		
	}
	
	@Override
	public String getString(Object value) {
		if (value == null || !(value instanceof Integer))
			return " ";
		
		Integer reminder = (Integer) value;
		
		if (reminder == 0)
			return Translations.getString("general.task.reminder.no_reminder");
		
		if (reminder % 1440 == 0) {
			int r = reminder / 1440;
			
			if (r == 1)
				return Translations.getString("date.1_day");
			else
				return Translations.getString("date.x_days", r);
		}
		
		if (reminder % 60 == 0) {
			int r = reminder / 60;
			
			if (r == 1)
				return Translations.getString("date.1_hour");
			else
				return Translations.getString("date.x_hours", r);
		}
		
		if (reminder == 1)
			return Translations.getString("date.1_minute");
		else
			return Translations.getString("date.x_minutes", reminder);
	}
	
}
