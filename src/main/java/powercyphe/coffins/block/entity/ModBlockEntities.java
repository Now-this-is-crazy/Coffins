package powercyphe.coffins.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import powercyphe.coffins.Mod;
import powercyphe.coffins.block.ModBlocks;

public class ModBlockEntities {
    public static BlockEntityType<CoffinBlockEntity> COFFIN;

    public static void registerBlockEntities() {
        Mod.debugMessage("Registering Block Entities");
        COFFIN = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(Mod.MOD_ID, "coffin"),
                FabricBlockEntityTypeBuilder.create(CoffinBlockEntity::new,
                        ModBlocks.COFFIN).build(null));
    }
}
