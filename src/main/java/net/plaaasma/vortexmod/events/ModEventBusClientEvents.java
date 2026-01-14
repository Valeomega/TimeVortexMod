package net.plaaasma.vortexmod.events;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.plaaasma.vortexmod.VortexMod;
import net.plaaasma.vortexmod.block.entity.ModBlockEntities;
import net.plaaasma.vortexmod.block.entity.renderer.BioscannerBlockEntityRenderer;
import net.plaaasma.vortexmod.block.entity.renderer.MonitorBlockEntityRenderer;
import net.plaaasma.vortexmod.entities.client.models.*;
import net.plaaasma.vortexmod.entities.client.ModModelLayers;
import net.plaaasma.vortexmod.screen.ModMenuTypes;
import net.plaaasma.vortexmod.screen.custom.menu.KeypadMenu;
import net.plaaasma.vortexmod.screen.custom.menu.ScannerMenu;
import net.plaaasma.vortexmod.screen.custom.menu.SizeManipulatorMenu;
import net.plaaasma.vortexmod.screen.custom.screen.KeypadScreen;
import net.plaaasma.vortexmod.screen.custom.screen.ScannerScreen;
import net.plaaasma.vortexmod.screen.custom.screen.SizeManipulatorScreen;

import java.lang.reflect.Method;

@EventBusSubscriber(modid = VortexMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.LOST_TRAVELER_LAYER, LostTravelerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.DALEK_LAYER, DalekModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.LASER_LAYER, LaserModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.TARDIS_LAYER, TardisModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ANGEL_LAYER, AngelModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.RIFT_LAYER, RiftModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.BIOMETRIC_BLOCK_BE.get(), BioscannerBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MONITOR_BE.get(), MonitorBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Register menu screens using reflection since MenuScreens.register is not public
            try {
                Method registerMethod = MenuScreens.class.getDeclaredMethod("register", 
                    net.minecraft.world.inventory.MenuType.class, 
                    net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor.class);
                registerMethod.setAccessible(true);
                
                registerMethod.invoke(null, ModMenuTypes.SIZE_MANIPULATOR_MENU.get(), 
                    (MenuScreens.ScreenConstructor<SizeManipulatorMenu, SizeManipulatorScreen>) SizeManipulatorScreen::new);
                registerMethod.invoke(null, ModMenuTypes.KEYPAD_MENU.get(), 
                    (MenuScreens.ScreenConstructor<KeypadMenu, KeypadScreen>) KeypadScreen::new);
                registerMethod.invoke(null, ModMenuTypes.SCANNER_MENU.get(), 
                    (MenuScreens.ScreenConstructor<ScannerMenu, ScannerScreen>) ScannerScreen::new);
                
                VortexMod.LOGGER.info("Registered menu screens");
            } catch (Exception e) {
                VortexMod.LOGGER.error("Failed to register menu screens", e);
            }
        });
    }
}
