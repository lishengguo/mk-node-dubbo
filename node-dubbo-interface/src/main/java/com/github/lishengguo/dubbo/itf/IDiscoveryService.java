package com.github.lishengguo.dubbo.itf;

import java.util.ArrayList;

import com.github.lishengguo.dubbo.dto.ClassDto;

public interface IDiscoveryService {

	/**
	 * 接口类型序列化
	 * 
	 * @param className
	 * @return
	 */
	public ArrayList<ClassDto> interfaceSerializer(String[] interfaceNames) throws Exception;
}
