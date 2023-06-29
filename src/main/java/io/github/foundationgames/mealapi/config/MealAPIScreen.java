package io.github.foundationgames.mealapi.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

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
        var buttons = new OptionListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 13, 0xFFFFFF);
    }
}
