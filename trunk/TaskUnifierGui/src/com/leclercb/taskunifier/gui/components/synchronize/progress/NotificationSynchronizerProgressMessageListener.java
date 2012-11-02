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

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.progress.ProgressMessage;
import com.leclercb.commons.api.progress.ProgressMessageTransformer;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerMainProgressMessage;
import com.leclercb.taskunifier.api.synchronizer.progress.messages.SynchronizerUpdatedModelsProgressMessage;
import com.leclercb.taskunifier.gui.utils.notifications.NotificationList;
import com.leclercb.taskunifier.gui.utils.notifications.NotificationUtils;

public class NotificationSynchronizerProgressMessageListener implements ListChangeListener {
	
	private StringBuilder builder;
	
	public NotificationSynchronizerProgressMessageListener() {
		this.builder = new StringBuilder();
	}
	
	@Override
	public void listChange(ListChangeEvent event) {
		ProgressMessageTransformer t = SynchronizerProgressMessageTransformer.getInstance();
		
		if (t.acceptsEvent(event)) {
			if (event.getChangeType() == ListChangeEvent.VALUE_ADDED) {
				ProgressMessage message = (ProgressMessage) event.getValue();
				
				String content = (String) t.getEventValue(event, "message");
				
				if (message instanceof SynchronizerUpdatedModelsProgressMessage) {
					this.builder.append(content + "\n");
				} else if (message.getClass().equals(
						SynchronizerMainProgressMessage.class)) {
					SynchronizerMainProgressMessage m = (SynchronizerMainProgressMessage) message;
					
					switch (m.getType()) {
						case PUBLISHER_START:
						case SYNCHRONIZER_START:
							NotificationUtils.notify(
									NotificationList.SYNCHRONIZATION,
									content);
							
							break;
						case PUBLISHER_END:
						case SYNCHRONIZER_END:
							NotificationUtils.notify(
									NotificationList.SYNCHRONIZATION,
									content,
									this.builder.toString());
							
							this.builder = new StringBuilder();
							
							break;
					}
				}
			}
		}
	}
	
}
