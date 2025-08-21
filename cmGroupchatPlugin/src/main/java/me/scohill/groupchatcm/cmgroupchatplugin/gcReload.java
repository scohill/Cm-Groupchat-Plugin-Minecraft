package me.scohill.groupchatcm.cmgroupchatplugin;

import me.scohill.groupchatcm.cmgroupchatplugin.CmGroupchatPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gcReload implements CommandExecutor {
    CmGroupchatPlugin gc = CmGroupchatPlugin.getPlugin(CmGroupchatPlugin.class);
    String smain=gc.smain;
    String emain=gc.emain;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("gcadmin.reload"))) {
            sender.sendMessage(ChatColor.valueOf(emain) + "You don't have the permission gcadmin.reload");
            return false;
        }
            gc.reloadConfig();
             sender.sendMessage(ChatColor.valueOf(smain) + "CmGroupchatPlugin reloaded successfuly!");
           Bukkit.getServer().getPluginManager().disablePlugin(gc);
           Bukkit.getServer().getPluginManager().enablePlugin(gc);
            return true;
        }
}
