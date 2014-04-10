/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls.exc;

import java.util.List;

import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerSettingsException;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors;
import com.leclercb.taskunifier.plugin.toodledo.translations.PluginTranslations;

public class OrganiTaskSettingsException extends SynchronizerSettingsException {
	
	private ToodledoErrors error;
	
	public <M extends Model> OrganiTaskSettingsException(
            List<M> models,
            ToodledoErrors error) {
		super(
				error.isExpected(),
				com.leclercb.taskunifier.plugin.toodledo.OrganiTaskApi.getInstance().getApiId(),
				error.getCode() + "",
				PluginTranslations.getString(error.getTranslation())
						+ ToodledoErrors.toString(models));
		
		this.error = error;
	}
	
	public OrganiTaskSettingsException(
            boolean expected,
            String apiId,
            String code,
            String message) {
		super(expected, apiId, code, message);
	}
	
	public OrganiTaskSettingsException(
            boolean expected,
            String apiId,
            String code,
            String message,
            Throwable throwable) {
		super(expected, apiId, code, message, throwable);
	}
	
	public ToodledoErrors getError() {
		return this.error;
	}
	
}
