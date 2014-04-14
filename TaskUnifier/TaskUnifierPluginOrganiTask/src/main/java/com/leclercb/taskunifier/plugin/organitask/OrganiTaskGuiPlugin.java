/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.taskunifier.gui.api.synchronizer.SynchronizerGuiPlugin;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.plugins.PluginLogger;
import com.leclercb.taskunifier.plugin.organitask.help.PluginHelp;
import com.leclercb.taskunifier.plugin.organitask.resources.Resources;
import com.leclercb.taskunifier.plugin.organitask.translations.PluginTranslations;

import javax.help.HelpSet;
import java.net.URL;
import java.util.logging.Level;

public class OrganiTaskGuiPlugin extends OrganiTaskPlugin implements SynchronizerGuiPlugin {

    private HelpSet helpSet;

    public OrganiTaskGuiPlugin() {
        PluginTranslations.setLocale(PluginApi.getLocale());

        try {
            PropertyMap properties = new PropertyMap();
            properties.load(Resources.class.getResourceAsStream("private.properties"));

            OrganiTaskApi.getInstance().setClientId(properties.getStringProperty("organitask.client_id"));
            OrganiTaskApi.getInstance().setClientRandomId(properties.getStringProperty("organitask.client_random_id"));
            OrganiTaskApi.getInstance().setClientSecret(properties.getStringProperty("organitask.client_secret"));
        } catch (Exception e) {
            PluginLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public String getAccountLabel() {
        return PluginApi.getUserSettings().getStringProperty("plugin.organitask.email");
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
        OrganiTaskApi.getInstance().resetSynchronizerParameters(
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
        return new OrganiTaskConfigurationPanel(configuration, welcome);
    }

}
