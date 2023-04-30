package dev.anhcraft.timedmmoitems.task;

import dev.anhcraft.timedmmoitems.TimedMMOItems;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;

import static dev.anhcraft.timedmmoitems.TimedMMOItems.EXPIRY_DATE;
import static dev.anhcraft.timedmmoitems.TimedMMOItems.EXPIRY_PERIOD;

public class CheckTask extends BukkitRunnable {
    private final TimedMMOItems plugin;

    public CheckTask(TimedMMOItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(Player player : plugin.getServer().getOnlinePlayers()){
            if (player.hasPermission("timeditems.bypass")) {
                continue;
            }

            boolean needUpdate = false;
            int rmvCounter = 0;
            List<ItemStack> newItems = new LinkedList<>();

            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && !item.getType().isAir() && NBTItem.get(item).hasType()) {
                    LiveMMOItem mmo = new LiveMMOItem(item);
                    if(mmo.hasData(EXPIRY_PERIOD) && !mmo.hasData(EXPIRY_DATE)) {
                        mmo.setData(EXPIRY_DATE, new DoubleData(System.currentTimeMillis() + ((DoubleData) mmo.getData(EXPIRY_PERIOD)).getValue() * 1000));
                        if (plugin.config.replaceExpiryPeriod) {
                            mmo.removeData(EXPIRY_PERIOD);
                        }
                        newItems.add(mmo.newBuilder().build());
                        needUpdate = true;
                        continue;
                    }
                    if (plugin.config.removeExpiredItem && mmo.hasData(EXPIRY_DATE) && ((DoubleData) mmo.getData(EXPIRY_DATE)).getValue() < System.currentTimeMillis()) {
                        rmvCounter += item.getAmount();
                        needUpdate = true;
                        continue;
                    }
                }
                newItems.add(item);
            }

            if (!needUpdate) return;
            player.getInventory().setContents(newItems.toArray(new ItemStack[0]));
            player.updateInventory();

            if (rmvCounter > 0) {
                String msg = plugin.config.expiredItemRemoved.replace("%amount%", Integer.toString(rmvCounter));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            }
        }
    }
}
