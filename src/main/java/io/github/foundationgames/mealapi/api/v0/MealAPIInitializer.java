package io.github.foundationgames.mealapi.api.v0;

/**
 * An initializer class for mods intending for
 * optional compatibility with Meal API.
 *
 * Classes implementing this interface must be added to the
 * mod's fabric.mod.json as a "mealapi" entrypoint.
 *
 * @author FoundationGames
 */
public interface MealAPIInitializer {
    /**
     * Called on Meal API's initialization.
     */
    void onMealApiInit();
}
