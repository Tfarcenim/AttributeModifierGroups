package tfar.attributemodifiergroups;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeModifierGroup {

    public static final Codec<List<Entry>> CODEC = Entry.CODEC.listOf();

    public static Map<Holder<Attribute>, AttributeModifier> cook(List<Entry> entries, ResourceLocation resourcelocation) {
        Map<Holder<Attribute>,AttributeModifier> map = new HashMap<>();
        for (Entry entry : entries) {
            if (map.put(entry.attribute,new AttributeModifier(resourcelocation, entry.value,entry.operation))!=null) {
                throw new JsonParseException("Duplicate attribute: "+entry.attribute);
            }
        }
        return map;
    }

    public record Entry(Holder<Attribute> attribute, double value, AttributeModifier.Operation operation) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Attribute.CODEC.fieldOf("attribute").forGetter(Entry::attribute),
                Codec.DOUBLE.fieldOf("value").forGetter(Entry::value),
                AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(Entry::operation)
                ).apply(instance, Entry::new)
        );
    }
}
