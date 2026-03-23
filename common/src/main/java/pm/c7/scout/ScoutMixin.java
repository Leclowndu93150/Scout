package pm.c7.scout;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import pm.c7.scout.mixinsupport.ClassNodeTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScoutMixin implements IMixinConfigPlugin {
	private static final Logger LOGGER = LoggerFactory.getLogger("Scout:MixinPlugin");
	private static final String TRANSFORMER_DESC = "Lpm/c7/scout/ScoutMixin$Transformer;";

	public @interface Transformer {
		Class<? extends ClassNodeTransformer> value();
	}

	private final Map<String, ClassNodeTransformer> transformers = new HashMap<>();
	private String mixinPackage;

	@Override
	public void onLoad(String mixinPackage) {
		this.mixinPackage = mixinPackage;
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return !mixinClassName.endsWith("Transformer");
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		ClassNode mixinNode = mixinInfo.getClassNode(0);
		if (mixinNode.visibleAnnotations != null) {
			for (AnnotationNode an : mixinNode.visibleAnnotations) {
				if (an.desc.equals(TRANSFORMER_DESC)) {
					Type type = decodeAnnotationValue(an);
					if (type != null) {
						try {
							transformers.put(mixinClassName, (ClassNodeTransformer) Class.forName(type.getClassName()).getDeclaredConstructor().newInstance());
						} catch (Exception e) {
							LOGGER.error("[Scout] Transformer class for mixin {} not found", mixinClassName, e);
						}
					}
				}
			}
		}
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		transformers.getOrDefault(mixinClassName, (s, cn) -> {}).transform(targetClassName, targetClass);
	}

	private static Type decodeAnnotationValue(AnnotationNode an) {
		if (an.values == null) return null;
		for (int i = 0; i < an.values.size(); i += 2) {
			String key = (String) an.values.get(i);
			if ("value".equals(key)) {
				return (Type) an.values.get(i + 1);
			}
		}
		return null;
	}
}
