package wtf.beatrice.uhccore.commands.uhccommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;
import wtf.beatrice.uhccore.utils.MessageUtils;
import wtf.beatrice.uhccore.utils.configuration.LocalizedMessage;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class GlobalChat implements TabExecutor {

    UhcCore plugin;

    public GlobalChat(UhcCore plugin) {
        this.plugin = plugin;
    }

    String message;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        // If the command comes from Console, give a warning.
        boolean senderIsConsole = (commandSender instanceof ConsoleCommandSender);
        if (senderIsConsole) {
            MessageUtils.sendLocalizedMessage(commandSender.getName(), LocalizedMessage.WARNING_CONSOLE_ACCESS);
            // Only uncomment the following line if the command should not be able to run this command.
        }


        if (command.getName().equalsIgnoreCase("g")) {
            if (commandSender instanceof Player) {
                String playerName = commandSender.getName();

                if (Cache.playerTeam.containsKey(playerName)) {
                    int teamNumber = Cache.playerTeam.get(playerName);
                    String teamName = Cache.teamNames.get(teamNumber);
                    if (args.length > 0) {
                        message = "§7[" + teamName + "§7] §f" + playerName + "§7: " + createMessageContents(args);
                        plugin.getServer().broadcastMessage(message);
                    }

                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }

    private String createMessageContents( String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            if (i != 0) {
                sb.append(' ');
            }
            sb.append(args[i]);
        }
        return sb.toString();
    }

}