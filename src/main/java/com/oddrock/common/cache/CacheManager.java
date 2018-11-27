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
	 * @param forceFlag
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void createCache(String cacheName, CacheConfiguration config, boolean forceFlag) {	
		// 如果cache不存在或者需要强制增加，则强制增加
		if(cacheMap.get(cacheName)==null || (cacheMap.get(cacheName)!=null && forceFlag)) {
			Cache cache = cacheManager.createCache("test",config);
			cacheMap.put(cacheName, cache);
		}
	}
	
	public void createCache(String cacheName, CacheConfiguration<?, ?> config) {
		createCache(cacheName, config, false);
	}
	
	@SuppressWarnings("rawtypes")
	public Cache getCache(String cacheName){
		return cacheMap.get(cacheName);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object get(String cacheName, String cacheKey) {
		Cache cache = cacheMap.get(cacheName);
		if(cache!=null) {
			return cache.get(cacheKey);
		}else {
			return null;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void put(String cacheName, String cacheKey, Object value) {
		Cache cache = cacheMap.get(cacheName);
		if(cache!=null) {
			cache.put(cacheKey, value);
		}
	}
}
