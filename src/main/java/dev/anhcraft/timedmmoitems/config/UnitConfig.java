package dev.anhcraft.timedmmoitems.config;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Validation;

@Configurable
public class UnitConfig {
    @Validation(notNull = true, silent = true)
    public String second = "%d second";

    @Validation(notNull = true, silent = true)
    public String minute = "%d minute";

    @Validation(notNull = true, silent = true)
    public String hour = "%d hour";

    @Validation(notNull = true, silent = true)
    public String day = "%d day";

    @Validation(notNull = true, silent = true)
    public String seconds = "%d seconds";

    @Validation(notNull = true, silent = true)
    public String minutes = "%d minutes";

    @Validation(notNull = true, silent = true)
    public String hours = "%d hours";

    @Validation(notNull = true, silent = true)
    public String days = "%d days";
}
