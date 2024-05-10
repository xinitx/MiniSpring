package org.init.core.io;


import org.init.core.lang.Nullable;
import org.init.core.util.Assert;
import org.init.core.util.ClassUtils;
import org.init.core.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ClassPathResource implements Resource{
    private final String path;
    private final String absolutePath;
    @Nullable
    private final ClassLoader classLoader;
    @Nullable
    private final Class<?> clazz;
    public ClassPathResource(String path) {
        this(path, (ClassLoader)null);
    }
    public ClassPathResource(String path, @Nullable ClassLoader classLoader) {
        Assert.notNull(path, "Path must not be null");
        String pathToUse = StringUtils.cleanPath(path);
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }

        this.path = pathToUse;
        this.absolutePath = pathToUse;
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
        this.clazz = null;
    }


    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is;
        if (this.clazz != null) {
            is = this.clazz.getResourceAsStream(this.path);
        } else if (this.classLoader != null) {
            is = this.classLoader.getResourceAsStream(this.absolutePath);
        } else {
            is = ClassLoader.getSystemResourceAsStream(this.absolutePath);
        }

        if (is == null) {
            throw new FileNotFoundException(this.getDescription() + " cannot be opened because it does not exist");
        } else {
            return is;
        }
    }
    public String getDescription() {
        return "class path resource [" + this.absolutePath + "]";
    }
}
