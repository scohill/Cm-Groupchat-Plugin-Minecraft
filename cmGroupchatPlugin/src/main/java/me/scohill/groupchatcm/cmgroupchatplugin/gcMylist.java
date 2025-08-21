package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class gcMylist implements CommandExecutor {
    CmGroupchatPlugin gc = CmGroupchatPlugin.getPlugin(CmGroupchatPlugin.class);
    Connection con = gc.con;

    String pcolor=gc.pcolor;
    String gccolor=gc.gccolor;
    String gctext=gc.gctext;
    String smain=gc.smain;
    String splayer=gc.splayer;
    String emain=gc.emain;
    String eplayer=gc.eplayer;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (con != null) {
            if (!(sender.hasPermission("gc.mylist"))) {
                sender.sendMessage(ChatColor.valueOf(emain) +"You don't have the permission gc.mylist");
                return false;
            }
            if (sender instanceof Player) {
                PreparedStatement preparedStmt = null;
                try {
                    String query = "SELECT gcname,gcowner,type from gcdata where pname=?";

                    preparedStmt = con.prepareStatement(query);
                    preparedStmt.setString(1, sender.getName());
                    ResultSet rs = preparedStmt.executeQuery();
                    sender.sendMessage(ChatColor.valueOf(smain)+ "****My groupchats:****\n");
                    while (rs.next()) {
                        sender.sendMessage(ChatColor.valueOf(smain)+ "Name: "
                                + ChatColor.valueOf(splayer)+ rs.getString(1)
                                + ChatColor.valueOf(smain)+" Owner: "
                                + ChatColor.valueOf(splayer)+ rs.getString(2)
                                + ChatColor.valueOf(smain)+ " Type: "
                                + ChatColor.valueOf(splayer)+ rs.getString(3) + "\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return false;
    }
}
