package com.argus.pressurized.block;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.block.custom.BoilerCoreBlock;
import com.argus.pressurized.block.custom.BoilerShellBlock;
import com.argus.pressurized.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Pressurized.MODID);

    public static final RegistryObject<Block> BOILER_CORE_BLOCK = registerBlock("boiler_core", () -> new BoilerCoreBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> BOILER_SHELL_BLOCK_TIER_1_DAMAGE_4 = registerBlock("boiler_shell_tier_1_damage_4", () -> new BoilerShellBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), null));
    public static final RegistryObject<Block> BOILER_SHELL_BLOCK_TIER_1_DAMAGE_3 = registerBlock("boiler_shell_tier_1_damage_3", () -> new BoilerShellBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BOILER_SHELL_BLOCK_TIER_1_DAMAGE_4));
    public static final RegistryObject<Block> BOILER_SHELL_BLOCK_TIER_1_DAMAGE_2 = registerBlock("boiler_shell_tier_1_damage_2", () -> new BoilerShellBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BOILER_SHELL_BLOCK_TIER_1_DAMAGE_3));
    public static final RegistryObject<Block> BOILER_SHELL_BLOCK_TIER_1_DAMAGE_1 = registerBlock("boiler_shell_tier_1_damage_1", () -> new BoilerShellBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BOILER_SHELL_BLOCK_TIER_1_DAMAGE_2));
    public static final RegistryObject<Block> BOILER_SHELL_BLOCK_TIER_1_DAMAGE_0 = registerBlock("boiler_shell_tier_1_damage_0", () -> new BoilerShellBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), BOILER_SHELL_BLOCK_TIER_1_DAMAGE_1));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
         RegistryObject<T> toReturn = BLOCKS.register(name, block);
         registerBlockItem(name, (RegistryObject<Block>) toReturn);
         return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<Block> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
