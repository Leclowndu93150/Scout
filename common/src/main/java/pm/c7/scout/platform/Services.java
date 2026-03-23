package pm.c7.scout.platform;

import pm.c7.scout.platform.services.IScoutPlatform;

import java.util.ServiceLoader;

public class Services {
    public static final IScoutPlatform PLATFORM = load(IScoutPlatform.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
