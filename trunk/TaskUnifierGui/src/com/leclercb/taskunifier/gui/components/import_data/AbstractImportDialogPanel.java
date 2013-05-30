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
package com.leclercb.taskunifier.gui.components.import_data;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.api.utils.FileUtils;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.frames.FrameUtils;
import com.leclercb.taskunifier.gui.swing.TUDialogPanel;
import com.leclercb.taskunifier.gui.swing.TUFileField;
import com.leclercb.taskunifier.gui.swing.buttons.TUCancelButton;
import com.leclercb.taskunifier.gui.translations.Translations;

abstract class AbstractImportDialogPanel extends TUDialogPanel {
	
	private TUFileField fileField;
	private JCheckBox deleteExistingValues;
	
	private String title;
	private String fileExtention;
	private String fileExtentionDescription;
	private String fileProperty;
	
	public AbstractImportDialogPanel(
			String title,
			boolean showDeleteExistingValues,
			String fileExtention,
			String fileExtentionDescription,
			String fileProperty) {
		CheckUtils.isNotNull(fileExtention);
		CheckUtils.isNotNull(fileExtentionDescription);
		
		this.title = title;
		this.fileExtention = fileExtention;
		this.fileExtentionDescription = fileExtentionDescription;
		this.fileProperty = fileProperty;
		
		this.initialize(showDeleteExistingValues);
	}
	
	public String getTitle() {
		return this.title;
	}
	
	private void initialize(boolean showDeleteExistingValues) {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// Import file
		FileFilter fileFilter = new FileFilter() {
			
			@Override
			public String getDescription() {
				return AbstractImportDialogPanel.this.fileExtentionDescription;
			}
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				
				String extention = FileUtils.getExtention(f.getName());
				
				return AbstractImportDialogPanel.this.fileExtention.equals(extention);
			}
			
		};
		
		String defaultFile = null;
		
		if (this.fileProperty != null)
			defaultFile = Main.getSettings().getStringProperty(
					this.fileProperty);
		
		this.fileField = new TUFileField(
				Translations.getString("import.file_to_import"),
				true,
				defaultFile,
				JFileChooser.FILES_ONLY,
				fileFilter,
				null);
		
		this.add(this.fileField, BorderLayout.SOUTH);
		
		// Delete existing values
		this.deleteExistingValues = new JCheckBox(
				Translations.getString("import.delete_existing_values"));
		
		if (showDeleteExistingValues) {
			this.add(this.deleteExistingValues, BorderLayout.NORTH);
		}
		
		this.initializeButtonsPanel();
	}
	
	private void initializeButtonsPanel() {
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("IMPORT")) {
					try {
						if (AbstractImportDialogPanel.this.fileProperty != null)
							Main.getSettings().setStringProperty(
									AbstractImportDialogPanel.this.fileProperty,
									AbstractImportDialogPanel.this.fileField.getFile());
						
						if (AbstractImportDialogPanel.this.deleteExistingValues.isSelected())
							AbstractImportDialogPanel.this.deleteExistingValue();
						
						AbstractImportDialogPanel.this.importFromFile(AbstractImportDialogPanel.this.fileField.getFile());
						
						AbstractImportDialogPanel.this.deleteExistingValues.setSelected(false);
						AbstractImportDialogPanel.this.getDialog().setVisible(
								false);
					} catch (Exception e) {
						ErrorInfo info = new ErrorInfo(
								Translations.getString("general.error"),
								e.getMessage(),
								null,
								"GUI",
								e,
								Level.WARNING,
								null);
						
						JXErrorPane.showDialog(
								FrameUtils.getCurrentWindow(),
								info);
					}
				}
				
				if (event.getActionCommand().equals("CANCEL")) {
					AbstractImportDialogPanel.this.deleteExistingValues.setSelected(false);
					AbstractImportDialogPanel.this.getDialog().setVisible(false);
				}
			}
			
		};
		
		JButton importButton = new JButton(
				Translations.getString("general.import"));
		importButton.setActionCommand("IMPORT");
		importButton.addActionListener(listener);
		
		JButton cancelButton = new TUCancelButton(listener);
		
		this.setButtons(importButton, importButton, cancelButton);
	}
	
	protected abstract void deleteExistingValue();
	
	protected abstract void importFromFile(String file) throws Exception;
	
	@Override
	protected void dialogLoaded() {
		this.getDialog().addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				AbstractImportDialogPanel.this.fileField.setFile(null);
				AbstractImportDialogPanel.this.deleteExistingValues.setSelected(false);
				AbstractImportDialogPanel.this.getDialog().setVisible(false);
			}
			
		});
	}
	
}
