package de.mert.vars;

import de.mert.Listener.PlayerJoinListener;
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

        if (!PlayerJoinListener.rankedMod) return;

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
        } else db.set("pw", password);

        if (db.isSet("port")) {
            port = db.getInt("port");
        } else db.set("port", port);

        if (db.isSet("ip")) {
            host = db.getString("ip");
        } else db.set("ip", host);

        if (db.isSet("database")) {
            database = db.getString("database");
        } else db.set("database", database);

        if (db.isSet("user")) {
            username = db.getString("user");
        } else db.set("user", username);

        try {
            db.save(pwF);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            openConnection();
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            s.sendMessage(pr+"§cNot Connected to Mysql database " +
                    "\nPlease edit the §1plugins/1v1/db.yml");
            return;
        }

        //Gets the Tabels
        List<String> tabels = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "%",  null);

            while (resultSet.next()) {
                tabels.add(resultSet.getString(3));
            }
        } catch (SQLException e) {
            p.sendMessage(pr+"§cError by Tabels");
            s.sendMessage(pr+"§cError by Tabels");
            e.printStackTrace();
        }

        elo.put("All", 0);
        File f = new File("plugins/kits");
        if (f.exists() ) {
            File[] list = f.listFiles();
            if (list != null) {
                for (File n:
                        list) {
                    String kit = n.getName();
                    File file = new File(n+"/"+kit+".yml");
                    if (!file.exists()) continue;

                    if (!tabels.contains(kit)) {
                        try {
                            statement.executeUpdate("CREATE TABLE `"+kit+"` (" +
                                    " `uuid` VARCHAR(255) NOT NULL," +
                                    " `elo` INT NOT NULL," +
                                    "PRIMARY KEY (`uuid`));");
                        } catch (SQLException e) {
                            p.sendMessage(pr+"§cError by creating the table elo");
                            s.sendMessage(pr+"§cError by creating the table elo");
                            e.printStackTrace();
                            return;
                        }
                    }

                    if (getElo(kit) == null) {
                        try {
                            statement.executeUpdate("INSERT INTO "+kit+" (uuid, elo) VALUES ('"+p.getUniqueId().toString()+"', 100)");
                        } catch (Exception ex) {
                            p.sendMessage(pr+"§cError loading the Elo");
                            s.sendMessage(pr+"§cError loading the Elo");
                            ex.printStackTrace();
                            return;
                        }
                    }

                    elo.put(kit, getElo(kit));
                    elo.replace("All", elo.get("All")+getElo(kit));

                }
            }
        }


        //Inserts the name
        if (!tabels.contains("name")) {
            try {
                statement.executeUpdate("CREATE TABLE name (" +
                        "  `uuid` VARCHAR(255) NOT NULL," +
                        "  `ign` VARCHAR(255) NOT NULL," +
                        "  `firstLogin` DATE NOT NULL," +
                        "  PRIMARY KEY (`uuid`))");
            } catch (SQLException e) {
                p.sendMessage(pr+"§cError by creating the table name");
                s.sendMessage(pr+"§cError by creating the table name");
                e.printStackTrace();
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
                p.sendMessage(pr+"§cError when inserting the name");
                ex1.printStackTrace();
                return;
            }
        }

        if (!getName().equals(p.getName())) {
            try {
                statement.executeUpdate("UPDATE name SET ign = '"+p.getName()+"' WHERE uuid = '"+p.getUniqueId().toString()+"'");
            } catch (SQLException throwables) {
                s.sendMessage(pr+"§cError updating the name");
                throwables.printStackTrace();
                p.sendMessage(pr+"§cError updating the name");
                return;
            }
        }

        //Inserts the Settings
        if (!tabels.contains("player")) {
            try {
                statement.executeUpdate("CREATE TABLE player (" +
                        "  `uuid` VARCHAR(255) NOT NULL," +
                        "  `kills` INT NULL," +
                        "  `death` INT NULL," +
                        "  `settings_maxElo` INT NOT NULL," +
                        "  `settings_minElo` INT NOT NULL," +
                        "  PRIMARY KEY (`uuid`))");
            } catch (SQLException e) {
                p.sendMessage(pr+"§cError by creating the table player");
                s.sendMessage(pr+"§cError by creating the table player");
                e.printStackTrace();
                return;
            }
        }

        if (getEloSettings() == null) {
            try {
                statement.executeUpdate("INSERT INTO player (uuid, kills, death, settings_maxElo, settings_minElo) VALUES ('"+p.getUniqueId().toString()+"', null, null, 200, 200)");
            } catch (SQLException throwables) {
                s.sendMessage(pr+"§cError updating the settings");
                throwables.printStackTrace();
                p.sendMessage(pr+"§cError updating the settings");
                return;
            }
        }

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
            s.sendMessage(pr+"§cNot Connected to Mysql database " +
                    "\nPlease edit the §1plugins/1v1/db.yml");
            return false;
        }

        try {
            statement.executeUpdate("UPDATE "+kit+" SET elo = "+elo+" WHERE uuid = '"+p.getUniqueId().toString()+"'");
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
            this.s.sendMessage(pr+"§cNot Connected to Mysql database " +
                    "\nPlease edit the §1plugins/1v1/db.yml");
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
            s.sendMessage(pr+"§cNot Connected to Mysql database " +
                    "\nPlease edit the §1plugins/1v1/db.yml");
            return null;
        }

        if (mmelo[1] < 0) mmelo[1] = 0;

        return mmelo;
    }

    private Integer getElo(String kit) {
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM "+kit+" WHERE uuid = '"+p.getUniqueId().toString()+"';");
            List<Integer> l = new ArrayList<>();
            while (result.next()) {
                l.add(result.getInt("elo"));
            }
            return l.get(0);
        } catch (Exception ex) {
            s.sendMessage(pr+"§cNot Connected to Mysql database " +
                    "\nPlease edit the §1plugins/1v1/db.yml");
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
            s.sendMessage(pr+"§cNot Connected to Mysql database " +
                    "\nPlease edit the §1plugins/1v1/db.yml");
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
