package io.microconfig.core.properties.impl;

import io.microconfig.core.properties.*;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static io.microconfig.core.properties.impl.PropertyImpl.asKeyValue;
import static io.microconfig.utils.StreamUtils.*;

@RequiredArgsConstructor
@EqualsAndHashCode
public class PropertiesImpl implements Properties {
    private final List<TypedProperties> properties;

    public static Properties composite(List<Properties> properties) {
        return new PropertiesImpl(flatMapEach(properties, Properties::asList));
    }

    @Override
    public List<TypedProperties> asList() {
        return properties;
    }

    @Override
    public Properties withoutTempValues() {
        return forEachComponent(TypedProperties::withoutTempValues);
    }

    @Override
    public Properties resolveBy(Resolver resolver) {
        return forEachComponent(c -> c.resolveBy(resolver));
    }

    @Override
    public List<Property> getProperties() {
        return flatMapEach(properties, TypedProperties::getProperties);
    }

    @Override
    public Map<String, String> propertiesAsKeyValue() {
        return asKeyValue(getProperties());
    }

    @Override
    public Optional<Property> getPropertyWithKey(String key) {
        return findFirstResult(properties, r -> r.getPropertyWithKey(key));
    }

    @Override
    public <T> List<T> save(PropertySerializer<T> serializer) {
        return forEach(properties, p -> p.save(serializer));
    }

    private Properties forEachComponent(UnaryOperator<TypedProperties> applyFunction) {
        return new PropertiesImpl(forEach(properties, applyFunction));
    }
}