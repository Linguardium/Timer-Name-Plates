package mod.linguardium.timernameplates.api;

import mod.linguardium.timernameplates.impl.TimerHandlerImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class TimerHandler {
    private static TimerHandler instance = null;

    public static void init() {
        // for class loading purposes
    }

    public static TimerHandler getInstance() {
        if (instance == null) instance = new TimerHandlerImpl();
        return instance;
    }

    public static <T extends BlockEntity> void register(Class<T> blockEntityClass, int slotCount, TimerHandler.TimerOffsetGetter<T> renderOffsetGetter, TimerHandler.TimeGetter<T> timeRemainingGetter) {
        getInstance().register(blockEntityClass, new Handler<>(slotCount, renderOffsetGetter, timeRemainingGetter));
    }

    public static List<Handler<? extends BlockEntity>> get(BlockEntity blockEntity) {
        return getInstance().getList(blockEntity);
    }

    @FunctionalInterface
    public interface TimerOffsetGetter<T extends BlockEntity> {
        Vec3d getOffset(T blockEntity, int slot);
    }
    @FunctionalInterface
    public interface TimeGetter<T extends BlockEntity> {
        int getTimerTime(T blockEntity, int slot);
    }

    public record Handler<T extends BlockEntity>(int slotCount, TimerOffsetGetter<T> offsetGetter, TimeGetter<T> timeGetter) {
    }

    protected <T extends BlockEntity> void register(Class<T> blockEntityClass, Handler<T> handler) {
        throw new UnsupportedOperationException("Method must be implemented in an implementation class");
    }

    protected List<Handler<? extends BlockEntity>> getList(BlockEntity blockEntity) {
        throw new UnsupportedOperationException("Method must be implemented in an implementation class");
    }


}
