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
package com.leclercb.commons.api.utils.properties;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.leclercb.commons.api.properties.SortedProperties;

public class ReplaceProperties {
	
	public static void removeProperty(
			File file,
			String keyStartsWith,
			String newKeyStartsWith) throws Exception {
		SortedProperties p = new SortedProperties();
		p.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
		Set<Object> keys = new HashSet<Object>(p.keySet());
		for (Object key : keys) {
			if (!key.toString().startsWith(keyStartsWith))
				continue;
			
			System.out.println("Property \""
					+ key
					+ "\" replaced in: "
					+ file.getName());
			
			String newKey = key.toString().replace(
					keyStartsWith,
					newKeyStartsWith);
			String value = p.getProperty(key.toString());
			
			p.remove(key);
			p.put(newKey, value);
		}
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		p.store(output, null);
		IOUtils.write(output.toString(), new FileOutputStream(file), "UTF-8");
	}
	
	public static void main(String[] args) throws Exception {
		File file = new File(args[0]);
		String keyStartsWith = args[1];
		String newKeyStartsWith = args[2];
		
		if (!file.exists() || !file.isFile())
			throw new IllegalArgumentException();
		
		final String bundle = file.getName().substring(
				0,
				(file.getName().indexOf('_') == -1 ? file.getName().indexOf('.') : file.getName().indexOf(
						'_')));
		
		File[] files = file.getParentFile().listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				return file.getName().startsWith(bundle);
			}
			
		});
		
		for (File f : files) {
			removeProperty(f, keyStartsWith, newKeyStartsWith);
		}
	}
	
}
