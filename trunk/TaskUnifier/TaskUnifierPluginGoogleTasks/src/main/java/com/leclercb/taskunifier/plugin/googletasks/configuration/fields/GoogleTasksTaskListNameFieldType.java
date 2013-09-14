/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googletasks.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class GoogleTasksTaskListNameFieldType extends ConfigurationFieldType.TextField {
	
	public GoogleTasksTaskListNameFieldType() {
		super(PluginApi.getUserSettings(), "plugin.googletasks.task_list_name");
	}
	
	@Override
	public String getPropertyValue() {
		if (PluginApi.getUserSettings().getStringProperty(
				"plugin.googlecal.task_list_name") == null)
			return "TaskUnifier";
		
		return super.getPropertyValue();
	}
	
}
