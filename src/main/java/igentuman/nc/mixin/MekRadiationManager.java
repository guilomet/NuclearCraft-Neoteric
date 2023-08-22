package igentuman.nc.mixin;

import igentuman.nc.handler.config.CommonConfig;
import mekanism.api.Coord4D;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.radiation.RadiationManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static igentuman.nc.handler.config.CommonConfig.RADIATION_CONFIG;

@Mixin(RadiationManager.class)
public abstract class MekRadiationManager {

    public boolean isMekRadiationEnabled()
    {
        return MekanismConfig.general.radiationEnabled.getOrDefault();
    }

    @Inject(method = "isRadiationEnabled", at = @At("TAIL"), remap=false, cancellable = true)
    public void isRadiationEnabled(CallbackInfoReturnable<Boolean> callback)
    {
        if(!RADIATION_CONFIG.MEKANISM_RADIATION_INTEGRATION.get()) return;
        callback.setReturnValue(true);
    }

    @Inject(method = "radiate(Lmekanism/api/Coord4D;D)V", at = @At("HEAD"), remap=false, cancellable = true)
    public void radiate(Coord4D source, double magnitude, CallbackInfo callback) {
        if(!RADIATION_CONFIG.MEKANISM_RADIATION_INTEGRATION.get()) return;
        Level level = ServerLifecycleHooks.getCurrentServer().getLevel(source.dimension);
        if(level == null) return;
        igentuman.nc.radiation.data.RadiationManager.get(level).addRadiation(level, magnitude*10, source.getX(), source.getY(), source.getZ());
        if(!isMekRadiationEnabled()) {
            callback.cancel();
        }
    }
}