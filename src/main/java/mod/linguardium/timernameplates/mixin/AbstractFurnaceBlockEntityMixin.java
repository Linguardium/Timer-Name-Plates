package mod.linguardium.timernameplates.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.linguardium.timernameplates.util.BlockEntityUpdateUtil;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.collection.DefaultedList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends BlockEntity implements BlockEntityUpdateUtil.NbtSyncHelper {

    @Shadow int cookTime;

    @Shadow int cookTimeTotal;

    @Shadow protected DefaultedList<ItemStack> inventory;

    @Shadow int burnTime;

    // IGNORED
    // EXISTS ONLY TO SHUT UP COMPILER
    private AbstractFurnaceBlockEntityMixin() {
        super(null, null, null); // this should crash if it ever gets called
    }

    @Override
    public NbtCompound timernameplates$addRequiredDataToNbt(NbtCompound nbt) {
        if (!nbt.contains("CookTime")) {
            nbt.putInt("CookTime", this.cookTime);
        }
        if (!nbt.contains("CookTimeTotal")) {
            nbt.putInt("CookTimeTotal", this.cookTimeTotal);
        }
        if (!nbt.contains("BurnTime", NbtElement.NUMBER_TYPE)) {
            nbt.putInt("BurnTime", this.burnTime);
        }
        if (!nbt.contains("Items", NbtElement.LIST_TYPE)) {
            Inventories.writeNbt(nbt, this.inventory);
        }
        return nbt;
    }

    @Inject(method="setStack", at= @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;markDirty()V", shift = At.Shift.AFTER))
    private void alwaysUpdateClient(int slot, ItemStack stack, CallbackInfo ci) {
        this.updateOnClients();
    }

    @WrapOperation(method="tick",at= @At(value = "FIELD", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;cookTime:I", opcode = Opcodes.PUTFIELD))
    private static void trackCooktimeChanges(AbstractFurnaceBlockEntity instance, int value, Operation<Void> original) {
        original.call(instance, value);
        if (value % 20 == 0 || value == 1) { // furnace is always 1 tick behind
            ((BlockEntityUpdateUtil.NbtSyncHelper) instance).updateOnClients();
        }

    }

    @Override
    public BlockEntity timernameplates$asBlockEntity() {
        return this;
    }
}
