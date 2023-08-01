package wtf.beatrice.uhccore.listeners;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import wtf.beatrice.uhccore.utils.Cache;
import wtf.beatrice.uhccore.utils.Debugger;
import wtf.beatrice.uhccore.utils.UhcUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener
{
    // Instantiate a Debugger for this class.
    private Debugger debugger = new Debugger(getClass().getName());



    // Call EventHandler and start listening to joining players.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        // Initialize needed variables for performance improvements and to avoid continuous method calls.
        Player player = e.getPlayer();


        if(!(Cache.playerTeam.containsKey(player.getName())))
        {
            UhcUtils.removeHeartsDisplay(player);
            UhcUtils.tpSpawnAndGiveItem(player);
            player.setGlowing(false);
        }else{

            UhcUtils.displayHearts(player);

            int teamNumber = Cache.playerTeam.get(player.getName());

            Cache.playerTeam.put(player.getName(), teamNumber);

            // Update the total number of players in each team, and the total number of alive teams.
            UhcUtils.updatePlayersPerTeam();

            UhcUtils.setTabColor(player, teamNumber);

            if(Cache.glowing)
                player.setGlowing(true);
        }

        if(Cache.game_running){
            if((!Cache.nether_enabled)&&player.getWorld().getName().equals(Cache.uhcWorlds.get(1)))
            {
                player.setHealth(0);
            }


        }


    }
}
