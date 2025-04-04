package com.argus.pressurized.blockentity;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.capability.HeatCapability;
import com.argus.pressurized.gui.menu.CrucibleFurnaceMenu;
import com.argus.pressurized.network.ModPackets;
import com.argus.pressurized.network.SyncFluidsPacket;
import com.argus.pressurized.recipe.CrucibleFurnaceRecipe;
import com.argus.pressurized.recipe.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CrucibleFurnaceBlockEntity extends BlockEntity implements MenuProvider {
    private static final Component TITLE = Component.translatable("container." + Pressurized.MODID + ".crucible_furnace_block");
    private static final int MAX_FLUID_CAPACITY = 8000; // 8 buckets
    private List<FluidStack> storedFluids;

    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public CrucibleFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUCIBLE_FURNACE_BLOCK_ENTITY.get(), pos, state);
        storedFluids = new ArrayList<>();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {

        if (this.hasFuel()) {

            SimpleContainer inputContainer = new SimpleContainer(1);

            for (int i = 0; i < 6; i++) {
                ItemStack input = getInputSlot(i);
                inputContainer.setItem(0, getInputSlot(i));
                if (!input.isEmpty()) {
                    CrucibleFurnaceRecipe recipe = getRecipeForInput(inputContainer, level);
                    if (recipe != null) {
                        int itemHeat = getHeat(input);
                        if (itemHeat < recipe.getRequiredHeat()) {
                            input.getOrCreateTag().putInt("Heat", itemHeat + 1);
                        }

                        if (itemHeat >= recipe.getRequiredHeat()) {
                            FluidStack fluid = recipe.getOutputFluid();
                            if (totalFluid() + fluid.getAmount() <= MAX_FLUID_CAPACITY) {
                                boolean foundExistingFluidStack = false;
                                for (FluidStack stack : storedFluids) {
                                    if (stack.isFluidEqual(fluid)) {
                                        stack.grow(fluid.getAmount());
                                        foundExistingFluidStack = true;
                                        break;
                                    }
                                }
                                if (!foundExistingFluidStack) {
                                    storedFluids.add(fluid.copy());
                                }
                                if (!level.isClientSide()) {
                                    syncFluidsToTrackingPlayers();
                                }
                                resetInputSlot(i);
                            }
                        }
                    }
                }
            }
        }
        if (!level.isClientSide) {
            handleFluidExtraction();
        }
    }

    public void syncFluidsToTrackingPlayers() {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            ModPackets.INSTANCE.send(
                    PacketDistributor.TRACKING_CHUNK.with(() -> serverLevel.getChunkAt(worldPosition)),
                    new SyncFluidsPacket(worldPosition, storedFluids)
            );
        }
    }

    private int getHeat(ItemStack stack) {
        Tag tag = stack.getOrCreateTag().get("Heat");
        if (tag != null) {
            return Integer.parseInt(tag.toString());
        }
        return 0;
    }

    private boolean hasFuel() {
        // Check if the fuel slot has a valid fuel item
        ItemStack fuel = getFuelSlot();
        return !fuel.isEmpty() && ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING) > 0;
    }

    private int totalFluid() {
        int total = 0;
        for (FluidStack stack : storedFluids) {
            total += stack.getAmount();
        }
        return total;
    }

    private CrucibleFurnaceRecipe getRecipeForInput(SimpleContainer inputContainer, Level level) {
        for (CrucibleFurnaceRecipe recipe : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.CRUCIBLE_FURNACE.get())) {
            if (recipe.getInput().is(inputContainer.getItem(0).getItem())) {
                return recipe;
            }
        }
        return null;
    }

    private void resetInputSlot(int slotIndex) {
        // Logic to reset an input slot (set to empty)
        setInputSlot(slotIndex, ItemStack.EMPTY);
    }

    // Methods for accessing the slots
    public ItemStack getInputSlot(int index) {
        return itemHandler.getStackInSlot(index);
    }

    public ItemStack getFuelSlot() {
        return itemHandler.getStackInSlot(6);  // Fuel slot
    }

    public void setInputSlot(int index, ItemStack stack) {
        itemHandler.setStackInSlot(index, stack);
    }

    public List<FluidStack> getStoredFluids() {
        return storedFluids;
    }

    public void setStoredFluids(List<FluidStack> fluids) {
        this.storedFluids = fluids;
    }

    //Extracts 1000mb of fluid into the output slot
    private void handleFluidExtraction() {
        ItemStack bucketSlot = itemHandler.getStackInSlot(7);
        ItemStack outputSlot = itemHandler.getStackInSlot(8);

        if (!bucketSlot.isEmpty()) {
            Iterator<FluidStack> iterator = storedFluids.iterator();
            while (iterator.hasNext()) {
                FluidStack stack = iterator.next();

                if (stack.getAmount() >= 1000) {
                    ItemStack filledBucket = stack.getFluid().getBucket().getDefaultInstance();

                    if (outputSlot.isEmpty() || (ItemStack.isSameItemSameTags(outputSlot, filledBucket) && outputSlot.getCount() < outputSlot.getMaxStackSize())) {
                        itemHandler.extractItem(7, 1, false);
                        itemHandler.insertItem(8, filledBucket, false);
                        stack.shrink(1000);

                        if (stack.isEmpty()) {
                            iterator.remove();
                        }

                        setChanged();
                        if (!level.isClientSide()) {
                            syncFluidsToTrackingPlayers();
                        }
                    }
                }
            }
        }
    }

    public LazyOptional<IFluidHandler> getFluidCapability() {
        return LazyOptional.of(() -> new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Override
            public FluidStack getFluidInTank(int tank) {
                if (!storedFluids.isEmpty())
                    return storedFluids.get(0);
                return FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {
                return MAX_FLUID_CAPACITY;
            }

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                return 0;
            }

            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                return FluidStack.EMPTY;
            }

            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                return FluidStack.EMPTY;
            }
        });
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, LazyOptional.of(() -> itemHandler));
        }
        return super.getCapability(capability, side);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());

        ListTag fluidListTag = new ListTag();
        for (FluidStack stack : storedFluids) {
            CompoundTag fluidTag = new CompoundTag();
            stack.writeToNBT(fluidTag);
            fluidListTag.add(fluidTag);
        }
        tag.put("Fluids", fluidListTag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        storedFluids.clear();
        ListTag fluidListTag = tag.getList("Fluids", Tag.TAG_COMPOUND);
        for (Tag t : fluidListTag) {
            CompoundTag fluidTag = (CompoundTag) t;
            FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidTag);
            if (!stack.isEmpty()) {
                storedFluids.add(stack);
            }
        }
    }

    public void dropContents() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, container);
    }

    @Override
    public Component getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new CrucibleFurnaceMenu(id, playerInventory, this);
    }
}