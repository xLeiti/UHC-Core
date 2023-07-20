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
        }

        org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective("showhealth");
        if (objective == null) {
            String dName = ChatColor.RED + "\u2665";
            objective = board.registerNewObjective("showhealth", "health", dName, RenderType.HEARTS);
            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
    }
}
