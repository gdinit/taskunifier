/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.enums.GoalLevel;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.api.models.utils.ModelFactoryUtils;
import com.leclercb.taskunifier.gui.plugins.PluginApi;

import java.util.Calendar;

final class OrganiTaskTranslations {

    private OrganiTaskTranslations() {

    }

    public static Calendar translateUTCDate(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);
        return calendar;
    }

    public static long translateUTCDate(Calendar calendar) {
        if (calendar == null)
            return 0;

        return calendar.getTimeInMillis() / 1000;
    }

    public static GoalLevel translateGoalLevel(String level) {
        if ("SHORT".equals(level))
            return GoalLevel.SHORT_TERM;

        if ("MEDIUM".equals(level))
            return GoalLevel.SHORT_TERM;

        if ("LONG".equals(level))
            return GoalLevel.LONG_TERM;

        if ("LIFE".equals(level))
            return GoalLevel.LIFE_TIME;

        return GoalLevel.SHORT_TERM;
    }

    public static String translateGoalLevel(GoalLevel level) {
        if (level == GoalLevel.SHORT_TERM)
            return "SHORT";

        if (level == GoalLevel.LONG_TERM)
            return "LONG";

        if (level == GoalLevel.LIFE_TIME)
            return "LIFE";

        return "SHORT";
    }

    public static TaskRepeatFrom translateTaskRepeatFrom(String repeatFrom) {
        return TaskRepeatFrom.valueOf(repeatFrom);
    }

    public static String translateTaskRepeatFrom(TaskRepeatFrom repeatFrom) {
        return repeatFrom.name();
    }

    public static TaskPriority translateTaskPriority(String priority) {
        return TaskPriority.valueOf(priority);
    }

    public static String translateTaskPriority(TaskPriority priority) {
        return priority.name();
    }

    public static ModelId getModelOrCreateShell(
            final ModelType modelType,
            final String foreignId) {
        Model model = ModelFactoryUtils.getFactory(modelType).get(
                "organitask",
                foreignId);

        if (model == null && foreignId != null && foreignId.length() != 0) {
            PluginApi.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    Model model = ModelFactoryUtils.getFactory(modelType).createShell(
                            new ModelId());
                    model.addModelReferenceId("organitask", foreignId);
                }

            });

            model = ModelFactoryUtils.getFactory(modelType).get(
                    "organitask",
                    foreignId);
        }

        return (model == null ? null : model.getModelId());
    }

}
