package de.glaubekeinemdev.kbffa.listener;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit( final PlayerQuitEvent event ) {
        final Player player = event.getPlayer();

        if(KnockBackFFA.getInstance().getConfig().getString( "settings.quitMessage" ).equalsIgnoreCase( "null" )) {
            event.setQuitMessage( null );
        } else {
            final String message = KnockBackFFA.getInstance().getConfig().getString( "settings.quitMessage" )
                    .replace( "&", "§" ).replace( "%prefix%", KnockBackFFA.PREFIX ).replace( "%player%", player.getName() );

            event.setQuitMessage( message );
        }

        if(KnockBackFFA.getInstance().getDatabaseHandler() != null) {
            if( KnockBackFFA.getInstance().getDatabaseHandler().getStatsCache().containsKey( player.getUniqueId() ) ) {
                KnockBackFFA.getInstance().getDatabaseHandler().insertStatsToDatabase(
                        player.getUniqueId(), KnockBackFFA.getInstance().getDatabaseHandler().getStatsCache().get( player.getUniqueId() ) );
            }
        }

        Bukkit.getOnlinePlayers().forEach( players -> KnockBackFFA.getInstance().getBoardManager().updateOnlineCount( players ) );
    }
}
