package io.microconfig.core.environments.impl.repository;

import io.microconfig.core.environments.Component;
import io.microconfig.core.environments.impl.ComponentFactory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
class ComponentDefinition {
    @Getter
    private final String name;
    private final String type;

    public static ComponentDefinition withAlias(String alias, String type) {
        return new ComponentDefinition(alias, type);
    }

    public static ComponentDefinition withName(String name) {
        return withAlias(name, name);
    }

    public Component toComponent(ComponentFactory componentFactory, String environment) {
        return componentFactory.createComponent(name, type, environment);
    }
}
