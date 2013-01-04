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
package com.leclercb.taskunifier.api.models.beans.converters;

import java.util.ArrayList;
import java.util.List;

import com.leclercb.commons.api.utils.CheckUtils;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class MultiConverter implements Converter {
	
	private List<Converter> converters;
	
	public MultiConverter() {
		this.converters = new ArrayList<Converter>();
	}
	
	public void addConverter(Converter converter) {
		this.converters.add(converter);
	}
	
	public void addConverter(SingleValueConverter converter) {
		this.converters.add(new SingleValueConverterDecorator(converter));
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class type) {
		for (Converter converter : this.converters) {
			if (converter.canConvert(type))
				return true;
		}
		
		return false;
	}
	
	@Override
	public Object unmarshal(
			HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		for (Converter converter : this.converters) {
			if (converter.canConvert(context.getRequiredType()))
				return converter.unmarshal(reader, context);
		}
		
		return null;
	}
	
	@Override
	public void marshal(
			Object source,
			HierarchicalStreamWriter writer,
			MarshallingContext context) {
		if (source == null)
			return;
		
		for (Converter converter : this.converters) {
			if (converter.canConvert(source.getClass())) {
				converter.marshal(source, writer, context);
				return;
			}
		}
	}
	
	public static class SingleValueConverterDecorator implements Converter {
		
		private SingleValueConverter converter;
		
		public SingleValueConverterDecorator(SingleValueConverter converter) {
			CheckUtils.isNotNull(converter);
			this.converter = converter;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean canConvert(Class type) {
			return this.converter.canConvert(type);
		}
		
		@Override
		public Object unmarshal(
				HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			return this.converter.fromString(reader.getValue());
		}
		
		@Override
		public void marshal(
				Object source,
				HierarchicalStreamWriter writer,
				MarshallingContext context) {
			if (source == null)
				return;
			
			writer.setValue(this.converter.toString(source));
		}
		
	}
	
}
