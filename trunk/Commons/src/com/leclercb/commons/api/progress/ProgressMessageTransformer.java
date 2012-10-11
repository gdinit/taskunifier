package com.leclercb.commons.api.progress;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;

public interface ProgressMessageTransformer {
	
	public abstract boolean acceptsEvent(ListChangeEvent event);
	
	public abstract Object getEventValue(ListChangeEvent event, String key);
	
}
