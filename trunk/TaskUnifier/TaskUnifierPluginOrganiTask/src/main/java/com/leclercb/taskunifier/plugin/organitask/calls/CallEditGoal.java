/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.organitask.calls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.beans.GoalBean;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallEditGoal extends AbstractCallGoal {

    public GoalBean editGoal(String accessToken, Goal goal) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(goal);

        if (goal.getModelReferenceId("organitask") == null)
            throw new IllegalArgumentException("You cannot edit a new goal");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("title", goal.getTitle());
        node.put("level", OrganiTaskTranslations.translateGoalLevel(goal.getLevel()));

        String content = super.call("PUT", "/goals/" + goal.getModelReferenceId("organitask"), accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
