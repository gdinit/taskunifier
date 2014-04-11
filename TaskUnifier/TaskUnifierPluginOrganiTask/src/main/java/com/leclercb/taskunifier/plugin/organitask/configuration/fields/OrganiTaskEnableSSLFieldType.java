/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.organitask.translations.PluginTranslations;

public class OrganiTaskEnableSSLFieldType extends ConfigurationFieldType.CheckBox {

    public OrganiTaskEnableSSLFieldType() {
        super(
                PluginApi.getUserSettings(),
                "plugin.organitask.enable_ssl",
                PluginTranslations.getString("enable_ssl"));
    }

    @Override
    public Boolean getPropertyValue() {
        if (PluginApi.getUserSettings().getBooleanProperty(
                "plugin.organitask.enable_ssl") == null)
            return true;

        return super.getPropertyValue();
    }

}
