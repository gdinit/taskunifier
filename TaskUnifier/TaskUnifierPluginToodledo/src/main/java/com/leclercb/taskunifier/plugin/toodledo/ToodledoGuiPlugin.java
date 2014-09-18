/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;
import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoTranslations;
import com.leclercb.taskunifier.plugin.toodledo.help.PluginHelp;
import com.leclercb.taskunifier.plugin.toodledo.resources.Resources;
import com.leclercb.taskunifier.plugin.toodledo.translations.PluginTranslations;

import javax.help.HelpSet;
import java.net.URL;
import java.util.logging.Level;

public class ToodledoGuiPlugin extends ToodledoPlugin implements SynchronizerGuiPlugin {

    private HelpSet helpSet;

    public ToodledoGuiPlugin() {
        PluginTranslations.setLocale(PluginApi.getLocale());
        ToodledoApi.getInstance().setClientId("taskunifier3");
        ToodledoApi.getInstance().setVersion(getVersionFromConstants());

        try {
            PropertyMap properties = new PropertyMap();
            properties.load(Resources.class.getResourceAsStream("private.properties"));

            ToodledoApi.getInstance().setApiKey(properties.getStringProperty("toodledo.api_key"));
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
        ToodledoTranslations.createDefaultTaskStatuses();

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
        return 41; // See: Constants.PLUGIN_API_VERSION
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
