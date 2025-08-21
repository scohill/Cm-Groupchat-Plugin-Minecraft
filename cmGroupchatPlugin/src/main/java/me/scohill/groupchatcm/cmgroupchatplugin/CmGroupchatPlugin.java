package me.scohill.groupchatcm.cmgroupchatplugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.HashMap;

public final class CmGroupchatPlugin extends JavaPlugin {
    HashMap <String,String> invites=new HashMap<>();
    Connection con=null;
    String host,database,user,pass,table;
    String pcolor,gccolor,gctext,smain,splayer,emain,eplayer;//color codes from config
    boolean chatmsgs;
    int port;
    @Override
    public void onEnable() {
        //invite,accept,decline
        loadConfig();
        Connection con=Connect();

        if(isConnected(con)){
            Bukkit.getLogger().info("Connected to database");
            createGcdata();
            createStaffSpy();
            this.getServer().getPluginManager().registerEvents(new chatListener(),this);
            getCommand("gccreate").setExecutor(new gcCreate());
            getCommand("gclist").setExecutor(new gcListAll());
            getCommand("gcmylist").setExecutor(new gcMylist());
            getCommand("g").setExecutor(new gcChat());
            getCommand("gctoggle").setExecutor(new gcToggle());
            getCommand("gcselect").setExecutor(new gcSwitch());
            getCommand("gcselect").setExecutor(new gcSwitch());
            getCommand("gckick").setExecutor(new gcKick());
            getCommand("gcsetowner").setExecutor(new gcSetowner());
            getCommand("gcdelete").setExecutor(new gcDelete());
            getCommand("gctypeset").setExecutor(new gcTypeSet());
            getCommand("gcJoin").setExecutor(new gcJoin());
            getCommand("gcLeave").setExecutor(new gcLeave());
            getCommand("gcInvite").setExecutor(new gcInvite());
            getCommand("gcHelp").setExecutor(new gcHelp());
            getCommand("gcReload").setExecutor(new gcReload());
            getCommand("gcsocialspy").setExecutor(new gcSocialSpy());

        }

    }

    @Override
    public void onDisable() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
    public Connection Connect(){
        pcolor = this.getConfig().getString("log-gcmsgs-in-console");
        pcolor = this.getConfig().getString("gcplayernamecolor");
        gccolor = this.getConfig().getString("gcnamecolor");
        gctext = this.getConfig().getString("gctextcolor");
        smain = this.getConfig().getString("success-msg-main-color");
        splayer = this.getConfig().getString("names-in-success-msgs-color");
        emain = this.getConfig().getString("error-msg-main-color");
        eplayer = this.getConfig().getString("name-in-error-msg-color");
        chatmsgs = this.getConfig().getBoolean("log-gcmsgs-in-console");

        host = this.getConfig().getString("host");
        port = this.getConfig().getInt("port");
        user = this.getConfig().getString("username");
        pass = this.getConfig().getString("password");
        table = this.getConfig().getString("table");
        database = this.getConfig().getString("database");
        try { //connecting the database
            con = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", user, pass);
        } catch (SQLException e) {e.printStackTrace();
        }
        return con;
    }
    public boolean isConnected(Connection con){
    if(con==null){
        return false;
    }
    return true;
    }
    public void createGcdata(){
        Statement stmt=null;
        ResultSet rs;
        String sql = "SELECT * \n" + //test if gcdata exist
                "FROM information_schema.tables\n" +
                "WHERE table_schema = '" + database + "'\n" +
                "    AND table_name = 'gcdata'\n" +
                "LIMIT 1;";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (!rs.next()) {//if the table gcdata doesn't exist:
                sql = "CREATE TABLE gcdata ("
                        + "id int (4) NOT NULL AUTO_INCREMENT,"
                        + "gcname VARCHAR (16),"
                        + "pname VARCHAR (32),"
                        + "maingc VARCHAR (16),"
                        + "gcowner VARCHAR (16),"
                        + "type VARCHAR (7),"
                        + "toggle VARCHAR (5),"
                        + "PRIMARY KEY(id))";
                try {
                    PreparedStatement stamt = con.prepareStatement(sql);
                    stamt.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {e.printStackTrace();
        }
    }
    public void createStaffSpy(){
        Statement stmt=null;
        ResultSet rs;
        String sql = "SELECT * \n" + //test if staffspy exist
                "FROM information_schema.tables\n" +
                "WHERE table_schema = '" + database + "'\n" +
                "    AND table_name = 'staffspy'\n" +
                "LIMIT 1;";
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (!rs.next()) {//if the table staffspy doesn't exist:
                sql = "CREATE TABLE staffspy ("
                        + "id int (4) NOT NULL AUTO_INCREMENT,"
                        + "name VARCHAR (64),"
                        + "status VARCHAR (10),"
                        + "PRIMARY KEY(id))";
                try {
                    PreparedStatement stamt = con.prepareStatement(sql);
                    stamt.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {e.printStackTrace();
        }
    }
}
