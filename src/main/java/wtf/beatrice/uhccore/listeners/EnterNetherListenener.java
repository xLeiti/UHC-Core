package wtf.beatrice.uhccore.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;
import wtf.beatrice.uhccore.utils.Debugger;

public class EnterNetherListenener implements Listener
{
    private UhcCore plugin;
    // Instantiate a Debugger for this class.


    public EnterNetherListenener(UhcCore givenPlugin) {plugin = givenPlugin;}
    private Debugger debugger = new Debugger(getClass().getName());

    // Call EventHandler and start listening to joining players.
    @EventHandler
    public void onPlayerUsingPortal(PlayerPortalEvent e)
    {
        // Initialize needed variables for performance improvements and to avoid continuous method calls.
        Player player = e.getPlayer();


        if((!Cache.nether_enabled)&&(player.getGameMode() == GameMode.SURVIVAL))
        {
            e.setCancelled(true);
        }


    }
}
