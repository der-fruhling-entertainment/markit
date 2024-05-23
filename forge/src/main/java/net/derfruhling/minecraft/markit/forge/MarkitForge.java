package net.derfruhling.minecraft.markit.forge;

import dev.architectury.platform.forge.EventBuses;
import net.derfruhling.minecraft.markit.Markit;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Markit.MOD_ID)
public class MarkitForge {
    public MarkitForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Markit.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Markit.init();
    }
}