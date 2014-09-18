/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class CallEditTaskParent extends AbstractCallTask {

    private static final int MAX = 50;

    public void editTaskParent(
            ToodledoAccountInfo accountInfo,
            String accessToken,
            Task task) throws SynchronizerException {
        CheckUtils.isNotNull(task);

        this.editTasksParent(accountInfo, accessToken, new Task[]{task});
    }

    public void editTasksParent(
            ToodledoAccountInfo accountInfo,
            String accessToken,
            Task[] tasks) throws SynchronizerException {
        List<Task> taskList = new ArrayList<Task>(Arrays.asList(tasks));
        while (taskList.size() != 0) {
            List<Task> list = taskList.subList(
                    0,
                    (MAX > taskList.size() ? taskList.size() : MAX));
            this.editTasksParentMax(accountInfo, accessToken, list);
            list.clear();
        }
    }

    private void editTasksParentMax(
            ToodledoAccountInfo accountInfo,
            String accessToken,
            List<Task> tasks) throws SynchronizerException {
        CheckUtils.isNotNull(accessToken);
        CheckUtils.isNotNull(tasks);

        for (Task task : tasks)
            if (task.getModelReferenceId("toodledo") == null)
                throw new IllegalArgumentException("You cannot edit a new task");

        JSONArray jsonArray = new JSONArray();

        try {
            for (Task task : tasks) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("id", task.getModelReferenceId("toodledo"));

                if (task.getRoot() != null
                        && task.getRoot().getModelReferenceId("toodledo") != null)
                    jsonObject.put(
                            "parent",
                            task.getRoot().getModelReferenceId("toodledo"));
                else
                    jsonObject.put("parent", "0");

                jsonObject.put("repeat", RepeatConverter.getRepeat(task.getRepeat()));
                jsonObject.put(
                        "repeatfrom",
                        ToodledoTranslations.translateTaskRepeatFrom(task.getRepeatFrom()));

                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            throw new SynchronizerException(
                    false,
                    "Error during JSON request creation",
                    e);
        }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("access_token", accessToken));
        params.add(new BasicNameValuePair("tasks", jsonArray.toString()));
        params.add(new BasicNameValuePair("f", "xml"));

        String scheme = super.getScheme(accountInfo);
        String content = super.callPost(scheme, "/3/tasks/edit.php", params);

        this.getResponseMessage(tasks, content, accountInfo);
    }

}
