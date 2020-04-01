package de.glaubekeinemdev.kbffa.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
public class GrapplingHookListener implements Listener {

    @EventHandler( priority = EventPriority.NORMAL, ignoreCancelled = true )
    public void onFish( PlayerFishEvent event ) {
        final Player player = event.getPlayer();
        final Fish fish = event.getHook();


        if( ( ( event.getState() == PlayerFishEvent.State.IN_GROUND )
                || ( event.getState().equals( PlayerFishEvent.State.CAUGHT_ENTITY ) )
                || ( event.getState().equals( PlayerFishEvent.State.FAILED_ATTEMPT ) ) )
                && ( Bukkit.getWorld( event.getPlayer().getWorld().getName() ).getBlockAt( fish.getLocation().getBlockX(),
                fish.getLocation().getBlockY() - 1, fish.getLocation().getBlockZ() )
                .getType() != Material.AIR ) ) {


            Location playerLocation = player.getLocation();
            Location hookLocation = event.getHook().getLocation();

            playerLocation.setY( playerLocation.getY() + 0.5D );
            player.teleport( playerLocation );
            player.playSound( player.getLocation(), Sound.ENDERDRAGON_WINGS, 1.0F, 1.0F );

            final double distance = hookLocation.distance( playerLocation );

            final double vector_x = ( 1.0D + 0.07D * distance ) * ( hookLocation.getX() - playerLocation.getX() ) / distance;
            final double vector_y = ( 1.0D + 0.03D * distance ) * ( hookLocation.getY() - playerLocation.getY() ) / distance - 0.5D * (-0.08D) * distance;
            final double vector_z = ( 1.0D + 0.07D * distance ) * ( hookLocation.getZ() - playerLocation.getZ() ) / distance;

            final Vector vector = player.getVelocity();
            vector.setX( vector_x );
            vector.setY( vector_y );
            vector.setZ( vector_z );

            player.setVelocity( vector );
        }
    }

}
