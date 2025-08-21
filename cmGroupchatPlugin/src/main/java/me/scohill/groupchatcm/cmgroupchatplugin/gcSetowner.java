package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class gcSetowner implements TabExecutor {
    CmGroupchatPlugin gc = CmGroupchatPlugin.getPlugin(CmGroupchatPlugin.class);
    Connection con = gc.con;
    List<String>members=new ArrayList<>();
    String maingc=null;
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
            if (!(sender.hasPermission("gc.setowner"))) {
                sender.sendMessage(ChatColor.valueOf(emain)+ "You don't have the permission gc.setowner");
                return false;
            }
            if (sender instanceof Player) {
                int i=0;
                while(i<members.size()) {
                    if (args[0].equals(members.get(i))){
                        PreparedStatement stmt;
                        String sql="update gcdata set gcowner=? where gcname=? and gcowner=?";
                        try {

                            stmt=con.prepareStatement(sql);
                            stmt.setString(1,args[0]);
                            stmt.setString(2,maingc);
                            stmt.setString(3,sender.getName());
                            stmt.executeUpdate();
                            sender.sendMessage(ChatColor.DARK_PURPLE + ""
                                    + ChatColor.BOLD + args[0]+" has been made the owner of "+maingc);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }else{
                        i++;
                    }
                }
                sender.sendMessage(ChatColor.valueOf(emain)+ "You are not the owner of this groupchat or " +ChatColor.valueOf(eplayer)
                        +args[0]+ChatColor.valueOf(emain)+" doesn't belong to it");
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        PreparedStatement stmt;

        String sql="Select gcname from gcdata where gcowner=? and gcname=maingc LIMIT 1";
        try {
            stmt=con.prepareStatement(sql);
            stmt.setString(1,sender.getName());
            ResultSet rs=stmt.executeQuery();

            if(rs.next()){
                System.out.println(rs.getString(1));
                 maingc=rs.getString(1);
                sql="Select pname from gcdata where gcname=?";
                stmt=con.prepareStatement(sql);
                stmt.setString(1,maingc);
                rs=stmt.executeQuery();
                while (rs.next()){
                    members.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }
}
