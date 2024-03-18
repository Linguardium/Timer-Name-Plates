package mod.linguardium.timernameplates;

import mod.linguardium.timernameplates.handlers.Campfire;
import mod.linguardium.timernameplates.handlers.FurnaceLikeBlocks;
import mod.linguardium.timernameplates.render.NamePlateRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class TimerNamePlatesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Campfire.init(); // campfire handler
		FurnaceLikeBlocks.init(); // furnace handler
		WorldRenderEvents.AFTER_ENTITIES.register(NamePlateRenderer::renderTimerNamePlates);
	}

}