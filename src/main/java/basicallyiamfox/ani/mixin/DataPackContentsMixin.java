package basicallyiamfox.ani.mixin;

import basicallyiamfox.ani.interfaces.IDataPackContents;
import basicallyiamfox.ani.server.ServerAbilityLoader;
import basicallyiamfox.ani.server.ServerTransformationLoader;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.DataPackContents;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(DataPackContents.class)
public abstract class DataPackContentsMixin implements IDataPackContents {
    @Final
    @Unique
    private ServerTransformationLoader stLoader = new ServerTransformationLoader();
    @Final
    @Unique
    private ServerAbilityLoader saLoader = new ServerAbilityLoader();

    @Override
    public @NotNull ServerTransformationLoader getServerTransformationLoader() {
        return stLoader;
    }
    @Override
    public @NotNull ServerAbilityLoader getServerAbilityLoader() {
        return saLoader;
    }

    @ModifyReturnValue(method = "getContents", at = @At("RETURN"))
    private List<ResourceReloader> animorphs$getContents(List<ResourceReloader> list) {
        ArrayList<ResourceReloader> list1 = new ArrayList<>(list);
        list1.add(stLoader);
        list1.add(saLoader);
        return list1;
    }
}
