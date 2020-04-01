package de.glaubekeinemdev.kbffa.database;

import com.google.common.collect.Maps;
import de.glaubekeinemdev.kbffa.KnockBackFFA;
import de.glaubekeinemdev.kbffa.utils.fetchers.UUIDFetcher;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
@Getter
public class DatabaseHandler {

    private final MySQL mySQL;
    private final HashMap<UUID, PlayerStats> statsCache = new HashMap<>( );
    private final HashMap<UUID, String> nameCache = new HashMap<>( );

    private final HashMap<UUID, PlayerStats> top10Players = new HashMap<>( );
    private final HashMap<UUID, Integer> playerPositionInRanking = new HashMap<>( );

    public DatabaseHandler( final MySQL mySQL ) {
        this.mySQL = mySQL;
    }

    public double getKills( final Player player ) {
        if( statsCache.containsKey( player.getUniqueId( ) ) ) {
            return statsCache.get( player.getUniqueId( ) ).getKills( );
        }
        return 0.0;
    }

    public double getDeaths( final Player player ) {
        if( statsCache.containsKey( player.getUniqueId( ) ) ) {
            return statsCache.get( player.getUniqueId( ) ).getDeaths( );
        }
        return 0.0;
    }

    public PlayerStats getStats( final Player player ) {
        if( statsCache.containsKey( player.getUniqueId( ) ) )
            return statsCache.get( player.getUniqueId( ) );

        return new PlayerStats( 0, 0 );
    }

    public void insertStatsToDatabase( final UUID uuid, final PlayerStats playerStats ) {
        existsPlayerInDatabase( uuid, aBoolean -> {
            if( aBoolean ) {
                getMySQL( ).update( "UPDATE KBFFA_stats SET KILLS= '" + playerStats.getKills( )
                        + "' WHERE UUID= '" + uuid.toString( ) + "';" );

                getMySQL( ).update( "UPDATE KBFFA_stats SET DEATHS= '" + playerStats.getDeaths( )
                        + "' WHERE UUID= '" + uuid.toString( ) + "';" );
            }
        } );
    }

    public void loadPlayerIntoCache( final Player player ) {
        createPlayerInDatabase( player );

        getMySQL( ).getResult( "SELECT * FROM KBFFA_stats WHERE UUID='" + player.getUniqueId( ).toString( ) + "'", resultSet -> {
            try {
                if( resultSet.next( ) ) {

                    statsCache.put( player.getUniqueId( ), new PlayerStats(
                            Double.parseDouble( resultSet.getString( "KILLS" ) ),
                            Double.parseDouble( resultSet.getString( "DEATHS" ) ) ) );

                    nameCache.put( player.getUniqueId( ), player.getName( ) );

                    if( !resultSet.getString( "SKINVALUE" ).equalsIgnoreCase( KnockBackFFA.getInstance( ).getValue( player ) ) ) {
                        getMySQL( ).update( "UPDATE KBFFA_stats SET SKINVALUE= '" + KnockBackFFA.getInstance( ).getValue( player )
                                + "' WHERE UUID= '" + player.getUniqueId( ).toString( ) + "';" );
                    }

                    if( !resultSet.getString( "NAME" ).equalsIgnoreCase( player.getName( ) ) ) {
                        getMySQL( ).update( "UPDATE KBFFA_stats SET NAME= '" + player.getName( )
                                + "' WHERE UUID= '" + player.getUniqueId( ).toString( ) + "';" );
                    }

                    KnockBackFFA.getInstance( ).getPlayerSkullManager( ).getSkinCache( ).put( player.getUniqueId( ), KnockBackFFA.getInstance( ).getValue( player ) );
                }
            } catch ( SQLException e ) {
                e.printStackTrace( );
            }
        } );
    }

    public void createPlayerInDatabase( final Player player ) {
        existsPlayerInDatabase( player.getUniqueId( ), aBoolean -> {

            if( !aBoolean ) {
                getMySQL( ).update( "INSERT INTO KBFFA_stats (NAME, UUID, KILLS, DEATHS, SKINVALUE) VALUES (" +
                        "'" + player.getName( ) + "', '" + player.getUniqueId( ).toString( ) + "'," +
                        " '" + "0" + "', '" + "0" + "', '" + KnockBackFFA.getInstance( ).getValue( player ) + "')" );
            }

        } );
    }

    public void existsPlayerInDatabase( final UUID uuid, final Consumer<Boolean> consumer ) {
        getMySQL( ).getResult( "SELECT * FROM KBFFA_stats WHERE UUID='" + uuid.toString( ) + "'", resultSet -> {
            try {
                consumer.accept( resultSet.next( ) );
            } catch ( SQLException e ) {
                e.printStackTrace( );
                consumer.accept( false );
            }
        } );
    }

    public void addDeath( final Player player ) {
        if( statsCache.containsKey( player.getUniqueId( ) ) ) {
            PlayerStats playerStats = statsCache.get( player.getUniqueId( ) );
            playerStats.addDeath( );
            updateCache( player.getUniqueId( ), playerStats );
        }
    }

    public void addKill( final Player player ) {
        if( statsCache.containsKey( player.getUniqueId( ) ) ) {
            PlayerStats playerStats = statsCache.get( player.getUniqueId( ) );
            playerStats.addKill( );
            updateCache( player.getUniqueId( ), playerStats );
        }
    }

    public void updateCache( final UUID uuid, final PlayerStats playerStats ) {
        if( !statsCache.containsKey( uuid ) )
            return;

        statsCache.remove( uuid );
        statsCache.put( uuid, playerStats );

    }

    public String getName( final UUID uuid ) {
        if( nameCache.containsKey( uuid ) )
            return nameCache.get( uuid );

        return UUIDFetcher.getName( uuid );
    }

    public void loadTop10( ) {
        getTop10Players( ).clear( );
        getPlayerPositionInRanking().clear();

        getMySQL( ).getResult( "SELECT * FROM KBFFA_stats ORDER BY KILLS DESC", resultSet -> {

            int counter = 1;

            try {
                while ( resultSet.next( ) ) {
                    final UUID uuid = UUID.fromString( resultSet.getString( "UUID" ) );

                    if(top10Players.keySet().size() != 10)
                        top10Players.put( uuid, new PlayerStats( resultSet.getInt( "KILLS" ), resultSet.getInt( "DEATHS" ) ) );

                    if( !playerPositionInRanking.containsKey( uuid ) )
                        playerPositionInRanking.put( uuid, counter );

                    counter++;

                    if( !nameCache.containsKey( uuid ) )
                        nameCache.put( uuid, resultSet.getString( "NAME" ) );

                    if( !KnockBackFFA.getInstance( ).getPlayerSkullManager( ).getSkinCache( ).containsKey( uuid ) )
                        KnockBackFFA.getInstance( ).getPlayerSkullManager( ).getSkinCache( ).put( uuid, resultSet.getString( "SKINVALUE" ) );
                }
            } catch ( SQLException e ) {
                e.printStackTrace( );
            }

            new BukkitRunnable( ) {
                @Override
                public void run( ) {
                    KnockBackFFA.getInstance( ).getInventoryManager( ).loadStatsInventory( );
                }
            }.runTaskLater( KnockBackFFA.getInstance( ), 20 );
        } );
    }

    public void startRefreshingTop10( ) {
        final int delay = 20 * 60 * 5;

        KnockBackFFA.getInstance( ).getService( ).execute( ( ) -> new BukkitRunnable( ) {
            @Override
            public void run( ) {
                loadTop10( );
            }
        }.runTaskTimer( KnockBackFFA.getInstance( ), delay, delay ) );
    }

}
