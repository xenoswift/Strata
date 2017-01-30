package com.blueyu2.strata;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;


/**
 * Created by blueyu2 on 1/4/16.
 */
@Mod(modid = StrataLib.MODID, version = StrataLib.VERSION )
//removed fuckery with load order. Solved DenseOres issue, but I don't like it.
    //deleted ubc support b/c this will be ts2 patch until code org'd to be a real update

public class Strata {
//    public static final String MODID = "strata";
//    public static final String VERSION = "1.7.10-1.5.5";

    @SidedProxy(serverSide = StrataLib.PROXY_SERVERSIDE, clientSide = StrataLib.PROXY_CLIENTSIDE)
    public static Proxy proxy;

    File config;

    public static Logger logger = LogManager.getLogger(StrataLib.MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        config = event.getSuggestedConfigurationFile();
        StrataConfig.configDir = new File(config.getParentFile(), StrataLib.MOD_NAME);
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
        StrataConfig.instance.loadConfig(config);
        MinecraftForge.EVENT_BUS.register(new ChunkReplacer());
        //can i shitcan this entirely its extra work that doesn't need to be done and this is gonna be a specialized distrib
//        if(Loader.isModLoaded("UndergroundBiomes"))
//            UBC.load();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        proxy.postInit();
    }
}
