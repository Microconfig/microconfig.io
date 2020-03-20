package io.microconfig.core.properties.impl;

import io.microconfig.core.configtypes.ConfigType;
import io.microconfig.core.properties.Property;
import io.microconfig.core.properties.PropertySerializer;
import io.microconfig.core.properties.Resolver;
import io.microconfig.core.properties.TypedProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.microconfig.core.properties.impl.PropertyImpl.asKeyValue;
import static io.microconfig.utils.StreamUtils.filter;
import static io.microconfig.utils.StreamUtils.forEach;
import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode
@RequiredArgsConstructor
public class TypedPropertiesImpl implements TypedProperties {
    @Getter
    private final String component;
    private final String environment;
    private final ConfigType configType;
    @Getter
    @With(PRIVATE)
    private final List<Property> properties;

    @Override
    public String getConfigType() {
        return configType.getType();
    }

    @Override
    public TypedProperties withoutTempValues() {
        return withProperties(filter(properties, p -> !p.isTemp()));
    }

    @Override
    public TypedProperties resolveBy(Resolver resolver) {
        return withProperties(forEach(properties, p -> p.resolveBy(resolver, configType.getType())));
    }

    @Override
    public Map<String, String> propertiesAsKeyValue() {
        return asKeyValue(getProperties());
    }

    @Override
    public Optional<Property> getPropertyWithKey(String key) {
        return properties.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst();
    }

    @Override
    public <T> T save(PropertySerializer<T> serializer) {
        return serializer.serialize(properties, configType, component, environment);
    }
}