package com.github.lishengguo.dubbo.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
 

public class InterfaceDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8782978634425296484L;
	public String name;
	public List<MethodDto> methods;
	public Map<String, String> requestMapping;
}
