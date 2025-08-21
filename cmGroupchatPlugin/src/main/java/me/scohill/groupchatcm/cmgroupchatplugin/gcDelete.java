package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class gcDelete implements TabExecutor {
    List<String> groups=new ArrayList<>();
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
            if(!(sender.hasPermission("gc.delete"))){
                sender.sendMessage(ChatColor.valueOf(emain)+"You don't have the permission gc.delete");
                return  false;
            }
            if (sender instanceof Player) {
                if(args.length!=1){
                    sender.sendMessage(ChatColor.valueOf(emain)+"Inccorect usage of /gcdelete {gcname}");
                    return true;
                }
                int i=0;
                while(i<groups.size()){
                    if(args[0].equals(groups.get(i))){
                        String sql = "delete from gcdata where gcname=?";
                        String sql1 = "update gcdata set maingc=gcname where maingc=?";

                        PreparedStatement preparedStmt = null;
                        try {
                            preparedStmt = con.prepareStatement(sql);
                            preparedStmt.setString(1, args[0]);
                            preparedStmt.execute();
                            preparedStmt = con.prepareStatement(sql1);
                            preparedStmt.setString(1, args[0]);
                            preparedStmt.execute();
                            sender.sendMessage(ChatColor.valueOf(pcolor)+ args[0]+ChatColor.valueOf(gccolor)+" has been deleted successfuly");
                            groups=new ArrayList<>();
                            return true;
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


                        return true;

                    }else{i++;}
                }
                sender.sendMessage(ChatColor.valueOf(eplayer)+args[0]+ChatColor.valueOf(emain)+" doesn't exist or it isn't owned by you");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        String sql="SELECT gcname from gcdata where gcowner=?";
        try {
            PreparedStatement stm=con.prepareStatement(sql);
            stm.setString(1,sender.getName());
            ResultSet rs=stm.executeQuery();
            while (rs.next()){
                groups.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }
}
