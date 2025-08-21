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

public class gcLeave implements TabExecutor {
    List<String> groups=new ArrayList<>();
    CmGroupchatPlugin gc = CmGroupchatPlugin.getPlugin(CmGroupchatPlugin.class);
    Connection con = gc.con;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (con != null) {
            if(!(sender.hasPermission("gc.leave"))){
                sender.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You don't have the permission gc.leave");
                return  false;
            }
            if (sender instanceof Player) {
                int i=0;
                while(i<groups.size()){
                    if(args[0].equals(groups.get(i))){
                        PreparedStatement preparedStmt = null;
                        String req= "Select pname,gcowner from gcdata where gcname=?";

                        String sql = "delete from gcdata where gcname=? and pname=?";


                        try {
                            preparedStmt = con.prepareStatement(req);
                            preparedStmt.setString(1, args[0]);
                            ResultSet rs=preparedStmt.executeQuery();
                            if(rs.next()){
                                if(rs.getString(2).equals(sender.getName())){
                                    String sql1="update gcdata set gcowner=? where gcname=? and gcowner=?";
                                    PreparedStatement stmt=con.prepareStatement(sql1);
                                    stmt.setString(1,rs.getString(1));
                                    stmt.setString(2,args[0]);
                                    stmt.setString(3,sender.getName());
                                    stmt.executeUpdate();
                                }
                            }
                            preparedStmt = con.prepareStatement(sql);
                            preparedStmt.setString(1, args[0]);
                            preparedStmt.setString(2, sender.getName());
                            preparedStmt.execute();
                            sender.sendMessage(ChatColor.DARK_PURPLE + ""
                                    + ChatColor.BOLD +"You have left "+args[0]);

                            groups=new ArrayList<>();
                            return true;
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


                        return true;

                    }else{i++;}
                }
                sender.sendMessage(ChatColor.DARK_PURPLE + ""
                        + ChatColor.BOLD + args[0]+" doesn't exist or it isn't owned by you");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        String sql="SELECT gcname from gcdata where pname=?";
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
