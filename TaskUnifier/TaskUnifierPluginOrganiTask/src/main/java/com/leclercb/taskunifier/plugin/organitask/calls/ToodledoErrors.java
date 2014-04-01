/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.util.List;

import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerApiException;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoApiException;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoConnectionException;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoSettingsException;

public enum ToodledoErrors {
	
	ERROR_1(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 1, false, "errors.1"),
	ERROR_2(ToodledoErrorType.GENERAL, ToodledoErrorException.CONNECTION_EXCEPTION, 2, false, "errors.2"),
	ERROR_100(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 100, false, "errors.100"),
	ERROR_400(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 400, true, "errors.400"),
	ERROR_500(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 500, true, "errors.500"),
	
	ERROR_ACCOUNT_3(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 3, false, "errors.account.3"),
	ERROR_ACCOUNT_4(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 4, false, "errors.account.4"),
	ERROR_ACCOUNT_5(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 5, false, "errors.account.5"),
	ERROR_ACCOUNT_6(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 6, false, "errors.account.6"),
	ERROR_ACCOUNT_7(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 7, false, "errors.account.7"),
	ERROR_ACCOUNT_8(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 8, false, "errors.account.8"),
	ERROR_ACCOUNT_9(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 9, false, "errors.account.9"),
	ERROR_ACCOUNT_10(ToodledoErrorType.ACCOUNT, ToodledoErrorException.SETTINGS_EXCEPTION, 10, true, "errors.account.10"),
	ERROR_ACCOUNT_11(ToodledoErrorType.ACCOUNT, ToodledoErrorException.SETTINGS_EXCEPTION, 11, true, "errors.account.11"),
	ERROR_ACCOUNT_12(ToodledoErrorType.ACCOUNT, ToodledoErrorException.SETTINGS_EXCEPTION, 12, true, "errors.account.12"),
	
	ERROR_CONTEXT_3(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 3, false, "errors.context.3"),
	ERROR_CONTEXT_4(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 4, false, "errors.context.4"),
	ERROR_CONTEXT_5(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 5, true, "errors.context.5"),
	ERROR_CONTEXT_6(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 6, true, "errors.context.6"),
	ERROR_CONTEXT_7(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 7, true, "errors.context.7"),
	ERROR_CONTEXT_8(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 8, false, "errors.context.8"),
	
	ERROR_FOLDER_3(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 3, false, "errors.folder.3"),
	ERROR_FOLDER_4(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 4, false, "errors.folder.4"),
	ERROR_FOLDER_5(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 5, true, "errors.folder.5"),
	ERROR_FOLDER_6(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 6, true, "errors.folder.6"),
	ERROR_FOLDER_7(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 7, true, "errors.folder.7"),
	ERROR_FOLDER_8(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 8, false, "errors.folder.8"),
	
	ERROR_GOAL_3(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 3, false, "errors.goal.3"),
	ERROR_GOAL_4(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 4, false, "errors.goal.4"),
	ERROR_GOAL_5(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 5, true, "errors.goal.5"),
	ERROR_GOAL_6(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 6, true, "errors.goal.6"),
	ERROR_GOAL_7(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 7, true, "errors.goal.7"),
	ERROR_GOAL_8(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 8, false, "errors.goal.8"),
	
	ERROR_LOCATION_3(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 3, false, "errors.location.3"),
	ERROR_LOCATION_4(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 4, false, "errors.location.4"),
	ERROR_LOCATION_5(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 5, true, "errors.location.5"),
	ERROR_LOCATION_6(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 6, true, "errors.location.6"),
	ERROR_LOCATION_7(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 7, true, "errors.location.7"),
	ERROR_LOCATION_8(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 8, false, "errors.location.8"),
	
	ERROR_NOTE_3(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 3, false, "errors.note.3"),
	ERROR_NOTE_4(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 4, false, "errors.note.4"),
	ERROR_NOTE_6(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 6, true, "errors.note.6"),
	ERROR_NOTE_7(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 7, false, "errors.note.7"),
	ERROR_NOTE_8(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 8, false, "errors.note.8"),
	ERROR_NOTE_12(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 12, false, "errors.note.12"),
	
	ERROR_TASK_3(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 3, false, "errors.task.3"),
	ERROR_TASK_4(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 4, false, "errors.task.4"),
	ERROR_TASK_5(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 5, true, "errors.task.5"),
	ERROR_TASK_6(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 6, true, "errors.task.6"),
	ERROR_TASK_7(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 7, false, "errors.task.7"),
	ERROR_TASK_8(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 8, false, "errors.task.8"),
	ERROR_TASK_9(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 9, false, "errors.task.9"),
	ERROR_TASK_10(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 10, false, "errors.task.10"),
	ERROR_TASK_11(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 11, false, "errors.task.11"),
	ERROR_TASK_12(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 12, false, "errors.task.12"),
	ERROR_TASK_13(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 13, false, "errors.task.13");
	
	private ToodledoErrorType type;
	private ToodledoErrorException exception;
	private int code;
	private boolean expected;
	private String translation;
	
	private ToodledoErrors(
			ToodledoErrorType type,
			ToodledoErrorException exception,
			int code,
			boolean expected,
			String translation) {
		this.type = type;
		this.exception = exception;
		this.code = code;
		this.expected = expected;
		this.translation = translation;
	}
	
	public ToodledoErrorType getType() {
		return this.type;
	}
	
	public ToodledoErrorException getException() {
		return this.exception;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public boolean isExpected() {
		return this.expected;
	}
	
	public String getTranslation() {
		return this.translation;
	}
	
	public <M extends Model> void throwError(List<M> models)
			throws SynchronizerApiException {
		switch (this.exception) {
			case CONNECTION_EXCEPTION:
				throw new ToodledoConnectionException(models, this);
			case API_EXCEPTION:
				throw new ToodledoApiException(models, this);
			case SETTINGS_EXCEPTION:
				throw new ToodledoSettingsException(models, this);
		}
	}
	
	public static <M extends Model> void throwError(
			List<M> models,
			ToodledoErrorType type,
			int code,
			String message) throws SynchronizerApiException {
		for (ToodledoErrors error : ToodledoErrors.values())
			if (error.type == type && error.code == code)
				error.throwError(models);
		
		for (ToodledoErrors error : ToodledoErrors.values())
			if (error.type == ToodledoErrorType.GENERAL && error.code == code)
				error.throwError(models);
		
		throw new ToodledoApiException(
				false,
				com.leclercb.taskunifier.plugin.toodledo.OrganiTaskApi.getInstance().getApiId(),
				code + "",
				message + toString(models));
	}
	
	public static <M extends Model> String toString(List<M> models) {
		if (models == null || models.size() != 1 || models.get(0) == null)
			return "";
		
		return ": " + models.get(0).toString();
	}
	
	public static enum ToodledoErrorType {
		
		GENERAL,
		ACCOUNT,
		CONTEXT,
		FOLDER,
		GOAL,
		LOCATION,
		NOTE,
		TASK;
		
	}
	
	public static enum ToodledoErrorException {
		
		CONNECTION_EXCEPTION,
		API_EXCEPTION,
		SETTINGS_EXCEPTION;
		
	}
	
}
