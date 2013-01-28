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
package com.leclercb.taskunifier.gui.api.accessor;

import java.util.logging.Level;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.gui.logger.GuiLogger;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.Task;

public class TaskPropertyAccessor extends DefaultPropertyAccessor<Task> {
	
	public TaskPropertyAccessor(
			String name,
			String fieldSettingsPropertyName,
			PropertyAccessorType type,
			String propertyName,
			String label,
			boolean editable,
			boolean usable,
			boolean sortable) {
		super(
				name,
				fieldSettingsPropertyName,
				type,
				propertyName,
				label,
				editable,
				usable,
				sortable);
	}
	
	@Override
	public Object getProperty(Task task) {
		CheckUtils.isNotNull(task);
		
		try {
			Object property = task.getProperties().getObjectProperty(
					this.getPropertyName(),
					this.getType().getType(),
					null);
			
			if (property instanceof Model)
				if (!((Model) property).getModelStatus().isEndUserStatus())
					return null;
			
			return property;
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.SEVERE, "Cannot get property", e);
			
			return null;
		}
	}
	
	@Override
	public void setProperty(Task task, Object value) {
		CheckUtils.isNotNull(task);
		
		try {
			task.getProperties().setRawObjectProperty(
					this.getPropertyName(),
					this.getType().getType(),
					value);
		} catch (Exception e) {
			GuiLogger.getLogger().log(Level.SEVERE, "Cannot set property", e);
		}
	}
	
}
