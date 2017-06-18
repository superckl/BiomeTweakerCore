package me.superckl.biometweakercore;

import java.io.File;

import lombok.Getter;
import me.superckl.biometweakercore.util.ModData;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Getter
public class Config {

	private final Configuration configFile;

	private final File whereAreWe;

	private String[] asmBlacklist;
	private boolean removeLateAssignments;

	private boolean actualFillerBlocks;
	private boolean oceanTopBlock;
	private boolean oceanFillerBlock;
	private boolean grassColor;
	private boolean foliageColor;
	private boolean waterColor;
	private boolean skyColor;


	public Config(final File whereAreWe) {
		this.configFile = new Configuration(new File(whereAreWe, "BiomeTweakerCore.cfg"));
		this.whereAreWe = whereAreWe;
		this.configFile.load();
		if(this.configFile.hasChanged())
			this.configFile.save();
	}

	public void loadValues(){
		this.actualFillerBlocks = this.configFile.getBoolean("Actual Filler Blocks", "ASM Tweaks", false, "Enables the 'addActualFillerBlock' command.");
		this.oceanTopBlock = this.configFile.getBoolean("Ocean Top Block", "ASM Tweaks", false, "Enables 'oceanTopBlock' in the set command.");
		this.oceanFillerBlock = this.configFile.getBoolean("Ocean Filler Block", "ASM Tweaks", false, "Enables 'oceanFillerBlock' in the set command.");
		this.grassColor = this.configFile.getBoolean("Grass Color", "ASM Tweaks", false, "Enables 'grassColor' in the set command.");
		this.foliageColor = this.configFile.getBoolean("Foliage Color", "ASM Tweaks", false, "Enables 'foliageColor' in the set command.");
		this.waterColor = this.configFile.getBoolean("Water Color", "ASM Tweaks", false, "Enables 'waterColor' in the set command.");
		this.skyColor = this.configFile.getBoolean("Sky Color", "ASM Tweaks", false, "Enables 'skyColor' in the set command.");

		this.removeLateAssignments = this.configFile.getBoolean("Remove Late Block Assignments", "ASM Tweaks", false,
				"Enable this if you want BiomeTweaker to force topBlock and fillerBlock overrides in most biomes. This is done by using ASM"
						+ " to strip some assignments that happen in the 'genTerrainBlocks' method. This will not work for all biomes. This WILL cause"
						+ " issues where some biomes will have incorrect top and filler blocks if you do not override them (Extreme Hills (M), Mutated"
						+ " Savannah, Taiga). light ASM must be disabled for this to have any effect.");
		this.asmBlacklist = this.configFile.getStringList("ASM Blacklist", "ASM", new String[0],
				"This can be used to specify biome classes BiomeTweaker should not touch with ASM. You can find the class for a biome in the output"
						+ " files. You should only be using this if you understand what ASM is, and you know the issue it is causing.");
		if(this.configFile.hasChanged())
			this.configFile.save();
	}

	public void onConfigChange(final OnConfigChangedEvent e){
		if(e.getModID().equals(ModData.MOD_ID))
			this.loadValues();
	}

}
