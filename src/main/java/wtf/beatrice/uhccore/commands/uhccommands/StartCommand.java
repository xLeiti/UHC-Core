package wtf.beatrice.uhccore.commands.uhccommands;


import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;
import wtf.beatrice.uhccore.utils.Debugger;
import wtf.beatrice.uhccore.utils.UhcUtils;
import wtf.beatrice.uhccore.utils.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.logging.Level;


public class StartCommand {

    private static Debugger debugger = new Debugger(StartCommand.class.getName());

    private static int loadDelay = 5;
    static int count = 15;

    public static void startUhcCommand(CommandSender sender, UhcCore plugin)
    {

        HashMap<Integer, Location> spawnPerTeam = new HashMap<>();

        World spawnWorld = plugin.getServer().getWorld(Cache.uhcWorlds.get(0));
        World Nether = plugin.getServer().getWorld(Cache.uhcWorlds.get(1));

        spawnWorld.setPVP(false);
        Nether.setPVP(false);

        Cache.game_running = true;

        int borderX = Cache.borderX;
        int borderZ = Cache.borderZ;
        int borderSize = Cache.borderSize;
        int borderSizeFinal = Cache.borderSizeFinal;
        int borderTime = Cache.borderTime;

        int range = borderSize / 2;

        Location borderCenter = new Location(spawnWorld, borderX, 64, borderZ);


        for(String playerName : Cache.playerTeam.keySet())
        {
            Player player = plugin.getServer().getPlayer(playerName);
            player.sendTitle("The §dUHC§r will start shortly!", "Generating spawn points...", 20, 70, 10);
        }


        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()->
        {
            for(int i = 0; i < Cache.totalTeams; i++)
            {

                double x = NumberUtils.getRandomNumberInRange(borderX - range + 5, borderX + range - 5) + 0.5;
                double z = NumberUtils.getRandomNumberInRange(borderZ - range + 5, borderZ + range - 5) + 0.5;
                int y = spawnWorld.getHighestBlockYAt((int) x, (int) z); // todo: this method is shit, use the one i already implemented in Factions...

                Location loc = new Location(spawnWorld, x, y + 1, z);

                Location standingBlockLoc = new Location(spawnWorld, x, y, z);
                Location upperBlockLoc = new Location(spawnWorld, x, y + 2, z);
                Material standingBlockType = standingBlockLoc.getBlock().getType();
                Material upperBlockType = upperBlockLoc.getBlock().getType();

                spawnPerTeam.put(i, loc);
                debugger.sendDebugMessage(Level.INFO, "found block! " + loc);
                if(standingBlockType.equals(Material.WATER) || standingBlockType.equals(Material.LAVA) || standingBlockType.equals(Material.AIR)
                || !upperBlockType.equals(Material.AIR) || !loc.getBlock().getType().equals(Material.AIR))
                {
                    debugger.sendDebugMessage(Level.WARNING, "block is not valid: " + standingBlockType + ", " + loc.getBlock().getType() + ", " + upperBlockType);
                    i--;
                }
            }
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()->
            {
                Cache.allowMovement = false;
            }, 20L);

            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()->
            {
                Cache.allowMovement = true;
            }, loadDelay * 20L);


            for(String playerName : Cache.playerTeam.keySet())
            {
                int teamNumber = Cache.playerTeam.get(playerName);

                Player player = plugin.getServer().getPlayer(playerName);
                Location hisTeamLoc = spawnPerTeam.get(teamNumber);

                //Display Playerhearts


                //player.addPotionEffect((new PotionEffect(PotionEffectType.ABSORPTION, 200, 10)));
                UhcUtils.displayHearts(player);
                player.setHealth(10);
                plugin.getServer().getScheduler().runTaskLater(plugin, ()->
                {
                    player.setHealth(20);
                }, 20L);




                plugin.getServer().getScheduler().runTask(plugin, () ->
                {
                    player.setInvulnerable(true);
                    player.teleport(hisTeamLoc);
                    player.getInventory().clear();
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    player.sendTitle("Loading...", "§7Please wait a moment", 20, 70, 10);



                    plugin.getServer().getScheduler().runTaskLater(plugin, ()->
                    {
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        player.sendTitle("The §dUHC§r has begun!", "Good luck!", 20, 70, 10);
                        player.setHealth(20);
                        player.setFoodLevel(22);
                        player.setGameMode(GameMode.SURVIVAL);
                    }, loadDelay * 20L);

                    plugin.getServer().getScheduler().runTaskLater(plugin, ()->
                    {
                        player.setInvulnerable(false);
                        player.sendMessage("§7Damage is enabled!");
                    }, 60L * 20L);

                });
            }

            plugin.getServer().getScheduler().runTask(plugin, ()->
            {
                spawnWorld.setTime(0L);
                spawnWorld.setGameRule(GameRule.NATURAL_REGENERATION, false);
                Nether.setGameRule(GameRule.NATURAL_REGENERATION, false);
                spawnWorld.setDifficulty(Difficulty.NORMAL);
                spawnWorld.getWorldBorder().setCenter(borderCenter);
                spawnWorld.getWorldBorder().setSize(borderSize);
                spawnWorld.getWorldBorder().setDamageBuffer(0.3);
                spawnWorld.getWorldBorder().setDamageAmount(1);
                Nether.getWorldBorder().setSize(borderSize);
                spawnWorld.getWorldBorder().setSize(borderSizeFinal, borderTime*60L);
                plugin.getLogger().log(Level.INFO,"UHC Started!");
            });

            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()->
            {
                spawnWorld.setPVP(true);
                Nether.setPVP(true);
                plugin.getServer().broadcastMessage("§6PVP enabled!");
            }, Cache.peaceperiod * 20L * 60L);


            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()->
            {
                plugin.getServer().broadcastMessage("§6Horizontal border reached it's final state");
            }, Cache.borderTime * 20L * 60L);

            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, ()->
            {
                startClosecountdown(plugin, Nether);
            }, (Cache.netherclosetime-15) * 20L * 60L);




        });
    }


    public static void startClosecountdown(UhcCore plugin, World nether) {


            if(count>0){
                plugin.getServer().broadcastMessage("§6§lNether closes in "+count+" minutes!!!");

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, ()->
                {

                    count--; // reduce the counter
                    startClosecountdown(plugin, nether);

                },   60L*20L);

            }else{
                plugin.getServer().broadcastMessage("§4§lNether closed.");
                Cache.nether_enabled = false;
                for (Player p : nether.getPlayers()) {
                    p.setHealth(0);
                }
            }




    }

}
