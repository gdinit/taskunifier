/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal;

import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import javax.help.HelpSet;

import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.googlecal.help.PluginHelp;
import com.leclercb.taskunifier.plugin.googlecal.resources.Resources;

public class GoogleCalGuiPlugin extends GoogleCalPlugin implements SynchronizerGuiPlugin {
	
	private HelpSet helpSet;
	
	public GoogleCalGuiPlugin() {
        GoogleCalApi.getInstance().setApplicationName("TaskUnifier");

        try {
            Properties properties = new Properties();
            properties.load(Resources.class.getResourceAsStream("general.properties"));

            GoogleCalApi.getInstance().setClientId(properties.get("googlecal.client_id"));
            GoogleCalApi.getInstance().setClientSecret(properties.get("googlecal.client_secret"));
        } catch (Exception e) {
            PluginLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
	}
	
	@Override
	public String getAccountLabel() {
		return PluginApi.getUserSettings().getStringProperty(
				"plugin.googlecal.email");
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
		GoogleCalApi.getInstance().resetSynchronizerParameters(
				PluginApi.getUserSettings());
	}
	
	@Override
	public void deletePlugin() {
		if (this.helpSet != null)
			PluginApi.removeHelpSet(this.helpSet);
	}
	
	@Override
	public int getPluginApiVersion() {
		return 40; // See: Constants.PLUGIN_API_VERSION
	}
	
	@Override
	public ConfigurationPanel getConfigurationPanel(
			ConfigurationGroup configuration,
			boolean welcome) {
		return new GoogleCalConfigurationPanel(configuration, welcome);
	}
	
}
