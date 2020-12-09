package io.github.foundationgames.mealapi.config;

import io.github.foundationgames.mealapi.util.MAUtil;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = MAUtil.MOD_ID)
@Config.Gui.Background("mealapi:textures/gui/config_background_static.png")
public class MealAPIConfig implements ConfigData {
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int fullnessBarOpacityPct = 100;
    public DefaultedYesNo showFlashingFullnessPreview = DefaultedYesNo.DEFAULT;
    public DefaultedYesNo fullnessIconBorders = DefaultedYesNo.DEFAULT;

    public enum DefaultedYesNo {
        YES, NO, DEFAULT;
    }
}
