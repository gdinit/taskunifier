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
package com.leclercb.taskunifier.gui.components.tasktemplates;

import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.leclercb.taskunifier.api.models.templates.TaskTemplate;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;

public class TaskTemplateConfigurationPanel extends JSplitPane {
	
	private TaskTemplateList templateList;
	private TaskTemplatePanel taskTemplatePanel;
	
	public TaskTemplateConfigurationPanel() {
		this.initialize();
	}
	
	public void setSelectedTemplate(TaskTemplate template) {
		this.templateList.setSelectedTemplate(template);
	}
	
	private void initialize() {
		this.taskTemplatePanel = new TaskTemplatePanel();
		this.taskTemplatePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setRightComponent(ComponentFactory.createJScrollPane(
				this.taskTemplatePanel,
				false));
		
		this.templateList = new TaskTemplateList(
				this.taskTemplatePanel.getTemplateTitle());
		this.setLeftComponent(this.templateList);
		
		this.setDividerLocation(250);
	}
	
	private class TaskTemplateList extends com.leclercb.taskunifier.gui.components.tasktemplates.TaskTemplateList {
		
		public TaskTemplateList(JTextField templateTitle) {
			super(templateTitle);
		}
		
		@Override
		public void templateSelected(TaskTemplate template) {
			TaskTemplateConfigurationPanel.this.taskTemplatePanel.setTemplate(template);
		}
		
	}
	
}
