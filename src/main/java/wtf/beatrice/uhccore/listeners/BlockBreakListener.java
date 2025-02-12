package wtf.beatrice.uhccore.listeners;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import wtf.beatrice.uhccore.UhcCore;
import wtf.beatrice.uhccore.utils.math.NumberUtils;



public class BlockBreakListener implements Listener {


    private UhcCore plugin;

    public BlockBreakListener(UhcCore givenPlugin) {plugin = givenPlugin;}

    //Oak and dark_oak already drop apples
    Material[] leaves = new Material[]{Material.SPRUCE_LEAVES, Material.ACACIA_LEAVES, Material.AZALEA_LEAVES, Material.BIRCH_LEAVES, Material.CHERRY_LEAVES, Material.FLOWERING_AZALEA_LEAVES, Material.JUNGLE_LEAVES, Material.MANGROVE_LEAVES, Material.NETHER_WART_BLOCK, Material.WARPED_WART_BLOCK, Material.PALE_OAK_LEAVES};

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        var block = event.getBlock();

        this.onBlockRemove(block);

        if(block.getType() == Material.BEE_NEST){
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(block.getLocation().add(.5, .5, .5), new ItemStack(Material.GOLDEN_APPLE));
        }

    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event)
    {
       onBlockRemove(event.getBlock());
    }

    private void onBlockRemove(Block block)
    {
        for(Material m : leaves) {
            if (block.getType().equals(m)) {
                //0.5%
                int random = NumberUtils.getRandomNumberInRange(0,200);
                if (random > 1) {
                    return;
                }
                block.getWorld().dropItemNaturally(block.getLocation().add(.5, .5, .5), new ItemStack(Material.APPLE));
            }
        }
    }
}
