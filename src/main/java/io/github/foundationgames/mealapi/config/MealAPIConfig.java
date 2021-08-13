package io.github.foundationgames.mealapi.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import io.github.foundationgames.mealapi.MealAPI;
import me.shedaniel.clothconfiglite.api.ConfigScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

public class MealAPIConfig {
    public static final String FILE_NAME = "mealapi.json";

    public static class Values {
        public int fullnessBarOpacityPct = 100;
        public DefaultedYesNo showFlashingFullnessPreview = DefaultedYesNo.DEFAULT;
        public DefaultedYesNo fullnessIconBorders = DefaultedYesNo.DEFAULT;

        private void fix() {
            fullnessBarOpacityPct = MathHelper.clamp(fullnessBarOpacityPct, 0, 100);
        }
    }

    private Values values = new Values();

    public enum DefaultedYesNo {
        YES, NO, DEFAULT;
    }

    public Values getValues() {
        return values;
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
        Path configFile = getPath();
        Gson gson = new Gson();
        values = gson.fromJson(Files.newBufferedReader(configFile), Values.class);
        values.fix();
    }

    public void save() throws IOException {
        values.fix();
        Path configFile = getPath();
        Gson gson = new Gson();
        JsonWriter writer = gson.newJsonWriter(Files.newBufferedWriter(configFile));
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

    // Yeah I like annotation based config serializer libraries but I also don't want to make one
    public ConfigScreen screen(Screen parent) {
        try {
            load();
        } catch (IOException e) {
            MealAPI.LOG.error("Error loading config to create screen: "+e);
        }
        ConfigScreen screen = ConfigScreen.create(new TranslatableText("text.config.mealapi.title"), parent);
        Values defaultVals = new Values();
        for (Field f : Values.class.getDeclaredFields()) {
            try {
                // Set and get the field value, so it throws an exception if it's not modifiable and therefore is excluded from config
                f.set(defaultVals, f.get(defaultVals));
                screen.add(
                        new TranslatableText("text.config.mealapi.option."+f.getName()),
                        f.get(values),
                        () -> { try { return f.get(defaultVals); } catch (IllegalAccessException ignored) {} return null; },
                        (val) -> { try { f.set(values, val); } catch (IllegalAccessException ignored) {} trySave(); }
                );
            } catch (IllegalAccessException ignored) {
                // Field can't be accessed and thus can't be saved
            }
        }
        return screen;
    }
}
