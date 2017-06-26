package me.superckl.biometweakercore.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent.GetFoliageColor;
import net.minecraftforge.event.terraingen.BiomeEvent.GetGrassColor;
import net.minecraftforge.event.terraingen.BiomeEvent.GetWaterColor;

public class BiomeHooks {

	public static boolean contains(final IBlockState[] blocks, final IBlockState block){
		//LogHelper.info("Called for "+block+"in "+Arrays.toString(blocks));
		for(final IBlockState search:blocks)
			if(search.getBlock() == block.getBlock() && search.getBlock().getMetaFromState(search) == block.getBlock().getMetaFromState(block))
				return true;
		return false;
	}


	public static int callGrassColorEvent(final int color, final Biome gen){
		final GetGrassColor e = new GetGrassColor(gen, color);
		MinecraftForge.EVENT_BUS.post(e);
		return e.getNewColor();
	}

	public static int callFoliageColorEvent(final int color, final Biome gen){
		final GetFoliageColor e = new GetFoliageColor(gen, color);
		MinecraftForge.EVENT_BUS.post(e);
		return e.getNewColor();
	}

	public static int callWaterColorEvent(final int color, final Biome gen){
		final GetWaterColor e = new GetWaterColor(gen, color);
		MinecraftForge.EVENT_BUS.post(e);
		return e.getNewColor();
	}

}
