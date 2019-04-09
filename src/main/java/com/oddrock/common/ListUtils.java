package com.oddrock.common;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
	/**
	 * 将list按照len长度分为多个list
	 * @param list
	 * @param len
	 * @return
	 */
	public static <T> List<List<T>> splitList(List<T> list, int len) {  
		if (list == null || list.size() == 0 || len < 1) {  
			return null;  
		}  
		List<List<T>> result = new ArrayList<List<T>>();  
		int size = list.size();  
		int count = (size + len - 1) / len;  
		for (int i = 0; i < count; i++) {  
			List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));  
			result.add(subList);  
		}  
		return result;  
	}
}
