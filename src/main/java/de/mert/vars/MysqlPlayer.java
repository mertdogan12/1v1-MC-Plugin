package de.mert.vars;

import de.mert.main.OneVOne;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MysqlPlayer {
    private String host, database, username, password;
    private int port;
    private Connection connection;
    private Statement statement;
    private ConsoleCommandSender s = Bukkit.getConsoleSender();
    private String pr = OneVOne.prefix;
    public HashMap<String, Integer> elo = new HashMap<>();
    public double[] mmElo;
    private String name;
    private Player p;

    public MysqlPlayer(Player p) {
        this.p = p;

        //Connect to Mysql Database
        File pwF = new File("plugins/1v1/db.yml");
        if (!pwF.exists()) {
            if (pwF.getParentFile() != null) pwF.getParentFile().mkdirs();
            try {
                pwF.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration db = YamlConfiguration.loadConfiguration(pwF);

        port = 3306;
        host = "localhost";
        database = "minecraft";
        username = "root";
        password = "";

        if (db.isSet("pw")) {
            password = db.getString("pw");
        }

        if (db.isSet("port")) {
            port = db.getInt("port");
        }

        if (db.isSet("ip")) {
            host = db.getString("ip");
        }

        if (db.isSet("database")) {
            database = db.getString("database");
        }

        if (db.isSet("user")) {
            username = db.getString("user");
        }

        try {
            openConnection();
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            s.sendMessage(pr+"§cNot Connected to Mysql database");
        }

        if (getElo("UHC") == null) {
            try {
                statement.executeUpdate("INSERT INTO elo (uuid, Soup, UHC) VALUES ('"+p.getUniqueId().toString()+"', 100, 100)");
            } catch (Exception ex) {
                p.sendMessage(pr+"§cError loading the Elo");
                return;
            }
        }

        if (getName() == null ) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
            java.util.Date date = new Date();
            try {
                statement.executeUpdate("INSERT INTO name (uuid, ign, firstLogin) VALUES ('"+p.getUniqueId().toString()+"', '"+p.getName()+"', '"+formatter.format(date)+"')");
            } catch (Exception ex1) {
                s.sendMessage(pr+"§cError when inserting the name");
                p.sendMessage(pr+"§cError when connecting to the Mysql database");
                return;
            }
        }

        if (!getName().equals(p.getName())) {
            try {
                statement.executeUpdate("UPDATE name SET ign = '"+p.getName()+"' WHERE uuid = '"+p.getUniqueId().toString()+"'");
            } catch (SQLException throwables) {
                s.sendMessage(pr+"§cError updating the name");
                throwables.printStackTrace();
                p.sendMessage(pr+"§cError when connecting to the Mysql database");
                return;
            }
        }

        if (getEloSettings() == null) {
            try {
                statement.executeUpdate("INSERT INTO player (uuid, kills, death, settings_maxElo, settings_minElo) VALUES ('"+p.getUniqueId().toString()+"', null, null, 200, 200)");
            } catch (SQLException throwables) {
                s.sendMessage(pr+"§cError updating the settings");
                throwables.printStackTrace();
                p.sendMessage(pr+"§cError when connecting to the Mysql database");
                return;
            }
        }

        elo.put("Soup", getElo("Soup"));
        elo.put("UHC", getElo("UHC"));
        elo.put("All", getElo("Soup") + getElo("UHC"));
        mmElo = getEloSettings();

        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean updateElo(double elo, String kit) {
        try {
            openConnection();
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            statement.executeUpdate("UPDATE elo SET "+kit+" = "+elo+" WHERE uuid = '"+p.getUniqueId().toString()+"'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updatemmElo(double elo, String s) {
        try {
            openConnection();
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            statement.executeUpdate("UPDATE player SET "+s+" = "+elo+" WHERE uuid = '"+p.getUniqueId().toString()+"'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public void changeEloSetting(String mmElo, int newElo) throws SQLException {
        statement.executeUpdate("UPDATE `player` SET `"+mmElo+"`="+newElo+" WHERE uuid = '"+p.getUniqueId().toString()+"';");
    }

    private double[] getEloSettings() {
        double[] mmelo = new double[2];
        String pr = OneVOne.prefix;

        // Get the max and min Elo
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM player WHERE uuid = '"+p.getUniqueId().toString()+"';");
            List<Integer> l = new ArrayList<>();
            while (result.next()) {
                l.add(result.getInt("settings_maxElo"));
                l.add(result.getInt("settings_minElo"));
            }

            /*
                1 - 0 is the opponent's rating range.
            */
            mmelo[0] = l.get(0);
            mmelo[1] = l.get(1);
        } catch (Exception ex) {
            this.s.sendMessage(OneVOne.prefix+"Error in Funktion bei ändern der Settings");
            return null;
        }

        if (mmelo[1] < 0) mmelo[1] = 0;

        return mmelo;
    }

    private Integer getElo(String kit) {
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM elo WHERE uuid = '"+p.getUniqueId().toString()+"';");
            List<Integer> l = new ArrayList<>();
            while (result.next()) {
                l.add(result.getInt(kit));
            }
            return l.get(0);
        } catch (Exception ex) {
            s.sendMessage("Error in the funktion getMysql |  elo | "+kit);
            return null;
        }
    }

    private String getName() {
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM name WHERE uuid = '"+p.getUniqueId().toString()+"';");
            List<String> l = new ArrayList<>();
            while (result.next()) {
                l.add(result.getString("ign"));
            }
            return l.get(0);
        } catch (Exception ex) {
            s.sendMessage(pr+"§cError in the funktion getName");
            ex.printStackTrace();
            return null;
        }
    }

    public boolean openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            s.sendMessage(pr+"Connection is already open");
            return false;
        }

        synchronized (OneVOne.getPlugin()) {
            if (connection != null && !connection.isClosed()) {
                s.sendMessage(pr+"Connection is already open");
                return false;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=latin1", username, password);
        }
        return true;
    }
}