package de.glaubekeinemdev.kbffa.listener;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
public class ServerListPingListener implements Listener {

    @EventHandler
    public void onPing( final ServerListPingEvent event ) {

        if( KnockBackFFA.getInstance().getCurrentMap() != null ) {
            event.setMotd( KnockBackFFA.getInstance().getCurrentMap().getMapName() );
        }

    }

}
