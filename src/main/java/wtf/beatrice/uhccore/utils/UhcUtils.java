package wtf.beatrice.uhccore.utils;

import org.bukkit.*;
import org.bukkit.scoreboard.*;
import wtf.beatrice.uhccore.UhcCore;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import wtf.beatrice.uhccore.utils.configuration.LocalizedMessage;

public class UhcUtils {

    // Function to check how many players a team has.
    // This function returns the total number of alive teams.
    public static void updatePlayersPerTeam()
    {
        // Integer to check how many teams are left alive.
        int playingTeams = 0;

        // Iterate through every existing team.
        for(int i = 0; i < Cache.totalTeams; i++)
        {
            // Int to store the players number for each team.
            int playersNumber = 0;

            // Iterate through every player and...
            for(String s : Cache.playerTeam.keySet())
            {
                //if his team is the current checked one...
                if(Cache.playerTeam.get(s) == i)
                {
                    //increase the playersNumber by 1.
                    playersNumber++;
                }
            }

            // Finally, put the team number and his player count in the playersPerTeam HashMap.
             Cache.playersPerTeam.put(i, playersNumber);

            // If there is at least 1 player in this team, then count this as an "alive team".
            if(playersNumber != 0) playingTeams++;
        }

        // Return the number of alive teams.
        Cache.playingTeams = playingTeams;
    }

    public static void giveTeamsSelectorItem(Player player)
    {

        player.getInventory().clear();
        player.getInventory().setItem(4, Cache.teamsItem);
    }

    public static void setTabColor(Player player, Integer teamNumber)
    {
        //player.setPlayerListName(returnColor(teamNumber) + player.getName());
    }

    public static ChatColor returnColor(Integer teamNumber)
    {

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

        return color;

    }

    public static void setTeam(Player player, Integer teamNumber)
    {
        registerTeams(player);
        Scoreboard board = player.getServer().getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam(Cache.teamNames.get(teamNumber));
        team.addEntry(player.getName());
    }
    public static void removeFromTeams(Player player)
    {
        for (Team team : player.getServer().getScoreboardManager().getMainScoreboard().getTeams()){
            team.removeEntry(player.getName());
        }
    }
    public static void clearTeams()
    {
        for (Team team : UhcCore.getInstance().getServer().getScoreboardManager().getMainScoreboard().getTeams()){
            for(String string : team.getEntries())
                team.removeEntry(string);
        }
    }

    public static void spawnFirework(Location location, long detonateDelay) {

        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.setPower(100);
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.RED).flicker(true).build());

        firework.setFireworkMeta(fireworkMeta);

        UhcCore plugin = UhcCore.getInstance();
        plugin.getServer().getScheduler().runTaskLater(plugin, firework::detonate, detonateDelay);
    }

    public static void tpSpawnAndGiveItem(Player player)
    {
        if(Cache.isServerReady)
        {
            MessageUtils.sendLocalizedMessage(player, LocalizedMessage.ERROR_SERVER_NOT_SET_UP);
            return;
        }


        player.teleport(Cache.spawn);

        // Clear the player's inventory and give hims the Teams selector item.
        UhcUtils.giveTeamsSelectorItem(player);
    }

    public static void displayHearts(Player player) {
        org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective("showhealth");
        if (objective == null) {
            String dName = ChatColor.RED + "\u2665";
            objective = board.registerNewObjective("showhealth", "health", dName, RenderType.HEARTS);
        }
        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }

    public static void registerTeams(Player player){

        Scoreboard board = player.getServer().getScoreboardManager().getMainScoreboard();

        for(int i = 0; i< Cache.teamNames.size(); i++){
            Team team = board.getTeam(Cache.teamNames.get(i));
            team.setColor(returnColor(i));
            if (team == null) {
                team = board.registerNewTeam(Cache.teamNames.get(i));
                team.setColor(returnColor(i));
            }

        }
    }

    public static void removeHeartsDisplay(Player player){
        org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();
        board.resetScores(player);


    }

}
