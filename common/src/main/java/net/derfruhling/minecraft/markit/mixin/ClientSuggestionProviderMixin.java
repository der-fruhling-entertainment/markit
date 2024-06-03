package net.derfruhling.minecraft.markit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Mixin(ClientSuggestionProvider.class)
public class ClientSuggestionProviderMixin {
    @WrapOperation(method = "getCustomTabSugggestions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientSuggestionProvider;getOnlinePlayerNames()Ljava/util/Collection;"))
    public Collection<String> getOnlinePlayerNamesFormatted(ClientSuggestionProvider instance, Operation<Collection<String>> original) {
        return original.call(instance)
                .stream()
                .map(s -> s.replace("_", "\\_"))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
