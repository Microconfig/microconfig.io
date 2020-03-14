package io.microconfig.domain.impl.properties;

import io.microconfig.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static io.microconfig.io.StreamUtils.toList;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
public class ConfigBuildResultImpl implements ConfigBuildResult {
    private final String component;
    private final String environment;
    private final ConfigType configType;
    private final Resolver resolver;
    @Getter
    @With(PRIVATE)
    private final List<Property> properties;

    @Override
    public String getConfigType() {
        return configType.getType();
    }

    @Override
    public ConfigBuildResult build() {
        return forEachProperty(p -> p.resolveBy(resolver));
    }

    @Override
    public Optional<Property> getProperty(String key) {
        return properties.stream()
                .filter(p -> p.getValue().equals(key))
                .findFirst();
    }

    @Override
    public ConfigBuildResult forEachProperty(UnaryOperator<Property> operator) {
        return withProperties(toList(properties, operator));
    }

    @Override
    public <T> T save(PropertySerializer<T> serializer) {
        return serializer.serialize(properties, configType, component, environment);
    }
}