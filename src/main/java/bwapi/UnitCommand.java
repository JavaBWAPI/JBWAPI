package bwapi;


import java.util.Objects;

import static bwapi.TechType.*;
import static bwapi.UnitCommandType.*;

public final class UnitCommand {
    Unit unit;
    UnitCommandType type;
    Unit target = null;
    int x = Position.None.x;
    int y = Position.None.y;
    int extra = 0;

    private UnitCommand(final Unit unit, final UnitCommandType type) {
        this.unit = unit;
        this.type = type;
    }

    private <T extends Point<T>> void assignTarget(Point<T> target) {
        this.x = target.x;
        this.y = target.y;
    }

    public static UnitCommand attack(final Unit unit, final Position target) {
        return attack(unit, target, false);
    }

    public static UnitCommand attack(final Unit unit, final Unit target) {
        return attack(unit, target, false);
    }

    public static UnitCommand attack(final Unit unit, final Position target, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Attack_Move);
        c.assignTarget(target);
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand attack(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        final UnitCommand c =  new UnitCommand(unit, Attack_Unit);
        c.target = target;
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand build(final Unit unit, final TilePosition target, final UnitType type) {
        final UnitCommand c = new UnitCommand(unit, Build);
        c.assignTarget(target);
        c.extra = type.id;
        return c;
    }

    public static UnitCommand buildAddon(final Unit unit, final UnitType type) {
        final UnitCommand c = new UnitCommand(unit, Build_Addon);
        c.extra = type.id;
        return c;
    }

    public static UnitCommand train(final Unit unit, final UnitType type) {
        final UnitCommand c = new UnitCommand(unit, Train);
        c.extra = type.id;
        return c;
    }

    public static UnitCommand morph(final Unit unit, final UnitType type) {
        final UnitCommand c = new UnitCommand(unit, Morph);
        c.extra = type.id;
        return c;
    }

    public static UnitCommand research(final Unit unit, final TechType tech) {
        final UnitCommand c = new UnitCommand(unit, Research);
        c.extra = tech.id;
        return c;
    }

    public static UnitCommand upgrade(final Unit unit, final UpgradeType upgrade) {
        final UnitCommand c = new UnitCommand(unit, Upgrade);
        c.extra = upgrade.id;
        return c;
    }

    public static UnitCommand setRallyPoint(final Unit unit, final Position target) {
        final UnitCommand c = new UnitCommand(unit, Set_Rally_Position);
        c.assignTarget(target);
        return c;
    }

    public static UnitCommand setRallyPoint(final Unit unit, final Unit target) {
        final UnitCommand c = new UnitCommand(unit, Set_Rally_Unit);
        c.target = target;
        return c;
    }

    public static UnitCommand move(final Unit unit, final Position target) {
        return move(unit, target, false);
    }

    public static UnitCommand move(final Unit unit, final Position target, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Move);
        c.assignTarget(target);
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand patrol(final Unit unit, final Position target) {
        return patrol(unit, target, false);
    }

    public static UnitCommand patrol(final Unit unit, final Position target, final boolean shiftQueueCommand) {
        final UnitCommand c =  new UnitCommand(unit, Patrol);
        c.assignTarget(target);
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand holdPosition(final Unit unit) {
        return holdPosition(unit, false);
    }

    public static UnitCommand holdPosition(final Unit unit, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Hold_Position);
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand stop(final Unit unit) {
        return stop(unit, false);
    }

    public static UnitCommand stop(final Unit unit, final boolean shiftQueueCommand) {
        final UnitCommand c =  new UnitCommand(unit, Stop);
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand follow(final Unit unit, final Unit target) {
        return follow(unit, target, false);
    }

    public static UnitCommand follow(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Follow);
        c.target = target;
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand gather(final Unit unit, final Unit target) {
        return gather(unit, target, false);
    }

    public static UnitCommand gather(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Gather);
        c.target = target;
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand returnCargo(final Unit unit) {
        return returnCargo(unit, false);
    }

    public static UnitCommand returnCargo(final Unit unit, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Return_Cargo);
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand repair(final Unit unit, final Unit target) {
        return repair(unit, target, false);
    }

    public static UnitCommand repair(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Repair);
        c.target = target;
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand burrow(final Unit unit) {
        return new UnitCommand(unit, Burrow);
    }

    public static UnitCommand unburrow(final Unit unit) {
        return new UnitCommand(unit, Unburrow);
    }

    public static UnitCommand cloak(final Unit unit) {
        return new UnitCommand(unit, Cloak);
    }

    public static UnitCommand decloak(final Unit unit) {
        return new UnitCommand(unit, Decloak);
    }

    public static UnitCommand siege(final Unit unit) {
        return new UnitCommand(unit, Siege);
    }

    public static UnitCommand unsiege(final Unit unit) {
        return new UnitCommand(unit, Unsiege);
    }

    public static UnitCommand lift(final Unit unit) {
        return new UnitCommand(unit, Lift);
    }

    public static UnitCommand land(final Unit unit, final TilePosition target) {
        final UnitCommand c = new UnitCommand(unit, Land);
        c.assignTarget(target);
        return c;
    }

    public static UnitCommand load(final Unit unit, final Unit target) {
        return load(unit, target, false);
    }

    public static UnitCommand load(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Load);
        c.target = target;
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand unload(final Unit unit, final Unit target) {
        final UnitCommand c = new UnitCommand(unit, Unload);
        c.target = target;
        return c;
    }

    public static UnitCommand unloadAll(final Unit unit) {
        return unloadAll(unit, false);
    }

    public static UnitCommand unloadAll(final Unit unit, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Unload_All);
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand unloadAll(final Unit unit, final Position target) {
        return unloadAll(unit, target, false);
    }

    public static UnitCommand unloadAll(final Unit unit, final Position target, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Unload_All_Position);
        c.assignTarget(target);
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand rightClick(final Unit unit, final Position target) {
        return rightClick(unit, target, false);
    }

    public static UnitCommand rightClick(final Unit unit, final Unit target) {
        return rightClick(unit, target, false);
    }

    public static UnitCommand rightClick(final Unit unit, final Position target, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Right_Click_Position);
        c.assignTarget(target);
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand rightClick(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        final UnitCommand c = new UnitCommand(unit, Right_Click_Unit);
        c.target = target;
        c.extra = shiftQueueCommand ? 1 : 0;
        return c;
    }

    public static UnitCommand haltConstruction(final Unit unit) {
        return new UnitCommand(unit, Halt_Construction);
    }

    public static UnitCommand cancelConstruction(final Unit unit) {
        return new UnitCommand(unit, Cancel_Construction);
    }

    public static UnitCommand cancelAddon(final Unit unit) {
        return new UnitCommand(unit, Cancel_Addon);
    }

    public static UnitCommand cancelTrain(final Unit unit) {
        return cancelTrain(unit, -2);
    }

    public static UnitCommand cancelTrain(final Unit unit, final int slot) {
        final UnitCommand c = new UnitCommand(unit, slot >= 0 ? Cancel_Train_Slot : Cancel_Train);
        c.extra = slot;
        return c;
    }

    public static UnitCommand cancelMorph(final Unit unit) {
        return new UnitCommand(unit, Cancel_Morph);
    }

    public static UnitCommand cancelResearch(final Unit unit) {
        return new UnitCommand(unit, Cancel_Research);
    }

    public static UnitCommand cancelUpgrade(final Unit unit) {
        return new UnitCommand(unit, Cancel_Upgrade);
    }

    public static UnitCommand useTech(final Unit unit, final TechType tech) {
        final UnitCommand c = new UnitCommand(unit, Use_Tech);
        c.extra = tech.id;
        if (tech == Burrowing ) {
            c.type = unit.isBurrowed() ? UnitCommandType.Unburrow : UnitCommandType.Burrow;
        }
        else if (tech == Cloaking_Field || tech == Personnel_Cloaking) {
            c.type = unit.isCloaked() ? Decloak : Cloak;
        }
        else if (tech == Tank_Siege_Mode ) {
            c.type = unit.isSieged() ? Unsiege : Siege;
        }
        return c;
    }

    public static UnitCommand useTech(final Unit unit, final TechType tech, final Position target) {
        final UnitCommand c = new UnitCommand(unit, Use_Tech_Position);
        c.assignTarget(target);
        c.extra = tech.id;
        return c;
    }

    public static UnitCommand useTech(final Unit unit, final TechType tech, final Unit target) {
        final UnitCommand c = new UnitCommand(unit, Use_Tech_Unit);
        c.target = target;
        c.extra = tech.id;
        return c;
    }

    public static UnitCommand placeCOP(final Unit unit, final TilePosition target) {
        final UnitCommand c = new UnitCommand(unit, Place_COP);
        c.assignTarget(target);
        return c;
    }

    public Unit getUnit() {
        return unit;
    }

    public UnitCommandType getType() {
        return type;
    }

    public Unit getTarget() {
        return target;
    }

    public int getSlot() {
        return type == Cancel_Train_Slot ? extra : -1;
    }

    public Position getTargetPosition() {
        if (type == Build || type == Land || type == Place_COP) {
            return new TilePosition(x, y).toPosition();
        }
        return new Position(x, y);
    }

    public TilePosition getTargetTilePosition() {
        if (type == Build || type == Land || type == Place_COP) {
            return new TilePosition(x, y);
        }
        return new Position(x, y).toTilePosition();
    }

    public UnitType getUnitType() {
        if (type == Build || type == Build_Addon || type == Train || type == Morph) {
            return UnitType.idToEnum[extra];
        }
        return UnitType.None;
    }

    public TechType getTechType() {
        if (type == Research || type == Use_Tech || type == Use_Tech_Position || type == Use_Tech_Unit) {
            return TechType.idToEnum[extra];
        }
        return TechType.None;
    }

    public UpgradeType getUpgradeType() {
        return type == UnitCommandType.Upgrade ? UpgradeType.idToEnum[extra] : UpgradeType.None;
    }

    public boolean isQueued() {
        return (type == Attack_Move ||
                type == Attack_Unit ||
                type == Move ||
                type == Patrol ||
                type == Hold_Position ||
                type == Stop ||
                type == Follow ||
                type == Gather ||
                type == Return_Cargo ||
                type == Load ||
                type == Unload_All ||
                type == Unload_All_Position ||
                type == Right_Click_Position ||
                type == Right_Click_Unit)
                && extra != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitCommand that = (UnitCommand) o;
        return x == that.x &&
                y == that.y &&
                extra == that.extra &&
                type == that.type &&
                Objects.equals(target, that.target) &&
                Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target, x, y, extra, unit);
    }

    @Override
    public String toString() {
        return "UnitCommand{" +
                "unit=" + unit +
                ", type=" + type +
                ", target=" + target +
                ", x=" + x +
                ", y=" + y +
                ", extra=" + extra +
                '}';
    }
}
