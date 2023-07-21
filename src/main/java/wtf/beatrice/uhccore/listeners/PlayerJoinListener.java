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
            org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();
            board.clearSlot(DisplaySlot.PLAYER_LIST);
            UhcUtils.tpSpawnAndGiveItem(player);
        }else{



            int teamNumber = Cache.playerTeam.get(player.getName());



            // Load the team number from the team name in the teams list.
            //int teamNumber = Cache.teamNames.indexOf(player.getName());
            player.sendMessage("§cYou're in team"+teamNumber);
            // Add the player to that team.
            Cache.playerTeam.remove(player.getName());
            Cache.playerTeam.put(player.getName(), teamNumber);

            // Update the total number of players in each team, and the total number of alive teams.
            UhcUtils.updatePlayersPerTeam();


            ChatColor color;
            switch (teamNumber) {
                case 0:  color = ChatColor.BLUE;
                    break;
                case 1:  color = ChatColor.RED;
                    break;
                case 2:  color = ChatColor.GREEN;
                    break;
                case 3:  color = ChatColor.YELLOW;
                    break;
                case 4:  color = ChatColor.DARK_PURPLE;
                    break;
                case 5:  color = ChatColor.GOLD;
                    break;
                case 6:  color = ChatColor.BLACK;
                    break;
                case 7:  color = ChatColor.LIGHT_PURPLE;
                    break;
                default: color = ChatColor.WHITE;
                    break;
            }

            player.setPlayerListName(color + player.getName());
        }

        if(Cache.game_running){
            player.sendMessage(String.valueOf(Cache.nether_enabled));
            if((!Cache.nether_enabled)&&(player.getWorld().getName()==Cache.uhcWorlds.get(1))){
                player.sendMessage("wtf");
                player.setHealth(0);
            }
        }


    }
}
