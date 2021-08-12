package io.github.foundationgames.mealapi.impl;

import com.google.common.collect.Maps;
import io.github.foundationgames.mealapi.api.v0.FullnessProvider;
import io.github.foundationgames.mealapi.api.v0.MealItemRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public final class MealItemRegistryImpl implements MealItemRegistry {
    /**
     * @deprecated INTERNAL, Use {@link MealItemRegistry#instance()}
     */
    public static final MealItemRegistry INSTANCE = new MealItemRegistryImpl();

    private final Map<Item, FullnessProvider> entries = Maps.newHashMap();

    private MealItemRegistryImpl() {
    }

    @Override
    public void register(Item item, FullnessProvider fullness) {
        entries.put(item, fullness);
    }

    @Override
    public int getFullness(PlayerEntity player, ItemStack stack) {
        if(entries.containsKey(stack.getItem())) return entries.get(stack.getItem()).getFullness(player, stack);
        return 0;
    }
}
