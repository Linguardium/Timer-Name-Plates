package mod.linguardium.timernameplates.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.linguardium.timernameplates.util.BlockEntityUpdateUtil;
import mod.linguardium.timernameplates.util.ListenerUpdater;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityNbtSyncMixin implements ListenerUpdater {
    @Inject(method="markDirty(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at=@At("RETURN"))
    private static void updateListenersOnTargetBlockEntities(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (world.getBlockEntity(pos) instanceof ListenerUpdater updater && updater.alwaysUpdateListeners()) {
            world.updateListeners(pos,state, state, Block.NOTIFY_LISTENERS);
        }
    }
    // can be used to allow BlockEntities to add required data to update packets
    @ModifyReturnValue(method="toInitialChunkDataNbt", at=@At("RETURN"))
    protected NbtCompound addToInitialBlockEntityData(NbtCompound original) {
        if (this instanceof BlockEntityUpdateUtil.NbtSyncHelper syncHelper) {
            syncHelper.timernameplates$addRequiredDataToNbt(original);
        }
        return original;
    }
    // can be used to provide an update packet for BlockEntities that dont by overriding sendUpdatePacketOnUpdate and checking the packet against null
    @ModifyReturnValue(method="toUpdatePacket", at=@At("RETURN"))
    protected Packet<ClientPlayPacketListener> sendUpdatePacketOnUpdate(Packet<ClientPlayPacketListener> packet) {
        return packet;
    }

}
