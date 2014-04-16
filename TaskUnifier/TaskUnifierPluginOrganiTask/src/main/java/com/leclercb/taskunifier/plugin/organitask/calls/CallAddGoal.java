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
import com.leclercb.taskunifier.gui.api.models.GuiModel;

final class CallAddGoal extends AbstractCallGoal {

    public GoalBean addGoal(String accessToken, Goal goal, boolean syncParent) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(goal);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        if (syncParent && goal.getParent() != null && goal.getParent().getModelReferenceId("organitask") != null)
            node.put("parent_id", goal.getParent().getModelReferenceId("organitask"));
        else
            node.put("parent_id", (String) null);

        node.put("title", goal.getTitle());
        node.put("level", OrganiTaskTranslations.translateGoalLevel(goal.getLevel()));

        if (goal instanceof GuiModel) {
            node.put("color", OrganiTaskTranslations.translateColor(((GuiModel) goal).getColor()));
        }

        String content = super.call("POST", "/goals", accessToken, node.toString());

        return this.getResponseMessage(content)[0];
    }

}
