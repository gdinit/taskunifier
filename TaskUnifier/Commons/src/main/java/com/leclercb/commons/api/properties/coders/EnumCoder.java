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
package com.leclercb.commons.api.properties.coders;

import com.leclercb.commons.api.properties.PropertiesCoder;
import com.leclercb.commons.api.utils.CheckUtils;

public class EnumCoder<T extends Enum<?>> extends PropertiesCoder<T> {
	
	private Class<T> cls;
	
	public EnumCoder(Class<T> cls) {
		CheckUtils.isNotNull(cls);
		this.cls = cls;
	}
	
	@Override
	public Class<T> getCoderClass() {
		return this.cls;
	}
	
	@Override
	public T decode(String value) throws Exception {
		if (value == null || value.length() == 0)
			return null;
		
		T[] enumConstants = this.cls.getEnumConstants();
		for (T enumConstant : enumConstants)
			if (enumConstant.name().equals(value))
				return enumConstant;
		
		throw new Exception("This enum constant doesn't exist");
	}
	
	@Override
	public String encode(T value) throws Exception {
		return (value == null ? "" : value.name());
	}
	
}