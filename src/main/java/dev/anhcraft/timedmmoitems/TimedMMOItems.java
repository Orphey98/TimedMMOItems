package dev.anhcraft.timedmmoitems;

import co.aikar.commands.PaperCommandManager;
import dev.anhcraft.config.bukkit.BukkitConfigDeserializer;
import dev.anhcraft.config.bukkit.BukkitConfigProvider;
import dev.anhcraft.config.bukkit.BukkitConfigSerializer;
import dev.anhcraft.config.bukkit.struct.YamlConfigSection;
import dev.anhcraft.config.schema.SchemaScanner;
import dev.anhcraft.timedmmoitems.cmd.MainCommand;
import dev.anhcraft.timedmmoitems.config.Config;
import dev.anhcraft.timedmmoitems.stats.ExpiryDate;
import dev.anhcraft.timedmmoitems.stats.ExpiryPeriod;
import dev.anhcraft.timedmmoitems.task.CheckTask;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class TimedMMOItems extends JavaPlugin {
    private static final long DAY = 60 * 60 * 24;
    private static final long HOUR = 60 * 60;
    private static final long MINUTE = 60;
    public static final ExpiryPeriod EXPIRY_PERIOD = new ExpiryPeriod();
    public static final ExpiryDate EXPIRY_DATE = new ExpiryDate();
    public static TimedMMOItems plugin;
    public Config config;
    public SimpleDateFormat dateFormat;

    @Override
    public void onEnable() {
        plugin = this;

        initConfig();

        MMOItems.plugin.getStats().register(EXPIRY_PERIOD);
        MMOItems.plugin.getStats().register(EXPIRY_DATE);

        new CheckTask(this).runTaskTimer(this, 0, 20L * config.itemCheckInterval);

        getServer().dispatchCommand(getServer().getConsoleSender(), "mi reload"); // force reload MMOItems

        PaperCommandManager pcm = new PaperCommandManager(this);
        pcm.enableUnstableAPI("help");
        pcm.registerCommand(new MainCommand());
    }

    public void initConfig() {
        getDataFolder().mkdir();
        File f = new File(getDataFolder(), "config.yml");
        if (f.exists()) {
            try {
                config = new BukkitConfigDeserializer(BukkitConfigProvider.YAML).transformConfig(SchemaScanner.scanConfig(Config.class), new YamlConfigSection(getConfig()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            config = new Config();
            try {
                YamlConfiguration c = new YamlConfiguration();
                new BukkitConfigSerializer(BukkitConfigProvider.YAML).transformConfig(SchemaScanner.scanConfig(Config.class), new YamlConfigSection(c), config);
                c.save(f);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        dateFormat = new SimpleDateFormat(config.dateFormat);
    }

    public String formatDuration(long seconds) {
        long days = seconds / DAY; seconds = Math.max(0, seconds - days * DAY);
        long hours = seconds / HOUR; seconds = Math.max(0, seconds - hours * HOUR);
        long minutes = seconds / MINUTE; seconds = Math.max(0, seconds - minutes * MINUTE);

        List<String> args = new ArrayList<>();
        if(days == 1) args.add(config.unitFormat.day.replace("%d", Long.toString(days)));
        else if(days > 0) args.add(config.unitFormat.days.replace("%d", Long.toString(days)));

        if(hours == 1) args.add(config.unitFormat.hour.replace("%d", Long.toString(days)));
        else if(hours > 0) args.add(config.unitFormat.hours.replace("%d", Long.toString(days)));

        if(minutes == 1) args.add(config.unitFormat.minute.replace("%d", Long.toString(days)));
        else if(minutes > 0) args.add(config.unitFormat.minutes.replace("%d", Long.toString(days)));

        if(seconds == 1) args.add(config.unitFormat.second.replace("%d", Long.toString(days)));
        else if(seconds > 0) args.add(config.unitFormat.seconds.replace("%d", Long.toString(days)));

        return String.join(" ", args);
    }

    public String formatDate(long time) {
        return dateFormat.format(new Date(time));
    }
}
