package mod.linguardium.timernameplates.handlers;

import mod.linguardium.timernameplates.api.TimerHandler;
import mod.linguardium.timernameplates.mixin.client.AbstractFurnaceBlockEntityAccessor;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class FurnaceLikeBlocks {
    public static void init() {
        TimerHandler.register(AbstractFurnaceBlockEntity.class, 1, FurnaceLikeBlocks::getFurnaceOffset, FurnaceLikeBlocks::getFurnaceTime);

    }
    private static Vec3d getFurnaceOffset(AbstractFurnaceBlockEntity blockEntity, int slot) {
        return new Vec3d(0,1,0);

    }
    private static int getFurnaceTime(AbstractFurnaceBlockEntity entity, int slot) {
        if (!(entity instanceof AbstractFurnaceBlockEntityAccessor accessor)) return -1;
        int burnTime = accessor.getPropertyDelegate().get(0);
        int cookTime = accessor.getPropertyDelegate().get(2);
        int cookTimeTotal = accessor.getPropertyDelegate().get(3);
        if (cookTimeTotal == 0) return -1;

        ItemStack input = entity.getStack(0);
        if (input.isEmpty()) return -1;
        if (burnTime < 2) {
            ItemStack fuel = entity.getStack(1);
            Integer fuelBurnTime = FuelRegistry.INSTANCE.get(fuel.getItem());
            if (fuelBurnTime == null || fuelBurnTime < 1) return -1;
        }
        return cookTimeTotal - cookTime;
    }

}
