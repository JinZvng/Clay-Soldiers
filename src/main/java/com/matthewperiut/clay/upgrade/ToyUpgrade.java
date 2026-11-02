package com.matthewperiut.clay.upgrade;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
/** Small material upgrades, saved and synchronized through the existing upgrade protocol. */
public class ToyUpgrade implements ISoldierUpgrade {
 public final String kind; private final Item material;
 public ToyUpgrade(String kind, Item material) { this.kind=kind; this.material=material; }
 public ItemStack getUpgradeItem(){return new ItemStack(material);}
 public Identifier getUpgradeIdentifier(){return Identifier.fromNamespaceAndPath("clay","upgrades/soldier/"+kind);}
 public boolean shouldSyncToClient(){return true;}
 public boolean canUpgrade(ItemStack stack,SoldierDollEntity s){
  if(!stack.is(material)||s.upgrades.contains(this))return false;
  // One weapon, one armor, one special effect. Shield is an off-hand accessory.
  if(kind.equals("bow")) return s.upgrades.stream().noneMatch(u->u instanceof com.matthewperiut.clay.upgrade.hand.SoldierStickUpgrade);
  if(kind.equals("copper")) return s.upgrades.stream().noneMatch(u->u instanceof com.matthewperiut.clay.upgrade.armor.SoldierLeatherUpgrade);
  if(kind.equals("shield"))return true;
  return s.upgrades.stream().noneMatch(u->u instanceof ToyUpgrade t && !t.kind.equals("bow")&&!t.kind.equals("copper")&&!t.kind.equals("shield"));
 }
}
