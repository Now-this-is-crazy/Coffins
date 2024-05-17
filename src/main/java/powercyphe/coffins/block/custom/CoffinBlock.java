package powercyphe.coffins.block.custom;


import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import powercyphe.coffins.Mod;
import powercyphe.coffins.block.entity.CoffinBlockEntity;
import powercyphe.coffins.sound.ModSounds;

public class CoffinBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final BooleanProperty FRAGILE = BooleanProperty.of("fragile");

    public CoffinBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN, false).with(FRAGILE, false));
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        boolean allowBreaking = false;
        CoffinBlockEntity coffinBlockEntity = ((CoffinBlockEntity) blockEntity);
        if (!world.getGameRules().getBoolean(Mod.ALLOW_COFFIN_ROBBING)) {
            assert blockEntity != null;
            if (coffinBlockEntity.getOwner().isEmpty() || coffinBlockEntity.getOwner().equals(player.getName().getString()) || player.getAbilities().creativeMode) allowBreaking = true;
        } else {
            allowBreaking = true;
        }
        if (allowBreaking) {
            if (blockEntity instanceof CoffinBlockEntity) {
                ItemScatterer.spawn(world, pos, (CoffinBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.afterBreak(world, player, pos, state, blockEntity, stack);
        } else {
            player.sendMessage(Text.translatable("block.coffins.coffin.disallowBreak"), true);
            world.setBlockState(pos, state);
            world.addBlockEntity(coffinBlockEntity);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
        builder.add(FRAGILE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            boolean allowInteraction = false;
            CoffinBlockEntity blockEntity = (((CoffinBlockEntity) world.getBlockEntity(pos)));
            if (!world.getGameRules().getBoolean(Mod.ALLOW_COFFIN_ROBBING)) {
                assert blockEntity != null;
                if (blockEntity.getOwner().isEmpty() || blockEntity.getOwner().equals(player.getName().getString()) || player.getAbilities().creativeMode) allowInteraction = true;
            } else {
                allowInteraction = true;
            }

            if (allowInteraction) {
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        } else {
                player.getWorld().playSound(null, pos, ModSounds.COFFIN_LOCKED, SoundCategory.BLOCKS, 5f, 1f);
                player.sendMessage(Text.translatable("block.coffins.coffin.disallowOpen"), true);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CoffinBlockEntity(pos, state);
    }


}
