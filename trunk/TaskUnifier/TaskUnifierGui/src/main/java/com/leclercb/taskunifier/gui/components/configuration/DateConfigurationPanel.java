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
package com.leclercb.taskunifier.gui.components.configuration;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationTab;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.components.configuration.fields.date.DateFormatFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.date.DayBreakHourFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.date.DayEndHourFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.date.DayStartHourFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.date.TimeFormatFieldType;
import com.leclercb.taskunifier.gui.components.configuration.fields.date.TimeZoneFieldType;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;

public class DateConfigurationPanel extends DefaultConfigurationPanel {
	
	private boolean welcome;
	
	public DateConfigurationPanel(
			ConfigurationGroup configuration,
			boolean showAfterRestart,
			boolean welcome) {
		super(
				ConfigurationTab.DATE,
				configuration,
				showAfterRestart,
				"configuration_date");
		
		this.welcome = welcome;
		
		this.initialize();
		this.pack();
	}
	
	private void initialize() {
		this.addField(new ConfigurationField(
				"TIMEZONE",
				Translations.getString("configuration.date.timezone"),
				true,
				new TimeZoneFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_1",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"DATE_FORMAT",
				Translations.getString("configuration.date.date_format"),
				true,
				new DateFormatFieldType()));
		
		this.addField(new ConfigurationField(
				"TIME_FORMAT",
				Translations.getString("configuration.date.time_format"),
				true,
				new TimeFormatFieldType()));
		
		this.addField(new ConfigurationField(
				"SHOW_DAY_OF_WEEK",
				null,
				true,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"date.show_day_of_week",
						Translations.getString("configuration.date.show_day_of_week"))));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_2",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"USE_DUE_TIME",
				null,
				true,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"date.use_due_time",
						Translations.getString("configuration.date.use_due_time"))));
		
		this.addField(new ConfigurationField(
				"USE_START_TIME",
				null,
				true,
				new ConfigurationFieldType.CheckBox(
						Main.getSettings(),
						"date.use_start_time",
						Translations.getString("configuration.date.use_start_time"))));
		
		if (!this.welcome) {
			this.addField(new ConfigurationField(
					"SEPARATOR_3",
					null,
					new ConfigurationFieldType.Separator()));
			
			this.addField(new ConfigurationField(
					"DAY_START_HOUR",
					Translations.getString("configuration.date.day_start_hour"),
					true,
					new DayStartHourFieldType()));
			
			this.addField(new ConfigurationField(
					"DAY_BREAK_HOUR",
					Translations.getString("configuration.date.day_break_hour"),
					true,
					new DayBreakHourFieldType()));
			
			this.addField(new ConfigurationField(
					"DAY_END_HOUR",
					Translations.getString("configuration.date.day_end_hour"),
					true,
					new DayEndHourFieldType()));
			
			this.addField(new ConfigurationField(
					"SEPARATOR_4",
					null,
					new ConfigurationFieldType.Separator()));
			
			this.addField(new ConfigurationField(
					"ALWAYS_SHOW_REMINDER",
					null,
					new ConfigurationFieldType.CheckBox(
							Main.getSettings(),
							"reminder.always_show_reminder",
							Translations.getString("configuration.date.reminder.always_show_reminder"))));
			
			this.addField(new ConfigurationField(
					"SHOW_OVERDUE_TASKS",
					null,
					new ConfigurationFieldType.CheckBox(
							Main.getSettings(),
							"reminder.show_overdue_tasks",
							Translations.getString("configuration.date.reminder.show_overdue_tasks"))));
		}
	}
	
}
