package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class chatListener implements Listener {
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
@EventHandler
public boolean chatChecker(AsyncPlayerChatEvent event){
    Player player =event.getPlayer();
    player.getDisplayName();
    Statement stmt=null;
    ResultSet rs=null;
    PreparedStatement preparedStmt=null;
    if (con != null) {
        try {
            String sql = "SELECT maingc,toggle \n" + //finding the gcname
                    "FROM gcdata\n" +
                    "WHERE pname = '" + player.getName() + "'\n" +
                    "LIMIT 1;";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next()){
                if(rs.getString(1) ==null){
                    return true;
                }
                if(rs.getString(2).equals("true")){
                    event.setCancelled(true);
                    String sentmsg=event.getMessage();
                    String gcnamemsg=ChatColor.valueOf(gccolor)  + "["+ rs.getString(1) + ChatColor.BLUE+"] ";
                    String gcpnamemsg=ChatColor.valueOf(pcolor) + player.getDisplayName()+ChatColor.GRAY + " > "+ChatColor.RESET;
                    String sendmsg=ChatColor.valueOf(gctext)+sentmsg;
                    sendmsg = ChatColor.translateAlternateColorCodes('&', sendmsg);
                    gcnamemsg = ChatColor.translateAlternateColorCodes('&', gcnamemsg);
                    String msg = gcnamemsg+gcpnamemsg+sendmsg;
                    sql = "SELECT name from staffspy where status='enabled'";
                    preparedStmt = con.prepareStatement(sql);
                    ResultSet rs1 = preparedStmt.executeQuery();
                    sql = "SELECT pname from gcdata where gcname=?";
                    preparedStmt = con.prepareStatement(sql);
                    preparedStmt.setString(1, rs.getString(1));
                    rs = preparedStmt.executeQuery();
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

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        }
    return true;
}
}
