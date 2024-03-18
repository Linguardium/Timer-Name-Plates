package mod.linguardium.timernameplates.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class BlockEntityUpdateUtil {
    public interface NbtSyncHelper {
        NbtCompound timernameplates$addRequiredDataToNbt(NbtCompound nbt);
        BlockEntity timernameplates$asBlockEntity();
        default void updateOnClients() {
            if (timernameplates$asBlockEntity().getWorld() instanceof ServerWorld serverWorld) {
                BlockEntityUpdateUtil.sendBlockEntityUpdateToClient(serverWorld, timernameplates$asBlockEntity(), timernameplates$addRequiredDataToNbt(timernameplates$asBlockEntity().toInitialChunkDataNbt()));
            }
        }
    }
    public static void updateOnClients(BlockEntity helper) {
        if (helper instanceof NbtSyncHelper syncHelper) {
            syncHelper.updateOnClients();
        } else {
            if (helper.getWorld() instanceof ServerWorld serverWorld) {
                sendBlockEntityUpdateToClient(serverWorld, helper, helper.createNbt());
            }
        }
    }
    private static void sendBlockEntityUpdateToClient(ServerWorld world, BlockEntity blockEntity, NbtCompound data) {
        BlockPos bePos = blockEntity.getPos();
        BlockEntityUpdateS2CPacket packet = BlockEntityUpdateS2CPacket.create(blockEntity, be->data);
        world.getPlayers().forEach(player->
            world.sendToPlayerIfNearby(player,false,bePos.getX(),bePos.getY(),bePos.getZ(), packet)
        );
    }
}
