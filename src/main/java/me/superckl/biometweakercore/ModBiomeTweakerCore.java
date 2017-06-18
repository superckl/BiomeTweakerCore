package me.superckl.biometweakercore;

import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import me.superckl.biometweakercore.util.ModData;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModBiomeTweakerCore extends DummyModContainer{

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

		BiomeTweakerCore.logger.debug("Setting ASM properties for BiomeTweaker...");
		final Map<String, String> properties = Loader.instance().getCustomModProperties("biometweaker");
		properties.put("actualFillerBlocks", Boolean.toString(BiomeTweakerCore.config.isActualFillerBlocks()));
		properties.put("oceanTopBlock", Boolean.toString(BiomeTweakerCore.config.isOceanTopBlock()));
		properties.put("oceanFillerBlock", Boolean.toString(BiomeTweakerCore.config.isOceanFillerBlock()));
		properties.put("grassColor", Boolean.toString(BiomeTweakerCore.config.isGrassColor()));
		properties.put("foliageColor", Boolean.toString(BiomeTweakerCore.config.isFoliageColor()));
		properties.put("skyColor", Boolean.toString(BiomeTweakerCore.config.isSkyColor()));
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
