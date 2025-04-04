package com.argus.pressurized.block;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.block.boiler.BoilerCoreBlock;
import com.argus.pressurized.block.boiler.shell.t1.BoilerShellBlockTier1Damage0;
import com.argus.pressurized.block.boiler.shell.t1.BoilerShellBlockTier1Damage1;
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

    public static final RegistryObject<Block> VISUAL_BLOCK = registerBlock("visual_block", () -> new VisualBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> CRUCIBLE_FURNACE_BLOCK = registerBlock("crucible_furnace", () -> new CrucibleFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> CRUCIBLE_FURNACE_TEST_BLOCK = registerBlock("crucible_furnace_test", () -> new CrucibleFurnaceTestBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> BOILER_CORE_BLOCK = registerBlock("boiler_core", () -> new BoilerCoreBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> BOILER_SHELL_BLOCK_T1_D1 = registerBlock("boiler_shell_t1_d1", () -> new BoilerShellBlockTier1Damage1(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> BOILER_SHELL_BLOCK_T1_D0 = registerBlock("boiler_shell_t1_d0", () -> new BoilerShellBlockTier1Damage0(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

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
