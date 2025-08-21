package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gcHelp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CmGroupchatPlugin gc = CmGroupchatPlugin.getPlugin(CmGroupchatPlugin.class);
        String splayer=gc.splayer;
        if (sender instanceof Player) {
            sender.sendMessage( "/gccreate: Create a groupchat (/gccreate MyGroupchat {0 for private,1 for public}\n/g: Chat in a groupchat\n/gcinvite: Invite players to your groupchat\n" +
                    "/gselect: Select your main groupchat\n/gclist: List all groupchats\n" +
                    "/gcmylist: List all groupchats you are a part of\n/gctypeset: change the type of your groupchat (Private or Public)\n" +
                    "/gcleave: Leave groupchat\n/gcsetowner: Change the owner\ngckick: Kick a player from the groupchat\n/gctoggle: Redirects msgs directly to groupchat(no need for /g)" +
                    "\n /gcdelete: Delete a groupchat" +
                    "\n/gcinvite gcaccept {name}: Accept a groupchat invite (invite has clickable text)\n " +
                    "/gcinvite gcdecline: Declipe a groupchat invite\n/gchelp: Shows avaiable commands\nDeveloped by "+ChatColor.valueOf(splayer)+"Scohill" );
        }

    return true;
    }
}
