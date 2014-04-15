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
package com.leclercb.taskunifier.gui.components.plugins;

import com.leclercb.taskunifier.gui.components.help.Help;
import com.leclercb.taskunifier.gui.plugins.PluginApi;
import com.leclercb.taskunifier.gui.swing.TUDialogPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUCancelButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUOkButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;
import org.jdesktop.swingx.JXHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SynchronizerPluginsDialogPanel extends TUDialogPanel {

    private static SynchronizerPluginsDialogPanel INSTANCE;

    protected static SynchronizerPluginsDialogPanel getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SynchronizerPluginsDialogPanel();

        return INSTANCE;
    }

    private PluginsPanel pluginsPanel;
    private boolean welcome;

    private SynchronizerPluginsDialogPanel() {
        this.initialize();
    }

    public boolean isWelcome() {
        return welcome;
    }

    public void setWelcome(boolean welcome) {
        this.welcome = welcome;
    }

    private void initialize() {
        this.setLayout(new BorderLayout());

        JXHeader header = new JXHeader();
        header.setTitle(Translations.getString("header.title.manage_synchronizer_plugins"));
        header.setDescription(Translations.getString("header.description.manage_synchronizer_plugins"));
        header.setIcon(ImageUtils.getResourceImage("settings.png", 32, 32));
        this.add(header, BorderLayout.NORTH);

        this.pluginsPanel = new PluginsPanel(false, true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        mainPanel.add(this.pluginsPanel, BorderLayout.CENTER);

        this.add(mainPanel, BorderLayout.CENTER);

        this.initializeButtonsPanel();
    }

    private void initializeButtonsPanel() {
        ActionListener listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                if (event.getActionCommand().equals("OK")) {
                    if (!isWelcome()) {
                        String[] options = new String[]{
                                Translations.getString("general.ok"),
                                Translations.getString("general.cancel")};

                        int result = JOptionPane.showOptionDialog(
                                PluginApi.getCurrentWindow(),
                                Translations.getString(
                                        "plugin.synchronizer.remove_all_data",
                                        SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName(),
                                        SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName(),
                                        SynchronizerUtils.getSynchronizerPlugin().getSynchronizerApi().getApiName()),
                                Translations.getString("general.question"),
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                options,
                                options[0]);

                        if (result == 0) {
                            SynchronizerUtils.resetAllConnections();
                            SynchronizerUtils.resetAllSynchronizersAndDeleteModels();
                        }
                    }

                    SynchronizerPluginsDialogPanel.this.pluginsPanel.installSelectedPlugin();
                }

                SynchronizerPluginsDialogPanel.this.getDialog().setVisible(
                        false);
            }

        };

        JButton helpButton = Help.getInstance().getHelpButton("synchronization");
        JButton okButton = new TUOkButton(listener);
        JButton cancelButton = new TUCancelButton(listener);

        this.pluginsPanel.getButtonsPanel().addButton(helpButton);
        this.pluginsPanel.getButtonsPanel().addButton(okButton);
        this.pluginsPanel.getButtonsPanel().addButton(cancelButton);
    }

    @Override
    protected void dialogLoaded() {
        this.getDialog().addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                if (!SynchronizerPluginsDialogPanel.this.pluginsPanel.isPluginListLoaded())
                    SynchronizerPluginsDialogPanel.this.pluginsPanel.reloadPlugins();
            }

        });
    }

}
