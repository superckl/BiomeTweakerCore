package me.superckl.biometweakercore;

import java.util.Set;

import com.google.common.collect.Sets;

import me.superckl.biometweakercore.module.IClassTransformerModule;
import me.superckl.biometweakercore.module.ModuleBiomeGenBase;
import me.superckl.biometweakercore.module.ModuleBiomeGenBaseSubclass;
import me.superckl.biometweakercore.util.CollectionHelper;
import net.minecraft.launchwrapper.IClassTransformer;

public class BiomeTweakerASMTransformer implements IClassTransformer{

	private final Set<IClassTransformerModule> modules = Sets.newIdentityHashSet();

	public BiomeTweakerASMTransformer() {
		this.registerModule(new ModuleBiomeGenBase());
		this.registerModule(new ModuleBiomeGenBaseSubclass());
	}

	public void registerModule(final IClassTransformerModule module){
		this.modules.add(module);
	}

	@Override
	public byte[] transform(final String name, final String transformedName, byte[] basicClass) {
		if(BiomeTweakerCore.config == null || basicClass == null || (CollectionHelper.find(transformedName, BiomeTweakerCore.config.getAsmBlacklist()) != -1))
			return basicClass;
		for(final IClassTransformerModule module:this.modules)
			for(final String clazz:module.getClassesToTransform())
				if(clazz.equals("*") || clazz.equals(transformedName))
					try{
						final byte[] newBytes = module.transform(name, transformedName, basicClass);
						basicClass = newBytes;
					}catch(final Exception e){
						BiomeTweakerCore.logger.error("Caught an exception from module "+module.getModuleName());
						e.printStackTrace();
					}
		return basicClass;
	}

}
