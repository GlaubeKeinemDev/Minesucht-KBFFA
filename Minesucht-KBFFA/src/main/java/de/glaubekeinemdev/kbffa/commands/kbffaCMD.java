package de.glaubekeinemdev.kbffa.commands;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import de.glaubekeinemdev.kbffa.configuration.MapData;
import de.glaubekeinemdev.kbffa.configuration.SerializableLocation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
public class kbffaCMD implements CommandExecutor {

    private final HashMap<Player, MapData > settingUpMap = new HashMap<>(  );

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        if( !( sender instanceof Player ) ) {
            sender.sendMessage( KnockBackFFA.PREFIX + "Du musst ein Spieler sein" );
            return true;
        }
        final Player player = (Player)sender;

        if(!player.hasPermission( KnockBackFFA.getInstance().getConfig().getString( "settings.command_kbffa_permission" ) )) {
            player.sendMessage( KnockBackFFA.NO_PERMISSION );
            return true;
        }

        if( args.length > 2 ) {
            sendHelpMap( player );
            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase( "listmaps" )) {
                if(KnockBackFFA.getInstance().getConfiguration().getMaps().isEmpty()) {
                    player.sendMessage( KnockBackFFA.PREFIX + "Es wurde noch keine Maps eingerichtet" );
                } else {
                    player.sendMessage( KnockBackFFA.PREFIX + "Es wurden folgende Maps gefunden:" );

                    KnockBackFFA.getInstance().getConfiguration().getMaps().keySet().forEach( maps -> player.sendMessage( KnockBackFFA.PREFIX + "§e" + maps ) );
                }
                return true;
            }
        }

        if(args.length == 2) {
            final String mapName = args[1];

            if(args[0].equalsIgnoreCase( "addMap" )) {
                if(settingUpMap.containsKey( player ))
                    settingUpMap.remove( player );

                settingUpMap.put( player, new MapData( mapName ));
                player.sendMessage( KnockBackFFA.PREFIX + "Die Map §e" + mapName + " §7wurde erstellt" );
                player.sendMessage( KnockBackFFA.PREFIX + "Bitte setze die Positionen für §e" + mapName );
                return true;
            }
            if(args[0].equalsIgnoreCase( "setSpawn" )) {
                if(!settingUpMap.containsKey( player )) {
                    player.sendMessage( KnockBackFFA.PREFIX + "Du musst erst eine Map hinzufügen bevor du den Spawn setzt!" );
                    return true;
                }
                MapData mapData = settingUpMap.get( player );

                mapData.setSpawnLocation( SerializableLocation.toLocation( player.getLocation() ) );

                settingUpMap.remove( player);
                settingUpMap.put( player, mapData );

                player.sendMessage( KnockBackFFA.PREFIX + "Du hast den Spawn für die Map §e" + mapName + " §7gesetzt" );
                return true;
            }
            if(args[0].equalsIgnoreCase( "setProtection" )) {
                if(!settingUpMap.containsKey( player )) {
                    player.sendMessage( KnockBackFFA.PREFIX + "Du musst erst eine Map hinzufügen bevor du die Schutzhöhe setzt!" );
                    return true;
                }
                MapData mapData = settingUpMap.get( player );

                mapData.setProtectionHeight( player.getLocation().getY() );

                settingUpMap.remove( player);
                settingUpMap.put( player, mapData );

                player.sendMessage( KnockBackFFA.PREFIX + "Du hast die Schutzhöhe für die Map §e" + mapName + " §7gesetzt" );
                return true;
            }
            if(args[0].equalsIgnoreCase( "setDeath" )) {
                if(!settingUpMap.containsKey( player )) {
                    player.sendMessage( KnockBackFFA.PREFIX + "Du musst erst eine Map hinzufügen bevor du die Todeshöhe setzt!" );
                    return true;
                }
                MapData mapData = settingUpMap.get( player );

                mapData.setDeathHeight( player.getLocation().getY() );

                settingUpMap.remove( player);
                settingUpMap.put( player, mapData );

                player.sendMessage( KnockBackFFA.PREFIX + "Du hast die Todeshöhe für die Map §e" + mapName + " §7gesetzt" );
                return true;
            }
            if(args[0].equalsIgnoreCase( "saveMap" )) {
                if(!settingUpMap.containsKey( player )) {
                    player.sendMessage( KnockBackFFA.PREFIX + "Bitte erstelle zuerst eine neue Map! §8(§e/kbffa addMap <Name>§8)" );
                    return true;
                }
                MapData mapData = settingUpMap.get( player );

                if(mapData.getProtectionHeight() == null || mapData.getDeathHeight() == null || mapData.getSpawnLocation() == null) {
                    if( mapData.getSpawnLocation() == null ) {
                        player.sendMessage( KnockBackFFA.PREFIX + "Du musst erst den Spawn setzen, bevor du Map speicherst!" );
                    }
                    if( mapData.getDeathHeight() == null ) {
                        player.sendMessage( KnockBackFFA.PREFIX + "Du musst erst die Todeshöhe setzen, bevor du Map speicherst!" );
                    }
                    if( mapData.getProtectionHeight() == null ) {
                        player.sendMessage( KnockBackFFA.PREFIX + "Du musst erst die Schutzhöhe setzen, bevor du Map speicherst!" );
                    }
                    return true;
                }

                settingUpMap.remove( player );
                KnockBackFFA.getInstance().getConfiguration().getMaps().put( mapName, mapData );
                try {
                    KnockBackFFA.getInstance().getConfiguration().save( KnockBackFFA.getInstance().getConfigFile() );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }

                player.sendMessage( KnockBackFFA.PREFIX + "Du hast die Map §e" + mapName + " §7erfolgreich gespeichert" );
                player.sendMessage( KnockBackFFA.PREFIX + "Um diese Änderung wirksam zu machen, starte den Server neu" );
                return true;
            }
            if(args[0].equalsIgnoreCase( "deleteMap" )) {
                if(!KnockBackFFA.getInstance().getConfiguration().getMaps().containsKey( mapName )) {
                    player.sendMessage( KnockBackFFA.PREFIX + "Es existiert keine Map mit dem Namen §e" + mapName );
                    return true;
                }
                KnockBackFFA.getInstance().getConfiguration().getMaps().remove( mapName );
                try {
                    KnockBackFFA.getInstance().getConfiguration().save( KnockBackFFA.getInstance().getConfigFile() );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                player.sendMessage( KnockBackFFA.PREFIX + "Du hast die Map §e" + mapName + "§7 gelöscht" );
                return true;
            }
            if(args[0].equalsIgnoreCase( "forcemap" )) {
                if(!KnockBackFFA.getInstance().getConfiguration().getMaps().containsKey( mapName )) {
                    player.sendMessage( KnockBackFFA.PREFIX + "Es existiert keine Map mit dem Namen §e" + mapName );
                    return true;
                }
                if(KnockBackFFA.getInstance().getMapPicker().getSeconds() < 30) {
                    player.sendMessage( KnockBackFFA.PREFIX + "Es ist zu spät um die Map zu wechseln" );
                    return true;
                }
                if(KnockBackFFA.getInstance().getMapPicker().getForceMap() != null) {
                    player.sendMessage( KnockBackFFA.PREFIX + "Es wurde bereits die Map gewechselt" );
                    return true;
                }
                final MapData mapData = KnockBackFFA.getInstance().getConfiguration().getMaps().get( mapName );
                KnockBackFFA.getInstance().getMapPicker().startForcemap( mapData );

                player.sendMessage( KnockBackFFA.PREFIX + "Du hast die Map auf §e" + mapName + " gesetzt!" );
                return true;
            }

        }

        sendHelpMap( player );
        return false;
    }

    private void sendHelpMap(final Player player) {
        player.sendMessage( KnockBackFFA.PREFIX + "Du benutzt das KBFFA Plugin von GlaubeKeinemDev" );
        player.sendMessage( KnockBackFFA.PREFIX + "" );
        player.sendMessage( KnockBackFFA.PREFIX + "/kbffa addMap <MapName>" );
        player.sendMessage( KnockBackFFA.PREFIX + "/kbffa setSpawn <MapName>" );
        player.sendMessage( KnockBackFFA.PREFIX + "/kbffa setProtection <MapName>" );
        player.sendMessage( KnockBackFFA.PREFIX + "/kbffa setDeath <MapName>" );
        player.sendMessage( KnockBackFFA.PREFIX + "/kbffa saveMap <MapName>" );
        player.sendMessage( KnockBackFFA.PREFIX + "" );
        player.sendMessage( KnockBackFFA.PREFIX + "/kbffa forcemap <MapName>" );
        player.sendMessage( KnockBackFFA.PREFIX + "/kbffa deleteMap <MapName>" );
        player.sendMessage( KnockBackFFA.PREFIX + "/kbffa listMaps" );

    }
}
