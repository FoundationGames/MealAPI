# Meal API
Looking for the download? Go to [CurseForge](https://www.curseforge.com/minecraft/mc-mods/meal-api) or [Modrinth](https://modrinth.com/mod/mealapi).

## Guide for Mod Developers

### Including the mod
Add Modrinth to your repositories block in `build.gradle`, and add the mod to your dependencies block.
`build.gradle`
```gradle
repositories {
	[...]
	maven { url "https://api.modrinth.com/maven" }
}
```

##### Adding as a dependency
`build.gradle`
```gradle
dependencies {
    //Replace the version number with the correct one for your version.
    //See all versions in this project's GitHub Releases.
	modApi "com.github.FoundationGames:MealAPI:0.3+1.16"
}
```
Meal API isn't confined to one Minecraft version in its fabric.mod.json, however there is no guarantee a release will work on versions past what it's marked for.

##### As a required dependency
Add the mod id `mealapi` to your dependencies in your fabric.mod.json. <br/>
Replace `0.3` with the relevant version, if applicable.
```json
"depends": {
    [...]
    "mealapi": ">=0.3"
},
```
If desired, add the dependency to your `build.gradle` in the `dependencies` block with `include` in order to package Meal API within your mod.

### Using the API

#### API Classes
Make sure you are importing API classes from the newest API version. Classes from old API versions will be marked as deprecated. Each API version has its own package in `io.github.foundationgames.mealapi.api`. Directly touching the impl classes is discouraged. Deprecated API versions will be removed after being deprecated for about 2 major (x.x) mod versions.

#### Adding a Meal
Decide how you want to register your meal items, based on the inclusion of your mod.

##### As an *optional* dependency
Create a new initializer class, implementing `MealAPIInitializer`.
Add that class to your `entrypoints` array in `fabric.mod.json` under the name `"mealapi"`.
Implement the `onMealApiInit()` method. This method is where you will register your custom item as a meal.

##### As a *required* dependency
You will be registering your item as a meal in your "main" initializer class, in `onInitialize()`

##### Registering your Meal Item
Add this code wherever you are registering your item as a meal.
```java
// Makes MY_ITEM be worth 50 fullness points
MealItemRegistry.getInstance().register(MY_ITEM, ((player, stack) -> 50));
```
#### Additional API Features
See this repository's wiki for additional features of the API.