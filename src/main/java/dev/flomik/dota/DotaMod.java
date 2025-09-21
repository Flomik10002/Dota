package dev.flomik.dota;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(DotaMod.MODID)
public class DotaMod {

    public static final String MODID = "dota";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DotaMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ClientModEvents::onClientSetup);

        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DotaConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Future setup
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Future server init
    }

    public static class ClientModEvents {
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Future client init
        }
    }
}
