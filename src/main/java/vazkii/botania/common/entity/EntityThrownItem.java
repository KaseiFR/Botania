/**
 * This class was created by <Flaxbeard>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [? (GMT)]
 */
package vazkii.botania.common.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.botania.common.core.helper.Vector3;
import vazkii.botania.common.lib.LibObfuscation;

public class EntityThrownItem extends EntityItem {

	public EntityThrownItem(World par1World) {
		super(par1World);
	}

	public EntityThrownItem(World p_i1710_1_, double p_i1710_2_,
			double p_i1710_4_, double p_i1710_6_, EntityItem item) {
		super(p_i1710_1_, p_i1710_2_, p_i1710_4_, p_i1710_6_, item.getEntityItem());

		ObfuscationReflectionHelper.setPrivateValue(
				EntityItem.class,
				this,
				ObfuscationReflectionHelper.getPrivateValue(EntityItem.class, item, LibObfuscation.PICKUP_DELAY),
				LibObfuscation.PICKUP_DELAY
		);
		motionX = item.motionX;
		motionY = item.motionY;
		motionZ = item.motionZ;
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		Vec3 vec3 = new Vec3(posX, posY, posZ);
		Vec3 vec31 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);

		MovingObjectPosition movingobjectposition = worldObj.rayTraceBlocks(vec3, vec31);


		if (!worldObj.isRemote)
		{
			Entity entity = null;
			List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().addCoord(motionX*2, motionY*2, motionZ*2).expand(2.0D, 2.0D, 2.0D));
			double d0 = 0.0D;

			for (int j = 0; j < list.size(); ++j)
			{
				Entity entity1 = (Entity)list.get(j);

				if (entity1.canBeCollidedWith() && (!(entity1 instanceof EntityPlayer) || ((Integer) ObfuscationReflectionHelper.getPrivateValue(EntityItem.class, this, LibObfuscation.PICKUP_DELAY)) == 0))
				{
					float f = 1.0F;
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f, f, f);
					MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31);

					if (movingobjectposition1 != null)
					{
						double d1 = vec3.distanceTo(movingobjectposition1.hitVec);

						if (d1 < d0 || d0 == 0.0D)
						{
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if (entity != null)
			{
				movingobjectposition = new MovingObjectPosition(entity);
			}
		}

		if (movingobjectposition != null)
		{
			if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && worldObj.getBlockState(movingobjectposition.getBlockPos()).getBlock() == Blocks.portal)
			{
				setPortal(movingobjectposition.getBlockPos());
			}
			else
			{
				if (movingobjectposition.entityHit != null) {
					movingobjectposition.entityHit.attackEntityFrom(DamageSource.magic, 2.0F);
					if (!worldObj.isRemote) {
						Entity item = getEntityItem().getItem().createEntity(worldObj, this, getEntityItem());
						if (item == null) {
							item = new EntityItem(worldObj, posX, posY, posZ, getEntityItem());
							worldObj.spawnEntityInWorld(item);
							item.motionX = motionX*0.25F;
							item.motionY = motionY*0.25F;
							item.motionZ = motionZ*0.25F;

						}
						else
						{
							item.motionX = motionX*0.25F;
							item.motionY = motionY*0.25F;
							item.motionZ = motionZ*0.25F;
						}
					}
					setDead();

				}
			}
		}

		Vector3 vec3m = new Vector3(motionX, motionY, motionZ);
		if (vec3m.mag() < 1.0F) {
			if (!worldObj.isRemote) {
				Entity item = getEntityItem().getItem().createEntity(worldObj, this, getEntityItem());
				if (item == null) {
					item = new EntityItem(worldObj, posX, posY, posZ, getEntityItem());
					worldObj.spawnEntityInWorld(item);
					item.motionX = motionX;
					item.motionY = motionY;
					item.motionZ = motionZ;
				}
				else
				{
					item.motionX = motionX;
					item.motionY = motionY;
					item.motionZ = motionZ;
				}
			}
			setDead();
		}
	}
}
