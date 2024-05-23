package net.derfruhling.minecraft.markit.fabric;

import net.derfruhling.minecraft.markit.Markit;
import net.fabricmc.api.ModInitializer;

public class MarkitFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Markit.init();
    }
}