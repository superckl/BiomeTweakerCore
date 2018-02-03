package me.superckl.biometweakercore;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.superckl.biometweakercore.util.ModData;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@SortingIndex(1001)
@MCVersion("1.12.2")
@Name("BiomeTweakerCore")
@TransformerExclusions({"me.superckl.biometweakercore.BiomeTweakerCore", "me.superckl.biometweakercore.BiomeTweakerCallHook",
	"me.superckl.biometweakercore.BiomeTweakerASMTransformer", "me.superckl.biometweakercore.util.CollectionHelper",
	"me.superckl.biometweakercore.util.ObfNameHelper", "me.superckl.biometweakercore.module", "me.superckl.biometweakercore.util.ASMHelper",
	"me.superckl.biometweakercore.util.InsnComparator"})
public class BiomeTweakerCore implements IFMLLoadingPlugin{

	public static final Logger logger = LogManager.getLogger(ModData.MOD_NAME);
	public static File mcLocation;
	public static Config config;

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {BiomeTweakerASMTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return ModBiomeTweakerCore.class.getName();
	}

	@Override
	public String getSetupClass() {
		return BiomeTweakerCallHook.class.getName();
	}

	@Override
	public void injectData(final Map<String, Object> data) {
		BiomeTweakerCore.mcLocation = (File) data.get("mcLocation");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
