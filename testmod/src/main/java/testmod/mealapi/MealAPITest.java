package testmod.mealapi;

import io.github.foundationgames.mealapi.api.v0.MealAPIInitializer;
import io.github.foundationgames.mealapi.api.v0.MealItemRegistry;
import io.github.foundationgames.mealapi.api.v0.PlayerFullnessUtil;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class MealAPITest implements MealAPIInitializer {
    @Override
    public void onMealApiInit() {
        MealItemRegistry.instance().register(Items.CHICKEN, (player, stack) -> 42);
        MealItemRegistry.instance().register(Items.ROTTEN_FLESH, (player, stack) -> 69);
        MealItemRegistry.instance().register(Items.GOLDEN_APPLE, (player, stack) -> 76);
        MealItemRegistry.instance().register(Items.PUMPKIN_PIE, (player, stack) -> 51);
        MealItemRegistry.instance().register(Items.BAKED_POTATO, (player, stack) -> 34);

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.getBlockState(hitResult.getBlockPos()).isOf(Blocks.EMERALD_BLOCK) && player instanceof ServerPlayerEntity) {
                PlayerFullnessUtil.instance().addFullness((ServerPlayerEntity)player, 18);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
}
