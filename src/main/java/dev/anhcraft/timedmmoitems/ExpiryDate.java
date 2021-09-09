package dev.anhcraft.timedmmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ExpiryDate extends DoubleStat implements ItemRestriction {
    public ExpiryDate() {
        super(
                "EXPIRY_DATE", Material.PAINTING, "Expiry Date",
                new String[]{"Defines the expiry date", "The value is in second(s) since Unix Epoch"},
                new String[]{"!block", "all"}
        );
    }

    @Override
    public void whenApplied(@NotNull ItemStackBuilder builder, @NotNull StatData statData) {
        double val = ((DoubleData) statData).getValue();
        if (val > 0) {
            String date = TimedMMOItems.plugin.formatDate((long) val);
            String format = Objects.requireNonNull(TimedMMOItems.plugin.getConfig().getString("expiry-date-format"));
            builder.getLore().insert(this.getPath(), format.replace("%value%", date));
            builder.addItemTag(this.getAppliedNBT(statData));
        }
    }

    @Override
    public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean b) {
        double v = nbtItem.getDouble(getNBTPath());
        if(v > 0 && v < System.currentTimeMillis()) {
            if (rpgPlayer.getPlayer().hasPermission("timeditems.bypass")) {
                return true;
            }
            String t = Objects.requireNonNull(TimedMMOItems.plugin.getConfig().getString("item-expired-placement", "action-bar"));
            String m = Objects.requireNonNull(TimedMMOItems.plugin.getConfig().getString("item-expired"));
            if(t.equalsIgnoreCase("action-bar")) {
                rpgPlayer.getPlayer().sendActionBar('&', m);
            } else if(t.equalsIgnoreCase("chat")) {
                rpgPlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', m));
            }
            rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
            return false;
        }
        return true;
    }
}
