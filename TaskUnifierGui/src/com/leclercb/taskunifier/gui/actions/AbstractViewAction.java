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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.gui.components.pro.ProDialog;
import com.leclercb.taskunifier.gui.components.views.ViewList;
import com.leclercb.taskunifier.gui.components.views.ViewType;
import com.leclercb.taskunifier.gui.components.views.ViewUtils;

public abstract class AbstractViewAction extends AbstractAction implements PropertyChangeListener {
	
	private ViewType[] enabledViews;
	private boolean proRequired;
	
	public AbstractViewAction(ViewType... enabledViews) {
		this(null, null, enabledViews);
	}
	
	public AbstractViewAction(String title, ViewType... enabledViews) {
		this(title, null, enabledViews);
	}
	
	public AbstractViewAction(String title, Icon icon, ViewType... enabledViews) {
		super(title, icon);
		
		this.proRequired = false;
		this.initialize(enabledViews);
	}
	
	public ViewType[] getEnabledViews() {
		return this.enabledViews.clone();
	}
	
	public boolean isProRequired() {
		return this.proRequired;
	}
	
	public void setProRequired(boolean proRequired) {
		this.proRequired = proRequired;
	}
	
	private void initialize(final ViewType... enabledViews) {
		this.enabledViews = enabledViews;
		
		this.setEnabled(this.shouldBeEnabled());
		
		if (this.enabledViews != null && this.enabledViews.length != 0) {
			ViewList.getInstance().addPropertyChangeListener(
					ViewList.PROP_CURRENT_VIEW,
					new WeakPropertyChangeListener(ViewList.getInstance(), this));
		}
	}
	
	public boolean shouldBeEnabled() {
		if (this.enabledViews != null && this.enabledViews.length != 0) {
			for (ViewType view : this.enabledViews)
				if (view.equals(ViewUtils.getCurrentViewType())
						&& ViewList.getInstance().getCurrentView().isLoaded())
					return true;
			
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (EqualsUtils.equals(
				ViewList.PROP_CURRENT_VIEW,
				evt.getPropertyName())) {
			AbstractViewAction.this.setEnabled(AbstractViewAction.this.shouldBeEnabled());
		}
	}
	
	public static void showProRequired() {
		ProDialog dialog = new ProDialog();
		dialog.setVisible(true);
		dialog.dispose();
	}
	
}
