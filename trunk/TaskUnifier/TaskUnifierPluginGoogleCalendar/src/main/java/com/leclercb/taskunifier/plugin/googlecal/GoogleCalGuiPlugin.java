/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.googlecal.help.PluginHelp;
import com.leclercb.taskunifier.plugin.googlecal.resources.Resources;

import javax.help.HelpSet;
import java.net.URL;
import java.util.logging.Level;

public class GoogleCalGuiPlugin extends GoogleCalPlugin implements SynchronizerGuiPlugin {
	
	private HelpSet helpSet;
	
	public GoogleCalGuiPlugin() {
        GoogleCalApi.getInstance().setApplicationName("TaskUnifier");

        try {
            PropertyMap properties = new PropertyMap();
            properties.load(Resources.class.getResourceAsStream("private.properties"));

            GoogleCalApi.getInstance().setClientId(properties.getStringProperty("googlecal.client_id"));
            GoogleCalApi.getInstance().setClientSecret(properties.getStringProperty("googlecal.client_secret"));
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
		return 41; // See: Constants.PLUGIN_API_VERSION
	}
	
	@Override
	public ConfigurationPanel getConfigurationPanel(
			ConfigurationGroup configuration,
			boolean welcome) {
		return new GoogleCalConfigurationPanel(configuration, welcome);
	}
	
}
