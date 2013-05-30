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
package com.leclercb.taskunifier.gui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.leclercb.taskunifier.gui.utils.FileChooserUtils;
import com.leclercb.taskunifier.gui.utils.ImageUtils;

public class TUFileEditor extends JPanel {
	
	private JTextField fileTextField;
	private JButton selectFile;
	
	public TUFileEditor(
			boolean open,
			String file,
			int fileSelectionMode,
			FileFilter fileFilter,
			String appendFileExtention) {
		this.initialize(
				open,
				file,
				fileSelectionMode,
				fileFilter,
				appendFileExtention);
	}
	
	public String getFile() {
		return this.fileTextField.getText();
	}
	
	public void setFile(String file) {
		this.fileTextField.setText(file);
	}
	
	private void initialize(
			final boolean open,
			final String file,
			final int fileSelectionMode,
			final FileFilter fileFilter,
			final String appendFileExtention) {
		this.fileTextField = new JTextField();
		
		if (file != null)
			this.fileTextField.setText(file);
		
		this.selectFile = new JButton(ImageUtils.getResourceImage(
				"folder.png",
				12,
				12));
		this.selectFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedFile = FileChooserUtils.getFile(
						open,
						file,
						fileFilter,
						fileSelectionMode,
						appendFileExtention);
				
				if (selectedFile != null)
					TUFileEditor.this.fileTextField.setText(selectedFile);
			}
			
		});
		
		this.setLayout(new BorderLayout());
		
		this.add(this.fileTextField, BorderLayout.CENTER);
		this.add(this.selectFile, BorderLayout.EAST);
	}
	
}
