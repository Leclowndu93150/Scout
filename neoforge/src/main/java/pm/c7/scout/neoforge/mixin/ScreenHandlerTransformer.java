package pm.c7.scout.neoforge.mixin;

import org.objectweb.asm.tree.*;

import pm.c7.scout.mixinsupport.ClassNodeTransformer;

import static org.objectweb.asm.Opcodes.*;

public class ScreenHandlerTransformer implements ClassNodeTransformer {
	// NeoForge uses Mojang mappings at runtime, so names are used directly.

	@Override
	public void transform(String name, ClassNode node) {
		var internalOnSlotClick = "doClick";

		var PlayerEntity = "net/minecraft/world/entity/player/Player";
		var PlayerScreenHandler = "net/minecraft/world/inventory/InventoryMenu";
		var LPlayerScreenHandler = L(PlayerScreenHandler);
		String playerScreenHandler = "inventoryMenu";

		var Slot = "net/minecraft/world/inventory/Slot";
		var LSlot = L(Slot);

		var DefaultedList = "net/minecraft/core/NonNullList";

		int ordinal = 0;

		for (var mn : node.methods) {
			if (mn.name.equals(internalOnSlotClick)) {
				for (var insn : mn.instructions) {
					if (insn instanceof VarInsnNode vin) {
						if (vin.getOpcode() == ILOAD && vin.var == 1) {
							if (insn.getNext() instanceof JumpInsnNode nextInsn && nextInsn.getOpcode() == IFGE) {
								// `if (slotIndex < 0) return` -> `if (slotIndex < 0 && !isBagSlot(slotIndex)) return`
								var jumpTo = nextInsn.label;
								mn.instructions.insert(nextInsn, insns(
									ILOAD(1),
									INVOKESTATIC("pm/c7/scout/ScoutUtil", "isBagSlot", "(I)Z"),
									IFNE(jumpTo)
								));
							} else if (insn.getPrevious() instanceof JumpInsnNode prevInsn && prevInsn.getOpcode() == IFEQ && insn.getNext() instanceof JumpInsnNode nextInsn && nextInsn.getOpcode() == IFLT) {
								// skip creative duping, it uses same signature and i dont feel like overcomplicating the check
								if (ordinal != 1) {
									ordinal++;
									continue;
								}

								// fix dropping from bags not working
								LabelNode Lcheck = new LabelNode();
								nextInsn.label = Lcheck;
								nextInsn.setOpcode(IFGE);
								mn.instructions.insert(nextInsn, insns(
									ILOAD(1),
									INVOKESTATIC("pm/c7/scout/ScoutUtil", "isBagSlot", "(I)Z"),
									IFNE(Lcheck),
									RETURN(),
									Lcheck
								));
							}
						} else if (vin.getOpcode() == ASTORE && (vin.var == 6 || vin.var == 7)) {
							// fix most but not all calls to `slots.get`
							if (vin.getPrevious() instanceof TypeInsnNode prevInsn && prevInsn.getOpcode() == CHECKCAST && prevInsn.desc.equals(Slot)) {
								if (prevInsn.getPrevious() instanceof MethodInsnNode prevPrevInsn && prevPrevInsn.getOpcode() == INVOKEVIRTUAL) {
									if(prevPrevInsn.owner.equals(DefaultedList)) {
										var insertPoint = prevPrevInsn.getPrevious();

										if (insertPoint.getOpcode() == ILOAD) {
											var beforeInsert = insertPoint.getPrevious();

											if (beforeInsert != null && beforeInsert.getPrevious() != null){
												if (beforeInsert.getOpcode() == GETFIELD && beforeInsert.getPrevious().getOpcode() == ALOAD) {
													insertPoint = beforeInsert.getPrevious();
												} else {
													continue;
												}
											}

											LabelNode LnotBag = new LabelNode();
											LabelNode Lend = (LabelNode) vin.getNext();

											mn.instructions.insertBefore(insertPoint, insns(
												ILOAD(1),
												INVOKESTATIC("pm/c7/scout/ScoutUtil", "isBagSlot", "(I)Z"),
												IFEQ(LnotBag),
												ILOAD(1),
												ALOAD(4),
												GETFIELD(PlayerEntity, playerScreenHandler, LPlayerScreenHandler),
												INVOKESTATIC("pm/c7/scout/ScoutUtil", "getBagSlot", "(I" + LPlayerScreenHandler + ")" + LSlot),
												CHECKCAST(Slot),
												ASTORE(vin.var),
												GOTO(Lend),
												LnotBag
											));
										}
									}
								}
							}
						}
					}
				}
			} else if (mn.name.startsWith("handler$")) {
				// fix getting slots for mixins
				for (var insn : mn.instructions) {
					if (insn instanceof VarInsnNode vin && vin.getOpcode() == ASTORE && (vin.var == 6 || vin.var == 7)) {
						if (vin.getPrevious() instanceof TypeInsnNode prevInsn && prevInsn.getOpcode() == CHECKCAST && prevInsn.desc.equals(Slot)) {
							if (prevInsn.getPrevious() instanceof MethodInsnNode prevPrevInsn && prevPrevInsn.getOpcode() == INVOKEVIRTUAL) {
								if(prevPrevInsn.owner.equals(DefaultedList)) {
									var insertPoint = prevPrevInsn.getPrevious();

									if (insertPoint.getOpcode() == ILOAD) {
										var beforeInsert = insertPoint.getPrevious();

										if (beforeInsert != null && beforeInsert.getPrevious() != null && beforeInsert.getOpcode() == GETFIELD && beforeInsert.getPrevious().getOpcode() == ALOAD) {
											insertPoint = beforeInsert.getPrevious();
										}

										LabelNode LnotBag = new LabelNode();
										LabelNode Lend = (LabelNode) vin.getNext();

										mn.instructions.insertBefore(insertPoint, insns(
											ILOAD(1),
											INVOKESTATIC("pm/c7/scout/ScoutUtil", "isBagSlot", "(I)Z"),
											IFEQ(LnotBag),
											ILOAD(1),
											ALOAD(4),
											GETFIELD(PlayerEntity, playerScreenHandler, LPlayerScreenHandler),
											INVOKESTATIC("pm/c7/scout/ScoutUtil", "getBagSlot", "(I" + LPlayerScreenHandler + ")" + LSlot),
											CHECKCAST(Slot),
											ASTORE(vin.var),
											GOTO(Lend),
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
	}

	private String slash(String clazz) {
		return clazz.replaceAll("\\.", "/");
	}

	private String L(String clazz) {
		return "L" + clazz + ";";
	}

	private InsnList insns(AbstractInsnNode... insns) {
		var li = new InsnList();
		for (var i : insns) li.add(i);
		return li;
	}
	private static VarInsnNode ILOAD(int v) {
		return new VarInsnNode(ILOAD, v);
	}
	private static MethodInsnNode INVOKESTATIC(String owner, String name, String desc) {
		return new MethodInsnNode(INVOKESTATIC, owner, name, desc);
	}
	private static JumpInsnNode IFNE(LabelNode v) {
		return new JumpInsnNode(IFNE, v);
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
	private static FieldInsnNode GETFIELD(String owner, String name, String desc) {
		return new FieldInsnNode(GETFIELD, owner, name, desc);
	}
	private static TypeInsnNode CHECKCAST(String desc) {
		return new TypeInsnNode(CHECKCAST, desc);
	}
	private static JumpInsnNode GOTO(LabelNode v) {
		return new JumpInsnNode(GOTO, v);
	}
	private static InsnNode RETURN() {
		return new InsnNode(RETURN);
	}
}
