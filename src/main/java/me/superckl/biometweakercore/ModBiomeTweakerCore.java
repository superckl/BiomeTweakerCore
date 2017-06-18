package me.superckl.biometweakercore;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import me.superckl.biometweakercore.util.ModData;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModBiomeTweakerCore extends DummyModContainer{

	//TODO The jar is signed with fingerprint in ModData, but there's no way to declare it.

	public ModBiomeTweakerCore() {
		super(new ModMetadata());
		final ModMetadata meta = this.getMetadata();
		meta.modId = ModData.MOD_ID;
		meta.name = ModData.MOD_NAME;
		meta.version = ModData.VERSION;
		meta.authorList = Lists.newArrayList("superckl");
	}

	@Subscribe
	public void onPreInit(final FMLPreInitializationEvent e){
		if(!Loader.isModLoaded("biometweaker")){
			BiomeTweakerCore.logger.warn("BiomeTweaker is not loaded... Why are you running BiomeTweakerCore?");
			return;
		}

		BiomeTweakerCore.logger.debug("Sending ASM property messages to BiomeTweaker...");
		if(BiomeTweakerCore.config.isActualFillerBlocks())
			FMLInterModComms.sendRuntimeMessage(this, "biometweaker", "enableTweak", "actualFillerBlocks");
		if(BiomeTweakerCore.config.isOceanTopBlock())
			FMLInterModComms.sendRuntimeMessage(this, "biometweaker", "enableTweak", "oceanTopBlock");
		if(BiomeTweakerCore.config.isOceanFillerBlock())
			FMLInterModComms.sendRuntimeMessage(this, "biometweaker", "enableTweak", "oceanFillerBlock");
		if(BiomeTweakerCore.config.isGrassColor())
			FMLInterModComms.sendRuntimeMessage(this, "biometweaker", "enableTweak", "grassColor");
		if(BiomeTweakerCore.config.isFoliageColor())
			FMLInterModComms.sendRuntimeMessage(this, "biometweaker", "enableTweak", "foliageColor");
		if(BiomeTweakerCore.config.isSkyColor())
			FMLInterModComms.sendRuntimeMessage(this, "biometweaker", "enableTweak", "skyColor");
	}

	@Override
	public boolean registerBus(final EventBus bus, final LoadController controller) {
		bus.register(this);
		return true;
	}

	@Override
	public Object getMod() {
		return this;
	}

}
