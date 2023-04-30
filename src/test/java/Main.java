import dev.anhcraft.configdoc.ConfigDocGenerator;
import dev.anhcraft.timedmmoitems.config.Config;
import dev.anhcraft.timedmmoitems.config.UnitConfig;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        new ConfigDocGenerator().withSchemaOf(Config.class).withSchemaOf(UnitConfig.class).generate(new File("docs"));
    }
}
