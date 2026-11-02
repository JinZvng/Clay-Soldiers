package com.matthewperiut.clay.entity.ai.goal;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
public class CommandGoal extends Goal {
 private final SoldierDollEntity soldier;
 public CommandGoal(SoldierDollEntity s){soldier=s;setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK,Flag.TARGET));}
 public boolean canUse(){return soldier.orderMode!=3;}
 public boolean canContinueToUse(){return canUse();}
 public boolean requiresUpdateEveryTick(){return true;}
 public void tick(){soldier.tickCommand();}
 public void stop(){soldier.getNavigation().stop();}
}
