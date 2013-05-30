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
package com.leclercb.taskunifier.gui.components.taskfiles;

import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.MappedValue;

import com.leclercb.taskunifier.api.models.FileList.FileItem;
import com.leclercb.taskunifier.gui.api.accessor.DefaultPropertyAccessor;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorList;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;
import com.leclercb.taskunifier.gui.commons.editors.TaskFileLinkEditor;
import com.leclercb.taskunifier.gui.commons.values.IconValueOpen;
import com.leclercb.taskunifier.gui.translations.Translations;

public class TaskFilesColumnList extends PropertyAccessorList<FileItem> {
	
	public static final String LINK = "LINK";
	public static final String FILE = "FILE";
	public static final String OPEN = "OPEN";
	
	private static TaskFilesColumnList INSTANCE;
	
	public static TaskFilesColumnList getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskFilesColumnList();
		
		return INSTANCE;
	}
	
	private TaskFilesColumnList() {
		this.initialize();
	}
	
	private void initialize() {
		this.add(new DefaultPropertyAccessor<FileItem>(
				"LINK",
				null,
				PropertyAccessorType.STRING,
				null,
				Translations.getString("general.file.link"),
				true,
				true,
				true) {
			
			private TableCellEditor editor;
			
			@Override
			public TableCellEditor getCellEditor() {
				if (this.editor == null) {
					this.editor = new TaskFileLinkEditor();
				}
				
				return this.editor;
			}
			
			@Override
			public Object getProperty(FileItem item) {
				return item.getLink();
			}
			
			@Override
			public void setProperty(FileItem item, Object value) {
				item.setLink((String) value);
			}
			
		});
		
		this.add(new DefaultPropertyAccessor<FileItem>(
				"FILE",
				null,
				PropertyAccessorType.FILE,
				null,
				Translations.getString("general.file"),
				true,
				true,
				true) {
			
			@Override
			public Object getProperty(FileItem item) {
				return item.getFile();
			}
			
			@Override
			public void setProperty(FileItem item, Object value) {
				item.setFile((String) value);
			}
			
		});
		
		this.add(new DefaultPropertyAccessor<FileItem>(
				"OPEN",
				null,
				PropertyAccessorType.VOID,
				null,
				Translations.getString("general.open"),
				false,
				true,
				true) {
			
			private TableCellRenderer renderer;
			
			@Override
			public TableCellRenderer getCellRenderer() {
				if (this.renderer == null) {
					this.renderer = new DefaultTableRenderer(new MappedValue(
							null,
							IconValueOpen.INSTANCE), SwingConstants.CENTER);
				}
				
				return this.renderer;
			}
			
			@Override
			public Object getProperty(FileItem item) {
				return null;
			}
			
			@Override
			public void setProperty(FileItem item, Object value) {
				
			}
			
		});
	}
	
}
