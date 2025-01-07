package dev.anhcraft.timedmmoitems.config;

import dev.anhcraft.config.annotations.*;
import dev.anhcraft.config.annotations.Optional;

import java.text.SimpleDateFormat;
import java.util.*;

@Configurable(keyNamingStyle = Configurable.NamingStyle.TRAIN_CASE)
public class Config {
    private static final long DAY = 60 * 60 * 24;
    private static final long HOUR = 60 * 60;
    private static final long MINUTE = 60;

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
    private String dateFormat = "dd/MM/yyyy";

    @Exclude
    public SimpleDateFormat simpleDateFormat;

    @Description("Inventory update is usually handled automatically. Enable this if you experience glitch!")
    public boolean forceUpdateInventory = false;

    @Description("Expired item change")
    @Optional
    public Map<String, List<ItemConfig>> expiredItemReplace = new HashMap<>();

    @PostHandler
    private void init() {
        simpleDateFormat = new SimpleDateFormat(dateFormat);
    }

    public String formatDate(Date date) {
        return simpleDateFormat.format(date);
    }

    public String formatDuration(long seconds) {
        long days = seconds / DAY; seconds = Math.max(0, seconds - days * DAY);
        long hours = seconds / HOUR; seconds = Math.max(0, seconds - hours * HOUR);
        long minutes = seconds / MINUTE; seconds = Math.max(0, seconds - minutes * MINUTE);

        List<String> args = new ArrayList<>();
        if(days == 1) args.add(unitFormat.day.replace("%d", Long.toString(days)));
        else if(days > 0) args.add(unitFormat.days.replace("%d", Long.toString(days)));

        if(hours == 1) args.add(unitFormat.hour.replace("%d", Long.toString(hours)));
        else if(hours > 0) args.add(unitFormat.hours.replace("%d", Long.toString(hours)));

        if(minutes == 1) args.add(unitFormat.minute.replace("%d", Long.toString(minutes)));
        else if(minutes > 0) args.add(unitFormat.minutes.replace("%d", Long.toString(minutes)));

        if(seconds == 1) args.add(unitFormat.second.replace("%d", Long.toString(seconds)));
        else if(seconds > 0) args.add(unitFormat.seconds.replace("%d", Long.toString(seconds)));

        return String.join(" ", args);
    }
}
