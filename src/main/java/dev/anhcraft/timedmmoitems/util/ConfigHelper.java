package dev.anhcraft.timedmmoitems.util;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.NamingPolicy;
import dev.anhcraft.config.SchemalessDictionary;
import java.io.BufferedReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("unchecked")
public class ConfigHelper {
  public static final ConfigFactory FACTORY =
      ConfigFactory.create().useNamingPolicy(NamingPolicy.KEBAB_CASE).build();
  public static final Yaml YAML = new Yaml();

  @NotNull public static <T> T load(Class<T> clazz, Reader reader) {
    Yaml yaml = new Yaml();
    Map<String, Object> data = yaml.load(reader);
    Dictionary dict = (Dictionary) normalize(data);
    return (T) Objects.requireNonNull(FACTORY.getDenormalizer().denormalize(dict, clazz));
  }

  @NotNull public static <T> T load(Class<T> clazz, Path path) {
    try {
      try (BufferedReader reader = Files.newBufferedReader(path)) {
        return load(clazz, reader);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Object normalize(Object val) {
    if (val instanceof Map) {
      Dictionary normalized = new SchemalessDictionary();

      for (Map.Entry<?, ?> entry : ((Map<?, ?>) val).entrySet()) {
        normalized.put(entry.getKey().toString(), normalize(entry.getValue()));
      }

      return normalized;

    } else if (val instanceof List) {
      List<?> list = (List<?>) val;
      Object[] array = new Object[list.size()];
      for (int i = 0; i < list.size(); i++) {
        array[i] = normalize(list.get(i));
      }
      return array;
    }

    return val;
  }
}
