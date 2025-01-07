package dev.anhcraft.timedmmoitems;

import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Preconditions;
import dev.anhcraft.config.bukkit.BukkitConfigDeserializer;
import dev.anhcraft.config.bukkit.BukkitConfigProvider;
import dev.anhcraft.config.bukkit.BukkitConfigSerializer;
import dev.anhcraft.config.bukkit.struct.YamlConfigSection;
import dev.anhcraft.config.schema.SchemaScanner;
import dev.anhcraft.jvmkit.utils.FileUtil;
import dev.anhcraft.jvmkit.utils.IOUtil;
import dev.anhcraft.timedmmoitems.cmd.MainCommand;
import dev.anhcraft.timedmmoitems.config.Config;
import dev.anhcraft.timedmmoitems.stats.ExpiryDate;
import dev.anhcraft.timedmmoitems.stats.ExpiryPeriod;
import dev.anhcraft.timedmmoitems.task.CheckTask;
import dev.anhcraft.timedmmoitems.util.ConfigHelper;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class TimedMMOItems extends JavaPlugin {
    public static final ExpiryPeriod EXPIRY_PERIOD = new ExpiryPeriod();
    public static final ExpiryDate EXPIRY_DATE = new ExpiryDate();
    public static TimedMMOItems plugin;
    public Config config;

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
        config = ConfigHelper.load(Config.class, requestConfig("config.yml"));
    }

    public YamlConfiguration requestConfig(String path) {
        File f = new File(getDataFolder(), path);
        Preconditions.checkArgument(f.getParentFile().exists());

        if (!f.exists()) {
            try {
                FileUtil.write(f, IOUtil.readResource(TimedMMOItems.class, "/config/" + path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(f);
    }
}
