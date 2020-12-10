# Meal API
Looking for the download? Go to CurseForge.
## Guide for Mod Developers
### Including the mod
Note to modders: Meal API does not like to be JIJ included. It's not recommended. <br/>
Add JitPack to your repositories block in `build.gradle`, and add the mod to your dependencies block.
```gradle
repositories {
	[...]
	maven { url "https://jitpack.io" }
}
```
`gradle.properties`.
##### Adding as a dependency
`build.gradle`
```gradle
dependencies {
    //Replace the version number with the correct one for your version.
    //See all versions in this project's GitHub Releases.
	modApi "com.github.FoundationGames:MealAPI:0.2+1.16.4"
}
```
##### As a required dependency
Add the mod id `mealapi` to your dependencies in your fabric.mod.json. <br/>
Replace `0.2+1.16.4` with the relevant version, if applicable.
```json
"depends": {
    [...]
    "mealapi": ">=0.2+1.16.4"
},
```

### Adding a meal
Decide how you want to register your meal items, based on the inclusion of your mod.
##### As an *optional* dependency
Create a new initializer class, implementing `MealAPIInitializer`.
Add that class to your `entrypoints` array in `fabric.mod.json` under the name `"mealapi"`.
Implement the `onInitialize()` method like normal. This method is where you will register your custom item as a meal.
##### As a *required* dependency
You will be registering your item as a meal in your "main" initializer class, in `onInitialize()`
#### Registering
Add this code wherever you are registering your item as a meal.
```java
MealItemRegistry.register(MY_ITEM, ((player, stack) -> {
	return 50; //fullness value
}));
```
As you can see, you get some context when choosing how much fullness you want to heal, which are the `PlayerEntity` and `ItemStack`.<br></br>
The function is run before the player's hunger and saturation are healed by the food item, so the player won't actually be fed when this code runs.<br></br>
The purpose of the separate initializer for optional inclusion is to not load this mod's classes when this mod is not loaded.
