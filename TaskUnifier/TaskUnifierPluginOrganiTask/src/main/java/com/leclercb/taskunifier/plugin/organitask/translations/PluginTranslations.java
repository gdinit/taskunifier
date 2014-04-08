/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.translations;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public final class PluginTranslations {

    private PluginTranslations() {

    }

    private static ResourceBundle messages;

    static {
        setLocale(getDefaultLocale());
    }

    public static Locale getLocale() {
        return Locale.getDefault();
    }

    public static void setLocale(Locale locale) {
        messages = ResourceBundle.getBundle(
                PluginTranslations.class.getName(),
                locale);
    }

    public static Locale getDefaultLocale() {
        String language = Locale.getDefault().getLanguage();

        for (Locale locale : getLocales()) {
            if (locale.getLanguage().equals(language))
                return locale;
        }

        return new Locale("en", "US");
    }

    public static List<Locale> getLocales() {
        List<Locale> locales = new ArrayList<Locale>();

        locales.add(new Locale("en", "US"));
        locales.add(new Locale("fr", "FR"));

        return locales;
    }

    public static String getString(String key) {
        if (!messages.containsKey(key))
            return "#" + key + "#";

        return messages.getString(key);
    }

    public static String getString(String key, Object... args) {
        String value = getString(key);
        return String.format(value, args);
    }

}
