/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalCalendarNameFieldType;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalDayEventIfSameDayFieldType;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalDueDateReminderMethodFieldType;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalDueEventColorFieldType;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalEmailFieldType;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalEventTransparencyFieldType;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalPublishAfterNowFieldType;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalPublishBeforeNowFieldType;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalPublishEndDateEventsFieldType;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalPublishStartDateEventsFieldType;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalStartDateReminderMethodFieldType;
import com.leclercb.taskunifier.plugin.googlecal.configuration.fields.GoogleCalStartEventColorFieldType;
import com.leclercb.taskunifier.plugin.googlecal.translations.PluginTranslations;

public class GoogleCalConfigurationPanel extends DefaultConfigurationPanel {
	
	public GoogleCalConfigurationPanel(
			ConfigurationGroup configuration,
			boolean welcome) {
		super(configuration, "plugin_googlecal_main");
		
		this.initialize(welcome);
		this.pack();
	}
	
	private void initialize(boolean welcome) {
		this.addField(new ConfigurationField(
				"EMAIL",
				PluginApi.getTranslation("general.email"),
				new GoogleCalEmailFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_1",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"CALENDAR_NAME",
				PluginTranslations.getString("calendar_name"),
				new GoogleCalCalendarNameFieldType()));
		
		this.addField(new ConfigurationField(
				"DAY_EVENT_IF_SAME_DAY",
				null,
				new GoogleCalDayEventIfSameDayFieldType()));
		
		this.addField(new ConfigurationField(
				"PUBLISH_START_DATE_EVENTS",
				null,
				new GoogleCalPublishStartDateEventsFieldType()));
		
		this.addField(new ConfigurationField(
				"PUBLISH_END_DATE_EVENTS",
				null,
				new GoogleCalPublishEndDateEventsFieldType()));
		
		this.addField(new ConfigurationField(
				"PUBLISH_BEFORE_NOW",
				PluginTranslations.getString("publish_before_now"),
				new GoogleCalPublishBeforeNowFieldType()));
		
		this.addField(new ConfigurationField(
				"PUBLISH_AFTER_NOW",
				PluginTranslations.getString("publish_after_now"),
				new GoogleCalPublishAfterNowFieldType()));
		
		this.addField(new ConfigurationField(
				"EVENT_TRANSPARENCY",
				null,
				new GoogleCalEventTransparencyFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_2",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"START_DATE_REMINDER_METHOD",
				PluginTranslations.getString("start_date_reminder_method"),
				new GoogleCalStartDateReminderMethodFieldType()));
		
		this.addField(new ConfigurationField(
				"DUE_DATE_REMINDER_METHOD",
				PluginTranslations.getString("due_date_reminder_method"),
				new GoogleCalDueDateReminderMethodFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_3",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"START_EVENT_COLOR",
				PluginTranslations.getString("start_event_color"),
				new GoogleCalStartEventColorFieldType()));
		
		this.addField(new ConfigurationField(
				"DUE_EVENT_COLOR",
				PluginTranslations.getString("due_event_color"),
				new GoogleCalDueEventColorFieldType()));
	}
	
}
