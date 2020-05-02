package io.microconfig.templates.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import io.microconfig.core.properties.TypedProperties;
import io.microconfig.core.templates.TemplateContentPostProcessor;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class MustacheTemplateProcessor implements TemplateContentPostProcessor {
    private static final String MUSTACHE = "mustache";

    @Override
    public String process(String templateName, File source, String content, TypedProperties properties) {
        if (!source.getName().endsWith("." + MUSTACHE) && !templateName.contains(MUSTACHE)) return content;

        return compile(content)
                .execute(new StringWriter(), toYaml(properties))
                .toString();
    }

    private Map<String, Object> toYaml(TypedProperties properties) {
        String text = new YamlTreeImpl().toYaml(properties.getPropertiesAsKeyValue());
        try {
            return new Yaml().load(text);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Mustache compile(String source) {
        return new DefaultMustacheFactory()
                .compile(new StringReader(source), "");
    }
}