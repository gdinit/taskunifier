/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.sample;

import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;

public class SampleGuiPlugin extends SamplePlugin implements SynchronizerGuiPlugin {
	
	@Override
	public String getAccountLabel() {
		return null;
	}
	
	@Override
	public void loadPlugin() {
		
	}
	
	@Override
	public void installPlugin() {
		
	}
	
	@Override
	public void deletePlugin() {
		
	}
	
	@Override
	public int getPluginApiVersion() {
		return 41; // See: Constants.PLUGIN_API_VERSION
	}
	
	@Override
	public ConfigurationPanel getConfigurationPanel(
			ConfigurationGroup configuration,
			boolean welcome) {
		return new SampleConfigurationPanel(configuration, welcome);
	}
	
}
