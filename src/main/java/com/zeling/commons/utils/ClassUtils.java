package com.zeling.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassUtils {
	/**
     * 获取父类的真实参数化类型，在本类中泛型会被擦除
     *
     * @param clazz
     * @return 参数化类型
     */
    public final static List<Type> getSuperClassGenericTypes(Class<?> clazz) {
    	List<Type> parameterizedTypes = new ArrayList<>();
    	Type superClass = clazz.getGenericSuperclass();
    	if (superClass instanceof ParameterizedType) {
    		parameterizedTypes.addAll(Arrays.asList(((ParameterizedType) superClass).getActualTypeArguments()));
    	}
    	return parameterizedTypes;
    }
    
    /**
     * 获取类实现的接口的真实参数化类型
     *
     * @param clazz
     * @return 参数化类型列表
     */
    public final static List<Type> getIntefacesGenericTypes(Class<?> clazz) {
    	List<Type> parameterizedTypes = new ArrayList<>();
    	Type[] interfaces = clazz.getGenericInterfaces();
    	for (Type type : interfaces) {
    		if (type instanceof ParameterizedType) {
    			parameterizedTypes.addAll(Arrays.asList(((ParameterizedType) type).getActualTypeArguments()));
    		}
    	}
    	return parameterizedTypes;
    }

    /**
     * 获取一个类的所有字段
     *
     * @param calzz
     * @return 类的字段集合
     */
    public static Set<Field> getAllFiled(Class<?> calzz) {
        // 获取本类的所有字段
        Set<Field> fields = new HashSet<Field>();
        for (Field field : calzz.getFields()) {
            fields.add(field);
        }
        for (Field field : calzz.getDeclaredFields()) {
            fields.add(field);
        }
        // 递归获取父类的所有字段
        Class<?> superClass = calzz.getSuperclass();
        if (!superClass.equals(Object.class)) {
            Set<Field> superFileds = getAllFiled(superClass);
            fields.addAll(superFileds);
        }
        return fields;
    }

    /**
     * 获取一个类的所有方法
     *
     * @param clazz
     * @return 方法集合
     */
    public static Set<Method> getAllMethod(Class<?> clazz) {
        // 获取本类的所有的方法
        Set<Method> methods = new HashSet<Method>();
        for (Method method : clazz.getMethods()) {
            methods.add(method);
        }
        for (Method method : clazz.getDeclaredMethods()) {
            methods.add(method);
        }
        // 递归获取父类的所有方法
        Class<?> superClass = clazz.getSuperclass();
        if (!superClass.equals(Object.class)) {
            Set<Method> superFileds = getAllMethod(superClass);
            methods.addAll(superFileds);
        }
        return methods;
    }

    /**
     * 将from的属性copy到to中
     *
     * @param from
     * @param to
     */
    public final static void copyProperties(Object from, Object to) throws Exception {
    	// from和to的所有属性
        Set<Field> fromSet = getAllFiled(from.getClass());
        Set<Field> toSet = getAllFiled(to.getClass());

        Map<String, Field> toMap = new HashMap<String, Field>();
        for (Field f : toSet) {
            toMap.put(f.getName(), f);
        }

        for (Field f : fromSet) {
        	// 静态熟悉不用复制
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            String name = f.getName();
            Field toField = toMap.get(name);
            // to对象没有的属性不用复制
            if (toField == null) {
                continue;
            }
            // 私有属性也能读写
            toField.setAccessible(true);
            f.setAccessible(true);
            toField.set(to, f.get(from));
        }

    }

    /**
     * 获取一个类的field
     * 
     * @param field
     * @param clazz
     * @return field信息
     */
    public static Field getFieldFromClass(String field, Class<? extends Object> clazz) {
        try {
            return clazz.getDeclaredField(field);
        } catch (Exception e) {
            try {
                return clazz.getField(field);
            } catch (Exception ex) {
            }
        }
        return null;
    }

	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	private ClassUtils() {
		throw new AssertionError(ClassUtils.class.getName() + ": 禁止实例化");
	}
}
