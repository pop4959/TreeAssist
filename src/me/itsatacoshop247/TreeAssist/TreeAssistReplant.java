package me.itsatacoshop247.TreeAssist;

import me.itsatacoshop247.TreeAssist.modding.ModUtils;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class TreeAssistReplant implements Runnable {
	public final TreeAssist plugin;
	public Block block;
	public byte data;
	public int type;
	
	public TreeAssistReplant(TreeAssist instance, Block importBlock, int typeid, byte importData)
	{
		this.plugin = instance;
		this.block = importBlock;
		this.data = importData;

		Object[] logs;
		Object[] saplings;
		
		logs = new Object[ModUtils.customLogs.size()];
		int pos = 0;
		for (Object o : ModUtils.customLogs) {
			logs[pos++] = o;
		}

		saplings = new Object[ModUtils.customSaplings.size()];
		pos = 0;
		for (Object o : ModUtils.customSaplings) {
			saplings[pos++] = o;
		}

		if (typeid == Material.LOG.getId() || (!ModUtils.customLogs.contains(typeid) && !ModUtils.customLogs.contains(typeid+":"+importData))) {
			type = Material.SAPLING.getId();
			System.out.print("Standard; " + block.getY());
			return;
		}
		
		for (pos = 0; pos < logs.length && pos < saplings.length; pos++) {
			if (logs[pos].equals(typeid) || logs[pos].equals(typeid+':'+importData)) {
				Object o = saplings[pos];
				
				if (o instanceof Integer) {
					type = (Integer) o;
					return;
				}
				
				String value = (String) o;
				
				String[] split = value.split(":");

				type = Integer.parseInt(split[0]);
				importData = Byte.parseByte(split[1]);
				return;
			}
		}
		
		type = Material.SAPLING.getId();
	}

	@Override
	public void run() 
	{
		if(plugin.isEnabled())
		{
			this.block.setTypeId(type);
			this.block.setData(this.data);
		}
	}
}
