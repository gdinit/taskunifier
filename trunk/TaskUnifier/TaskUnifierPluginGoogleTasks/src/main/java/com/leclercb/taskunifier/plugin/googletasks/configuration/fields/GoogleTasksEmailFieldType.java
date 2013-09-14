/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googletasks.configuration.fields;

import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.googletasks.GoogleTasksApi;

public class GoogleTasksEmailFieldType extends ConfigurationFieldType.TextField {
	
	public GoogleTasksEmailFieldType() {
		super(PluginApi.getUserSettings(), "plugin.googletasks.email");
	}
	
	@Override
	public void saveAndApplyConfig() {
		String currentEmail = this.getPropertyValue();
		
		if (!EqualsUtils.equalsStringIgnoreCase(
				currentEmail,
				this.getFieldValue())) {
			GoogleTasksApi.getInstance().resetConnectionParameters(
					PluginApi.getUserSettings());
		}
		
		super.saveAndApplyConfig();
	}
	
}
