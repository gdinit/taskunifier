package com.leclercb.taskunifier.gui.api.rules.actions;

import java.util.List;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.gui.api.rules.TaskRuleAction;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;
import com.leclercb.taskunifier.gui.utils.TaskUtils;

public class TaskRuleActionDeleteSubTask implements TaskRuleAction {
	
	private TaskFilter filter;
	
	public TaskRuleActionDeleteSubTask() {
		this(null);
	}
	
	public TaskRuleActionDeleteSubTask(TaskFilter filter) {
		this.filter = filter;
	}
	
	@Override
	public void execute(Task task) {
		List<Task> subTasks = task.getChildren();
		
		for (Task subTask : subTasks) {
			if (!subTask.getModelStatus().isEndUserStatus())
				continue;
			
			if (!TaskUtils.showUnindentTask(subTask, this.filter))
				continue;
			
			TaskFactory.getInstance().markToDelete(subTask);
		}
	}
	
}
