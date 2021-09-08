package dev.anhcraft.timedmmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
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
            TimedMMOItems plugin = TimedMMOItems.getPlugin(TimedMMOItems.class);
            String date = plugin.formatDate((long) val);
            String format = Objects.requireNonNull(plugin.getConfig().getString("expiry-date-format"));
            builder.getLore().insert(this.getPath(), format.replace("%value%", date));
            builder.addItemTag(this.getAppliedNBT(statData));
        }
    }

    @Override
    public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean b) {
        double v = nbtItem.getDouble(getNBTPath());
        if(v > 0 && v < System.currentTimeMillis()) {
            rpgPlayer.getPlayer().sendActionBar('&', Objects.requireNonNull(TimedMMOItems.getPlugin(TimedMMOItems.class).getConfig().getString("item-expired")));
            rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
            return false;
        }
        return true;
    }
}
