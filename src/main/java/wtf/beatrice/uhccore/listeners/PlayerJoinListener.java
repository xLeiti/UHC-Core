package wtf.beatrice.uhccore.listeners;

import org.bukkit.GameMode;
import wtf.beatrice.uhccore.utils.Cache;
import wtf.beatrice.uhccore.utils.UhcUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener
{

    // Call EventHandler and start listening to joining players.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {

        Player player = e.getPlayer();
        //register playerhearts display
        UhcUtils.displayHearts(player);

        //check if game is running
        if(Cache.game_running){
            //game running - check if nether is enabled
            if((!Cache.nether_enabled)&&player.getWorld().getName().equals(Cache.uhcWorlds.get(1)))
            {
                if(player.getGameMode() == GameMode.SURVIVAL)
                    player.setHealth(0);
            }
            //game running - check if player isn't an active player
            if(!(Cache.playerTeam.containsKey(player.getName())))
            {
                UhcUtils.tpSpawnAndGiveItem(player);
            }else {
                if (Cache.glowing) {
                    player.setGlowing(true);
                }
            }
        //game not running -> spawn players in lobby
        }else{
            UhcUtils.tpSpawnAndGiveItem(player);
        }


    }
}
