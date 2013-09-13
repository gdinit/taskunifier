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
package com.leclercb.taskunifier.gui.components.models.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXColorSelectionButton;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.value.ValueModel;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.Contact;
import com.leclercb.taskunifier.api.models.ContactFactory;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.gui.actions.ActionImportVCard;
import com.leclercb.taskunifier.gui.api.models.GuiContact;
import com.leclercb.taskunifier.gui.api.models.GuiModel;
import com.leclercb.taskunifier.gui.commons.converters.ColorConverter;
import com.leclercb.taskunifier.gui.commons.models.ContactModel;
import com.leclercb.taskunifier.gui.components.models.lists.IModelList;
import com.leclercb.taskunifier.gui.components.models.lists.ModelList;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ContactUtils;
import com.leclercb.taskunifier.gui.utils.FormBuilder;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class ContactConfigurationPanel extends JSplitPane implements IModelList {
	
	private ModelList modelList;
	
	public ContactConfigurationPanel() {
		this.initialize();
	}
	
	@Override
	public void addNewModel() {
		this.modelList.addNewModel();
	}
	
	@Override
	public Model[] getSelectedModels() {
		return this.modelList.getSelectedModels();
	}
	
	@Override
	public void setSelectedModel(Model model) {
		this.modelList.setSelectedModel(model);
	}
	
	private void initialize() {
		this.setBorder(null);
		
		// Initialize Fields
		final JCheckBox contactCurrentUser = new JCheckBox();
		final JTextField contactTitle = new JTextField();
		final JTextField contactFirstName = new JTextField();
		final JTextField contactLastName = new JTextField();
		final JTextField contactEmail = new JTextField();
		final JXColorSelectionButton contactColor = new JXColorSelectionButton();
		final JButton removeColor = new JButton();
		
		// Set Disabled
		contactCurrentUser.setEnabled(false);
		contactTitle.setEnabled(false);
		contactFirstName.setEnabled(false);
		contactLastName.setEnabled(false);
		contactEmail.setEnabled(false);
		contactColor.setEnabled(false);
		removeColor.setEnabled(false);
		
		// Initialize Model List
		this.modelList = new ModelList(new ContactModel(false) {
			
			@Override
			protected void fireContentsChanged(
					Object source,
					int index0,
					int index1) {
				this.superFireContentsChanged(source, index0, index1);
			}
			
		}, contactTitle) {
			
			private BeanAdapter<Contact> adapter;
			
			{
				this.adapter = new BeanAdapter<Contact>((Contact) null, true);
				
				ValueModel titleModel = this.adapter.getValueModel(BasicModel.PROP_TITLE);
				Bindings.bind(contactTitle, titleModel);
				
				ValueModel firstNameModel = this.adapter.getValueModel(Contact.PROP_FIRSTNAME);
				Bindings.bind(contactFirstName, firstNameModel);
				
				ValueModel lastNameModel = this.adapter.getValueModel(Contact.PROP_LASTNAME);
				Bindings.bind(contactLastName, lastNameModel);
				
				ValueModel emailModel = this.adapter.getValueModel(Contact.PROP_EMAIL);
				Bindings.bind(contactEmail, emailModel);
				
				ValueModel colorModel = this.adapter.getValueModel(GuiModel.PROP_COLOR);
				Bindings.bind(contactColor, "background", new ColorConverter(
						colorModel));
			}
			
			@Override
			public Model addModel() {
				return ContactFactory.getInstance().create(
						Translations.getString("contact.default.title"));
			}
			
			@Override
			public void removeModel(Model model) {
				ContactFactory.getInstance().markToDelete((Contact) model);
			}
			
			@Override
			public void modelsSelected(Model[] models) {
				Model model = null;
				
				if (models != null && models.length == 1)
					model = models[0];
				
				this.adapter.setBean(model != null ? (Contact) model : null);
				
				contactCurrentUser.setEnabled(model != null);
				contactTitle.setEnabled(model != null);
				contactFirstName.setEnabled(model != null);
				contactLastName.setEnabled(model != null);
				contactEmail.setEnabled(model != null);
				contactColor.setEnabled(model != null);
				removeColor.setEnabled(model != null);
				
				contactCurrentUser.setSelected(EqualsUtils.equals(
						model,
						ContactUtils.getCurrentUser()));
			}
			
		};
		
		this.modelList.addButton(new JButton(new ActionImportVCard(16, 16)));
		
		this.setLeftComponent(this.modelList);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		rightPanel.setLayout(new BorderLayout());
		this.setRightComponent(ComponentFactory.createJScrollPane(
				rightPanel,
				false));
		
		FormBuilder builder = new FormBuilder(
				"right:pref, 4dlu, fill:default:grow");
		
		// Contact Title
		builder.appendI15d("general.contact.title", true, contactTitle);
		
		// Contact FirstName
		builder.appendI15d("general.contact.firstname", true, contactFirstName);
		
		// Contact LastName
		builder.appendI15d("general.contact.lastname", true, contactLastName);
		
		// Contact Email
		builder.appendI15d("general.contact.email", true, contactEmail);
		
		// Contact Color
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		builder.appendI15d("general.color", true, p);
		
		contactColor.setPreferredSize(new Dimension(24, 24));
		contactColor.setBorder(BorderFactory.createEmptyBorder());
		
		removeColor.setIcon(ImageUtils.getResourceImage("remove.png", 16, 16));
		removeColor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				((GuiContact) ContactConfigurationPanel.this.modelList.getSelectedModels()[0]).setColor(null);
			}
			
		});
		
		p.add(contactColor);
		p.add(removeColor);
		
		// Contact Current User
		builder.appendI15d("general.contact.me", true, contactCurrentUser);
		
		contactCurrentUser.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent event) {
				Model model = null;
				Model[] models = ContactConfigurationPanel.this.modelList.getSelectedModels();
				
				if (models != null && models.length == 1)
					model = models[0];
				
				if (EqualsUtils.equals(model, ContactUtils.getCurrentUser())) {
					if (!contactCurrentUser.isSelected())
						ContactUtils.setCurrentUser(null);
				} else {
					if (contactCurrentUser.isSelected())
						ContactUtils.setCurrentUser((Contact) model);
				}
			}
			
		});
		
		// Lay out the panel
		rightPanel.add(builder.getPanel(), BorderLayout.CENTER);
		
		this.setDividerLocation(200);
	}
	
}
