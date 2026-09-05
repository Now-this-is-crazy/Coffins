package powercyphe.coffins.common.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import powercyphe.coffins.common.block.entity.CoffinBlockEntity;

public class NonGraveCoffinCondition implements LootItemCondition {
    public static final MapCodec<NonGraveCoffinCondition> CODEC = MapCodec.unit(NonGraveCoffinCondition::new);

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(LootContext context) {
        BlockEntity entity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (entity instanceof CoffinBlockEntity coffin) {
            return !coffin.isGrave();
        }

        return true;
    }
}
