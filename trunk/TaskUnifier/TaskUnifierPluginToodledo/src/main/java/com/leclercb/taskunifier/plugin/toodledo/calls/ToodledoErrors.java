/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import java.util.List;

import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerApiException;
import com.leclercb.taskunifier.plugin.toodledo.ToodledoApi;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoApiException;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoConnectionException;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoSettingsException;

public enum ToodledoErrors {

    ERROR_GENERAL_0(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 0, false, "errors.general.0"),
    ERROR_GENERAL_1(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 1, false, "errors.general.1"),
    ERROR_GENERAL_2(ToodledoErrorType.GENERAL, ToodledoErrorException.CONNECTION_EXCEPTION, 2, false, "errors.general.2"),
    ERROR_GENERAL_3(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 3, false, "errors.general.3"),
    ERROR_GENERAL_4(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 4, true, "errors.general.4"),
	
	ERROR_ACCOUNT_101(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 101, false, "errors.account.101"),
	ERROR_ACCOUNT_102(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 102, false, "errors.account.102"),
	ERROR_ACCOUNT_103(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 103, false, "errors.account.103"),
	
	ERROR_FOLDER_201(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 201, false, "errors.folder.201"),
	ERROR_FOLDER_202(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 202, false, "errors.folder.202"),
	ERROR_FOLDER_203(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 203, true, "errors.folder.203"),
	ERROR_FOLDER_204(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 204, true, "errors.folder.204"),
	ERROR_FOLDER_205(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 205, true, "errors.folder.205"),
	ERROR_FOLDER_206(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 206, false, "errors.folder.206"),

    ERROR_CONTEXT_301(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 301, false, "errors.context.301"),
    ERROR_CONTEXT_302(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 302, false, "errors.context.302"),
    ERROR_CONTEXT_303(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 303, true, "errors.context.303"),
    ERROR_CONTEXT_304(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 304, true, "errors.context.304"),
    ERROR_CONTEXT_305(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 305, true, "errors.context.305"),
    ERROR_CONTEXT_306(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 306, false, "errors.context.306"),

    ERROR_GOAL_401(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 401, false, "errors.goal.401"),
    ERROR_GOAL_402(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 402, false, "errors.goal.402"),
    ERROR_GOAL_403(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 403, true, "errors.goal.403"),
    ERROR_GOAL_404(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 404, true, "errors.goal.404"),
    ERROR_GOAL_405(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 405, true, "errors.goal.405"),
    ERROR_GOAL_406(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 406, false, "errors.goal.406"),

    ERROR_LOCATION_501(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 501, false, "errors.location.501"),
    ERROR_LOCATION_502(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 502, false, "errors.location.502"),
    ERROR_LOCATION_503(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 503, true, "errors.location.503"),
    ERROR_LOCATION_504(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 504, true, "errors.location.504"),
    ERROR_LOCATION_505(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 505, true, "errors.location.505"),
    ERROR_LOCATION_506(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 506, false, "errors.location.506"),
	
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
				ToodledoApi.getInstance().getApiId(),
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
