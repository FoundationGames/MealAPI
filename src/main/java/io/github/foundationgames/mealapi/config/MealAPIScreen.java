package io.github.foundationgames.mealapi.config;

import io.github.foundationgames.mealapi.MealAPI;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class MealAPIScreen extends Screen {
    private static final Text TITLE = Text.translatable("text.config.mealapi.title");

    private final Screen parent;
    private final List<SimpleOption<?>> options = new ArrayList<>();
    private final Runnable save;

    public MealAPIScreen(Screen parent, Runnable save) {
        super(TITLE);

        this.parent = parent;
        this.save = save;
    }

    public static MealAPIScreen create(MealAPIConfig config, Screen parent) {
        var values = config.copyValues();

        var screen = new MealAPIScreen(parent, () -> {
            config.setValues(values);
            try {
                config.save();
            } catch (IOException e) {
                MealAPI.LOG.error("Could not save config!", e);
            }
        });

        screen.addIntRange("fullnessBarOpacityPct", val -> values.fullnessBarOpacityPct = val, values.fullnessBarOpacityPct, 0, 100);
        screen.addDefaultedYesNo("showFlashingFullnessPreview", val -> values.showFlashingFullnessPreview = val, values.showFlashingFullnessPreview);
        screen.addDefaultedYesNo("showFullnessTooltip", val -> values.showFullnessTooltip = val, values.showFullnessTooltip);
        screen.addDefaultedYesNo("fullnessIconBorders", val -> values.fullnessIconBorders = val, values.fullnessIconBorders);

        return screen;
    }

    public void addDefaultedYesNo(String key, Consumer<MealAPIConfig.DefaultedYesNo> setter, MealAPIConfig.DefaultedYesNo currentValue) {
        this.options.add(new SimpleOption<>("text.config.mealapi.option." + key,
                SimpleOption.emptyTooltip(),
                (optionText, value) -> Text.literal(value.toString()),
                new SimpleOption.PotentialValuesBasedCallbacks<>(MealAPIConfig.YES_NO_VALUES, MealAPIConfig.YES_NO_CODEC),
                currentValue, setter)
        );
    }

    public void addIntRange(String key, IntConsumer setter, int currentValue, int min, int max) {
        this.options.add(new SimpleOption<>("text.config.mealapi.option." + key,
                SimpleOption.emptyTooltip(),
                (optionText, value) ->
                        GameOptions.getGenericValueText(optionText, Text.literal(value.toString())),
                new SimpleOption.ValidatingIntSliderCallbacks(min, max),
                currentValue, setter::accept)
        );
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        var buttons = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        for (SimpleOption<?> option : this.options) {
            buttons.addSingleOptionEntry(option);
        }
        this.addDrawableChild(buttons);

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            this.save.run();
            this.close();
        }).dimensions(this.width / 2 + 2, this.height - 27, 150, 20).build());

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> this.close())
                .dimensions(this.width / 2 - 152, this.height - 27, 150, 20).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 0xFFFFFF);
    }
}
