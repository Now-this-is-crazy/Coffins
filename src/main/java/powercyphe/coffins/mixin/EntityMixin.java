package powercyphe.coffins.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import powercyphe.coffins.Mod;
import powercyphe.coffins.util.IEntityDataSaver;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntityDataSaver {
    @Nullable
    private NbtCompound persistentData = new NbtCompound();

    @Override
    public NbtCompound getPersistentData() {
        if (this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        return persistentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void writeNbt(NbtCompound nbt, CallbackInfoReturnable ci) {
        if (persistentData != null) {
            nbt.put(Mod.MOD_ID + ":recovery_compass", persistentData);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(Mod.MOD_ID + ":recovery_compass", 10))
            persistentData = nbt.getCompound(Mod.MOD_ID + ":recovery_compass");
    }
}
