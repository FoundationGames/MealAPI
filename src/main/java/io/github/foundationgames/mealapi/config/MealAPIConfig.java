package io.github.foundationgames.mealapi.config;

import com.google.gson.Gson;
import com.mojang.serialization.Codec;
import io.github.foundationgames.mealapi.MealAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MealAPIConfig {
    public static final String FILE_NAME = "mealapi.json";

    public static class Values {
        public int fullnessBarOpacityPct = 100;
        public DefaultedYesNo showFlashingFullnessPreview = DefaultedYesNo.DEFAULT;
        public DefaultedYesNo showFullnessTooltip = DefaultedYesNo.DEFAULT;
        public DefaultedYesNo fullnessIconBorders = DefaultedYesNo.DEFAULT;

        private void fix() {
            fullnessBarOpacityPct = MathHelper.clamp(fullnessBarOpacityPct, 0, 100);
        }
    }

    private Values values = new Values();

    public enum DefaultedYesNo implements StringIdentifiable {
        YES, NO, DEFAULT;

        @Override
        public String asString() {
            return this.name();
        }
    }

    public static List<DefaultedYesNo> YES_NO_VALUES = List.of(DefaultedYesNo.values());
    public static Codec<DefaultedYesNo> YES_NO_CODEC = StringIdentifiable.createCodec(DefaultedYesNo::values);

    public Values getValues() {
        return values;
    }

    public Values copyValues() {
        var newVals = new Values();
        for (var f : Values.class.getDeclaredFields()) {
            try {
                f.set(newVals, f.get(this.values));
            } catch (IllegalAccessException ignored) {}
        }

        return newVals;
    }

    public Path getPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    private void preload() throws IOException {
        if (!Files.exists(getPath())) {
            Files.createFile(getPath());
            save();
        }
    }

    public void load() throws IOException {
        preload();
        var configFile = getPath();
        var gson = new Gson();
        values = gson.fromJson(Files.newBufferedReader(configFile), Values.class);
        values.fix();
    }

    public void save() throws IOException {
        values.fix();
        var configFile = getPath();
        var gson = new Gson();
        var writer = gson.newJsonWriter(Files.newBufferedWriter(configFile));
        writer.setIndent("    ");
        gson.toJson(gson.toJsonTree(values, Values.class), writer);
        writer.close();
    }

    private void trySave() {
        try {
            this.save();
        } catch (IOException e) {
            MealAPI.LOG.error("Could not save config: "+e);
        }
    }

    public Screen screen(Screen parent) {
        var values = this.copyValues();

        var screen = new MealAPIScreen(parent, () -> {
            this.values = values;
            this.trySave();
        });

        screen.addIntRange("fullnessBarOpacityPct", val -> values.fullnessBarOpacityPct = val, values.fullnessBarOpacityPct, 0, 100);
        screen.addDefaultedYesNo("showFlashingFullnessPreview", val -> values.showFlashingFullnessPreview = val, values.showFlashingFullnessPreview);
        screen.addDefaultedYesNo("showFullnessTooltip", val -> values.showFullnessTooltip = val, values.showFullnessTooltip);
        screen.addDefaultedYesNo("fullnessIconBorders", val -> values.fullnessIconBorders = val, values.fullnessIconBorders);

        return screen;
    }
}
