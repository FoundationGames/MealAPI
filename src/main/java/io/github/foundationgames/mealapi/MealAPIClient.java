package io.github.foundationgames.mealapi;

import io.github.foundationgames.mealapi.util.HudRenderUtil;
import io.github.foundationgames.mealapi.api.PlayerFullnessManager;
import net.fabricmc.api.ClientModInitializer;

public class MealAPIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PlayerFullnessManager.initClient();
        HudRenderUtil.initClient();
    }
}
