package wtf.beatrice.uhccore.listeners;

import org.bukkit.entity.Player;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

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
