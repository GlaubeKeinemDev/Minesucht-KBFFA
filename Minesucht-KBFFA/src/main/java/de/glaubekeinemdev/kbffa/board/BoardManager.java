package de.glaubekeinemdev.kbffa.board;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import de.glaubekeinemdev.kbffa.database.PlayerStats;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
@Getter
public class BoardManager {

    private final ArrayList< Player > setScoreboard = new ArrayList<>();

    public void setScoreboard( final Player player ) {
        if( !KnockBackFFA.getInstance().getConfig().getBoolean( "settings.setScoreboard" ) )
            return;

        final Scoreboard scoreboard = ( KnockBackFFA.getInstance().getConfig().getBoolean( "settings.usePlayersScoreboard" )
                ? player.getScoreboard() : Bukkit.getScoreboardManager().getNewScoreboard() );

        final Objective objective = ( scoreboard.getObjective( "aaa" ) != null ? scoreboard.getObjective( "aaa" ) :
                scoreboard.registerNewObjective( "aaa", "bbb" ) );

        objective.setDisplaySlot( DisplaySlot.SIDEBAR );
        objective.setDisplayName( KnockBackFFA.getInstance().getConfig().getString( "settings.serverName" )
                .replace( "&", "§" ).replace( "%prefix%", KnockBackFFA.PREFIX ) );

        objective.getScore( "     " ).setScore( 14 );

        // Coins
        objective.getScore( "Coins:" ).setScore( 13 );
        objective.getScore( "§e" ).setScore( 12 );
        Team coinsTeam = scoreboard.registerNewTeam( "playercoins" );
        coinsTeam.addEntry( "§e" );
        coinsTeam.setSuffix( KnockBackFFA.getInstance().getCoinsMap().get( player ) + "" );

        objective.getScore( "    " ).setScore( 11 );

        // Replayid
        objective.getScore( "Replay-ID" ).setScore( 10 );
        objective.getScore( "§eDeaktiviert" ).setScore( 9 );


        objective.getScore( "   " ).setScore( 8 );

        // Spieler
        objective.getScore( "Spieler:" ).setScore( 7 );
        objective.getScore( "§a" ).setScore( 6 );
        Team playerTeam = scoreboard.registerNewTeam( "onlineplayers" );
        playerTeam.addEntry( "§a" );
        playerTeam.setSuffix( Bukkit.getOnlinePlayers().size() + "" );


        objective.getScore( "  " ).setScore( 5 );

        // Map
        objective.getScore( "Map:" ).setScore( 4 );
        objective.getScore( "§b" ).setScore( 3 );
        Team mapTeam = scoreboard.registerNewTeam( "mapname" );
        mapTeam.addEntry( "§b" );
        if( KnockBackFFA.getInstance().getCurrentMap() == null ) {
            mapTeam.setSuffix( "null" );
        } else {
            mapTeam.setSuffix( KnockBackFFA.getInstance().getCurrentMap().getMapName() );
        }

        objective.getScore( " " ).setScore( 2 );

        if( KnockBackFFA.getInstance().getConfig().getBoolean( "settings.allowTeams" ) ) {
            objective.getScore( "§aTeams erlaubt" ).setScore( 1 );
        } else {
            objective.getScore( "§cTeams verboten" ).setScore( 1 );
        }

        objective.getScore( "" ).setScore( 0 );

        player.setScoreboard( scoreboard );
        setScoreboard.add( player );
    }

    public void updateOnlineCount( final Player player ) {
        if( !KnockBackFFA.getInstance().getConfig().getBoolean( "settings.setScoreboard" ) )
            return;

        if( !setScoreboard.contains( player ) )
            return;

        Scoreboard scoreboard = player.getScoreboard();
        if( scoreboard.getTeam( "onlineplayers" ) != null ) {
            scoreboard.getTeam( "onlineplayers" ).setSuffix( Bukkit.getOnlinePlayers().size() + "" );
        }
    }

    public void updateMapName( final Player player ) {
        if( !KnockBackFFA.getInstance().getConfig().getBoolean( "settings.setScoreboard" ) )
            return;

        if( !setScoreboard.contains( player ) )
            return;

        Scoreboard scoreboard = player.getScoreboard();
        if( scoreboard.getTeam( "mapname" ) != null ) {
            Team mapTeam = scoreboard.getTeam( "mapname" );
            if( KnockBackFFA.getInstance().getCurrentMap() == null ) {
                mapTeam.setSuffix( "null" );
            } else {
                mapTeam.setSuffix( KnockBackFFA.getInstance().getCurrentMap().getMapName() );
            }
        }
    }

    public void updateCoins( final Player player ) {
        if( !KnockBackFFA.getInstance().getConfig().getBoolean( "settings.setScoreboard" ) )
            return;

        if( !setScoreboard.contains( player ) )
            return;

        Scoreboard scoreboard = player.getScoreboard();
        if( scoreboard.getTeam( "playercoins" ) != null ) {
            scoreboard.getTeam( "playercoins" ).setSuffix( KnockBackFFA.getInstance().getCoinsMap().get( player ) + "" );
        }
    }

}
