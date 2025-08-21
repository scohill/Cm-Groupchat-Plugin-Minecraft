package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class gcJoin implements TabExecutor {
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
            if (!(sender.hasPermission("gc.join"))) {
                sender.sendMessage(ChatColor.valueOf(emain)+ "You don't have the permission gc.join");
                return false;
            }
            if (sender instanceof Player) {
            if(args.length!=1){
                sender.sendMessage(ChatColor.valueOf(emain)+ "Invalid arguments, Example: /gcjoin Groupchat1");
                return false;
                }
            if(groups!=null){
                int i=0;
                while (i<groups.size()){
                    if(groups.get(i).equals(args[0])){
                        PreparedStatement preparedStmt=null;



                        String query = " insert into gcdata (gcname,pname,maingc,gcowner,type,toggle)"
                                + " values (?, ?, ?, ?, ?, ?)";
                        try {
                            String sql="Select gcowner from gcdata where gcname=? and type=?";
                            preparedStmt=con.prepareStatement(sql);
                            preparedStmt.setString(1,args[0]);
                            preparedStmt.setString(2,"Public");
                            ResultSet rs=preparedStmt.executeQuery();
                            String gcowner="";
                            if(rs.next()){
                                gcowner=rs.getString(1);
                            }
                            preparedStmt = con.prepareStatement(query);
                            preparedStmt.setString(1, args[0]);
                            preparedStmt.setString(2, ((Player) sender).getDisplayName());
                            preparedStmt.setString(3, args[0]);
                            preparedStmt.setString(4, gcowner);
                            preparedStmt.setString(5, "Public");
                            preparedStmt.setString(6, "false");
                            preparedStmt.execute();

                        sql = "SELECT pname from gcdata where gcname=?";
                        preparedStmt = con.prepareStatement(sql);
                        preparedStmt.setString(1, args[0]);
                        rs = preparedStmt.executeQuery();
                        while (rs.next()) {
                            for (Player p : gc.getServer().getOnlinePlayers()) {
                                if (rs.getString(1).equals(p.getDisplayName())) {
                                    p.sendMessage(ChatColor.valueOf(splayer)+((Player) sender).getDisplayName()+ChatColor.valueOf(smain)
                                            +" has joined the groupchat "+ChatColor.valueOf(splayer)+args[0]);
                                    break;
                                }
                            }
                        }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.valueOf(eplayer)+args[0]+ChatColor.valueOf(emain)+" doesn't exist, is not public or you already belong to it!");
            }
        }
    }
        return false;
    }

    @Override
    public List<String> onTabComplete (CommandSender sender, Command command, String alias, String[] args) {
        groups=new ArrayList<>();
       String query = "select distinct gcname from gcdata where gcowner!=? and gcname not in" +
                        "(select distinct gcname from gcdata where pname=?)";
        try {
            PreparedStatement stm=con.prepareStatement(query);
            stm.setString(1,sender.getName());
            stm.setString(2,sender.getName());
            ResultSet rs=stm.executeQuery();
            while(rs.next()){
                groups.add(rs.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }
}
