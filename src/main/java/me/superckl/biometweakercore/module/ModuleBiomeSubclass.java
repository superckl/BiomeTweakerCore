package me.superckl.biometweakercore.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import me.superckl.biometweakercore.BiomeTweakerCore;
import me.superckl.biometweakercore.util.ASMHelper;
import me.superckl.biometweakercore.util.ObfNameHelper.Classes;
import me.superckl.biometweakercore.util.ObfNameHelper.Fields;
import me.superckl.biometweakercore.util.ObfNameHelper.Methods;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class ModuleBiomeSubclass implements IClassTransformerModule{

	@Override
	public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
		final ClassReader reader = new ClassReader(basicClass);
		if(ASMHelper.doesClassExtend(reader, FMLDeobfuscatingRemapper.INSTANCE.unmap(Classes.BIOME.getInternalName()))){
			final ClassNode cNode = new ClassNode();
			reader.accept(cNode, 0);
			for(final MethodNode mNode:cNode.methods)
				if(BiomeTweakerCore.config.isGrassColor() && Methods.GETBIOMEGRASSCOLOR.matches(mNode)){
					boolean shouldCont = false;
					final AbstractInsnNode aNode = mNode.instructions.get(mNode.instructions.size()-2);
					if(aNode instanceof MethodInsnNode){
						final MethodInsnNode methNode = (MethodInsnNode) aNode;
						if(Methods.GETMODDEDBIOMEGRASSCOLOR.matches(methNode)){
							shouldCont = true;
							break;
						}
					}
					if(shouldCont)
						continue;
					BiomeTweakerCore.logger.debug("Found Biome subclass "+transformedName+" with overriden grass color method and no event call. Attempting to force modded color event call...");
					for(final AbstractInsnNode aINode:this.findReturnNodes(mNode.instructions)){
						final InsnList list = new InsnList();
						list.add(new VarInsnNode(Opcodes.ALOAD, 0));
						list.add(Methods.CALLGRASSCOLOREVENT.toInsnNode(Opcodes.INVOKESTATIC));
						mNode.instructions.insertBefore(aINode, list);
					}
				}else if(BiomeTweakerCore.config.isFoliageColor() && Methods.GETBIOMEFOLIAGECOLOR.matches(mNode)){
					boolean shouldCont = false;
					final AbstractInsnNode aNode = mNode.instructions.get(mNode.instructions.size()-2);
					if(aNode instanceof MethodInsnNode){
						final MethodInsnNode methNode = (MethodInsnNode) aNode;
						if(Methods.GETMODDEDBIOMEFOLIAGECOLOR.matches(methNode)){
							shouldCont = true;
							break;
						}
					}
					if(shouldCont)
						continue;
					BiomeTweakerCore.logger.debug("Found Biome subclass "+transformedName+" with overriden foliage color method and no event call. Attempting to force modded color event call...");
					for(final AbstractInsnNode aINode:this.findReturnNodes(mNode.instructions)){
						final InsnList list = new InsnList();
						list.add(new VarInsnNode(Opcodes.ALOAD, 0));
						list.add(Methods.CALLFOLIAGECOLOREVENT.toInsnNode(Opcodes.INVOKESTATIC));
						mNode.instructions.insertBefore(aINode, list);
					}
				}else if(BiomeTweakerCore.config.isWaterColor() && Methods.GETWATERCOLORMULTIPLIER.matches(mNode)){
					BiomeTweakerCore.logger.debug("Found Biome subclass "+transformedName+" with overriden water color method. Attempting to force modded color event call...");
					for(final AbstractInsnNode aINode:this.findReturnNodes(mNode.instructions)){
						final InsnList list = new InsnList();
						list.add(new VarInsnNode(Opcodes.ALOAD, 0));
						list.add(Methods.CALLWATERCOLOREVENT.toInsnNode(Opcodes.INVOKESTATIC));
						mNode.instructions.insertBefore(aINode, list);
					}
				}else if(BiomeTweakerCore.config.isRemoveLateAssignments() && Methods.GENTERRAINBLOCKS.matches(mNode)){
					//TODO misses many plausible cases, but catches all vanilla cases.
					AbstractInsnNode node = ASMHelper.findFirstInstruction(mNode);
					AbstractInsnNode nextNode = node;
					int removed = 0;
					do
						try {
							nextNode = node.getNext();
							if((node instanceof FieldInsnNode) == false)
								continue;
							final FieldInsnNode fNode = (FieldInsnNode) node;
							if(Fields.TOPBLOCK.matches(fNode, true) || Fields.FILLERBLOCK.matches(fNode, true)){
								AbstractInsnNode prevNode = ASMHelper.findPreviousInstruction(fNode);
								if((prevNode != null) && (prevNode instanceof MethodInsnNode) && (prevNode.getOpcode() == Opcodes.INVOKEVIRTUAL || prevNode.getOpcode() == Opcodes.INVOKEINTERFACE)){
									MethodInsnNode prevMNode = (MethodInsnNode) prevNode;
									//Only tests for getDefaultState...
									if(!(Methods.GETDEFAULTSTATE.matches(prevMNode, true))){
										//skip exactly three nodes to test for lines with simple withProperty calls
										prevNode = ASMHelper.findPreviousInstruction(ASMHelper.findPreviousInstruction(ASMHelper.findPreviousInstruction(prevMNode)));
										if(prevNode == null || prevNode instanceof MethodInsnNode == false || prevNode.getOpcode() != Opcodes.INVOKEVIRTUAL)
											continue;
										prevMNode = (MethodInsnNode) prevNode;
									}
									if(Methods.GETDEFAULTSTATE.matches(prevMNode, true)){
										prevNode = ASMHelper.findPreviousInstruction(prevMNode);
										if(prevNode != null && prevNode instanceof FieldInsnNode && prevNode.getOpcode() == Opcodes.GETSTATIC){
											final FieldInsnNode prevFNode = (FieldInsnNode) prevNode;
											if(prevFNode.desc.equals(ASMHelper.toDescriptor(Classes.BLOCK.getInternalName())) || ASMHelper.doesClassExtend(ASMHelper.getClassReaderForClassName(Type.getType(prevFNode.desc).getClassName()), Classes.BLOCK.getInternalName())){
												prevNode = ASMHelper.findPreviousInstruction(prevFNode);
												if((prevNode != null) && (prevNode instanceof VarInsnNode) && (prevNode.getOpcode() == Opcodes.ALOAD) && (((VarInsnNode)prevNode).var == 0)){
													ASMHelper.removeFromInsnListUntil(mNode.instructions, prevNode, nextNode);
													removed++;
												}
											}
										}
									}
								}
							}
						} catch (final IOException e) {
							e.printStackTrace();
							//Swallow, it doesn't extend Block
						}
					while((node = ASMHelper.findNextInstructionWithOpcode(nextNode, Opcodes.PUTFIELD)) != null);
					if(removed > 0){
						if(name.startsWith("net.minecraft.world.biome."))
							BiomeTweakerCore.logger.warn("Found vanilla Biome subclass "+transformedName+" that was setting topBlock"
									+ " or fillerBlock in genTerrainBlocks!"+removed+" items were removed. If this is not a vanilla biome,"
									+ " please let me (superckl) know.");
						else
							BiomeTweakerCore.logger.warn("Found Biome subclass "+transformedName+" that was setting topBlock or fillerBlock"
									+ " in genTerrainBlocks! This is bad practice and breaks functionality in BiomeTweaker! "+removed+" items"
									+ " were removed. If this is a vanilla biome, please let me (superckl) know.");
						BiomeTweakerCore.logger.info("If you feel the removal of this is causing issues, add this class to"
								+ " the ASM blacklist in the config and let me know.");
					}
				}
			return ASMHelper.writeClassToBytes(cNode);
		}
		return basicClass;
	}

	@Override
	public String[] getClassesToTransform() {
		return new String[] {"*"};
	}

	@Override
	public String getModuleName() {
		return "moduleTransformBiomeSubclass";
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	private List<AbstractInsnNode> findReturnNodes(final InsnList instructions){
		final List<AbstractInsnNode> list = new ArrayList<>();
		for(int i = instructions.size()-1; i >= 0; i--)
			if(instructions.get(i).getOpcode() == Opcodes.IRETURN)
				list.add(instructions.get(i));
		return list;
	}

}
