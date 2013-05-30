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
package com.leclercb.taskunifier.gui.components.propertyaccessoredit;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.renderer.DefaultListRenderer;

import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;
import com.leclercb.taskunifier.gui.commons.models.PropertyAccessorTypeModel;
import com.leclercb.taskunifier.gui.commons.values.StringValuePropertyAccessorType;
import com.leclercb.taskunifier.gui.utils.FormBuilder;
import com.leclercb.taskunifier.gui.utils.TaskCustomColumnList;

public class EditTaskPropertyAccessorPanel extends JPanel {
	
	private PropertyAccessor<Task> accessor;
	
	private JComboBox typeField;
	private JTextField labelField;
	
	public EditTaskPropertyAccessorPanel() {
		this.initialize();
	}
	
	public PropertyAccessor<Task> getPropertyAccessor() {
		return this.accessor;
	}
	
	public void setPropertyAccessor(PropertyAccessor<Task> accessor) {
		this.accessor = accessor;
		
		if (this.accessor == null) {
			this.typeField.setEnabled(true);
			this.typeField.setSelectedItem(PropertyAccessorType.STRING);
			this.labelField.setText(null);
		} else {
			this.typeField.setEnabled(false);
			this.typeField.setSelectedItem(this.accessor.getType());
			this.labelField.setText(this.accessor.getLabel());
		}
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		FormBuilder builder = new FormBuilder(
				"right:pref, 4dlu, fill:default:grow");
		
		this.typeField = new JComboBox(new PropertyAccessorTypeModel(false));
		
		this.typeField.setRenderer(new DefaultListRenderer(
				StringValuePropertyAccessorType.INSTANCE));
		
		this.labelField = new JTextField();
		
		builder.appendI15d(
				"general.propertyaccessor.type",
				true,
				this.typeField);
		builder.appendI15d(
				"general.propertyaccessor.label",
				true,
				this.labelField);
		
		this.add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	public void editPropertyAccessor() {
		PropertyAccessorType type = (PropertyAccessorType) this.typeField.getSelectedItem();
		String label = this.labelField.getText();
		
		if (this.accessor == null) {
			TaskCustomColumnList.getInstance().addColumn(type, label);
		} else {
			TaskCustomColumnList.getInstance().renameColumn(
					this.accessor,
					label);
		}
	}
	
}
