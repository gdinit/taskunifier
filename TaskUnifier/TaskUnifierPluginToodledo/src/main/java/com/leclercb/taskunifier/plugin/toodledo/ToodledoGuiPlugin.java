/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo;

import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import javax.help.HelpSet;

import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.toodledo.help.PluginHelp;
import com.leclercb.taskunifier.plugin.toodledo.resources.Resources;
import com.leclercb.taskunifier.plugin.toodledo.translations.PluginTranslations;

public class ToodledoGuiPlugin extends ToodledoPlugin implements SynchronizerGuiPlugin {
	
	private HelpSet helpSet;
	
	public ToodledoGuiPlugin() {
		PluginTranslations.setLocale(PluginApi.getLocale());
		ToodledoApi.getInstance().setApplicationId("taskunifier");
		ToodledoApi.getInstance().setVersion(getVersionFromConstants());

        try {
            Properties properties = new Properties();
            properties.load(Resources.class.getResourceAsStream("general.properties"));

            ToodledoApi.getInstance().setApiKey(properties.get("toodledo.api_key"));
        } catch (Exception e) {
            PluginLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
	}
	
	@Override
	public String getAccountLabel() {
		return PluginApi.getUserSettings().getStringProperty("toodledo.email");
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
		ToodledoApi.getInstance().resetSynchronizerParameters(
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
		return new ToodledoConfigurationPanel(configuration, welcome);
	}
	
	private static int getVersionFromConstants() {
		try {
			String version = Constants.getVersion();
			version = version.replaceAll("\\.", "");
			return Integer.parseInt(version);
		} catch (Exception e) {
			return 0;
		}
	}
	
}
