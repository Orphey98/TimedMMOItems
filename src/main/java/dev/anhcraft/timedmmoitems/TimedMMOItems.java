package dev.anhcraft.timedmmoitems;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class TimedMMOItems extends JavaPlugin implements Listener {
    private static final long DAY = 60 * 60 * 24;
    private static final long HOUR = 60 * 60;
    private static final long MINUTE = 60;
    public static final ExpiryPeriod EXPIRY_PERIOD = new ExpiryPeriod();
    public static final ExpiryDate EXPIRY_DATE = new ExpiryDate();
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        MMOItems.plugin.getStats().register(EXPIRY_PERIOD);
        MMOItems.plugin.getStats().register(EXPIRY_DATE);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(InventoryClickEvent event) {
        if(event.getClickedInventory() != null &&
                event.getResult() != Event.Result.DENY &&
                event.getCurrentItem() != null && !event.getCurrentItem().getType().isAir()) {
            ItemStack item = applyExpiryDate(event.getCurrentItem());
            if(item != null) {
                event.setCurrentItem(item);
            }
        }
    }

    public static boolean isMMOItem(ItemStack vanilla) {
        return io.lumine.mythic.lib.api.item.NBTItem.get(vanilla).hasType();
    }

    public static ItemStack applyExpiryDate(ItemStack itemStack) {
        if(isMMOItem(itemStack)) {
            LiveMMOItem mmo = new LiveMMOItem(itemStack);
            if(mmo.hasData(EXPIRY_PERIOD) && !mmo.hasData(EXPIRY_DATE)) {
                mmo.setData(EXPIRY_DATE, new DoubleData(System.currentTimeMillis() + ((DoubleData) mmo.getData(EXPIRY_PERIOD)).getValue() * 1000));
                return mmo.newBuilder().build();
            }
        }
        return null;
    }

    public String formatDuration(long seconds) {
        long days = seconds / DAY; seconds = Math.max(0, seconds - days * DAY);
        long hours = seconds / HOUR; seconds = Math.max(0, seconds - hours * HOUR);
        long minutes = seconds / MINUTE; seconds = Math.max(0, seconds - minutes * MINUTE);
        List<String> args = new ArrayList<>();
        if(days > 0) args.add(String.format("%d ngày", days));
        if(hours > 0) args.add(String.format("%d giờ", hours));
        if(minutes > 0) args.add(String.format("%d phút", minutes));
        if(seconds > 0) args.add(String.format("%d giây", seconds));
        return String.join(" ", args);
    }

    public String formatDate(long time) {
        return DATE_FORMAT.format(new Date(time));
    }
}
