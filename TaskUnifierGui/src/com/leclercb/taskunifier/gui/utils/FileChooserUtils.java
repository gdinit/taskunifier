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
package com.leclercb.taskunifier.gui.utils;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.SystemUtils;

import com.leclercb.taskunifier.gui.main.frames.FrameUtils;

public final class FileChooserUtils {
	
	private FileChooserUtils() {
		
	}
	
	public static String getFile(
			boolean open,
			String file,
			FileFilter fileFilter,
			int fileSelectionMode,
			String appendFileExtention) {
		String selectedFile = null;
		
		if (SystemUtils.IS_OS_MAC)
			selectedFile = getFileAWT(open, file, fileSelectionMode, fileFilter);
		else
			selectedFile = getFileSwing(
					open,
					file,
					fileSelectionMode,
					fileFilter);
		
		if (selectedFile != null && appendFileExtention != null) {
			if (!selectedFile.endsWith("." + appendFileExtention)) {
				selectedFile += "." + appendFileExtention;
			}
		}
		
		return selectedFile;
	}
	
	private static String getFileSwing(
			final boolean open,
			final String file,
			int fileSelectionMode,
			final FileFilter fileFilter) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(fileSelectionMode);
		fileChooser.setMultiSelectionEnabled(false);
		
		if (file != null)
			fileChooser.setSelectedFile(new File(file));
		
		if (fileFilter != null)
			fileChooser.setFileFilter(fileFilter);
		
		int result = 0;
		
		if (open)
			result = fileChooser.showOpenDialog(FrameUtils.getCurrentFrame());
		else
			result = fileChooser.showSaveDialog(FrameUtils.getCurrentFrame());
		
		if (result != JFileChooser.APPROVE_OPTION)
			return null;
		
		return fileChooser.getSelectedFile().getAbsolutePath();
	}
	
	private static String getFileAWT(
			final boolean open,
			final String file,
			int fileSelectionMode,
			final FileFilter fileFilter) {
		if (fileSelectionMode == JFileChooser.FILES_ONLY)
			System.setProperty("apple.awt.fileDialogForDirectories", "false");
		else
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
		
		FileDialog fileChooser = new FileDialog(FrameUtils.getCurrentFrame());
		
		if (open)
			fileChooser.setMode(FileDialog.LOAD);
		else
			fileChooser.setMode(FileDialog.SAVE);
		
		if (file != null)
			fileChooser.setFile(file);
		
		if (fileFilter != null)
			fileChooser.setFilenameFilter(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return fileFilter.accept(new File(dir
							+ File.separator
							+ name));
				}
				
			});
		
		fileChooser.setVisible(true);
		
		if (fileChooser.getFile() == null)
			return null;
		
		return fileChooser.getDirectory() + fileChooser.getFile();
	}
	
}
