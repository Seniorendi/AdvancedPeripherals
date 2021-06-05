package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.machine.TileEntityDigitalMiner;

import mekanism.api.NBTConstants;
import net.minecraft.nbt.CompoundNBT;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketGuiInteract;
import mekanism.common.network.PacketGuiInteract.GuiInteraction;
import java.util.List;

public class DigitalMinerIntegration extends ProxyIntegration<TileEntityDigitalMiner> {

    @Override
    protected Class<TileEntityDigitalMiner> getTargetClass() {
        return TileEntityDigitalMiner.class;
    }

    @Override
    public DigitalMinerIntegration getNewInstance() {
        return new DigitalMinerIntegration();
    }

    @Override
    protected String getName() {
        return "digitalMiner";
    }

    @LuaFunction
    public final int getToMine() {
        return getTileEntity().cachedToMine;
    }

    @LuaFunction
    public final boolean isRunning() {
        return getTileEntity().running;
    }

    @LuaFunction
    public final boolean getAutoEject() {
        return getTileEntity().inverse;
    }

    @LuaFunction
    public final void setAutoEject(boolean eject) {
        if(!(eject == getTileEntity().doEject))
            getTileEntity().toggleAutoEject();
    }

    @LuaFunction
    public final boolean getAutoPull() {
        return getTileEntity().doPull;
    }

    @LuaFunction
    public final void setAutoPull(boolean pull) {
        if(!(pull == getTileEntity().doPull))
            getTileEntity().toggleAutoPull();
    }

    @LuaFunction
    public final boolean getSilkTouch() {
        return getTileEntity().getSilkTouch();
    }

    @LuaFunction
    public final void setSilkTouch(boolean silk) {
        if(!(silk == getTileEntity().getSilkTouch()))
            getTileEntity().toggleSilkTouch();
    }

    @LuaFunction
    public final void start(){
        TileEntityDigitalMiner tile = ((TileEntityDigitalMiner) getTileEntity());
        // just calling tile.start() causes the miner searcher thread to don´t find any blocks to mine
        // doing it like that ensures that it´s called as the client would do it (and works)
        Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteraction.START_BUTTON, tile));
    }

    @LuaFunction
    public final void stop(){
        TileEntityDigitalMiner tile = ((TileEntityDigitalMiner) getTileEntity());
        Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteraction.STOP_BUTTON, tile));
    }
    
    @LuaFunction
    public final void reset(){
        TileEntityDigitalMiner tile = ((TileEntityDigitalMiner) getTileEntity());
        Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteraction.RESET_BUTTON, tile));
    }

    @LuaFunction
    public final int getRadius() {
        return getTileEntity().getRadius();
    }

    @LuaFunction
    public final void setRadius(int radius) {
        getTileEntity().setRadiusFromPacket(radius);
    }

    @LuaFunction
    public final int getMinY() {
        return getTileEntity().getMinY();
    }

    @LuaFunction
    public final void setMinY(int y) {
        getTileEntity().setMinYFromPacket(y);
    }

    @LuaFunction
    public final int getMaxY() {
        return getTileEntity().getMaxY();
    }

    @LuaFunction
    public final void setMaxY(int y) {
        getTileEntity().setMaxYFromPacket(y);
    }

    @LuaFunction
    public final boolean getInverseMode() {
        return getTileEntity().inverse;
    }

    @LuaFunction
    public final void setInverseMode(boolean enabled) {
        if(!(enabled == getTileEntity().inverse))
            getTileEntity().toggleInverse();
    }

    @LuaFunction
    public final int getDelay() {
        return getTileEntity().getDelay();
    }

    @LuaFunction
    public final boolean getIsFilterEmpty(){
        return getTileEntity().getFilters().isEmpty();
    }
    @LuaFunction
    public final double getTotalEnergyFilledPercentage() {
        FloatingLong stored = FloatingLong.ZERO;
        FloatingLong max = FloatingLong.ZERO;
        List<IEnergyContainer> energyContainers = getTileEntity().getEnergyContainers(null);
        for (IEnergyContainer energyContainer : energyContainers) {
            stored = stored.plusEqual(energyContainer.getEnergy());
            max = max.plusEqual(energyContainer.getMaxEnergy());
        }
        return stored.divideToLevel(max);
    }

}
