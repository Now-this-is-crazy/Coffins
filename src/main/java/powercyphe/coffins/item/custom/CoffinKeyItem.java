package powercyphe.coffins.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercyphe.coffins.block.entity.CoffinBlockEntity;
import powercyphe.coffins.sound.ModSounds;

public class CoffinKeyItem extends Item {
    public CoffinKeyItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        if (!world.isClient) {
            if (world.getBlockEntity(blockPos) instanceof CoffinBlockEntity blockEntity) {
                if (blockEntity.getOwner().isEmpty()) {
                    blockEntity.setLocked(true);
                    blockEntity.setOwner(player.getUuid().toString(), player.getName().getString());
                    world.playSound(null, blockPos, ModSounds.COFFIN_LOCKED, SoundCategory.BLOCKS, 0.5f, 1.4f);
                    context.getStack().decrement(1);
                } else {
                    player.sendMessage(Text.translatable("block.coffins.coffin.disallowKey"), true);
                    world.playSound(null, blockPos, ModSounds.COFFIN_LOCKED, SoundCategory.BLOCKS, 0.5f, 1f);
                }
            }
        }
        return ActionResult.SUCCESS;
    }
}