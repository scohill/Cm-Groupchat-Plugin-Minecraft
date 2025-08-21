package me.scohill.groupchatcm.cmgroupchatplugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class gcInvite implements CommandExecutor {
    CmGroupchatPlugin gc = CmGroupchatPlugin.getPlugin(CmGroupchatPlugin.class);
    Connection con = gc.con;
    Map<String,Long> cds=new HashMap<String,Long>();
    String pcolor=gc.pcolor;
    String gccolor=gc.gccolor;
    String gctext=gc.gctext;
    String smain=gc.smain;
    String splayer=gc.splayer;
    String emain=gc.emain;
    String eplayer=gc.eplayer;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String maingc = "";
        if (con != null) {
            if (!(sender.hasPermission("gc.invite"))) {

                ((Player) sender).sendMessage(org.bukkit.ChatColor.valueOf(emain) +"You don't have the permission gc.invite");
                return false;
            }
            if (sender instanceof Player) {
                try {
                    if(args[0].equals("gcaccept")) {
                        if (cds.containsKey(sender.getName())) {
                            ((Player) sender).sendMessage(org.bukkit.ChatColor.valueOf(emain) + "You have already responded to this invitation");
                            return true;
                        }
                        cds.putIfAbsent(sender.getName(), System.currentTimeMillis() + (3000 * 1000));
                        String sql = "Select gcowner,type from gcdata where gcname=?";
                        PreparedStatement stm = con.prepareStatement(sql);
                        stm.setString(1, args[1]);
                        ResultSet rs = stm.executeQuery();
                        if (rs.next()) {
                            String query = " insert into gcdata (gcname,pname,maingc,gcowner,type,toggle)"
                                    + " values (?, ?, ?, ?, ?, ?)";
                            PreparedStatement preparedStmt = con.prepareStatement(query);
                            preparedStmt.setString(1,args[1]);
                            preparedStmt.setString(2,sender.getName());
                            preparedStmt.setString(3,args[1]);
                            preparedStmt.setString(4,rs.getString(1));
                            preparedStmt.setString(5,rs.getString(2));
                            preparedStmt.setString(6,"false");
                            preparedStmt.execute();
                            query="update gcdata set maingc=? where pname=?";
                            preparedStmt=con.prepareStatement(query);
                            preparedStmt.setString(1,args[1]);
                            preparedStmt.setString(2,sender.getName());
                            preparedStmt.execute();
                            sql = "SELECT pname from gcdata where gcname=?";
                            preparedStmt = con.prepareStatement(sql);
                            preparedStmt.setString(1, args[1]);
                            rs = preparedStmt.executeQuery();
                            while (rs.next()) {
                                for (Player p : gc.getServer().getOnlinePlayers()) {
                                    if (rs.getString(1).equals(p.getDisplayName())) {
                                        p.sendMessage(org.bukkit.ChatColor.DARK_AQUA + "" + org.bukkit.ChatColor.BOLD + sender.getName() + " has joined your groupchat (" + args[1] + ")");
                                    }
                                }
                            }
                            return true;
                        }
                    }else if(args[0].equals("gcdeny")){
                        if (cds.containsKey(sender.getName())) {
                            ((Player) sender).sendMessage(org.bukkit.ChatColor.valueOf(emain) + "You have already responded to this invitation");
                            return true;
                        }
                        cds.putIfAbsent(sender.getName(), System.currentTimeMillis() + (3000 * 1000));
                        ((Player) sender).sendMessage(org.bukkit.ChatColor.valueOf(emain) + "You have declined this invitation");

                        return true;
                    }

                    maingc = getMaingc(sender, args);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!maingc.equals("empty")) {
            TextComponent macc = new TextComponent(" Accept");
            TextComponent mref = new TextComponent(" Deny?");
            TextComponent Phrase = new TextComponent(sender.getName()+" has invited you to join the groupchat '"+maingc+"'");
            TextComponent or = new TextComponent(" or ");
            Phrase.setBold(true);
            or.setBold(true);
            macc.setBold(true);
            mref.setBold(true);
            Phrase.setColor(ChatColor.DARK_PURPLE);
            or.setColor(ChatColor.DARK_PURPLE);
            macc.setColor(ChatColor.GREEN);
            mref.setColor(ChatColor.RED);
            macc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gcinvite gcaccept "+maingc));
            mref.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gcinvite gcdeny"));
            macc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Click Here To Accept")));
            mref.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Click Here To Deny")));
            for (Player p : gc.getServer().getOnlinePlayers()) {
                if (args[0].equals(p.getDisplayName())) {
                    cds.remove(args[0]);
                    sender.sendMessage(org.bukkit.ChatColor.valueOf(splayer)+args[0]+org.bukkit.ChatColor.valueOf(smain)+" has been invited!");
                    BaseComponent[] msg=new ComponentBuilder(Phrase).append(macc).append(or).append(mref).create();
                    p.spigot().sendMessage(msg);
                    return true;
                }
            }
                sender.sendMessage(org.bukkit.ChatColor.valueOf(eplayer)+args[0]+org.bukkit.ChatColor.valueOf(emain)+" is not online");
        }
        return true;
    }
    public String getMaingc(CommandSender sender, String[] args) throws SQLException {
        String maingc = "empty";
        String sql = "Select maingc from gcdata where pname=? LIMIT 1";
        PreparedStatement preparedStmt = con.prepareStatement(sql);
        preparedStmt.setString(1, sender.getName());
        ResultSet rs = preparedStmt.executeQuery();
        if (rs.next()) {
            maingc = rs.getString(1);
            sql = "Select * from gcdata where pname=? and gcname=? LIMIT 1";
            preparedStmt = con.prepareStatement(sql);
            preparedStmt.setString(1, args[0]);
            preparedStmt.setString(2, rs.getString(1));
            rs = preparedStmt.executeQuery();
            if (rs.next()) {
                sender.sendMessage(org.bukkit.ChatColor.valueOf(eplayer)+ args[0] +org.bukkit.ChatColor.valueOf(emain)+ " already belongs to this groupchat");
                return "empty";

            }
        }else{
            sender.sendMessage(org.bukkit.ChatColor.valueOf(emain)+"You don't belong to any groupchat, either join /gcjoin one or create /gccreate a new one");
        }
        return maingc;
    }
}