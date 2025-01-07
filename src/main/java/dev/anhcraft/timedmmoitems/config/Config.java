package dev.anhcraft.timedmmoitems.config;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Optional;
import dev.anhcraft.config.annotations.Validation;

import java.util.*;

@Configurable(keyNamingStyle = Configurable.NamingStyle.TRAIN_CASE)
public class Config {
    @Description("Expiry period stat format")
    @Validation(notNull = true, silent = true)
    public String expiryPeriodFormat = "&e[Expiry Time: %value%]";

    @Description("Expiry date stat format")
    @Validation(notNull = true, silent = true)
    public String expiryDateFormat = "&e[Expiry Date: %value%]";

    @Description("Item expired message")
    @Validation(notNull = true, silent = true)
    public String itemExpired = "&c&l[!] Item is expired";

    @Description("Item expired message placement")
    @Validation(notNull = true, silent = true)
    public String itemExpiredPlacement = "action-bar";

    @Description("Should remove expired item")
    public boolean removeExpiredItem = true;

    @Description("Expired item removed message")
    @Validation(notNull = true, silent = true)
    public String expiredItemRemoved = "&cRemoved %amount% expired items";

    @Description("Replaced item dropped on the ground message")
    @Validation(notNull = true, silent = true)
    public String replacedItemDropped = "&c[!] Some items were dropped on the ground";

    @Description("Unit format")
    @Validation(notNull = true, silent = true)
    public UnitConfig unitFormat = new UnitConfig();

    @Description("Item check interval in seconds")
    public int itemCheckInterval = 5;

    @Description("Replace expiry period with expiry date instead of keeping both stats")
    public boolean replaceExpiryPeriod = true;

    @Description("Date format")
    @Validation(notNull = true, silent = true)
    public String dateFormat = "dd/MM/yyyy";

    @Description("Inventory update is usually handled automatically. Enable this if you experience glitch!")
    public boolean forceUpdateInventory = false;

    @Description("Expired item change")
    @Optional
    public Map<String, List<ItemConfig>> expiredItemReplace = new HashMap<>();
}
