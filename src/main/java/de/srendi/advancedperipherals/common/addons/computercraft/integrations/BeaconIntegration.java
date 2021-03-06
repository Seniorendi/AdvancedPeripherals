package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import net.minecraft.tileentity.BeaconTileEntity;

public class BeaconIntegration extends Integration<BeaconTileEntity> {
    @Override
    public Class<BeaconTileEntity> getTargetClass() {
        return BeaconTileEntity.class;
    }

    @Override
    public BeaconIntegration getNewInstance() {
        return new BeaconIntegration();
    }

    @Override
    public String getType() {
        return "beacon";
    }

    @LuaFunction
    public final int getLevel() {
        return getTileEntity().getLevels();
    }

    @LuaFunction
    public final String getPrimaryEffect() {
        return getTileEntity().primaryPower == null ? "none" : getTileEntity().primaryPower.getDescriptionId();
    }

    @LuaFunction
    public final String getSecondaryEffect() {
        return getTileEntity().secondaryPower == null ? "none" : getTileEntity().secondaryPower.getDescriptionId();
    }

}
