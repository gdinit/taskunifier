/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal.configuration.fields;

import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.googlecal.GoogleCalApi;

public class GoogleCalEmailFieldType extends ConfigurationFieldType.TextField {
	
	public GoogleCalEmailFieldType() {
		super(PluginApi.getUserSettings(), "plugin.googlecal.email");
	}
	
	@Override
	public void saveAndApplyConfig() {
		String currentEmail = this.getPropertyValue();
		
		if (!EqualsUtils.equalsStringIgnoreCase(
				currentEmail,
				this.getFieldValue())) {
			GoogleCalApi.getInstance().resetConnectionParameters(
					PluginApi.getUserSettings());
		}
		
		super.saveAndApplyConfig();
	}
	
}
