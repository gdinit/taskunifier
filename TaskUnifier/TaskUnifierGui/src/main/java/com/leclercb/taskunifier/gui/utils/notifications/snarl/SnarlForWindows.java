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
package com.leclercb.taskunifier.gui.utils.notifications.snarl;

import net.snarl.SnarlNetworkBridge;

import org.apache.commons.lang3.SystemUtils;

import com.leclercb.taskunifier.gui.constants.Constants;
import com.leclercb.taskunifier.gui.utils.notifications.NotificationList;
import com.leclercb.taskunifier.gui.utils.notifications.Notifier;
import com.leclercb.taskunifier.gui.utils.notifications.exceptions.NotifierException;
import com.leclercb.taskunifier.gui.utils.notifications.exceptions.NotifierOSException;

public class SnarlForWindows implements Notifier {
	
	public SnarlForWindows() {
		
	}
	
	@Override
	public String getName() {
		return "Snarl for Windows";
	}
	
	@Override
	public void open() throws NotifierException {
		if (!SystemUtils.IS_OS_WINDOWS)
			throw new NotifierOSException();
		
		SnarlNetworkBridge.snRegisterConfig(Constants.TITLE, "localhost");
	}
	
	@Override
	public void notify(NotificationList list, String title)
			throws NotifierException {
		this.notify(list, title, null);
	}
	
	@Override
	public void notify(NotificationList list, String title, String description)
			throws NotifierException {
		SnarlNetworkBridge.snRegisterAlert(list.getName());
		SnarlNetworkBridge.snShowMessage(list.getName(), title, description);
	}
	
	@Override
	public void close() throws NotifierException {
		
	}
	
}
