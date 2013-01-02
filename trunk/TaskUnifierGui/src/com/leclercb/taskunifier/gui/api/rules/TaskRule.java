/*
 * TaskUnifier
 * Copyright (c) 2011, Benjamin Leclerc
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
package com.leclercb.taskunifier.gui.api.rules;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.AbstractBasicModel;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;
import com.leclercb.taskunifier.gui.utils.TaskUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class TaskRule extends AbstractBasicModel {
	
	public static final String PROP_ENABLED = "enabled";
	public static final String PROP_FILTER = "filter";
	public static final String PROP_ACTION = "action";
	
	@XStreamAlias("enabled")
	private boolean enabled;
	
	@XStreamAlias("filter")
	private TaskFilter filter;
	
	@XStreamAlias("action")
	private TaskRuleAction action;
	
	public TaskRule() {
		this(new ModelId(), "");
	}
	
	public TaskRule(String title) {
		this(new ModelId(), title);
	}
	
	public TaskRule(ModelId modelId, String title) {
		super(modelId, title);
		
		this.setEnabled(true);
		this.setFilter(new TaskFilter());
	}
	
	@Override
	public TaskRule clone(ModelId modelId) {
		TaskRule rule = new TaskRule(modelId, this.getTitle());
		
		rule.setEnabled(this.enabled);
		rule.setFilter(this.filter.clone());
		rule.setAction(this.action);
		
		return rule;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		boolean oldEnabled = enabled;
		this.enabled = enabled;
		this.updateProperty(PROP_ENABLED, oldEnabled, enabled);
	}
	
	public TaskFilter getFilter() {
		return this.filter;
	}
	
	public void setFilter(TaskFilter filter) {
		CheckUtils.isNotNull(filter);
		TaskFilter oldFilter = this.filter;
		this.filter = filter;
		this.updateProperty(PROP_FILTER, oldFilter, filter);
	}
	
	public TaskRuleAction getAction() {
		return this.action;
	}
	
	public void setAction(TaskRuleAction action) {
		TaskRuleAction oldAction = this.action;
		this.action = action;
		this.updateProperty(PROP_ACTION, oldAction, action);
	}
	
	public void execute(Task task) {
		if (!TaskUtils.showUnindentTask(task, this.filter))
			return;
		
		if (this.action != null)
			this.action.execute(task);
	}
	
}
