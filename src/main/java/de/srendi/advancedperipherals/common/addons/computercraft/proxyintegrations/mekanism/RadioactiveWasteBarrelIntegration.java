package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.common.tile.TileEntityRadioactiveWasteBarrel;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import java.util.List;
import java.util.function.Function;

public class RadioactiveWasteBarrelIntegration extends ProxyIntegration<TileEntityRadioactiveWasteBarrel> {
    @Override
    protected Class<TileEntityRadioactiveWasteBarrel> getTargetClass() {
        return TileEntityRadioactiveWasteBarrel.class;
    }

    @Override
    public RadioactiveWasteBarrelIntegration getNewInstance() {
        return new RadioactiveWasteBarrelIntegration();
    }

    @Override
    protected String getName() {
        return "radioactiveWasteBarrel";
    }

    @LuaFunction
    public final float getStored() {
        return getTileEntity().getGasTank().getStored();
    }

    @LuaFunction
    public final float getCapacity() {
        return getTileEntity().getGasTank().getCapacity();
    }

    @LuaFunction
    public final float getFilledPercentage() {
        return getTileEntity().getGasScale();
    }

}
