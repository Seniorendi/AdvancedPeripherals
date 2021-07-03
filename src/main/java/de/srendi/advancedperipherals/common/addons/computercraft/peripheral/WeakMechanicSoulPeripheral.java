package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.mojang.datafixers.util.Pair;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.util.InventoryUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.FakePlayerProviderTurtle;
import de.srendi.advancedperipherals.common.util.TurtleFakePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WeakMechanicSoulPeripheral extends BasePeripheral {

    private static final int TURTLE_SUCK_ITEM_RADIUS = 2;

    public WeakMechanicSoulPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableChunkyTurtle;
    }

    private Optional<MethodResult> turtleChecks() {
        if (turtle == null) {
            return Optional.of(MethodResult.of(null, "Well, you can use it only from turtle now!"));
        }
        if (turtle.getOwningPlayer() == null) {
            return Optional.of(MethodResult.of(null, "Well, turtle should have owned player!"));
        }
        MinecraftServer server = getWorld().getServer();
        if (server == null) {
            return Optional.of(MethodResult.of(null, "Problem with server finding ..."));
        }
        return Optional.empty();
    }

    protected AxisAlignedBB getBox(BlockPos pos, int radius) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return new AxisAlignedBB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );
    }

    protected List<ItemEntity> getItems() {
        return getWorld().getEntitiesOfClass(ItemEntity.class, getBox(getPos(), TURTLE_SUCK_ITEM_RADIUS));
    }

    protected int suckItem(ItemEntity entity, int requiredQuantity) {
        ItemStack stack = entity.getItem().copy();

        ItemStack storeStack;
        ItemStack leaveStack;
        if (stack.getCount() > requiredQuantity) {
            storeStack = stack.split(requiredQuantity);
            leaveStack = stack;
        } else {
            storeStack = stack;
            leaveStack = ItemStack.EMPTY;
        }

        ItemStack remainder = InventoryUtil.storeItems(storeStack, turtle.getItemHandler(), turtle.getSelectedSlot());

        if (remainder != storeStack) {
            if (remainder.isEmpty() && leaveStack.isEmpty()) {
                entity.remove();
            } else if (remainder.isEmpty()) {
                entity.setItem(leaveStack);
            } else if (leaveStack.isEmpty()) {
                entity.setItem(remainder);
            } else {
                leaveStack.grow(remainder.getCount());
                entity.setItem(leaveStack);
            }
        }
        requiredQuantity -= storeStack.getCount();
        return requiredQuantity;
    }

    @LuaFunction
    public final MethodResult lookAtBlock() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();

        RayTraceResult result = FakePlayerProviderTurtle.withPlayer(turtle, turtleFakePlayer -> turtleFakePlayer.findHit(true, false));
        if (result.getType() == RayTraceResult.Type.MISS) {
            return MethodResult.of(null, "No block find");
        }
        BlockRayTraceResult blockHit = (BlockRayTraceResult) result;
        BlockState state = getWorld().getBlockState(blockHit.getBlockPos());
        Map<String, Object> data = new HashMap<>();
        data.put("name", state.getBlock().getRegistryName().toString());
        data.put("tags", state.getBlock().getTags());
        return MethodResult.of(data);
    }

    @LuaFunction
    public final MethodResult lookAtEntity() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();

        RayTraceResult result = FakePlayerProviderTurtle.withPlayer(turtle, turtleFakePlayer -> turtleFakePlayer.findHit(false, true));
        if (result.getType() == RayTraceResult.Type.MISS) {
            return MethodResult.of(null, "No entity find");
        }
        EntityRayTraceResult entityHit = (EntityRayTraceResult) result;
        Map<String, Object> data = new HashMap<>();
        Entity entity = entityHit.getEntity();
        data.put("entity_id", entity.getId());
        data.put("name", entity.getName().getString());
        data.put("tags", entity.getTags());
        return MethodResult.of(data);
    }

    @LuaFunction
    public final MethodResult digBlock() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();

        BlockPos blockPos = turtle.getPosition().relative(turtle.getDirection());

        Pair<Boolean, String> result = FakePlayerProviderTurtle.withPlayer(turtle, turtleFakePlayer -> turtleFakePlayer.digBlock(blockPos, turtle.getDirection().getOpposite()));
        if (!result.getFirst()) {
            return MethodResult.of(null, result.getSecond());
        }

        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult clickBlock() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();

        ActionResultType result = FakePlayerProviderTurtle.withPlayer(turtle, TurtleFakePlayer::clickBlock);

        return MethodResult.of(true, result.toString());
    }

    @LuaFunction
    public final MethodResult scanItems() {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        List<ItemEntity> items = getItems();
        Map<Integer, Map<String, Object>> data = new HashMap<>();
        int index = 1;
        for (ItemEntity item: items) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("entity_id", item.getId());
            itemData.put("name", item.getItem().getDisplayName().getString());
            itemData.put("technicalName", item.getItem().getItem().getRegistryName().toString());
            itemData.put("count", item.getItem().getCount());
            itemData.put("tags", item.getItem().getItem().getTags().stream().map(ResourceLocation::toString).collect(Collectors.toList()));
            data.put(index, itemData);
            index++;
        }
        return MethodResult.of(data);
    }

    @LuaFunction
    public final MethodResult suckSpecificItem(String technicalName, Optional<Integer> quantity){
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();

        int requiredQuantity = quantity.orElse(Integer.MAX_VALUE);

        List<ItemEntity> items = getItems();

        for (ItemEntity item: items) {
            if (item.getItem().getItem().getRegistryName().toString().equals(technicalName)) {
                requiredQuantity -= suckItem(item, requiredQuantity);
            }
        }

        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult suckItems(Optional<Integer> quantity) {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();

        int requiredQuantity = quantity.orElse(Integer.MAX_VALUE);

        if (requiredQuantity == 0) {
            return MethodResult.of(true);
        }

        List<ItemEntity> items = getItems();
        if (items.isEmpty()) {
            return MethodResult.of(null, "Nothing to take");
        }

        for (ItemEntity entity : items) {
            requiredQuantity -= suckItem(entity, requiredQuantity);
            if (requiredQuantity <= 0) {
                break;
            }
        }

        return MethodResult.of(true);
    }

}