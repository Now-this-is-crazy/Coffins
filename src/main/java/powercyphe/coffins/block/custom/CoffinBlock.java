package powercyphe.coffins.block.custom;


import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import powercyphe.coffins.util.CoffinsUtil;

public class CoffinBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final BooleanProperty FRAGILE = BooleanProperty.of("fragile");
    public static final BooleanProperty LOCKED = BooleanProperty.of("locked");

    public CoffinBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN, false).with(FRAGILE, false).with(LOCKED, false));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CoffinBlockEntity) {
                ItemScatterer.spawn(world, pos, (CoffinBlockEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
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
        builder.add(LOCKED);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            boolean allowInteraction = false;
            if (world.getBlockEntity(pos) instanceof CoffinBlockEntity blockEntity && !world.getGameRules().getBoolean(Mod.ALLOW_COFFIN_ROBBING)) {
                if (CoffinsUtil.allowOpeningCoffin(world, blockEntity, player)) allowInteraction = true;
            } else {
                allowInteraction = true;
            }
            if (allowInteraction) {
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            } else {
                player.getWorld().playSound(null, pos, ModSounds.COFFIN_LOCKED, SoundCategory.BLOCKS, 0.5f, 1f);
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
