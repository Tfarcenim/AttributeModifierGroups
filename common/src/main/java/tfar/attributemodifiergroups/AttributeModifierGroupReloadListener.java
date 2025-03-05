package tfar.attributemodifiergroups;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;


public class AttributeModifierGroupReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final HolderLookup.Provider registries;
    private Map<ResourceLocation, Map<Holder<Attribute>, AttributeModifier>> map = ImmutableMap.of();

    public static final ResourceKey<Registry<AttributeModifierGroup>> ATTRIBUTE_MODIFIER_GROUP = AttributeModifierGroups.createRegistryKey("attribute_modifier_group");


    public AttributeModifierGroupReloadListener(HolderLookup.Provider registries) {
        super(GSON, Registries.elementsDirPath(ATTRIBUTE_MODIFIER_GROUP));
        this.registries = registries;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        Builder<ResourceLocation, Map<Holder<Attribute>, AttributeModifier>> builder = ImmutableMap.builder();
        RegistryOps<JsonElement> registryops = this.registries.createSerializationContext(JsonOps.INSTANCE);

        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            JsonElement element = entry.getValue();
            if (element instanceof JsonArray jsonArray && jsonArray.isEmpty()) {
                LOGGER.info("SKipping empty attribute modifier group: {}",resourcelocation);
                continue;
            }
            try {

                List<AttributeModifierGroup.Entry> raw = AttributeModifierGroup.CODEC.parse(registryops, element)
                        .getOrThrow(JsonParseException::new);

                Map<Holder<Attribute>, AttributeModifier> cooked = AttributeModifierGroup.cook(raw,resourcelocation);

                builder.put(resourcelocation, cooked);
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                LOGGER.error("Parsing error attribute modifier group {}", resourcelocation, jsonparseexception);
            }
        }

        map = builder.build();
        LOGGER.info("Loaded {} attribute modifier groups", map.size());
    }

    public Map<ResourceLocation, Map<Holder<Attribute>, AttributeModifier>> getMap() {
        return map;
    }
}
