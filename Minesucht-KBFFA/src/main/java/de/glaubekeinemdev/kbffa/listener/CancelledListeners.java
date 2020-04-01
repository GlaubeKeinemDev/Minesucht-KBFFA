package de.glaubekeinemdev.kbffa.listener;

import de.glaubekeinemdev.kbffa.commands.buildCMD;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
public class CancelledListeners implements Listener {

    @EventHandler
    public void onCraft( final CraftItemEvent event ) {
        event.setCancelled( true );
    }

    @EventHandler
    public void onWeather( final WeatherChangeEvent event ) {
        event.setCancelled( true );
    }

    @EventHandler
    public void onAchivement( final PlayerAchievementAwardedEvent event ) {
        event.setCancelled( true );
    }

    @EventHandler
    public void onFood( final FoodLevelChangeEvent event ) {
        event.setCancelled( true);
    }

    @EventHandler
    public void onDrop( final PlayerDropItemEvent event ) {
        event.setCancelled( true );
    }

    @EventHandler
    public void onPickup( final PlayerPickupItemEvent event ) {
        event.setCancelled( true );
    }

    @EventHandler
    public void onBreak( final BlockBreakEvent event ) {
        if( buildCMD.BUILDPLAYERS.contains( event.getPlayer() )) {
            event.setCancelled( false );
        } else {
            event.setCancelled( true );
        }
    }

    @EventHandler
    public void onPlace( final BlockPlaceEvent event ) {
        if( buildCMD.BUILDPLAYERS.contains( event.getPlayer() )) {
            event.setCancelled( false );
        } else {
            event.setCancelled( true );
        }
    }
}
