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
package com.leclercb.taskunifier.api.xstream;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class TUXStream extends XStream {
	
	private List<String> implicitFields = new ArrayList<String>();
	
	public TUXStream() {
		super();
	}
	
	public TUXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
		super(hierarchicalStreamDriver);
	}
	
	public TUXStream(
			ReflectionProvider reflectionProvider,
			HierarchicalStreamDriver driver,
			ClassLoader classLoader,
			Mapper mapper,
			ConverterLookup converterLookup,
			ConverterRegistry converterRegistry) {
		super(
				reflectionProvider,
				driver,
				classLoader,
				mapper,
				converterLookup,
				converterRegistry);
	}
	
	public TUXStream(
			ReflectionProvider reflectionProvider,
			HierarchicalStreamDriver driver,
			ClassLoader classLoader,
			Mapper mapper) {
		super(reflectionProvider, driver, classLoader, mapper);
	}
	
	public TUXStream(
			ReflectionProvider reflectionProvider,
			HierarchicalStreamDriver driver,
			ClassLoader classLoader) {
		super(reflectionProvider, driver, classLoader);
	}
	
	public TUXStream(
			ReflectionProvider reflectionProvider,
			HierarchicalStreamDriver hierarchicalStreamDriver) {
		super(reflectionProvider, hierarchicalStreamDriver);
	}
	
	public TUXStream(ReflectionProvider reflectionProvider) {
		super(reflectionProvider);
	}
	
	@Override
	public void addImplicitCollection(
			Class ownerType,
			String fieldName,
			String itemFieldName,
			Class itemType) {
		this.implicitFields.add(fieldName);
		
		super.addImplicitCollection(
				ownerType,
				fieldName,
				itemFieldName,
				itemType);
	}
	
	@Override
	protected MapperWrapper wrapMapper(MapperWrapper next) {
		return new MapperWrapper(next) {
			
			@SuppressWarnings("rawtypes")
			@Override
			public boolean shouldSerializeMember(
					Class definedIn,
					String fieldName) {
				if (!TUXStream.this.implicitFields.contains(fieldName)
						&& definedIn.equals(Object.class)) {
					return false;
				}
				
				return super.shouldSerializeMember(definedIn, fieldName);
			}
			
		};
	}
	
}
