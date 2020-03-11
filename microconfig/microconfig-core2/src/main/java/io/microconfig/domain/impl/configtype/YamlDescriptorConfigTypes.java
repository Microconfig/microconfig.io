package io.microconfig.domain.impl.configtype;

import io.microconfig.domain.ConfigType;
import io.microconfig.domain.ConfigTypes;
import io.microconfig.service.io.Io;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.microconfig.domain.impl.configtype.ConfigTypeImpl.byName;
import static io.microconfig.domain.impl.configtype.ConfigTypeImpl.byNameAndExtensions;
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.StreamUtils.map;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

@RequiredArgsConstructor
public class YamlDescriptorConfigTypes implements ConfigTypes {
    private static final String DESCRIPTOR = "microconfig.yaml";

    private final Path rootDir;
    private final Io io;

    @Override
    public List<ConfigType> getTypes() {
        File descriptor = descriptorFile();
        if (!descriptor.exists()) return emptyList();

        List<ConfigType> types = parse(descriptor);
        if (!types.isEmpty()) {
            announce("Using settings from " + DESCRIPTOR);
        }
        return types;
    }

    private File descriptorFile() {
        return new File(rootDir.toFile(), DESCRIPTOR);
    }

    @SuppressWarnings("unchecked")
    private List<ConfigType> parse(File file) {
        try {
            Map<String, Object> types = new Yaml().load(io.readFully(file));
            List<Object> configTypes = (List<Object>) types.getOrDefault("configTypes", emptyList());
            return map(configTypes, this::parseType);
        } catch (RuntimeException e) {
            throw new RuntimeException("Can't parse Microconfig descriptor '" + file + "'", e);
        }
    }

    @SuppressWarnings("unchecked")
    private ConfigType parseType(Object configTypeObject) {
        if (configTypeObject instanceof String) {
            return byName(configTypeObject.toString());
        }

        Map<String, Object> configType = (Map<String, Object>) configTypeObject;
        String type = configType.keySet().iterator().next();
        Map<String, Object> attributes = (Map<String, Object>) configType.get(type);
        Set<String> sourceExtensions = attributes.containsKey("sourceExtensions") ? new LinkedHashSet<>((List<String>) attributes.get("sourceExtensions")) : singleton(type);
        String resultFileName = (String) attributes.getOrDefault("resultFileName", type);

        return byNameAndExtensions(type, sourceExtensions, resultFileName);
    }
}