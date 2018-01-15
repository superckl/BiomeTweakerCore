package me.superckl.biometweakercore.util;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientHelper {

	public static Biome getBiome() {
		return Minecraft.getMinecraft().world.getBiome(Minecraft.getMinecraft().player.getPosition());
	}
	
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_)
    {
        float f = MathHelper.cos(p_76562_1_ * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        if(Biomes.FOREST.getWaterColorMultiplier() != -1) {
        	return BiomeHooks.calcFogColor(Biomes.FOREST.getWaterColorMultiplier(), f);
        }
        float f1 = 0.7529412F;
        float f2 = 0.84705883F;
        float f3 = 1.0F;
        f1 = f1 * (f * 0.94F + 0.06F);
        f2 = f2 * (f * 0.94F + 0.06F);
        f3 = f3 * (f * 0.91F + 0.09F);
        return new Vec3d(f1, f2, f3);
    }
	
}
