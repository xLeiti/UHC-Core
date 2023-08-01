package wtf.beatrice.uhccore.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.Cache;

import java.util.ArrayList;
import java.util.List;

public class InfoCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args)
    {
        List<String> list = new ArrayList<String>();
        if(args.length == 1)
        {
            list.add("help");
            list.add("setspawn");
            list.add("setfirework");
            list.add("setglowing");
            list.add("start");
            list.add("reload");
            list.add("list");
            list.add("removeplayer");
            list.add("reviveplayer");
            if(args[0].startsWith("h"))
            {
                list.clear();
                list.add("help");
            } else
            if(args[0].startsWith("s"))
            {
                list.clear();
                list.add("setspawn");
                list.add("setfirework");
                list.add("setglowing");
                list.add("start");

            } else
            if(args[0].startsWith("r"))
            {
                list.clear();
                list.add("reload");
                list.add("removeplayer");
                list.add("reviveplayer");
            } else
            if(args[0].equalsIgnoreCase("list"))
            {
                list.clear();
                list.add("list");
            }
        }else
        if(args.length==2)
        {
            if(args[0].equalsIgnoreCase("removeplayer"))
            {
                for (String playerName : Cache.playerTeam.keySet()) {
                    list.add(playerName);
                }
            }else
            if(args[0].equalsIgnoreCase("reviveplayer"))
            {
                for (Player player : UhcCore.getInstance().getServer().getOnlinePlayers()) {
                    if(!Cache.playerTeam.containsKey(player.getName())){
                        list.add(player.getName());
                    }
                }
            }else
            if(args[0].equalsIgnoreCase("setglowing"))
            {
                list.add("true");
                list.add("false");
            }
        }else
        if(args.length == 3 && args[0].equalsIgnoreCase("reviveplayer"))
        {
            for(int i = 0; i < Cache.totalTeams; i++)
                list.add(String.valueOf(i+1));
        }

        return list;
    }
}
