package io.github.foundationgames.mealapi;

import io.github.foundationgames.mealapi.util.PlayerFullnessManager;
import net.fabricmc.api.ClientModInitializer;

public class MealAPIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PlayerFullnessManager.initClient();
    }
}
