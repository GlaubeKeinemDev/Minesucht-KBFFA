package de.glaubekeinemdev.kbffa.listener;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin( final PlayerJoinEvent event ) {
        final Player player = event.getPlayer();

        if(KnockBackFFA.getInstance().getConfig().getString( "settings.joinMessage" ).equalsIgnoreCase( "null" )) {
            event.setJoinMessage( null );
        } else {
            final String message = KnockBackFFA.getInstance().getConfig().getString( "settings.joinMessage" )
                    .replace( "&", "§" ).replace( "%prefix%", KnockBackFFA.PREFIX ).replace( "%player%", player.getName() );

            event.setJoinMessage( message );
        }


        KnockBackFFA.getInstance().getCoinsMap().put( player, 0 );

        KnockBackFFA.getInstance().getInventoryManager().setJoinInventory( player );
        if(KnockBackFFA.getInstance().getConfig().getBoolean( "settings.setScoreboardDelayed" )) {
            new BukkitRunnable() {
                @Override
                public void run( ) {
                    KnockBackFFA.getInstance().getBoardManager().setScoreboard( player );
                }
            }.runTaskLaterAsynchronously( KnockBackFFA.getInstance(), 20 );
        } else {
            KnockBackFFA.getInstance().getBoardManager().setScoreboard( player );
        }

        if(KnockBackFFA.getInstance().getDatabaseHandler() != null)
            KnockBackFFA.getInstance().getDatabaseHandler().loadPlayerIntoCache( player );

        if(KnockBackFFA.getInstance().getCurrentMap() == null) {
            player.sendMessage( KnockBackFFA.PREFIX + "Es wurde noch keine Map eingerichtet!" );
        } else {
            final Location location = KnockBackFFA.getInstance().getCurrentMap().getSpawnLocation().toLocation();

            player.teleport( location );

            if(KnockBackFFA.getInstance().getConfig().getBoolean( "settings.bypassMultiverse" )) {
                new BukkitRunnable() {
                    @Override
                    public void run( ) {
                        player.teleport( location );
                    }
                }.runTaskLater( KnockBackFFA.getInstance(), 5 );
            }
        }

        Bukkit.getOnlinePlayers().forEach( players -> KnockBackFFA.getInstance().getBoardManager().updateOnlineCount( players ) );

    }

}
