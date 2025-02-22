package net.backupcup.mcde.block.entity;

import org.jetbrains.annotations.Nullable;

import net.backupcup.mcde.screen.handler.RollBenchScreenHandler;
import net.backupcup.mcde.util.EnchantmentSlots;
import net.backupcup.mcde.util.EnchantmentUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RollBenchBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    public RollBenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROLL_BENCH, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.mcde.roll_bench");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new RollBenchScreenHandler(syncId, inv, this, ScreenHandlerContext.create(world, pos));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, RollBenchBlockEntity entity) {
        if (world.isClient()) {
            return;
        }
        if (entity.inventory.isEmpty()) {
            return;
        }
        var itemStack = entity.inventory.get(0);
        if (EnchantmentSlots.fromItemStack(itemStack) != null) {
            return;
        }
        EnchantmentUtils.generateEnchantments(itemStack).updateItemStack(itemStack);
    }
}
