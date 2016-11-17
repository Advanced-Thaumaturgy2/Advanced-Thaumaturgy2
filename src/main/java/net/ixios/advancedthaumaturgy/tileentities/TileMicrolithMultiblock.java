package net.ixios.advancedthaumaturgy.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by katsw on 17/11/2016.
 */
public class TileMicrolithMultiblock extends TileEntity {

    private int crystalX;
    private int crystalY;
    private int crystalZ;

    public int getCrystalX() {
        return crystalX;
    }

    public void setCrystalX(int crystalX) {
        this.crystalX = crystalX;
    }

    public int getCrystalY() {
        return crystalY;
    }

    public void setCrystalY(int crystalY) {
        this.crystalY = crystalY;
    }

    public int getCrystalZ() {
        return crystalZ;
    }

    public void setCrystalZ(int crystalZ) {
        this.crystalZ = crystalZ;
    }

    @Override
    public void readFromNBT(NBTTagCompound p_145839_1_) {
        super.readFromNBT(p_145839_1_);
        readExtraNBT(p_145839_1_);
    }

    private void readExtraNBT(NBTTagCompound tagCompound) {
        crystalX=tagCompound.getInteger("crystalX");
        crystalY=tagCompound.getInteger("crystalY");
        crystalZ=tagCompound.getInteger("crystalZ");
    }

    @Override
    public void writeToNBT(NBTTagCompound p_145841_1_) {
        super.writeToNBT(p_145841_1_);
        writeExtraNBT(p_145841_1_);
    }

    private void writeExtraNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("crystalX",crystalX);
        tagCompound.setInteger("crystalY",crystalY);
        tagCompound.setInteger("crystalZ",crystalZ);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound cmp=new NBTTagCompound();
        writeExtraNBT(cmp);
        return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,1,cmp);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        readExtraNBT(pkt.func_148857_g());
    }
}
