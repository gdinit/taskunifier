/*
 * NoteUnifier
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
package com.leclercb.taskunifier.gui.components.notes;

import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValues;

import com.leclercb.taskunifier.api.models.BasicModel;
import com.leclercb.taskunifier.api.models.Folder;
import com.leclercb.taskunifier.api.models.ModelNote;
import com.leclercb.taskunifier.api.models.Note;
import com.leclercb.taskunifier.gui.api.accessor.DefaultPropertyAccessor;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorList;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;
import com.leclercb.taskunifier.gui.commons.editors.TitleEditor;
import com.leclercb.taskunifier.gui.commons.values.IconValueNote;
import com.leclercb.taskunifier.gui.commons.values.StringValueModelId;
import com.leclercb.taskunifier.gui.commons.values.StringValueNoteTitle;
import com.leclercb.taskunifier.gui.translations.Translations;

public class NoteColumnList extends PropertyAccessorList<Note> {
	
	public static final String MODEL = "MODEL";
	public static final String MODEL_CREATION_DATE = "MODEL_CREATION_DATE";
	public static final String MODEL_UPDATE_DATE = "MODEL_UPDATE_DATE";
	public static final String TITLE = "TITLE";
	public static final String FOLDER = "FOLDER";
	public static final String NOTE = "NOTE";
	
	private static NoteColumnList INSTANCE;
	
	public static NoteColumnList getInstance() {
		if (INSTANCE == null)
			INSTANCE = new NoteColumnList();
		
		return INSTANCE;
	}
	
	private NoteColumnList() {
		super(NOTE);
		
		this.initialize();
	}
	
	private void initialize() {
		this.add(new DefaultPropertyAccessor<Note>(
				"MODEL",
				"note.field.model",
				PropertyAccessorType.NOTE,
				BasicModel.PROP_MODEL_ID,
				Translations.getString("general.note.id"),
				false,
				true,
				true) {
			
			private TableCellRenderer renderer;
			
			@Override
			public TableCellRenderer getCellRenderer() {
				if (this.renderer == null) {
					this.renderer = new DefaultTableRenderer(
							StringValueModelId.INSTANCE);
				}
				
				return this.renderer;
			}
			
			@Override
			public Object getProperty(Note model) {
				return model;
			}
			
			@Override
			public void setProperty(Note model, Object value) {
				
			}
			
		});
		
		this.add(new DefaultPropertyAccessor<Note>(
				"MODEL_CREATION_DATE",
				"note.field.model_creation_date",
				PropertyAccessorType.CALENDAR_DATE_TIME,
				BasicModel.PROP_MODEL_CREATION_DATE,
				Translations.getString("general.creation_date"),
				false,
				true,
				false) {
			
			@Override
			public Object getProperty(Note model) {
				return model.getModelCreationDate();
			}
			
			@Override
			public void setProperty(Note model, Object value) {
				
			}
			
		});
		
		this.add(new DefaultPropertyAccessor<Note>(
				"MODEL_UPDATE_DATE",
				"note.field.model_update_date",
				PropertyAccessorType.CALENDAR_DATE_TIME,
				BasicModel.PROP_MODEL_UPDATE_DATE,
				Translations.getString("general.update_date"),
				false,
				true,
				false) {
			
			@Override
			public Object getProperty(Note model) {
				return model.getModelUpdateDate();
			}
			
			@Override
			public void setProperty(Note model, Object value) {
				
			}
			
		});
		
		this.add(new DefaultPropertyAccessor<Note>(
				"TITLE",
				"note.field.title",
				PropertyAccessorType.STRING,
				BasicModel.PROP_TITLE,
				Translations.getString("general.note.title"),
				true,
				true,
				false) {
			
			private TableCellRenderer renderer;
			private TableCellEditor editor;
			
			@Override
			public TableCellRenderer getCellRenderer() {
				if (this.renderer == null) {
					this.renderer = new DefaultTableRenderer(
							StringValueNoteTitle.INSTANCE);
				}
				
				return this.renderer;
			}
			
			@Override
			public TableCellEditor getCellEditor() {
				if (this.editor == null) {
					this.editor = new TitleEditor();
				}
				
				return this.editor;
			}
			
			@Override
			public Object getProperty(Note model) {
				return model.getTitle();
			}
			
			@Override
			public void setProperty(Note model, Object value) {
				model.setTitle((String) value);
			}
			
		});
		
		this.add(new DefaultPropertyAccessor<Note>(
				"FOLDER",
				"note.field.folder",
				PropertyAccessorType.FOLDER,
				Note.PROP_FOLDER,
				Translations.getString("general.note.folder"),
				true,
				true,
				false) {
			
			@Override
			public Object getProperty(Note model) {
				return model.getFolder();
			}
			
			@Override
			public void setProperty(Note model, Object value) {
				model.setFolder((Folder) value);
			}
			
		});
		
		this.add(new DefaultPropertyAccessor<Note>(
				"NOTE",
				"note.field.note",
				PropertyAccessorType.STRING,
				ModelNote.PROP_NOTE,
				Translations.getString("general.note.note"),
				false,
				true,
				false) {
			
			private TableCellRenderer renderer;
			
			@Override
			public TableCellRenderer getCellRenderer() {
				if (this.renderer == null) {
					this.renderer = new DefaultTableRenderer(new MappedValue(
							StringValues.EMPTY,
							IconValueNote.INSTANCE), SwingConstants.CENTER);
				}
				
				return this.renderer;
			}
			
			@Override
			public String getPropertyAsString(Note model) {
				Object value = this.getProperty(model);
				return (value == null ? null : "\n" + value.toString());
			}
			
			@Override
			public Object getProperty(Note model) {
				return model.getNote();
			}
			
			@Override
			public void setProperty(Note model, Object value) {
				model.setNote((String) value);
			}
			
		});
	}
	
}
