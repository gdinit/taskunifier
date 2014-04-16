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

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.value.ConverterValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.leclercb.commons.api.event.propertychange.WeakPropertyChangeListener;
import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.TaskStatus;
import com.leclercb.taskunifier.api.models.TaskStatusFactory;
import com.leclercb.taskunifier.gui.api.models.GuiModel;
import com.leclercb.taskunifier.gui.api.models.GuiTaskStatus;
import com.leclercb.taskunifier.gui.commons.converters.ColorConverter;
import com.leclercb.taskunifier.gui.commons.models.TaskStatusModel;
import com.leclercb.taskunifier.gui.components.models.lists.IModelList;
import com.leclercb.taskunifier.gui.components.models.lists.ModelList;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.FormBuilder;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;
import org.jdesktop.swingx.JXColorSelectionButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TaskStatusConfigurationPanel extends JSplitPane implements IModelList, PropertyChangeListener {

    private ModelList modelList;
    private boolean enabled;

    private JLabel taskStatusLabel;
    private JTextField taskStatusTitle;
    private JXColorSelectionButton taskStatusColor;
    private JButton removeColor;

    public TaskStatusConfigurationPanel() {
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
        this.taskStatusLabel = new JLabel(Translations.getString(
                "configuration.list.task_statuses.cannot_modify",
                SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName()));
        this.taskStatusLabel.setForeground(Color.RED);

        this.taskStatusTitle = new JTextField();
        this.taskStatusColor = new JXColorSelectionButton();
        this.removeColor = new JButton();

        // Set Disabled

        this.taskStatusLabel.setVisible(false);
        this.taskStatusTitle.setEnabled(false);
        this.taskStatusColor.setEnabled(false);
        this.removeColor.setEnabled(false);

        // Initialize Model List
        this.modelList = new ModelList(new TaskStatusModel(false) {

            @Override
            protected void fireContentsChanged(
                    Object source,
                    int index0,
                    int index1) {
                this.superFireContentsChanged(source, index0, index1);
            }

        }, taskStatusTitle) {

            private BeanAdapter<TaskStatus> adapter;

            {
                this.adapter = new BeanAdapter<TaskStatus>((TaskStatus) null, true);

                ValueModel titleModel = this.adapter.getValueModel(BasicModel.PROP_TITLE);
                Bindings.bind(taskStatusTitle, titleModel);

                ValueModel colorModel = this.adapter.getValueModel(GuiModel.PROP_COLOR);
                Bindings.bind(
                        taskStatusColor,
                        "background",
                        new ConverterValueModel(colorModel, new ColorConverter()));
            }

            @Override
            public Model addModel() {
                return TaskStatusFactory.getInstance().create(
                        Translations.getString("task_status.default.title"));
            }

            @Override
            public void removeModel(Model model) {
                TaskStatusFactory.getInstance().markToDelete((TaskStatus) model);
            }

            @Override
            public void modelsSelected(Model[] models) {
                Model model = null;

                if (models != null && models.length == 1)
                    model = models[0];

                this.adapter.setBean(model != null ? (TaskStatus) model : null);

                taskStatusTitle.setEnabled(model != null && TaskStatusConfigurationPanel.this.enabled);
                taskStatusColor.setEnabled(model != null && TaskStatusConfigurationPanel.this.enabled);
                removeColor.setEnabled(model != null && TaskStatusConfigurationPanel.this.enabled);
            }

        };

        this.setLeftComponent(this.modelList);

        JPanel rightPanel = new JPanel();
        rightPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        rightPanel.setLayout(new BorderLayout());
        this.setRightComponent(ComponentFactory.createJScrollPane(
                rightPanel,
                false));

        FormBuilder builder = new FormBuilder(
                "right:pref, 4dlu, fill:default:grow");

        // TaskStatus Label
        builder.appendI15d(null, false, this.taskStatusLabel);

        // TaskStatus Title
        builder.appendI15d("general.task_status.title", true, taskStatusTitle);

        // TaskStatus Color
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        builder.appendI15d("general.color", true, p);

        taskStatusColor.setPreferredSize(new Dimension(24, 24));
        taskStatusColor.setBorder(BorderFactory.createEmptyBorder());

        removeColor.setIcon(ImageUtils.getResourceImage("remove.png", 16, 16));
        removeColor.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ((GuiTaskStatus) TaskStatusConfigurationPanel.this.modelList.getSelectedModels()[0]).setColor(null);
            }

        });

        p.add(taskStatusColor);
        p.add(removeColor);

        // Lay out the panel
        rightPanel.add(builder.getPanel(), BorderLayout.CENTER);

        this.setDividerLocation(200);

        Main.getUserSettings().addPropertyChangeListener(
                "plugin.synchronizer.id",
                new WeakPropertyChangeListener(Main.getUserSettings(), this));

        this.propertyChange(null);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (!SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().allowCustomTaskStatuses()) {
            this.enabled = false;

            this.taskStatusLabel.setVisible(true);
            this.taskStatusTitle.setEnabled(false);
            this.taskStatusColor.setEnabled(false);
            this.removeColor.setEnabled(false);

            this.modelList.getAddButton().setVisible(false);
            this.modelList.getRemoveButton().setVisible(false);
        } else {
            this.enabled = true;

            this.taskStatusLabel.setVisible(false);

            this.modelList.getAddButton().setVisible(true);
            this.modelList.getRemoveButton().setVisible(true);
        }
    }

}
