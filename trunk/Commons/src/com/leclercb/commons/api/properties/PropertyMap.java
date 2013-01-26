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
package com.leclercb.commons.api.properties;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.leclercb.commons.api.event.propertychange.PropertyChangeSupport;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.properties.coders.BooleanCoder;
import com.leclercb.commons.api.properties.coders.ByteCoder;
import com.leclercb.commons.api.properties.coders.CalendarCoder;
import com.leclercb.commons.api.properties.coders.ColorCoder;
import com.leclercb.commons.api.properties.coders.DoubleCoder;
import com.leclercb.commons.api.properties.coders.FloatCoder;
import com.leclercb.commons.api.properties.coders.IntegerCoder;
import com.leclercb.commons.api.properties.coders.LocaleCoder;
import com.leclercb.commons.api.properties.coders.LongCoder;
import com.leclercb.commons.api.properties.coders.SimpleDateFormatCoder;
import com.leclercb.commons.api.properties.coders.StringCoder;
import com.leclercb.commons.api.properties.events.ReloadPropertiesListener;
import com.leclercb.commons.api.properties.events.ReloadPropertiesSupport;
import com.leclercb.commons.api.properties.events.ReloadPropertiesSupported;
import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.properties.events.SavePropertiesSupport;
import com.leclercb.commons.api.properties.events.SavePropertiesSupported;
import com.leclercb.commons.api.properties.exc.PropertiesException;
import com.leclercb.commons.api.utils.CheckUtils;

public class PropertyMap extends Properties implements PropertyChangeSupported, SavePropertiesSupported, ReloadPropertiesSupported {
	
	private static transient Map<Class<?>, PropertiesCoder<?>> DEFAULT_CODERS;
	
	static {
		DEFAULT_CODERS = new HashMap<Class<?>, PropertiesCoder<?>>();
		
		addDefaultCoder(new BooleanCoder());
		addDefaultCoder(new ByteCoder());
		addDefaultCoder(new CalendarCoder());
		addDefaultCoder(new ColorCoder());
		addDefaultCoder(new DoubleCoder());
		addDefaultCoder(new FloatCoder());
		addDefaultCoder(new IntegerCoder());
		addDefaultCoder(new LocaleCoder());
		addDefaultCoder(new LongCoder());
		addDefaultCoder(new SimpleDateFormatCoder());
		addDefaultCoder(new StringCoder());
	}
	
	public static <T> void addDefaultCoder(PropertiesCoder<T> coder) {
		DEFAULT_CODERS.put(coder.getCoderClass(), coder);
	}
	
	public static <T> PropertiesCoder<T> removeDefaultCoder(Class<T> cls) {
		@SuppressWarnings("unchecked")
		PropertiesCoder<T> coder = (PropertiesCoder<T>) DEFAULT_CODERS.remove(cls);
		
		return coder;
	}
	
	private transient PropertyChangeSupport propertyChangeSupport;
	private transient SavePropertiesSupport savePropertiesSupport;
	private transient ReloadPropertiesSupport reloadPropertiesSupport;
	
	private Properties properties;
	private PropertyMap exceptionProperties;
	private transient final Map<Class<?>, PropertiesCoder<?>> coders;
	
	public PropertyMap() {
		this(new Properties(), null);
	}
	
	public PropertyMap(Properties properties) {
		this(properties, null);
	}
	
	public PropertyMap(Properties properties, Properties exceptionProperties) {
		this(properties, (exceptionProperties == null ? null : new PropertyMap(
				exceptionProperties,
				null)));
	}
	
	public PropertyMap(Properties properties, PropertyMap exceptionProperties) {
		CheckUtils.isNotNull(properties);
		
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.savePropertiesSupport = new SavePropertiesSupport();
		this.reloadPropertiesSupport = new ReloadPropertiesSupport();
		
		this.properties = properties;
		this.exceptionProperties = exceptionProperties;
		
		this.coders = new HashMap<Class<?>, PropertiesCoder<?>>();
	}
	
	public void replaceKey(String oldKey, String newKey) {
		String value = this.getStringProperty(oldKey);
		this.setStringProperty(newKey, value);
		this.remove(oldKey);
	}
	
	@Override
	public void addSavePropertiesListener(SavePropertiesListener listener) {
		this.savePropertiesSupport.addSavePropertiesListener(listener);
	}
	
	@Override
	public void removeSavePropertiesListener(SavePropertiesListener listener) {
		this.savePropertiesSupport.removeSavePropertiesListener(listener);
	}
	
	@Override
	public void addReloadPropertiesListener(ReloadPropertiesListener listener) {
		this.reloadPropertiesSupport.addReloadPropertiesListener(listener);
	}
	
	@Override
	public void removeReloadPropertiesListener(ReloadPropertiesListener listener) {
		this.reloadPropertiesSupport.removeReloadPropertiesListener(listener);
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	@Override
	public void addPropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(
				propertyName,
				listener);
	}
	
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	@Override
	public void removePropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(
				propertyName,
				listener);
	}
	
	public <T> void addCoder(PropertiesCoder<T> coder) {
		this.coders.put(coder.getCoderClass(), coder);
	}
	
	public <T> PropertiesCoder<T> removeCoder(Class<T> cls) {
		@SuppressWarnings("unchecked")
		PropertiesCoder<T> coder = (PropertiesCoder<T>) this.coders.remove(cls);
		
		return coder;
	}
	
	public Boolean getBooleanProperty(String key) {
		return this.getObjectProperty(key, Boolean.class);
	}
	
	public Boolean getBooleanProperty(String key, Boolean def) {
		return this.getObjectProperty(key, Boolean.class, def);
	}
	
	public Byte getByteProperty(String key) {
		return this.getObjectProperty(key, Byte.class);
	}
	
	public Byte getByteProperty(String key, Byte def) {
		return this.getObjectProperty(key, Byte.class, def);
	}
	
	public Calendar getCalendarProperty(String key) {
		return this.getObjectProperty(key, Calendar.class);
	}
	
	public Calendar getCalendarProperty(String key, Calendar def) {
		return this.getObjectProperty(key, Calendar.class, def);
	}
	
	public Color getColorProperty(String key) {
		return this.getObjectProperty(key, Color.class);
	}
	
	public Color getColorProperty(String key, Color def) {
		return this.getObjectProperty(key, Color.class, def);
	}
	
	public Double getDoubleProperty(String key) {
		return this.getObjectProperty(key, Double.class);
	}
	
	public Double getDoubleProperty(String key, Double def) {
		return this.getObjectProperty(key, Double.class, def);
	}
	
	public <T extends Enum<?>> T getEnumProperty(String key, Class<T> enumClass) {
		String value = this.properties.getProperty(key);
		
		if (value == null || value.length() == 0)
			return null;
		
		T[] enumConstants = enumClass.getEnumConstants();
		for (T enumConstant : enumConstants)
			if (enumConstant.name().equals(value))
				return enumConstant;
		
		if (this.exceptionProperties != null)
			return this.exceptionProperties.getEnumProperty(key, enumClass);
		
		throw new PropertiesException(this.getExceptionMessage(key, value));
	}
	
	public <T extends Enum<?>> T getEnumProperty(
			String key,
			Class<T> enumClass,
			T def) {
		try {
			T value = this.getEnumProperty(key, enumClass);
			
			if (value == null)
				return def;
			
			return value;
		} catch (PropertiesException e) {
			return def;
		}
	}
	
	public Float getFloatProperty(String key) {
		return this.getObjectProperty(key, Float.class);
	}
	
	public Float getFloatProperty(String key, Float def) {
		return this.getObjectProperty(key, Float.class, def);
	}
	
	public Integer getIntegerProperty(String key) {
		return this.getObjectProperty(key, Integer.class);
	}
	
	public Integer getIntegerProperty(String key, Integer def) {
		return this.getObjectProperty(key, Integer.class, def);
	}
	
	public Locale getLocaleProperty(String key) {
		return this.getObjectProperty(key, Locale.class);
	}
	
	public Locale getLocaleProperty(String key, Locale def) {
		return this.getObjectProperty(key, Locale.class, def);
	}
	
	public Long getLongProperty(String key) {
		return this.getObjectProperty(key, Long.class);
	}
	
	public Long getLongProperty(String key, Long def) {
		return this.getObjectProperty(key, Long.class, def);
	}
	
	public <T> T getObjectProperty(String key, Class<T> cls) {
		String value = this.properties.getProperty(key);
		
		try {
			@SuppressWarnings("unchecked")
			PropertiesCoder<T> coder = (PropertiesCoder<T>) this.coders.get(cls);
			
			if (coder == null)
				throw new PropertiesException("No coder found for class: "
						+ cls);
			
			return coder.decode(value);
		} catch (Exception e) {
			if (this.exceptionProperties != null)
				return this.exceptionProperties.getObjectProperty(key, cls);
			
			throw new PropertiesException(
					this.getExceptionMessage(key, value),
					e);
		}
	}
	
	public <T> T getObjectProperty(String key, Class<T> cls, T def) {
		try {
			T value = this.getObjectProperty(key, cls);
			
			if (value == null)
				return def;
			
			return value;
		} catch (PropertiesException e) {
			return def;
		}
	}
	
	public SimpleDateFormat getSimpleDateFormatProperty(String key) {
		return this.getObjectProperty(key, SimpleDateFormat.class);
	}
	
	public SimpleDateFormat getSimpleDateFormatProperty(
			String key,
			SimpleDateFormat def) {
		return this.getObjectProperty(key, SimpleDateFormat.class, def);
	}
	
	public String getStringProperty(String key) {
		return this.getObjectProperty(key, String.class);
	}
	
	public String getStringProperty(String key, String def) {
		return this.getObjectProperty(key, String.class, def);
	}
	
	private Object setProperty(
			String key,
			String value,
			Object oldValue,
			Object newValue) {
		this.properties.setProperty(key, value);
		
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(
				this,
				key,
				oldValue,
				newValue));
		
		return oldValue;
	}
	
	public Object setBooleanProperty(String key, Boolean value) {
		return this.setObjectProperty(key, Boolean.class, value);
	}
	
	public Object setByteProperty(String key, Byte value) {
		return this.setObjectProperty(key, Byte.class, value);
	}
	
	public Object setCalendarProperty(String key, Calendar value) {
		return this.setObjectProperty(key, Calendar.class, value);
	}
	
	public Object setColorProperty(String key, Color value) {
		return this.setObjectProperty(key, Color.class, value);
	}
	
	public Object setDoubleProperty(String key, Double value) {
		return this.setObjectProperty(key, Double.class, value);
	}
	
	public <T extends Enum<?>> Object setEnumProperty(
			String key,
			Class<T> enumClass,
			T value) {
		T oldValue = this.getEnumProperty(key, enumClass);
		return this.setProperty(
				key,
				(value == null ? "" : value.name()),
				oldValue,
				value);
	}
	
	public Object setFloatProperty(String key, Float value) {
		return this.setObjectProperty(key, Float.class, value);
	}
	
	public Object setIntegerProperty(String key, Integer value) {
		return this.setObjectProperty(key, Integer.class, value);
	}
	
	public Object setLocaleProperty(String key, Locale value) {
		return this.setObjectProperty(key, Locale.class, value);
	}
	
	public Object setLongProperty(String key, Long value) {
		return this.setObjectProperty(key, Long.class, value);
	}
	
	public <T> Object setRawObjectProperty(
			String key,
			Class<T> cls,
			Object value) {
		try {
			T castValue = cls.cast(value);
			return this.setObjectProperty(key, cls, castValue);
		} catch (PropertiesException e) {
			throw e;
		} catch (Exception e) {
			throw new PropertiesException(
					"Cannot cast object of class: " + cls,
					e);
		}
	}
	
	public <T> Object setObjectProperty(String key, Class<T> cls, T value) {
		return this.setObjectProperty(key, cls, value, false);
	}
	
	@SuppressWarnings("unchecked")
	public <T> Object setObjectProperty(
			String key,
			Class<T> cls,
			T value,
			boolean force) {
		Object oldValue = this.getObjectProperty(key, cls);
		
		try {
			PropertiesCoder<T> coder = (PropertiesCoder<T>) this.coders.get(cls);
			
			if (coder == null)
				coder = (PropertiesCoder<T>) DEFAULT_CODERS.get(cls);
			
			if (coder == null)
				throw new PropertiesException("No coder found for class: "
						+ cls);
			
			String encodedValue = coder.encode(value);
			
			return this.setProperty(
					key,
					(encodedValue == null ? "" : encodedValue),
					oldValue,
					value);
		} catch (Exception e) {
			throw new PropertiesException("Cannot encode object of class: "
					+ cls, e);
		}
	}
	
	public Object setSimpleDateFormatProperty(String key, SimpleDateFormat value) {
		return this.setObjectProperty(key, SimpleDateFormat.class, value);
	}
	
	public Object setStringProperty(String key, String value) {
		return this.setObjectProperty(key, String.class, value);
	}
	
	private String getExceptionMessage(String key, String value) {
		return "Property value \""
				+ value
				+ "\" for key \""
				+ key
				+ "\" is invalid";
	}
	
	@Override
	public void clear() {
		this.properties.clear();
	}
	
	@Override
	public PropertyMap clone() {
		Properties properties = null;
		PropertyMap exceptionProperties = null;
		
		properties = (Properties) this.properties.clone();
		
		if (this.exceptionProperties != null)
			exceptionProperties = this.exceptionProperties.clone();
		
		return new PropertyMap(properties, exceptionProperties);
	}
	
	@Override
	public boolean contains(Object value) {
		return this.properties.contains(value);
	}
	
	@Override
	public boolean containsKey(Object key) {
		return this.properties.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		return this.properties.containsValue(value);
	}
	
	@Override
	public Enumeration<Object> elements() {
		return this.properties.elements();
	}
	
	@Override
	public Set<java.util.Map.Entry<Object, Object>> entrySet() {
		return this.properties.entrySet();
	}
	
	@Override
	public boolean equals(Object o) {
		return this.properties.equals(o);
	}
	
	@Override
	public Object get(Object key) {
		return this.properties.get(key);
	}
	
	@Override
	public String getProperty(String key, String defaultValue) {
		return this.properties.getProperty(key, defaultValue);
	}
	
	@Override
	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}
	
	@Override
	public int hashCode() {
		return this.properties.hashCode();
	}
	
	@Override
	public boolean isEmpty() {
		return this.properties.isEmpty();
	}
	
	@Override
	public Set<Object> keySet() {
		return this.properties.keySet();
	}
	
	@Override
	public Enumeration<Object> keys() {
		return this.properties.keys();
	}
	
	@Override
	public void list(PrintStream out) {
		this.properties.list(out);
	}
	
	@Override
	public void list(PrintWriter out) {
		this.properties.list(out);
	}
	
	@Override
	public void load(InputStream inStream) throws IOException {
		this.properties.load(inStream);
		this.reloadPropertiesSupport.fireReloadPropertiesPerformed();
	}
	
	@Override
	public void load(Reader reader) throws IOException {
		this.properties.load(reader);
		this.reloadPropertiesSupport.fireReloadPropertiesPerformed();
	}
	
	@Override
	public void loadFromXML(InputStream in) throws IOException,
			InvalidPropertiesFormatException {
		this.properties.loadFromXML(in);
		this.reloadPropertiesSupport.fireReloadPropertiesPerformed();
	}
	
	@Override
	public Enumeration<?> propertyNames() {
		return this.properties.propertyNames();
	}
	
	@Override
	public Object put(Object key, Object value) {
		return this.properties.put(key, value);
	}
	
	@Override
	public void putAll(Map<? extends Object, ? extends Object> t) {
		this.properties.putAll(t);
	}
	
	@Override
	public Object remove(Object key) {
		return this.properties.remove(key);
	}
	
	@Override
	@Deprecated
	public void save(OutputStream out, String comments) {
		this.properties.save(out, comments);
	}
	
	@Override
	public Object setProperty(String key, String value) {
		return this.setStringProperty(key, value);
	}
	
	@Override
	public int size() {
		return this.properties.size();
	}
	
	@Override
	public void store(OutputStream out, String comments) throws IOException {
		this.savePropertiesSupport.fireSavePropertiesPerformed();
		this.properties.store(out, comments);
	}
	
	@Override
	public void store(Writer writer, String comments) throws IOException {
		this.savePropertiesSupport.fireSavePropertiesPerformed();
		this.properties.store(writer, comments);
	}
	
	@Override
	public void storeToXML(OutputStream os, String comment, String encoding)
			throws IOException {
		this.savePropertiesSupport.fireSavePropertiesPerformed();
		this.properties.storeToXML(os, comment, encoding);
	}
	
	@Override
	public void storeToXML(OutputStream os, String comment) throws IOException {
		this.savePropertiesSupport.fireSavePropertiesPerformed();
		this.properties.storeToXML(os, comment);
	}
	
	@Override
	public Set<String> stringPropertyNames() {
		return this.properties.stringPropertyNames();
	}
	
	@Override
	public String toString() {
		return this.properties.toString();
	}
	
	@Override
	public Collection<Object> values() {
		return this.properties.values();
	}
	
}
