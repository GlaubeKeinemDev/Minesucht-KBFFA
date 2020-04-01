package de.glaubekeinemdev.kbffa.listener;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
public class EntityDamageListener implements Listener {

    @EventHandler
    public void onDamage1( final EntityDamageEvent event ) {
        if( KnockBackFFA.getInstance( ).getCurrentMap( ) != null && event.getEntity( ).getLocation( ).getY( ) > KnockBackFFA.getInstance( ).getCurrentMap( ).getProtectionHeight( ) )
            event.setCancelled( true );

        if( event.getCause( ) == EntityDamageEvent.DamageCause.FALL )
            event.setCancelled( true );

    }

    @EventHandler
    public void onDamage2( final EntityDamageByEntityEvent event ) {
        if( !( event.getDamager( ) instanceof Player ) )
            return;

        if( !( event.getEntity( ) instanceof Player ) )
            return;

        final Player damager = ( Player ) event.getDamager( );
        final Player target = ( Player ) event.getEntity( );

        if( KnockBackFFA.getInstance( ).getCurrentMap( ) != null ) {
            if( damager.getLocation( ).getY( ) > KnockBackFFA.getInstance( ).getCurrentMap( ).getProtectionHeight( ) || target.getLocation( ).getY( ) > KnockBackFFA.getInstance( ).getCurrentMap( ).getProtectionHeight( ) ) {
                event.setCancelled( true );
                return;
            }

            event.setCancelled( false );

            if( KnockBackFFA.getInstance( ).getLastDamager( ).containsKey( target ) )
                KnockBackFFA.getInstance( ).getLastDamager( ).remove( target );

            KnockBackFFA.getInstance( ).getLastDamager( ).put( target, damager );
            target.setHealth( 20 );
        } else {
            event.setCancelled( true );
        }
    }


}
