package io.github.foundationgames.mealapi.config;

import io.github.foundationgames.mealapi.util.MAUtil;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class MealAPIModMenu implements ModMenuApi {
    @Override
    public String getModId() {
        return MAUtil.MOD_ID;
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(MealAPIConfig.class, screen).get();
    }
}
