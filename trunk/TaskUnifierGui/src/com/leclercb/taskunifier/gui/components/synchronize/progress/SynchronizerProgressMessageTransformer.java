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
package com.leclercb.taskunifier.gui.components.synchronize.progress;

import com.leclercb.commons.api.progress.ProgressMessage;
import com.leclercb.commons.api.progress.ProgressMessageTransformer;
import com.leclercb.commons.api.progress.event.ProgressMessageAddedEvent;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerDefaultProgressMessage;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerMainProgressMessage;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerRetrievedModelsProgressMessage;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerUpdatedModelsProgressMessage;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.translations.TranslationsUtils;

public class SynchronizerProgressMessageTransformer implements ProgressMessageTransformer {
	
	private static SynchronizerProgressMessageTransformer INSTANCE;
	
	public static SynchronizerProgressMessageTransformer getInstance() {
		if (INSTANCE == null)
			INSTANCE = new SynchronizerProgressMessageTransformer();
		
		return INSTANCE;
	}
	
	private SynchronizerProgressMessageTransformer() {
		
	}
	
	@Override
	public boolean acceptsEvent(ProgressMessageAddedEvent event) {
		ProgressMessage message = event.getMessage();
		
		if (message instanceof SynchronizerDefaultProgressMessage) {
			return true;
		} else if (message instanceof SynchronizerRetrievedModelsProgressMessage) {
			SynchronizerRetrievedModelsProgressMessage m = (SynchronizerRetrievedModelsProgressMessage) message;
			
			switch (m.getType()) {
				case PUBLISHER_END:
				case SYNCHRONIZER_END:
					return false;
			}
			
			return true;
		} else if (message instanceof SynchronizerUpdatedModelsProgressMessage) {
			SynchronizerUpdatedModelsProgressMessage m = (SynchronizerUpdatedModelsProgressMessage) message;
			
			switch (m.getType()) {
				case PUBLISHER_END:
				case SYNCHRONIZER_END:
					return false;
			}
			
			return true;
		} else if (message instanceof SynchronizerMainProgressMessage) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public Object getEventValue(ProgressMessageAddedEvent event, String key) {
		ProgressMessage message = event.getMessage();
		
		if (EqualsUtils.equalsStringIgnoreCase(key, "icon")) {
			if (message instanceof SynchronizerDefaultProgressMessage) {
				SynchronizerDefaultProgressMessage m = (SynchronizerDefaultProgressMessage) message;
				
				return m.getIcon();
			}
			
			return null;
		}
		
		if (message instanceof SynchronizerDefaultProgressMessage) {
			SynchronizerDefaultProgressMessage m = (SynchronizerDefaultProgressMessage) message;
			
			return m.getMessage();
		} else if (message instanceof SynchronizerRetrievedModelsProgressMessage) {
			SynchronizerRetrievedModelsProgressMessage m = (SynchronizerRetrievedModelsProgressMessage) message;
			
			switch (m.getType()) {
				case PUBLISHER_END:
				case SYNCHRONIZER_END:
					return null;
			}
			
			String type = TranslationsUtils.translateModelType(
					m.getModelType(),
					true);
			
			return Translations.getString(
					"synchronizer.retrieving_models",
					type);
		} else if (message instanceof SynchronizerUpdatedModelsProgressMessage) {
			SynchronizerUpdatedModelsProgressMessage m = (SynchronizerUpdatedModelsProgressMessage) message;
			
			String property = null;
			
			switch (m.getType()) {
				case PUBLISHER_START:
					property = "synchronizer.publishing";
					break;
				case SYNCHRONIZER_START:
					property = "synchronizer.synchronizing";
					break;
			}
			
			if (property == null)
				return null;
			
			String type = TranslationsUtils.translateModelType(
					m.getModelType(),
					m.getActionCount() > 1);
			
			return Translations.getString(property, m.getActionCount(), type);
		} else if (message instanceof SynchronizerMainProgressMessage) {
			SynchronizerMainProgressMessage m = (SynchronizerMainProgressMessage) message;
			
			String apiName = "?";
			
			if (m.getPlugin() != null)
				apiName = m.getPlugin().getSynchronizerApi().getApiName();
			
			switch (m.getType()) {
				case PUBLISHER_END:
					return Translations.getString(
							"synchronizer.publication_completed",
							apiName);
				case PUBLISHER_START:
					return Translations.getString(
							"synchronizer.start_publication",
							apiName);
				case SYNCHRONIZER_END:
					return Translations.getString(
							"synchronizer.synchronization_completed",
							apiName);
				case SYNCHRONIZER_START:
					return Translations.getString(
							"synchronizer.start_synchronization",
							apiName);
			}
		}
		
		return null;
	}
	
}
