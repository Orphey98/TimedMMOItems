package dev.anhcraft.timedmmoitems.task;

import dev.anhcraft.timedmmoitems.TimedMMOItems;
import dev.anhcraft.timedmmoitems.config.ItemConfig;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static dev.anhcraft.timedmmoitems.TimedMMOItems.EXPIRY_DATE;
import static dev.anhcraft.timedmmoitems.TimedMMOItems.EXPIRY_PERIOD;

public class CheckTask extends BukkitRunnable {
  private final TimedMMOItems plugin;

  public CheckTask(TimedMMOItems plugin) {
    this.plugin = plugin;
  }

  @Override
  public void run() {
    for (Player player : plugin.getServer().getOnlinePlayers()) {
      if (player.hasPermission("timeditems.bypass")) {
        continue;
      }

      boolean needUpdate = false;
      int rmvCounter = 0;

      boolean needReplace = false;
      int rplCounter = 0;
      HashMap<String, Integer> rplMap = new HashMap<>();

      List<ItemStack> newItems = new LinkedList<>();

      for (ItemStack item : player.getInventory().getContents()) {
        if (item == null || item.getType().isAir() || NBTItem.get(item).getType() == null) {
          newItems.add(item);
          continue;
        }

        LiveMMOItem mmo = new LiveMMOItem(item);

        if (mmo.hasData(EXPIRY_PERIOD) && !mmo.hasData(EXPIRY_DATE)) {
            double expiryPeriod = ((DoubleData) mmo.getData(EXPIRY_PERIOD)).getValue();
          double expiryDate = System.currentTimeMillis();
          expiryDate += expiryPeriod * 1000d;
          mmo.setData(EXPIRY_DATE, new DoubleData(expiryDate));

          if (plugin.config.replaceExpiryPeriod) {
            mmo.removeData(EXPIRY_PERIOD);
            TimedMMOItems.plugin.debug(1, "%s item has EXPIRY_PERIOD(%.1f) but no EXPIRY_DATE => Create EXPIRY_DATE(%.1f), and remove EXPIRY_PERIOD", player.getName(), expiryPeriod, expiryDate);
          } else {
            TimedMMOItems.plugin.debug(1, "%s item has EXPIRY_PERIOD(%.1f) but no EXPIRY_DATE => Create EXPIRY_DATE(%.1f)", player.getName(), expiryPeriod, expiryDate);
          }

          newItems.add(mmo.newBuilder().build());
          needUpdate = true;
          continue;
        }

        if (plugin.config.removeExpiredItem && mmo.hasData(EXPIRY_DATE) && ((DoubleData) mmo.getData(EXPIRY_DATE)).getValue() < System.currentTimeMillis()) {
          rmvCounter += item.getAmount();
          needUpdate = true;
          if (plugin.config.expiredItemReplace.containsKey(mmo.getId())) {
            rplCounter += item.getAmount();
            rplMap.put(mmo.getId(), rplCounter);
            needReplace = true;
          }
        }
      }

      if (!needUpdate) return;
      player.getInventory().setContents(newItems.toArray(new ItemStack[0]));

      if (plugin.config.forceUpdateInventory) player.updateInventory();

      if (rmvCounter > 0) {
        if (needReplace) itemReplace(player, rplMap);
        String msg = plugin.config.expiredItemRemoved.replace("%amount%", Integer.toString(rmvCounter));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
      }
    }
  }

  private void itemReplace(Player player, HashMap<String, Integer> map) {
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      String itemId = entry.getKey();
      int rplCounter = entry.getValue();
      List<ItemConfig> itemConfigList = plugin.config.expiredItemReplace.get(itemId);

      for (ItemConfig itemConfig : itemConfigList) {
        try {
          ItemStack itemStack = ReplaceFactory.createItemStack(itemConfig, rplCounter);
          // Attempt to add to inventory
          HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(itemStack);
          if (!leftovers.isEmpty()) {
            // Drop leftovers on the ground
            for (ItemStack leftover : leftovers.values()) {
              player.getWorld().dropItem(player.getLocation(), leftover);
            }
            String msg = plugin.config.replacedItemDropped;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
          }
        } catch (IllegalArgumentException e) {
          plugin.getLogger().severe(e.getMessage());
        }
      }
    }
  }
}
