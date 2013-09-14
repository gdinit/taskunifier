/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import org.apache.commons.codec.digest.DigestUtils;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.plugin.toodledo.ToodledoApi;

final class CallGetKey extends AbstractCall {
	
	public String getKey(String password, String token)
			throws SynchronizerException {
		CheckUtils.isNotNull(password);
		CheckUtils.isNotNull(token);
		
		return DigestUtils.md5Hex(DigestUtils.md5Hex(password)
				+ ToodledoApi.getInstance().getApiKey()
				+ token);
	}
	
}
