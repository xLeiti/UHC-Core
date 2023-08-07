package wtf.beatrice.uhccore.commands.uhccommands;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;


public class SpectateCommand {

    public static void spectate(CommandSender sender) {

        World spawnWorld = sender.getServer().getWorld(Cache.uhcWorlds.get(0));
        Location location = new Location(spawnWorld, 0, spawnWorld.getHighestBlockYAt(0,0), 0);
        Scoreboard board = sender.getServer().getScoreboardManager().getMainScoreboard();

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players are able to spectate.");
            return;
        }

        if (!Cache.game_running) {
            sender.sendMessage("Game hasn't started yet.");
            return;
        }

        if (Cache.playerTeam.containsKey(sender.getName())) {
            sender.sendMessage("You need to be dead to spectate.");
            return;
        }

        for (int i = 0; i < Cache.totalTeams; i++) {
            Team team = board.getTeam(Cache.teamNames.get(i));
            if (team!=null && team.hasEntry(sender.getName())) {
                for (String mates : team.getEntries()) {
                    if (Cache.playerTeam.containsKey(mates)) {
                        sender.sendMessage("Not all players on your team are dead yet. You aren't allowed to spectate.");
                        sender.sendMessage("Ask your teammate to share their screen over discord.");
                        return;
                    }
                }
            }
        }
        //player either isn't a member of any team, or his entire team is dead
        setSpectator((Player)sender, location);

    }

    private static void setSpectator(Player player, Location location) {
        UhcCore.getInstance().getServer().getScheduler().runTask(UhcCore.getInstance(), () ->
        {
            player.sendMessage("Gamemode changed to spectator.");
            player.sendMessage("Don't share any information with the playing teams!");
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(location);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        });
    }
}
