package dev.anhcraft.timedmmoitems.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import dev.anhcraft.timedmmoitems.TimedMMOItems;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("ti|tmi|timeditems|timedmmoitems")
public class MainCommand extends BaseCommand {

    @HelpCommand
    @CatchUnknown
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @CommandPermission("timedmmoitems.reload")
    public void reload(CommandSender sender) {
        TimedMMOItems.plugin.initConfig();
        sender.sendMessage(ChatColor.GREEN + "TimedMMOItems reloaded!");
    }
}
