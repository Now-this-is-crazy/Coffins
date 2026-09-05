package powercyphe.coffins.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import powercyphe.coffins.common.Coffins;
import powercyphe.coffins.common.menu.CoffinMenu;

public interface CMenuTypes {

    MenuType<CoffinMenu> COFFIN = register("coffin", new MenuType<>(
            CoffinMenu::new, FeatureFlagSet.of()));

    static void init() {}

    static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType<T> menuType) {
        return Registry.register(BuiltInRegistries.MENU, Coffins.id(name), menuType);
    }
}
