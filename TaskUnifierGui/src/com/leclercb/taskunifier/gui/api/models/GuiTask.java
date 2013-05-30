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
package com.leclercb.taskunifier.gui.api.models;

import java.awt.Color;

import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.beans.ModelBean;
import com.leclercb.taskunifier.api.models.beans.TaskBean;
import com.leclercb.taskunifier.gui.api.models.beans.GuiTaskBean;

public class GuiTask extends Task implements GuiModel {
	
	public static final String PROP_SHOW_CHILDREN = "showChildren";
	
	private Color color;
	private boolean showChildren;
	
	public GuiTask(TaskBean bean, boolean loadReferenceIds) {
		super(bean, loadReferenceIds);
		
		if (!(bean instanceof GuiTaskBean))
			this.setShowChildren(true);
	}
	
	public GuiTask(String title) {
		super(title);
		this.setShowChildren(true);
	}
	
	public GuiTask(ModelId modelId, String title) {
		super(modelId, title);
		this.setShowChildren(true);
	}
	
	@Override
	public void loadBean(ModelBean bean, boolean loadReferenceIds) {
		if (bean instanceof GuiTaskBean) {
			this.setColor(((GuiTaskBean) bean).getColor());
			this.setShowChildren(((GuiTaskBean) bean).isShowChildren());
		}
		
		super.loadBean(bean, loadReferenceIds);
	}
	
	@Override
	public GuiTaskBean toBean() {
		GuiTaskBean bean = (GuiTaskBean) super.toBean();
		
		bean.setColor(this.getColor());
		bean.setShowChildren(this.isShowChildren());
		
		return bean;
	}
	
	@Override
	public Color getColor() {
		return this.color;
	}
	
	@Override
	public void setColor(Color color) {
		if (!this.checkBeforeSet(this.getColor(), color))
			return;
		
		Color oldColor = this.color;
		this.color = color;
		this.updateProperty(PROP_COLOR, oldColor, color, false, false);
	}
	
	public boolean isShowChildren() {
		return this.showChildren;
	}
	
	public void setShowChildren(boolean showChildren) {
		if (!this.checkBeforeSet(this.isShowChildren(), showChildren))
			return;
		
		boolean oldShowChildren = this.showChildren;
		this.showChildren = showChildren;
		this.updateProperty(
				PROP_SHOW_CHILDREN,
				oldShowChildren,
				showChildren,
				false,
				false);
	}
	
}
