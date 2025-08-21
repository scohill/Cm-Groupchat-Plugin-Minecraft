package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.awt.geom.RectangularShape;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class gcSocialSpy implements CommandExecutor {
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
        if (!(sender.hasPermission("gcstaff.socialspy"))) {
            sender.sendMessage(ChatColor.valueOf(emain) + "You don't have the permission gcstaff.socialspy");
            return false;
        }
        String sql="Select * from staffspy where name=?";
        PreparedStatement stm=null;
        try {
            stm=con.prepareStatement(sql);
            stm.setString(1,sender.getName());
            ResultSet rs=stm.executeQuery();
            if(rs.next()){
                if(rs.getString(3).equals("enabled")){
                    sql="update staffspy set status='disabled' where name=?";
                    stm=con.prepareStatement(sql);
                    stm.setString(1,sender.getName());
                    stm.execute();
                    sender.sendMessage(ChatColor.valueOf(smain)+"Groupchat spy has been disabled");

                }
                else{
                    sql="update staffspy set status='enabled' where name=?";
                    stm=con.prepareStatement(sql);
                    stm.setString(1,sender.getName());
                    stm.execute();
                    sender.sendMessage(ChatColor.valueOf(smain)+"Groupchat spy has been enabled");
                }
            }
            else{
                sql="insert into staffspy (name,status) values (?,?)";
                stm=con.prepareStatement(sql);
                stm.setString(1,sender.getName());
                stm.setString(2,"enabled");
                stm.execute();
                sender.sendMessage(ChatColor.valueOf(smain)+"Groupchat spy has been enabled");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return false;
    }
}
