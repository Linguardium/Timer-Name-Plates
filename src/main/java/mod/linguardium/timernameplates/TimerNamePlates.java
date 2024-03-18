package mod.linguardium.timernameplates;

import mod.linguardium.timernameplates.api.TimerHandler;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerNamePlates implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("timer-name-plates");

	@Override
	public void onInitialize() {
		TimerHandler.init();
	}
}