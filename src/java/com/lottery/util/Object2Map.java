package com.lottery.util;

import java.lang.reflect.Modifier;
import java.text.Format.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;



public class Object2Map {  
      
	   public static Object map2Object(Map<String, Object> map, Class<?> beanClass) throws Exception {    
	        if (map == null)  
	            return null;    
	  
	        Object obj = beanClass.newInstance();  
	  
	        java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();   
	        for (java.lang.reflect.Field field : fields) {    
	            int mod = field.getModifiers();    
	            if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){    
	                continue;    
	            }    
	  
	            field.setAccessible(true);    
	            field.set(obj, map.get(field.getName()));   
	        }   
	  
	        return obj;    
	    }    
	  
	    public static Map<String, Object> object2Map(Object obj) throws Exception {    
	        if(obj == null){    
	            return null;    
	        }   
	  
	        Map<String, Object> map = new HashMap<String, Object>();    
	  
	        java.lang.reflect.Field[] declaredFields = obj.getClass().getDeclaredFields();    
	        for (java.lang.reflect.Field field : declaredFields) {    
	            field.setAccessible(true);  
	            map.put(field.getName(), field.get(obj));  
	        }    
	  
	        return map;  
	    }   
	}