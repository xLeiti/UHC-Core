package wtf.beatrice.uhccore.listeners;

import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;
import wtf.beatrice.uhccore.utils.Debugger;
import wtf.beatrice.uhccore.utils.UhcUtils;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.logging.Level;

public class PlayerDeathRespawnListener implements Listener
{

    private UhcCore plugin;

    public PlayerDeathRespawnListener(UhcCore givenPlugin)
    {
        plugin = givenPlugin;
    }


    Debugger debugger = new Debugger(getClass().getName());
    // HashMap containing players who died during the UHC, and their death location.
    // we need this  so we can teleport them there as spectators once they respawn.
    private HashMap<String, Location>deadPlayers = new HashMap<>();

    private boolean isTaskScheduled = false;
    int taskID, times;


    // Event called when the player dies.
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {

        Player player = event.getEntity();
        player.setGlowing(false);
        // Remove the Teams selector item from the drops.
        event.getDrops().remove(Cache.teamsItem);

        // Check if the player died in the lobby...
        if(Cache.lobbyWorlds.contains(player.getWorld().getName()))
        {
            // And clear all drops.
            event.getDrops().clear();
        }


        // check if the death world is a UHC world (we don't want this to happen in the lobby!)
        // and
        // check if the player is in any team (players who are not in a team are not playing!)
        if((Cache.uhcWorlds.contains(player.getWorld().getName())) && Cache.playerTeam.containsKey(player.getName()))
        {

            // Spawn a Firework where the player died.
            UhcUtils.spawnFirework(player.getLocation(), 15L);
            //UhcUtils.removeFromTeams(player);
            // Drop golden apple
            ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 1);
            event.getDrops().add(gapple);

            // Load the player name.
            String playerName = player.getName();

            // Store the player name and his death location.
            deadPlayers.put(playerName, player.getLocation());

            // Load the dead player's team number.
            int thisPlayerTeamNumber = Cache.playerTeam.get(playerName);
            // Load the death player's team name.
            String thisPlayerTeamName = Cache.teamNames.get(thisPlayerTeamNumber);
            // Remove the player from his team.
            Cache.playerTeam.remove(playerName);



            // Run this task Async, because it may be CPU heavy.
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () ->

            {
                // Update the total number of players in each team, and the total number of alive teams.
                UhcUtils.updatePlayersPerTeam();

                // Check how many players are left in the dead player's team.
                int thisPlayerTeamPlayers = Cache.playersPerTeam.get(thisPlayerTeamNumber);

                // Run this task Sync, because we need to access the API, and also delay it by 1 second.
                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                {

                    int playingPlayers = Cache.playerTeam.size();
                    for(Player p : plugin.getServer().getOnlinePlayers())
                    {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    }
                    plugin.getServer().broadcastMessage(player.getDisplayName() + "§7 of team §e" + thisPlayerTeamName + "§7 got eliminated!");
                    plugin.getServer().broadcastMessage("§7In team " + thisPlayerTeamName + "§7 remain §e" + thisPlayerTeamPlayers + "§7 players.");
                    plugin.getServer().broadcastMessage("§7In total remain §e" + playingPlayers + "§7 players, in §e" + Cache.playingTeams + "§7 teams.");

                    if(Cache.playingTeams <= 1)
                    {
                        Cache.allowMovement = false;
                        Cache.game_running = false;
                        scheduleTask();
                        plugin.getServer().broadcastMessage("§6The UHC is over!");

                        //Remove playerhearts display


                        int winningTeam = 0;
                        for(int i = 0; i < Cache.totalTeams; i++)
                        {
                            if(Cache.playersPerTeam.get(i) > 0)
                            {
                                winningTeam = i;
                            }
                        }

                        String teamName = Cache.teamNames.get(winningTeam) + "§r";
                        plugin.getServer().broadcastMessage("§6The winners are: " + teamName);
                        Cache.glowing = false;


                        for(Player currentPlayer : plugin.getServer().getOnlinePlayers())
                        {
                            UhcUtils.tpSpawnAndGiveItem(currentPlayer);
                            plugin.getLogger().log(Level.INFO,"UHC Finished!");
                            currentPlayer.sendTitle("Team " + teamName + " won!", "", 20 * 2, 20 * 10, 20 * 2);

                            for(Player hiddenPlayer : plugin.getServer().getOnlinePlayers())
                            {
                                currentPlayer.hidePlayer(plugin, hiddenPlayer);
                            }
                        }
                    }
                }, 20L);
            });

        }
    }

    // Event called when a player respawns.
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        // Load the player value.
        Player player = event.getPlayer();
        event.setRespawnLocation(Cache.spawn);
        //player.teleport(Cache.spawn);
        // Check if the player died during the UHC, so we can get his death location.
        if(deadPlayers.containsKey(event.getPlayer().getName()))
        {

                // Check if there is more than 1 team alive.
                // If there is only 1 team alive, then the UHC is over.
                if(Cache.playingTeams > 1)
                {
                    // warn the player that he's not a spectator.
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                            {
                                //player.teleport(Cache.spawn);
                                player.sendMessage("§cYou died in the UHC and are now a spectator!");
                            });
                    // teleport him to his death location.
                    //player.teleport(deadPlayers.get(player.getName()));

                    // wait 0,5s and set his gamemode to spectator.
                    //plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        //player.setGameMode(GameMode.SPECTATOR);
                    //}, 10L);
                }
                else
                {
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                    {
                        UhcUtils.tpSpawnAndGiveItem(player);
                    });
                }

                deadPlayers.remove(player.getName());

        }
        else
        {

            plugin.getServer().getScheduler().runTask(plugin, () ->
            {
                UhcUtils.tpSpawnAndGiveItem(player);
            });
        }


    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        if(event.getDamager() instanceof Firework)
        {
            event.setDamage(0.0);
            event.setCancelled(true);
        }
    }

    private void scheduleTask()
    {
        if(isTaskScheduled)return;
        isTaskScheduled = true;

        taskID = plugin.getServer().getScheduler().runTaskTimer(plugin, () ->
        {
            times++;
            if(times == 10)
            {
                times = 0;
                plugin.getServer().getScheduler().cancelTask(taskID);
                taskID = 0;
            }

            for(Location loc : Cache.fireworksLocations)
            {
                debugger.sendDebugMessage(Level.INFO, "FIREWORK LOC: " + loc);
                UhcUtils.spawnFirework(loc, 20L);
            }


        }, 20L, 20L).getTaskId();




        plugin.getServer().getScheduler().runTaskLater(plugin, ()->
        {
            isTaskScheduled = false;
            Cache.allowMovement = true;

            for (Player currentPlayer : plugin.getServer().getOnlinePlayers())
            {
                for(Player hiddenPlayer : plugin.getServer().getOnlinePlayers())
                {
                    currentPlayer.showPlayer(plugin, hiddenPlayer);
                }
            }
        }, 20L * 10L);


    }


    public static class PlayerChatListener implements Listener {

        private UhcCore plugin;

        public PlayerChatListener(UhcCore givenPlugin)
        {
            plugin = givenPlugin;
        }


        // Event called whenever a player sends a chat message.
        @EventHandler(priority = EventPriority.HIGH)
        public void onPlayerChat(AsyncPlayerChatEvent event)
        {

            // Store the player's name, his display name, and the sent message.
            String playerName = event.getPlayer().getName();
            String displayName = event.getPlayer().getDisplayName();
            String message;

            // Check if the player is in a team.
            if(Cache.playerTeam.containsKey(playerName))
            {

                    // Load the team number and the team name from that.
                int teamNumber = Cache.playerTeam.get(playerName);
                String teamName = Cache.teamNames.get(teamNumber);

                    if(Cache.game_running){
                    for(String matesName : Cache.playerTeam.keySet()) {
                        Player player = plugin.getServer().getPlayer(matesName);
                        if (Cache.playerTeam.get(matesName) == teamNumber) {
                            player.sendMessage( displayName + "§7: " + event.getMessage());
                        }
                    }

                }else{
                        // Build the chat message.
                        message = "§7[" + teamName + "§7] §f" + displayName + "§7: " + event.getMessage();
                        plugin.getServer().broadcastMessage(message);
                    }


            } else // Else, if the player is not in any team...
            {
                // Just fomat the message.
                message = "§f" + displayName + "§7: " + event.getMessage();
                plugin.getServer().broadcastMessage(message);
            }

            // cancel the event (we want to send our own custom message)
            event.setCancelled(true);

            // and finally broadcast the message.



        }
    }
}
