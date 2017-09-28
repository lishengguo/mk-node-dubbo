package com.github.lishengguo.dubbo.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;

import com.github.lishengguo.dubbo.annotation.ApiContext;
import com.github.lishengguo.dubbo.annotation.ApiResult;
import com.github.lishengguo.dubbo.dto.ClassDto;
import com.github.lishengguo.dubbo.dto.InterfaceDto;
import com.github.lishengguo.dubbo.dto.MethodDto;
import com.github.lishengguo.dubbo.dto.ParameterDto;

public class InterfaceSerializer {

	// 单例
	static InterfaceSerializer INSTANCE = new InterfaceSerializer();

	public static ArrayList<ClassDto> serialize(String[] interfaceNames) throws Exception {
		return INSTANCE.serializeInterfaces(interfaceNames);
	}

	public ArrayList<ClassDto> serializeInterfaces(String[] interfaceNames) throws Exception {
		ArrayList<ClassDto> list = new ArrayList<>();
		HashMap<String, ClassDto> clsDtoCache = new HashMap<>();

		for (int i = 0; i < interfaceNames.length; i++) {
			ClassDto itf = new ClassDto();
			itf.name = interfaceNames[i];
			itf.requestMapping = new HashMap<String, String>();
			serializeMethods(itf, clsDtoCache);
			if (itf.methods != null && itf.methods.size() > 0) {
				list.add(itf);
			}
		}

		list.addAll(clsDtoCache.values());

		return list;
	}

	String ROOTTAG = "@";
	String DELIMITER = ",";

	void serializeMethods(InterfaceDto itfDto, HashMap<String, ClassDto> clsDtoCache) throws Exception {
		Class<?> itf = null;
		try {
			itf = Class.forName(itfDto.name);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			return;
		}

		if (itf == null || !itf.isAnnotationPresent(RequestMapping.class)) {
			return;
		}

		RequestMapping itfMapping = (RequestMapping) itf.getAnnotation(RequestMapping.class);
		itfDto.requestMapping.put(ROOTTAG, String.join(DELIMITER, itfMapping.value()));

		Method[] methods = itf.getMethods();
		itfDto.methods = new ArrayList<>();
		List<String> methodNames = new ArrayList<>();
		for (Method m : methods) {
			if (!m.isAnnotationPresent(RequestMapping.class) || !Modifier.isAbstract(m.getModifiers())) {
				continue;
			}
			MethodDto methodDto = new MethodDto();
			methodDto.name = m.getName();
			if (methodNames.contains(methodDto.name)) {
				throw new Exception("暂时不支持接口的方法名相同！");
			} else {
				methodNames.add(methodDto.name);
			}
			methodDto.parameters = serializeParameterTypes(m, clsDtoCache);
			RequestMapping methodMapping = (RequestMapping) m.getAnnotation(RequestMapping.class);
			ApiResult apiResult = (ApiResult) m.getAnnotation(ApiResult.class);
			ApiContext apiContext = (ApiContext) m.getAnnotation(ApiContext.class);
			if (apiContext != null && methodDto.parameters.size() > 0) {
				methodDto.parameters.get(0).$ctx = apiContext.value();
			}
			String urls = String.join(DELIMITER, methodMapping.value());
			if (apiResult != null) {
				urls += "?" + apiResult.value();
			}
			itfDto.requestMapping.put(m.getName(), urls);
			itfDto.methods.add(methodDto);
		}
	}

	List<ParameterDto> serializeParameterTypes(Method m, Map<String, ClassDto> clsDtoCache) {

		Parameter[] parameters = m.getParameters();
		List<String> dependTypes = new ArrayList<>();
		List<ParameterDto> parameterDtos = new ArrayList<>();

		for (int i = 0; i < parameters.length; i++) {
			Parameter param = parameters[i];
			Class<?> cls = param.getType();
			Type paraType = param.getParameterizedType();
			ParameterDto para = new ParameterDto();
			parameterDtos.add(para);
			if (param.isAnnotationPresent(ApiContext.class)) {
				ApiContext restContext = (ApiContext) param.getAnnotation(ApiContext.class);
				para.$ctx = restContext.value();
			}

			String typeName = null;
			if (paraType != null) {
				typeName = paraType.getTypeName();
			} else {
				typeName = cls.getTypeName();
			}
			para.$class = typeName;
			para.$name = param.getName();

			serializeType(cls, paraType, dependTypes, clsDtoCache);
		}
		dependTypes.forEach((item) -> serializeClass(item, clsDtoCache));
		return parameterDtos;
	}

	void serializeClass(String className, Map<String, ClassDto> clsDtoCache) {
		try {
			Class<?> cls = Class.forName(className);
			serializeClass(cls, clsDtoCache, null, null);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	void serializeClass(Class<?> cls, Map<String, ClassDto> clsDtoCache, String genClsName, Type[] typeParams) {
		String clsName = cls.getName();
		boolean isGenicCls = null != genClsName;
		TypeVariable<?>[] genCls = cls.getTypeParameters();
		if (isGenicCls) {
			clsName = genClsName;
		}
		List<String> dependTypes = new ArrayList<>();
		if (clsDtoCache.containsKey(clsName) && clsDtoCache.get(clsName).name != null || clsName.indexOf("java.") == 0
				|| clsName.indexOf("sun.") == 0) {
			return;
		}
		ClassDto dto = new ClassDto();
		dto.name = clsName;
		dto.fields = new HashMap<String, String>();
		List<Field> fieldList = new ArrayList<>();
		Class<?> tempClass = cls;
		while (tempClass != null) {
			fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
			tempClass = tempClass.getSuperclass(); // 递归父类
		}
		for (Field field : fieldList) {
			Type genType = field.getGenericType();
			Class<?> fieldCls = field.getType();
			String typeName = genType.getTypeName();
			if (isGenicCls) {
				for (int i = 0; i < genCls.length; i++) {
					if (genCls[i].getName().equals(typeName)) {
						typeName = typeParams[i].getTypeName();
						try {
							fieldCls = Class.forName(typeName);
							break;
						} catch (ClassNotFoundException e) {
							System.out.println(e.getMessage());
						}
					}
				}
			}
			dto.fields.put(field.getName(), typeName);
			serializeType(fieldCls, genType, dependTypes, clsDtoCache);
		}

		clsDtoCache.put(clsName, dto);

		dependTypes.forEach((item) -> serializeClass(item, clsDtoCache));
	}

	void serializeType(Class<?> cls, Type genType, List<String> dependTypes, Map<String, ClassDto> clsDtoCache) {
		if (cls.isPrimitive()) {
			return;
		}
		String typeName = genType.getTypeName();
		Type[] actualTypes = null;
		ClassDto emptyClassDto = new ClassDto();
		emptyClassDto.fields = new HashMap<>();
		emptyClassDto.name = typeName;

		if (typeName.indexOf("<") != -1) {
			actualTypes = ((ParameterizedType) genType).getActualTypeArguments();
			for (int j = 0; j < actualTypes.length; j++) {
				dependTypes.add(actualTypes[j].getTypeName());
			}
		}

		if (cls.isArray()) {
			Class<?> realType = cls.getComponentType();
			serializeClass(realType, clsDtoCache, null, null);
			clsDtoCache.put(typeName, emptyClassDto);
		} else if (typeName.indexOf("java.util.List<") != -1) {
			clsDtoCache.put(typeName, emptyClassDto);
		} else if (typeName.indexOf("Map<") != -1) {
			emptyClassDto.fields.put("*", actualTypes[1].getTypeName());
			clsDtoCache.put(typeName, emptyClassDto);
		} else if (typeName.indexOf("<") != -1) {
			serializeClass(cls, clsDtoCache, typeName, actualTypes);
		} else {
			serializeClass(cls, clsDtoCache, null, null);
		}
	}
}
