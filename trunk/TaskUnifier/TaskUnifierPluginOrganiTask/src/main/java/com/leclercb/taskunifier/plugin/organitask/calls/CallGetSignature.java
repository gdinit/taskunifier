/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import org.apache.commons.codec.digest.DigestUtils;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;

final class CallGetSignature extends AbstractCall {
	
	public String getSignature(String info) throws SynchronizerException {
		CheckUtils.isNotNull(info);
		
		return DigestUtils.md5Hex(info + com.leclercb.taskunifier.plugin.toodledo.OrganiTaskApi.getInstance().getApiKey());
	}
	
}
