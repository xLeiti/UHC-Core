package wtf.beatrice.uhccore.commands.uhccommands;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;
import wtf.beatrice.uhccore.utils.UhcUtils;


import java.util.logging.Level;

public class RemovePlayerCommand {

    public static void removePlayer(CommandSender sender, String[] args, UhcCore plugin) {

        String name = args[1];

        if (name != null) {


            if (Cache.playerTeam.containsKey(name)) {

                // Load the to be removed player's team number.
                int thisPlayerTeamNumber = Cache.playerTeam.get(name);
                // Load the to be removed player's team name.
                String thisPlayerTeamName = Cache.teamNames.get(thisPlayerTeamNumber);
                //remove player from the team
                Cache.playerTeam.remove(name);
                sender.sendMessage("Player " + name + " was successfully removed from the UHC!");

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () ->

                {
                    // Update the total number of players in each team, and the total number of alive teams.
                    UhcUtils.updatePlayersPerTeam();

                    // Check how many players are left in the to removed player's team.
                    int thisPlayerTeamPlayers = Cache.playersPerTeam.get(thisPlayerTeamNumber);

                    // Run this task Sync, because we need to access the API, and also delay it by 1 second.
                    plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    {

                        int playingPlayers = Cache.playerTeam.size();
                        for (Player p : plugin.getServer().getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                        plugin.getServer().broadcastMessage(name + "§7 of team §e" + thisPlayerTeamName + "§7 got removed!");
                        plugin.getServer().broadcastMessage("§7In team " + thisPlayerTeamName + "§7 remain §e" + thisPlayerTeamPlayers + "§7 players.");
                        plugin.getServer().broadcastMessage("§7In total remain §e" + playingPlayers + "§7 players, in §e" + Cache.playingTeams + "§7 teams.");

                        //In case the last player left
                        if (Cache.playingTeams <= 1) {
                            Cache.allowMovement = false;

                            plugin.getServer().broadcastMessage("§6The UHC is over!");

                            int winningTeam = 0;
                            for (int i = 0; i < Cache.totalTeams; i++) {
                                if (Cache.playersPerTeam.get(i) > 0) {
                                    winningTeam = i;
                                }
                            }

                            String teamName = Cache.teamNames.get(winningTeam) + "§r";
                            plugin.getServer().broadcastMessage("§6The winners are: " + teamName);


                            for (Player currentPlayer : plugin.getServer().getOnlinePlayers()) {
                                currentPlayer.teleport(Cache.spawn);
                                // Clear his inventory and give him the Teams selector item.
                                UhcUtils.giveTeamsSelectorItem(currentPlayer);
                                plugin.getLogger().log(Level.INFO, "UHC Finished!");
                                currentPlayer.sendTitle("Team " + teamName + " won!", "", 20 * 2, 20 * 10, 20 * 2);

                                for (Player hiddenPlayer : plugin.getServer().getOnlinePlayers()) {
                                    currentPlayer.hidePlayer(plugin, hiddenPlayer);
                                }
                            }
                        }
                    }, 20L);
                });


            } else {
                sender.sendMessage("Player " + name + " is not part of an active Team");
            }
        } else {
            sender.sendMessage("You need to add a player name");
        }

    }
}
