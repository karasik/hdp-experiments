package net.msusevastopol.math.ypys.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionUtils
{
	// Prevent instantiating.
	private CollectionUtils()
	{
	}

	public static <T> List<T> newList()
	{
		return new ArrayList<T>();
	}

	public static <K, V> Map<K, V> newMap()
	{
		return new HashMap<K, V>();
	}
	
	public static <K> Set<K> newSet()
	{
		return new HashSet<K>();
	}

	public static <K, V> V getDefault(Map<K, V> map, K key, V defaultValue)
	{
		if (map.containsKey(key))
			return map.get(key);
		return defaultValue;
	}

	public static <K, V> void initMap(Map<K, V> map, K key, V value)
	{
		if (map.containsKey(key))
			return;
		map.put(key, value);
	}
}
