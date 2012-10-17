package com.leclercb.taskunifier.gui.settings;

import java.util.List;

import com.leclercb.taskunifier.api.models.DeprecatedModelId;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelFactory;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;
import com.leclercb.taskunifier.api.models.utils.ModelFactoryUtils;

@SuppressWarnings("deprecation")
public final class ModelVersion {
	
	private ModelVersion() {
		
	}
	
	public static void updateModels() {
		for (ModelType type : ModelType.values()) {
			ModelFactory<?, ?, ?, ?> factory = ModelFactoryUtils.getFactory(type);
			for (Object object : factory.getList()) {
				Model model = (Model) object;
				
				if (model.getModelId() instanceof DeprecatedModelId)
					if (!((DeprecatedModelId) model.getModelId()).isNew())
						if (model.getModelReferenceId("toodledo") == null)
							model.addModelReferenceId(
									"toodledo",
									model.getModelId().getId());
			}
		}
		
		fixTaskStatus();
	}
	
	private static void fixTaskStatus() {
		List<Task> tasks = TaskFactory.getInstance().getList();
		for (Task task : tasks) {
			if (!task.getModelStatus().isEndUserStatus())
				continue;
			
			if (task.getStatus() == null)
				continue;
			
			if (!task.getStatus().equalsIgnoreCase("NEXT_ACTION"))
				continue;
			
			task.setStatus("Next Action");
		}
	}
	
}
