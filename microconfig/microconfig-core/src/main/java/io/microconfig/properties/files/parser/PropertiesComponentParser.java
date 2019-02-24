package io.microconfig.properties.files.parser;

import io.microconfig.environments.Component;
import io.microconfig.properties.Property;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.microconfig.properties.Property.isComment;
import static io.microconfig.properties.Property.isTempProperty;
import static io.microconfig.utils.IoUtils.readAllLines;
import static java.util.Collections.emptyList;

@RequiredArgsConstructor
public class PropertiesComponentParser implements ComponentParser {
    private static final String IGNORE = "#@Ignore";
    private final String rootComponent;

    public PropertiesComponentParser(File rootComponent) {
        this.rootComponent = rootComponent.getAbsolutePath();
    }

    @Override
    public ParsedComponent parse(File path, Component component, String env) {
        List<Include> includes = new ArrayList<>();
        List<Property> properties = new ArrayList<>();
        boolean ignore = false;

        List<String> lines = readAllLines(path);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            if (isIgnore(line)) {
                ignore = true;
                continue;
            }

            if (Include.isInclude(line)) {
                includes.add(Include.parse(line, env));
                continue;
            }

            if (isTempProperty(line) || !isComment(line)) {
                properties.add(Property.parse(line, env, new Property.Source(component, getPath(path), i)));
            }
        }

        return new ParsedComponent(component.getName(), includes, ignore ? emptyList() : properties);
    }

    private boolean isIgnore(String line) {
        return line.startsWith(IGNORE);
    }

    private String getPath(File path) {
        String absolutePath = path.getAbsolutePath();
        return absolutePath.substring(absolutePath.indexOf(rootComponent) + rootComponent.length());
    }
}