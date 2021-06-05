package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.BeaconIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.botania.ManaFlowerIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.botania.ManaPoolIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.botania.SpreaderIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.immersiveengineering.CapacitorIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.immersiveengineering.RedstoneConnectorIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.immersiveengineering.RedstoneProbeIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.mekanism.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class ProxyIntegrationRegistry {

    public static final List<ProxyIntegration<?>> integrations = new ArrayList<>();

    public static void registerIntegration(ProxyIntegration<?> integration) {
        integrations.add(integration);
    }

    public static void register() {
        //It's vanilla, so the condition is always true
        registerIntegration(new BeaconIntegration());
        if (ModList.get().isLoaded("mekanismgenerators")) {
            registerIntegration(new FissionIntegration());
            registerIntegration(new FusionIntegration());
            registerIntegration(new TurbineIntegration());
        }
        if (ModList.get().isLoaded("mekanism")) {
            registerIntegration(new InductionPortIntegration());
            registerIntegration(new BoilerIntegration());
            registerIntegration(new DigitalMinerIntegration());
            registerIntegration(new RadioactiveWasteBarrelIntegration());
            registerIntegration(new GenericMekanismIntegration());
        }
        if (ModList.get().isLoaded("botania")) {
            registerIntegration(new ManaPoolIntegration());
            registerIntegration(new SpreaderIntegration());
            registerIntegration(new ManaFlowerIntegration());
        }
        if (ModList.get().isLoaded("immersiveengineering")) {
            registerIntegration(new RedstoneProbeIntegration());
            registerIntegration(new RedstoneConnectorIntegration());
            registerIntegration(new CapacitorIntegration());
        }
    }

    public static ProxyIntegration<?> getIntegration(TileEntity tileEntity) {
        for (ProxyIntegration<?> integration : integrations) {
            if (integration.isTileEntity(tileEntity)) {
                return integration.getNewInstance();
            }
        }
        return null;
    }

}
