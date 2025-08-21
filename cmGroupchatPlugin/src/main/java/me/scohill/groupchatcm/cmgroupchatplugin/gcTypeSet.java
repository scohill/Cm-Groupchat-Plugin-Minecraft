package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class gcTypeSet implements TabExecutor {
    CmGroupchatPlugin gc = CmGroupchatPlugin.getPlugin(CmGroupchatPlugin.class);
    Connection con = gc.con;
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement preparedStmt = null;
    List<String> groups=new ArrayList<>();
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
            if (!(sender.hasPermission("gc.typeset"))) {
                sender.sendMessage(ChatColor.valueOf(emain)+ "You don't have the permission gc.typeset");
                return false;
            }
            if (sender instanceof Player) {
                int i = 0;
                while (i < groups.size()) {
                    if (args[0].equals(groups.get(i))) {
                        if(args.length>2||args.length<2){
                            sender.sendMessage(ChatColor.valueOf(emain)+ "Invalid format, example: /gctypeset Mygroupchat 1[0 for private, 1 for public]");

                        }else{
                            if(args[1].equals("1")){
                                String sql="Update gcdata set type='Public'where gcname=? and gcowner=?";
                                try {
                                    PreparedStatement stm=con.prepareStatement(sql);
                                    stm.setString(1,args[0]);
                                    stm.setString(2,sender.getName());
                                    stm.executeUpdate();
                                    sender.sendMessage(ChatColor.valueOf(splayer) + args[0] +ChatColor.valueOf(smain)+" has been set to public ");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                            }else {
                                String sql="Update gcdata set type='Private'where gcname=? and gcowner=?";
                                try {
                                    PreparedStatement stm=con.prepareStatement(sql);
                                    stm.setString(1,args[0]);
                                    stm.setString(2,sender.getName());
                                    stm.executeUpdate();
                                    sender.sendMessage(ChatColor.valueOf(splayer) + args[0] +ChatColor.valueOf(smain)+ " has been set to private ");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            return true;
                        }
                    }else i++;
                }
                sender.sendMessage(ChatColor.valueOf(eplayer)+args[0]+ChatColor.valueOf(emain)+" doesn't exist or it isn't owned by you");
            }
        }
        return true;
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
