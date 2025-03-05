package tfar.attributemodifiergroups;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public class ReloadListenerFabric extends AttributeModifierGroupReloadListener implements IdentifiableResourceReloadListener {


    public ReloadListenerFabric(HolderLookup.Provider registries) {
        super(registries);
    }

    @Override
    public ResourceLocation getFabricId() {
        return AttributeModifierGroupReloadListener.ATTRIBUTE_MODIFIER_GROUP.location();
    }
}
