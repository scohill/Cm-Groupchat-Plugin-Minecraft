package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.*;

public class gcToggle implements CommandExecutor {
    CmGroupchatPlugin gc = CmGroupchatPlugin.getPlugin(CmGroupchatPlugin.class);
    Connection con = gc.con;
    Statement stmt=null;
    ResultSet rs=null;
    PreparedStatement preparedStmt=null;
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
            if(!(sender.hasPermission("gc.toggle"))){
                sender.sendMessage(ChatColor.valueOf(emain)+"You don't have the permission gc.toggle");
                return  false;
            }
            if (sender instanceof Player) {
                try {
                Player player=((Player) sender).getPlayer();
                String sql = "SELECT toggle \n" + //finding the gcname
                        "FROM gcdata\n" +
                        "WHERE pname = '" + player.getName() + "'\n" +
                        "LIMIT 1;";
                stmt = con.createStatement();
                rs = stmt.executeQuery(sql);
                if(rs.next()){
                    String toggle="true";
                    if(rs.getString(1).equals("true")){
                        sender.sendMessage(ChatColor.valueOf(smain)+"Your messages will be redirected to the main chat");
                        toggle="false";
                    }else{sender.sendMessage(ChatColor.valueOf(smain)+"Your messages will be redirected to the groupchat"); }
                    String query = "update gcdata set toggle = ? where pname = ?";
                    preparedStmt = con.prepareStatement(query);
                    preparedStmt.setString(1, toggle);
                    preparedStmt.setString(2, sender.getName());
                    preparedStmt.execute();
                }
                } catch (SQLException e) {
                 e.printStackTrace();
                }
            }
            }
        return false;
    }
}
