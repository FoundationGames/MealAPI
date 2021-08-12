package io.github.foundationgames.mealapi;

import io.github.foundationgames.mealapi.api.v0.MealAPIInitializer;
import io.github.foundationgames.mealapi.impl.PlayerFullnessUtilImpl;
import io.github.foundationgames.mealapi.config.MealAPIConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class MealAPI implements ModInitializer {
    public static final Logger LOG = LogManager.getLogger("Meal API");

    @Override
    public void onInitialize() {
        PlayerFullnessUtilImpl.initCommon();
        for(MealAPIInitializer init : FabricLoader.getInstance().getEntrypoints("mealapi", MealAPIInitializer.class)) {
            init.onMealApiInit();
        }
    }

    private static final MealAPIConfig cfg = new MealAPIConfig();

    public static MealAPIConfig getConfig() {
        return cfg;
    }

    static {
        try {
            cfg.load();
        } catch (IOException e) {
            LOG.error("Error loading config on game start: "+e);
        }
    }
}
