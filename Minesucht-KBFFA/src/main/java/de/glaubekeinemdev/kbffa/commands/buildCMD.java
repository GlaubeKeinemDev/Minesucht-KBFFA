package de.glaubekeinemdev.kbffa.commands;

import de.glaubekeinemdev.kbffa.KnockBackFFA;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
public class buildCMD implements CommandExecutor {

    public static final ArrayList<Player> BUILDPLAYERS = new ArrayList<>(  );

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        if( !( sender instanceof Player ) ) {
            sender.sendMessage( KnockBackFFA.PREFIX + "Du musst ein Spieler sein" );
            return true;
        }
        final Player player = (Player) sender;

        if(!player.hasPermission( KnockBackFFA.getInstance().getConfig().getString( "settings.command_build_permission" ) )) {
            player.sendMessage( KnockBackFFA.NO_PERMISSION );
            return true;
        }

        if(args.length > 1) {
            player.sendMessage( KnockBackFFA.PREFIX + "/build" );
            return true;
        }

        if(BUILDPLAYERS.contains( player )) {
            BUILDPLAYERS.remove( player );

            KnockBackFFA.getInstance().getInventoryManager().setJoinInventory( player );

            if(KnockBackFFA.getInstance().getCurrentMap() != null)
                player.teleport( KnockBackFFA.getInstance().getCurrentMap().getSpawnLocation().toLocation() );

            player.sendMessage( KnockBackFFA.PREFIX + "Du hast den Baumodus verlassen" );
        } else {
            BUILDPLAYERS.add( player );

            player.setGameMode( GameMode.CREATIVE );

            player.sendMessage( KnockBackFFA.PREFIX + "Du hast den Baumodus betreten" );
        }

        return false;
    }
}
