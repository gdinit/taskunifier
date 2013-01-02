package com.leclercb.taskunifier.gui.api.rules;

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
