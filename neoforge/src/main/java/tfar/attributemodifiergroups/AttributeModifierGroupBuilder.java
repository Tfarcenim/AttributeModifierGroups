package tfar.attributemodifiergroups;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

public class AttributeModifierGroupBuilder {

    final List<AttributeModifierGroup.Entry> entries = new ArrayList<>();
    final ResourceLocation id;

    public AttributeModifierGroupBuilder(ResourceLocation name) {
        id = name;
    }

    public AttributeModifierGroupBuilder append(Holder<Attribute> attribute, double value, AttributeModifier.Operation operation) {
        return append(new AttributeModifierGroup.Entry(attribute, value, operation));
    }

    public AttributeModifierGroupBuilder append(AttributeModifierGroup.Entry entry) {
        Holder<Attribute> holder =entry.attribute();
        for (AttributeModifierGroup.Entry entry1 : entries) {
            if (entry1.attribute().equals(holder)) {
                throw new IllegalStateException("Duplicate attribute: "+holder);
            }
        }
        entries.add(entry);
        return this;
    }

    public static AttributeModifierGroupBuilder begin(ResourceLocation name) {
        return new AttributeModifierGroupBuilder(name);
    }

    public static AttributeModifierGroupBuilder begin(String name) {
        return begin(AttributeModifierGroups.id(name));
    }

    public void save(AttributeModifierGroupProvider.AttributeModifierGroupOutput output) {
        this.ensureValid();
        output.accept(id, entries);
    }

    /**
     * Makes sure that this group is valid and obtainable.
     */
    private void ensureValid() {

    }

}
