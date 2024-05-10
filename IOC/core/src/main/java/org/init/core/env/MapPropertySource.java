package org.init.core.env;

import org.init.core.lang.Nullable;
import org.init.core.util.StringUtils;

import java.util.Map;

public class MapPropertySource extends EnumerablePropertySource<Map<String, Object>> {
    public MapPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    @Nullable
    public Object getProperty(String name) {
        return ((Map)this.source).get(name);
    }

    public boolean containsProperty(String name) {
        return ((Map)this.source).containsKey(name);
    }

    public String[] getPropertyNames() {
        return StringUtils.toStringArray(((Map)this.source).keySet());
    }
}
