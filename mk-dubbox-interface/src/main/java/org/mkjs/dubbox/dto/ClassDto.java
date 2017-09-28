package org.mkjs.dubbox.dto;

import java.io.Serializable;
import java.util.Map;

public class ClassDto extends InterfaceDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2447815578110650625L;

	public Map<String, String> fields;
}