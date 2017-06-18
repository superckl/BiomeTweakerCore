package me.superckl.biometweakercore;

import java.io.File;
import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLCallHook;

public class BiomeTweakerCallHook implements IFMLCallHook{

	@Override
	public Void call() throws Exception {
		BiomeTweakerCore.logger.info("Beginning early config parsing...");
		final File operateIn = new File(BiomeTweakerCore.mcLocation, "config/BiomeTweakerCore/");
		BiomeTweakerCore.logger.debug("We are operating in "+operateIn.getAbsolutePath());
		BiomeTweakerCore.config = new Config(operateIn);
		BiomeTweakerCore.config.loadValues();
		return null;
	}

	@Override
	public void injectData(final Map<String, Object> data) {}

}
