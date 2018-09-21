package bwapi;

import bwapi.point.TilePosition;
import bwapi.types.*;
import bwapi.values.Color;

import JavaBWAPIBackend.Client.GameData.PlayerData;

import java.util.List;

public class Player {
    private final PlayerData playerData;
    private final Game game;

    Player(final PlayerData playerData, final Game game) {
        this.playerData = playerData;
        this.game = game;
    }

    public int getID() {
        return playerData.id();
    }

    public String getName() {
        return playerData.name();
    }

    public List<Unit> getUnits() {
        return null;
    }

    public Race getRace() {
        return null;
    }

    public PlayerType getType() {
        return null;
    }

    public Force getForce() {
        return null;
    }

    public boolean isAlly(Player player) {
        return false;
    }

    public boolean isEnemy(Player player) {
        return false;
    }

    public boolean isNeutral() {
        return false;
    }

    public TilePosition getStartLocation() {
        return null;
    }

    public boolean isVictorious() {
        return false;
    }

    public boolean isDefeated() {
        return false;
    }

    public boolean leftGame() {
        return false;
    }

    public int minerals() {
        return -1;
    }

    public int gas() {
        return -1;
    }

    public int gatheredMinerals() {
        return -1;
    }

    public int gatheredGas() {
        return -1;
    }

    public int repairedMinerals() {
        return -1;
    }

    public int repairedGas() {
        return -1;
    }

    public int refundedMinerals() {
        return -1;
    }

    public int refundedGas() {
        return -1;
    }

    public int spentMinerals() {
        return -1;
    }

    public int spentGas() {
        return -1;
    }

    public int supplyTotal() {
        return -1;
    }

    public int supplyTotal(Race race) {
        return -1;
    }

    public int supplyUsed() {
        return -1;
    }

    public int supplyUsed(Race race) {
        return -1;
    }

    public int allUnitCount() {
        return -1;
    }

    public int allUnitCount(UnitType unit) {
        return -1;
    }

    public int visibleUnitCount() {
        return -1;
    }

    public int visibleUnitCount(UnitType unit) {
        return -1;
    }

    public int completedUnitCount() {
        return -1;
    }

    public int completedUnitCount(UnitType unit) {
        return -1;
    }

    public int incompleteUnitCount() {
        return -1;
    }

    public int incompleteUnitCount(UnitType unit) {
        return -1;
    }

    public int deadUnitCount() {
        return -1;
    }

    public int deadUnitCount(UnitType unit) {
        return -1;
    }

    public int killedUnitCount() {
        return -1;
    }

    public int killedUnitCount(UnitType unit) {
        return -1;
    }

    public int getUpgradeLevel(UpgradeType upgrade) {
        return -1;
    }

    public boolean hasResearched(TechType tech) {
        return false;
    }

    public boolean isResearching(TechType tech) {
        return false;
    }

    public boolean isUpgrading(UpgradeType upgrade) {
        return false;
    }

    public Color getColor() {
        return null;
    }

    public char getTextColor() {
        return 0;
    }

    public int maxEnergy(UnitType unit) {
        return -1;
    }

    public double topSpeed(UnitType unit) {
        return -1;
    }

    public int weaponMaxRange(WeaponType weapon) {
        return -1;
    }

    public int sightRange(UnitType unit) {
        return -1;
    }

    public int weaponDamageCooldown(UnitType unit) {
        return -1;
    }

    public int armor(UnitType unit) {
        return -1;
    }

    public int damage(WeaponType wpn) {
        return -1;
    }

    public int getUnitScore() {
        return -1;
    }

    public int getKillScore() {
        return -1;
    }

    public int getBuildingScore() {
        return -1;
    }

    public int getRazingScore() {
        return -1;
    }

    public int getCustomScore() {
        return -1;
    }

    public boolean isObserver() {
        return false;
    }

    public int getMaxUpgradeLevel(UpgradeType upgrade) {
        return -1;
    }

    public boolean isResearchAvailable(TechType tech) {
        return false;
    }

    public boolean isUnitAvailable(UnitType unit) {
        return false;
    }

    public boolean hasUnitTypeRequirement(UnitType unit) {
        return false;
    }

    public boolean hasUnitTypeRequirement(UnitType unit, int amount) {
        return false;
    }

    public boolean equals(Object that){
        if(!(that instanceof Player)){
            return false;
        }
        return getID() == ((Player)that).getID();
    }

    public int hashCode(){
        return getID();
    }

}
