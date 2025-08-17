package dev.lazurite.hexaplex.mixin;

import dev.lazurite.hexaplex.Hexaplex;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow private volatile boolean pause;
    @Shadow @Final private DeltaTracker.Timer deltaTracker;

    @Inject(
        method = "runTick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;blitToScreen()V"
        )
    )
    private void runTick_unbindWrite(boolean bl, CallbackInfo info) {
        if (Hexaplex.dirty) {
            Hexaplex.INSTANCE.setUniforms();
            Hexaplex.dirty = false;
        }

        // TODO do I need to have this if check?
        if (!Hexaplex.INSTANCE.getProfile().equals(Hexaplex.ProfileNames.NORMAL) && !(Hexaplex.INSTANCE.getStrength() == 0.0)) {
            Hexaplex.FILTER.render(this.pause ? this.deltaTracker.getGameTimeDeltaPartialTick(this.pause) : this.deltaTracker.getGameTimeDeltaTicks());
        }
    }

}
