package wtf.beatrice.uhccore.commands.uhccommands;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;
import wtf.beatrice.uhccore.utils.UhcUtils;

public class TeleportWorldCommand {

    public static void teleportWorld(CommandSender sender, String[] args) {

        if((sender instanceof Player)){
            Player player = (Player) sender;
            for(World world : Bukkit.getWorlds()){
                if(world.getName().equals(args[1])){
                    Location spawn = new Location(world, 0, 150, 0);
                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(spawn);
                    return;
                }
            }
            sender.sendMessage("Couldn't find a world with that name.");
            sender.sendMessage("Following worlds are available:");
            for(World world : Bukkit.getWorlds()){
                sender.sendMessage(world.getName());
            }
        }else{
            sender.sendMessage("Only players can teleport to a world.");
        }


    }
}
