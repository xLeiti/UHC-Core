package wtf.beatrice.uhccore.commands.uhccommands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class HelpCommand
{
    public static void infoCommand(CommandSender commandSender, Plugin plugin)
    {
        commandSender.sendMessage("UHC-Commands:");
        commandSender.sendMessage("/uhc list");
        commandSender.sendMessage("/uhc spectate");
        if(commandSender.isOp()){
            commandSender.sendMessage("/uhc start");
            commandSender.sendMessage("/uhc setspawn");
            commandSender.sendMessage("/uhc setglowing <true>");
            commandSender.sendMessage("/uhc setfirework");
            commandSender.sendMessage("/uhc reload");
            commandSender.sendMessage("/uhc removeplayer <name>");
            commandSender.sendMessage("/uhc removeplayer <name>");
            commandSender.sendMessage("/uhc tp <world>");
        }
    }
}
