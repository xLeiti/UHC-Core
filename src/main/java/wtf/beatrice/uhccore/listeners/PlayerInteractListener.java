package wtf.beatrice.uhccore.listeners;


import org.bukkit.ChatColor;
import wtf.beatrice.uhccore.utils.Cache;
import wtf.beatrice.uhccore.utils.UhcUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractListener implements Listener
{

    // Event called when someone interacts with the world.
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        // Check if the action was a right click.
        Action action = event.getAction();
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))
        {

            Player player = event.getPlayer();

            // check if the player's item in hand was the Teams selector item.
            if(player.getInventory().getItemInMainHand().equals(Cache.teamsItem))
            {
                if(!Cache.game_running){
                    // Open the teams selector GUI.
                    player.openInventory(Cache.teamsSelectorGUI.getInventory());
                }else{
                    player.sendMessage("You can't join a team, the game is already running.");
                }

            }
        }
    }

    // Event called when someone clicks inside of an inventory.
    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event)
    {

        // Check if any of the inventories is null, and return.
        if(event.getClickedInventory() == null) return;
        if(event.getCurrentItem() == null) return;

        // Get the clicked item.
        ItemStack item = event.getCurrentItem();

        // Check if the clicked inventory is the Teams selector GUI.
        if(event.getClickedInventory().equals(Cache.teamsSelectorGUI.getInventory()))
        {
            // Cancel the event (we don't want items to be moved!)
            event.setCancelled(true);


            // Load the item's item meta.
            ItemMeta im = item.getItemMeta();
            // if it's null, return.
            if(im == null) return;

            // Load the player and close his inventory.
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            if(Cache.game_running){
                player.sendMessage("You can't join a team, the game is already running.");
                return;
            }
            String playername = player.getName();

            // Check if the clicked item is an existing Team.
            if(Cache.teamNames.contains(im.getDisplayName()))
            {

                // Load the team number from the team name in the teams list.
                int teamNumber = Cache.teamNames.indexOf(im.getDisplayName());

                // Add the player to that team.
                Cache.playerTeam.remove(player.getName());
                Cache.playerTeam.put(player.getName(), teamNumber);

                // Update the total number of players in each team, and the total number of alive teams.
                UhcUtils.updatePlayersPerTeam();


                UhcUtils.setTabColor(player, teamNumber);
                UhcUtils.setTeam(player, teamNumber);




                // Tell the player he has joined a team.
                player.sendMessage("§7You joined team " + im.getDisplayName());
            }
            // Else, check if the clicked item is the one used to quit teams.
            else if(item.equals(Cache.quitTeamItem))
            {
                // Check if the player is in any team.
                if(Cache.playerTeam.containsKey(player.getName()))
                {
                    // Remove the player from the team.
                    player.sendMessage("§eYou left the team!");
                    Cache.playerTeam.remove(player.getName());
                    player.setPlayerListName(ChatColor.WHITE + playername);
                    UhcUtils.removeFromTeams(player);

                    // Update the total number of players in each team, and the total number of alive teams.
                    UhcUtils.updatePlayersPerTeam();
                } else
                {
                    player.sendMessage("§cYou're currently in no team!");
                }
            }
        } // Check if the non-clicked inventory (there always are two inventories) is the Teams selector GUI, and cancel the event (we don't want items to be put inside of it!)
        else if(event.getInventory().equals(Cache.teamsSelectorGUI.getInventory())) event.setCancelled(true);

        // Prevent everyone from moving the Teams selector item in their inventory.
        if(item.equals(Cache.teamsItem)) event.setCancelled(true);
    }



    // Event called when an item is dropped.
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event)
    {
        // Check if the dropped item is the Teams selector item.
        if(event.getItemDrop().getItemStack().equals(Cache.teamsItem))
        {
            // Prevent it from being dropped.
            event.setCancelled(true);
        }
    }
}
