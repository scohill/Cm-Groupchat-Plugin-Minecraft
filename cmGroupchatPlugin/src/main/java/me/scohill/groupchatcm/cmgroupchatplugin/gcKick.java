package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.*;

public class gcKick implements CommandExecutor {
    CmGroupchatPlugin gc = CmGroupchatPlugin.getPlugin(CmGroupchatPlugin.class);
    Connection con = gc.con;
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement preparedStmt = null;
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
            if (!(sender.hasPermission("gc.kick"))) {
                sender.sendMessage(ChatColor.valueOf(emain) +"You don't have the permission gc.kick");
                return false;
            }
            if (sender instanceof Player) {
                String maingc="";
                String sql="Select maingc from gcdata where pname=? LIMIT 1"; //retrieving the main gc of the sender
                try {
                    PreparedStatement stm=con.prepareStatement(sql);
                    stm.setString(1,((Player) sender).getDisplayName());
                    ResultSet rs=stm.executeQuery();
                    if(!rs.next()){
                        sender.sendMessage(ChatColor.valueOf(emain)+"You don't belong to any groupchat, create, join or select a groupchat!");
                        return false;
                    }
                    else {
                        maingc = rs.getString(1);
                        sql="select * from gcdata where pname=? and maingc=? and gcowner=? and gcname=?";
                        stm=con.prepareStatement(sql);
                        stm.setString(1,((Player) sender).getDisplayName());
                        stm.setString(2,maingc);
                        stm.setString(3,((Player) sender).getDisplayName());
                        stm.setString(4,maingc);
                        rs= stm.executeQuery();
                        if(!rs.next()){
                            sender.sendMessage(ChatColor.valueOf(emain)+"You are not the owner of groupchat "+ChatColor.valueOf(eplayer)+maingc);
                            return true;
                        }
                        if(args[0].equals(((Player) sender).getDisplayName())){
                            sender.sendMessage(ChatColor.valueOf(eplayer)+args[0]+ChatColor.valueOf(emain)+" is the owner of "+ChatColor.valueOf(eplayer)+maingc);
                            return true;
                        }
                        sql = "Select maingc from gcdata where pname=? and maingc=? LIMIT 1";//checking if the kicked has this gc as main
                        stm = con.prepareStatement(sql);
                        stm.setString(1, args[0]);
                        stm.setString(2, maingc);
                        rs = stm.executeQuery();
                        if (rs.next()) {
                            if (!rs.getString(1).isEmpty()) {
                                if (rs.getString(1).equals(maingc)) {
                                    sql = "UPDATE gcdata set maingc='' where pname=?";
                                    stm = con.prepareStatement(sql);
                                    stm.setString(1, args[0]);
                                    stm.execute();
                                }
                            }
                        }
                        sql = "Select * from gcdata where pname=? and gcname=? LIMIT 1";
                        stm = con.prepareStatement(sql);
                        stm.setString(1, args[0]);
                        stm.setString(2, maingc);
                        ResultSet rs1 = stm.executeQuery();
                        if (!rs1.next()) {
                            sender.sendMessage(ChatColor.valueOf(eplayer) + args[0] + ChatColor.valueOf(emain) + " is not part of this groupchat!");
                            return false;
                        }

                        sql = "Delete from gcdata where gcname=? and pname=? and gcowner=?";
                        stm = con.prepareStatement(sql);
                        stm.setString(1, maingc);
                        stm.setString(2, args[0]);
                        stm.setString(3, ((Player) sender).getDisplayName());
                        stm.execute();
                        sender.sendMessage(ChatColor.valueOf(splayer) + args[0] + ChatColor.valueOf(smain) + " Has been kicked from groupchat !" + ChatColor.valueOf(splayer));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
        return true;
    }
}

