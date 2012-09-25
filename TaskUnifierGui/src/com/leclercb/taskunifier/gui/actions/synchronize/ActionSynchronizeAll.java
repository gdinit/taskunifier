package com.leclercb.taskunifier.gui.actions.synchronize;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import com.leclercb.taskunifier.gui.actions.ActionSynchronize;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public class ActionSynchronizeAll extends ActionSynchronize {
	
	private ConfigurationGroup configurationGroup;
	
	public ActionSynchronizeAll(ConfigurationGroup configurationGroup) {
		super(22, 22, false);
		
		this.putValue(
				Action.NAME,
				Translations.getString("action.synchronize_all"));
		
		this.configurationGroup = configurationGroup;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (this.configurationGroup != null) {
			this.configurationGroup.saveAndApplyConfig();
		}
		
		SynchronizerUtils.resetAllConnections();
		SynchronizerUtils.resetAllSynchronizers();
		
		super.actionPerformed(event);
	}
	
}
