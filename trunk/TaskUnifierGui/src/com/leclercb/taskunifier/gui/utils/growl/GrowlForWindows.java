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
package com.leclercb.taskunifier.gui.utils.growl;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.code.jgntp.Gntp;
import com.google.code.jgntp.GntpApplicationInfo;
import com.google.code.jgntp.GntpClient;
import com.google.code.jgntp.GntpNotificationInfo;

public class GrowlForWindows implements Growl {
	
	private GntpClient client;
	private GntpApplicationInfo applicationInfo;
	
	public GrowlForWindows() {
		this.initialize();
	}
	
	private void initialize() {
		this.applicationInfo = Gntp.appInfo("TaskUnifier").build();
		this.client = Gntp.client(this.applicationInfo).forHost("localhost").build();
		this.client.register();
	}
	
	@Override
	public void registerApplication() throws Exception {
		this.client.register();
	}
	
	@Override
	public void notify(String notificationList, String title) throws Exception {
		this.notify(notificationList, title, null);
	}
	
	@Override
	public void notify(String notificationList, String title, String description)
			throws Exception {
		GntpNotificationInfo notificationInfo = Gntp.notificationInfo(
				this.applicationInfo,
				notificationList).build();
		
		this.client.notify(
				Gntp.notification(notificationInfo, title).text(description).build(),
				2,
				SECONDS);
	}
	
	@Override
	public void close() throws Exception {
		this.client.shutdown(2, SECONDS);
	}
	
}
