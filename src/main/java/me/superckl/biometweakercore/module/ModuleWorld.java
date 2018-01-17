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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import me.superckl.biometweakercore.BiomeTweakerCore;
import me.superckl.biometweakercore.util.ObfNameHelper.Classes;
import me.superckl.biometweakercore.util.ObfNameHelper.Fields;
import me.superckl.biometweakercore.util.ObfNameHelper.Methods;
import squeek.asmhelper.me.superckl.biometweakercore.ASMHelper;

public class ModuleWorld implements IClassTransformerModule{

	@Override
	public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
		if(!BiomeTweakerCore.config.isInitialSnow())
			return bytes;
		final ClassNode cNode = ASMHelper.readClassFromBytes(bytes);
		BiomeTweakerCore.logger.info("Attempting to patch class "+transformedName+"...");
		final MethodNode mNode = ASMHelper.findMethodNodeOfClass(cNode, Methods.CANSNOWATBODY.getName(), Methods.CANSNOWATBODY.getDescriptor());
		if(mNode == null) {
			BiomeTweakerCore.logger.error("Unable to find canSnowAtBody method! Tweak will not be applied.");
			return bytes;
		}
		final AbstractInsnNode aNode = ASMHelper.findFirstInstructionWithOpcode(mNode, Opcodes.ASTORE);
		if(aNode == null) {
			BiomeTweakerCore.logger.error("Unable to find insertion site in canSnowAtBody method! Tweak will not be applied.");
			return bytes;
		}
		final InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 3));
		list.add(Fields.GENINITIALSNOW.toInsnNode(Opcodes.GETFIELD));
		final LabelNode label = new LabelNode();
		list.add(new JumpInsnNode(Opcodes.IFNULL, label));
		list.add(new VarInsnNode(Opcodes.ALOAD, 3));
		list.add(Fields.GENINITIALSNOW.toInsnNode(Opcodes.GETFIELD));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
		list.add(new InsnNode(Opcodes.IRETURN));
		list.add(label);
		list.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

		mNode.instructions.insert(aNode, list);

		BiomeTweakerCore.logger.info("Sucessfully patched "+transformedName+"!");

		return ASMHelper.writeClassToBytes(cNode, ClassWriter.COMPUTE_FRAMES);
	}

	@Override
	public String[] getClassesToTransform() {
		return new String[] {Classes.WORLD.getName()};
	}

	@Override
	public String getModuleName() {
		return "moduleTransformWorld";
	}

	@Override
	public boolean isRequired() {
		return false;
	}

}
