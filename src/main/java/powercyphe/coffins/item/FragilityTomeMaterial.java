package powercyphe.coffins.item;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class FragilityTomeMaterial implements ToolMaterial {
    public static final FragilityTomeMaterial FRAGILITY_TOME_MATERIAL = new FragilityTomeMaterial();

    @Override
    public int getDurability() {
        return 5;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 0;
    }

    @Override
    public float getAttackDamage() {
        return 0;
    }

    @Override
    public int getMiningLevel() {
        return 0;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return null;
    }
}
