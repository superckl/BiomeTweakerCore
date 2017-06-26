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
import squeek.asmhelper.me.superckl.biometweakercore.ObfHelper;

@SortingIndex(1001)
@MCVersion("1.12")
@Name("BiomeTweakerCore")
@TransformerExclusions({"me.superckl.biometweakercore", "squeek.asmhelper.me.superckl.biometweakercore"})
public class BiomeTweakerCore implements IFMLLoadingPlugin{

	public static final Logger logger = LogManager.getLogger(ModData.MOD_NAME);
	public static File mcLocation;
	public static boolean modifySuccess;
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
		ObfHelper.setObfuscated((Boolean) data.get("runtimeDeobfuscationEnabled"));
		ObfHelper.setRunsAfterDeobfRemapper(true);
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
