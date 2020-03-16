package io.microconfig.domain.impl.properties.resolvers.placeholder.strategies.component;

import java.util.Optional;

public interface ComponentProperty {
    String key();

    Optional<String> value(String componentName, String componentType);
}