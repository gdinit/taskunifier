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
package com.leclercb.taskunifier.gui.components.modelselectiontable;

import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.gui.api.accessor.DefaultPropertyAccessor;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorList;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessorType;
import com.leclercb.taskunifier.gui.translations.Translations;

public class ModelSelectionColumnList extends PropertyAccessorList<Model> {
	
	public static final String SELECT = "SELECT";
	public static final String MODEL = "MODEL";
	
	private static ModelSelectionColumnList INSTANCE;
	
	public static ModelSelectionColumnList getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ModelSelectionColumnList();
		
		return INSTANCE;
	}
	
	private ModelSelectionColumnList() {
		this.initialize();
	}
	
	private void initialize() {
		this.add(new DefaultPropertyAccessor<Model>(
				"SELECT",
				null,
				PropertyAccessorType.BOOLEAN,
				null,
				Translations.getString("general.select"),
				true,
				true,
				false) {
			
			@Override
			public Object getProperty(Model model) {
				return null;
			}
			
			@Override
			public void setProperty(Model model, Object value) {
				
			}
			
		});
		
		this.add(new DefaultPropertyAccessor<Model>(
				"MODEL",
				null,
				PropertyAccessorType.MODEL,
				null,
				Translations.getString("general.title"),
				false,
				true,
				true) {
			
			@Override
			public Object getProperty(Model model) {
				return model;
			}
			
			@Override
			public void setProperty(Model model, Object value) {
				
			}
			
		});
	}
	
}
