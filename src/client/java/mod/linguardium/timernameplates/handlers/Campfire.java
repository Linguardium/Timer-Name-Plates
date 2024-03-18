package mod.linguardium.timernameplates.handlers;

import mod.linguardium.timernameplates.api.TimerHandler;
import mod.linguardium.timernameplates.mixin.CampfireBlockEntityAccessor;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Campfire {
    public static void init() {
        TimerHandler.register(CampfireBlockEntity.class,4, Campfire::getCampfireOffset, Campfire::getCampfireTime);
    }

    private static Vec3d getCampfireOffset(CampfireBlockEntity blockEntity, int slot) {
        Vec3d offset = new Vec3d(-0.3125F, 1f,-0.3125F);
        Direction stateFacing = blockEntity.getCachedState().getOrEmpty(CampfireBlock.FACING).orElse(Direction.NORTH);
        Direction direction2 = Direction.fromHorizontal((slot + stateFacing.getHorizontal()) % 4);
        float rotation = -direction2.asRotation();
        offset = offset.rotateY((float)Math.toRadians(rotation));

        return offset;
    }
    private static int getCampfireTime(CampfireBlockEntity entity, int slot) {
        if (entity instanceof CampfireBlockEntityAccessor accessor) {
            ItemStack stack = entity.getItemsBeingCooked().get(slot);
            if (stack.isEmpty()) return -1;
            int[] max = accessor.timernameplates$getRequiredCooktimes();
            int[] current = accessor.timernameplates$getRunningCooktimes();
            if (slot > max.length || slot > current.length) return -1;
            return max[slot] - current[slot];
        }
        return -1;
    }


}
