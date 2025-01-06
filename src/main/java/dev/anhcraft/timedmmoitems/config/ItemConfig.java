package dev.anhcraft.timedmmoitems.config;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Validation;

@Configurable(keyNamingStyle = Configurable.NamingStyle.TRAIN_CASE)
public class ItemConfig {
    @Validation(notNull = true, silent = true)
    public String type = "bukkit";
    @Validation(notNull = true, silent = true)
    public String id = "air";
    @Validation(notEmpty = true, silent = true)
    public int amount = 1;

    public ItemConfig() {
    }

    public ItemConfig(String type, String id, int amount) {
        this.type = type;
        this.id = id;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ItemConfig{type='" + type + "', material='" + id + "', amount=" + amount + "}";
    }
}
