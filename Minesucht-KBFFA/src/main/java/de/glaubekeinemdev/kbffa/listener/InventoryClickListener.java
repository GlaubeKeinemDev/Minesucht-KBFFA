package de.glaubekeinemdev.kbffa.listener;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import de.glaubekeinemdev.kbffa.commands.buildCMD;
import de.glaubekeinemdev.kbffa.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick( final InventoryClickEvent event ) {
        final Player player = ( Player ) event.getWhoClicked( );

        if( buildCMD.BUILDPLAYERS.contains( player ) ) {
            event.setCancelled( false );
            return;
        }

        if(event.getCurrentItem() != null && event.getCurrentItem().getType() != null) {
            if(event.getCurrentItem().getType() == Material.CHEST || event.getCurrentItem().getType() == Material.SKULL || event.getCurrentItem().getType() == Material.SKULL_ITEM) {
                event.setCancelled( true );
            }
        }

        if( event.getInventory( ) == null )
            return;

        if( event.getCurrentItem( ) == null )
            return;

        if( event.getCurrentItem( ).getType( ) == null )
            return;

        if( event.getCurrentItem( ).getItemMeta( ) == null )
            return;

        if( event.getCurrentItem( ).getItemMeta( ).getDisplayName( ) == null )
            return;

        if( event.getInventory( ).getName( ).equalsIgnoreCase( KnockBackFFA.getInstance( ).getInventoryManager( ).getShopInventory( ).getTitle( ) ) ) {
            event.setCancelled( true );

            final String displayName = event.getCurrentItem( ).getItemMeta( ).getDisplayName( );

            if(!displayName.contains( "-" ))
                return;

            final String boughtItem = displayName.split( "-" )[ 0 ].replace( " ", "" ).replace( "§f", "" );

            final int currentItemPrice = Integer.parseInt( displayName.split( "-" )[ 1 ]
                    .replace( "§6", "" )
                    .replace( "Coins", "" )
                    .replace( "Coin", "" )
                    .replace( " ", "" ) );


            if( KnockBackFFA.getInstance( ).getCoinsMap( ).get( player ) < currentItemPrice ) {
                player.sendMessage( KnockBackFFA.PREFIX + "Du hast nicht genügend Coins um dir das zu kaufen§8!" );
                return;
            }

            int playerCoins = KnockBackFFA.getInstance( ).getCoinsMap( ).get( player ) - currentItemPrice;
            KnockBackFFA.getInstance( ).getCoinsMap( ).remove( player );
            KnockBackFFA.getInstance( ).getCoinsMap( ).put( player, playerCoins );
            KnockBackFFA.getInstance( ).getBoardManager( ).updateCoins( player );

            if(event.getCurrentItem().getEnchantments().isEmpty()) {
                player.getInventory( ).addItem( new ItemBuilder(event.getCurrentItem().getType(), 1,
                        (short) event.getCurrentItem().getData().getData(), "§e" + boughtItem).create() );
            } else {
                ItemStack itemStack = new ItemBuilder(event.getCurrentItem().getType(), 1,
                        (short) event.getCurrentItem().getData().getData(), "§e" + boughtItem).addEnchantments( event.getCurrentItem().getEnchantments() ).create();

                player.getInventory().addItem( itemStack );
            }

            player.sendMessage( KnockBackFFA.PREFIX + "§7Du hast dir das Item §e" + boughtItem + " §7gekauft§8!" );
            return;
        }

    }

}
