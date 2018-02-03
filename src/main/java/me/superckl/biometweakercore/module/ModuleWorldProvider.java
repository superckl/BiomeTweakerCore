package me.superckl.biometweakercore.module;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import me.superckl.biometweakercore.BiomeTweakerCore;
import me.superckl.biometweakercore.util.ASMHelper;
import me.superckl.biometweakercore.util.ObfNameHelper.Classes;
import me.superckl.biometweakercore.util.ObfNameHelper.Fields;
import me.superckl.biometweakercore.util.ObfNameHelper.Methods;

public class ModuleWorldProvider implements IClassTransformerModule{

	@Override
	public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
		if(!BiomeTweakerCore.config.isFogColor())
			return bytes;
		final ClassNode cNode = ASMHelper.readClassFromBytes(bytes);
		BiomeTweakerCore.logger.info("Attempting to patch class "+transformedName+"...");
		final MethodNode mNode = ASMHelper.findMethodNodeOfClass(cNode, Methods.GETFOGCOLOR.getName(), Methods.GETFOGCOLOR.getDescriptor());
		if(mNode == null) {
			BiomeTweakerCore.logger.error("Unable to find getFogColor method! Tweak will not be applied.");
			return bytes;
		}
		final AbstractInsnNode aNode = ASMHelper.find(ASMHelper.findFirstInstructionWithOpcode(mNode, Opcodes.FSTORE).getNext(),
				new VarInsnNode(Opcodes.FSTORE, 3));
		if(aNode == null) {
			BiomeTweakerCore.logger.error("Unable to find insertion site in getFogColor method! Tweak will not be applied.");
			return bytes;
		}
		final InsnList list = new InsnList();
		list.add(Methods.GETBIOME.toInsnNode(Opcodes.INVOKESTATIC));
		list.add(Fields.FOGCOLOR.toInsnNode(Opcodes.GETFIELD));
		list.add(new InsnNode(Opcodes.ICONST_M1));
		final LabelNode label = new LabelNode();
		list.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, label));
		list.add(Methods.GETBIOME.toInsnNode(Opcodes.INVOKESTATIC));
		list.add(Fields.FOGCOLOR.toInsnNode(Opcodes.GETFIELD));
		list.add(new VarInsnNode(Opcodes.FLOAD, 3));
		list.add(Methods.CALCFOGCOLOR.toInsnNode(Opcodes.INVOKESTATIC));
		list.add(new InsnNode(Opcodes.ARETURN));
		list.add(label);
		list.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

		mNode.instructions.insert(aNode, list);

		BiomeTweakerCore.logger.info("Sucessfully patched "+transformedName+"!");

		return ASMHelper.writeClassToBytes(cNode, ClassWriter.COMPUTE_FRAMES);
	}

	@Override
	public String[] getClassesToTransform() {
		return new String[] {Classes.WORLDPROVIDER.getName()};
	}

	@Override
	public String getModuleName() {
		return "moduleTransformWorldProvider";
	}

	@Override
	public boolean isRequired() {
		return false;
	}

}
