/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googletasks;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.googletasks.configuration.fields.GoogleTasksEmailFieldType;
import com.leclercb.taskunifier.plugin.googletasks.configuration.fields.GoogleTasksTaskListNameFieldType;
import com.leclercb.taskunifier.plugin.googletasks.translations.PluginTranslations;

public class GoogleTasksConfigurationPanel extends DefaultConfigurationPanel {
	
	public GoogleTasksConfigurationPanel(
			ConfigurationGroup configuration,
			boolean welcome) {
		super(configuration, "plugin_googletasks_configuration");
		
		this.initialize(welcome);
		this.pack();
	}
	
	private void initialize(boolean welcome) {
		this.addField(new ConfigurationField(
				"EMAIL",
				PluginApi.getTranslation("general.email"),
				new GoogleTasksEmailFieldType()));
		
		this.addField(new ConfigurationField(
				"SEPARATOR_1",
				null,
				new ConfigurationFieldType.Separator()));
		
		this.addField(new ConfigurationField(
				"TASK_LIST_NAME",
				PluginTranslations.getString("task_list_name"),
				new GoogleTasksTaskListNameFieldType()));
	}
	
}
