package tfar.attributemodifiergroups;

import com.google.common.collect.Sets;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AttributeModifierGroupProvider implements DataProvider {

    private final CompletableFuture<HolderLookup.Provider> registries;
    private final PackOutput.PathProvider pathProvider;

    public AttributeModifierGroupProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createRegistryElementsPathProvider(AttributeModifierGroupReloadListener.ATTRIBUTE_MODIFIER_GROUP);
        this.registries = registries;
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(future -> this.run(output, future));
    }
    protected CompletableFuture<?> run(final CachedOutput output, final HolderLookup.Provider registries) {
        final Set<ResourceLocation> set = Sets.newHashSet();
        final List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildGroups(
                (location, group) -> {
                    if (!set.add(location)) {
                        throw new IllegalStateException("Duplicate group " + location);
                    } else {
                        list.add(DataProvider.saveStable(output, registries, AttributeModifierGroup.CODEC, group, pathProvider.json(location)));
                    }
                }
        );
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    private void buildGroups(AttributeModifierGroupOutput output) {
        AttributeModifierGroupBuilder.begin("giant")
                .append(Attributes.SCALE,3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .append(Attributes.MAX_HEALTH,3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .append(Attributes.ATTACK_DAMAGE,3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .append(Attributes.BLOCK_INTERACTION_RANGE,3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .append(Attributes.ENTITY_INTERACTION_RANGE,3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .save(output);
    }

    @Override
    public String getName() {
        return "Example Attribute Modifier Groups";
    }

    @FunctionalInterface
    public interface AttributeModifierGroupOutput {
        void accept(ResourceLocation location,List<AttributeModifierGroup.Entry> group);
    }

}
