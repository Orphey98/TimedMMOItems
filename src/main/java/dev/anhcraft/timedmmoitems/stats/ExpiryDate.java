package dev.anhcraft.timedmmoitems.stats;

import dev.anhcraft.timedmmoitems.TimedMMOItems;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.Date;
import java.util.Objects;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class ExpiryDate extends DoubleStat implements ItemRestriction {
  public ExpiryDate() {
    super(
        "EXPIRY_DATE",
        Material.PAINTING,
        "Expiry Date",
        new String[] {"Defines the expiry date", "The value is in milliseconds since Unix Epoch"},
        new String[] {"!block", "all"});
  }

  @Override
  public void whenApplied(@NotNull ItemStackBuilder item, @NotNull DoubleData data) {
    double val = data.getValue();
    if (val > 0) {
      String date = TimedMMOItems.plugin.config.formatDate(new Date((long) val));
      String format = Objects.requireNonNull(TimedMMOItems.plugin.config.expiryDateFormat);
      item.getLore().insert(this.getPath(), format.replace("%value%", date));
      item.addItemTag(this.getAppliedNBT(data));
    }
  }

  @Override
  public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean b) {
    double v = nbtItem.getDouble(getNBTPath());
    if (v > 0 && v < System.currentTimeMillis()) {
      if (rpgPlayer.getPlayer().hasPermission("timeditems.bypass")) {
        TimedMMOItems.plugin.debug(
            2, "%s skips expired-item block via permission", rpgPlayer.getPlayer().getName());
        return true;
      }
      String t = TimedMMOItems.plugin.config.itemExpiredPlacement;
      String m =
          ChatColor.translateAlternateColorCodes('&', TimedMMOItems.plugin.config.itemExpired);
      if (t.equalsIgnoreCase("action-bar")) {
        rpgPlayer
            .getPlayer()
            .spigot()
            .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(m));
      } else if (t.equalsIgnoreCase("chat")) {
        rpgPlayer.getPlayer().sendMessage(m);
      }
      rpgPlayer
          .getPlayer()
          .playSound(rpgPlayer.getPlayer().getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
      return false;
    }
    return true;
  }
}
