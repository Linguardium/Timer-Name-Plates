package mod.linguardium.timernameplates.render;

import mod.linguardium.timernameplates.api.TimerHandler;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.List;


public class NamePlateRenderer {
    public static void renderTimerNamePlates(WorldRenderContext context) {

        // need client later for crosshair
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        // probably dont need the frustum check since the crosshair is used.
        // previous code allowed all in a given range, this lets it filter to visible by camera
        Frustum frustum = context.frustum();
        if (frustum == null) return;

        // text renderer should really never be null but...here we are
        TextRenderer textRenderer = client.textRenderer;
        if (textRenderer == null) return;

        ClientWorld world = context.world();

        // we use the crosshair
        if (client.crosshairTarget == null || !client.crosshairTarget.getType().equals(HitResult.Type.BLOCK)) return;
        BlockPos blockPos = ((BlockHitResult)client.crosshairTarget).getBlockPos();
        // is it technically in view scope of the camera?
        if (!frustum.isVisible(Box.from(blockPos.toCenterPos()))) return;

        // our handlers interact with block entities directly
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity == null) return;
        // the handlers, generified
        List<TimerHandler.Handler<? extends BlockEntity>> handlerList = TimerHandler.get(blockEntity);
        if (handlerList.isEmpty()) return;
        // illuminate correctly at block position...technically this should be light of the position at the offset...
        int light = WorldRenderer.getLightmapCoordinates(world, blockPos);
        // handle it!
        for (TimerHandler.Handler<? extends BlockEntity> handler : handlerList) {
            //noinspection unchecked
            renderAllSlotNameplates(context.camera(), textRenderer, context.matrixStack(), context.consumers(), blockEntity, light, (TimerHandler.Handler<BlockEntity>)handler);
        }
    }
    public static void renderAllSlotNameplates(Camera camera, TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockEntity blockEntity, int light, TimerHandler.Handler<BlockEntity> handler) {
        Vec3d blockCenterPos = blockEntity.getPos().toCenterPos();
        for (int index = 0; index < handler.slotCount(); index++) {
            // if the handler returns -1, assume the value is invalid and dont display it
            int tickTime = handler.timeGetter().getTimerTime(blockEntity, index);
            if (tickTime <= 0) continue;
            // show it in seconds. for performance reasons updates are slowed down to 1 per second
            // the blocks dont normally update clients unless they are in a screen handler
            int secondsTime = (int)Math.ceil(tickTime/20.0d);

            Vec3d offset = handler.offsetGetter().getOffset(blockEntity, index);
            renderNameplate(camera, textRenderer, matrices, vertexConsumers, blockCenterPos.add(offset), light, Text.of(String.valueOf(secondsTime)));
        }
    }
    public static void renderNameplate(Camera camera, TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d nameplatePos, int light, Text text) {
        Vec3d cameraPos = camera.getPos();
        Vec3d renderPos = nameplatePos.subtract(cameraPos); // render at the camera + block position offset

        if (textRenderer == null) return; // shouldnt happen but people be doing dumb things

        matrices.push(); // save state

        matrices.translate(renderPos.getX(), renderPos.getY(), renderPos.getZ()); // set position to where nameplate should be
        matrices.multiply(camera.getRotation()); // rotate nameplate towards player
        matrices.scale(-0.025F, -0.025F, 0.025F); // shrink it because it's WAY too big

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float textBackgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f) * 255.0f;
        int backgroundAlphaColor = (int)textBackgroundOpacity << 24; // 0xAARRGGBB

        float xOffset = (-textRenderer.getWidth(text) / 2.0f); // center text on X position by subtracting half its width

        // transparent layer first with transparent background
        textRenderer.draw(text, xOffset, 0, 0x20FFFFFF, false, matrix, vertexConsumers, true, backgroundAlphaColor, light);
        // opaque layer with clear background (AA is 00)
        textRenderer.draw(text, xOffset, 0, -1, false, matrix, vertexConsumers, false, 0, light);

        matrices.pop(); // reload save state
    }


}
