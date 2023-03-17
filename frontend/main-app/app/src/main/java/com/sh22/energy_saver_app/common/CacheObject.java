package com.sh22.energy_saver_app.common;

import java.time.LocalDateTime;

/**
 * Class for caching objects that expire after some time
 * @param <T> Templated object type
 */
public class CacheObject<T> {
    private T object = null;
    private Long time_set = null;

    public void SetObject(T obj) {
        object = obj;
        time_set = System.currentTimeMillis();
    }

    public T GetObject() {
        if(object == null)
            return null;
        // time_set should never be null if object is not null
        if((System.currentTimeMillis() - time_set) < Constants.CACHE_TIMEOUT) {
            return object;
        } else {
            object = null;
            time_set = null;
            return null;
        }
    }
}
