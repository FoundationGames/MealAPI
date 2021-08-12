package io.github.foundationgames.mealapi;

import io.github.foundationgames.mealapi.util.HudRenderUtil;
import io.github.foundationgames.mealapi.impl.PlayerFullnessUtilImpl;
import net.fabricmc.api.ClientModInitializer;

public class MealAPIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PlayerFullnessUtilImpl.initClient();
        HudRenderUtil.initClient();
    }
}
