/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import com.leclercb.commons.api.utils.HttpResponse;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerApiException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerHttpException;
import com.leclercb.taskunifier.plugin.toodledo.ToodledoApi;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoApiException;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoConnectionException;
import com.leclercb.taskunifier.plugin.toodledo.calls.exc.ToodledoSettingsException;

import java.util.List;

public enum ToodledoErrors {

    ERROR_GENERAL_0(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 0, false, "errors.general.0"),
    ERROR_GENERAL_1(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 1, false, "errors.general.1"),
    ERROR_GENERAL_2(ToodledoErrorType.GENERAL, ToodledoErrorException.CONNECTION_EXCEPTION, 2, 401, false, "errors.general.2"),
    ERROR_GENERAL_3(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 3, true, "errors.general.3"),
    ERROR_GENERAL_4(ToodledoErrorType.GENERAL, ToodledoErrorException.API_EXCEPTION, 4, true, "errors.general.4"),

    ERROR_ACCOUNT_101(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 101, false, "errors.account.101"),
    ERROR_ACCOUNT_102(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 102, false, "errors.account.102"),
    ERROR_ACCOUNT_103(ToodledoErrorType.ACCOUNT, ToodledoErrorException.API_EXCEPTION, 103, true, "errors.account.103"),

    ERROR_FOLDER_201(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 201, true, "errors.folder.201"),
    ERROR_FOLDER_202(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 202, true, "errors.folder.202"),
    ERROR_FOLDER_203(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 203, true, "errors.folder.203"),
    ERROR_FOLDER_204(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 204, false, "errors.folder.204"),
    ERROR_FOLDER_205(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 205, false, "errors.folder.205"),
    ERROR_FOLDER_206(ToodledoErrorType.FOLDER, ToodledoErrorException.API_EXCEPTION, 206, true, "errors.folder.206"),

    ERROR_CONTEXT_301(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 301, true, "errors.context.301"),
    ERROR_CONTEXT_302(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 302, true, "errors.context.302"),
    ERROR_CONTEXT_303(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 303, true, "errors.context.303"),
    ERROR_CONTEXT_304(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 304, false, "errors.context.304"),
    ERROR_CONTEXT_305(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 305, false, "errors.context.305"),
    ERROR_CONTEXT_306(ToodledoErrorType.CONTEXT, ToodledoErrorException.API_EXCEPTION, 306, true, "errors.context.306"),

    ERROR_GOAL_401(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 401, true, "errors.goal.401"),
    ERROR_GOAL_402(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 402, true, "errors.goal.402"),
    ERROR_GOAL_403(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 403, true, "errors.goal.403"),
    ERROR_GOAL_404(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 404, false, "errors.goal.404"),
    ERROR_GOAL_405(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 405, false, "errors.goal.405"),
    ERROR_GOAL_406(ToodledoErrorType.GOAL, ToodledoErrorException.API_EXCEPTION, 406, true, "errors.goal.406"),

    ERROR_LOCATION_501(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 501, true, "errors.location.501"),
    ERROR_LOCATION_502(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 502, true, "errors.location.502"),
    ERROR_LOCATION_503(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 503, true, "errors.location.503"),
    ERROR_LOCATION_504(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 504, false, "errors.location.504"),
    ERROR_LOCATION_505(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 505, false, "errors.location.505"),
    ERROR_LOCATION_506(ToodledoErrorType.LOCATION, ToodledoErrorException.API_EXCEPTION, 506, true, "errors.location.506"),

    ERROR_TASK_601(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 601, true, "errors.task.601"),
    ERROR_TASK_602(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 602, false, "errors.task.602"),
    ERROR_TASK_603(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 603, true, "errors.task.603"),
    ERROR_TASK_604(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 604, false, "errors.task.604"),
    ERROR_TASK_605(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 605, false, "errors.task.605"),
    ERROR_TASK_606(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 606, true, "errors.task.606"),
    ERROR_TASK_607(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 607, false, "errors.task.607"),
    ERROR_TASK_608(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 608, false, "errors.task.608"),
    ERROR_TASK_609(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 609, false, "errors.task.609"),
    ERROR_TASK_610(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 610, false, "errors.task.610"),
    ERROR_TASK_611(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 611, false, "errors.task.611"),
    ERROR_TASK_612(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 612, false, "errors.task.612"),
    ERROR_TASK_613(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 613, false, "errors.task.613"),
    ERROR_TASK_614(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 614, false, "errors.task.614"),
    ERROR_TASK_615(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 615, false, "errors.task.615"),
    ERROR_TASK_616(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 616, false, "errors.task.616"),
    ERROR_TASK_617(ToodledoErrorType.TASK, ToodledoErrorException.API_EXCEPTION, 617, false, "errors.task.617"),

    ERROR_NOTE_1(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 3, true, "errors.note.1"),
    ERROR_NOTE_2(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 2, false, "errors.note.2"),
    ERROR_NOTE_3(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 3, true, "errors.note.3"),
    ERROR_NOTE_4(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 4, false, "errors.note.4"),
    ERROR_NOTE_5(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 5, false, "errors.note.5"),
    ERROR_NOTE_6(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 6, true, "errors.note.6"),
    ERROR_NOTE_7(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 7, false, "errors.note.7"),
    ERROR_NOTE_11(ToodledoErrorType.NOTE, ToodledoErrorException.API_EXCEPTION, 11, false, "errors.note.11");

    private ToodledoErrorType type;
    private ToodledoErrorException exception;
    private int code;
    private int httpCode;
    private boolean expected;
    private String translation;

    private ToodledoErrors(
            ToodledoErrorType type,
            ToodledoErrorException exception,
            int code,
            boolean expected,
            String translation) {
        this(type, exception, code, 200, expected, translation);
    }

    private ToodledoErrors(
            ToodledoErrorType type,
            ToodledoErrorException exception,
            int code,
            int httpCode,
            boolean expected,
            String translation) {
        this.type = type;
        this.exception = exception;
        this.code = code;
        this.httpCode = httpCode;
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

    public int getHttpCode() {
        return httpCode;
    }

    public boolean isExpected() {
        return this.expected;
    }

    public String getTranslation() {
        return this.translation;
    }

    public void throwError()
            throws SynchronizerApiException {
        switch (this.exception) {
            case CONNECTION_EXCEPTION:
                throw new ToodledoConnectionException(null, this);
            case API_EXCEPTION:
                throw new ToodledoApiException(null, this);
            case SETTINGS_EXCEPTION:
                throw new ToodledoSettingsException(null, this);
        }
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

    public static void throwError(HttpResponse response) throws SynchronizerException {
        for (ToodledoErrors error : ToodledoErrors.values())
            if (response.getCode() == error.getHttpCode())
                error.throwError();

        throw new SynchronizerHttpException(
                false,
                response.getCode(),
                response.getMessage());
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
