/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.networkshare;

import java.net.URL;

import javax.help.HelpSet;

import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.networkshare.help.PluginHelp;

public class NetworkShareGuiPlugin extends NetworkSharePlugin implements SynchronizerGuiPlugin {
	
	private HelpSet helpSet;
	
	public NetworkShareGuiPlugin() {
		
	}
	
	@Override
	public String getAccountLabel() {
		return PluginApi.getUserSettings().getStringProperty(
				"plugin.networkshare.email");
	}
	
	@Override
	public void loadPlugin() {
		try {
			URL url = PluginHelp.class.getResource("help.xml");
			this.helpSet = new HelpSet(null, url);
			PluginApi.addHelpSet(this.helpSet);
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void installPlugin() {
		NetworkShareApi.getInstance().resetSynchronizerParameters(
				PluginApi.getUserSettings());
	}
	
	@Override
	public void deletePlugin() {
		if (this.helpSet != null)
			PluginApi.removeHelpSet(this.helpSet);
	}
	
	@Override
	public int getPluginApiVersion() {
		return 41; // See: Constants.PLUGIN_API_VERSION
	}
	
	@Override
	public ConfigurationPanel getConfigurationPanel(
			ConfigurationGroup configuration,
			boolean welcome) {
		return new NetworkShareConfigurationPanel(configuration, welcome);
	}
	
}
