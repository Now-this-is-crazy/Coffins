package powercyphe.coffins.common.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import powercyphe.coffins.common.Coffins;
import powercyphe.coffins.common.block.entity.CoffinBlockEntity;

public interface CBlockEntityTypes {

    BlockEntityType<CoffinBlockEntity> COFFIN = register("coffin", FabricBlockEntityTypeBuilder.create(
            CoffinBlockEntity::new, CBlocks.COFFIN).build());

    static void init() {}

    static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> bType) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Coffins.id(name), bType);
    }
}
