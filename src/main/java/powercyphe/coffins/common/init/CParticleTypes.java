package powercyphe.coffins.common.init;

import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import powercyphe.coffins.client.particle.RecoveryPingParticle;
import powercyphe.coffins.common.Coffins;

public interface CParticleTypes {

    SimpleParticleType RECOVERY_PING = register("recovery_ping", FabricParticleTypes.simple(true));

    static void init() {}

    static void initClient() {
        ParticleProviderRegistry.getInstance().register(RECOVERY_PING, RecoveryPingParticle.Provider::new);
    }

    static <T extends ParticleType<? extends ParticleOptions>> T register(String name, T particle) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Coffins.id(name), particle);
    }
}
