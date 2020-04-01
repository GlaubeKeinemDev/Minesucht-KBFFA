package de.glaubekeinemdev.kbffa.listener;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerItemConsumeListener implements Listener {

    @EventHandler
    public void onConsume( final PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();

        if(event.getItem() != null && event.getItem().getType() != null && event.getItem().getType() == Material.POTION) {

            new BukkitRunnable() {
                @Override
                public void run( ) {
                    player.getInventory().remove( Material.GLASS_BOTTLE );
                }
            }.runTaskLater( KnockBackFFA.getInstance(), 3 );

        }

    }

}
