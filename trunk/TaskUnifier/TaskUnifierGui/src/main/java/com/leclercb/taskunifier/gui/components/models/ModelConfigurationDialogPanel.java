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
package com.leclercb.taskunifier.gui.components.models;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.Tag;
import com.leclercb.taskunifier.gui.components.models.lists.IModelList;
import com.leclercb.taskunifier.gui.components.models.lists.ITagList;
import com.leclercb.taskunifier.gui.components.models.panels.*;
import com.leclercb.taskunifier.gui.swing.TUDialogPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUOkButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import org.jdesktop.swingx.JXHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ModelConfigurationDialogPanel extends TUDialogPanel {

    private static ModelConfigurationDialogPanel INSTANCE = null;

    protected static ModelConfigurationDialogPanel getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ModelConfigurationDialogPanel();

        return INSTANCE;
    }

    private JTabbedPane tabbedPane;

    private ActionListener okListener;

    private ModelConfigurationDialogPanel() {
        this.initialize();
    }

    public void setSelectedModelConfigurationTab(ModelConfigurationTab tab) {
        CheckUtils.isNotNull(tab);
        this.tabbedPane.setSelectedIndex(tab.ordinal());
    }

    public void addNewModel(ModelType type) {
        int index = modelTypeToTabIndex(type);

        if (index == -1)
            return;

        this.tabbedPane.setSelectedIndex(index);

        IModelList list = (IModelList) this.tabbedPane.getSelectedComponent();
        list.addNewModel();
    }

    public void setSelectedModel(ModelType type, Model model) {
        int index = modelTypeToTabIndex(type);

        if (index == -1)
            return;

        this.tabbedPane.setSelectedIndex(index);

        if (model != null) {
            IModelList list = (IModelList) this.tabbedPane.getSelectedComponent();
            list.setSelectedModel(model);
        }
    }

    public void setSelectedTag(Tag tag) {
        this.tabbedPane.setSelectedIndex(5);

        ITagList list = (ITagList) this.tabbedPane.getSelectedComponent();
        list.setSelectedTag(tag);
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        JXHeader header = new JXHeader();
        header.setTitle(Translations.getString("header.title.manage_models"));
        header.setDescription(Translations.getString("header.description.manage_models"));
        header.setIcon(ImageUtils.getResourceImage("folder.png", 32, 32));

        JPanel tabbedPanel = new JPanel(new BorderLayout());
        tabbedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.tabbedPane = new JTabbedPane();

        this.tabbedPane.addTab(
                Translations.getString("general.contacts"),
                new ContactConfigurationPanel());

        this.tabbedPane.addTab(
                Translations.getString("general.contexts"),
                new ContextConfigurationPanel());

        this.tabbedPane.addTab(
                Translations.getString("general.folders"),
                new FolderConfigurationPanel());

        this.tabbedPane.addTab(
                Translations.getString("general.goals"),
                new GoalConfigurationPanel());

        this.tabbedPane.addTab(
                Translations.getString("general.locations"),
                new LocationConfigurationPanel());

        this.tabbedPane.addTab(
                Translations.getString("general.task_statuses"),
                new TaskStatusConfigurationPanel());

        this.tabbedPane.addTab(
                Translations.getString("general.task.tags"),
                new TagConfigurationPanel());

        tabbedPanel.add(this.tabbedPane);

        this.add(header, BorderLayout.NORTH);
        this.add(tabbedPanel, BorderLayout.CENTER);

        this.initializeButtonsPanel();
    }

    private void initializeButtonsPanel() {
        this.okListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                ModelConfigurationDialogPanel.this.getDialog().setVisible(false);
            }

        };

        JButton okButton = new TUOkButton(this.okListener);

        this.setButtons(okButton, okButton);
    }

    private static int modelTypeToTabIndex(ModelType type) {
        if (type == null)
            return -1;

        switch (type) {
            case CONTACT:
                return 0;
            case CONTEXT:
                return 1;
            case FOLDER:
                return 2;
            case GOAL:
                return 3;
            case LOCATION:
                return 4;
            case TASK_STATUS:
                return 5;
            case NOTE:
            case TASK:
            default:
                return -1;
        }
    }

    @Override
    protected void dialogLoaded() {
        this.getDialog().getRootPane().registerKeyboardAction(
                this.okListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

}
