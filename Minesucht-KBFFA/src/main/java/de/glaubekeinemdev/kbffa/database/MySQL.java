package de.glaubekeinemdev.kbffa.database;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
@Getter
public class MySQL {

    private final ExecutorService service;

    private String username;
    private String password;
    private String hostname;
    private String database;
    private String port;

    private Connection connection;

    public MySQL( String username, String password, String host, String database, Integer port ) {
        this.username = username;
        this.password = password;
        this.hostname = host;
        this.database = database;
        this.port = String.valueOf( port );
        this.service = Executors.newCachedThreadPool();
    }

    public void connect( ) {
        try {
            connection = DriverManager.getConnection( "jdbc:mysql://" + getHostname() + ":" + getPort() +
                    "/" + getDatabase() + "?autoReconnect=true", getUsername(), getPassword() );
            Bukkit.getConsoleSender().sendMessage( KnockBackFFA.PREFIX + "Verbindung zur Datenbank aufgebaut" );
        } catch ( SQLException e ) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage( KnockBackFFA.PREFIX + "Verbindung zur Datenbank gescheitert!" );
        }
    }

    public void createTable( ) {
        try {
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS KBFFA_stats (NAME VARCHAR(100), UUID VARCHAR(100), KILLS VARCHAR(100), DEATHS VARCHAR(100), SKINVALUE VARCHAR(500))" );

            Bukkit.getConsoleSender().sendMessage( KnockBackFFA.PREFIX + "Tabellen erstellt!" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public void close( ) {
        if( connection != null ) {
            try {
                connection.close();
                Bukkit.getConsoleSender().sendMessage( KnockBackFFA.PREFIX + "Verbindung zur Datenbank geschlossen" );
            } catch ( SQLException e ) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage( KnockBackFFA.PREFIX + "Verbindung zur Datenbank konnte nicht geschlossen werden" );
            }
        }
    }

    public void update( final String query ) {
        service.execute( ( ) -> {
            try {
                Statement st = connection.createStatement();
                st.executeUpdate( query );
                st.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        } );
    }

    public void getResult( final String query, final Consumer< ResultSet > consumer ) {
        service.execute( ( ) -> {
            ResultSet resultSet = null;
            try {
                Statement statement = connection.createStatement();
                resultSet = statement.executeQuery( query );
            } catch ( SQLException e ) {
                System.err.println( e );
            }
            consumer.accept( resultSet );
        } );
    }

}
