package bwapi;


import bwapi.point.Position;
import bwapi.point.TilePosition;
import bwapi.types.TechType;
import bwapi.types.UnitCommandType;
import bwapi.types.UnitType;
import bwapi.types.UpgradeType;

import static bwapi.types.UnitCommandType.*;

import java.util.*;

public class UnitCommand {
    private Unit unit;
    private UnitCommandType unitCommandType;
    private Unit target;
    private int x, y;
    private int extra;

    private UnitCommand(Unit unit, UnitCommandType unitCommandType, Unit target, int x, int y, int extra) {
        Objects.requireNonNull(unit);
        Objects.requireNonNull(target);
        this.unit = unit;
        this.unitCommandType = unitCommandType;
        this.target = target;
        this.x = x;
        this.y = y;
        this.extra = extra;
    }

    public Unit getUnit() {
        return unit;
    }

    public UnitCommandType getUnitCommandType() {
        return unitCommandType;
    }

    public Unit getTarget() {
        return target;
    }

    public int getSlot() {
        return unitCommandType == UnitCommandType.None ? extra : -1;
    }

    private static Set<UnitCommandType> posComs = new TreeSet<>(
            Arrays.asList(Build, Land, Place_COP)
    );

    public Position getTargetPosition() {
        return posComs.contains(unitCommandType) ? new Position(x * 32, y * 32): new Position(x, y);
    }

    public TilePosition getTargetTilePosition() {
        return posComs.contains(unitCommandType) ? new TilePosition(x, y) : new TilePosition(x / 32, y / 32);
    }

    private static Set<UnitCommandType> queuables = new TreeSet<>(
            Arrays.asList(Attack_Move, Attack_Unit, Move, Patrol, Hold_Position, Stop, Follow, Gather, Return_Cargo,
                    Repair, Load, Unload_All, Unload_All_Position, Right_Click_Position, Right_Click_Unit)
    );

    public boolean isQueued() {
        return queuables.contains(unitCommandType) && extra != 0;

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UnitCommand)) return false;

        UnitCommand that = (UnitCommand) o;

        return extra == that.extra && x == that.x && y == that.y && target.equals(that.target) && unit.equals(that.unit)
                && unitCommandType == that.unitCommandType;
    }

    @Override
    public int hashCode() {
        int result = unit != null ? unit.hashCode() : 0;
        result = 31 * result + unitCommandType.hashCode();
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + extra;
        return result;
    }


    public static UnitCommand attack(Unit unit, Position target) {
        return null;
    }

    public static UnitCommand attack(Unit unit, Unit target) {
        return null;
    }

    public static UnitCommand attack(Unit unit, Position target, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand attack(Unit unit, Unit target, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand build(Unit unit, TilePosition target, UnitType type) {
        return null;
    }

    public static UnitCommand buildAddon(Unit unit, UnitType type) {
        return null;
    }

    public static UnitCommand train(Unit unit, UnitType type) {
        return null;
    }

    public static UnitCommand morph(Unit unit, UnitType type) {
        return null;
    }

    public static UnitCommand research(Unit unit, TechType tech) {
        return null;
    }

    public static UnitCommand upgrade(Unit unit, UpgradeType upgrade) {
        return null;
    }

    public static UnitCommand setRallyPoint(Unit unit, Position target) {
        return null;
    }

    public static UnitCommand setRallyPoint(Unit unit, Unit target) {
        return null;
    }

    public static UnitCommand move(Unit unit, Position target) {
        return null;
    }

    public static UnitCommand move(Unit unit, Position target, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand patrol(Unit unit, Position target) {
        return null;
    }

    public static UnitCommand patrol(Unit unit, Position target, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand holdPosition(Unit unit) {
        return null;
    }

    public static UnitCommand holdPosition(Unit unit, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand stop(Unit unit) {
        return null;
    }

    public static UnitCommand stop(Unit unit, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand follow(Unit unit, Unit target) {
        return null;
    }

    public static UnitCommand follow(Unit unit, Unit target, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand gather(Unit unit, Unit target) {
        return null;
    }

    public static UnitCommand gather(Unit unit, Unit target, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand returnCargo(Unit unit) {
        return null;
    }

    public static UnitCommand returnCargo(Unit unit, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand repair(Unit unit, Unit target) {
        return null;
    }

    public static UnitCommand repair(Unit unit, Unit target, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand burrow(Unit unit) {
        return null;
    }

    public static UnitCommand unburrow(Unit unit) {
        return null;
    }

    public static UnitCommand cloak(Unit unit) {
        return null;
    }

    public static UnitCommand decloak(Unit unit) {
        return null;
    }

    public static UnitCommand siege(Unit unit) {
        return null;
    }

    public static UnitCommand unsiege(Unit unit) {
        return null;
    }

    public static UnitCommand lift(Unit unit) {
        return null;
    }

    public static UnitCommand land(Unit unit, TilePosition target) {
        return null;
    }

    public static UnitCommand load(Unit unit, Unit target) {
        return null;
    }

    public static UnitCommand load(Unit unit, Unit target, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand unload(Unit unit, Unit target) {
        return null;
    }

    public static UnitCommand unloadAll(Unit unit) {
        return null;
    }

    public static UnitCommand unloadAll(Unit unit, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand unloadAll(Unit unit, Position target) {
        return null;
    }

    public static UnitCommand unloadAll(Unit unit, Position target, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand rightClick(Unit unit, Position target) {
        return null;
    }

    public static UnitCommand rightClick(Unit unit, Unit target) {
        return null;
    }

    public static UnitCommand rightClick(Unit unit, Position target, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand rightClick(Unit unit, Unit target, boolean shiftQueueCommand) {
        return null;
    }

    public static UnitCommand haltConstruction(Unit unit) {
        return null;
    }

    public static UnitCommand cancelConstruction(Unit unit) {
        return null;
    }

    public static UnitCommand cancelAddon(Unit unit) {
        return null;
    }

    public static UnitCommand cancelTrain(Unit unit) {
        return null;
    }

    public static UnitCommand cancelTrain(Unit unit, int slot) {
        return null;
    }

    public static UnitCommand cancelMorph(Unit unit) {
        return null;
    }

    public static UnitCommand cancelResearch(Unit unit) {
        return null;
    }

    public static UnitCommand cancelUpgrade(Unit unit) {
        return null;
    }

    public static UnitCommand useTech(Unit unit, TechType tech) {
        return null;
    }

    public static UnitCommand useTech(Unit unit, TechType tech, Position target) {
        return null;
    }

    public static UnitCommand useTech(Unit unit, TechType tech, Unit target) {
        return null;
    }

    public static UnitCommand placeCOP(Unit unit, TilePosition target) {
        return null;
    }

}