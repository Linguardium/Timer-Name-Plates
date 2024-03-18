package mod.linguardium.timernameplates.util;

public interface ListenerUpdater {
    default boolean alwaysUpdateListeners() {
        return false;
    }
}
