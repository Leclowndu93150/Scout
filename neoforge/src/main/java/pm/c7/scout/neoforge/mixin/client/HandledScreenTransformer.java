package pm.c7.scout.neoforge.mixin.client;

import org.objectweb.asm.tree.*;

import pm.c7.scout.mixinsupport.ClassNodeTransformer;

import static org.objectweb.asm.Opcodes.*;

public class HandledScreenTransformer implements ClassNodeTransformer {
	// NeoForge uses Mojang mappings at runtime, so names are used directly.

	@Override
	public void transform(String name, ClassNode node) {
		var drawSlot = "renderSlot";
		var Slot = "net/minecraft/world/inventory/Slot";
		var y = "y";

		for (var mn : node.methods) {
			if (mn.name.equals(drawSlot)) {
				for (var insn : mn.instructions) {
					if (insn instanceof FieldInsnNode fin) {
						if (fin.getOpcode() == GETFIELD) {
							if(fin.owner.equals(Slot) && fin.name.equals(y)) {
								if (fin.getNext() instanceof VarInsnNode vin && vin.getOpcode() == ISTORE) {
									LabelNode LnotBag = new LabelNode();
									int SAFE_REGISTER = 20;
									mn.instructions.insert(vin, insns(
										ALOAD(2),
										INSTANCEOF("pm/c7/scout/screen/BagSlot"),
										IFEQ(LnotBag),
										ALOAD(2),
										CHECKCAST("pm/c7/scout/screen/BagSlot"),
										ASTORE(SAFE_REGISTER),
										ALOAD(SAFE_REGISTER),
										INVOKEVIRTUAL("pm/c7/scout/screen/BagSlot", "getX", "()I"),
										ISTORE(vin.var - 1),
										ALOAD(SAFE_REGISTER),
										INVOKEVIRTUAL("pm/c7/scout/screen/BagSlot", "getY", "()I"),
										ISTORE(vin.var),
										LnotBag
									));
								}
							}
						}
					}
				}
			}
		}
	}

	private String slash(String clazz) {
		return clazz.replaceAll("\\.", "/");
	}

	private InsnList insns(AbstractInsnNode... insns) {
		var li = new InsnList();
		for (var i : insns) li.add(i);
		return li;
	}
	private static JumpInsnNode IFEQ(LabelNode v) {
		return new JumpInsnNode(IFEQ, v);
	}
	private static VarInsnNode ALOAD(int v) {
		return new VarInsnNode(ALOAD, v);
	}
	private static VarInsnNode ASTORE(int v) {
		return new VarInsnNode(ASTORE, v);
	}
	private static TypeInsnNode INSTANCEOF(String desc) {
		return new TypeInsnNode(INSTANCEOF, desc);
	}
	private static TypeInsnNode CHECKCAST(String desc) {
		return new TypeInsnNode(CHECKCAST, desc);
	}
	private static MethodInsnNode INVOKEVIRTUAL(String owner, String name, String desc) {
		return new MethodInsnNode(INVOKEVIRTUAL, owner, name, desc);
	}
	private static VarInsnNode ISTORE(int v) {
		return new VarInsnNode(ISTORE, v);
	}
}
