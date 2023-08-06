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
            UhcUtils.tpSpawnAndGiveItem(player);
            UhcUtils.removeHeartsDisplay(player);
            player.setGlowing(false);
        }else{

            if(Cache.glowing){
                player.setGlowing(true);
            }


            if(Cache.game_running) {
                UhcUtils.displayHearts(player);
            }else{
                UhcUtils.removeHeartsDisplay(player);
            }
        }

        if(Cache.game_running){
            if((!Cache.nether_enabled)&&player.getWorld().getName().equals(Cache.uhcWorlds.get(1)))
            {
                player.setHealth(0);
            }




        }


    }
}
