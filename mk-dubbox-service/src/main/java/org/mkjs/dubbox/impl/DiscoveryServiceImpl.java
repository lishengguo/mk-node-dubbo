package org.mkjs.dubbox.impl;

import java.util.ArrayList;

import org.mkjs.dubbox.dto.ClassDto;
import org.mkjs.dubbox.itf.IDiscoveryService;
import org.mkjs.dubbox.utils.InterfaceSerializer;

public class DiscoveryServiceImpl implements IDiscoveryService {

	public ArrayList<ClassDto> interfaceSerializer(String[] interfaceNames) throws Exception {
		return InterfaceSerializer.serialize(interfaceNames);
	} 
}
