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

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.beans.ModelBean;
import com.leclercb.taskunifier.api.models.beans.TaskStatusBean;

import java.util.Calendar;

public class TaskStatus extends AbstractModel {

    protected TaskStatus(TaskStatusBean bean, boolean loadReferenceIds) {
        this(bean.getModelId(), bean.getTitle());
        this.loadBean(bean, loadReferenceIds);
    }

    protected TaskStatus(String title) {
        this(new ModelId(), title);
    }

    protected TaskStatus(ModelId modelId, String title) {
        super(modelId, title);

        this.getFactory().register(this);
    }

    @Override
    public TaskStatus clone(ModelId modelId) {
        TaskStatus taskStatus = this.getFactory().create(modelId, this.getTitle());

        // After all other setXxx methods
        taskStatus.setOrder(this.getOrder());
        taskStatus.addProperties(this.getProperties());
        taskStatus.setModelStatus(this.getModelStatus());
        taskStatus.setModelCreationDate(Calendar.getInstance());
        taskStatus.setModelUpdateDate(Calendar.getInstance());

        return taskStatus;
    }

    @Override
    public TaskStatusFactory<TaskStatus, TaskStatusBean> getFactory() {
        return TaskStatusFactory.getInstance();
    }

    @Override
    public ModelType getModelType() {
        return ModelType.TASK_STATUS;
    }

    @Override
    public void loadBean(ModelBean bean, boolean loadReferenceIds) {
        CheckUtils.isNotNull(bean);
        CheckUtils.isInstanceOf(bean, TaskStatusBean.class);

        super.loadBean(bean, loadReferenceIds);
    }

    @Override
    public TaskStatusBean toBean() {
        return (TaskStatusBean) super.toBean();
    }

}
