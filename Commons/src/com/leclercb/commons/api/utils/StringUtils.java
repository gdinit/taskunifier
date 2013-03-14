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
package com.leclercb.commons.api.utils;

import java.text.Normalizer;

public class StringUtils {
	
	private StringUtils() {
		
	}
	
	public static boolean contains(String s1, String s2) {
		if (s1 == null || s2 == null)
			return false;
		
		return s1.contains(s2);
	}
	
	public static boolean containsIgnoreCase(String s1, String s2) {
		if (s1 == null || s2 == null)
			return false;
		
		return s1.toLowerCase().contains(s2.toLowerCase());
	}
	
	public static boolean containsLocalized(String s1, String s2) {
		if (s1 == null || s2 == null)
			return false;
		
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		
		s1 = Normalizer.normalize(s1, Normalizer.Form.NFD);
		s1 = s1.replaceAll("[^\\p{ASCII}]", "");
		
		s2 = Normalizer.normalize(s2, Normalizer.Form.NFD);
		s2 = s2.replaceAll("[^\\p{ASCII}]", "");
		
		return s1.contains(s2);
	}
	
}
