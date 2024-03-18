package mod.linguardium.timernameplates.mixin;

import mod.linguardium.timernameplates.util.BlockEntityUpdateUtil;
import mod.linguardium.timernameplates.updaters.Campfire;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin extends BlockEntity implements BlockEntityUpdateUtil.NbtSyncHelper {
// NbtSyncHelper provides the updateOnClients
    @Shadow @Final private int[] cookingTimes;

    @Shadow @Final private int[] cookingTotalTimes;

    private CampfireBlockEntityMixin() {
        super(null, null, null); // no
    }

    // this method adds additional data to the update packet
    // this allows limiting the additional data sent to client to prevent some unwanted sharing
    @Override
    public NbtCompound timernameplates$addRequiredDataToNbt(NbtCompound nbt) {
        nbt.putIntArray("CookingTimes",cookingTimes);
        nbt.putIntArray("CookingTotalTimes",cookingTotalTimes);
        return nbt;
    }

    @Inject(method="litServerTick", at=@At(value = "INVOKE", target = "Lnet/minecraft/block/entity/CampfireBlockEntity;markDirty(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", shift = At.Shift.AFTER))
    private static void updateListenersPerSecond(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci) {
        if ( Campfire.shouldSendUpdate(campfire)) {
            BlockEntityUpdateUtil.updateOnClients(campfire);
        }
    }

    @Override
    public BlockEntity timernameplates$asBlockEntity() {
        return this;
    }
}
