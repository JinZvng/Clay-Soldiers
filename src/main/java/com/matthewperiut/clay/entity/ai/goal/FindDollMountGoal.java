package com.matthewperiut.clay.entity.ai.goal;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;

import java.util.EnumSet;
import java.util.List;

public class FindDollMountGoal extends Goal
{
    protected final SoldierDollEntity mob;
    private final double speed;
    private Path path;
    private HorseDollEntity target;

    public FindDollMountGoal(SoldierDollEntity mob, double speed)
    {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.mob = mob;
        this.speed = speed;
    }

    public HorseDollEntity getGoal()
    {
        Level world = this.mob.level();

        AABB box = new AABB(this.mob.position().subtract(8, 4, 8), this.mob.position().add(8, 4, 8));

        List<HorseDollEntity> horses = world.getEntitiesOfClass(HorseDollEntity.class, box, (horse) -> !horse.isVehicle());


        // closest
        double distance = 1000;
        HorseDollEntity chosen = null;
        for (HorseDollEntity horse : horses)
        {
            if (this.mob.getRandom().nextFloat() < 0.33F)
            {
                return horse;
            }
            double d = this.mob.distanceToSqr(horse.getX(), horse.getY(), horse.getZ());
            if (d < distance)
            {
                chosen = horse;
            }
        }

        return chosen;
    }

    private long lastUpdateTime;

    @Override
    public boolean canUse()
    {
        if (this.mob.isPassenger())
        {
            return false;
        }

        long currentWorldTime = this.mob.level().getGameTime();
        if (currentWorldTime - this.lastUpdateTime < 20L)
        {
            return false;
        }
        else
        {
            this.lastUpdateTime = currentWorldTime;
            this.target = getGoal();
            if (this.target == null)
            {
                return false;
            } else if (!this.target.isAlive())
            {
                return false;
            }
            else
            {
                this.path = this.mob.getNavigation().createPath(this.target, 0);
                if (this.path != null)
                {
                    return true;
                }
                else
                {
                    return this.getSquaredMaxRideDistance(this.target) >= this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                }
            }
        }
    }

    private int cooldown;

    @Override
    public void start()
    {
        this.mob.getNavigation().moveTo(this.path, this.speed);
        this.cooldown = 0;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (this.target == null)
        {
            return false;
        } else if (!this.target.isAlive())
        {
            return false;
        } else if (this.target.isVehicle())
        {
            return false;
        } else return this.mob.isWithinHome(this.target.blockPosition());
    }

    @Override
    public void stop()
    {
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(this.target))
        {
            this.mob.setTarget(null);
        }
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick()
    {
        return true;
    }

    public void tick()
    {
        HorseDollEntity goalMob = this.target;
        if (goalMob != null)
        {
            this.mob.getLookControl().setLookAt(goalMob, 30.0F, 30.0F);
            double d = this.mob.distanceToSqr(goalMob.getX(), goalMob.getY(), goalMob.getZ());

            if (this.mob.getRandom().nextFloat() < 0.25F)
            {
                this.mob.getNavigation().moveTo(goalMob, this.speed);
            }

            if (this.mob.getRandom().nextFloat() < 0.05f)
            {
                this.target = getGoal();
                return;
            }

            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.ride(goalMob, d);
        }
    }

    protected void ride(HorseDollEntity target, double hostileRadiusSqr)
    {
        double d = this.getSquaredMaxRideDistance(target);
        if (hostileRadiusSqr <= d && this.cooldown <= 0)
        {
            if (canContinueToUse())
            {
                if (target.tickCount > 40) this.mob.startRiding(target);
            }
        }
    }

    protected double getSquaredMaxRideDistance(LivingEntity entity)
    {
        return 2.0;
    }

    protected void resetCooldown()
    {
        this.cooldown = this.adjustedTickDelay(20);
    }

    protected boolean isTimeToAttack()
    {
        return this.cooldown <= 0;
    }

    protected int getCooldown()
    {
        return this.cooldown;
    }

    protected int getAttackInterval()
    {
        return this.adjustedTickDelay(20);
    }
}