package ru.glassspirit.cnpcntrpg.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import noppes.npcs.api.NpcAPI;

/**
 * Used as dummy mod container just to subscribe for forge events
 */
@Mod(modid = "cnpc-ntrpg-forge", serverSideOnly = true, acceptableRemoteVersions = "*")
public class CnpcRpgForge {

    @Mod.Instance
    public static CnpcRpgForge instance;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        if (instance == null) {
            instance = this;
        }
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        try {
            Class.forName("noppes.npcs.api.NpcAPI");
            CustomNPCsEventListener listener = new CustomNPCsEventListener();

            NpcAPI.Instance().events().register(listener);
            MinecraftForge.EVENT_BUS.register(listener);
        } catch (ClassNotFoundException e) {
            // CustomNPCs not found!
        }
    }
}
