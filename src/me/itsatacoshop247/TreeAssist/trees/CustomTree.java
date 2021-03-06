package me.itsatacoshop247.TreeAssist.trees;

import me.itsatacoshop247.TreeAssist.TreeAssistProtect;
import me.itsatacoshop247.TreeAssist.TreeAssistReplant;
import me.itsatacoshop247.TreeAssist.core.CustomTreeDefinition;
import me.itsatacoshop247.TreeAssist.core.Debugger;
import me.itsatacoshop247.TreeAssist.core.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CustomTree extends AbstractGenericTree {

    public static List<CustomTreeDefinition> customTreeDefinitions = new ArrayList<>();
    public static Debugger debugger;
    private final Material mat;

    public CustomTree(Material material, byte data) {
        mat = material;
    }

    public static boolean isCustomLog(Block blockAt) {
        for (CustomTreeDefinition def : CustomTree.customTreeDefinitions) {
            if (blockAt.getType().equals(def.getLog())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCustomTreeBlock(Block blockAt) {
        for (CustomTreeDefinition def : CustomTree.customTreeDefinitions) {
            if (blockAt.getType().equals(def.getLeaf())) {
                return true;
            }
        }
        return false;

    }

    @Override
    protected boolean isLog(Material type) {
        return type == mat;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    protected boolean hasPerms(Player player) {
        return true; //TODO implement custom
    }

    @Override
    protected Block getBottom(Block block) {
        int counter = 1;
        do {
            if (CustomTree.isCustomLog(block.getRelative(0, 0 - counter, 0))) {
                counter++;
            } else {
                bottom = block.getRelative(0, 1 - counter, 0);
                return bottom;
            }
        } while (block.getY() - counter > 0);

        bottom = null;
        return bottom;
    }

    @Override
    protected Block getTop(Block block) {
        int maxY = block.getWorld().getMaxHeight() + 10;
        int counter = 1;

        while (block.getY() + counter < maxY) {
            if (CustomTree.isCustomTreeBlock(block.getRelative(0, counter, 0))) {
                top = block.getRelative(0, counter - 1, 0);
                break;
            } else {
                counter++;
            }
        }
        return (top != null && leafCheck(top)) ? top.getRelative(0, 1, 0) : null;
    }

    @Override
    protected List<Block> calculate(final Block bottom, final Block top) {
        List<Block> list = new ArrayList<Block>();
        checkBlock(list, bottom, top, true, bottom.getData());
        return list;
    }

    @Override
    protected int isLeaf(Block block) {
        if (CustomTree.isCustomTreeBlock(block)) {
            return 1;
        }
        return 0;
    }

    @Override
    protected void getTrunks() {
    }

    @Override
    protected boolean willBeDestroyed() {
        return true;
    }

    @Override
    protected boolean willReplant() {
        if (!CustomTree.isCustomLog(bottom)) {
            return false;
        }

        return true;
    }

    @Override
    protected void handleSaplingReplace(int delay) {
        debug.i("handling custom sapling replace");
        int pos = 0;

        for (CustomTreeDefinition def : customTreeDefinitions) {
            if (saplingBlock.getType().equals(def.getLog())) {
                break;
            }
            pos++;
        }
        debug.i("pos: " + pos + "/" + customTreeDefinitions.size());

        if (pos < customTreeDefinitions.size()) {
            for (CustomTreeDefinition def : customTreeDefinitions) {
                if (--pos < 0) {
                    replaceSapling(def.getSapling(), delay,
                            saplingBlock);
                    break;
                }
            }
        }
    }

    private void replaceSapling(Material material, int delay, Block bottom) {
        debug.i("replacing custom sapling: " + delay + " sec; " + material.toString());
        if (bottom == null) {
            return;
        }

        Runnable b = new TreeAssistReplant(Utils.plugin, bottom, material);
        Utils.plugin.getServer()
                .getScheduler()
                .scheduleSyncDelayedTask(Utils.plugin, b,
                        20 * delay);

        if (Utils.plugin.getConfig()
                .getInt("Sapling Replant.Time to Protect Sapling (Seconds)") > 0) {
            Utils.plugin.saplingLocationList.add(bottom.getLocation());
            Runnable X = new TreeAssistProtect(Utils.plugin,
                    bottom.getLocation());

            Utils.plugin.getServer()
                    .getScheduler()
                    .scheduleSyncDelayedTask(
                            Utils.plugin,
                            X,
                            20 * Utils.plugin.getConfig()
                                    .getInt("Sapling Replant.Time to Protect Sapling (Seconds)"));
        }
    }

    private void checkBlock(List<Block> list, Block block,
                           Block top, boolean deep, byte origData) {

        if (block.getType() != mat) {
            if (isLeaf(block) > 0) {
                if (!list.contains(block)) {
                    list.add(block);
                }
            }
            return;
        }

        if (!CustomTree.isCustomLog(block)) {
            return;
        }

        if (block.getX() == top.getX() && block.getZ() == top.getZ()) {
//			debug.i("main trunk!");
            if (!deep) {
                // something else caught the main, return, this will be done later!
//				debug.i("not deep; out!");
                return;
            }
        }


        if (block.getRelative(0, 1, 0).getType() == mat) { // might
            // be a
            // trunk

            // one above is a tree block
            if (block.getX() != top.getX() && block.getZ() != top.getZ()) {


                if (checkFail(block)) {
                    return;
                }
            }
        }

        if (list.contains(block)) {
//			debug.i("already added!");
            return;
        } else {
//			debug.i(">>>>>>>>>> adding! <<<<<<<<<<<");
            list.add(block);
        }

        for (BlockFace face : Utils.NEIGHBORFACES) {
            checkBlock(list, block.getRelative(face), top, false, origData);

            checkBlock(list, block.getRelative(face).getRelative(BlockFace.DOWN), top, false, origData);
            checkBlock(list, block.getRelative(face).getRelative(BlockFace.UP), top, false, origData);
        }

        if (!deep) {
//			debug.i("not deep, out!");
            return;
        }

        if (block.getY() > top.getY()) {
//			debug.i("over the top! (hah) out!");
            return;
        }

        checkBlock(list, block.getRelative(0, 1, 0), top, true, origData);
    }


    protected boolean checkFail(Block block) {
        int failCount = 0;
        for (int cont = -4; cont < 5; cont++) {
            if (block.getRelative(0, cont, 0).getType() == mat) {
                failCount++;
            }
        }
        if (failCount > 3) {
//			debug.i("fail count "+failCount+"! out!");
            return true;
        }
        return false;
    }

    @Override
    protected boolean isBottom(Block block) {
        return block.equals(bottom);
    }

    @Override
    protected void debug() {
        System.out.print("Tree: CustomTree");
        System.out.print("mat: " + (mat == null ? "null" : mat.name()));

        System.out.print("bottom: " + (bottom == null ? "null" : bottom.toString()));
        System.out.print("top: " + (top == null ? "null" : top.toString()));
        System.out.print("valid: " + valid);

        System.out.print("removeBlocks: " + removeBlocks.size());
        System.out.print("totalBlocks: " + totalBlocks.size());
    }
}
