package wtf.beatrice.uhccore.commands.uhccommands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;
import wtf.beatrice.uhccore.utils.UhcUtils;

public class RevivePlayerCommand {

    public static void revivePlayer(CommandSender sender, String[] args, UhcCore plugin) {

        String name = args[1];
        int teamNumber = -1;

        try{
            teamNumber = Integer.valueOf(args[2])-1;
        } catch (NumberFormatException e){
            sender.sendMessage("Please enter a number as team ID");
            return;
        }

        if(!Cache.game_running){
            sender.sendMessage("Can't revive player before the game has started");
            return;
        }

        if ((name != null)&&(teamNumber > 0)&&teamNumber < Cache.totalTeams+1) {


            if (!Cache.playerTeam.containsKey(name)) {



                if(Cache.playersPerTeam.get(teamNumber)>0){

                    // Add the player to that team.
                    Player player = plugin.getServer().getPlayer(name);

                    for(String matesName : Cache.playerTeam.keySet()) {
                        Player mate = plugin.getServer().getPlayer(matesName);
                        if (mate != null &&(Cache.playerTeam.get(matesName) == teamNumber)) {
                            Location hisTeamLoc = mate.getLocation();
                            Cache.playerTeam.put(name, teamNumber);
                            UhcUtils.updatePlayersPerTeam();
                            String teamName = Cache.teamNames.get(teamNumber);

                            player.getInventory().clear();
                            player.teleport(hisTeamLoc);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                            player.sendTitle("Welcome back " + player.getName(), "§7Enjoy the UHC", 20, 70, 10);
                            player.setHealth(20);
                            player.setFoodLevel(22);
                            player.setGameMode(GameMode.SURVIVAL);

                            if(Cache.glowing)
                                player.setGlowing(true);

                            UhcUtils.displayHearts(player);
                            //adding Hearts
                            org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();
                            Objective objective = board.getObjective("showhealth");
                            if (objective == null) {
                                String dName = ChatColor.RED + "\u2665";
                                objective = board.registerNewObjective("showhealth", "health", dName, RenderType.HEARTS);
                            }
                            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

                            int thisPlayerTeamPlayers = Cache.playersPerTeam.get(teamNumber);

                            plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                            {

                                int playingPlayers = Cache.playerTeam.size();
                                plugin.getServer().broadcastMessage("§7Player §e" + player.getName() + "§7 got revived into §e" + teamName );
                                plugin.getServer().broadcastMessage(teamName + "§7 consists of §e" + thisPlayerTeamPlayers + "§7 players now.");
                                plugin.getServer().broadcastMessage("§7In total remain §e" + playingPlayers + "§7 players, in §e" + Cache.playingTeams + "§7 teams.");
                                player.setHealth(10);


                            }, 20L);

                                UhcUtils.setTabColor(player, teamNumber);
                        }
                        break;
                    }


                }else {
                    sender.sendMessage("All players on this team are dead, he can't be revived");
                }


            } else {
                sender.sendMessage("Player is already part of a Team");
            }
        } else {
            sender.sendMessage("/uhc reviveplayer <player> <team>");
        }

    }
}
