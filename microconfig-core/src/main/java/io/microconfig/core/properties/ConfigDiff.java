package io.microconfig.core.properties;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import static io.microconfig.core.properties.io.selector.ConfigIoFactory.configIo;
import static io.microconfig.utils.FileUtils.delete;
import static io.microconfig.utils.Logger.error;
import static io.microconfig.utils.Logger.warn;
import static java.util.Collections.emptyMap;

public class ConfigDiff {
    private static final String DIFF_PREFIX = "diff-";

    public void storeDiffFor(File current, Collection<Property> newProperties) {
        File diffFile = diffFile(current);

        Map<String, String> oldProperties = readOldConfig(current);
        Map<String, String> diff = compare(oldProperties, newProperties);

        if (!diff.isEmpty()) {
            warn("Stored " + diff.size() + " property changes to " + diffFile.getParentFile().getName() + "/" + diffFile.getName());
            configIo().writeTo(diffFile).write(diff);
        }
    }

    private File diffFile(File current) {
        File diffFile = new File(current.getParent(), DIFF_PREFIX + current.getName());
        delete(diffFile);
        return diffFile;
    }

    private Map<String, String> readOldConfig(File current) {
        try {
            return configIo().readFrom(current).propertiesAsMap();
        } catch (RuntimeException e) {
            error("Can't read previous config '" + current + "' for comparison: " + e.getMessage());
            return emptyMap();
        }
    }

    private Map<String, String> compare(Map<String, String> old, Collection<Property> current) {
        if (old.isEmpty()) return emptyMap();

        Map<String, String> result = new TreeMap<>();

        for (Property p : current) {
            if (p.isTemp()) continue;

            String oldValue = old.remove(p.getKey());
            if (oldValue == null) {
                markAdded(p.getKey(), p.getValue(), result);
            } else if (!linesEquals(p.getValue(), oldValue)) {
                markChanged(p.getKey(), oldValue, p.getValue(), result);
            }
        }

        old.forEach((k, oldValue) -> markRemoved(k, oldValue, result));
        return result;
    }

    private boolean linesEquals(String current, String old) {
        return current.trim()
                .equals(old.trim());
    }

    private void markAdded(String key, String value, Map<String, String> result) {
        result.put("+" + key, value);
    }

    private void markRemoved(String key, String value, Map<String, String> result) {
        result.put("-" + key, value);
    }

    private void markChanged(String key, String oldValue, String currentValue, Map<String, String> result) {
        result.put(key, oldValue + " -> " + currentValue);
    }
}