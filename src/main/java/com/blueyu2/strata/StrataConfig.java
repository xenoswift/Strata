package com.blueyu2.strata;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by blueyu2 on 1/4/16.
 */
public class StrataConfig {
    public static final StrataConfig instance = new StrataConfig();
    public static final String CATEGORY_STONE = "stones.";
    public static final String CATEGORY_ORE = "ores.";
    public static final String STONE_TEXTURE_STRING = "stoneTexture";
    public static final String ORE_TEXTURE_STRING = "oreTexture";

    public static File configDir = null;

    public static int maxDepth = 2;
    //public static boolean uninstall = false;
    
    public static int strata_1_hard = 2;
    public static int strata_2_hard = 3;

    public void loadConfig(File file){
        Configuration baseConfig = new Configuration(file, true);

        baseConfig.load();
        //uninstall = baseConfig.getBoolean("Uninstall", "Main", false, "Set this to true and go to all the areas you went to with Strata installed to replace all Strata blocks in the world with the original blocks. This allows for safe removal of Strata without your worlds getting ruined.");
        
        strata_1_hard = baseConfig.getInt("First strata mining level", "Main", 2, 1, 10, "Changes the mining level required to mine stone on the first strata level");
        strata_2_hard = baseConfig.getInt("Second strata mining level", "Main", 3, 1, 10, "Changes the mining level required to mine stone on the second strata level");
        
        //maxDepth = baseConfig.getInt("Max generation depth", "Main", 2, 0, 60, "Lowest y level strata stone will generate to");
        
        baseConfig.save();

        File vanillaFile = new File(configDir, "minecraft.cfg");

        if(!vanillaFile.exists()){
            Configuration vanillaConfig = new Configuration(vanillaFile, true);
            vanillaConfig.load();
            StrataRegistry.initVanillaBlocks();
            for(Block block : StrataRegistry.blocks.values()){
                if(block instanceof StrataBlock){
                    StrataBlock strataBlock = (StrataBlock) block;
                    String cat; //mrow

                    switch (strataBlock.type){
                        case STONE:
                            cat = CATEGORY_STONE + strataBlock.blockName;
                            if(strataBlock.meta > 0)
                                cat = cat + ":" + strataBlock.meta;
                            vanillaConfig.get(cat, STONE_TEXTURE_STRING, strataBlock.stoneTexture);
                            break;
                        case ORE:
                            cat = CATEGORY_ORE + strataBlock.blockName;
                            if(strataBlock.meta > 0)
                                cat = cat + ":" + strataBlock.meta;
                            vanillaConfig.get(cat, ORE_TEXTURE_STRING, strataBlock.oreTexture);
                            vanillaConfig.get(cat, STONE_TEXTURE_STRING, strataBlock.stoneTexture);
                            break;
                    }
                }
            }
            vanillaConfig.save();
        }

        if(configDir.listFiles() != null){
            for(File configFile : configDir.listFiles()){
                String fileName = configFile.getName();
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                if(!extension.equals("cfg"))
                    continue;

                Configuration config = new Configuration(configFile, true);
                config.load();
                for(String cat : config.getCategoryNames()){
                    if(cat.startsWith(CATEGORY_STONE)){
                        addBlock(cat, StrataBlock.Type.STONE, config);
                    }
                    else if(cat.startsWith(CATEGORY_ORE)){
                        addBlock(cat, StrataBlock.Type.ORE, config);
                    }
                }
                config.save();
            }
        }
    }

    private void addBlock(String cat, StrataBlock.Type type, Configuration config){
        String blockId = "NOPE";
        switch (type){
            case STONE:
                blockId = cat.substring(CATEGORY_STONE.length());
                break;
            case ORE:
                blockId = cat.substring(CATEGORY_ORE.length());
                break;
        }
        if(blockId.equals("NOPE"))
            return;

        int index = blockId.indexOf(':');
        int meta = 0;

        try{
            String[] values = blockId.split(":");
            //Add 1 to account for ':'
            blockId = blockId.substring(0, values[0].length() + 1 + values[1].length());
            meta = Integer.parseInt(values[2]);
        }
        catch (Exception e){
            //Probably doesn't have metadata
        }

        if(index > 1){
            Block baseBlock = Block.getBlockFromName(blockId);
            if(baseBlock != null){
                if(StrataRegistry.blocks.containsKey(StrataRegistry.getBlockMeta(baseBlock, meta, false)))
                    return;
                switch (type){
                    case STONE:
                        StrataRegistry.registerStone(blockId, meta, config.get(cat, STONE_TEXTURE_STRING, "").getString().trim());
                        break;
                    case ORE:
                        StrataRegistry.registerOre(blockId, meta, config.get(cat, ORE_TEXTURE_STRING, "").getString().trim(), config.get(cat, STONE_TEXTURE_STRING, "").getString().trim());
                }
            }
        }
    }
}
