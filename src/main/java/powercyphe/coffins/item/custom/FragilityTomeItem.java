package powercyphe.coffins.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercyphe.coffins.block.ModBlocks;
import powercyphe.coffins.block.custom.CoffinBlock;
import powercyphe.coffins.block.entity.CoffinBlockEntity;

public class FragilityTomeItem extends ToolItem {

    public FragilityTomeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        ItemStack stack = context.getStack();
        if (!player.getWorld().isClient) {
            if (world.getBlockState(blockPos) == ModBlocks.COFFIN.getDefaultState()) {
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.contains(CoffinBlock.FRAGILE) && !blockState.get(CoffinBlock.FRAGILE)) {
                    if (world.getBlockEntity(blockPos) instanceof CoffinBlockEntity blockEntity) {
                        if (stack.getDamage() < stack.getMaxDamage()-1) {
                            stack.setDamage(stack.getDamage() + 1);
                        } else {
                            player.getInventory().setStack(player.getInventory().getSlotWithStack(stack), Items.BOOK.getDefaultStack());
                        }
                        blockEntity.setFragile(true);
                        player.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.5F, 1.5F);
                        ((ServerWorld) player.getWorld()).spawnParticles(ParticleTypes.ENCHANT, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 30, 0, 0, 0, 1.0);
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
