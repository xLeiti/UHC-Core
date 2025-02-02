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

        return switch (teamNumber) {
            case 0 -> ChatColor.BLUE;
            case 1 -> ChatColor.RED;
            case 2 -> ChatColor.GREEN;
            case 3 -> ChatColor.YELLOW;
            case 4 -> ChatColor.DARK_PURPLE;
            case 5 -> ChatColor.GOLD;
            case 6 -> ChatColor.BLACK;
            case 7 -> ChatColor.LIGHT_PURPLE;
            case 8 -> ChatColor.AQUA;
            default -> ChatColor.WHITE;
        };

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

        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
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

        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setSaturation(20);
        player.setCollidable(false);
        player.teleport(Cache.spawn);
        player.setGlowing(false);
        //remove players heartdisplay in tab
        UhcUtils.removeHeartsDisplay(player);
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
            if (team == null) {
                team = board.registerNewTeam(Cache.teamNames.get(i));
                team.setColor(returnColor(i));
                team.setCanSeeFriendlyInvisibles(true);
            }

        }
    }

    public static void removeHeartsDisplay(Player player){

        //Shitty solution but works ig
        UhcCore.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(UhcCore.getInstance(), ()->
        {
            org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();
            board.resetScores(player.getName());
        }, 5L);
    }

}
