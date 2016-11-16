package net.ixios.advancedthaumaturgy.compat.computercraft;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.ixios.advancedthaumaturgy.tileentities.TileWatchfulMicrolith;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by katsw on 15/11/2016.
 */
public class ComputerCraft {


    public static void InitCC()
    {
        ComputerCraftAPI.registerPeripheralProvider(new IPeripheralProvider() {
            @Override
            public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
                TileEntity te=world.getTileEntity(x,y,z);
                if(te!=null && te instanceof TileWatchfulMicrolith)
                {
                    return (IPeripheral)te;
                }
                return null;
            }
        });
    }
}
