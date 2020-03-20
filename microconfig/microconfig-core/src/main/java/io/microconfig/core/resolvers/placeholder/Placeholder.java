package io.microconfig.core.resolvers.placeholder;

import io.microconfig.core.properties.ComponentWithEnv;
import io.microconfig.core.properties.Property;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import static lombok.AccessLevel.PACKAGE;

@Getter
@RequiredArgsConstructor(access = PACKAGE)
class Placeholder {
    private static final String SELF_REFERENCE = "this";

    private final String configType;
    @With
    private final String component;
    private final String environment;
    private final String key;
    private final String defaultValue;

    public String resolveUsing(PlaceholderResolveStrategy strategy) {
        return strategy.resolve(component, key, environment, configType)
                .map(Property::getValue)
                .orElseThrow(() -> new IllegalStateException("Cant resolve '" + this + "'"));
    }

    public ComponentWithEnv getReferencedComponent() {
        return new ComponentWithEnv(component, environment);
    }

    public boolean isSelfReferenced() {
        return component.equals(SELF_REFERENCE);
    }

    public boolean referencedTo(ComponentWithEnv c) {
        //todo old impl uses c.getComponentType
        return component.equals(c.getComponent()) && environment.equals(c.getEnvironment());
    }

    @Override
    public String toString() {
        return "${" +
                configType + "::" +
                component +
                "[" + environment + "]" +
                "@" +
                key +
                (defaultValue == null ? "" : ":" + defaultValue) +
                "}";
    }
}