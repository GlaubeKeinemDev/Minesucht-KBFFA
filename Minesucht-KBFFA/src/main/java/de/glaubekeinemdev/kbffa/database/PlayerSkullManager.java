package de.glaubekeinemdev.kbffa.database;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import de.glaubekeinemdev.kbffa.utils.fetchers.GameProfileBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
@Getter
public class PlayerSkullManager {

    private final HashMap< UUID, String > skinCache = new HashMap<>();

    public ItemStack getSkullSync( final Player player ) {
        if( !skinCache.containsKey( player.getUniqueId() ) ) {
            skinCache.put( player.getUniqueId(), KnockBackFFA.getInstance().getValue( player ) );
        }

        ItemStack stack = new ItemStack( Material.SKULL_ITEM, 1, ( short ) 3 );
        ItemStack result = Bukkit.getUnsafe().modifyItemStack( stack,
                "{SkullOwner:{Id:" + player.getUniqueId().toString() + ",Properties:{textures:[{Value:\"" + skinCache.get( player.getUniqueId() ) + "\"}]}}}" );
        return result;
    }

    public ItemStack getSkullSync( final UUID uuid, final String value ) {
        if( !skinCache.containsKey( uuid ) ) {
            skinCache.put( uuid, value );
        }

        ItemStack stack = new ItemStack( Material.SKULL_ITEM, 1, ( short ) 3 );
        ItemStack result = Bukkit.getUnsafe().modifyItemStack( stack,
                "{SkullOwner:{Id:" + uuid + ",Properties:{textures:[{Value:\"" + value + "\"}]}}}" );
        return result;
    }

    public ItemStack getCachedSkullSync( final UUID uuid ) {
        final ItemStack stack = new ItemStack( Material.SKULL_ITEM, 1, ( short ) 3 );
        String value = "";

        if( skinCache.containsKey( uuid ) ) {
            value = skinCache.get( uuid );
        } else {
            try {
                String inCode_value = KnockBackFFA.getInstance().getValue( GameProfileBuilder.fetch( uuid ) );

                value = inCode_value;
                skinCache.put( uuid, inCode_value );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        final ItemStack result = Bukkit.getUnsafe().modifyItemStack( stack,
                "{SkullOwner:{Id:" + uuid + ",Properties:{textures: [{Value:\"" + value + "\"}]}}}" );

        return result;
    }

    public void getCachedSkullAsync( final UUID uuid, final Consumer< ItemStack > consumer ) {
        KnockBackFFA.getInstance().getService().execute( ( ) -> consumer.accept( getCachedSkullSync( uuid ) ) );
    }

    public void getSkullAsync( final Player player, final Consumer< ItemStack > consumer ) {
        KnockBackFFA.getInstance().getService().execute( ( ) -> consumer.accept( getSkullSync( player ) ) );
    }

    public void getSkullAsync( final UUID uuid, final String value, final Consumer< ItemStack > consumer ) {
        KnockBackFFA.getInstance().getService().execute( ( ) -> consumer.accept( getSkullSync( uuid, value ) ) );
    }

}
