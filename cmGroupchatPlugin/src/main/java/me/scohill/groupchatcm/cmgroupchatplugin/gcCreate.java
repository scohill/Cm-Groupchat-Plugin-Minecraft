package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class gcCreate implements CommandExecutor {
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
            if(!(sender.hasPermission("gc.create"))){
                sender.sendMessage(ChatColor.valueOf(emain) +"You don't have the permission gc.create");
                return  false;
            }
            if (sender instanceof Player) {
                if (args.length < 2 || args == null) {
                    sender.sendMessage(ChatColor.valueOf(emain)+ "Please supply a name and a type (0 for private, 1 for public)");
                    return true;
                } else if (args.length > 2) {
                    sender.sendMessage(ChatColor.valueOf(emain)+ "Incorrect usage of the command, correct example: /gccreate MyGroupChat 1");
                    return true;
                } else {
                    int test = Integer.parseInt(args[1]);
                    if (test != 0 && test != 1) {
                        sender.sendMessage(ChatColor.valueOf(emain)+ "Please insert a valid type, Example: /gccreate MyGroupChat 1");
                        return true;
                    } else {

                        for(int i=0;i<args[0].length();i++){
                            if(args[0].charAt(i)=='&'){
                                if (args[0].charAt(i+1)=='k'||args[0].charAt(i+1)=='m'){
                                    sender.sendMessage(ChatColor.valueOf(emain)+ "Invalid characters used");
                                    return true;
                                }
                            }
                        }
                        try {
                            createGroup(sender,args);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
        }
        return true;
    }
    public void createGroup(CommandSender sender, String[] args) throws SQLException {
        String type;
        PreparedStatement preparedStmt;
        int i = Integer.parseInt(args[1]);
        if (i == 1) {
            type = "Public";
        } else type = "Private";
        String query = "SELECT * from gcdata where gcname=? LIMIT 1";
        preparedStmt = con.prepareStatement(query);
        preparedStmt.setString(1, args[0]);
        ResultSet rs = preparedStmt.executeQuery();
        if (!rs.next()) {
            query = " insert into gcdata (gcname,pname,maingc,gcowner,type,toggle)"
                    + " values (?, ?, ?, ?, ?, ?)";
            preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, args[0]);
            preparedStmt.setString(2, ((Player) sender).getDisplayName());
            preparedStmt.setString(3, args[0]);
            preparedStmt.setString(4, ((Player) sender).getDisplayName());
            preparedStmt.setString(5, type);
            preparedStmt.setString(6, "false");
            preparedStmt.execute();
            sender.sendMessage(ChatColor.valueOf(smain) + "Groupchat "+ChatColor.valueOf(pcolor)+ args[0] + ChatColor.RESET + " "
                    + ChatColor.valueOf(smain)+ "Created Successfuly!");
             query = "update gcdata set maingc = ? where pname = ?";
             preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, args[0]);
            preparedStmt.setString(2, sender.getName());
            preparedStmt.execute();
        }else {
            sender.sendMessage(ChatColor.valueOf(emain)+ "This groupchat name is in use, please choose another name");
        }
    }
}