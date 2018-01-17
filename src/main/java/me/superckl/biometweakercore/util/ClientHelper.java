package me.superckl.biometweakercore.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientHelper {
	
	public static Biome getBiome() {
		return Minecraft.getMinecraft().world.getBiome(Minecraft.getMinecraft().player.getPosition());
	}
	
}
