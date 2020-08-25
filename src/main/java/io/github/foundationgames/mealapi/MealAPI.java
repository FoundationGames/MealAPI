package io.github.foundationgames.mealapi;

import io.github.foundationgames.mealapi.api.MealAPIInitializer;
import io.github.foundationgames.mealapi.api.MealItemRegistry;
import io.github.foundationgames.mealapi.util.PlayerFullnessManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.item.Items;

public class MealAPI implements ModInitializer {
    @Override
    public void onInitialize() {
        PlayerFullnessManager.initCommon();
        for(MealAPIInitializer init : FabricLoader.INSTANCE.getEntrypoints("mealapi", MealAPIInitializer.class)) {
            init.onInitialize();
        }
    }
}
