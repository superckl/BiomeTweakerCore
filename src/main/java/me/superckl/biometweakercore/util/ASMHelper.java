package me.superckl.biometweakercore.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

/**
 * This class is derived from Squeek502's ASMHelper project. Unneeded functionality has been removed. Find ASMHelper here:
 * https://github.com/squeek502/ASMHelper
 */
public class ASMHelper
{
	public static InsnComparator insnComparator = new InsnComparator();

	/**
	 * Converts a class name to an internal class name.
	 * @return internal/class/name
	 */
	public static String toInternalClassName(final String className)
	{
		return className.replace('.', '/');
	}

	/**
	 * @return true if the String is a valid descriptor;
	 */
	public static boolean isDescriptor(final String descriptor)
	{
		return descriptor.length() == 1 || (descriptor.startsWith("L") && descriptor.endsWith(";"));
	}

	/**
	 * Converts a class name to a descriptor.
	 * @return Linternal/class/name;
	 */
	public static String toDescriptor(final String className)
	{
		return ASMHelper.isDescriptor(className) ? className : "L" + ASMHelper.toInternalClassName(className) + ";";
	}

	/**
	 * Turns the given return and parameter values into a method descriptor
	 * Converts the types into descriptors as needed
	 * @return (LparamType;)LreturnType;
	 */
	public static String toMethodDescriptor(final String returnType, final String... paramTypes)
	{
		final StringBuilder paramDescriptors = new StringBuilder();
		for (final String paramType : paramTypes)
			paramDescriptors.append(ASMHelper.toDescriptor(paramType));

		return "(" + paramDescriptors.toString() + ")" + ASMHelper.toDescriptor(returnType);
	}

	/**
	 * Convert a byte array into a ClassNode.
	 */
	public static ClassNode readClassFromBytes(final byte[] bytes)
	{
		return ASMHelper.readClassFromBytes(bytes, 0);
	}

	/**
	 * Overload of {@link #readClassFromBytes(byte[])} with a flags parameter.
	 */
	public static ClassNode readClassFromBytes(final byte[] bytes, final int flags)
	{
		final ClassNode classNode = new ClassNode();
		final ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, flags);
		return classNode;
	}

	/**
	 * Convert a ClassNode into a byte array.
	 * Attempts to resolve issues with resolving super classes in an obfuscated environment.
	 * See {@link ObfRemappingClassWriter}.
	 */
	public static byte[] writeClassToBytes(final ClassNode classNode)
	{
		return ASMHelper.writeClassToBytes(classNode, ClassWriter.COMPUTE_MAXS);
	}

	/**
	 * Overload of {@link #writeClassToBytes(ClassNode)} with a flags parameter.
	 */
	public static byte[] writeClassToBytes(final ClassNode classNode, final int flags)
	{
		return ASMHelper.writeClassToBytesNoDeobf(classNode, flags);
	}

	/**
	 * Overload of {@link #writeClassToBytesNoDeobf(ClassNode)} with a flags parameter.
	 */
	public static byte[] writeClassToBytesNoDeobf(final ClassNode classNode, final int flags)
	{
		final ClassWriter writer = new ClassWriter(flags);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	/**
	 * Uses writeClassToBytes to write a ClassNode into a File for decompilation and analysis.
	 */
	public static void writeClassToFile(final ClassNode classNode, final File file) throws IOException {
		final FileOutputStream fos = new FileOutputStream(file);
		fos.write(ASMHelper.writeClassToBytes(classNode));
		fos.close();
	}

	/**
	 * @return An InputStream instance for the specified class name loaded by the specified ClassLoader.
	 */
	public static InputStream getClassAsStreamFromClassLoader(final String className, final ClassLoader classLoader)
	{
		return classLoader.getResourceAsStream(className.replace('.', '/') + ".class");
	}

	/**
	 * @return A ClassReader instance for the specified class name.
	 */
	public static ClassReader getClassReaderForClassName(final String className) throws IOException
	{
		return new ClassReader(ASMHelper.getClassAsStreamFromClassLoader(className, ASMHelper.class.getClassLoader()));
	}

	/**
	 * @return Whether or not the class read by the ClassReader has a valid super class.
	 */
	public static boolean classHasSuper(final ClassReader classReader)
	{
		return classReader.getSuperName() != null && !classReader.getSuperName().equals("java/lang/Object");
	}

	/**
	 * @return Whether or not the class read by the ClassReader extends the specified class.
	 */
	public static boolean doesClassExtend(final ClassReader classReader, final String targetSuperInternalClassName)
	{
		if (!ASMHelper.classHasSuper(classReader))
			return false;

		final String immediateSuperName = classReader.getSuperName();
		if (immediateSuperName.equals(targetSuperInternalClassName))
			return true;

		try
		{
			return ASMHelper.doesClassExtend(ASMHelper.getClassReaderForClassName(immediateSuperName), targetSuperInternalClassName);
		}
		catch (final IOException e)
		{
			// This will trigger when the super class is abstract; just ignore the error
		}
		return false;
	}

	/**
	 * @return Whether or not the instruction is a label or a line number.
	 */
	public static boolean isLabelOrLineNumber(final AbstractInsnNode insn)
	{
		return insn.getType() == AbstractInsnNode.LABEL || insn.getType() == AbstractInsnNode.LINE;
	}

	/**
	 * @return The first instruction for which {@link AbstractInsnNode#getType()} == {@code type} (could be {@code firstInsnToCheck}).
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode getOrFindInstructionOfType(final AbstractInsnNode firstInsnToCheck, final int type)
	{
		return ASMHelper.getOrFindInstructionOfType(firstInsnToCheck, type, false);
	}

	/**
	 * @return The first instruction for which {@link AbstractInsnNode#getType()} == {@code type} (could be {@code firstInsnToCheck}).
	 * If {@code reverseDirection} is {@code true}, instructions will be traversed backwards (using getPrevious()).
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode getOrFindInstructionOfType(final AbstractInsnNode firstInsnToCheck, final int type, final boolean reverseDirection)
	{
		for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext())
			if (instruction.getType() == type)
				return instruction;
		return null;
	}

	/**
	 * @return The first instruction for which {@link AbstractInsnNode#getOpcode()} == {@code opcode} (could be {@code firstInsnToCheck}).
	 * If a matching instruction cannot be found, returns {@code null}
	 */
	public static AbstractInsnNode getOrFindInstructionWithOpcode(final AbstractInsnNode firstInsnToCheck, final int opcode)
	{
		return ASMHelper.getOrFindInstructionWithOpcode(firstInsnToCheck, opcode, false);
	}

	/**
	 * @return The first instruction for which {@link AbstractInsnNode#getOpcode()} == {@code opcode} (could be {@code firstInsnToCheck}).
	 * If {@code reverseDirection} is {@code true}, instructions will be traversed backwards (using getPrevious()).
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode getOrFindInstructionWithOpcode(final AbstractInsnNode firstInsnToCheck, final int opcode, final boolean reverseDirection)
	{
		for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext())
			if (instruction.getOpcode() == opcode)
				return instruction;
		return null;
	}

	/**
	 * @return The first instruction for which {@link #isLabelOrLineNumber} == {@code false} (could be {@code firstInsnToCheck}).
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode getOrFindInstruction(final AbstractInsnNode firstInsnToCheck)
	{
		return ASMHelper.getOrFindInstruction(firstInsnToCheck, false);
	}

	/**
	 * @return The first instruction for which {@link #isLabelOrLineNumber} == {@code false} (could be {@code firstInsnToCheck}).
	 * If {@code reverseDirection} is {@code true}, instructions will be traversed backwards (using getPrevious()).
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode getOrFindInstruction(final AbstractInsnNode firstInsnToCheck, final boolean reverseDirection)
	{
		for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext())
			if (!ASMHelper.isLabelOrLineNumber(instruction))
				return instruction;
		return null;
	}

	/**
	 * @return The first instruction of the {@code method} for which {@link #isLabelOrLineNumber} == {@code false}.
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode findFirstInstruction(final MethodNode method)
	{
		return ASMHelper.getOrFindInstruction(method.instructions.getFirst());
	}

	/**
	 * @return The first instruction of the {@code method} for which {@link AbstractInsnNode#getOpcode()} == {@code opcode}.
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode findFirstInstructionWithOpcode(final MethodNode method, final int opcode)
	{
		return ASMHelper.getOrFindInstructionWithOpcode(method.instructions.getFirst(), opcode);
	}

	/**
	 * @return The last instruction of the {@code method} for which {@link AbstractInsnNode#getOpcode()} == {@code opcode}.
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode findLastInstructionWithOpcode(final MethodNode method, final int opcode)
	{
		return ASMHelper.getOrFindInstructionWithOpcode(method.instructions.getLast(), opcode, true);
	}

	/**
	 * @return The next instruction after {@code instruction} for which {@link #isLabelOrLineNumber} == {@code false}
	 * (excluding {@code instruction}).
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode findNextInstruction(final AbstractInsnNode instruction)
	{
		return ASMHelper.getOrFindInstruction(instruction.getNext());
	}

	/**
	 * @return The next instruction after {@code instruction} for which {@link AbstractInsnNode#getOpcode()} == {@code opcode}
	 * (excluding {@code instruction}).
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode findNextInstructionWithOpcode(final AbstractInsnNode instruction, final int opcode)
	{
		return ASMHelper.getOrFindInstructionWithOpcode(instruction.getNext(), opcode);
	}

	/**
	 * @return The previous instruction before {@code instruction} for which {@link #isLabelOrLineNumber} == {@code false}
	 * (excluding {@code instruction}).
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode findPreviousInstruction(final AbstractInsnNode instruction)
	{
		return ASMHelper.getOrFindInstruction(instruction.getPrevious(), true);
	}

	/**
	 * @return The previous instruction before {@code instruction} for which {@link AbstractInsnNode#getOpcode()} == {@code opcode}
	 * (excluding {@code instruction}).
	 * If a matching instruction cannot be found, returns {@code null}.
	 */
	public static AbstractInsnNode findPreviousInstructionWithOpcode(final AbstractInsnNode instruction, final int opcode)
	{
		return ASMHelper.getOrFindInstructionWithOpcode(instruction.getPrevious(), opcode, true);
	}

	/**
	 * @return The method of the class that has both a matching {@code methodName} and {@code methodDesc}.
	 * If no matching method is found, returns {@code null}.
	 */
	public static MethodNode findMethodNodeOfClass(final ClassNode classNode, final String methodName, final String methodDesc)
	{
		for (final MethodNode method : classNode.methods)
			if (method.name.equals(methodName) && (methodDesc == null || method.desc.equals(methodDesc)))
				return method;
		return null;
	}

	/**
	 * @return The method of the class that has a matching {@code srgMethodName} or {@code mcpMethodName} and a matching {@code methodDesc}.
	 * If no matching method is found, returns {@code null}.
	 */
	public static MethodNode findMethodNodeOfClass(final ClassNode classNode, final String srgMethodName, final String mcpMethodName, final String methodDesc)
	{
		for (final MethodNode method : classNode.methods)
			if ((method.name.equals(srgMethodName) || method.name.equals(mcpMethodName)) && (methodDesc == null || method.desc.equals(methodDesc)))
				return method;
		return null;
	}

	/**
	 * Remove instructions from {@code insnList} starting with {@code startInclusive}
	 * up until reaching {@code endNotInclusive} ({@code endNotInclusive} will not be removed).
	 *
	 * @return The number of instructions removed
	 */
	public static int removeFromInsnListUntil(final InsnList insnList, final AbstractInsnNode startInclusive, final AbstractInsnNode endNotInclusive)
	{
		AbstractInsnNode insnToRemove = startInclusive;
		int numDeleted = 0;
		while (insnToRemove != null && insnToRemove != endNotInclusive)
		{
			numDeleted++;
			insnToRemove = insnToRemove.getNext();
			insnList.remove(insnToRemove.getPrevious());
		}
		return numDeleted;
	}

	/**
	 * Convenience method for accessing {@link InsnComparator#areInsnsEqual}
	 */
	public static boolean instructionsMatch(final AbstractInsnNode first, final AbstractInsnNode second)
	{
		return ASMHelper.insnComparator.areInsnsEqual(first, second);
	}

	/**
	 * @return Whether or not the pattern in {@code checkFor} matches starting at {@code checkAgainst}
	 */
	public static boolean patternMatches(final InsnList checkFor, final AbstractInsnNode checkAgainst)
	{
		return ASMHelper.checkForPatternAt(checkFor, checkAgainst).getFirst() != null;
	}

	/**
	 * Checks whether or not the pattern in {@code checkFor} matches, starting at {@code checkAgainst}.
	 *
	 * @return All of the instructions that were matched by the {@code checkFor} pattern.
	 * If the pattern was not found, returns an empty {@link InsnList}.<br>
	 * <br>
	 * Note: If the pattern was matched, the size of the returned {@link InsnList} will be >= {@code checkFor}.size().
	 */
	public static InsnList checkForPatternAt(final InsnList checkFor, AbstractInsnNode checkAgainst)
	{
		final InsnList foundInsnList = new InsnList();
		boolean firstNeedleFound = false;
		for (AbstractInsnNode lookFor = checkFor.getFirst(); lookFor != null;)
		{
			if (checkAgainst == null)
				return new InsnList();

			if (ASMHelper.isLabelOrLineNumber(lookFor))
			{
				lookFor = lookFor.getNext();
				continue;
			}

			if (ASMHelper.isLabelOrLineNumber(checkAgainst))
			{
				if (firstNeedleFound)
					foundInsnList.add(checkAgainst);
				checkAgainst = checkAgainst.getNext();
				continue;
			}

			if (!ASMHelper.instructionsMatch(lookFor, checkAgainst))
				return new InsnList();

			foundInsnList.add(checkAgainst);
			lookFor = lookFor.getNext();
			checkAgainst = checkAgainst.getNext();
			firstNeedleFound = true;
		}
		return foundInsnList;
	}

	/**
	 * Searches for the pattern in {@code needle}, starting at {@code haystackStart}.
	 *
	 * @return All of the instructions that were matched by the pattern.
	 * If the pattern was not found, returns an empty {@link InsnList}.<br>
	 * <br>
	 * Note: If the pattern was matched, the size of the returned {@link InsnList} will be >= {@code checkFor}.size().
	 */
	public static InsnList findAndGetFoundInsnList(final AbstractInsnNode haystackStart, final InsnList needle)
	{
		final int needleStartOpcode = needle.getFirst().getOpcode();
		AbstractInsnNode checkAgainstStart = ASMHelper.getOrFindInstructionWithOpcode(haystackStart, needleStartOpcode);
		while (checkAgainstStart != null)
		{
			final InsnList found = ASMHelper.checkForPatternAt(needle, checkAgainstStart);

			if (found.getFirst() != null)
				return found;

			checkAgainstStart = ASMHelper.findNextInstructionWithOpcode(checkAgainstStart, needleStartOpcode);
		}
		return new InsnList();
	}

	/**
	 * Searches for the pattern in {@code needle} within {@code haystack}.
	 *
	 * @return The first instruction of the matched pattern.
	 * If the pattern was not found, returns an empty {@link InsnList}.
	 */
	public static AbstractInsnNode find(final InsnList haystack, final InsnList needle)
	{
		return ASMHelper.find(haystack.getFirst(), needle);
	}

	/**
	 * Searches for the pattern in {@code needle}, starting at {@code haystackStart}.
	 *
	 * @return The first instruction of the matched pattern.
	 * If the pattern was not found, returns {@code null}.
	 */
	public static AbstractInsnNode find(final AbstractInsnNode haystackStart, final InsnList needle)
	{
		if (needle.getFirst() == null)
			return null;

		final InsnList found = ASMHelper.findAndGetFoundInsnList(haystackStart, needle);
		return found.getFirst();
	}

	/**
	 * Searches for an instruction matching {@code needle} within {@code haystack}.
	 *
	 * @return The matching instruction.
	 * If a matching instruction was not found, returns {@code null}.
	 */
	public static AbstractInsnNode find(final InsnList haystack, final AbstractInsnNode needle)
	{
		return ASMHelper.find(haystack.getFirst(), needle);
	}

	/**
	 * Searches for an instruction matching {@code needle}, starting at {@code haystackStart}.
	 *
	 * @return The matching instruction.
	 * If a matching instruction was not found, returns {@code null}.
	 */
	public static AbstractInsnNode find(final AbstractInsnNode haystackStart, final AbstractInsnNode needle)
	{
		final InsnList insnList = new InsnList();
		insnList.add(needle);
		return ASMHelper.find(haystackStart, insnList);
	}

	/**
	 * Searches for the pattern in {@code needle} within {@code haystack} and replaces it with {@code replacement}.
	 *
	 * @return The instruction after the replacement.
	 * If the pattern was not found, returns {@code null}.
	 */
	public static AbstractInsnNode findAndReplace(final InsnList haystack, final InsnList needle, final InsnList replacement)
	{
		return ASMHelper.findAndReplace(haystack, needle, replacement, haystack.getFirst());
	}

	/**
	 * Searches for the pattern in {@code needle} within {@code haystack} (starting at {@code haystackStart})
	 * and replaces it with {@code replacement}.
	 *
	 * @return The instruction after the replacement.
	 * If the pattern was not found, returns {@code null}.
	 */
	public static AbstractInsnNode findAndReplace(final InsnList haystack, final InsnList needle, final InsnList replacement, final AbstractInsnNode haystackStart)
	{
		final InsnList found = ASMHelper.findAndGetFoundInsnList(haystackStart, needle);
		if (found.getFirst() != null)
		{
			haystack.insertBefore(found.getFirst(), replacement);
			final AbstractInsnNode afterNeedle = found.getLast().getNext();
			ASMHelper.removeFromInsnListUntil(haystack, found.getFirst(), afterNeedle);
			return afterNeedle;
		}
		return null;
	}

}
