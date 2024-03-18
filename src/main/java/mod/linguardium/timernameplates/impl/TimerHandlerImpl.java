package mod.linguardium.timernameplates.impl;

import com.google.common.collect.LinkedListMultimap;
import mod.linguardium.timernameplates.api.TimerHandler;
import net.minecraft.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class TimerHandlerImpl extends TimerHandler {
    private static final LinkedListMultimap<Class<? extends BlockEntity>, Handler<? extends BlockEntity>> handlerMap = LinkedListMultimap.create();

    @Override
    protected <T extends BlockEntity> void register(Class<T> blockEntityClass, Handler<T> handler) {
        handlerMap.put(blockEntityClass, handler);
    }

    @Override
    public List<Handler<? extends BlockEntity>> getList(BlockEntity blockEntity) {
        Class<?> blockEntityClass = blockEntity.getClass();
        List<Handler<? extends BlockEntity>> handlerList = new ArrayList<>(handlerMap.get(blockEntity.getClass()));
        for (Class<? extends BlockEntity> clazz : handlerMap.keySet()) {
            if (clazz.equals(blockEntityClass)) continue;
            if (clazz.isAssignableFrom(blockEntityClass)) handlerList.addAll(handlerMap.get(clazz));
        }
        return handlerList;
    }

}
