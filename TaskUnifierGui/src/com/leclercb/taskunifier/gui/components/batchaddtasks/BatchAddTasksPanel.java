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
package com.leclercb.taskunifier.gui.components.batchaddtasks;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.renderer.DefaultListRenderer;

import com.leclercb.taskunifier.api.models.templates.TaskTemplate;
import com.leclercb.taskunifier.gui.actions.ActionBatchAddTasks;
import com.leclercb.taskunifier.gui.actions.ActionManageTaskTemplates;
import com.leclercb.taskunifier.gui.commons.models.TaskTemplateModel;
import com.leclercb.taskunifier.gui.commons.values.StringValueTaskTemplateTitle;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;

public class BatchAddTasksPanel extends JPanel {
	
	private JTextArea titlesTextArea;
	private JComboBox templateComboBox;
	
	public BatchAddTasksPanel() {
		this.initialize();
	}
	
	private void initialize() {
		this.setLayout(new MigLayout());
		
		this.titlesTextArea = new JTextArea();
		this.titlesTextArea.setEditable(true);
		
		this.templateComboBox = new JComboBox();
		this.templateComboBox.setModel(new TaskTemplateModel(true));
		this.templateComboBox.setRenderer(new DefaultListRenderer(
				StringValueTaskTemplateTitle.INSTANCE));
		
		JLabel templateLabel = new JLabel(
				Translations.getString("general.template") + ": ");
		
		this.add(
				ComponentFactory.createJScrollPane(this.titlesTextArea, true),
				"grow, push, wrap 10px");
		this.add(templateLabel, "split");
		this.add(this.templateComboBox, "growx, pushx");
		
		JButton manageTemplates = new JButton(new ActionManageTaskTemplates(
				16,
				16));
		manageTemplates.setText(null);
		
		this.add(manageTemplates);
	}
	
	public void batchAddTasks() {
		String titles = this.titlesTextArea.getText();
		TaskTemplate template = (TaskTemplate) this.templateComboBox.getSelectedItem();
		
		if (titles != null && titles.trim().length() != 0) {
			String[] titleArray = titles.trim().split("\n");
			ActionBatchAddTasks.batchAddTasks(template, titleArray);
		}
		
		this.titlesTextArea.setText(null);
		this.templateComboBox.setSelectedItem(null);
	}
	
	public void reset() {
		this.titlesTextArea.setText(null);
		this.templateComboBox.setSelectedItem(null);
	}
	
}
