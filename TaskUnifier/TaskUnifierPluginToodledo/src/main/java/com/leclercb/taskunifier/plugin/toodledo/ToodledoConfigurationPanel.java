/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationField;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.DefaultConfigurationPanel;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.toodledo.configuration.fields.ToodledoEmailFieldType;
import com.leclercb.taskunifier.plugin.toodledo.configuration.fields.ToodledoEnableSSLFieldType;
import com.leclercb.taskunifier.plugin.toodledo.configuration.fields.ToodledoSubTasksFieldType;

public class ToodledoConfigurationPanel extends DefaultConfigurationPanel {

    public ToodledoConfigurationPanel(
            ConfigurationGroup configuration,
            boolean welcome) {
        super(configuration, "plugin_toodledo_configuration");
        this.initialize(welcome);
        this.pack();
    }

    private void initialize(boolean welcome) {
        this.addField(new ConfigurationField(
                "EMAIL",
                PluginApi.getTranslation("general.email"),
                new ToodledoEmailFieldType()));

        this.addField(new ConfigurationField(
                "SEPARATOR_1",
                null,
                new ConfigurationFieldType.Separator()));

        this.addField(new ConfigurationField(
                "ENABLE_SSL",
                null,
                new ToodledoEnableSSLFieldType()));

        this.addField(new ConfigurationField(
                "SUB_TASKS",
                null,
                new ToodledoSubTasksFieldType()));
    }

}
