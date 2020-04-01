package de.glaubekeinemdev.kbffa.listener;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import de.glaubekeinemdev.kbffa.commands.buildCMD;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
public class InteractListener implements Listener {

    @EventHandler
    public void onInteract( final PlayerInteractEvent event ) {
        final Player player = event.getPlayer();

        if( buildCMD.BUILDPLAYERS.contains( player ) ) {
            event.setCancelled( false );
        } else {
            if( event.getItem() != null && event.getItem().getType() != null &&
                    ( event.getItem().getType() == Material.FISHING_ROD || event.getItem().getType() == Material.POTION || event.getItem().getType() == Material.BOW ) ) {
                event.setCancelled( false );
                return;
            }
            event.setCancelled( true );
        }


        if( event.getItem() == null )
            return;

        if( event.getItem().getType() == null )
            return;

        if( event.getItem().getItemMeta() == null )
            return;

        if( event.getItem().getItemMeta().getDisplayName() == null )
            return;

        if( event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK ) {
            final String displayName = event.getItem().getItemMeta().getDisplayName();

            if( displayName.equalsIgnoreCase( KnockBackFFA.getInstance().getInventoryManager().getShopItem().getItemMeta().getDisplayName() ) ) {
                KnockBackFFA.getInstance().getInventoryManager().openShop( player );
                return;
            }

            if( displayName.equalsIgnoreCase( KnockBackFFA.getInstance().getInventoryManager().getStatsItemDisplayName() ) ) {
                KnockBackFFA.getInstance().getInventoryManager().openStatsInventory( player );
            }
        }
    }

}
