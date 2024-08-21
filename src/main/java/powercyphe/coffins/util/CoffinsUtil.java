package powercyphe.coffins.util;

import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.jetbrains.annotations.Nullable;
import powercyphe.coffins.Mod;
import powercyphe.coffins.block.custom.CoffinBlock;
import powercyphe.coffins.block.entity.CoffinBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CoffinsUtil {

    public static boolean allowOpeningCoffin(World world, CoffinBlockEntity coffinBlockEntity, PlayerEntity player) {
        if (player.getAbilities().creativeMode) return true;
        boolean allow = true;
        String playerName = player.getEntityName();
        String playerUuid = player.getUuid().toString();
        if (!world.getGameRules().getBoolean(Mod.ALLOW_COFFIN_ROBBING)) {
            if (coffinBlockEntity.getOwnerName().isEmpty()) {
                if (!coffinBlockEntity.getOwner().equals(playerName) && !coffinBlockEntity.getOwner().isEmpty()) {
                    allow = false;
                }
            } else {
                if (!coffinBlockEntity.getOwner().equals(playerUuid) && !coffinBlockEntity.getOwner().isEmpty()) {
                    allow = false;
                }
            }
        }
        if (coffinBlockEntity.getWorld().getBlockState(coffinBlockEntity.getPos()).get(CoffinBlock.LOCKED) && !coffinBlockEntity.getOwner().equals(playerUuid)) {
            allow = false;
        }
        return allow;
    }

    public static @Nullable BlockPos findCoffinLocation(World world, BlockPos playerPos, @Nullable  DamageSource recentDamageSource) {
        TreeMap<BlockPos, Integer> distanceMap = new TreeMap<>();
        RegistryKey<DimensionType> dimension = world.getDimensionKey();

        boolean shift = true;
        boolean skipCheck = false;
        boolean voidDeath = false;
        if (recentDamageSource != null) {
            if (recentDamageSource.isOutOfWorld()) voidDeath = true;
        }
        while (shift) {
            BlockPos shiftPos = playerPos.down(1);
            if ((dimension.equals(DimensionTypes.THE_END) && playerPos.getY() < 40) || voidDeath) {
                if (playerPos.getY() < 1) playerPos = playerPos.withY(1);
                distanceMap.put(playerPos, 1);
                BlockPos platformPos = playerPos.add(-2, -1 ,-1);
                int platform = 0;
                while (platform < 9) {
                    platformPos = platformPos.add(1, 0, 0);
                    if (world.getBlockState(platformPos) == Blocks.AIR.getDefaultState()) world.setBlockState(platformPos, Blocks.OBSIDIAN.getDefaultState());
                    platform++;
                    if (platform % 3 == 0) {
                        platformPos = platformPos.add(-3, 0, 1);
                    }
                }
                skipCheck = true;
                shift = false;
            }
            if (world.getBlockState(shiftPos).isIn(ModTags.Blocks.COFFIN_REPLACEABLE)) {
                playerPos = shiftPos;
            } else {
                shift = false;
            }

        }

        if (!skipCheck) {
            int xPlayer = playerPos.getX();
            int yPlayer = playerPos.getY();
            int zPlayer = playerPos.getZ();

            int xMin = playerPos.getX() - 16;
            int xMax = playerPos.getX() + 16;
            int zMin = playerPos.getZ() - 16;
            int zMax = playerPos.getZ() + 16;

            int bottomY = world.getBottomY();
            int topY = world.getTopY();

            for (int x = xMin; x < xMax; x++) {
                for (int z = zMin; z < zMax; z++) {
                    for (int y = bottomY; y < topY; y++) {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        int distance = 0;
                        if (
                                world.getBlockState(blockPos).isIn(ModTags.Blocks.COFFIN_REPLACEABLE) &&
                                        world.getWorldBorder().contains(x, z)
                        ) {
                            distance = distance + Math.abs(x - xPlayer) + Math.abs(y - yPlayer) + Math.abs(z - zPlayer);
                            distanceMap.put(blockPos, distance);
                        }
                    }
                }
            }
        }
        List<Map.Entry<BlockPos, Integer>> entryList = new ArrayList<>(distanceMap.entrySet());
        entryList.sort(Map.Entry.comparingByValue());
        if (!entryList.isEmpty()) {
            return entryList.get(0).getKey();
        } else {
            return null;
        }
    }
}
