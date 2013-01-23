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
package com.leclercb.taskunifier.gui.api.rules;

import java.io.InputStream;
import java.io.OutputStream;

import com.leclercb.taskunifier.api.models.AbstractBasicModelFactory;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.xstream.TUXStream;
import com.leclercb.taskunifier.gui.api.accessor.PropertyAccessor;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TaskRuleFactory extends AbstractBasicModelFactory<TaskRule> {
	
	private static TaskRuleFactory FACTORY;
	
	public static TaskRuleFactory getInstance() {
		if (FACTORY == null)
			FACTORY = new TaskRuleFactory();
		
		return FACTORY;
	}
	
	private TaskRuleFactory() {
		
	}
	
	public void execute(Task task, PropertyAccessor<Task> column) {
		for (TaskRule rule : this.getList()) {
			if (!rule.getModelStatus().isEndUserStatus())
				continue;
			
			rule.execute(task, column);
		}
	}
	
	public TaskRule create() {
		TaskRule rule = new TaskRule();
		this.register(rule);
		return rule;
	}
	
	@Override
	public TaskRule create(String title) {
		TaskRule rule = new TaskRule(title);
		this.register(rule);
		return rule;
	}
	
	@Override
	public TaskRule create(ModelId modelId, String title) {
		TaskRule rule = new TaskRule(modelId, title);
		this.register(rule);
		return rule;
	}
	
	@Override
	public void decodeFromXML(InputStream input) {
		XStream xstream = new TUXStream(
				new PureJavaReflectionProvider(),
				new DomDriver("UTF-8"));
		xstream.setMode(XStream.ID_REFERENCES);
		xstream.alias("rules", TaskRule[].class);
		xstream.alias("rule", TaskRule.class);
		xstream.processAnnotations(TaskRule.class);
		
		TaskRule[] rules = (TaskRule[]) xstream.fromXML(input);
		for (TaskRule rule : rules) {
			this.register(rule);
		}
	}
	
	@Override
	public void encodeToXML(OutputStream output) {
		XStream xstream = new TUXStream(
				new PureJavaReflectionProvider(),
				new DomDriver("UTF-8"));
		xstream.setMode(XStream.ID_REFERENCES);
		xstream.alias("rules", TaskRule[].class);
		xstream.alias("rule", TaskRule.class);
		xstream.processAnnotations(TaskRule.class);
		
		xstream.toXML(this.getList().toArray(new TaskRule[0]), output);
	}
	
}
