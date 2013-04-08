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
package com.leclercb.taskunifier.gui.components.notesearcheredit.searcher;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.leclercb.commons.api.utils.FileUtils;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcher;
import com.leclercb.taskunifier.gui.api.searchers.NoteSearcherType;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.FileChooserUtils;
import com.leclercb.taskunifier.gui.utils.FormBuilder;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class NoteSearcherPanel extends JPanel {
	
	private NoteSearcher searcher;
	
	private JComboBox searcherType;
	private String searcherIconFile;
	private JButton searcherIcon;
	private JTextField searcherTitle;
	private JTextField searcherFolder;
	
	public NoteSearcherPanel(NoteSearcher searcher) {
		this.searcher = searcher;
		
		this.initialize();
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		FormBuilder builder = new FormBuilder(
				"right:pref, 4dlu, fill:default:grow");
		
		// Type
		DefaultComboBoxModel searcherTypeModel = new DefaultComboBoxModel();
		searcherTypeModel.addElement(NoteSearcherType.PERSONAL);
		
		this.searcherType = new JComboBox(searcherTypeModel);
		this.searcherType.setSelectedItem(this.searcher.getType());
		
		builder.appendI15d(
				"searcheredit.searcher.type",
				true,
				this.searcherType);
		
		// Icon
		this.searcherIconFile = this.searcher.getIcon();
		
		JPanel iconPanel = new JPanel(new BorderLayout());
		
		this.searcherIcon = new JButton();
		iconPanel.add(this.searcherIcon, BorderLayout.CENTER);
		this.searcherIcon.setIcon(this.searcherIconFile == null ? ImageUtils.getResourceImage(
				"remove.png",
				24,
				24) : ImageUtils.getImage(this.searcherIconFile, 24, 24));
		this.searcherIcon.setText(this.searcherIconFile == null ? Translations.getString("searcheredit.searcher.no_icon") : this.searcherIconFile);
		this.searcherIcon.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FileFilter fileFilter = new FileFilter() {
					
					@Override
					public String getDescription() {
						return Translations.getString("general.images");
					}
					
					@Override
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						
						String extention = FileUtils.getExtention(f.getName());
						
						String[] imageExtentions = new String[] {
								"jpeg",
								"jpg",
								"gif",
								"tiff",
								"tif",
								"png" };
						
						for (int i = 0; i < imageExtentions.length; i++)
							if (imageExtentions[i].equals(extention))
								return true;
						
						return false;
					}
					
				};
				
				String file = FileChooserUtils.getFile(
						true,
						NoteSearcherPanel.this.searcherIconFile,
						fileFilter,
						JFileChooser.FILES_ONLY,
						null);
				
				if (file != null) {
					NoteSearcherPanel.this.searcherIconFile = file;
					NoteSearcherPanel.this.searcherIcon.setText(file);
				}
			}
			
		});
		
		final JButton searcherRemoveIcon = new JButton(
				ImageUtils.getResourceImage("remove.png", 16, 16));
		iconPanel.add(searcherRemoveIcon, BorderLayout.EAST);
		searcherRemoveIcon.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				NoteSearcherPanel.this.searcherIconFile = null;
				NoteSearcherPanel.this.searcherIcon.setText(Translations.getString("searcheredit.searcher.no_icon"));
			}
			
		});
		
		builder.appendI15d("searcheredit.searcher.icon", true, iconPanel);
		
		// Title
		this.searcherTitle = new JTextField(this.searcher.getTitle());
		
		builder.appendI15d(
				"searcheredit.searcher.title",
				true,
				this.searcherTitle);
		
		// Folder
		this.searcherFolder = new JTextField(this.searcher.getFolder());
		
		builder.appendI15d(
				"searcheredit.searcher.folder",
				true,
				this.searcherFolder);
		
		// Lay out the panel
		panel.add(builder.getPanel(), BorderLayout.CENTER);
		
		this.add(panel, BorderLayout.NORTH);
	}
	
	public void saveChanges() {
		NoteSearcherType type = (NoteSearcherType) NoteSearcherPanel.this.searcherType.getSelectedItem();
		
		if (type != null)
			NoteSearcherPanel.this.searcher.setType(type);
		
		this.searcher.setIcon(this.searcherIconFile);
		this.searcher.setTitle(this.searcherTitle.getText());
		this.searcher.setFolder(this.searcherFolder.getText());
	}
	
}
