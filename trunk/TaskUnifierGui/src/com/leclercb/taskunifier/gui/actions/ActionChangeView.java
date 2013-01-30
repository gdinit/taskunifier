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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.KeyStroke;

import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.components.views.ViewList;
import com.leclercb.taskunifier.gui.components.views.ViewType;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ActionChangeView extends AbstractViewAction implements PropertyChangeListener {
	
	private int width;
	private int height;
	
	public ActionChangeView(int width, int height) {
		super(Translations.getString("action.change_view"));
		
		this.width = width;
		this.height = height;
		
		this.putValue(
				SHORT_DESCRIPTION,
				Translations.getString("action.change_view"));
		
		this.putValue(
				ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.ALT_DOWN_MASK));
		
		this.updateIcon();
		
		ViewList.getInstance().addPropertyChangeListener(
				ViewList.PROP_CURRENT_VIEW,
				new WeakPropertyChangeListener(ViewList.getInstance(), this));
	}
	
	private void updateIcon() {
		ViewType viewType = ViewUtils.getCurrentViewType();
		
		if (viewType == null)
			viewType = ViewType.TASKS;
		
		switch (viewType) {
			case CALENDAR:
				this.putValue(SMALL_ICON, ImageUtils.getResourceImage(
						"change_view_calendar.png",
						this.width,
						this.height));
				break;
			case NOTES:
				this.putValue(SMALL_ICON, ImageUtils.getResourceImage(
						"change_view_notes.png",
						this.width,
						this.height));
				break;
			case TASKS:
				this.putValue(SMALL_ICON, ImageUtils.getResourceImage(
						"change_view_tasks.png",
						this.width,
						this.height));
				break;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		ActionChangeView.changeView();
	}
	
	public static void changeView() {
		if (ViewList.getInstance().getCurrentView() == null)
			return;
		
		int currentFrameId = ViewList.getInstance().getCurrentView().getFrameId();
		int currentIndex = ViewList.getInstance().getIndexOf(
				ViewList.getInstance().getCurrentView());
		
		int index = currentIndex;
		index++;
		
		while (index != currentIndex) {
			if (index >= ViewList.getInstance().getViewCount())
				index = 0;
			
			if (currentFrameId == ViewList.getInstance().getView(index).getFrameId())
				break;
			
			index++;
		}
		
		ViewList.getInstance().setCurrentView(
				ViewList.getInstance().getView(index));
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		
		if (EqualsUtils.equals(
				ViewList.PROP_CURRENT_VIEW,
				evt.getPropertyName())) {
			ActionChangeView.this.updateIcon();
		}
	}
	
}
