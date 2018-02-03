package me.superckl.biometweakercore.module;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import me.superckl.biometweakercore.BiomeTweakerCore;
import me.superckl.biometweakercore.util.ASMHelper;
import me.superckl.biometweakercore.util.ObfNameHelper.Classes;
import me.superckl.biometweakercore.util.ObfNameHelper.Fields;
import me.superckl.biometweakercore.util.ObfNameHelper.Methods;

public class ModuleBiome implements IClassTransformerModule{

	@Override
	public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
		final ClassNode cNode = ASMHelper.readClassFromBytes(bytes);
		BiomeTweakerCore.logger.info("Attempting to patch class "+transformedName+"...");
		int fixed  = 0;
		int expected = 0;
		if(BiomeTweakerCore.config.isActualFillerBlocks()){
			expected++;
			cNode.fields.add(Fields.ACTUALFILLERBLOCKS.toNode(Opcodes.ACC_PUBLIC, null));
			BiomeTweakerCore.logger.debug("Successfully inserted 'actualFillerBlocks' field into "+transformedName);
			fixed++;
		}
		if(BiomeTweakerCore.config.isOceanTopBlock()){
			expected++;
			cNode.fields.add(Fields.OCEANTOPBLOCK.toNode(Opcodes.ACC_PUBLIC, null));
			BiomeTweakerCore.logger.debug("Successfully inserted 'oceanTopBlock' field into "+transformedName);
			fixed++;
		}
		if(BiomeTweakerCore.config.isOceanFillerBlock()){
			expected++;
			cNode.fields.add(Fields.OCEANFILLERBLOCK.toNode(Opcodes.ACC_PUBLIC, null));
			BiomeTweakerCore.logger.debug("Successfully inserted 'oceanFillerBlock' field into "+transformedName);
			fixed++;
		}
		if(BiomeTweakerCore.config.isGrassColor()){
			expected++;
			cNode.fields.add(Fields.GRASSCOLOR.toNode(Opcodes.ACC_PUBLIC, -1));
			BiomeTweakerCore.logger.debug("Successfully inserted 'grassColor' field into "+transformedName);
			fixed++;
		}
		if(BiomeTweakerCore.config.isFoliageColor()){
			expected++;
			cNode.fields.add(Fields.FOLIAGECOLOR.toNode(Opcodes.ACC_PUBLIC, -1));
			BiomeTweakerCore.logger.debug("Successfully inserted 'foliageColor' field into "+transformedName);
			fixed++;
		}
		if(BiomeTweakerCore.config.isSkyColor()){
			expected++;
			cNode.fields.add(Fields.SKYCOLOR.toNode(Opcodes.ACC_PUBLIC, -1));
			BiomeTweakerCore.logger.debug("Successfully inserted 'skyColor' field into "+transformedName);
			fixed++;
		}
		if(BiomeTweakerCore.config.isFogColor()){
			expected++;
			cNode.fields.add(Fields.FOGCOLOR.toNode(Opcodes.ACC_PUBLIC, -1));
			BiomeTweakerCore.logger.debug("Successfully inserted 'fogColor' field into "+transformedName);
			fixed++;
		}
		if(BiomeTweakerCore.config.isInitialSnow()){
			expected++;
			cNode.fields.add(Fields.GENINITIALSNOW.toNode(Opcodes.ACC_PUBLIC, null));
			BiomeTweakerCore.logger.debug("Successfully inserted 'genInitialSnow' field into "+transformedName);
			fixed++;
		}
		boolean sky = false;
		for(final MethodNode node:cNode.methods)
			if(Methods.GENBIOMETERRAIN.matches(node)){
				InsnList toFind;
				if (BiomeTweakerCore.config.isActualFillerBlocks()) {
					expected++;
					toFind = new InsnList();
					toFind.add(new VarInsnNode(Opcodes.ALOAD, 17));
					toFind.add(Methods.GETBLOCK.toInsnNode(Opcodes.INVOKEINTERFACE));
					toFind.add(Fields.STONE.toInsnNode(Opcodes.GETSTATIC));
					final AbstractInsnNode found = ASMHelper.find(node.instructions, toFind);
					if (found != null) {
						final AbstractInsnNode someNode = found.getNext().getNext().getNext();
						if ((someNode instanceof JumpInsnNode) && (someNode.getOpcode() == Opcodes.IF_ACMPNE)) {
							((JumpInsnNode) someNode).setOpcode(Opcodes.IFEQ);
							final InsnList toInsert = new InsnList();
							toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
							toInsert.add(Fields.ACTUALFILLERBLOCKS.toInsnNode(Opcodes.GETFIELD));
							toInsert.add(new VarInsnNode(Opcodes.ALOAD, 17));
							toInsert.add(Methods.CONTAINS.toInsnNode(Opcodes.INVOKESTATIC));
							if (ASMHelper.findAndReplace(node.instructions, toFind, toInsert) != null) {
								BiomeTweakerCore.logger
								.debug("Successfully redirected 'Stone' check to 'contains' method.");
								fixed++;
							}
						}
					}
				}
				if (BiomeTweakerCore.config.isOceanFillerBlock() || BiomeTweakerCore.config.isOceanTopBlock()) {
					toFind = new InsnList();
					toFind.add(new VarInsnNode(Opcodes.ALOAD, 3));
					toFind.add(new VarInsnNode(Opcodes.ILOAD, 14));
					toFind.add(new VarInsnNode(Opcodes.ILOAD, 16));
					toFind.add(new VarInsnNode(Opcodes.ILOAD, 13));
					toFind.add(Fields.BIOMEGENBASE_GRAVEL.toInsnNode(Opcodes.GETSTATIC));
					toFind.add(Methods.SETBLOCKSTATE.toInsnNode(Opcodes.INVOKEVIRTUAL));
					AbstractInsnNode aNode = ASMHelper.find(node.instructions, toFind);
					if (aNode != null) {
						final AbstractInsnNode aaNode = ASMHelper.findNextInstructionWithOpcode(aNode,
								Opcodes.GETSTATIC);
						if (aaNode != null) {
							if (BiomeTweakerCore.config.isOceanTopBlock()) {
								expected++;
								final InsnList toInsert = new InsnList();
								toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
								toInsert.add(Fields.OCEANTOPBLOCK.toInsnNode(Opcodes.GETFIELD));
								node.instructions.insert(aaNode, toInsert);
								node.instructions.remove(aaNode);
								BiomeTweakerCore.logger.debug("Successfully inserted 'oceanTopBlock' instructions.");
								fixed++;
							}
							if (BiomeTweakerCore.config.isOceanFillerBlock()) {
								expected++;
								aNode = ASMHelper.findPreviousInstructionWithOpcode(aNode, Opcodes.GETSTATIC);
								toFind = new InsnList();
								toFind.add(Fields.BIOMEGENBASE_STONE.toInsnNode(Opcodes.GETSTATIC));
								toFind.add(new VarInsnNode(Opcodes.ASTORE, 10));
								aNode = ASMHelper.find(aNode, toFind);
								if (aNode != null) {
									final InsnList toInsert1 = new InsnList();
									toInsert1.add(new VarInsnNode(Opcodes.ALOAD, 0));
									toInsert1.add(Fields.OCEANFILLERBLOCK.toInsnNode(Opcodes.GETFIELD));
									node.instructions.insert(aNode, toInsert1);
									node.instructions.remove(aNode);
									BiomeTweakerCore.logger
									.debug("Successfully inserted 'oceanFillerBlock' instructions.");
									fixed++;
								}
							}
						}
					}
				}
			}else if(BiomeTweakerCore.config.isSkyColor() && Methods.GETSKYCOLORBYTEMP.matches(node)){
				final InsnList list = new InsnList();
				list.add(new VarInsnNode(Opcodes.ALOAD, 0));
				list.add(Fields.SKYCOLOR.toInsnNode(Opcodes.GETFIELD));
				list.add(new InsnNode(Opcodes.ICONST_M1));
				final LabelNode label = new LabelNode();
				list.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, label));
				list.add(new VarInsnNode(Opcodes.ALOAD, 0));
				list.add(Fields.SKYCOLOR.toInsnNode(Opcodes.GETFIELD));
				list.add(new InsnNode(Opcodes.IRETURN));
				list.add(label);
				list.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
				node.instructions.insertBefore(node.instructions.getFirst(), list);
				BiomeTweakerCore.logger.debug("Successfully inserted sky color instructions.");
				sky = true;
			}else if(Methods.BIOME_CONSTRUCTOR.matches(node)){
				InsnList list;
				if (BiomeTweakerCore.config.isActualFillerBlocks()) {
					expected++;
					list = new InsnList();
					list.add(new VarInsnNode(Opcodes.ALOAD, 0));
					list.add(new InsnNode(Opcodes.ICONST_1));
					list.add(new TypeInsnNode(Opcodes.ANEWARRAY, Classes.IBLOCKSTATE.getInternalName()));
					list.add(new InsnNode(Opcodes.DUP));
					list.add(new InsnNode(Opcodes.ICONST_0));
					list.add(Fields.STONE.toInsnNode(Opcodes.GETSTATIC));
					list.add(Methods.GETDEFAULTSTATE.toInsnNode(Opcodes.INVOKEVIRTUAL));
					list.add(new InsnNode(Opcodes.AASTORE));
					list.add(Fields.ACTUALFILLERBLOCKS.toInsnNode(Opcodes.PUTFIELD));
					node.instructions.insert(list);
					BiomeTweakerCore.logger.debug("Successfully inserted empty array into 'actualFillerBlocks'");
					fixed++;
				}
				if (BiomeTweakerCore.config.isOceanFillerBlock()) {
					expected++;
					list = new InsnList();
					list.add(new VarInsnNode(Opcodes.ALOAD, 0));
					list.add(Fields.BIOMEGENBASE_STONE.toInsnNode(Opcodes.GETSTATIC));
					list.add(Fields.OCEANFILLERBLOCK.toInsnNode(Opcodes.PUTFIELD));
					node.instructions.insert(list);
					BiomeTweakerCore.logger.debug("Successfully inserted stone into 'oceanFillerBlock'");
					fixed++;
				}
				if (BiomeTweakerCore.config.isOceanTopBlock()) {
					expected++;
					list = new InsnList();
					list.add(new VarInsnNode(Opcodes.ALOAD, 0));
					list.add(Fields.BIOMEGENBASE_GRAVEL.toInsnNode(Opcodes.GETSTATIC));
					list.add(Fields.OCEANTOPBLOCK.toInsnNode(Opcodes.PUTFIELD));
					node.instructions.insert(list);
					BiomeTweakerCore.logger.debug("Successfully inserted gravel into 'oceanTopBlock'");
					fixed++;
				}
				if (BiomeTweakerCore.config.isGrassColor()) {
					expected++;
					list = new InsnList();
					list.add(new VarInsnNode(Opcodes.ALOAD, 0));
					list.add(new InsnNode(Opcodes.ICONST_M1));
					list.add(Fields.GRASSCOLOR.toInsnNode(Opcodes.PUTFIELD));
					node.instructions.insert(list);
					BiomeTweakerCore.logger.debug("Successfully inserted -1 into 'grassColor'");
					fixed++;
				}
				if (BiomeTweakerCore.config.isFoliageColor()) {
					expected++;
					list = new InsnList();
					list.add(new VarInsnNode(Opcodes.ALOAD, 0));
					list.add(new InsnNode(Opcodes.ICONST_M1));
					list.add(Fields.FOLIAGECOLOR.toInsnNode(Opcodes.PUTFIELD));
					node.instructions.insert(list);
					BiomeTweakerCore.logger.debug("Successfully inserted -1 into 'foliageColor'");
					fixed++;
				}
				if (BiomeTweakerCore.config.isSkyColor()) {
					expected++;
					list = new InsnList();
					list.add(new VarInsnNode(Opcodes.ALOAD, 0));
					list.add(new InsnNode(Opcodes.ICONST_M1));
					list.add(Fields.SKYCOLOR.toInsnNode(Opcodes.PUTFIELD));
					node.instructions.insert(list);
					BiomeTweakerCore.logger.debug("Successfully inserted -1 into 'skyColor'");
					fixed++;
				}
				if (BiomeTweakerCore.config.isFogColor()) {
					expected++;
					list = new InsnList();
					list.add(new VarInsnNode(Opcodes.ALOAD, 0));
					list.add(new InsnNode(Opcodes.ICONST_M1));
					list.add(Fields.FOGCOLOR.toInsnNode(Opcodes.PUTFIELD));
					node.instructions.insert(list);
					BiomeTweakerCore.logger.debug("Successfully inserted -1 into 'fogColor'");
					fixed++;
				}
			}
		if(BiomeTweakerCore.config.isSkyColor() && !sky)
			BiomeTweakerCore.logger.warn("Failed to insert sky color instructions. If this is a server, don't worry. If if this a client, worry. A lot.");

		if(fixed < expected){
			BiomeTweakerCore.logger.error("Failed to completely patch "+transformedName+"! Only "+fixed+" patches were processed. Ye who continue now abandon all hope.");
			BiomeTweakerCore.logger.error("Seriously, this is really bad. Things are probably going to break.");
		}
		else if(fixed > expected)
			BiomeTweakerCore.logger.warn("Sucessfully patched "+transformedName+", but "+fixed+" patches were applied when we were expecting "+ expected
					+ ". Is something else also patching this class?");
		else
			BiomeTweakerCore.logger.info("Sucessfully patched "+transformedName+"! "+fixed+" patches were applied.");
		return ASMHelper.writeClassToBytes(cNode);
	}

	@Override
	public String[] getClassesToTransform() {
		return new String[] {Classes.BIOME.getName()};
	}

	@Override
	public String getModuleName() {
		return "moduleTransformBiome";
	}

	@Override
	public boolean isRequired() {
		return true;
	}

}
