package com.argus.pressurized.util;

import com.argus.pressurized.Pressurized;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {

        public static final TagKey<Block> BOILER_SHELL_BLOCKS = tag("boiler_shell");
        public static final TagKey<Block> BOILER_INTERIOR_BLOCKS = tag("boiler_interior");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(Pressurized.MODID, name));
        }
    }

}
