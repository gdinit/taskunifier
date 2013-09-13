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
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXColorSelectionButton;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.ComboBoxAdapter;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.value.ValueModel;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.Goal;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelArchive;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.enums.GoalLevel;
import com.leclercb.taskunifier.gui.api.models.GuiGoal;
import com.leclercb.taskunifier.gui.api.models.GuiModel;
import com.leclercb.taskunifier.gui.commons.converters.ColorConverter;
import com.leclercb.taskunifier.gui.commons.models.GoalContributeModel;
import com.leclercb.taskunifier.gui.commons.models.GoalModel;
import com.leclercb.taskunifier.gui.commons.values.IconValueModel;
import com.leclercb.taskunifier.gui.commons.values.StringValueGoalLevel;
import com.leclercb.taskunifier.gui.commons.values.StringValueModel;
import com.leclercb.taskunifier.gui.components.models.lists.IModelList;
import com.leclercb.taskunifier.gui.components.models.lists.ModelList;
import com.leclercb.taskunifier.gui.components.models.lists.draganddrop.ModelTransferHandler;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.FormBuilder;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class GoalConfigurationPanel extends JSplitPane implements IModelList {
	
	private ModelList modelList;
	
	public GoalConfigurationPanel() {
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
		final JTextField goalTitle = new JTextField();
		final JComboBox goalLevel = new JComboBox();
		final JComboBox goalContributes = ComponentFactory.createModelComboBox(
				null,
				true);
		final JCheckBox goalArchived = new JCheckBox();
		final JXColorSelectionButton goalColor = new JXColorSelectionButton();
		final JButton removeColor = new JButton();
		
		// Overwrite renderer
		goalContributes.setRenderer(new DefaultListRenderer(
				StringValueModel.INSTANCE,
				IconValueModel.INSTANCE));
		
		// Set Disabled
		goalTitle.setEnabled(false);
		goalLevel.setEnabled(false);
		goalContributes.setEnabled(false);
		goalArchived.setEnabled(false);
		goalColor.setEnabled(false);
		removeColor.setEnabled(false);
		
		// Initialize Model List
		this.modelList = new ModelList(new GoalModel(false, true) {
			
			@Override
			protected void fireContentsChanged(
					Object source,
					int index0,
					int index1) {
				this.superFireContentsChanged(source, index0, index1);
			}
			
		}, goalTitle) {
			
			private BeanAdapter<Goal> adapter;
			
			{
				this.adapter = new BeanAdapter<Goal>((Goal) null, true);
				
				ValueModel titleModel = this.adapter.getValueModel(BasicModel.PROP_TITLE);
				Bindings.bind(goalTitle, titleModel);
				
				ValueModel levelModel = this.adapter.getValueModel(Goal.PROP_LEVEL);
				goalLevel.setModel(new ComboBoxAdapter<GoalLevel>(
						GoalLevel.values(),
						levelModel));
				
				ValueModel contributesModel = this.adapter.getValueModel(Goal.PROP_CONTRIBUTES);
				goalContributes.setModel(new ComboBoxAdapter<Goal>(
						new GoalContributeModel(true, true),
						contributesModel));
				
				ValueModel archivedModel = this.adapter.getValueModel(ModelArchive.PROP_ARCHIVED);
				Bindings.bind(goalArchived, archivedModel);
				
				ValueModel colorModel = this.adapter.getValueModel(GuiModel.PROP_COLOR);
				Bindings.bind(goalColor, "background", new ColorConverter(
						colorModel));
			}
			
			@Override
			public Model addModel() {
				return GoalFactory.getInstance().create(
						Translations.getString("goal.default.title"));
			}
			
			@Override
			public void removeModel(Model model) {
				GoalFactory.getInstance().markToDelete((Goal) model);
			}
			
			@Override
			public void modelsSelected(Model[] models) {
				Model model = null;
				
				if (models != null && models.length == 1)
					model = models[0];
				
				this.adapter.setBean(model != null ? (Goal) model : null);
				
				goalTitle.setEnabled(model != null);
				goalLevel.setEnabled(model != null);
				goalContributes.setEnabled(model != null);
				goalArchived.setEnabled(model != null);
				goalColor.setEnabled(model != null);
				removeColor.setEnabled(model != null);
				
				if (model != null)
					goalContributes.setEnabled(!((Goal) model).getLevel().equals(
							GoalLevel.LIFE_TIME));
			}
			
		};
		
		this.modelList.getModelList().setDragEnabled(true);
		this.modelList.getModelList().setTransferHandler(
				new ModelTransferHandler<Goal>(ModelType.GOAL));
		this.modelList.getModelList().setDropMode(DropMode.ON_OR_INSERT);
		
		this.setLeftComponent(this.modelList);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		rightPanel.setLayout(new BorderLayout());
		this.setRightComponent(ComponentFactory.createJScrollPane(
				rightPanel,
				false));
		
		FormBuilder builder = new FormBuilder(
				"right:pref, 4dlu, fill:default:grow");
		
		// Goal Title
		builder.appendI15d("general.goal.title", true, goalTitle);
		
		// Goal Level
		builder.appendI15d("general.goal.level", true, goalLevel);
		
		goalLevel.setRenderer(new DefaultListRenderer(
				StringValueGoalLevel.INSTANCE));
		goalLevel.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				Goal goal = null;
				Model[] models = GoalConfigurationPanel.this.modelList.getSelectedModels();
				
				if (models != null && models.length == 1)
					goal = (Goal) models[0];
				
				if (goal == null) {
					goalContributes.setEnabled(false);
					return;
				}
				
				goalContributes.setEnabled(!goal.getLevel().equals(
						GoalLevel.LIFE_TIME));
			}
			
		});
		
		// Goal Contributes
		builder.appendI15d("general.goal.contributes", true, goalContributes);
		
		// Folder Archived
		builder.appendI15d("general.goal.archived", true, goalArchived);
		
		// Goal Color
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		builder.appendI15d("general.color", true, p);
		
		goalColor.setPreferredSize(new Dimension(24, 24));
		goalColor.setBorder(BorderFactory.createEmptyBorder());
		
		removeColor.setIcon(ImageUtils.getResourceImage("remove.png", 16, 16));
		removeColor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				((GuiGoal) GoalConfigurationPanel.this.modelList.getSelectedModels()[0]).setColor(null);
			}
			
		});
		
		p.add(goalColor);
		p.add(removeColor);
		
		// Lay out the panel
		rightPanel.add(builder.getPanel(), BorderLayout.CENTER);
		
		this.setDividerLocation(200);
	}
	
}
