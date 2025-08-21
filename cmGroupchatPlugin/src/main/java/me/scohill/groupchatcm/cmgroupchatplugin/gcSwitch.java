package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class gcSwitch implements TabExecutor {
    CmGroupchatPlugin gc = CmGroupchatPlugin.getPlugin(CmGroupchatPlugin.class);
    Connection con = gc.con;
    Statement stmt=null;
    ResultSet rs=null;
    String pcolor=gc.pcolor;
    String gccolor=gc.gccolor;
    String gctext=gc.gctext;
    String smain=gc.smain;
    String splayer=gc.splayer;
    String emain=gc.emain;
    String eplayer=gc.eplayer;
    PreparedStatement preparedStmt=null;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (con != null) {
            if (!(sender.hasPermission("gc.select"))) {
                sender.sendMessage(ChatColor.valueOf(emain) + "You don't have the permission gc.select");
                return false;
            }
            if (sender instanceof Player) {
                try {
                    String sql = "select * from gcdata where gcname=? and pname=?";
                    preparedStmt = con.prepareStatement(sql);
                    preparedStmt.setString(1, args[0]);
                    preparedStmt.setString(2, sender.getName());
                    rs= preparedStmt.executeQuery();
                    if(rs.next()) {
                        String query = "update gcdata set maingc = ? where pname = ?";
                        preparedStmt = con.prepareStatement(query);
                        preparedStmt.setString(1, args[0]);
                        preparedStmt.setString(2, sender.getName());
                        preparedStmt.execute();
                        sender.sendMessage(ChatColor.valueOf(smain)+ "You have switched to groupchat " +ChatColor.valueOf(splayer)+ args[0]);
                    }
                    else{sender.sendMessage(ChatColor.valueOf(emain)+ "You are not part of groupchat "+ChatColor.valueOf(eplayer)+ args[0]);}
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return false;
            }
        }
    return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
       List<String>groups=new ArrayList<>();
        String query = "SELECT gcname from gcdata where pname=?";


        try {
            preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, sender.getName());
            ResultSet rs = preparedStmt.executeQuery();
            while(rs.next()){
                groups.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }
}
