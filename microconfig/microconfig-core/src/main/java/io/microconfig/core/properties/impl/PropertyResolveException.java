package io.microconfig.core.properties.impl;

import io.microconfig.core.properties.ComponentWithEnv;
import io.microconfig.core.properties.Property;

import static io.microconfig.utils.FileUtils.LINES_SEPARATOR;
import static java.lang.String.format;

public class PropertyResolveException extends RuntimeException {
    public PropertyResolveException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyResolveException(String message) {
        super(message);
    }

    public PropertyResolveException(String unresolvedPlaceholder, Property sourceOfPlaceholder, ComponentWithEnv root,
                                    Throwable cause) {
        super(resolveExceptionMessage(unresolvedPlaceholder, sourceOfPlaceholder, root), cause);
    }

    public PropertyResolveException(String unresolvedPlaceholder, Property sourceOfPlaceholder, ComponentWithEnv root) {
        super(resolveExceptionMessage(unresolvedPlaceholder, sourceOfPlaceholder, root));
    }

    public PropertyResolveException(String expression, ComponentWithEnv root, Throwable cause) {
        super(format("Can't evaluate EL '%s'. Root component -> %s[%s]. " +
                        "Example of correct EL: #{'${component1@ip}' + ':' + ${ports@port1}}",
                expression, root.getComponent(), root.getEnvironment()), cause
        );
    }

    private static String resolveExceptionMessage(String unresolvedPlaceholder, Property sourceOfPlaceholder, ComponentWithEnv root) {
        return format("Can't resolve placeholder '%s' defined in " + LINES_SEPARATOR + "'%s', that property is a transitive dependency of '%s'.",
                unresolvedPlaceholder,
                sourceOfPlaceholder.getSource().sourceInfo(),
                root);
    }

    public static PropertyResolveException badPlaceholderFormat(String value) {
        return new PropertyResolveException("Can't parse placeholder '" + value + "'"
                + ". Supported format: ${componentName[optionalEnvName]@propertyPlaceholder:optionalDefaultValue}");
    }

    public static PropertyResolveException badSpellFormat(String value) {
        throw new PropertyResolveException("'" + value + "' is not an expression. Supported format is: #{expression}");
    }
}