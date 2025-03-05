package tfar.attributemodifiergroups;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ModCommand {
    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(AttributeModifierGroups.MOD_ID)
                .requires(p_136958_ -> p_136958_.hasPermission(2))
                .then(Commands.literal("give")
                        .then(Commands.argument("targets", EntityArgument.entities()).then(
                                        Commands.argument("group", ResourceLocationArgument.id())
                                                .suggests(ALL_GROUPS)
                                                .executes(ModCommand::apply)
                                )
                        )
                ).then(Commands.literal("clear")
                        .then(Commands.argument("targets", EntityArgument.entities()).then(
                                        Commands.argument("group", ResourceLocationArgument.id())
                                                .suggests(ALL_GROUPS)
                                                .executes(ModCommand::removeGroup)))
                        .then(Commands.argument("group", ResourceLocationArgument.id())
                                .suggests(ALL_GROUPS)
                                .executes(ModCommand::removeSelfGroup)))
        );
    }


    static int apply(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        ResourceLocation group = ResourceLocationArgument.getId(ctx, "group");
        Map<Holder<Attribute>, AttributeModifier> map = AttributeModifierGroups.listener.getMap().get(group);

        int i = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity living) {
                try {
                    for (var entry : map.entrySet()) {
                        living.getAttribute(entry.getKey()).addPermanentModifier(entry.getValue());
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    ctx.getSource().sendFailure(Component.literal("Something went wrong, check logs"));
                }
            }
        }

        return i;
    }

    static int removeSelfGroup(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        return remove(ctx, List.of(player));
    }

    static int removeGroup(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        return remove(ctx,targets);
    }

    static int remove(CommandContext<CommandSourceStack> ctx,Collection<? extends Entity> targets) {
        ResourceLocation group = ResourceLocationArgument.getId(ctx, "group");
        Map<Holder<Attribute>, AttributeModifier> map = AttributeModifierGroups.listener.getMap().get(group);
        int i = 0;
        for (Entity entity : targets) {
            if (entity instanceof LivingEntity living) {
                try {
                    for (var entry : map.entrySet()) {
                        living.getAttribute(entry.getKey()).removeModifier(entry.getValue());
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    ctx.getSource().sendFailure(Component.literal("Something went wrong, check logs"));
                }
            }
        }
        return i;
    }

    public static final SuggestionProvider<CommandSourceStack> ALL_GROUPS = SuggestionProviders.register(
            AttributeModifierGroups.id("all_groups"),
            (context, builder) -> SharedSuggestionProvider.suggestResource(AttributeModifierGroups.listener.getMap().keySet(), builder)
    );

}
