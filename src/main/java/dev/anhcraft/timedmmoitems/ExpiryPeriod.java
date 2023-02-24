package dev.anhcraft.timedmmoitems;

import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ExpiryPeriod extends DoubleStat {
    public ExpiryPeriod() {
        super(
                "EXPIRY_PERIOD", Material.SHIELD, "Expiry Period",
                new String[]{"Defines the duration that the item remains usable", "The value is in second(s)"},
                new String[]{"!block", "all"}
        );
    }

    @Override
    public void whenApplied(@NotNull ItemStackBuilder item, @NotNull DoubleData data) {
        double val = data.getValue();
        if (val > 0) {
            String period = TimedMMOItems.plugin.formatDuration((long) val);
            String format = Objects.requireNonNull(TimedMMOItems.plugin.getConfig().getString("expiry-period-format"));
            item.getLore().insert(this.getPath(), format.replace("%value%", period));
            item.addItemTag(this.getAppliedNBT(data));
        }
    }
}
