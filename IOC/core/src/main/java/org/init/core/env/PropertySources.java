package org.init.core.env;

import org.init.core.lang.Nullable;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface PropertySources extends Iterable<PropertySource<?>> {
    default Stream<PropertySource<?>> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    boolean contains(String name);

    @Nullable
    PropertySource<?> get(String name);
}
