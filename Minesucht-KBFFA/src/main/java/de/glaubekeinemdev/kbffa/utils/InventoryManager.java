package de.glaubekeinemdev.kbffa.utils;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import de.glaubekeinemdev.kbffa.database.PlayerStats;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
@Getter
public class InventoryManager {

    private ItemStack defaultKnockBackStick;
    private ItemStack shopItem;

    private ItemStack extra_Doping;
    private ItemStack extra_KnockBackStick;
    private ItemStack extra_GrapplingHook;
    private ItemStack extra_Sniper;
    private ItemStack extra_Munition;

    private Inventory shopInventory;

    private String statsItemDisplayName = "§eStats";
    private String statsInventoryTitle = "§aStats";

    private Inventory statsInventory;

    public InventoryManager( ) {
        defaultKnockBackStick = new ItemBuilder( Material.STICK, 1, 0, "Knüppel" )
                .addUnsafeEnchantment( Enchantment.KNOCKBACK, 2 ).create();
        shopItem = new ItemBuilder( Material.CHEST, 1, 0, "Shop" ).create();


        extra_Doping = new ItemBuilder( Material.POTION, 1, 8226, "Doping §7- §62 Coins" ).create();
        extra_KnockBackStick = new ItemBuilder( Material.STICK, 1, 0, "Knüppel II §7- §68 Coins" )
                .addUnsafeEnchantment( Enchantment.KNOCKBACK, 4 ).create();
        extra_GrapplingHook = new ItemBuilder( Material.FISHING_ROD, 1, 0, "Spielerangel §7- §65 Coins" ).create();
        extra_Sniper = new ItemBuilder( Material.BOW, 1, 0, "Sniper §7- §65 Coins" )
                .addUnsafeEnchantment( Enchantment.ARROW_KNOCKBACK, 1 ).create();
        extra_Munition = new ItemBuilder( Material.ARROW, 1, 0, "Munition §7- §61 Coin" ).create();

        shopInventory = Bukkit.createInventory( null, 9, "§eShop" );
        shopInventory.setItem( 0, extra_Doping );
        shopInventory.setItem( 2, extra_KnockBackStick );
        shopInventory.setItem( 4, extra_GrapplingHook );
        shopInventory.setItem( 6, extra_Sniper );
        shopInventory.setItem( 8, extra_Munition );
    }

    public void openShop( final Player player ) {
        player.openInventory( shopInventory );
    }

    public void loadStatsInventory( ) {
        Inventory inventory = Bukkit.createInventory( null, 18, statsInventoryTitle );

        for ( UUID uuid : KnockBackFFA.getInstance().getDatabaseHandler().getTop10Players().keySet()) {
            final PlayerStats playerStats = KnockBackFFA.getInstance().getDatabaseHandler().getTop10Players().get( uuid );
            final String name = KnockBackFFA.getInstance().getDatabaseHandler().getName( uuid );
            final ItemStack playerSkull = KnockBackFFA.getInstance().getPlayerSkullManager().getCachedSkullSync( uuid );
            final int position = KnockBackFFA.getInstance().getDatabaseHandler().getPlayerPositionInRanking().get( uuid );

            final ItemStack itemStack = playerSkull;

            final ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName( "§7-- §6" + name + "§7 --" );

            final List< String > lore = new ArrayList<>();
            lore.add( "§7Position im Ranking: §e" +  position);
            lore.add( "§7Kills: §e" + playerStats.getKills() );
            lore.add( "§7K/D: §e" + playerStats.getKD() );

            itemMeta.setLore( lore );

            itemStack.setItemMeta( itemMeta );

            inventory.setItem( (position -1), itemStack );
        }

        statsInventory = inventory;
    }


    public void openStatsInventory( final Player player ) {
        Inventory inventory = statsInventory;

        final PlayerStats playerStats = KnockBackFFA.getInstance().getDatabaseHandler().getStats( player );
        final ItemStack playerSkull = KnockBackFFA.getInstance().getPlayerSkullManager().getSkullSync( player );
        final int postion = ( KnockBackFFA.getInstance().getDatabaseHandler().getPlayerPositionInRanking().getOrDefault( player.getUniqueId(), -1 ) );

        final ItemStack itemStack = playerSkull;
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName( "§7-= §6Deine KFFA Stats§7 =-" );

        final List< String > lore = new ArrayList<>();
        lore.add( "§7Position im Ranking: §e" + postion );
        lore.add( "§7Kills: §e" + playerStats.getKills() );
        lore.add( "§7Deaths: §e" + playerStats.getDeaths() );
        lore.add( "§7K/D: §e" + playerStats.getKD() );
        itemMeta.setLore( lore );

        itemStack.setItemMeta( itemMeta );

        inventory.setItem( 17, itemStack );

        player.openInventory( inventory );
    }

    public void setJoinInventory( final Player player ) {
        player.setAllowFlight( false );
        player.setFlying( false );
        player.setHealth( 20 );
        player.setFoodLevel( 20 );
        player.setGameMode( GameMode.SURVIVAL );
        player.getInventory().setArmorContents( null );
        player.getInventory().clear();


        if( KnockBackFFA.getInstance().getConfig().getBoolean( "database.useMySQL" ) ) {
            KnockBackFFA.getInstance().getPlayerSkullManager().getSkullAsync( player, playerHead -> {
                ItemStack itemStack = playerHead;
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName( statsItemDisplayName );
                itemStack.setItemMeta( itemMeta );

                player.getInventory().setItem( 8, itemStack );
            } );
            player.getInventory().setItem( 7, shopItem );
        } else {
            player.getInventory().setItem( 8, shopItem );
        }

        player.getInventory().setItem( 0, defaultKnockBackStick );
    }
}
