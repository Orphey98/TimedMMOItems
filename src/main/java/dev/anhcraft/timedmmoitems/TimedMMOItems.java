package dev.anhcraft.timedmmoitems;

import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Preconditions;
import dev.anhcraft.jvmkit.utils.FileUtil;
import dev.anhcraft.jvmkit.utils.IOUtil;
import dev.anhcraft.timedmmoitems.cmd.MainCommand;
import dev.anhcraft.timedmmoitems.config.Config;
import dev.anhcraft.timedmmoitems.config.ItemConfig;
import dev.anhcraft.timedmmoitems.stats.ExpiryDate;
import dev.anhcraft.timedmmoitems.stats.ExpiryPeriod;
import dev.anhcraft.timedmmoitems.task.CheckTask;
import dev.anhcraft.timedmmoitems.util.ConfigHelper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class TimedMMOItems extends JavaPlugin {
  public static final ExpiryPeriod EXPIRY_PERIOD = new ExpiryPeriod();
  public static final ExpiryDate EXPIRY_DATE = new ExpiryDate();
  public static TimedMMOItems plugin;
  public Config config;

  public void debug(@NotNull String format, @NotNull Object... args) {
    debug(1, format, args);
  }

  public void debug(int level, @NotNull String format, @NotNull Object... args) {
    if (config != null && config.debugLevel >= level) {
      getServer()
          .getConsoleSender()
          .sendMessage(ChatColor.GOLD + "[TimedMMOItems#DEBUG] " + String.format(format, args));
    }
  }

  @Override
  public void onEnable() {
    plugin = this;

    initConfig();

    MMOItems.plugin.getStats().register(EXPIRY_PERIOD);
    MMOItems.plugin.getStats().register(EXPIRY_DATE);

    new CheckTask(this).runTaskTimer(this, 0, 20L * config.itemCheckInterval);

    getServer()
        .dispatchCommand(getServer().getConsoleSender(), "mi reload"); // force reload MMOItems

    PaperCommandManager pcm = new PaperCommandManager(this);
    pcm.enableUnstableAPI("help");
    pcm.registerCommand(new MainCommand());
  }

  public void initConfig() {
    getDataFolder().mkdir();

    this.config = ConfigHelper.load(Config.class, requestConfig("config.yml").toPath());

    for (Map.Entry<String, List<ItemConfig>> entry : config.expiredItemReplace.entrySet()) {
      debug(
          2,
          "Replace MMO item %s => %s",
          entry.getKey(),
          entry.getValue().stream().map(ItemConfig::toString).collect(Collectors.joining(", ")));
    }
  }

  public File requestConfig(String path) {
    File f = new File(getDataFolder(), path);
    Preconditions.checkArgument(f.getParentFile().exists());

    if (!f.exists()) {
      try {
        FileUtil.write(f, IOUtil.readResource(TimedMMOItems.class, "/config/" + path));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return f;
  }
}
