package me.superckl.biometweakercore.util;

import java.io.Serializable;
import java.util.Comparator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * This class is derived from Squeek502's ASMHelper project. Unneeded functionality has been removed. Find ASMHelper here:
 * https://github.com/squeek502/ASMHelper
 */
public class InsnComparator implements Comparator<AbstractInsnNode>, Serializable
{
	private static final long serialVersionUID = 408241651446425342L;
	public static final int INT_WILDCARD = -1;
	public static final String WILDCARD = "*";

	@Override
	public int compare(final AbstractInsnNode a, final AbstractInsnNode b)
	{
		return this.areInsnsEqual(a, b) ? 0 : 1;
	}

	/**
	 * Respects {@link #INT_WILDCARD} and {@link #WILDCARD} instruction properties.
	 * Always returns true if {@code a} and {@code b} are label, line number, or frame instructions.
	 *
	 * @return Whether or not the given instructions are equivalent.
	 */
	public boolean areInsnsEqual(final AbstractInsnNode a, final AbstractInsnNode b)
	{
		if (a == b)
			return true;

		if (a == null || b == null)
			return false;

		if (a.equals(b))
			return true;

		if (a.getOpcode() != b.getOpcode())
			return false;

		switch (a.getType())
		{
		case AbstractInsnNode.VAR_INSN:
			return this.areVarInsnsEqual((VarInsnNode) a, (VarInsnNode) b);
		case AbstractInsnNode.TYPE_INSN:
			return this.areTypeInsnsEqual((TypeInsnNode) a, (TypeInsnNode) b);
		case AbstractInsnNode.FIELD_INSN:
			return this.areFieldInsnsEqual((FieldInsnNode) a, (FieldInsnNode) b);
		case AbstractInsnNode.METHOD_INSN:
			return this.areMethodInsnsEqual((MethodInsnNode) a, (MethodInsnNode) b);
		case AbstractInsnNode.LDC_INSN:
			return this.areLdcInsnsEqual((LdcInsnNode) a, (LdcInsnNode) b);
		case AbstractInsnNode.IINC_INSN:
			return this.areIincInsnsEqual((IincInsnNode) a, (IincInsnNode) b);
		case AbstractInsnNode.INT_INSN:
			return this.areIntInsnsEqual((IntInsnNode) a, (IntInsnNode) b);
		default:
			return true;
		}
	}

	private boolean areVarInsnsEqual(final VarInsnNode a, final VarInsnNode b)
	{
		return this.intValuesMatch(a.var, b.var);
	}

	private boolean areTypeInsnsEqual(final TypeInsnNode a, final TypeInsnNode b)
	{
		return this.valuesMatch(a.desc, b.desc);
	}

	private boolean areFieldInsnsEqual(final FieldInsnNode a, final FieldInsnNode b)
	{
		return this.valuesMatch(a.owner, b.owner) && this.valuesMatch(a.name, b.name) && this.valuesMatch(a.desc, b.desc);
	}

	private boolean areMethodInsnsEqual(final MethodInsnNode a, final MethodInsnNode b)
	{
		return this.valuesMatch(a.owner, b.owner) && this.valuesMatch(a.name, b.name) && this.valuesMatch(a.desc, b.desc);
	}

	private boolean areIntInsnsEqual(final IntInsnNode a, final IntInsnNode b)
	{
		return this.intValuesMatch(a.operand, b.operand);
	}

	private boolean areIincInsnsEqual(final IincInsnNode a, final IincInsnNode b)
	{
		return this.intValuesMatch(a.var, b.var) && this.intValuesMatch(a.incr, b.incr);
	}

	private boolean areLdcInsnsEqual(final LdcInsnNode a, final LdcInsnNode b)
	{
		return this.valuesMatch(a.cst, b.cst);
	}

	private boolean intValuesMatch(final int a, final int b)
	{
		return a == b || a == InsnComparator.INT_WILDCARD || b == InsnComparator.INT_WILDCARD;
	}

	private boolean valuesMatch(final Object a, final Object b)
	{
		return a.equals(b) || a.equals(InsnComparator.WILDCARD) || b.equals(InsnComparator.WILDCARD);
	}
}
