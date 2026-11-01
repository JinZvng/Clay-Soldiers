package com.matthewperiut.clay.entity.ai.goal;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;


import java.util.EnumSet;

import static java.lang.Math.sqrt;

public class MeleeAttackTinyGoal extends Goal
{
    protected final PathfinderMob mob;
    private final double speed;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private long lastUpdateTime;

    public MeleeAttackTinyGoal(PathfinderMob mob, double speed, boolean followingTargetEvenIfNotSeen)
    {
        this.mob = mob;
        this.speed = speed;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse()
    {
        long l = this.mob.level().getGameTime();
        if (l - this.lastUpdateTime < 20L)
        {
            return false;
        }
        else
        {
            this.lastUpdateTime = l;
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null)
            {
                return false;
            }
            else if (!livingEntity.isAlive())
            {
                return false;
            }
            else
            {
                this.path = this.mob.getNavigation().createPath(livingEntity, 0);
                if (this.path != null)
                {
                    return true;
                }
                else
                {
                    return this.getSquaredMaxAttackDistance(livingEntity) >= this.mob.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                }
            }
        }
    }

    public boolean canContinueToUse()
    {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null)
        {
            return false;
        }
        else if (!livingEntity.isAlive())
        {
            return false;
        }
        else if (!this.mob.isWithinHome(livingEntity.blockPosition()))
        {
            return false;
        }
        else
        {
            return !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player) livingEntity).isCreative();
        }
    }

    public void start()
    {
        this.mob.getNavigation().moveTo(this.path, this.speed);
        this.mob.setAggressive(true);
    }

    public void stop()
    {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity))
        {
            this.mob.setTarget(null);
        }

        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    public boolean requiresUpdateEveryTick()
    {
        return true;
    }

    private void pushTowardOther(Vec3 direction, double mod)
    {
        Entity mob = this.mob;
        if (mob.isPassenger()) mob = mob.getVehicle();
        double x = direction.x;
        double y = direction.y;
        double z = direction.z;
        double length = sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        mob.push((x / length) * mod, (y / length) * mod, (z / length) * mod);
    }

    public void tick()
    {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null)
        {
            this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
            double d = this.mob.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            if ((this.mob.getSensing().hasLineOfSight(livingEntity)))
            {
                this.mob.getNavigation().moveTo(livingEntity, this.speed);
                pushTowardOther(livingEntity.position().subtract(this.mob.position()), 0.0125);
            }

            this.attack(livingEntity, d);
        }
    }

    protected void attack(LivingEntity target, double hostileRadiusSqr)
    {
        double d = this.getSquaredMaxAttackDistance(target);
        if (hostileRadiusSqr <= d)
        {
            this.mob.swingForAttack(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget((net.minecraft.server.level.ServerLevel) this.mob.level(), target);
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity)
    {
        return 1.0;
    }
}
