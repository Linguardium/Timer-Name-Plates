package mod.linguardium.timernameplates.updaters;

import mod.linguardium.timernameplates.mixin.CampfireBlockEntityAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class Campfire {

    public static boolean shouldSendUpdate(BlockEntity blockEntity) {
        if (!(blockEntity instanceof CampfireBlockEntity campfire) ||
            !(blockEntity instanceof CampfireBlockEntityAccessor accessor)) return false;

        DefaultedList<ItemStack> stacks = campfire.getItemsBeingCooked();
        for (int i=0;i<stacks.size();i++) {
            if (!stacks.get(i).isEmpty() &&
                    accessor.timernameplates$getRequiredCooktimes()[i] > 0 &&
                    accessor.timernameplates$getRunningCooktimes()[i] > 0 &&
                    accessor.timernameplates$getRunningCooktimes()[i] % 20 == 0
            ) {
                return true;
            }
        }
        return false;
    }
}
