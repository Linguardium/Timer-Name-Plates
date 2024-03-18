package mod.linguardium.timernameplates.mixin.client;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.screen.PropertyDelegate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
    @Accessor
    PropertyDelegate getPropertyDelegate();
}
