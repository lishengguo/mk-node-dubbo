package com.github.lishengguo.dubbo.impl;

import java.util.ArrayList;

import com.github.lishengguo.dubbo.dto.ClassDto;
import com.github.lishengguo.dubbo.itf.IDiscoveryService;
import com.github.lishengguo.dubbo.utils.InterfaceSerializer;

public class DiscoveryServiceImpl implements IDiscoveryService {

	public ArrayList<ClassDto> interfaceSerializer(String[] interfaceNames) throws Exception {
		return InterfaceSerializer.serialize(interfaceNames);
	} 
}
