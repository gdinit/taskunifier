/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.configuration.fields;

import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.plugin.organitask.OrganiTaskApi;

public class OrganiTaskEmailFieldType extends ConfigurationFieldType.TextField {

    public OrganiTaskEmailFieldType() {
        super(PluginApi.getUserSettings(), "plugin.organitask.email");
    }

    @Override
    public void saveAndApplyConfig() {
        String currentEmail = this.getPropertyValue();

        if (!EqualsUtils.equalsStringIgnoreCase(
                currentEmail,
                this.getFieldValue())) {
            OrganiTaskApi.getInstance().resetConnectionParameters(
                    PluginApi.getUserSettings());
        }

        super.saveAndApplyConfig();
    }

}
