package com.argus.pressurized.gui.menu;
import com.argus.pressurized.block.ModBlocks;
import com.argus.pressurized.blockentity.CrucibleFurnaceBlockEntity;
import com.argus.pressurized.gui.menu.slot.FuelSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.world.inventory.FurnaceFuelSlot;
import org.jetbrains.annotations.NotNull;

public class CrucibleFurnaceMenu extends AbstractContainerMenu {
    private final CrucibleFurnaceBlockEntity blockEntity;

    public CrucibleFurnaceMenu(int id, Inventory playerInventory, CrucibleFurnaceBlockEntity blockEntity) {
        super(ModMenuTypes.CRUCIBLE_FURNACE_MENU.get(), id);
        this.blockEntity = blockEntity;

        IItemHandler itemHandler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElseThrow(() -> new IllegalStateException("Item handler capability is missing for block entity at " + blockEntity.getBlockPos()));

        createPlayerHotbar(playerInventory);
        createPlayerInventory(playerInventory);

        for (int i = 0; i < 3; i++) {
            this.addSlot(new SlotItemHandler(itemHandler, i, 17 + (i * 18), 13) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public int getMaxStackSize(@NotNull ItemStack stack) {
                    return 1;
                }

                @Override
                public void set(@NotNull ItemStack stack) {
                    ItemStack copiedItemStack = stack.copyWithCount(1);
                    super.set(copiedItemStack);
                    return;
                }
            });
        }
        for (int i = 0; i < 3; i++) {
            this.addSlot(new SlotItemHandler(itemHandler, i + 3, 17 + (i * 18), 31) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public int getMaxStackSize(@NotNull ItemStack stack) {
                    return 1;
                }

                @Override
                public void set(@NotNull ItemStack stack) {
                    ItemStack copiedItemStack = stack.copyWithCount(1);
                    super.set(copiedItemStack);
                    return;
                }
            });
        }

        this.addSlot(new FuelSlot(itemHandler, 6, 35, 57));

        this.addSlot(new SlotItemHandler(itemHandler, 7, 143, 13) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(net.minecraft.world.item.Items.BUCKET);
            }
        });

        this.addSlot(new SlotItemHandler(itemHandler, 8, 143, 57) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
    }

    public CrucibleFurnaceMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, getBlockEntity(playerInventory, buf.readBlockPos()));
    }

    private static CrucibleFurnaceBlockEntity getBlockEntity(Inventory playerInventory, BlockPos pos) {
        BlockEntity entity = playerInventory.player.level().getBlockEntity(pos);
        if (entity instanceof CrucibleFurnaceBlockEntity furnaceEntity) {
            return furnaceEntity;
        }
        throw new IllegalStateException("Block entity is missing or incorrect at " + pos);
    }

    public CrucibleFurnaceBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    private void createPlayerInventory(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv,
                        9 + column + (row * 9),
                        8 + (column * 18),
                        84 + (row * 18)));
            }
        }
    }

    private void createPlayerHotbar(Inventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv,
                    column,
                    8 + (column * 18),
                    142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot fromSlot = getSlot(index);
        ItemStack fromStack = fromSlot.getItem();

        if(fromStack.getCount() <= 0)
            fromSlot.set(ItemStack.EMPTY);

        if(!fromSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack copyFromStack = fromStack.copy();

        if(index < 36) {
            if(!moveItemStackTo(fromStack, 36, 45, false))
                return ItemStack.EMPTY;
        } else if (index < 45) {
            if(!moveItemStackTo(fromStack, 0, 36, false))
                return ItemStack.EMPTY;
        } else {
            System.err.println("Invalid slot index: " + index);
            return ItemStack.EMPTY;
        }

        fromSlot.setChanged();
        fromSlot.onTake(player, fromStack);

        return copyFromStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                pPlayer, ModBlocks.CRUCIBLE_FURNACE_BLOCK.get());
    }
}
