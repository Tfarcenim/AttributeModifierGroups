package tfar.attributemodifiergroups;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class AttributeModifierGroupsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(AttributeModifierGroupReloadListener.ATTRIBUTE_MODIFIER_GROUP.location(),
                registries -> {
                    AttributeModifierGroups.listener = new ReloadListenerFabric(registries);
                    return (IdentifiableResourceReloadListener) AttributeModifierGroups.listener;
                });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ModCommand.register(dispatcher));
        // Use Fabric to bootstrap the Common mod.
        AttributeModifierGroups.init();
    }
}
