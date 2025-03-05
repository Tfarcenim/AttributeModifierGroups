package tfar.attributemodifiergroups;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.concurrent.CompletableFuture;

@Mod(AttributeModifierGroups.MOD_ID)
public class AttributeModifierGroupsNeoForge {

    public AttributeModifierGroupsNeoForge(IEventBus eventBus) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.
        NeoForge.EVENT_BUS.addListener(AddReloadListenerEvent.class,event -> event.addListener(AttributeModifierGroups.listener = new AttributeModifierGroupReloadListener(event.getRegistryAccess())));
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> ModCommand.register(event.getDispatcher()));
        eventBus.addListener(this::gather);
        // Use NeoForge to bootstrap the Common mod.
        AttributeModifierGroups.init();

    }

    void gather(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(),new AttributeModifierGroupProvider(output,lookupProvider));
    }
}