package de.glaubekeinemdev.kbffa.utils;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import de.glaubekeinemdev.kbffa.configuration.MapData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
@Getter
public class MapPicker {

    private int seconds;

    private int defaultSeconds;

    private MapData forceMap;

    private final ArrayList<Integer> broadcasted = new ArrayList<>(  );

    public MapPicker( int defaultMinute ) {
        this.seconds = defaultMinute * 60 - 2;
        this.defaultSeconds = (defaultMinute * 60) - 2;
    }

    public void startPicking( ) {
        KnockBackFFA.getInstance().getService().execute( () -> new BukkitRunnable() {
            @Override
            public void run( ) {
                if(seconds == 0) {
                    final MapData newMap = (forceMap != null ? forceMap : KnockBackFFA.getInstance().getRandomMap());

                    KnockBackFFA.getInstance().setCurrentMap( newMap );

                    Bukkit.getOnlinePlayers().forEach( players -> {
                        players.teleport( newMap.getSpawnLocation().toLocation());
                        players.playSound( players.getLocation(), Sound.LEVEL_UP, 3, 2 );
                        KnockBackFFA.getInstance().getBoardManager().updateMapName( players );
                    });

                    forceMap = null;
                    seconds = defaultSeconds;
                    return;
                }

                if(seconds >= 60) {
                    final int minute = seconds / 60;


                    if(!broadcasted.contains( minute)) {
                        Bukkit.broadcastMessage( KnockBackFFA.PREFIX + "Neue Map in §e" + minute + " §7" + ( minute == 1 ? "Minute" : "Minuten" ) );
                        broadcasted.add( minute );
                    }
                } else {

                    if(seconds == 30) {
                        Bukkit.broadcastMessage( KnockBackFFA.PREFIX + "Neue Map in §e" + seconds + " §7Sekunden" );
                    }
                    if(seconds == 20) {
                        Bukkit.broadcastMessage( KnockBackFFA.PREFIX + "Neue Map in §e" + seconds + " §7Sekunden" );
                    }
                    if(seconds == 10) {
                        Bukkit.broadcastMessage( KnockBackFFA.PREFIX + "Neue Map in §e" + seconds + " §7Sekunden" );
                    }
                    if(seconds <= 5) {
                        Bukkit.broadcastMessage( KnockBackFFA.PREFIX + "Neue Map in §e" + seconds + " §7" + ( seconds == 1 ? "Sekunde" : "Sekunden" ) );
                        Bukkit.getOnlinePlayers().forEach( players -> players.playSound( players.getLocation(), Sound.WOOD_CLICK, 3, 2 ) );
                    }

                }

                seconds--;
            }
        }.runTaskTimer( KnockBackFFA.getInstance(), 0, 20 ) );
    }

    public void startForcemap(final MapData mapData) {
        seconds = 30;
        this.forceMap = mapData;
    }

}
