package com.leclercb.taskunifier.gui.actions;

import java.awt.event.ActionEvent;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.components.views.ViewItem;
import com.leclercb.taskunifier.gui.components.views.ViewList;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ActionRemoveTab extends AbstractViewAction {
	
	public ActionRemoveTab(int width, int height) {
		super(
				Translations.getString("action.remove_tab"),
				ImageUtils.getResourceImage("tab_remove.png", width, height));
		
		this.putValue(
				SHORT_DESCRIPTION,
				Translations.getString("action.remove_tab"));
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		ActionRemoveTab.removeTab();
	}
	
	public static void removeTab() {
		if (ViewList.getInstance().getCurrentView() == null)
			return;
		
		removeTab(ViewList.getInstance().getCurrentView());
	}
	
	public static void removeTab(ViewItem view) {
		CheckUtils.isNotNull(view);
		ViewList.getInstance().removeView(view);
	}
	
}
