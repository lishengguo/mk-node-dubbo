package org.mkjs.dubbox.itf;

import java.util.ArrayList;

import org.mkjs.dubbox.dto.ClassDto;

public interface IDiscoveryService {

	/**
	 * 接口类型序列化
	 * 
	 * @param className
	 * @return
	 */
	public ArrayList<ClassDto> interfaceSerializer(String[] interfaceNames) throws Exception;
}
