package antara.common.helpers;

import us.abstracta.jmeter.javadsl.http.DslCacheManager;

import static us.abstracta.jmeter.javadsl.JmeterDsl.httpCache;

public class CacheHelper {
    public static DslCacheManager getCacheDisable() {
        return httpCache()
                .disable();
    }

    public static DslCacheManager getCacheManager() {
        return httpCache();
    }
}