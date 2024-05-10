package org.init.core.env;

import org.init.core.lang.Nullable;
import org.init.core.util.Assert;

import java.util.Map;

public class SystemEnvironmentPropertySource extends MapPropertySource {
    public SystemEnvironmentPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    public boolean containsProperty(String name) {
        return this.getProperty(name) != null;
    }

    @Nullable
    public Object getProperty(String name) {
        String actualName = this.resolvePropertyName(name);
        if (this.logger.isDebugEnabled() && !name.equals(actualName)) {
            this.logger.debug("PropertySource '" + this.getName() + "' does not contain property '" + name + "', but found equivalent '" + actualName + "'");
        }

        return super.getProperty(actualName);
    }

    protected final String resolvePropertyName(String name) {
        Assert.notNull(name, "Property name must not be null");
        String resolvedName = this.checkPropertyName(name);
        if (resolvedName != null) {
            return resolvedName;
        } else {
            String uppercasedName = name.toUpperCase();
            if (!name.equals(uppercasedName)) {
                resolvedName = this.checkPropertyName(uppercasedName);
                if (resolvedName != null) {
                    return resolvedName;
                }
            }

            return name;
        }
    }

    @Nullable
    private String checkPropertyName(String name) {
        if (((Map)this.source).containsKey(name)) {
            return name;
        } else {
            String noDotName = name.replace('.', '_');
            if (!name.equals(noDotName) && ((Map)this.source).containsKey(noDotName)) {
                return noDotName;
            } else {
                String noHyphenName = name.replace('-', '_');
                if (!name.equals(noHyphenName) && ((Map)this.source).containsKey(noHyphenName)) {
                    return noHyphenName;
                } else {
                    String noDotNoHyphenName = noDotName.replace('-', '_');
                    return !noDotName.equals(noDotNoHyphenName) && ((Map)this.source).containsKey(noDotNoHyphenName) ? noDotNoHyphenName : null;
                }
            }
        }
    }
}
