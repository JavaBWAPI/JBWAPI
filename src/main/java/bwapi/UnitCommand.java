package bwapi;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static bwapi.UnitCommandType.*;

public class UnitCommand {
    private static Set<UnitCommandType> posComs = new HashSet<>(
            Arrays.asList(Build, Land, Place_COP)
    );
    final UnitCommandType type;
    final Unit target;
    final int x;
    final int y;
    final int extra;
    Unit unit;

    private UnitCommand(final Unit unit, final UnitCommandType type, final Unit target, final int x, final int y, final int extra) {
        this.unit = unit;
        this.type = type;
        this.target = target;
        this.x = x;
        this.y = y;
        this.extra = extra;
    }

    public static UnitCommand attack(final Unit unit, final Position target) {

        return attack(unit, target, false);
    }

    public static UnitCommand attack(final Unit unit, final Unit target) {
        return attack(unit, target, false);
    }

    public static UnitCommand attack(final Unit unit, final Position target, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Attack_Move, null, target.x, target.y, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand attack(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Attack_Unit, target, -1, -1, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand build(final Unit unit, final TilePosition target, final UnitType type) {
        return new UnitCommand(unit, Build, null, target.x, target.y, type.id);
    }

    public static UnitCommand buildAddon(final Unit unit, final UnitType type) {
        return new UnitCommand(unit, Build_Addon, null, -1, -1, type.id);
    }

    public static UnitCommand train(final Unit unit, final UnitType type) {
        return new UnitCommand(unit, Train, null, -1, -1, type.id);
    }

    public static UnitCommand morph(final Unit unit, final UnitType type) {
        return new UnitCommand(unit, Morph, null, -1, -1, type.id);
    }

    public static UnitCommand research(final Unit unit, final TechType tech) {
        return new UnitCommand(unit, Research, null, -1, -1, tech.id);
    }

    public static UnitCommand upgrade(final Unit unit, final UpgradeType upgrade) {
        return new UnitCommand(unit, Research, null, -1, -1, upgrade.id);
    }

    public static UnitCommand setRallyPoint(final Unit unit, final Position target) {
        return new UnitCommand(unit, Set_Rally_Position, null, target.x, target.y, 0);
    }

    public static UnitCommand setRallyPoint(final Unit unit, final Unit target) {
        return new UnitCommand(unit, Set_Rally_Unit, target, -1, -1, 0);
    }

    public static UnitCommand move(final Unit unit, final Position target) {
        return move(unit, target, false);
    }

    public static UnitCommand move(final Unit unit, final Position target, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Move, null, target.x, target.y, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand patrol(final Unit unit, final Position target) {
        return patrol(unit, target, false);
    }

    public static UnitCommand patrol(final Unit unit, final Position target, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Patrol, null, target.x, target.y, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand holdPosition(final Unit unit) {
        return holdPosition(unit, false);
    }

    public static UnitCommand holdPosition(final Unit unit, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Hold_Position, null, -1, -1, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand stop(final Unit unit) {
        return stop(unit, false);
    }

    public static UnitCommand stop(final Unit unit, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Stop, null, -1, -1, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand follow(final Unit unit, final Unit target) {
        return follow(unit, target, false);
    }

    public static UnitCommand follow(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Follow, target, -1, -1, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand gather(final Unit unit, final Unit target) {
        return gather(unit, target, false);
    }

    public static UnitCommand gather(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Gather, target, -1, -1, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand returnCargo(final Unit unit) {
        return returnCargo(unit, false);
    }

    public static UnitCommand returnCargo(final Unit unit, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Return_Cargo, null, -1, -1, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand repair(final Unit unit, final Unit target) {
        return repair(unit, target, false);
    }

    public static UnitCommand repair(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Repair, target, -1, -1, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand burrow(final Unit unit) {
        return new UnitCommand(unit, Burrow, null, -1, -1, 0);
    }

    public static UnitCommand unburrow(final Unit unit) {
        return new UnitCommand(unit, Unburrow, null, -1, -1, 0);
    }

    public static UnitCommand cloak(final Unit unit) {
        return new UnitCommand(unit, Cloak, null, -1, -1, 0);
    }

    public static UnitCommand decloak(final Unit unit) {
        return new UnitCommand(unit, Decloak, null, -1, -1, 0);
    }

    public static UnitCommand siege(final Unit unit) {
        return new UnitCommand(unit, Siege, null, -1, -1, 0);
    }

    public static UnitCommand unsiege(final Unit unit) {
        return new UnitCommand(unit, Unsiege, null, -1, -1, 0);
    }

    public static UnitCommand lift(final Unit unit) {
        return new UnitCommand(unit, Lift, null, -1, -1, 0);
    }

    public static UnitCommand land(final Unit unit, final TilePosition target) {
        return new UnitCommand(unit, Land, null, target.x, target.y, 0);
    }

    public static UnitCommand load(final Unit unit, final Unit target) {
        return load(unit, target, false);
    }

    public static UnitCommand load(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Load, target, -1, -1, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand unload(final Unit unit, final Unit target) {
        return new UnitCommand(unit, Unload, target, -1, -1, 0);
    }

    public static UnitCommand unloadAll(final Unit unit) {
        return unloadAll(unit, false);
    }

    public static UnitCommand unloadAll(final Unit unit, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Unload_All, null, -1, -1, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand unloadAll(final Unit unit, final Position target) {
        return unloadAll(unit, target, false);
    }

    public static UnitCommand unloadAll(final Unit unit, final Position target, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Unload_All_Position, null, target.x, target.y, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand rightClick(final Unit unit, final Position target) {
        return rightClick(unit, target, false);
    }

    public static UnitCommand rightClick(final Unit unit, final Unit target) {
        return rightClick(unit, target, false);
    }

    public static UnitCommand rightClick(final Unit unit, final Position target, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Right_Click_Position, null, target.x, target.y, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand rightClick(final Unit unit, final Unit target, final boolean shiftQueueCommand) {
        return new UnitCommand(unit, Right_Click_Unit, target, -1, -1, shiftQueueCommand ? 1 : 0);
    }

    public static UnitCommand haltConstruction(final Unit unit) {
        return new UnitCommand(unit, Halt_Construction, null, -1, -1, 0);
    }

    public static UnitCommand cancelConstruction(final Unit unit) {
        return new UnitCommand(unit, Cancel_Construction, null, -1, -1, 0);
    }

    public static UnitCommand cancelAddon(final Unit unit) {
        return new UnitCommand(unit, Cancel_Addon, null, -1, -1, 0);
    }

    public static UnitCommand cancelTrain(final Unit unit) {
        return new UnitCommand(unit, Cancel_Train, null, -1, -1, 0);
    }

    public static UnitCommand cancelTrain(final Unit unit, final int slot) {
        return new UnitCommand(unit, Cancel_Train_Slot, null, -1, -1, slot);
    }

    public static UnitCommand cancelMorph(final Unit unit) {
        return new UnitCommand(unit, Cancel_Morph, null, -1, -1, 0);
    }

    public static UnitCommand cancelResearch(final Unit unit) {
        return new UnitCommand(unit, Cancel_Research, null, -1, -1, 0);
    }

    public static UnitCommand cancelUpgrade(final Unit unit) {
        return new UnitCommand(unit, Cancel_Upgrade, null, -1, -1, 0);
    }

    public static UnitCommand useTech(final Unit unit, final TechType tech) {
        return new UnitCommand(unit, Use_Tech, null, -1, -1, 0);
    }

    public static UnitCommand useTech(final Unit unit, final TechType tech, final Position target) {
        return new UnitCommand(unit, Use_Tech_Position, null, target.x, target.y, tech.id);
    }

    public static UnitCommand useTech(final Unit unit, final TechType tech, final Unit target) {
        return new UnitCommand(unit, Use_Tech_Unit, target, -1, -1, tech.id);
    }

    public static UnitCommand placeCOP(final Unit unit, final TilePosition target) {
        return new UnitCommand(unit, Place_COP, null, target.x, target.y, 0);
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
        return posComs.contains(type) ? new Position(x * 32, y * 32) : new Position(x, y);
    }

    public TilePosition getTargetTilePosition() {
        return posComs.contains(type) ? new TilePosition(x, y) : new TilePosition(x / 32, y / 32);
    }

    public UnitType getUnitType() {
        if (type == Build || type == Build_Addon || type == Train || type == Morph) {
            return UnitType.unitTypes[extra];
        }
        return UnitType.None;
    }

    public TechType getTechType() {
        if (type == Research || type == Use_Tech || type == Use_Tech_Position || type == Use_Tech_Unit) {
            return TechType.techTypes[extra];
        }
        return TechType.None;
    }

    public UpgradeType getUpgradeType() {
        return type == UnitCommandType.Upgrade ? UpgradeType.upgradeTypes[extra] : UpgradeType.None;
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
    public boolean equals(final Object o) {
        if (!(o instanceof UnitCommand)) {
            return false;
        }

        final UnitCommand that = (UnitCommand) o;

        return extra == that.extra && x == that.x && y == that.y && target.equals(that.target) && unit.equals(that.unit)
                && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = unit.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + extra;
        return result;
    }

}