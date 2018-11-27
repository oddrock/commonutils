package com.oddrock.common.cache;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheManagerBuilder;

public class CacheManager {
	private static Logger logger = Logger.getLogger(CacheManager.class);
	private org.ehcache.CacheManager cacheManager;
	@SuppressWarnings("rawtypes")
	private Map<String, Cache> cacheMap;
	
	@SuppressWarnings("rawtypes")
	public CacheManager() {
		super();
		/*Configuration xmlConf = new XmlConfiguration(getClass().getResource("/ehcache.xml"));
		cacheManager = CacheManagerBuilder.newCacheManager(xmlConf);*/
		cacheManager= CacheManagerBuilder.newCacheManagerBuilder().build(); 
		cacheManager.init();
		cacheMap = new HashMap<String, Cache>();
		logger.warn("缓存管理器初始化完成......");
	}

	/**
	 * 根据cache名和配置创建cache
	 * @param cacheName
	 * @param config
	 * @param forceFlag		如果已有缓存，是否强制创建
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void createCache(String cacheName, CacheConfiguration config, boolean forceFlag) {	
		// 如果cache不存在或者需要强制增加，则强制增加
		if(cacheMap.get(cacheName)==null || (cacheMap.get(cacheName)!=null && forceFlag)) {
			Cache cache = cacheManager.createCache(cacheName,config);
			cacheMap.put(cacheName, cache);
		}
	}
	
	public void createCache(String cacheName, CacheConfiguration<?, ?> config) {
		createCache(cacheName, config, false);
	}
	
	/**
	 * 根据缓存名和缓存键查询缓存值
	 * @param cacheName
	 * @param cacheKey
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object get(String cacheName, String cacheKey) {
		Cache cache = cacheMap.get(cacheName);
		if(cache!=null) {
			return cache.get(cacheKey);
		}else {
			return null;
		}
	}
	
	/**
	 * 存储缓存值
	 * @param cacheName
	 * @param cacheKey
	 * @param value
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void put(String cacheName, String cacheKey, Object value) {
		Cache cache = cacheMap.get(cacheName);
		if(cache!=null) {
			cache.put(cacheKey, value);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized void clear() {
		for(Cache cache : cacheMap.values()) {
			if(cache!=null) {
				cache.clear();
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized void clear(String cacheName) {
		Cache cache = cacheMap.get(cacheName);
		if(cache!=null) {
			cache.clear();
		}
	}
}
