package wtf.beatrice.uhccore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import wtf.beatrice.uhccore.utils.UhcUtils;

public class PlayerMoveListener implements Listener
{

    private UhcCore plugin;
    public PlayerMoveListener(UhcCore givenPlugin)
    {
        plugin = givenPlugin;
    }

    //Debugger debugger = new Debugger(getClass().getName());

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if((player.getWorld().getName().equals(Cache.lobbyWorlds.get(0)))&&player.getLocation().getY()<0){
            UhcUtils.tpSpawnAndGiveItem(player);
        }

        if(!Cache.allowMovement){
            event.setCancelled(true);
        }

    }
    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (player.getWorld().getName().equals(Cache.lobbyWorlds.get(0))) {
            event.setCancelled(true);
        }
    }
}
