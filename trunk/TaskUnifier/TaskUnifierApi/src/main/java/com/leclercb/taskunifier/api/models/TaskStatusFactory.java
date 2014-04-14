/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of TaskUnifier or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.leclercb.taskunifier.api.models;

import com.leclercb.taskunifier.api.models.beans.TaskStatusBean;

public class TaskStatusFactory<TS extends TaskStatus, TSB extends TaskStatusBean> extends AbstractModelFactory<TaskStatus, TaskStatusBean, TS, TSB> {

    private static TaskStatusFactory<TaskStatus, TaskStatusBean> FACTORY;

    @SuppressWarnings("unchecked")
    public static <TS extends TaskStatus, TSB extends TaskStatusBean> void initializeWithClass(
            Class<TS> modelClass,
            Class<TSB> modelBeanClass) {
        if (FACTORY == null) {
            FACTORY = (TaskStatusFactory<TaskStatus, TaskStatusBean>) new TaskStatusFactory<TS, TSB>(
                    modelClass,
                    modelBeanClass);
        }
    }

    public static TaskStatusFactory<TaskStatus, TaskStatusBean> getInstance() {
        if (FACTORY == null)
            FACTORY = new TaskStatusFactory<TaskStatus, TaskStatusBean>(
                    TaskStatus.class,
                    TaskStatusBean.class);

        return FACTORY;
    }

    private TaskStatusFactory(Class<TS> modelClass, Class<TSB> modelBeanClass) {
        super(TaskStatus.class, TaskStatusBean.class, modelClass, modelBeanClass);
    }

    @Override
    protected String getModelNodeName() {
        return "task_status";
    }

    @Override
    protected String getModelListNodeName() {
        return "task_statuses";
    }

}
