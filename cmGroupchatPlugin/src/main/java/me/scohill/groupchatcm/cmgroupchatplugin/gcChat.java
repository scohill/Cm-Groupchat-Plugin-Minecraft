package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class gcChat implements CommandExecutor {
    CmGroupchatPlugin gc = CmGroupchatPlugin.getPlugin(CmGroupchatPlugin.class);
    Connection con = gc.con;
    String pcolor=gc.pcolor;
    String gccolor=gc.gccolor;
    String gctext=gc.gctext;
    String smain=gc.smain;
    String splayer=gc.splayer;
    String emain=gc.emain;
    String eplayer=gc.eplayer;
    boolean logchat=gc.chatmsgs;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement preparedStmt=null;
        String gcname=null;
        if (con != null) {
            if (!(sender.hasPermission("gc.chat"))) {
                sender.sendMessage(ChatColor.valueOf(emain)+ "You don't have the permission gc.chat");
                return false;
            }

            if (sender instanceof Player) {
                try {
                    String sql = "SELECT maingc \n" + //finding the gcname
                            "FROM gcdata\n" +
                            "WHERE pname = '" + sender.getName() + "'\n" +
                            "LIMIT 1;";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(sql);

                    if (rs.next()) {
                        if(rs.getString(1) ==null){
                            sender.sendMessage(ChatColor.valueOf(emain) + "Please select a groupchat(/gcselect)");
                            return true;
                        };
                        gcname = rs.getString(1);
                    }else{
                        sender.sendMessage(ChatColor.valueOf(emain) + "You dont belong to any groupchat, join or create one");

                    }
                    String msg="";
                    int i = 0;
                    while (i < args.length) {
                        msg = msg + args[i] + " ";
                        i++;
                    }
                    String gcnamemsg=ChatColor.valueOf(gccolor)  + "["+ rs.getString(1) + ChatColor.BLUE+"] ";
                    String gcpnamemsg=ChatColor.valueOf(pcolor) + sender.getName()+ChatColor.GRAY + " > "+ChatColor.RESET;
                    String sendmsg=ChatColor.valueOf(gctext)+msg;
                    sendmsg = ChatColor.translateAlternateColorCodes('&', sendmsg);
                    gcnamemsg = ChatColor.translateAlternateColorCodes('&', gcnamemsg);
                    msg = gcnamemsg+gcpnamemsg+sendmsg;
                    sql = "SELECT pname from gcdata where gcname=?";
                    preparedStmt = con.prepareStatement(sql);
                    preparedStmt.setString(1, gcname);
                    rs = preparedStmt.executeQuery();
                    sql = "SELECT name from staffspy where status='enabled'";
                    preparedStmt = con.prepareStatement(sql);
                    ResultSet rs1 = preparedStmt.executeQuery();
                    List<String> stafftest=new ArrayList<>();
                    while (rs.next()) {
                        for (Player p : gc.getServer().getOnlinePlayers()) {
                            if (rs.getString(1).equals(p.getDisplayName())) {
                                p.sendMessage(msg);
                                stafftest.add(p.getDisplayName());
                                break;
                            }
                        }
                    }
                    while (rs1.next()) {
                        int j=0;
                        boolean found=false;
                        while (j<stafftest.size()){
                            if(stafftest.get(j).equals(rs1.getString(1)));
                            found=true;
                            break;
                        }
                        if(found){
                            continue;
                        }
                        for (Player p : gc.getServer().getOnlinePlayers()) {
                            if (rs1.getString(1).equals(p.getDisplayName())) {
                                p.sendMessage(ChatColor.GREEN+"{Spy} "+msg);
                                break;
                            }
                        }
                    }
                    if(logchat)
                    Bukkit.getLogger().info(msg);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

                return false;
            }
            return true;
        }
    }

