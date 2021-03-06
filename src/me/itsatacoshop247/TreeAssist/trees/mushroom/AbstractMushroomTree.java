package me.itsatacoshop247.TreeAssist.trees.mushroom;

import me.itsatacoshop247.TreeAssist.TreeAssistProtect;
import me.itsatacoshop247.TreeAssist.TreeAssistReplant;
import me.itsatacoshop247.TreeAssist.core.Utils;
import me.itsatacoshop247.TreeAssist.trees.AbstractGenericTree;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractMushroomTree extends AbstractGenericTree {
    private final Material blockMaterial;
    private final Material itemMaterial;
    private final String destroySetting;
    private final String permissionString;

    AbstractMushroomTree(Material blockMaterial, Material itemMaterial, String destroySetting, String permissionString) {
        this.blockMaterial = blockMaterial;
        this.itemMaterial = itemMaterial;
        this.destroySetting = destroySetting;
        this.permissionString = permissionString;
    }
    /**
     * thanks to filbert66 for this determination method!
     *
     * @param tool the itemstack being used
     * @return the seconds that it will take to destroy
     */
    @Override
    public int calculateCooldown(ItemStack tool) {
        Material element = (tool != null ? tool.getType() : Material.AIR);

        float singleTime;

        switch (element) {
            case GOLDEN_AXE:
                singleTime = 0.05F;
                break;
            case DIAMOND_AXE:
                singleTime = 0.05F;
                break;
            case IRON_AXE:
                singleTime = 0.05F;
                break;
            case STONE_AXE:
                singleTime = 0.1F;
                break;
            case WOODEN_AXE:
                singleTime = 0.15F;
                break;

            default:
                singleTime = 0.3F;
                break;
        }

        float efficiencyFactor = 1.0F;
        if (tool != null && tool.hasItemMeta()) {
            int efficiencyLevel = tool.getItemMeta().getEnchantLevel(
                    Enchantment.DIG_SPEED);
            for (int i = 0; i < efficiencyLevel; i++) {
                efficiencyFactor /= 1.3F;
            }
            //debug.i("tool efficiency factor: " + efficiencyFactor);
        }

        int numLogs = 0;
        for (Block b : removeBlocks) {
            if (isLeaf(b) > 0) {
                numLogs++;
            }
        }

        //debug.i("breakTime (" + removeBlocks.size() + " blocks): " + numLogs
                //* singleTime * efficiencyFactor);

        return (int) (numLogs * singleTime * efficiencyFactor);
    }

    @Override
    protected boolean checkFail(Block block) {
        return false;
    }

    @Override
    protected void debug() {
        System.out.print("Tree: "+this.getClass().getName());
        System.out.print("blockMaterial: " + blockMaterial);

        System.out.print("removeBlocks: " + removeBlocks.size());
        System.out.print("totalBlocks: " + totalBlocks.size());

        System.out.print("valid: " + valid);
        System.out.print("top: " + (top == null ? "null" : top.toString()));
        System.out.print("bottom: " + (bottom == null ? "null" : bottom.toString()));
    }

    @Override
    protected Block getBottom(Block block) {
        int min = Utils.plugin.getConfig().getBoolean("Main.Destroy Only Blocks Above") ? block.getY() : 0;
        int counter = 1;
        while (block.getY() - counter >= min) {
            if (block.getRelative(0, 0 - counter, 0).getType() == blockMaterial ||
            		block.getRelative(0, 0 - counter, 0).getType() == Material.MUSHROOM_STEM) {
                counter++;
            } else {
                bottom = block.getRelative(0, 1 - counter, 0);
                if (bottom.getRelative(BlockFace.DOWN).getType() == Material.AIR
                        || bottom.getRelative(BlockFace.DOWN).getType() == blockMaterial
                        || bottom.getRelative(BlockFace.DOWN).getType() == Material.MUSHROOM_STEM) {
                    return null; // the shroom is already broken.
                }
                return bottom;
            }
        }

        if (Utils.plugin.getConfig().getBoolean("Main.Destroy Only Blocks Above")) {
            return bottom; // if we destroy above we can assume we have nothing to lose down there
        } // otherwise we assume that we tried to go too far down and return a non-tree!

        bottom = null;
        return bottom;
    }

    @Override
    protected Block getTop(Block block) {
        int maxY = block.getWorld().getMaxHeight() + 10;
        int counter = 1;

        //debug.i("getting top; type " + blockMaterial);

        while (block.getY() + counter < maxY) {
            if (block.getRelative(0, counter, 0).getType() != blockMaterial
            		&& block.getRelative(0, counter, 0).getType() != Material.MUSHROOM_STEM
                    || counter > 13) {
                top = block.getRelative(0, counter - 1, 0);
                //debug.i("++");
                break;
            } else {
                counter++;
            }
        }
        //debug.i("counter == " + counter);
        return top;
    }

    @Override
    protected void getTrunks() {
    }

    @Override
    protected void handleSaplingReplace(int delay) {

        Material saplingMat = itemMaterial;

        Runnable b = new TreeAssistReplant(Utils.plugin, saplingBlock, saplingMat);
        Utils.plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(Utils.plugin, b, 20 * delay);

        if (Utils.plugin.getConfig().getInt(
                "Sapling Replant.Time to Protect Sapling (Seconds)") > 0) {
            Utils.plugin.saplingLocationList.add(saplingBlock.getLocation());
            Runnable X = new TreeAssistProtect(Utils.plugin,
                    saplingBlock.getLocation());

            Utils.plugin
                    .getServer()
                    .getScheduler()
                    .scheduleSyncDelayedTask(
                            Utils.plugin,
                            X,
                            20 * Utils.plugin
                                    .getConfig()
                                    .getInt("Sapling Replant.Time to Protect Sapling (Seconds)"));
        }
    }

    @Override
    protected boolean hasPerms(Player player) {
        if (!Utils.plugin.getConfig().getBoolean("Main.Use Permissions")) {
            return true;
        }
        return player.hasPermission("treeassist.destroy." + permissionString);
    }

    @Override
    protected boolean isBottom(Block block) {
        return block.equals(bottom);
    }

    @Override
    protected int isLeaf(Block block) {
        return 0;
    }

    @Override
    protected boolean isLog(Material type) {
        return type == blockMaterial ||  type == Material.MUSHROOM_STEM;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    protected boolean willBeDestroyed() {
        return Utils.plugin.getConfig().getBoolean(
                "Automatic Tree Destruction.Tree Types."+destroySetting);
    }

    @Override
    protected boolean willReplant() {
        if (!Utils.replantType(blockMaterial)) {
            return false;
        }
        return true;
    }
}
