package wtf.beatrice.uhccore.commands.uhccommands;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;


public class GlowingCommand {

    public static void setGlowing(CommandSender sender, String[] args, UhcCore plugin) {

        String text = args[1];
        if(text.equals("true")){
            Cache.glowing = true;
            for(String playerName : Cache.playerTeam.keySet())
            {
                Player player = plugin.getServer().getPlayer(playerName);
                if(player==null){continue;}
                player.setGlowing(true);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                plugin.getServer().broadcastMessage("All players glow now!");
            }
        }else if (text.equals("false")){

            Cache.glowing = false;
            for(String playerName : Cache.playerTeam.keySet())
            {
                Player player = plugin.getServer().getPlayer(playerName);
                if(player==null){continue;}
                player.setGlowing(false);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                plugin.getServer().broadcastMessage("Players are no longer glowing.");
            }
        }else{
            sender.sendMessage("Wrong parameter");
        }

    }
}
