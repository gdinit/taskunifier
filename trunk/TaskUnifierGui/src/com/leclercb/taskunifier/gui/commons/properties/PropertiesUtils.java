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
package com.leclercb.taskunifier.gui.commons.properties;

import com.leclercb.commons.api.properties.PropertyMap;
import com.leclercb.commons.api.properties.coders.EnumCoder;
import com.leclercb.taskunifier.api.models.ModelType;
import com.leclercb.taskunifier.api.models.enums.TaskPriority;
import com.leclercb.taskunifier.api.models.enums.TaskRepeatFrom;
import com.leclercb.taskunifier.api.properties.ModelIdCoder;

public final class PropertiesUtils {
	
	private PropertiesUtils() {
		
	}
	
	public static void initializePropertiesCoders() {
		PropertyMap.addDefaultCoder(new ModelIdCoder());
		PropertyMap.addDefaultCoder(new ShortcutKeyCoder());
		
		PropertyMap.addDefaultCoder(new ModelCoder(ModelType.CONTACT));
		PropertyMap.addDefaultCoder(new ModelCoder(ModelType.CONTEXT));
		PropertyMap.addDefaultCoder(new ModelCoder(ModelType.FOLDER));
		PropertyMap.addDefaultCoder(new ModelCoder(ModelType.GOAL));
		PropertyMap.addDefaultCoder(new ModelCoder(ModelType.LOCATION));
		PropertyMap.addDefaultCoder(new ModelCoder(ModelType.NOTE));
		PropertyMap.addDefaultCoder(new ModelCoder(ModelType.TASK));
		
		PropertyMap.addDefaultCoder(new EnumCoder<TaskPriority>(
				TaskPriority.class));
		PropertyMap.addDefaultCoder(new EnumCoder<TaskRepeatFrom>(
				TaskRepeatFrom.class));
		
		PropertyMap.addDefaultCoder(new TimerCoder());
	}
	
}
