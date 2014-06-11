/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.googlecal.configuration.fields;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldType;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

public class GoogleCalDueDateReminderMethodFieldType extends ConfigurationFieldType.ComboBox {

    public GoogleCalDueDateReminderMethodFieldType() {
        super(
                getValues(),
                PluginApi.getUserSettings(),
                "plugin.googlecal.due_date_reminder_method");
    }

    @Override
    public Object getPropertyValue() {
        return PluginApi.getUserSettings().getStringProperty(
                "plugin.googlecal.due_date_reminder_method",
                "email");
    }

    @Override
    public void saveAndApplyConfig() {
        PluginApi.getUserSettings().setStringProperty(
                "plugin.googlecal.due_date_reminder_method",
                (String) this.getFieldValue());
    }

    private static String[] getValues() {
        return new String[]{"none", "email", "sms", "popup"};
    }

}
