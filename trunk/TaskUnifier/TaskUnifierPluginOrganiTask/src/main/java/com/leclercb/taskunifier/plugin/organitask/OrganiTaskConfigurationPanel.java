/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask;

import com.leclercb.taskunifier.plugin.organitask.configuration.fields.OrganiTaskEmailFieldType;
import com.leclercb.taskunifier.plugin.organitask.configuration.fields.OrganiTaskEnableSSLFieldType;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class OrganiTaskConfigurationPanel extends DefaultConfigurationPanel {

    public OrganiTaskConfigurationPanel(
            ConfigurationGroup configuration,
            boolean welcome) {
        super(configuration, "plugin_organitask_configuration");
        this.initialize(welcome);
        this.pack();
    }

    private void initialize(boolean welcome) {
        this.addField(new ConfigurationField(
                "EMAIL",
                PluginApi.getTranslation("general.email"),
                new OrganiTaskEmailFieldType()));

        this.addField(new ConfigurationField(
                "SEPARATOR_1",
                null,
                new ConfigurationFieldType.Separator()));

        this.addField(new ConfigurationField(
                "ENABLE_SSL",
                null,
                new OrganiTaskEnableSSLFieldType()));
    }

}
