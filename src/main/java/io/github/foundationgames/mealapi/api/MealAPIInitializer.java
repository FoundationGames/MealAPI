package io.github.foundationgames.mealapi.api;

/**
 * @author FoundationGames
 * An initializer class for mods intending for
 * optional compatibility with Meal API.
 *
 * Classes implementing this interface must be added to the
 * mod's fabric.mod.json as a "mealapi" entrypoint.
 */
public interface MealAPIInitializer {
    /**
     * Called on Meal API's initialization.
     */
    void onInitialize();
}
