package com.github.lishengguo.dubbo.dto;

import java.io.Serializable;
import java.util.List;
 

public class MethodDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 834381071350626830L;
	public String name;
	public List<ParameterDto> parameters;
}
