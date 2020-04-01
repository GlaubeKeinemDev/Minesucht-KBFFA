package de.glaubekeinemdev.kbffa;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.glaubekeinemdev.kbffa.board.BoardManager;
import de.glaubekeinemdev.kbffa.commands.buildCMD;
import de.glaubekeinemdev.kbffa.commands.kbffaCMD;
import de.glaubekeinemdev.kbffa.configuration.Configuration;
import de.glaubekeinemdev.kbffa.configuration.MapData;
import de.glaubekeinemdev.kbffa.database.DatabaseHandler;
import de.glaubekeinemdev.kbffa.database.MySQL;
import de.glaubekeinemdev.kbffa.database.PlayerSkullManager;
import de.glaubekeinemdev.kbffa.listener.*;
import de.glaubekeinemdev.kbffa.utils.InventoryManager;
import de.glaubekeinemdev.kbffa.utils.MapPicker;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
@Getter
public class KnockBackFFA extends JavaPlugin {

    public static String PREFIX;
    public static String NO_PERMISSION;

    private static KnockBackFFA instance;

    private final HashMap<Player, Integer> coinsMap = new HashMap<>( );
    private final Random random = new Random( );

    private File configFile;
    private Configuration configuration;

    private DatabaseHandler databaseHandler;
    private MySQL mySQL;

    private ExecutorService service;

    @Setter
    private MapData currentMap = null;

    private InventoryManager inventoryManager;

    private BoardManager boardManager;

    private MapPicker mapPicker;

    private PlayerSkullManager playerSkullManager;

    private final ConcurrentHashMap<Player, Player> lastDamager = new ConcurrentHashMap<>( );

    @Override
    public void onEnable( ) {
        service = Executors.newCachedThreadPool( );
        instance = this;

        // Config
        initializeConfigs( );


        inventoryManager = new InventoryManager( );
        boardManager = new BoardManager( );
        playerSkullManager = new PlayerSkullManager( );

        // Databse
        if( getConfig( ).getBoolean( "database.useMySQL" ) ) {
            mySQL = new MySQL( getConfig( ).getString( "database.username" ), getConfig( ).getString( "database.password" ),
                    getConfig( ).getString( "database.host" ), getConfig( ).getString( "database.database" ), getConfig( ).getInt( "database.port" ) );

            mySQL.connect( );
            mySQL.createTable( );

            databaseHandler = new DatabaseHandler( mySQL );

            databaseHandler.loadTop10( );
            databaseHandler.startRefreshingTop10( );
        } else {
            Bukkit.getConsoleSender( ).sendMessage( PREFIX + "Die Datenbank ist deaktiviert! Es werden keine Stats verwendet!" );
        }

        // Mappicking
        if( getConfiguration( ).getMaps( ).isEmpty( ) ) {
            Bukkit.getConsoleSender( ).sendMessage( PREFIX + "Es wurden keine Maps gefunden" );
            Bukkit.getConsoleSender( ).sendMessage( PREFIX + "Bitte richte mindestens 1 Map ein und starte den Server neu!" );
        } else {
            currentMap = getRandomMap( );

            if( getConfig( ).getBoolean( "settings.pickRandomMap" ) ) {
                mapPicker = new MapPicker( getConfig( ).getInt( "settings.mapPickInterval" ) );
                mapPicker.startPicking( );
            }
        }

        // Motd
        if( getConfig( ).getBoolean( "settings.setMapAsMotd" ) ) {
            Bukkit.getPluginManager( ).registerEvents( new ServerListPingListener( ), this );
        }
        startDeathchecker( );

        Bukkit.getPluginManager( ).registerEvents( new PlayerJoinListener( ), this );
        Bukkit.getPluginManager( ).registerEvents( new PlayerQuitListener( ), this );
        Bukkit.getPluginManager( ).registerEvents( new CancelledListeners( ), this );
        Bukkit.getPluginManager( ).registerEvents( new EntityDamageListener( ), this );
        Bukkit.getPluginManager( ).registerEvents( new GrapplingHookListener( ), this );
        Bukkit.getPluginManager( ).registerEvents( new InteractListener( ), this );
        Bukkit.getPluginManager( ).registerEvents( new InventoryClickListener( ), this );
        Bukkit.getPluginManager( ).registerEvents( new PlayerItemConsumeListener( ), this );


        getCommand( "build" ).setExecutor( new buildCMD( ) );
        getCommand( "kbffa" ).setExecutor( new kbffaCMD( ) );

        Bukkit.getConsoleSender( ).sendMessage( PREFIX + "KnockbackFFA wurde geladen" );
    }

    @Override
    public void onDisable( ) {
        if( mySQL != null )
            mySQL.close( );

        Bukkit.getConsoleSender( ).sendMessage( PREFIX + "KnockbackFFA wurde deaktiviert!" );
    }

    private void initializeConfigs( ) {
        if( !getDataFolder( ).exists( ) )
            getDataFolder( ).mkdir( );

        saveDefaultConfig( );

        PREFIX = getConfig( ).getString( "settings.prefix" ).replace( "&", "§" );
        NO_PERMISSION = getConfig( ).getString( "settings.no_permission" ).replace( "&", "§" ).
                replace( "%prefix%", PREFIX );


        configFile = new File( getDataFolder( ), "maps.json" );

        if( configFile.exists( ) ) {
            try {
                configuration = new Gson( ).fromJson( new FileReader( configFile ), Configuration.class );
            } catch ( FileNotFoundException e ) {
                e.printStackTrace( );
            }
        } else {
            configuration = new Configuration( );

            try {
                configuration.save( configFile );
            } catch ( IOException e ) {
                e.printStackTrace( );
            }
        }
    }

    public void startDeathchecker( ) {
        service.execute( ( ) -> {
            new BukkitRunnable( ) {
                @Override
                public void run( ) {
                    if( getCurrentMap( ) != null ) {
                        Bukkit.getOnlinePlayers( ).forEach( players -> {
                            if( players.getLocation( ).getY( ) <= getCurrentMap( ).getDeathHeight( ) ) {
                                if( lastDamager.containsKey( players ) ) {
                                    if( !lastDamager.get( players ).getName( ).equalsIgnoreCase( players.getName( ) ) ) {
                                        Player lastDamager = getLastDamager( ).get( players );

                                        final int damagerCoins = getCoinsMap( ).get( lastDamager ) + 2;

                                        getCoinsMap( ).remove( lastDamager );
                                        getCoinsMap( ).put( lastDamager, damagerCoins );

                                        getBoardManager( ).updateCoins( lastDamager );

                                        if( getDatabaseHandler( ) != null )
                                            getDatabaseHandler( ).addKill( lastDamager );

                                        String deathMessage = getConfig().getString( "settings.deathMessage" )
                                                .replace( "%prefix%", PREFIX )
                                                .replace( "&", "§" )
                                                .replace( "%target%", lastDamager.getName() );
                                        players.sendMessage( deathMessage );

                                        String killMessage = getConfig().getString( "settings.killMessage" )
                                                .replace( "%prefix%", PREFIX )
                                                .replace( "&", "§" )
                                                .replace( "%target%", players.getName() );
                                        lastDamager.sendMessage( killMessage );

                                    } else {
                                        String message = getConfig().getString( "settings.suicideMessage" )
                                                .replace( "%prefix%", PREFIX )
                                                .replace( "&", "§" );
                                        players.sendMessage( message );
                                    }
                                } else {
                                    String message = getConfig().getString( "settings.suicideMessage" )
                                            .replace( "%prefix%", PREFIX )
                                            .replace( "&", "§" );
                                    players.sendMessage( message );
                                }

                                final int playerCoins = getCoinsMap( ).get( players ) - 1;

                                getCoinsMap( ).remove( players );

                                if( playerCoins <= 0 ) {
                                    getCoinsMap( ).put( players, 0 );
                                } else {
                                    getCoinsMap( ).put( players, playerCoins );
                                }

                                players.removePotionEffect( PotionEffectType.SPEED );
                                getBoardManager( ).updateCoins( players );
                                getLastDamager().remove( players );

                                getInventoryManager( ).setJoinInventory( players );
                                if( getDatabaseHandler( ) != null )
                                    getDatabaseHandler( ).addDeath( players );
                                players.teleport( getCurrentMap( ).getSpawnLocation( ).toLocation( ) );
                            }
                        } );
                    }
                }
            }.runTaskTimer( instance, 0, 2 );
        } );
    }

    public MapData getRandomMap( ) {
        final List<String> mapList = new ArrayList<>( getConfiguration( ).getMaps( ).keySet( ) );
        return getConfiguration( ).getMaps( ).get( mapList.get( random.nextInt( mapList.size( ) ) ) );
    }

    public String getValue( final Player player ) {
        GameProfile gameProfile = ( ( CraftPlayer ) player ).getProfile( );

        for( Property properties : gameProfile.getProperties( ).get( "textures" ) ) {
            return properties.getValue( );
        }

        return null;
    }

    public String getValue( final GameProfile gameProfile ) {
        for( Property properties : gameProfile.getProperties( ).get( "textures" ) ) {
            return properties.getValue( );
        }

        return null;
    }

    public static KnockBackFFA getInstance( ) {
        return instance;
    }
}
