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
package com.leclercb.taskunifier.gui.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;

import javax.swing.KeyStroke;

import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.gui.components.views.ViewItem;
import com.leclercb.taskunifier.gui.components.views.ViewList;
import com.leclercb.taskunifier.gui.components.views.ViewType;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.main.frames.FrameView;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ActionNewWindow extends AbstractViewAction {
	
	public ActionNewWindow(int width, int height) {
		super(
				Translations.getString("action.new_window"),
				ImageUtils.getResourceImage("window_add.png", width, height));
		
		this.putValue(
				SHORT_DESCRIPTION,
				Translations.getString("action.new_window"));
		
		this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_W,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
						+ InputEvent.SHIFT_DOWN_MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		ActionNewWindow.newWindow();
	}
	
	public static void newWindow() {
		newWindow(true);
	}
	
	public static void newWindow(boolean createTabs) {
		FrameView frameView = FrameUtils.createFrameView();
		
		if (createTabs) {
			ViewItem mainViewItem = null;
			
			int addedViews = 0;
			
			String views = Main.getSettings().getStringProperty(
					"window.views." + frameView.getFrameId() + ".types",
					"");
			String[] viewArray = views.split(";");
			
			String labels = Main.getSettings().getStringProperty(
					"window.views." + frameView.getFrameId() + ".labels",
					"");
			String[] labelArray = labels.split(";");
			
			if (viewArray.length == labelArray.length) {
				for (int i = 0; i < viewArray.length; i++) {
					String view = viewArray[i].trim();
					String label = labelArray[i].trim();
					
					if (view.length() == 0)
						continue;
					
					try {
						ViewType viewType = ViewType.valueOf(view);
						
						if (viewType == null)
							continue;
						
						ViewItem viewItem = ActionAddTab.addTab(
								viewType,
								frameView);
						addedViews++;
						
						viewItem.setLabel(label);
						
						if (i == 0)
							mainViewItem = viewItem;
					} catch (Exception e) {
						GuiLogger.getLogger().log(
								Level.WARNING,
								"Cannot load view type: " + view,
								e);
					}
				}
			}
			
			if (addedViews == 0) {
				mainViewItem = ActionAddTab.addTab(ViewType.TASKS, frameView);
				
				ActionAddTab.addTab(ViewType.NOTES, frameView);
				ActionAddTab.addTab(ViewType.CALENDAR, frameView);
			}
			
			ViewList.getInstance().setCurrentView(mainViewItem);
		}
	}
	
}
