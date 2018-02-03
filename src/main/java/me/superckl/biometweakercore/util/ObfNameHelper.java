package me.superckl.biometweakercore.util;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import squeek.asmhelper.me.superckl.biometweakercore.ASMHelper;

public final class ObfNameHelper {

	@RequiredArgsConstructor
	public static enum Classes{

		BIOMEHOOKS("me.superckl.biometweakercore.util.BiomeHooks"),
		CLIENTHELPER("me.superckl.biometweakercore.util.ClientHelper"),
		BIOME("net.minecraft.world.biome.Biome"),
		IBLOCKSTATE("net.minecraft.block.state.IBlockState"),
		BLOCK("net.minecraft.block.Block"),
		CHUNKPRIMER("net.minecraft.world.chunk.ChunkPrimer"),
		WORLDPROVIDER("net.minecraft.world.WorldProvider"),
		WORLD("net.minecraft.world.World"),
		BLOCKS("net.minecraft.init.Blocks");

		@Getter
		private final String name;

		public String getInternalName(){
			return ASMHelper.toInternalClassName(this.name);
		}
	}

	@RequiredArgsConstructor
	public enum Methods{

		BIOME_CONSTRUCTOR(Classes.BIOME, "<init>", "(Lnet/minecraft/world/biome/Biome$BiomeProperties;)V", false),
		CALLGRASSCOLOREVENT(Classes.BIOMEHOOKS, "callGrassColorEvent", "(ILnet/minecraft/world/biome/Biome;)I", false),
		CALLFOLIAGECOLOREVENT(Classes.BIOMEHOOKS, "callFoliageColorEvent", "(ILnet/minecraft/world/biome/Biome;)I", false),
		CALLWATERCOLOREVENT(Classes.BIOMEHOOKS, "callWaterColorEvent", "(ILnet/minecraft/world/biome/Biome;)I", false),
		CONTAINS(Classes.BIOMEHOOKS, "contains", "([Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;)Z", false),
		CALCFOGCOLOR(Classes.BIOMEHOOKS, "calcFogColor", "(IF)Lnet/minecraft/util/math/Vec3d;", false),
		GETBIOME(Classes.CLIENTHELPER, "getBiome", "()Lnet/minecraft/world/biome/Biome;", false),
		GENBIOMETERRAIN(Classes.BIOME, "func_180628_b", "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/chunk/ChunkPrimer;IID)V", false),
		GENTERRAINBLOCKS(Classes.BIOME, "func_180622_a", "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/chunk/ChunkPrimer;IID)V", false),
		GETBIOMEGRASSCOLOR(Classes.BIOME, "func_180627_b", "(Lnet/minecraft/util/math/BlockPos;)I", false),
		GETBIOMEFOLIAGECOLOR(Classes.BIOME, "func_180625_c", "(Lnet/minecraft/util.math/BlockPos;)I", false),
		GETBLOCK(Classes.IBLOCKSTATE, "func_177230_c", "()Lnet/minecraft/block/Block;", true),
		GETDEFAULTSTATE(Classes.BLOCK, "func_176223_P", "()Lnet/minecraft/block/state/IBlockState;", false),
		SETBLOCKSTATE(Classes.CHUNKPRIMER, "func_177855_a", "(IIILnet/minecraft/block/state/IBlockState;)V", false),
		GETFOGCOLOR(Classes.WORLDPROVIDER, "func_76562_b", "(FF)Lnet/minecraft/util/math/Vec3d;", false),
		CANSNOWATBODY(Classes.WORLD, "canSnowAtBody", "(Lnet/minecraft/util/math/BlockPos;Z)Z", false),
		GETMODDEDBIOMEGRASSCOLOR(Classes.BIOME, "getModdedBiomeGrassColor", "(I)I", false),
		GETMODDEDBIOMEFOLIAGECOLOR(Classes.BIOME, "getModdedBiomeFoliageColor", "(I)I", false),
		GETWATERCOLORMULTIPLIER(Classes.BIOME, "getWaterColorMultiplier", "()I", false),
		GETSKYCOLORBYTEMP(Classes.BIOME, "func_76731_a", "(F)I", false);

		private final Classes clazz;
		private final String name;
		@Getter
		private final String descriptor;
		private final boolean isInterface;

		public String getName(){
			final String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(this.clazz.getInternalName());
			return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(internalClassName, this.name, this.getDescriptor());
		}

		public MethodInsnNode toInsnNode(final int opCode){
			return new MethodInsnNode(opCode, this.clazz.getInternalName(), this.getName(), this.getDescriptor(), this.isInterface);
		}

		public boolean matches(final MethodNode node){
			return node.name.equals(this.getName()) && node.desc.equals(this.getDescriptor());
		}

		public boolean matches(final MethodInsnNode node){
			return node.name.equals(this.getName()) && node.desc.equals(this.getDescriptor()) && node.owner.equals(this.clazz.getInternalName());
		}

	}

	@RequiredArgsConstructor
	public enum Fields {

		ACTUALFILLERBLOCKS(Classes.BIOME, "actualFillerBlocks", "[Lnet/minecraft/block/state/IBlockState;"),
		STONE(Classes.BLOCKS, "field_150348_b", "Lnet/minecraft/block/Block;"),
		BIOMEGENBASE_STONE(Classes.BIOME, "field_185365_a", "Lnet/minecraft/block/state/IBlockState;"),
		WATER(Classes.BLOCKS, "field_150355_j", "Lnet/minecraft/block/Block;"),
		GRAVEL(Classes.BLOCKS, "field_150351_n", "Lnet/minecraft/block/Block;"),
		BIOMEGENBASE_GRAVEL(Classes.BIOME, "field_185368_d", "Lnet/minecraft/block/state/IBlockState;"),
		TOPBLOCK(Classes.BIOME, "field_76752_A", "Lnet/minecraft/block/state/IBlockState;"),
		FILLERBLOCK(Classes.BIOME, "field_76753_B", "Lnet/minecraft/block/state/IBlockState;"),
		OCEANTOPBLOCK(Classes.BIOME, "oceanTopBlock", "Lnet/minecraft/block/state/IBlockState;"),
		OCEANFILLERBLOCK(Classes.BIOME, "oceanFillerBlock", "Lnet/minecraft/block/state/IBlockState;"),
		GRASSCOLOR(Classes.BIOME, "grassColor", "I"),
		FOLIAGECOLOR(Classes.BIOME, "foliageColor", "I"),
		SKYCOLOR(Classes.BIOME, "skyColor", "I"),
		FOGCOLOR(Classes.BIOME, "fogColor", "I"),
		GENINITIALSNOW(Classes.BIOME, "genInitialSnow", "Ljava/lang/Boolean;"),
		BIOMENAME(Classes.BIOME, "field_76791_y", "Ljava/lang/String;"),
		BASEHEIGHT(Classes.BIOME, "field_76748_D", "F"),
		HEIGHTVARIATION(Classes.BIOME, "field_76749_E", "F"),
		TEMPERATURE(Classes.BIOME, "field_76750_F", "F"),
		RAINFALL(Classes.BIOME, "field_76751_G", "F"),
		WATERCOLOR(Classes.BIOME, "field_76759_H", "I"),
		ENABLESNOW(Classes.BIOME, "field_76766_R", "Z"),
		ENABLERAIN(Classes.BIOME, "field_76765_S", "Z");

		private final Classes clazz;
		private final String name;
		@Getter
		private final String descriptor;

		public String getName(){
			return ObfuscationReflectionHelper.remapFieldNames(this.clazz.getName(), this.name)[0];
		}

		public FieldNode toNode(final int opCode, final Object value){
			return new FieldNode(opCode, this.getName(), this.getDescriptor(), this.getDescriptor(), value);
		}

		public FieldInsnNode toInsnNode(final int opCode){
			return new FieldInsnNode(opCode, this.clazz.getInternalName(), this.getName(), this.getDescriptor());
		}

		public boolean matches(final FieldNode node){
			return node.name.equals(this.getName()) && node.desc.equals(this.getDescriptor());
		}

		public boolean matches(final FieldInsnNode node){
			return this.matches(node, false);
		}

		public boolean matches(final FieldInsnNode node, final boolean isSubclass){
			return node.name.equals(this.getName()) && node.desc.equals(this.getDescriptor()) && (isSubclass || node.owner.equals(this.clazz.getInternalName()));
		}


	}

}
