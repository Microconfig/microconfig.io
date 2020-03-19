package io.microconfig.core.properties.impl;

import io.microconfig.core.properties.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static io.microconfig.core.properties.impl.PropertyImpl.asKeyValue;
import static io.microconfig.utils.StreamUtils.*;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class CompositeComponentPropertiesImpl implements CompositeComponentProperties {
    private final List<ComponentProperties> properties;

    public static CompositeComponentProperties composite(List<ComponentProperties> properties) {
        return new CompositeComponentPropertiesImpl(properties);
    }

    @Override
    public List<ComponentProperties> asList() {
        return properties;
    }

    @Override
    public CompositeComponentProperties withoutTempValues() {
        return forEachComponent(ComponentProperties::withoutTempValues);
    }

    @Override
    public CompositeComponentProperties resolveBy(Resolver resolver) {
        return forEachComponent(c -> c.resolveBy(resolver));
    }

    @Override
    public List<Property> getProperties() {
        return flatMapEach(properties, ComponentProperties::getProperties);
    }

    @Override
    public Map<String, String> propertiesAsKeyValue() {
        return asKeyValue(getProperties());
    }

    @Override
    public Optional<Property> getPropertyWithKey(String key) {
        return firstFirstResult(properties, r -> r.getPropertyWithKey(key));
    }

    @Override
    public <T> List<T> save(PropertySerializer<T> serializer) {
        return forEach(properties, r -> r.save(serializer));
    }

    private CompositeComponentProperties forEachComponent(UnaryOperator<ComponentProperties> func) {
        return composite(forEach(properties, func));
    }
}