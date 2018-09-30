package bwapi.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bwapi.types.Race.*;
import static bwapi.types.TechType.*;
import static bwapi.types.UnitSizeType.*;
import static bwapi.types.UpgradeType.*;
import static bwapi.types.WeaponType.*;
import static bwapi.types.UnitType.*;


class UnitTypeContainer {
    static Race unitRace[] ={
            Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran,
            Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran,
            Terran, Terran, Terran, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg,
            Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Terran, Zerg, Protoss, Protoss, Zerg, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss,
            Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss,
            Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Race.None, Race.None, Zerg, Zerg, Race.None, Race.None, Race.None, Race.None, Zerg, Protoss, Terran,
            Terran, Race.None, Terran, Zerg, Zerg, Race.None, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran,
            Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Race.None, Race.None, Zerg, Zerg, Zerg, Zerg,
            Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg,
            Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss,
            Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None,
            Race.None, Race.None, Race.None, Race.None, Protoss, Terran, Zerg, Terran, Protoss, Zerg, Terran, Protoss, Zerg, Terran, Protoss, Terran, Zerg,
            Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None,
            Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.Unknown
    };

    static UpgradeType armorUpgrade[]= {
            Terran_Infantry_Armor, Terran_Infantry_Armor, Terran_Vehicle_Plating, Terran_Vehicle_Plating, Terran_Vehicle_Plating,
            Terran_Vehicle_Plating, Terran_Vehicle_Plating, Terran_Infantry_Armor, Terran_Ship_Plating, Terran_Ship_Plating,
            Terran_Infantry_Armor, Terran_Ship_Plating, Terran_Ship_Plating, Upgrade_60, Upgrade_60, Terran_Infantry_Armor,
            Terran_Infantry_Armor, Terran_Vehicle_Plating, Terran_Vehicle_Plating, Terran_Vehicle_Plating, Terran_Infantry_Armor,
            Terran_Ship_Plating, Terran_Ship_Plating, Terran_Vehicle_Plating, Terran_Vehicle_Plating, Terran_Vehicle_Plating,
            Terran_Vehicle_Plating, Terran_Ship_Plating, Terran_Ship_Plating, Terran_Ship_Plating, Terran_Vehicle_Plating,
            Terran_Vehicle_Plating, Terran_Infantry_Armor, Upgrade_60, Terran_Infantry_Armor, Zerg_Carapace, Zerg_Carapace,
            Zerg_Carapace, Zerg_Carapace, Zerg_Carapace, Zerg_Carapace, Zerg_Carapace, Zerg_Flyer_Carapace, Zerg_Flyer_Carapace,
            Zerg_Flyer_Carapace, Zerg_Flyer_Carapace, Zerg_Carapace, Zerg_Flyer_Carapace, Zerg_Carapace, Zerg_Flyer_Carapace,
            Zerg_Carapace, Zerg_Carapace, Zerg_Carapace, Zerg_Carapace, Zerg_Carapace, Zerg_Flyer_Carapace, Zerg_Flyer_Carapace,
            Zerg_Flyer_Carapace, Terran_Ship_Plating, Zerg_Carapace, Protoss_Air_Armor, Protoss_Ground_Armor, Zerg_Flyer_Carapace,
            Protoss_Ground_Armor, Protoss_Ground_Armor, Protoss_Ground_Armor, Protoss_Ground_Armor, Protoss_Ground_Armor,
            Protoss_Ground_Armor, Protoss_Air_Armor, Protoss_Air_Armor, Protoss_Air_Armor, Protoss_Air_Armor, Protoss_Air_Armor,
            Protoss_Ground_Armor, Protoss_Ground_Armor, Protoss_Ground_Armor, Protoss_Ground_Armor, Protoss_Ground_Armor,
            Protoss_Ground_Armor, Protoss_Air_Armor, Protoss_Ground_Armor, Protoss_Air_Armor, Protoss_Ground_Armor, Protoss_Air_Armor,
            Protoss_Ground_Armor, Protoss_Air_Armor, Protoss_Ground_Armor, Protoss_Air_Armor, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Zerg_Carapace, Protoss_Air_Armor, Terran_Infantry_Armor,
            Terran_Infantry_Armor, Upgrade_60, Terran_Ship_Plating, Zerg_Carapace, Zerg_Carapace, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            UpgradeType.None, UpgradeType.None, UpgradeType.None, UpgradeType.None, UpgradeType.None, UpgradeType.Unknown
    };

    // DEFAULTS
    static int defaultMaxHP[]={
            40, 45, 80, 125, 0, 150, 0, 60, 120, 200, 160, 150, 500, 20, 100, 40, 250, 300, 0, 300, 200, 500, 800, 400, 0, 400, 0, 1000,
            850, 700, 150, 0, 50, 0, 60, 25, 200, 35, 80, 400, 30, 40, 200, 120, 150, 120, 80, 25, 800, 300, 60, 400, 250, 160, 120, 300,
            400, 1000, 200, 200, 100, 80, 250, 25, 20, 100, 100, 40, 10, 80, 150, 200, 300, 40, 40, 60, 100, 240, 240, 80, 400, 200, 800,
            100, 40, 20, 600, 80, 250, 60, 60, 125, 125, 60, 60, 60, 60, 200, 100, 200, 250, 1, 700, 125, 300, 800, 1500, 500, 600, 500,
            750, 1000, 600, 1250, 1300, 500, 850, 750, 600, 0, 750, 0, 850, 750, 200, 350, 700, 2000, 10000, 10000, 1500, 1250, 1800, 2500,
            250, 850, 850, 1000, 850, 750, 600, 600, 750, 400, 400, 0, 300, 5000, 2500, 750, 250, 1500, 1500, 0, 750, 500, 300, 450, 300,
            250, 500, 0, 100, 450, 500, 500, 550, 600, 2000, 500, 500, 450, 200, 100000, 1500, 5000, 100000, 100000, 100000, 800, 800, 800,
            800, 800, 800, 800, 800, 800, 100000, 700, 2000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 800,
            2500, 800, 50, 100000, 100000, 100000, 100000, 100000, 50, 50, 50, 50, 50, 800, 100000, 800, 800, 800, 800, 800, 800, 800, 800,
            800, 800, 800, 800, 0, 0, 0, 0, 0, 0
    };

    static int defaultMaxSP[] = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 80, 40, 0, 200, 20, 60, 80, 40, 350, 60, 100, 150, 150, 40, 80, 400, 800, 240,
            240, 300, 400, 400, 500, 80, 20, 10, 500, 300, 250, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 750, 500, 300,
            450, 300, 250, 500, 1, 100, 450, 500, 500, 550, 600, 0, 500, 500, 450, 200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static int Building                = 0x00000001;
    static int Addon                   = 0x00000002;
    static int Flyer                   = 0x00000004;
    static int Worker                  = 0x00000008;
    static int Subunit                 = 0x00000010;
    static int FlyingBuilding          = 0x00000020;
    static int Hero                    = 0x00000040;
    static int RegeneratesHP           = 0x00000080;
    static int AnimatedIdle            = 0x00000100;
    static int Cloakable               = 0x00000200;
    static int TwoUnitsIn1Egg          = 0x00000400;
    static int NeutralAccessories      = 0x00000800;
    static int ResourceDepot           = 0x00001000;
    static int ResourceContainer       = 0x00002000;
    static int RoboticUnit             = 0x00004000;
    static int Detector                = 0x00008000;
    static int OrganicUnit             = 0x00010000;
    static int CreepBuilding           = 0x00020000;
    static int Unused                  = 0x00040000;
    static int RequiresPsi             = 0x00080000;
    static int Burrowable              = 0x00100000;
    static int Spellcaster             = 0x00200000;
    static int PermanentCloak          = 0x00400000;
    static int NPCOrAccessories        = 0x00800000;
    static int MorphFromOtherUnit      = 0x01000000;
    static int LargeUnit               = 0x02000000;
    static int HugeUnit                = 0x04000000;
    static int AutoAttackAndMove       = 0x08000000;
    static int Attack                  = 0x10000000; /* < Can attack */
    static int Invincible              = 0x20000000;
    static int Mechanical              = 0x40000000;
    static int ProducesUnits           = 0x80000000; /* < It can produce units directly (making buildings doesn't count) */

    static int unitFlags[]= {
            OrganicUnit | AutoAttackAndMove | Attack,
            Cloakable | OrganicUnit | Spellcaster | AutoAttackAndMove | Attack,
            AutoAttackAndMove | Attack | Mechanical,
            AutoAttackAndMove | Attack | Mechanical,
            Subunit | Attack | Invincible,
            LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Subunit | Attack | Invincible,
            Worker | OrganicUnit | AutoAttackAndMove | Attack | Mechanical,
            Flyer | Cloakable | Spellcaster | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Flyer | AnimatedIdle | Detector | Spellcaster | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Hero | OrganicUnit | AutoAttackAndMove | Attack,
            Flyer | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Flyer | Spellcaster | HugeUnit | AutoAttackAndMove | Attack | Mechanical,
            AutoAttackAndMove | Attack,
            Flyer | AutoAttackAndMove | Attack | Invincible,
            OrganicUnit | AutoAttackAndMove | Attack,
            Hero | Cloakable | OrganicUnit | Spellcaster | AutoAttackAndMove | Attack,
            Hero | AutoAttackAndMove | Attack | Mechanical,
            Subunit | Attack | Invincible,
            Hero | AutoAttackAndMove | Attack | Mechanical,
            Hero | OrganicUnit | AutoAttackAndMove | Attack,
            Flyer | Hero | Cloakable | Spellcaster | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Flyer | Hero | AnimatedIdle | Detector | Spellcaster | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Hero | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Subunit | Attack | Invincible,
            Hero | LargeUnit | Mechanical,
            Subunit | Attack | Invincible,
            Flyer | Hero | Spellcaster | HugeUnit | AutoAttackAndMove | Attack | Mechanical,
            Flyer | Hero | Spellcaster | HugeUnit | AutoAttackAndMove | Attack | Mechanical,
            Flyer | Hero | Spellcaster | HugeUnit | AutoAttackAndMove | Attack | Mechanical,
            LargeUnit | Mechanical,
            Subunit | Attack | Invincible,
            OrganicUnit | AutoAttackAndMove | Attack,
            Flyer | Detector | AutoAttackAndMove | Attack,
            OrganicUnit | Spellcaster | AutoAttackAndMove | Attack,
            RegeneratesHP | OrganicUnit | AutoAttackAndMove | Attack,
            OrganicUnit,
            RegeneratesHP | TwoUnitsIn1Egg | OrganicUnit | Burrowable | AutoAttackAndMove | Attack,
            RegeneratesHP | OrganicUnit | Burrowable | AutoAttackAndMove | Attack,
            RegeneratesHP | OrganicUnit | HugeUnit | AutoAttackAndMove | Attack,
            RegeneratesHP | OrganicUnit | AutoAttackAndMove | Attack,
            Worker | RegeneratesHP | OrganicUnit | Burrowable | AutoAttackAndMove | Attack,
            Flyer | RegeneratesHP | Detector | OrganicUnit | LargeUnit | AutoAttackAndMove | Attack,
            Flyer | RegeneratesHP | OrganicUnit | LargeUnit | AutoAttackAndMove | Attack,
            Flyer | RegeneratesHP | OrganicUnit | MorphFromOtherUnit | HugeUnit | AutoAttackAndMove | Attack,
            Flyer | RegeneratesHP | OrganicUnit | Spellcaster | LargeUnit | AutoAttackAndMove | Attack,
            RegeneratesHP | OrganicUnit | Burrowable | Spellcaster | LargeUnit | AutoAttackAndMove | Attack,
            Flyer | RegeneratesHP | TwoUnitsIn1Egg | OrganicUnit | AutoAttackAndMove | Attack,
            Hero | RegeneratesHP | OrganicUnit | HugeUnit | AutoAttackAndMove | Attack,
            Flyer | Hero | RegeneratesHP | OrganicUnit | Spellcaster | LargeUnit | AutoAttackAndMove | Attack,
            RegeneratesHP | OrganicUnit | Burrowable | AutoAttackAndMove | Attack,
            Hero | RegeneratesHP | Cloakable | OrganicUnit | Spellcaster | AutoAttackAndMove | Attack,
            Hero | RegeneratesHP | OrganicUnit | Burrowable | Spellcaster | LargeUnit | AutoAttackAndMove | Attack,
            Hero | RegeneratesHP | OrganicUnit | Burrowable | AutoAttackAndMove | Attack,
            Hero | RegeneratesHP | TwoUnitsIn1Egg | OrganicUnit | Burrowable | AutoAttackAndMove | Attack,
            Flyer | Hero | RegeneratesHP | OrganicUnit | LargeUnit | AutoAttackAndMove | Attack,
            Flyer | Hero | RegeneratesHP | OrganicUnit | MorphFromOtherUnit | HugeUnit | AutoAttackAndMove | Attack,
            Flyer | Hero | RegeneratesHP | Detector | OrganicUnit | LargeUnit | AutoAttackAndMove | Attack,
            Flyer | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Flyer | OrganicUnit,
            Flyer | Spellcaster | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            OrganicUnit | PermanentCloak | AutoAttackAndMove | Attack,
            Flyer | RegeneratesHP | OrganicUnit | MorphFromOtherUnit | HugeUnit | AutoAttackAndMove | Attack,
            AnimatedIdle | Spellcaster | HugeUnit | AutoAttackAndMove | Attack,
            Worker | RoboticUnit | AutoAttackAndMove | Attack | Mechanical,
            OrganicUnit | AutoAttackAndMove | Attack,
            LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            OrganicUnit | Spellcaster | AutoAttackAndMove | Attack,
            AnimatedIdle | HugeUnit | AutoAttackAndMove | Attack,
            Flyer | RoboticUnit | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Flyer | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Flyer | Spellcaster | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Flyer | HugeUnit | AutoAttackAndMove | Attack | Mechanical | ProducesUnits,
            Flyer | AutoAttackAndMove | Attack | Mechanical,
            OrganicUnit | PermanentCloak | AutoAttackAndMove | Attack,
            Hero | OrganicUnit | PermanentCloak | AutoAttackAndMove | Attack,
            Hero | AnimatedIdle | HugeUnit | AutoAttackAndMove | Attack,
            Hero | OrganicUnit | AutoAttackAndMove | Attack,
            Hero | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Hero | OrganicUnit | Spellcaster | AutoAttackAndMove | Attack,
            Flyer | Hero | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Hero | RoboticUnit | HugeUnit | AutoAttackAndMove | Attack | Mechanical | ProducesUnits,
            Flyer | Hero | HugeUnit | AutoAttackAndMove | Attack | Mechanical | ProducesUnits,
            RoboticUnit | HugeUnit | AutoAttackAndMove | Attack | Mechanical | ProducesUnits,
            Flyer | RoboticUnit | Detector | PermanentCloak | AutoAttackAndMove | Attack | Mechanical,
            AutoAttackAndMove | Attack | Invincible | Mechanical,
            Flyer | Hero | Spellcaster | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Hero | OrganicUnit | Spellcaster | AutoAttackAndMove | Attack,
            Flyer | Hero | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            OrganicUnit | AutoAttackAndMove | Attack,
            OrganicUnit | AutoAttackAndMove | Attack,
            0,
            0,
            OrganicUnit | AutoAttackAndMove | Attack,
            Flyer | OrganicUnit | AutoAttackAndMove | Attack,
            OrganicUnit | AutoAttackAndMove | Attack,
            OrganicUnit | AutoAttackAndMove | Attack,
            OrganicUnit,
            Flyer | Hero | Spellcaster | LargeUnit | AutoAttackAndMove | Attack | Mechanical,
            Hero | Cloakable | OrganicUnit | Spellcaster | AutoAttackAndMove | Attack,
            Hero | Cloakable | OrganicUnit | Spellcaster | AutoAttackAndMove | Attack,
            Flyer,
            Flyer | Hero | Spellcaster | HugeUnit | AutoAttackAndMove | Attack | Mechanical,
            RegeneratesHP | OrganicUnit | Burrowable | AutoAttackAndMove | Attack,
            Hero | RegeneratesHP | Cloakable | OrganicUnit | Spellcaster | AutoAttackAndMove | Attack,
            Invincible,
            Building | FlyingBuilding | ResourceDepot | HugeUnit | Mechanical | ProducesUnits,
            Building | Addon | Spellcaster | HugeUnit | Mechanical,
            Building | Addon | HugeUnit | Mechanical,
            Building | HugeUnit | Mechanical,
            Building | ResourceContainer | HugeUnit | Mechanical,
            Building | FlyingBuilding | HugeUnit | Mechanical | ProducesUnits,
            Building | HugeUnit | Mechanical,
            Building | FlyingBuilding | HugeUnit | Mechanical | ProducesUnits,
            Building | FlyingBuilding | HugeUnit | Mechanical | ProducesUnits,
            Building | Addon | HugeUnit | Mechanical,
            Building | FlyingBuilding | HugeUnit | Mechanical,
            Building | Addon | HugeUnit | Mechanical,
            Building | Addon | HugeUnit | Mechanical,
            Building | HugeUnit,
            Building | Addon | HugeUnit | Mechanical,
            Building | Addon | HugeUnit | Mechanical,
            Building | FlyingBuilding | HugeUnit | Mechanical,
            Building | HugeUnit | Mechanical,
            Building | AnimatedIdle | Detector | HugeUnit | Attack | Mechanical,
            Building | HugeUnit | Mechanical,
            Building | HugeUnit | Mechanical,
            Building | HugeUnit | Mechanical,
            NeutralAccessories | Invincible,
            NeutralAccessories | Invincible,
            Building | FlyingBuilding | RegeneratesHP | OrganicUnit | HugeUnit | ProducesUnits,
            Building | RegeneratesHP | ResourceDepot | OrganicUnit | MorphFromOtherUnit | HugeUnit | ProducesUnits,
            Building | RegeneratesHP | ResourceDepot | OrganicUnit | MorphFromOtherUnit | HugeUnit | ProducesUnits,
            Building | RegeneratesHP | ResourceDepot | OrganicUnit | MorphFromOtherUnit | HugeUnit | ProducesUnits,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | Detector | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit | Attack,
            Building | RegeneratesHP | ResourceContainer | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit | Attack,
            Building | RegeneratesHP | Detector | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | Detector | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | ResourceContainer | OrganicUnit | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | Detector | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | Detector | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Building | RegeneratesHP | MorphFromOtherUnit | HugeUnit,
            Building | ResourceDepot | HugeUnit | Mechanical | ProducesUnits,
            Building | RequiresPsi | HugeUnit | Mechanical | ProducesUnits,
            Building | HugeUnit | Mechanical,
            Building | ResourceContainer | HugeUnit | Mechanical,
            Building | RequiresPsi | HugeUnit | Mechanical,
            Building | RequiresPsi | HugeUnit | Mechanical,
            Building | RequiresPsi | HugeUnit | Mechanical | ProducesUnits,
            Building | RequiresPsi | HugeUnit | Mechanical,
            Building | Detector | RequiresPsi | HugeUnit | Attack | Mechanical,
            Building | RequiresPsi | HugeUnit | Mechanical,
            Building | RequiresPsi | HugeUnit | Mechanical,
            Building | RequiresPsi | HugeUnit | Mechanical,
            Building | RequiresPsi | HugeUnit | Mechanical,
            Building | RequiresPsi | HugeUnit | Mechanical | ProducesUnits,
            Building | HugeUnit | Mechanical,
            Building | RequiresPsi | HugeUnit | Mechanical,
            Building | RequiresPsi | HugeUnit | Mechanical,
            Building | RequiresPsi | HugeUnit | Mechanical,
            Building | RequiresPsi | Spellcaster | HugeUnit | Mechanical,
            Building | HugeUnit | Invincible,
            Building | HugeUnit,
            Building | HugeUnit,
            Building | ResourceContainer | HugeUnit | Invincible,
            Building | ResourceContainer | HugeUnit | Invincible,
            Building | ResourceContainer | HugeUnit | Invincible,
            Building | ResourceContainer | HugeUnit | Invincible,
            Building | ResourceContainer | HugeUnit | Invincible,
            Building | HugeUnit | Mechanical,
            Building | HugeUnit | Mechanical,
            Building | HugeUnit | Mechanical,
            Building | HugeUnit | Mechanical,
            Building | HugeUnit,
            Building | HugeUnit,
            Building | HugeUnit,
            Building | ResourceContainer | HugeUnit | Invincible,
            Building | HugeUnit | Mechanical,
            Building | HugeUnit | Mechanical,
            Building | Invincible,
            Building | Invincible,
            Building | Invincible,
            Building | Invincible,
            Building | Invincible,
            Building | Invincible,
            Building | Invincible,
            Building | Invincible,
            Building | Invincible,
            Building | HugeUnit | Mechanical,
            Building | RegeneratesHP | Detector | OrganicUnit | CreepBuilding | MorphFromOtherUnit | HugeUnit,
            Invincible,
            Detector | HugeUnit | Attack | Mechanical,
            Invincible,
            Invincible,
            Invincible,
            Invincible,
            Invincible,
            Detector | HugeUnit | Attack | Mechanical,
            Detector | HugeUnit | Mechanical,
            Detector | HugeUnit | Mechanical,
            Detector | HugeUnit | Mechanical,
            Detector | HugeUnit | Mechanical,
            Building | ResourceDepot,
            NeutralAccessories | Invincible,
            NeutralAccessories | NPCOrAccessories | Invincible,
            NeutralAccessories | NPCOrAccessories | Invincible,
            NeutralAccessories | NPCOrAccessories | Invincible,
            NeutralAccessories | NPCOrAccessories | Invincible,
            NeutralAccessories | NPCOrAccessories,
            NeutralAccessories | NPCOrAccessories,
            NeutralAccessories | NPCOrAccessories,
            NeutralAccessories | NPCOrAccessories,
            NeutralAccessories | NPCOrAccessories,
            NeutralAccessories | NPCOrAccessories,
            NeutralAccessories | NPCOrAccessories,
            NeutralAccessories | NPCOrAccessories,
            0,0,0,0,0,0
    };

    static int defaultArmorAmount[] = {
            0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 3, 1, 3, 0, 0, 0, 3, 3, 0, 3, 3, 4, 4, 3, 0, 3, 0, 4, 4, 4, 1, 0, 1, 0, 1, 10, 10, 0,
            0, 1, 0, 0, 0, 0, 2, 0, 1, 0, 4, 3, 0, 2, 3, 2, 3, 3, 4, 4, 2, 0, 1, 1, 2, 1, 0, 1, 1, 0, 0, 1, 0, 1, 4, 0, 0, 0, 3,
            2, 3, 2, 3, 3, 4, 0, 0, 0, 3, 2, 3, 0, 0, 1, 1, 0, 0, 0, 0, 10, 0, 2, 3, 0, 4, 1, 3, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static int defaultOreCost[] = {
            50, 25, 75, 100, 1, 150, 1, 50, 150, 100, 100, 100, 400, 1, 200, 0, 50, 200, 1, 150, 50, 400, 50, 300, 1, 300, 1, 800,
            800, 800, 150, 1, 50, 0, 50, 1, 1, 50, 75, 200, 1, 50, 100, 100, 50, 100, 50, 25, 400, 200, 100, 200, 50, 150, 100, 200,
            100, 200, 250, 1, 150, 125, 150, 0, 50, 100, 125, 50, 0, 200, 275, 100, 350, 25, 150, 100, 0, 200, 300, 100, 600, 400,
            700, 200, 25, 15, 50, 100, 600, 1, 1, 100, 100, 1, 1, 1, 1, 1, 150, 200, 200, 0, 800, 50, 200, 250, 400, 50, 100, 100,
            100, 150, 150, 200, 150, 50, 100, 50, 50, 1, 50, 1, 125, 100, 75, 100, 800, 200, 1, 1, 1, 300, 150, 200, 150, 100, 100,
            100, 150, 75, 150, 200, 200, 75, 50, 1, 50, 1, 1, 50, 0, 0, 0, 1, 400, 200, 100, 100, 1, 50, 150, 1, 150, 150, 200, 150,
            150, 150, 150, 300, 200, 150, 100, 250, 250, 1500, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 600, 1000, 250, 50, 100, 250,
            50, 100, 250, 50, 100, 200, 1000, 250, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0
    };

    static int defaultGasCost[] = {
            0, 75, 0, 50, 1, 100, 1, 0, 100, 225, 50, 100, 300, 0, 200, 0, 150, 100, 1, 0, 0, 200, 600, 200, 1, 200, 1, 600, 600, 600, 100,
            1, 25, 0, 25, 1, 1, 0, 25, 200, 1, 0, 0, 100, 100, 100, 150, 75, 400, 300, 50, 300, 200, 50, 0, 200, 200, 0, 125, 1, 100, 100,
            50, 0, 0, 0, 50, 150, 0, 0, 125, 350, 250, 0, 150, 300, 0, 0, 100, 300, 300, 200, 600, 100, 75, 0, 1000, 300, 300, 1, 1, 100,
            100, 1, 1, 1, 1, 1, 100, 75, 75, 0, 600, 100, 75, 250, 0, 50, 100, 0, 0, 0, 0, 100, 100, 50, 150, 50, 50, 1, 50, 1, 0, 50, 0, 0,
            600, 0, 1, 1, 1, 0, 100, 150, 0, 50, 100, 150, 100, 0, 200, 150, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 200, 0, 0, 1, 100, 0, 1,
            0, 100, 0, 200, 0, 150, 0, 200, 150, 100, 0, 0, 0, 500, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 200, 400, 0, 50, 100, 0, 50, 100,
            0, 50, 100, 50, 500, 200, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0
    };

    static int defaultTimeCost[] = {
            360, 750, 450, 600, 1, 750, 1, 300, 900, 1200, 720, 750, 2000, 1, 1500, 1, 1500, 1200, 1, 900, 1, 1800, 2400, 1500, 1, 1500, 1,
            4800, 2400, 4800, 750, 1, 360, 1, 450, 1, 1, 420, 420, 900, 1, 300, 600, 600, 600, 750, 750, 450, 1800, 1500, 600, 1500, 1500,
            780, 840, 1200, 1200, 1200, 750, 1, 600, 750, 600, 300, 300, 600, 750, 750, 300, 900, 1200, 2400, 2100, 300, 750, 1500, 600,
            1200, 1500, 1500, 2400, 1800, 4200, 1050, 600, 105, 4800, 1500, 2400, 1, 1, 600, 600, 1, 1, 1, 1, 1, 750, 1500, 1500, 1, 4800,
            600, 1500, 2400, 1800, 600, 1200, 600, 600, 1200, 1200, 1200, 1050, 600, 900, 600, 600, 1, 600, 1, 900, 1200, 450, 450, 4800,
            900, 1, 1, 1800, 1800, 1500, 1800, 600, 600, 900, 1800, 900, 600, 1200, 1800, 1200, 300, 300, 1, 300, 1, 1, 600, 0, 0, 0, 1,
            1800, 1200, 450, 600, 1, 450, 900, 1, 750, 900, 900, 900, 600, 1050, 1, 900, 900, 450, 450, 1, 1, 4800, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 2400, 4800, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2400, 2400, 2400, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0
    };

    static int unitSupplyProvided[] = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static int unitSupplyRequired[] = {
            2, 2, 4, 4, 0, 4, 0, 2, 4, 4, 0, 4, 12, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 2, 0, 2, 0, 0, 1, 2, 8, 0, 2,
            0, 4, 4, 4, 4, 1, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 6, 0, 4, 4, 4, 8, 2, 4, 4, 4, 8, 4, 6, 8, 12, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 8, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static int unitSpaceRequired[] = {
            1, 1, 2, 2, 255, 4, 255, 1, 255, 255, 1, 255, 255, 255, 255, 1, 1, 2, 255, 2, 1, 255, 255, 4, 255, 255, 255, 255, 255,
            255, 255, 255, 1, 255, 1, 255, 255, 1, 2, 4, 1, 1, 255, 255, 255, 255, 2, 255, 4, 255, 1, 1, 2, 2, 1, 255, 255, 255, 255,
            255, 255, 2, 255, 4, 1, 2, 4, 2, 4, 255, 255, 255, 255, 255, 2, 2, 4, 2, 4, 2, 255, 4, 255, 4, 255, 255, 255, 2, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255, 255, 1, 1, 255, 255, 4, 1, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
            255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0
    };

    static int unitSpaceProvided[] = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static int unitBuildScore[] = {
            50, 175, 75, 200, 0, 350, 0, 50, 400, 625, 0, 300, 1200, 0, 800, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100, 0, 125,
            0, 0, 25, 125, 650, 0, 50, 100, 300, 550, 400, 225, 100, 0, 0, 200, 0, 0, 0, 0, 0, 0, 0, 400, 0, 350, 325, 550, 650, 50, 100, 250,
            350, 700, 200, 650, 1025, 950, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 400, 225, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 250,
            0, 0, 400, 75, 75, 50, 50, 75, 100, 200, 200, 100, 275, 75, 75, 0, 75, 0, 65, 100, 50, 50, 0, 0, 0, 0, 300, 300, 100, 100, 75, 100,
            150, 200, 175, 40, 275, 250, 75, 40, 25, 10, 40, 0, 0, 25, 0, 0, 0, 10, 400, 300, 50, 50, 10, 175, 75, 0, 100, 200, 100, 250, 100,
            300, 0, 350, 450, 125, 50, 0, 0, 0, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static int unitDestroyScore[] = {
            100, 350, 150, 400, 0, 700, 0, 100, 800, 1250, 400, 600, 2400, 25, 0, 10, 700, 800, 0, 300, 200, 1600, 2500, 1400, 0, 1400, 0, 4800,
            4800, 4800, 700, 0, 200, 0, 250, 10, 25, 50, 350, 1300, 25, 100, 200, 600, 1100, 800, 450, 200, 2600, 1600, 400, 4000, 900, 500, 100,
            1200, 2200, 400, 800, 1100, 700, 650, 1100, 1300, 100, 200, 500, 700, 1400, 400, 1300, 2050, 1900, 60, 400, 800, 2800, 400, 1000, 1400,
            2600, 1600, 3800, 800, 450, 0, 4100, 1400, 2400, 10, 10, 0, 0, 10, 10, 10, 10, 500, 1300, 700, 700, 0, 4800, 500, 700, 0, 1200, 225,
            225, 150, 150, 225, 300, 600, 600, 300, 825, 225, 225, 0, 225, 0, 195, 300, 150, 150, 5000, 5000, 0, 0, 900, 900, 1200, 1500, 225, 300,
            450, 1350, 525, 120, 825, 750, 225, 120, 195, 10, 240, 10000, 10000, 75, 5000, 2500, 2500, 10, 1200, 900, 150, 150, 10, 525, 225, 0,
            300, 600, 300, 750, 300, 900, 5000, 1050, 1350, 375, 150, 2500, 5000, 5000, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 2000,
            3600, 0, 0, 0, 0, 0, 0, 0, 0, 0, 600, 4000, 0, 100, 10, 10, 10, 10, 10, 100, 100, 100, 100, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0
    };

    static class UnitDimensions {
        static int tileWidth  = 0;
        static int tileHeight = 1;
        static int left       = 2;
        static int up         = 3;
        static int right      = 4;
        static int down       = 5;
    }
    static int unitDimensions[][] = {
            {1,1,8,9,8,10}, {1,1,7,10,7,11}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,1,1,1,1}, {1,1,16,16,15,15}, {1,1,1,1,1,1}, {1,1,11,11,11,11}, {1,1,19,15,18,14},
            {2,2,32,33,32,16}, {1,1,11,7,11,14}, {2,2,24,16,24,20}, {2,2,37,29,37,29}, {1,1,7,7,7,7}, {1,1,7,14,7,14}, {1,1,8,9,8,10}, {1,1,7,10,7,11}, {1,1,16,16,15,15},
            {1,1,1,1,1,1}, {1,1,16,16,15,15}, {1,1,8,9,8,10}, {1,1,19,15,18,14}, {2,2,32,33,32,16}, {1,1,16,16,15,15}, {1,1,1,1,1,1}, {1,1,16,16,15,15}, {1,1,1,1,1,1},
            {2,2,37,29,37,29}, {2,2,37,29,37,29}, {2,2,37,29,37,29}, {1,1,16,16,15,15}, {1,1,1,1,1,1}, {1,1,11,7,11,14}, {1,1,13,13,13,17}, {1,1,8,9,8,10}, {1,1,8,8,7,7},
            {1,1,16,16,15,15}, {1,1,8,4,7,11}, {1,1,10,10,10,12}, {2,2,19,16,18,15}, {1,1,9,9,9,9}, {1,1,11,11,11,11}, {2,2,25,25,24,24}, {2,2,22,22,21,21}, {2,2,22,22,21,21},
            {2,2,24,24,23,23}, {1,1,13,12,13,12}, {1,1,12,12,11,11}, {2,2,19,16,18,15}, {2,2,24,24,23,23}, {1,1,8,9,8,10}, {1,1,7,10,7,11}, {1,1,13,12,13,12}, {1,1,10,10,10,12},
            {1,1,8,4,7,11}, {2,2,22,22,21,21}, {2,2,22,22,21,21}, {2,2,25,25,24,24}, {2,2,24,16,24,20}, {1,1,16,16,15,15}, {1,1,18,16,17,15}, {1,1,12,6,11,19}, {2,2,22,22,21,21},
            {1,1,16,16,15,15}, {1,1,11,11,11,11}, {1,1,11,5,11,13}, {1,1,15,15,16,16}, {1,1,12,10,11,13}, {1,1,16,16,15,15}, {2,1,20,16,19,15}, {2,1,18,16,17,15}, {2,2,22,22,21,21},
            {2,2,32,32,31,31}, {1,1,8,8,7,7}, {1,1,12,6,11,19}, {1,1,12,6,11,19}, {1,1,16,16,15,15}, {1,1,11,5,11,13}, {1,1,15,15,16,16}, {1,1,12,10,11,13}, {2,1,18,16,17,15},
            {1,1,16,16,15,15}, {2,2,32,32,31,31}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,2,2,2,2}, {2,2,22,22,21,21}, {1,1,12,10,11,13}, {2,1,18,16,17,15}, {1,1,16,16,15,15},
            {1,1,16,16,15,15}, {1,1,15,15,16,16}, {1,1,15,15,16,16}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15},
            {1,1,18,16,17,15}, {1,1,7,10,7,11}, {1,1,7,10,7,11}, {1,1,13,13,13,17}, {2,2,37,29,37,29}, {1,1,15,15,16,16}, {1,1,7,10,7,11}, {4,3,60,40,59,39}, {4,3,58,41,58,41},
            {2,2,37,16,31,25}, {2,2,37,16,31,25}, {3,2,38,22,38,26}, {4,2,56,32,56,31}, {4,3,48,40,56,32}, {3,2,40,32,44,24}, {4,3,56,40,56,40}, {4,3,48,40,48,38},
            {2,2,47,24,28,22}, {4,3,48,38,48,38}, {2,2,47,24,28,22}, {2,2,47,24,28,22}, {3,2,48,32,47,31}, {2,2,39,24,31,24}, {3,3,48,48,47,47}, {4,3,48,32,48,28},
            {3,2,48,32,47,22}, {2,2,16,32,16,16}, {3,2,32,24,32,16}, {3,2,48,32,47,31}, {3,2,48,32,47,31}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {4,3,58,41,58,41},
            {4,3,49,32,49,32}, {4,3,49,32,49,32}, {4,3,49,32,49,32}, {2,2,32,32,31,31}, {3,2,40,32,40,24}, {4,2,48,32,48,4}, {2,2,28,32,28,24}, {3,2,38,28,32,28},
            {3,2,44,32,32,20}, {3,2,40,32,32,31}, {2,2,28,32,28,24}, {3,2,36,28,40,18}, {2,2,24,24,23,23}, {2,2,24,24,23,23}, {3,3,48,48,47,47}, {2,2,24,24,23,23},
            {5,3,80,32,79,40}, {5,3,80,32,79,40}, {4,2,64,32,63,31}, {2,2,32,32,31,31}, {3,2,40,32,32,31}, {3,2,40,32,32,31}, {1,1,16,16,15,15}, {4,3,56,39,56,39},
            {3,2,36,16,40,20}, {2,2,16,12,16,20}, {4,2,48,32,48,24}, {4,3,64,48,63,47}, {3,2,44,16,44,28}, {4,3,48,32,48,40}, {3,3,48,48,47,47}, {2,2,20,16,20,16},
            {3,2,24,24,40,24}, {3,2,40,24,40,24}, {3,2,32,24,32,24}, {3,2,36,24,36,20}, {4,3,48,40,48,32}, {4,3,64,48,63,47}, {3,2,40,32,47,24}, {3,2,44,28,44,28},
            {3,2,32,32,32,20}, {3,2,32,16,32,16}, {4,3,64,48,63,47}, {7,3,112,48,111,47}, {5,4,80,34,79,63}, {2,1,32,16,31,15}, {2,1,32,16,31,15}, {2,1,32,16,31,15},
            {2,2,32,32,31,31}, {2,2,32,32,31,31}, {2,2,32,32,31,31}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {2,2,32,32,31,31}, {1,1,16,16,15,15}, {1,1,16,16,15,15},
            {1,1,16,16,15,15}, {4,2,64,32,63,31}, {3,2,48,32,47,31}, {5,3,80,38,69,47}, {3,2,48,32,47,31}, {3,2,48,32,47,31}, {3,2,48,32,47,31}, {3,2,48,32,47,31},
            {3,2,48,32,47,31}, {3,2,48,32,47,31}, {3,2,48,32,47,31}, {3,2,48,32,47,31}, {3,2,48,32,47,31}, {4,3,56,28,63,43}, {3,2,48,32,47,31}, {5,5,80,80,79,79},
            {2,2,32,32,31,31}, {8,4,128,64,127,63}, {3,2,25,17,44,20}, {3,2,44,17,25,20}, {3,2,41,17,28,20}, {3,2,28,17,41,20}, {2,2,32,32,31,31}, {1,1,16,16,15,15},
            {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {4,3,48,32,48,32}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15},
            {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15}, {1,1,16,16,15,15},
            {1,1,16,16,15,15}, {0,0,0,0,0,0}, {0,0,0,0,0,0}, {0,0,0,0,0,0}, {0,0,0,0,0,0}, {0,0,0,0,0,0}, {0,0,0,0,0,0}
    };

    static UnitType whatBuilds[] = {
            Terran_Barracks, Terran_Barracks, Terran_Factory, Terran_Factory, UnitType.None, Terran_Factory, UnitType.None, Terran_Command_Center, Terran_Starport,
            Terran_Starport, UnitType.None, Terran_Starport, Terran_Starport, UnitType.None, Terran_Nuclear_Silo, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, Terran_Factory, UnitType.None, Terran_Barracks, UnitType.None, Terran_Barracks, Zerg_Hatchery, Zerg_Larva,
            Zerg_Larva, Zerg_Larva, Zerg_Larva, UnitType.None, Zerg_Larva, Zerg_Larva, Zerg_Larva, Zerg_Mutalisk, Zerg_Larva, Zerg_Larva, Zerg_Larva,
            UnitType.None, UnitType.None, Zerg_Infested_Command_Center, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, Terran_Starport, Zerg_Mutalisk, Protoss_Stargate,
            Protoss_Gateway, Zerg_Mutalisk, Protoss_Dark_Templar, Protoss_Nexus, Protoss_Gateway, Protoss_Gateway, Protoss_Gateway,
            Protoss_High_Templar, Protoss_Robotics_Facility, Protoss_Stargate, Protoss_Stargate, Protoss_Stargate, Protoss_Carrier, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, Protoss_Robotics_Facility, Protoss_Robotics_Facility, Protoss_Reaver, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, Zerg_Hydralisk, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, Zerg_Hydralisk, UnitType.None, UnitType.None, Terran_SCV,
            Terran_Command_Center, Terran_Command_Center, Terran_SCV, Terran_SCV, Terran_SCV, Terran_SCV, Terran_SCV, Terran_SCV, Terran_Starport,
            Terran_SCV, Terran_Science_Facility, Terran_Science_Facility, UnitType.None, Terran_Factory, UnitType.None, Terran_SCV, Terran_SCV, Terran_SCV,
            Terran_SCV, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, Zerg_Drone, Zerg_Hatchery, Zerg_Lair, Zerg_Drone, Zerg_Drone, Zerg_Drone, Zerg_Spire,
            Zerg_Drone, Zerg_Drone, Zerg_Drone, Zerg_Drone, Zerg_Drone, Zerg_Drone, Zerg_Creep_Colony, UnitType.None, Zerg_Creep_Colony, UnitType.None, UnitType.None,
            Zerg_Drone, UnitType.None, UnitType.None, UnitType.None, UnitType.None, Protoss_Probe, Protoss_Probe, Protoss_Probe, Protoss_Probe, UnitType.None, Protoss_Probe, Protoss_Probe,
            UnitType.None, Protoss_Probe, Protoss_Probe, Protoss_Probe, Protoss_Probe, Protoss_Probe, Protoss_Probe, UnitType.None, Protoss_Probe, Protoss_Probe,
            Protoss_Probe, Protoss_Probe, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.Unknown
    };

    static UnitType buildsWhat[][] = {
            {},// Terran_Marine = 0,
            {},// Terran_Ghost,
            {},// Terran_Vulture,
            {},// Terran_Goliath,
            {},// Terran_Goliath_Turret,
            {},// Terran_Siege_Tank_Tank_Mode,
            {},// Terran_Siege_Tank_Tank_Mode_Turret,
            {Terran_Academy, Terran_Armory, Terran_Barracks, Terran_Bunker, Terran_Command_Center, Terran_Engineering_Bay, Terran_Factory, Terran_Missile_Turret, Terran_Refinery, Terran_Science_Facility, Terran_Starport, Terran_Supply_Depot },// Terran_SCV,
            {},// Terran_Wraith,
            {},// Terran_Science_Vessel,
            {},// Hero_Gui_Montag,
            {},// Terran_Dropship,
            {},// Terran_Battlecruiser,
            {},// Terran_Vulture_Spider_Mine,
            {},// Terran_Nuclear_Missile,
            {},// Terran_Civilian,
            {},// Hero_Sarah_Kerrigan,
            {},// Hero_Alan_Schezar,
            {},// Hero_Alan_Schezar_Turret,
            {},// Hero_Jim_Raynor_Vulture,
            {},// Hero_Jim_Raynor_Marine,
            {},// Hero_Tom_Kazansky,
            {},// Hero_Magellan,
            {},// Hero_Edmund_Duke_Tank_Mode,
            {},// Hero_Edmund_Duke_Tank_Mode_Turret,
            {},// Hero_Edmund_Duke_Siege_Mode,
            {},// Hero_Edmund_Duke_Siege_Mode_Turret,
            {},// Hero_Arcturus_Mengsk,
            {},// Hero_Hyperion,
            {},// Hero_Norad_II,
            {},// Terran_Siege_Tank_Siege_Mode,
            {},// Terran_Siege_Tank_Siege_Mode_Turret,
            {},// Terran_Firebat,
            {},// Spell_Scanner_Sweep,
            {},// Terran_Medic,
            {Zerg_Defiler, Zerg_Drone, Zerg_Hydralisk, Zerg_Mutalisk, Zerg_Overlord, Zerg_Queen, Zerg_Scourge, Zerg_Ultralisk, Zerg_Zergling},// Zerg_Larva,
            {},// Zerg_Egg,
            {},// Zerg_Zergling,
            {Zerg_Lurker},// Zerg_Hydralisk,
            {},// Zerg_Ultralisk,
            {},// Zerg_Broodling,
            {Zerg_Creep_Colony, Zerg_Defiler_Mound, Zerg_Evolution_Chamber, Zerg_Extractor, Zerg_Hatchery, Zerg_Hydralisk_Den, Zerg_Nydus_Canal, Zerg_Queens_Nest, Zerg_Spawning_Pool, Zerg_Spire, Zerg_Ultralisk_Cavern},// Zerg_Drone,
            {},// Zerg_Overlord,
            {Zerg_Guardian, Zerg_Devourer},// Zerg_Mutalisk,
            {},// Zerg_Guardian,
            {},// Zerg_Queen,
            {},// Zerg_Defiler,
            {},// Zerg_Scourge,
            {},// Hero_Torrasque,
            {},// Hero_Matriarch,
            {},// Zerg_Infested_Terran,
            {},// Hero_Infested_Kerrigan,
            {},// Hero_Unclean_One,
            {},// Hero_Hunter_Killer,
            {},// Hero_Devouring_One,
            {},// Hero_Kukulza_Mutalisk,
            {},// Hero_Kukulza_Guardian,
            {},// Hero_Yggdrasill,
            {},// Terran_Valkyrie,
            {},// Zerg_Cocoon,
            {},// Protoss_Corsair,
            {Protoss_Dark_Archon},// Protoss_Dark_Templar,
            {},// Zerg_Devourer,
            {},// Protoss_Dark_Archon,
            { Protoss_Arbiter_Tribunal, Protoss_Assimilator, Protoss_Citadel_of_Adun, Protoss_Cybernetics_Core, Protoss_Fleet_Beacon, Protoss_Forge, Protoss_Gateway, Protoss_Nexus, Protoss_Observatory, Protoss_Photon_Cannon, Protoss_Pylon, Protoss_Robotics_Facility, Protoss_Robotics_Support_Bay, Protoss_Shield_Battery, Protoss_Stargate, Protoss_Templar_Archives},// Protoss_Probe,
            {},// Protoss_Zealot,
            {},// Protoss_Dragoon,
            {Protoss_Archon},// Protoss_High_Templar,
            {},// Protoss_Archon,
            {},// Protoss_Shuttle,
            {},// Protoss_Scout,
            {},// Protoss_Arbiter,
            {Protoss_Interceptor},// Protoss_Carrier,
            {},// Protoss_Interceptor,
            {},// Hero_Dark_Templar,
            {},// Hero_Zeratul,
            {},// Hero_Tassadar_Zeratul_Archon,
            {},// Hero_Fenix_Zealot,
            {},// Hero_Fenix_Dragoon,
            {},// Hero_Tassadar,
            {},// Hero_Mojo,
            {},// Hero_Warbringer,
            {},// Hero_Gantrithor,
            {Protoss_Scarab},// Protoss_Reaver,
            {},// Protoss_Observer,
            {},// Protoss_Scarab,
            {},// Hero_Danimoth,
            {},// Hero_Aldaris,
            {},// Hero_Artanis,
            {},// Critter_Rhynadon,
            {},// Critter_Bengalaas,
            {},// Special_Cargo_Ship,
            {},// Special_Mercenary_Gunship,
            {},// Critter_Scantid,
            {},// Critter_Kakaru,
            {},// Critter_Ragnasaur,
            {},// Critter_Ursadon,
            {},// Zerg_Lurker_Egg,
            {},// Hero_Raszagal,
            {},// Hero_Samir_Duran,
            {},// Hero_Alexei_Stukov,
            {},// Special_Map_Revealer,
            {},// Hero_Gerard_DuGalle,
            {},// Zerg_Lurker,
            {},// Hero_Infested_Duran,
            {},// Spell_Disruption_Web,
            {Terran_SCV, Terran_Comsat_Station, Terran_Nuclear_Silo},// Terran_Command_Center,
            {},// Terran_Comsat_Station,
            {Terran_Nuclear_Missile},// Terran_Nuclear_Silo,
            {},// Terran_Supply_Depot,
            {},// Terran_Refinery,
            {Terran_Marine, Terran_Medic, Terran_Firebat, Terran_Ghost},// Terran_Barracks,
            {},// Terran_Academy,
            {Terran_Vulture, Terran_Siege_Tank_Tank_Mode, Terran_Goliath, Terran_Machine_Shop},// Terran_Factory,
            {Terran_Wraith, Terran_Dropship, Terran_Science_Vessel, Terran_Battlecruiser, Terran_Valkyrie, Terran_Control_Tower},// Terran_Starport,
            {},// Terran_Control_Tower,
            {Terran_Covert_Ops, Terran_Physics_Lab},// Terran_Science_Facility,
            {},// Terran_Covert_Ops,
            {},// Terran_Physics_Lab,
            {},// Unused_Terran1,
            {},// Terran_Machine_Shop,
            {},// Unused_Terran2,
            {},// Terran_Engineering_Bay,
            {},// Terran_Armory,
            {},// Terran_Missile_Turret,
            {},// Terran_Bunker,
            {},// Special_Crashed_Norad_II,
            {},// Special_Ion_Cannon,
            {},// Powerup_Uraj_Crystal,
            {},// Powerup_Khalis_Crystal,
            {Zerg_Infested_Terran},// Zerg_Infested_Command_Center,
            {Zerg_Lair},// Zerg_Hatchery,
            {Zerg_Hive},// Zerg_Lair,
            {},// Zerg_Hive,
            {},// Zerg_Nydus_Canal,
            {},// Zerg_Hydralisk_Den,
            {},// Zerg_Defiler_Mound,
            {},// Zerg_Greater_Spire,
            {},// Zerg_Queens_Nest,
            {},// Zerg_Evolution_Chamber,
            {},// Zerg_Ultralisk_Cavern,
            {Zerg_Greater_Spire},// Zerg_Spire,
            {},// Zerg_Spawning_Pool,
            {Zerg_Sunken_Colony, Zerg_Spore_Colony},// Zerg_Creep_Colony,
            {},// Zerg_Spore_Colony,
            {},// Unused_Zerg1,
            {},// Zerg_Sunken_Colony,
            {},// Special_Overmind_With_Shell,
            {},// Special_Overmind,
            {},// Zerg_Extractor,
            {},// Special_Mature_Chrysalis,
            {},// Special_Cerebrate,
            {},// Special_Cerebrate_Daggoth,
            {},// Unused_Zerg2,
            {Protoss_Probe},// Protoss_Nexus,
            {Protoss_Observer, Protoss_Reaver, Protoss_Shuttle},// Protoss_Robotics_Facility,
            {},// Protoss_Pylon,
            {},// Protoss_Assimilator,
            {},// Unused_Protoss1,
            {},// Protoss_Observatory,
            {Protoss_Zealot, Protoss_Dragoon, Protoss_High_Templar, Protoss_Dark_Templar},// Protoss_Gateway,
            {},// Unused_Protoss2,
            {},// Protoss_Photon_Cannon,
            {},// Protoss_Citadel_of_Adun,
            {},// Protoss_Cybernetics_Core,
            {},// Protoss_Templar_Archives,
            {},// Protoss_Forge,
            {Protoss_Scout, Protoss_Carrier, Protoss_Arbiter, Protoss_Corsair},// Protoss_Stargate,
            {},// Special_Stasis_Cell_Prison,
            {},// Protoss_Fleet_Beacon,
            {},// Protoss_Arbiter_Tribunal,
            {},// Protoss_Robotics_Support_Bay,
            {},// Protoss_Shield_Battery,
            {},// Special_Khaydarin_Crystal_Form,
            {},// Special_Protoss_Temple,
            {},// Special_XelNaga_Temple,
            {},// Resource_Mineral_Field,
            {},// Resource_Mineral_Field_Type_2,
            {},// Resource_Mineral_Field_Type_3,
            {},// Unused_Cave,
            {},// Unused_Cave_In,
            {},// Unused_Cantina,
            {},// Unused_Mining_Platform,
            {},// Unused_Independant_Command_Center,
            {},// Special_Independant_Starport,
            {},// Unused_Independant_Jump_Gate,
            {},// Unused_Ruins,
            {},// Unused_Khaydarin_Crystal_Formation,
            {},// Resource_Vespene_Geyser,
            {},// Special_Warp_Gate,
            {},// Special_Psi_Disrupter,
            {},// Unused_Zerg_Marker,
            {},// Unused_Terran_Marker,
            {},// Unused_Protoss_Marker,
            {},// Special_Zerg_Beacon,
            {},// Special_Terran_Beacon,
            {},// Special_Protoss_Beacon,
            {},// Special_Zerg_Flag_Beacon,
            {},// Special_Terran_Flag_Beacon,
            {},// Special_Protoss_Flag_Beacon,
            {},// Special_Power_Generator,
            {},// Special_Overmind_Cocoon,
            {},// Spell_Dark_Swarm,
            {},// Special_Floor_Missile_Trap,
            {},// Special_Floor_Hatch,
            {},// Special_Upper_Level_Door,
            {},// Special_Right_Upper_Level_Door,
            {},// Special_Pit_Door,
            {},// Special_Right_Pit_Door,
            {},// Special_Floor_Gun_Trap,
            {},// Special_Wall_Missile_Trap,
            {},// Special_Wall_Flame_Trap,
            {},// Special_Right_Wall_Missile_Trap,
            {},// Special_Right_Wall_Flame_Trap,
            {},// Special_Start_Location,
            {},// Powerup_Flag,
            {},// Powerup_Young_Chrysalis,
            {},// Powerup_Psi_Emitter,
            {},// Powerup_Data_Disk,
            {},// Powerup_Khaydarin_Crystal,
            {},// Powerup_Mineral_Cluster_Type_1,
            {},// Powerup_Mineral_Cluster_Type_2,
            {},// Powerup_Protoss_Gas_Orb_Type_1,
            {},// Powerup_Protoss_Gas_Orb_Type_2,
            {},// Powerup_Zerg_Gas_Sac_Type_1,
            {},// Powerup_Zerg_Gas_Sac_Type_2,
            {},// Powerup_Terran_Gas_Tank_Type_1,
            {},// Powerup_Terran_Gas_Tank_Type_2,

            {},// None,
            {},// AllUnits,
            {},// Men,
            {},// Buildings,
            {},// Factories,
            {}// Unknown,
    };

    static int seekRangeTiles[] = {
            0, 0, 0, 5, 0, 8, 8, 1, 0, 0, 3, 0, 0, 3, 0, 0, 0, 5, 0, 0, 0, 0, 0, 8, 0, 0, 12, 0, 0, 0, 0, 12, 3, 0, 9, 0, 0, 3, 0, 3, 3, 0,
            0, 3, 0, 8, 0, 3, 3, 8, 3, 3, 0, 0, 3, 3, 0, 0, 0, 0, 9, 3, 7, 7, 0, 3, 0, 3, 3, 0, 0, 0, 8, 0, 3, 3, 3, 3, 0, 3, 0, 8, 8, 8, 0,
            3, 0, 3, 0, 0, 0, 8, 4, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 5, 5, 2, 5, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static int sightRangeTiles[] = {
            7, 9, 8, 8, 8, 10, 10, 7, 7, 10, 7, 8, 11, 3, 3, 7, 11, 8, 8, 8, 7, 7, 10, 10, 10, 10, 10, 8, 11, 11, 10, 10, 7, 10, 9, 4, 4, 5, 6,
            7, 5, 7, 9, 7, 11, 10, 10, 5, 7, 10, 5, 9, 10, 8, 5, 7, 11, 11, 8, 4, 9, 7, 10, 10, 8, 7, 8, 7, 8, 8, 8, 9, 11, 6, 7, 7, 8, 7, 8,
            7, 10, 10, 9, 10, 9, 5, 9, 7, 10, 7, 7, 8, 7, 7, 7, 7, 7, 4, 9, 10, 11, 10, 11, 8, 11, 8, 10, 10, 8, 8, 8, 8, 8, 8, 10, 8, 10, 8,
            8, 8, 8, 8, 8, 8, 11, 10, 10, 8, 5, 5, 10, 9, 10, 11, 8, 8, 8, 8, 8, 8, 8, 8, 8, 10, 10, 7, 10, 8, 8, 7, 8, 8, 8, 7, 11, 10, 8, 10,
            7, 10, 10, 10, 11, 10, 10, 10, 10, 10, 8, 10, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8, 10, 8, 8, 8, 8, 8,
            8, 8, 8, 8, 8, 10, 8, 6, 7, 1, 1, 1, 1, 6, 6, 3, 6, 3, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 0, 0, 0
    };

    static WeaponType groundWeapon[] = {
            Gauss_Rifle, C_10_Canister_Rifle, Fragmentation_Grenade, Twin_Autocannons, Twin_Autocannons, Arclite_Cannon, Arclite_Cannon,
            Fusion_Cutter, Burst_Lasers, WeaponType.None, Flame_Thrower_Gui_Montag, WeaponType.None, ATS_Laser_Battery, WeaponType.Spider_Mines, WeaponType.None, WeaponType.None,
            C_10_Canister_Rifle_Sarah_Kerrigan, Twin_Autocannons_Alan_Schezar, Twin_Autocannons_Alan_Schezar, Fragmentation_Grenade_Jim_Raynor,
            Gauss_Rifle_Jim_Raynor, Burst_Lasers_Tom_Kazansky, WeaponType.None, Arclite_Cannon_Edmund_Duke, Arclite_Cannon_Edmund_Duke,
            Arclite_Shock_Cannon_Edmund_Duke, Arclite_Shock_Cannon_Edmund_Duke, ATS_Laser_Battery_Hero, ATS_Laser_Battery_Hyperion,
            ATS_Laser_Battery_Hero, Arclite_Shock_Cannon, Arclite_Shock_Cannon, Flame_Thrower, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, Claws, Needle_Spines,
            Kaiser_Blades, Toxic_Spores, Spines, WeaponType.None, Glave_Wurm, Acid_Spore, WeaponType.None, WeaponType.None, WeaponType.None, Kaiser_Blades_Torrasque, WeaponType.None,
            Suicide_Infested_Terran, Claws_Infested_Kerrigan, WeaponType.None, Needle_Spines_Hunter_Killer, Claws_Devouring_One, Glave_Wurm_Kukulza,
            Acid_Spore_Kukulza, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, Warp_Blades, WeaponType.None, WeaponType.None, Particle_Beam, Psi_Blades, Phase_Disruptor, WeaponType.None,
            Psionic_Shockwave, WeaponType.None, Dual_Photon_Blasters, Phase_Disruptor_Cannon, WeaponType.None, Pulse_Cannon, Warp_Blades_Hero, Warp_Blades_Zeratul,
            Psionic_Shockwave_TZ_Archon, Psi_Blades_Fenix, Phase_Disruptor_Fenix, Psi_Assault, Dual_Photon_Blasters_Mojo, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, Scarab, Phase_Disruptor_Cannon_Danimoth, Psi_Assault, Dual_Photon_Blasters_Artanis, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, C_10_Canister_Rifle_Samir_Duran, C_10_Canister_Rifle_Alexei_Stukov, WeaponType.None, ATS_Laser_Battery_Hero,
            Subterranean_Spines, C_10_Canister_Rifle_Infested_Duran, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, Subterranean_Tentacle, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, STS_Photon_Cannon, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, Platform_Laser_Battery, WeaponType.None, Independant_Laser_Battery, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, Hellfire_Missile_Pack_Floor_Trap, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            Twin_Autocannons_Floor_Trap, Hellfire_Missile_Pack_Wall_Trap, Flame_Thrower_Wall_Trap, Hellfire_Missile_Pack_Wall_Trap,
            Flame_Thrower_Wall_Trap, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.Unknown
    };

    static WeaponType airWeapon[] = {
            Gauss_Rifle, C_10_Canister_Rifle, WeaponType.None, Hellfire_Missile_Pack, Hellfire_Missile_Pack, WeaponType.None, WeaponType.None, WeaponType.None, Gemini_Missiles, WeaponType.None, WeaponType.None, WeaponType.None,
            ATA_Laser_Battery, WeaponType.None, WeaponType.None, WeaponType.None, C_10_Canister_Rifle_Sarah_Kerrigan, Hellfire_Missile_Pack_Alan_Schezar, Hellfire_Missile_Pack_Alan_Schezar, WeaponType.None,
            Gauss_Rifle_Jim_Raynor, Gemini_Missiles_Tom_Kazansky, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, ATA_Laser_Battery_Hero,
            ATA_Laser_Battery_Hyperion, ATA_Laser_Battery_Hero, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, Needle_Spines, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, Glave_Wurm, WeaponType.None, WeaponType.None, WeaponType.None, Suicide_Scourge, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, Needle_Spines_Hunter_Killer,
            WeaponType.None, Glave_Wurm_Kukulza, WeaponType.None, WeaponType.None, Halo_Rockets, WeaponType.None, Neutron_Flare, WeaponType.None, Corrosive_Acid, WeaponType.None, WeaponType.None, WeaponType.None,
            Phase_Disruptor, WeaponType.None, Psionic_Shockwave, WeaponType.None, Anti_Matter_Missiles, Phase_Disruptor_Cannon, WeaponType.None, Pulse_Cannon, WeaponType.None,
            WeaponType.None, Psionic_Shockwave_TZ_Archon, WeaponType.None, Phase_Disruptor_Fenix, WeaponType.None, Anti_Matter_Missiles_Mojo, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, Phase_Disruptor_Cannon_Danimoth, WeaponType.None, Anti_Matter_Missiles_Artanis, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, Neutron_Flare, C_10_Canister_Rifle_Samir_Duran, C_10_Canister_Rifle_Alexei_Stukov, WeaponType.None, ATA_Laser_Battery_Hero, WeaponType.None,
            C_10_Canister_Rifle_Infested_Duran, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, Longbolt_Missile, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, Seeker_Spores, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, STA_Photon_Cannon, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.Unknown
    };

    static int groundWeaponHits[] = {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 3, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0,
            1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 2, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 2, 1, 1, 1, 0, 0, 0, 0, 1,
            1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static int airWeaponHits[] = {
            1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
            1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 4, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0,
            1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static UnitSizeType unitSize[] = {
            Small, Small, Medium, Large, Independent, Large, Independent, Small, Large, Large, Small, Large, Large, Small, Independent,
            Small, Small, Large, Independent, Medium, Small, Large, Large, Large, Independent, Large, Independent, Large, Large, Large,
            Large, Independent, Small, Independent, Small, Small, Medium, Small, Medium, Large, Small, Small, Large, Small, Large, Medium,
            Medium, Small, Large, Medium, Small, Small, Medium, Medium, Small, Small, Large, Large, Large, Large, Medium, Small, Large, Large,
            Small, Small, Large, Small, Large, Large, Large, Large, Large, Small, Small, Small, Large, Small, Large, Small, Large, Large,
            Large, Large, Small, Small, Large, Small, Large, Small, Small, Medium, Medium, Small, Small, Small, Small, Medium, Medium, Small,
            Small, Independent, Large, Medium, Small, Independent, Large, Large, Large, Large, Large, Large, Large, Large, Large, Large, Large,
            Large, Large, Independent, Large, Large, Large, Large, Large, Large, Large, Large, Independent, Independent, Large, Large, Large,
            Large, Large, Large, Large, Large, Large, Large, Large, Large, Large, Large, Large, Independent, Large, Large, Large, Large, Large,
            Large, Large, Independent, Large, Large, Large, Large, Independent, Large, Large, Large, Large, Large, Large, Large, Large, Large,
            Large, Large, Large, Large, Large, Large, Large, Large, Independent, Independent, Independent, Independent, Independent, Independent,
            Independent, Independent, Independent, Independent, Independent, Independent, Independent, Large, Large, Independent, Independent,
            Independent, Independent, Independent, Independent, Independent, Independent, Independent, Large, Large, Independent, Large,
            Independent, Independent, Independent, Independent, Independent, Large, Large, Large, Large, Large, Independent, Independent,
            Independent, Independent, Independent, Independent, Independent, Independent, Independent, Independent, Independent, Independent,
            Independent, Independent, UnitSizeType.None, UnitSizeType.None, UnitSizeType.None, UnitSizeType.None, UnitSizeType.None, UnitSizeType.Unknown
    };

    static double unitTopSpeeds[] = {
            4.0, 4.0, 6.4, 4.57, 0.0, 4.0, 0.0, 4.92, 6.67, 5.0, 4.0, 5.47, 2.5, 16.0, 33.33, 4.0, 4.0, 4.57, 0.0, 6.4, 4.0, 6.67,
            5.0, 4.0, 0.0, 0.0, 0.0, 2.5, 2.5, 2.5, 0.0, 0.0, 4.0, 0.0, 4.0, 0.0, 0.0, 5.49, 3.66, 5.12, 6.0, 4.92, 0.83, 6.67, 2.5,
            6.67, 4.0, 6.67, 5.12, 6.67, 5.82, 4.0, 4.0, 3.66, 5.49, 6.67, 2.5, 0.83, 6.6, 0.0, 6.67, 4.92, 5.0, 4.92, 4.92, 4.0, 5.0,
            3.2, 4.92, 4.43, 5.0, 5.0, 3.33, 13.33, 4.92, 4.92, 4.92, 4.0, 5.0, 3.2, 5.0, 1.78, 3.33, 1.78, 3.33, 16.0, 5.0, 3.2, 5.0,
            4.0, 4.0, 0.0, 0.0, 4.0, 5.0, 4.0, 4.0, 0.0, 6.67, 4.0, 4.0, 0.0, 2.5, 5.82, 4.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
            1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
    };

    static int unitAcceleration[] = {
            1, 1, 100, 1, 0, 1, 0, 67, 67, 50, 1, 17, 27, 1, 33, 1, 1, 1, 0, 100, 1, 67, 50, 1, 0, 1, 0, 27, 27, 27, 1, 0, 1, 0, 1, 1, 0, 1,
            1, 1, 1, 67, 27, 67, 27, 67, 1, 107, 1, 67, 1, 1, 1, 1, 1, 67, 27, 27, 65, 0, 67, 27, 48, 160, 67, 1, 1, 27, 160, 17, 48, 33, 27,
            427, 27, 27, 160, 1, 1, 27, 48, 1, 27, 1, 27, 1, 33, 27, 48, 1, 1, 0, 0, 1, 16, 1, 1, 0, 67, 1, 1, 0, 27, 1, 1, 0, 33, 0, 0, 0,
            33, 33, 0, 33, 33, 0, 33, 0, 0, 0, 0, 0, 33, 0, 0, 33, 0, 0, 0, 0, 33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static int unitHaltDistance[] = {
            1, 1, 14569, 1, 0, 1, 0, 12227, 21745, 5120, 1, 37756, 7585, 1, 1103213, 1, 1, 1, 0, 14569, 1, 21745, 5120, 1, 0, 1, 0,
            7585, 7585, 7585, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 12227, 840, 21745, 7585, 21745, 1, 13616, 1, 21745, 1, 1, 1, 1, 1, 21745,
            7585, 840, 21901, 0, 17067, 13474, 17067, 5120, 12227, 1, 1, 13474, 5120, 37756, 17067, 24824, 13474, 13640, 13474, 13474,
            5120, 1, 1, 13474, 17067, 1, 13474, 1, 13474, 1, 24824, 13474, 17067, 1, 1, 0, 0, 1, 51200, 1, 1, 0, 17067, 1, 1, 0, 7585,
            1, 1, 0, 2763, 0, 0, 0, 2763, 2763, 0, 2763, 2763, 0, 2763, 0, 0, 0, 0, 0, 2763, 0, 0, 2763, 0, 0, 0, 0, 2763, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static int unitTurnRadius[] = {
            40, 40, 40, 17, 0, 13, 0, 40, 40, 40, 40, 20, 20, 127, 127, 40, 40, 17, 0, 40, 40, 40, 40, 13, 0, 40, 0, 20, 20, 20, 40, 0, 40,
            0, 40, 20, 0, 27, 27, 40, 27, 40, 20, 40, 20, 40, 27, 40, 40, 40, 40, 40, 27, 27, 27, 40, 20, 20, 30, 0, 30, 40, 30, 40, 40, 40,
            40, 40, 40, 20, 30, 40, 20, 40, 40, 40, 40, 40, 40, 40, 30, 20, 20, 20, 20, 27, 40, 40, 30, 27, 27, 0, 0, 27, 14, 27, 27, 0, 30,
            40, 40, 0, 20, 40, 40, 0, 27, 0, 0, 0, 27, 27, 0, 27, 27, 0, 27, 0, 0, 0, 0, 0, 27, 0, 40, 27, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    static UpgradeType infantry_no_wpnUpgrade[]    = { Terran_Infantry_Armor };
    static UpgradeType infantryUpgrade[]           = { Terran_Infantry_Armor, Terran_Infantry_Weapons };
    static UpgradeType marineUpgrade[]             = { Terran_Infantry_Armor, Terran_Infantry_Weapons, U_238_Shells };
    static UpgradeType ghostUpgrade[]              = { Terran_Infantry_Armor, Terran_Infantry_Weapons, Ocular_Implants, Moebius_Reactor };

    static UpgradeType mechUpgrade[]               = { Terran_Vehicle_Plating, Terran_Vehicle_Weapons };
    static UpgradeType vultureUpgrade[]            = { Terran_Vehicle_Plating, Terran_Vehicle_Weapons, Ion_Thrusters };
    static UpgradeType goliathUpgrade[]            = { Terran_Vehicle_Plating, Terran_Vehicle_Weapons, Charon_Boosters };

    static UpgradeType terran_air_no_wpnUpgrade[]  = { Terran_Ship_Plating };
    static UpgradeType terran_airUpgrade[]         = { Terran_Ship_Plating, Terran_Ship_Weapons };
    static UpgradeType science_vesselUpgrade[]     = { Terran_Ship_Plating, Titan_Reactor };
    static UpgradeType wraithUpgrade[]             = { Terran_Ship_Plating, Terran_Ship_Weapons, Apollo_Reactor };
    static UpgradeType battlecruiserUpgrade[]      = { Terran_Ship_Plating, Terran_Ship_Weapons, Colossus_Reactor };
    static UpgradeType medicUpgrade[]              = { Terran_Infantry_Armor, Caduceus_Reactor };

    static UpgradeType carapaceUpgrade[]           = { Zerg_Carapace };
    static UpgradeType zerg_meleeUpgrade[]         = { Zerg_Carapace, Zerg_Melee_Attacks };
    static UpgradeType zerg_rangeUpgrade[]         = { Zerg_Carapace, Zerg_Missile_Attacks };
    static UpgradeType zerglingUpgrade[]           = { Zerg_Carapace, Zerg_Melee_Attacks, Metabolic_Boost, Adrenal_Glands };
    static UpgradeType hydraliskUpgrade[]          = { Zerg_Carapace, Zerg_Missile_Attacks, Muscular_Augments, Grooved_Spines };
    static UpgradeType defilerUpgrade[]            = { Zerg_Carapace, Metasynaptic_Node };
    static UpgradeType ultraliskUpgrade[]          = { Zerg_Carapace, Zerg_Melee_Attacks, Chitinous_Plating, Anabolic_Synthesis };

    static UpgradeType zerg_air_no_wpnUpgrade[]    = { Zerg_Flyer_Carapace };
    static UpgradeType zerg_airUpgrade[]           = { Zerg_Flyer_Carapace, Zerg_Flyer_Attacks };
    static UpgradeType overlordUpgrade[]           = { Zerg_Flyer_Carapace, Ventral_Sacs, Antennae, Pneumatized_Carapace };
    static UpgradeType queenUpgrade[]              = { Zerg_Flyer_Carapace, Gamete_Meiosis };

    static UpgradeType shieldsUpgrade[]            = { Protoss_Plasma_Shields };
    static UpgradeType protoss_ground_no_wpnUpgrade[]  = { Protoss_Plasma_Shields, Protoss_Ground_Armor };
    static UpgradeType protoss_groundUpgrade[]     = { Protoss_Plasma_Shields, Protoss_Ground_Armor, Protoss_Ground_Weapons };
    static UpgradeType zealotUpgrade[]             = { Protoss_Plasma_Shields, Protoss_Ground_Armor, Protoss_Ground_Weapons, Leg_Enhancements };
    static UpgradeType dragoonUpgrade[]            = { Protoss_Plasma_Shields, Protoss_Ground_Armor, Protoss_Ground_Weapons, Singularity_Charge };
    static UpgradeType reaverUpgrade[]             = { Protoss_Plasma_Shields, Protoss_Ground_Armor, Protoss_Ground_Weapons, Scarab_Damage, Reaver_Capacity };
    static UpgradeType templarUpgrade[]            = { Protoss_Plasma_Shields, Protoss_Ground_Armor, Khaydarin_Amulet };

    static UpgradeType protoss_air_no_wpnUpgrade[] = { Protoss_Plasma_Shields, Protoss_Air_Armor };
    static UpgradeType protoss_airUpgrade[]        = { Protoss_Plasma_Shields, Protoss_Air_Armor, Protoss_Air_Weapons };
    static UpgradeType shuttleUpgrade[]            = { Protoss_Plasma_Shields, Protoss_Air_Armor, Gravitic_Drive };
    static UpgradeType observerUpgrade[]           = { Protoss_Plasma_Shields, Protoss_Air_Armor, Sensor_Array, Gravitic_Boosters };
    static UpgradeType scoutUpgrade[]              = { Protoss_Plasma_Shields, Protoss_Air_Armor, Protoss_Air_Weapons, Apial_Sensors, Gravitic_Thrusters };
    static UpgradeType carrierUpgrade[]            = { Protoss_Plasma_Shields, Protoss_Air_Armor, Protoss_Air_Weapons, Carrier_Capacity };
    static UpgradeType arbiterUpgrade[]            = { Protoss_Plasma_Shields, Protoss_Air_Armor, Protoss_Air_Weapons, Khaydarin_Core };
    static UpgradeType corsairUpgrade[]            = { Protoss_Plasma_Shields, Protoss_Air_Armor, Protoss_Air_Weapons, Argus_Jewel };
    static UpgradeType dark_archonUpgrade[]        = { Protoss_Plasma_Shields, Protoss_Air_Armor, Protoss_Air_Weapons, Argus_Talisman };

    static UpgradeType infested_duranUpgrade[]     = { Zerg_Carapace, Terran_Infantry_Weapons };
    static UpgradeType trapUpgrade[]               = { Terran_Vehicle_Weapons };
    static UpgradeType flame_trapUpgrade[]         = { Terran_Infantry_Weapons };

    static UpgradeType upgrades[][] = {
            marineUpgrade, ghostUpgrade, vultureUpgrade, goliathUpgrade, goliathUpgrade, mechUpgrade, mechUpgrade, infantry_no_wpnUpgrade,
            wraithUpgrade, science_vesselUpgrade, infantryUpgrade, terran_air_no_wpnUpgrade, battlecruiserUpgrade, {}, {},
            infantry_no_wpnUpgrade, infantryUpgrade, mechUpgrade, mechUpgrade, mechUpgrade, infantryUpgrade, terran_airUpgrade,
            terran_air_no_wpnUpgrade, mechUpgrade, mechUpgrade, mechUpgrade, mechUpgrade, terran_airUpgrade, terran_airUpgrade,
            terran_airUpgrade, mechUpgrade, mechUpgrade, infantryUpgrade, {}, medicUpgrade, carapaceUpgrade, carapaceUpgrade,
            zerglingUpgrade, hydraliskUpgrade, ultraliskUpgrade, zerg_meleeUpgrade, carapaceUpgrade, overlordUpgrade, zerg_airUpgrade,
            zerg_airUpgrade, queenUpgrade, defilerUpgrade, zerg_air_no_wpnUpgrade, zerg_meleeUpgrade, zerg_air_no_wpnUpgrade, carapaceUpgrade,
            zerg_meleeUpgrade, carapaceUpgrade, zerg_rangeUpgrade, zerg_meleeUpgrade, zerg_airUpgrade, zerg_airUpgrade, zerg_air_no_wpnUpgrade,
            terran_airUpgrade, carapaceUpgrade, corsairUpgrade, protoss_groundUpgrade, zerg_airUpgrade, dark_archonUpgrade,
            protoss_ground_no_wpnUpgrade, zealotUpgrade, dragoonUpgrade, templarUpgrade, protoss_groundUpgrade, shuttleUpgrade, scoutUpgrade,
            arbiterUpgrade, carrierUpgrade, protoss_airUpgrade, protoss_groundUpgrade, protoss_groundUpgrade, protoss_groundUpgrade,
            protoss_groundUpgrade, protoss_groundUpgrade, protoss_groundUpgrade, protoss_airUpgrade, reaverUpgrade, protoss_airUpgrade,
            reaverUpgrade, observerUpgrade, protoss_groundUpgrade, protoss_airUpgrade, protoss_groundUpgrade, protoss_airUpgrade, {},
            {}, {}, {}, {}, {}, {}, {}, carapaceUpgrade, protoss_airUpgrade, infantryUpgrade,
            infantryUpgrade, {}, terran_airUpgrade, zerg_rangeUpgrade, infested_duranUpgrade, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, shieldsUpgrade, shieldsUpgrade, shieldsUpgrade, shieldsUpgrade, shieldsUpgrade, shieldsUpgrade, shieldsUpgrade,
            shieldsUpgrade, shieldsUpgrade, shieldsUpgrade, shieldsUpgrade, shieldsUpgrade, shieldsUpgrade, shieldsUpgrade, {},
            shieldsUpgrade, shieldsUpgrade, shieldsUpgrade, shieldsUpgrade, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, trapUpgrade, {}, {}, {}, {}, {}, trapUpgrade, trapUpgrade, flame_trapUpgrade,
            trapUpgrade, flame_trapUpgrade, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}
    };

    static UpgradeType upgradesWhat[][] = {
            {},// Terran_Marine = 0,
            {},// Terran_Ghost,
            {},// Terran_Vulture,
            {},// Terran_Goliath,
            {},// Terran_Goliath_Turret,
            {},// Terran_Siege_Tank_Tank_Mode,
            {},// Terran_Siege_Tank_Tank_Mode_Turret,
            {},// Terran_SCV,
            {},// Terran_Wraith,
            {},// Terran_Science_Vessel,
            {},// Hero_Gui_Montag,
            {},// Terran_Dropship,
            {},// Terran_Battlecruiser,
            {},// Terran_Vulture_Spider_Mine,
            {},// Terran_Nuclear_Missile,
            {},// Terran_Civilian,
            {},// Hero_Sarah_Kerrigan,
            {},// Hero_Alan_Schezar,
            {},// Hero_Alan_Schezar_Turret,
            {},// Hero_Jim_Raynor_Vulture,
            {},// Hero_Jim_Raynor_Marine,
            {},// Hero_Tom_Kazansky,
            {},// Hero_Magellan,
            {},// Hero_Edmund_Duke_Tank_Mode,
            {},// Hero_Edmund_Duke_Tank_Mode_Turret,
            {},// Hero_Edmund_Duke_Siege_Mode,
            {},// Hero_Edmund_Duke_Siege_Mode_Turret,
            {},// Hero_Arcturus_Mengsk,
            {},// Hero_Hyperion,
            {},// Hero_Norad_II,
            {},// Terran_Siege_Tank_Siege_Mode,
            {},// Terran_Siege_Tank_Siege_Mode_Turret,
            {},// Terran_Firebat,
            {},// Spell_Scanner_Sweep,
            {},// Terran_Medic,
            {},// Zerg_Larva,
            {},// Zerg_Egg,
            {},// Zerg_Zergling,
            {},// Zerg_Hydralisk,
            {},// Zerg_Ultralisk,
            {},// Zerg_Broodling,
            {},// Zerg_Drone,
            {},// Zerg_Overlord,
            {},// Zerg_Mutalisk,
            {},// Zerg_Guardian,
            {},// Zerg_Queen,
            {},// Zerg_Defiler,
            {},// Zerg_Scourge,
            {},// Hero_Torrasque,
            {},// Hero_Matriarch,
            {},// Zerg_Infested_Terran,
            {},// Hero_Infested_Kerrigan,
            {},// Hero_Unclean_One,
            {},// Hero_Hunter_Killer,
            {},// Hero_Devouring_One,
            {},// Hero_Kukulza_Mutalisk,
            {},// Hero_Kukulza_Guardian,
            {},// Hero_Yggdrasill,
            {},// Terran_Valkyrie,
            {},// Zerg_Cocoon,
            {},// Protoss_Corsair,
            {},// Protoss_Dark_Templar,
            {},// Zerg_Devourer,
            {},// Protoss_Dark_Archon,
            {},// Protoss_Probe,
            {},// Protoss_Zealot,
            {},// Protoss_Dragoon,
            {},// Protoss_High_Templar,
            {},// Protoss_Archon,
            {},// Protoss_Shuttle,
            {},// Protoss_Scout,
            {},// Protoss_Arbiter,
            {},// Protoss_Carrier,
            {},// Protoss_Interceptor,
            {},// Hero_Dark_Templar,
            {},// Hero_Zeratul,
            {},// Hero_Tassadar_Zeratul_Archon,
            {},// Hero_Fenix_Zealot,
            {},// Hero_Fenix_Dragoon,
            {},// Hero_Tassadar,
            {},// Hero_Mojo,
            {},// Hero_Warbringer,
            {},// Hero_Gantrithor,
            {},// Protoss_Reaver,
            {},// Protoss_Observer,
            {},// Protoss_Scarab,
            {},// Hero_Danimoth,
            {},// Hero_Aldaris,
            {},// Hero_Artanis,
            {},// Critter_Rhynadon,
            {},// Critter_Bengalaas,
            {},// Special_Cargo_Ship,
            {},// Special_Mercenary_Gunship,
            {},// Critter_Scantid,
            {},// Critter_Kakaru,
            {},// Critter_Ragnasaur,
            {},// Critter_Ursadon,
            {},// Zerg_Lurker_Egg,
            {},// Hero_Raszagal,
            {},// Hero_Samir_Duran,
            {},// Hero_Alexei_Stukov,
            {},// Special_Map_Revealer,
            {},// Hero_Gerard_DuGalle,
            {},// Zerg_Lurker,
            {},// Hero_Infested_Duran,
            {},// Spell_Disruption_Web,
            {},// Terran_Command_Center,
            {},// Terran_Comsat_Station,
            {},// Terran_Nuclear_Silo,
            {},// Terran_Supply_Depot,
            {},// Terran_Refinery,
            {},// Terran_Barracks,
            {U_238_Shells, Caduceus_Reactor},// Terran_Academy,
            {},// Terran_Factory,
            {},// Terran_Starport,
            {Apollo_Reactor},// Terran_Control_Tower,
            {Titan_Reactor},// Terran_Science_Facility,
            {Ocular_Implants, Moebius_Reactor},// Terran_Covert_Ops,
            {Colossus_Reactor},// Terran_Physics_Lab,
            {},// Unused_Terran1,
            {Ion_Thrusters, Charon_Boosters},// Terran_Machine_Shop,
            {},// Unused_Terran2,
            {Terran_Infantry_Armor, Terran_Infantry_Weapons},// Terran_Engineering_Bay,
            {Terran_Vehicle_Plating, Terran_Vehicle_Weapons, Terran_Ship_Plating, Terran_Ship_Weapons},// Terran_Armory,
            {},// Terran_Missile_Turret,
            {},// Terran_Bunker,
            {},// Special_Crashed_Norad_II,
            {},// Special_Ion_Cannon,
            {},// Powerup_Uraj_Crystal,
            {},// Powerup_Khalis_Crystal,
            {},// Zerg_Infested_Command_Center,
            {},// Zerg_Hatchery,
            {Ventral_Sacs, Antennae, Pneumatized_Carapace},// Zerg_Lair,
            { Ventral_Sacs, Antennae, Pneumatized_Carapace },// Zerg_Hive,
            {},// Zerg_Nydus_Canal,
            {Muscular_Augments, Grooved_Spines},// Zerg_Hydralisk_Den,
            {Metasynaptic_Node},// Zerg_Defiler_Mound,
            {Zerg_Flyer_Attacks, Zerg_Flyer_Carapace},// Zerg_Greater_Spire,
            {Gamete_Meiosis},// Zerg_Queens_Nest,
            {Zerg_Melee_Attacks, Zerg_Missile_Attacks, Zerg_Carapace},// Zerg_Evolution_Chamber,
            {Anabolic_Synthesis, Chitinous_Plating},// Zerg_Ultralisk_Cavern,
            { Zerg_Flyer_Attacks, Zerg_Flyer_Carapace },// Zerg_Spire,
            {Metabolic_Boost, Adrenal_Glands},// Zerg_Spawning_Pool,
            {},// Zerg_Creep_Colony,
            {},// Zerg_Spore_Colony,
            {},// Unused_Zerg1,
            {},// Zerg_Sunken_Colony,
            {},// Special_Overmind_With_Shell,
            {},// Special_Overmind,
            {},// Zerg_Extractor,
            {},// Special_Mature_Chrysalis,
            {},// Special_Cerebrate,
            {},// Special_Cerebrate_Daggoth,
            {},// Unused_Zerg2,
            {},// Protoss_Nexus,
            {},// Protoss_Robotics_Facility,
            {},// Protoss_Pylon,
            {},// Protoss_Assimilator,
            {},// Unused_Protoss1,
            {Sensor_Array, Gravitic_Boosters},// Protoss_Observatory,
            {},// Protoss_Gateway,
            {},// Unused_Protoss2,
            {},// Protoss_Photon_Cannon,
            {Leg_Enhancements},// Protoss_Citadel_of_Adun,
            {Protoss_Air_Armor, Protoss_Air_Weapons, Singularity_Charge},// Protoss_Cybernetics_Core,
            {Khaydarin_Amulet, Argus_Talisman},// Protoss_Templar_Archives,
            {Protoss_Ground_Armor, Protoss_Ground_Weapons, Protoss_Plasma_Shields},// Protoss_Forge,
            {},// Protoss_Stargate,
            {},// Special_Stasis_Cell_Prison,
            {Carrier_Capacity, Apial_Sensors, Gravitic_Thrusters, Argus_Jewel},// Protoss_Fleet_Beacon,
            {Khaydarin_Core},// Protoss_Arbiter_Tribunal,
            {Scarab_Damage, Reaver_Capacity, Gravitic_Drive},// Protoss_Robotics_Support_Bay,
            {},// Protoss_Shield_Battery,
            {},// Special_Khaydarin_Crystal_Form,
            {},// Special_Protoss_Temple,
            {},// Special_XelNaga_Temple,
            {},// Resource_Mineral_Field,
            {},// Resource_Mineral_Field_Type_2,
            {},// Resource_Mineral_Field_Type_3,
            {},// Unused_Cave,
            {},// Unused_Cave_In,
            {},// Unused_Cantina,
            {},// Unused_Mining_Platform,
            {},// Unused_Independant_Command_Center,
            {},// Special_Independant_Starport,
            {},// Unused_Independant_Jump_Gate,
            {},// Unused_Ruins,
            {},// Unused_Khaydarin_Crystal_Formation,
            {},// Resource_Vespene_Geyser,
            {},// Special_Warp_Gate,
            {},// Special_Psi_Disrupter,
            {},// Unused_Zerg_Marker,
            {},// Unused_Terran_Marker,
            {},// Unused_Protoss_Marker,
            {},// Special_Zerg_Beacon,
            {},// Special_Terran_Beacon,
            {},// Special_Protoss_Beacon,
            {},// Special_Zerg_Flag_Beacon,
            {},// Special_Terran_Flag_Beacon,
            {},// Special_Protoss_Flag_Beacon,
            {},// Special_Power_Generator,
            {},// Special_Overmind_Cocoon,
            {},// Spell_Dark_Swarm,
            {},// Special_Floor_Missile_Trap,
            {},// Special_Floor_Hatch,
            {},// Special_Upper_Level_Door,
            {},// Special_Right_Upper_Level_Door,
            {},// Special_Pit_Door,
            {},// Special_Right_Pit_Door,
            {},// Special_Floor_Gun_Trap,
            {},// Special_Wall_Missile_Trap,
            {},// Special_Wall_Flame_Trap,
            {},// Special_Right_Wall_Missile_Trap,
            {},// Special_Right_Wall_Flame_Trap,
            {},// Special_Start_Location,
            {},// Powerup_Flag,
            {},// Powerup_Young_Chrysalis,
            {},// Powerup_Psi_Emitter,
            {},// Powerup_Data_Disk,
            {},// Powerup_Khaydarin_Crystal,
            {},// Powerup_Mineral_Cluster_Type_1,
            {},// Powerup_Mineral_Cluster_Type_2,
            {},// Powerup_Protoss_Gas_Orb_Type_1,
            {},// Powerup_Protoss_Gas_Orb_Type_2,
            {},// Powerup_Zerg_Gas_Sac_Type_1,
            {},// Powerup_Zerg_Gas_Sac_Type_2,
            {},// Powerup_Terran_Gas_Tank_Type_1,
            {},// Powerup_Terran_Gas_Tank_Type_2,

            {},// None,
            {},// AllUnits,
            {},// Men,
            {},// Buildings,
            {},// Factories,
            {}// Unknown,
    };

    static TechType stimTech[] = { Stim_Packs };
    static TechType ghost_fullTech[] = { TechType.Lockdown, Personnel_Cloaking, TechType.Nuclear_Strike };
    static TechType vultureTech[] = { TechType.Spider_Mines };
    static TechType siegeTech[] = { Tank_Siege_Mode };
    static TechType wraithTech[] = { Cloaking_Field };
    static TechType vesselTech[] = { TechType.EMP_Shockwave, Defensive_Matrix, TechType.Irradiate };
    static TechType cruiserTech[] = { TechType.Yamato_Gun };
    static TechType ghost_heroTech[] = { TechType.Lockdown, Personnel_Cloaking };
    static TechType medicTech[] = { TechType.Restoration, TechType.Optical_Flare, Healing };
    static TechType burrowTech[] = { Burrowing };
    static TechType queenTech[] = { Infestation, TechType.Spawn_Broodlings, TechType.Ensnare, TechType.Parasite };
    static TechType defilerTech[] = { Burrowing, TechType.Dark_Swarm, TechType.Plague, TechType.Consume };
    static TechType ikerriganTech[] = { Personnel_Cloaking, TechType.Ensnare, TechType.Psionic_Storm, TechType.Consume };
    static TechType corsairTech[] = { TechType.Disruption_Web };
    static TechType dtTech[] = { Dark_Archon_Meld };
    static TechType darchonTech[] = { TechType.Mind_Control, TechType.Feedback, TechType.Maelstrom };
    static TechType templar_fullTech[] = { TechType.Psionic_Storm, Hallucination, Archon_Warp };
    static TechType arbiterTech[] = { Recall, TechType.Stasis_Field };
    static TechType templar_heroTech[] = { TechType.Psionic_Storm, Hallucination };
    static TechType iduranTech[] = { TechType.Lockdown, Personnel_Cloaking, TechType.Consume };
    static TechType comsatTech[] = { Scanner_Sweep };

    static TechType unitTechs[][] = {
            stimTech, ghost_fullTech, vultureTech, {}, {}, siegeTech, {}, {}, wraithTech, vesselTech,
            stimTech, {}, cruiserTech, {}, {}, {}, ghost_heroTech, {}, {}, vultureTech,
            stimTech, wraithTech, vesselTech, siegeTech, {}, siegeTech, {}, {}, cruiserTech, cruiserTech,
            siegeTech, {}, stimTech, {}, medicTech, {}, {}, burrowTech, burrowTech, {}, {},
            burrowTech, {}, {}, {}, queenTech, defilerTech, {}, {}, queenTech, burrowTech,
            ikerriganTech, defilerTech, burrowTech, burrowTech, {}, {}, {}, {}, {}, corsairTech,
            dtTech, {}, darchonTech, {}, {}, {}, templar_fullTech, {}, {}, {},
            arbiterTech, {}, {}, {}, {}, {}, {}, {}, templar_heroTech, {}, {},
            {}, {}, {}, {}, arbiterTech, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, corsairTech, ghost_heroTech, ghost_heroTech, {}, cruiserTech,
            burrowTech, iduranTech, {}, {}, comsatTech, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}
    };

    static TechType researchesWhat[][] = {
            {},// Terran_Marine = 0,
            {},// Terran_Ghost,
            {},// Terran_Vulture,
            {},// Terran_Goliath,
            {},// Terran_Goliath_Turret,
            {},// Terran_Siege_Tank_Tank_Mode,
            {},// Terran_Siege_Tank_Tank_Mode_Turret,
            {},// Terran_SCV,
            {},// Terran_Wraith,
            {},// Terran_Science_Vessel,
            {},// Hero_Gui_Montag,
            {},// Terran_Dropship,
            {},// Terran_Battlecruiser,
            {},// Terran_Vulture_Spider_Mine,
            {},// Terran_Nuclear_Missile,
            {},// Terran_Civilian,
            {},// Hero_Sarah_Kerrigan,
            {},// Hero_Alan_Schezar,
            {},// Hero_Alan_Schezar_Turret,
            {},// Hero_Jim_Raynor_Vulture,
            {},// Hero_Jim_Raynor_Marine,
            {},// Hero_Tom_Kazansky,
            {},// Hero_Magellan,
            {},// Hero_Edmund_Duke_Tank_Mode,
            {},// Hero_Edmund_Duke_Tank_Mode_Turret,
            {},// Hero_Edmund_Duke_Siege_Mode,
            {},// Hero_Edmund_Duke_Siege_Mode_Turret,
            {},// Hero_Arcturus_Mengsk,
            {},// Hero_Hyperion,
            {},// Hero_Norad_II,
            {},// Terran_Siege_Tank_Siege_Mode,
            {},// Terran_Siege_Tank_Siege_Mode_Turret,
            {},// Terran_Firebat,
            {},// Spell_Scanner_Sweep,
            {},// Terran_Medic,
            {},// Zerg_Larva,
            {},// Zerg_Egg,
            {},// Zerg_Zergling,
            {},// Zerg_Hydralisk,
            {},// Zerg_Ultralisk,
            {},// Zerg_Broodling,
            {},// Zerg_Drone,
            {},// Zerg_Overlord,
            {},// Zerg_Mutalisk,
            {},// Zerg_Guardian,
            {},// Zerg_Queen,
            {},// Zerg_Defiler,
            {},// Zerg_Scourge,
            {},// Hero_Torrasque,
            {},// Hero_Matriarch,
            {},// Zerg_Infested_Terran,
            {},// Hero_Infested_Kerrigan,
            {},// Hero_Unclean_One,
            {},// Hero_Hunter_Killer,
            {},// Hero_Devouring_One,
            {},// Hero_Kukulza_Mutalisk,
            {},// Hero_Kukulza_Guardian,
            {},// Hero_Yggdrasill,
            {},// Terran_Valkyrie,
            {},// Zerg_Cocoon,
            {},// Protoss_Corsair,
            {},// Protoss_Dark_Templar,
            {},// Zerg_Devourer,
            {},// Protoss_Dark_Archon,
            {},// Protoss_Probe,
            {},// Protoss_Zealot,
            {},// Protoss_Dragoon,
            {},// Protoss_High_Templar,
            {},// Protoss_Archon,
            {},// Protoss_Shuttle,
            {},// Protoss_Scout,
            {},// Protoss_Arbiter,
            {},// Protoss_Carrier,
            {},// Protoss_Interceptor,
            {},// Hero_Dark_Templar,
            {},// Hero_Zeratul,
            {},// Hero_Tassadar_Zeratul_Archon,
            {},// Hero_Fenix_Zealot,
            {},// Hero_Fenix_Dragoon,
            {},// Hero_Tassadar,
            {},// Hero_Mojo,
            {},// Hero_Warbringer,
            {},// Hero_Gantrithor,
            {},// Protoss_Reaver,
            {},// Protoss_Observer,
            {},// Protoss_Scarab,
            {},// Hero_Danimoth,
            {},// Hero_Aldaris,
            {},// Hero_Artanis,
            {},// Critter_Rhynadon,
            {},// Critter_Bengalaas,
            {},// Special_Cargo_Ship,
            {},// Special_Mercenary_Gunship,
            {},// Critter_Scantid,
            {},// Critter_Kakaru,
            {},// Critter_Ragnasaur,
            {},// Critter_Ursadon,
            {},// Zerg_Lurker_Egg,
            {},// Hero_Raszagal,
            {},// Hero_Samir_Duran,
            {},// Hero_Alexei_Stukov,
            {},// Special_Map_Revealer,
            {},// Hero_Gerard_DuGalle,
            {},// Zerg_Lurker,
            {},// Hero_Infested_Duran,
            {},// Spell_Disruption_Web,
            {},// Terran_Command_Center,
            {},// Terran_Comsat_Station,
            {},// Terran_Nuclear_Silo,
            {},// Terran_Supply_Depot,
            {},// Terran_Refinery,
            {},// Terran_Barracks,
            {Stim_Packs, TechType.Restoration, TechType.Optical_Flare},// Terran_Academy,
            {},// Terran_Factory,
            {},// Terran_Starport,
            {Cloaking_Field},// Terran_Control_Tower,
            {TechType.EMP_Shockwave, TechType.Irradiate},// Terran_Science_Facility,
            {Personnel_Cloaking, TechType.Lockdown},// Terran_Covert_Ops,
            {TechType.Yamato_Gun},// Terran_Physics_Lab,
            {},// Unused_Terran1,
            {Tank_Siege_Mode, TechType.Spider_Mines},// Terran_Machine_Shop,
            {},// Unused_Terran2,
            {},// Terran_Engineering_Bay,
            {},// Terran_Armory,
            {},// Terran_Missile_Turret,
            {},// Terran_Bunker,
            {},// Special_Crashed_Norad_II,
            {},// Special_Ion_Cannon,
            {},// Powerup_Uraj_Crystal,
            {},// Powerup_Khalis_Crystal,
            {},// Zerg_Infested_Command_Center,
            {Burrowing},// Zerg_Hatchery,
            { Burrowing },// Zerg_Lair,
            { Burrowing },// Zerg_Hive,
            {},// Zerg_Nydus_Canal,
            {Lurker_Aspect},// Zerg_Hydralisk_Den,
            {TechType.Consume, TechType.Plague},// Zerg_Defiler_Mound,
            {},// Zerg_Greater_Spire,
            {TechType.Ensnare, TechType.Spawn_Broodlings},// Zerg_Queens_Nest,
            {},// Zerg_Evolution_Chamber,
            {},// Zerg_Ultralisk_Cavern,
            {},// Zerg_Spire,
            {},// Zerg_Spawning_Pool,
            {},// Zerg_Creep_Colony,
            {},// Zerg_Spore_Colony,
            {},// Unused_Zerg1,
            {},// Zerg_Sunken_Colony,
            {},// Special_Overmind_With_Shell,
            {},// Special_Overmind,
            {},// Zerg_Extractor,
            {},// Special_Mature_Chrysalis,
            {},// Special_Cerebrate,
            {},// Special_Cerebrate_Daggoth,
            {},// Unused_Zerg2,
            {},// Protoss_Nexus,
            {},// Protoss_Robotics_Facility,
            {},// Protoss_Pylon,
            {},// Protoss_Assimilator,
            {},// Unused_Protoss1,
            {},// Protoss_Observatory,
            {},// Protoss_Gateway,
            {},// Unused_Protoss2,
            {},// Protoss_Photon_Cannon,
            {},// Protoss_Citadel_of_Adun,
            {},// Protoss_Cybernetics_Core,
            {TechType.Psionic_Storm, Hallucination, TechType.Maelstrom, TechType.Mind_Control},// Protoss_Templar_Archives,
            {},// Protoss_Forge,
            {},// Protoss_Stargate,
            {},// Special_Stasis_Cell_Prison,
            {TechType.Disruption_Web},// Protoss_Fleet_Beacon,
            {TechType.Stasis_Field, Recall},// Protoss_Arbiter_Tribunal,
            {},// Protoss_Robotics_Support_Bay,
            {},// Protoss_Shield_Battery,
            {},// Special_Khaydarin_Crystal_Form,
            {},// Special_Protoss_Temple,
            {},// Special_XelNaga_Temple,
            {},// Resource_Mineral_Field,
            {},// Resource_Mineral_Field_Type_2,
            {},// Resource_Mineral_Field_Type_3,
            {},// Unused_Cave,
            {},// Unused_Cave_In,
            {},// Unused_Cantina,
            {},// Unused_Mining_Platform,
            {},// Unused_Independant_Command_Center,
            {},// Special_Independant_Starport,
            {},// Unused_Independant_Jump_Gate,
            {},// Unused_Ruins,
            {},// Unused_Khaydarin_Crystal_Formation,
            {},// Resource_Vespene_Geyser,
            {},// Special_Warp_Gate,
            {},// Special_Psi_Disrupter,
            {},// Unused_Zerg_Marker,
            {},// Unused_Terran_Marker,
            {},// Unused_Protoss_Marker,
            {},// Special_Zerg_Beacon,
            {},// Special_Terran_Beacon,
            {},// Special_Protoss_Beacon,
            {},// Special_Zerg_Flag_Beacon,
            {},// Special_Terran_Flag_Beacon,
            {},// Special_Protoss_Flag_Beacon,
            {},// Special_Power_Generator,
            {},// Special_Overmind_Cocoon,
            {},// Spell_Dark_Swarm,
            {},// Special_Floor_Missile_Trap,
            {},// Special_Floor_Hatch,
            {},// Special_Upper_Level_Door,
            {},// Special_Right_Upper_Level_Door,
            {},// Special_Pit_Door,
            {},// Special_Right_Pit_Door,
            {},// Special_Floor_Gun_Trap,
            {},// Special_Wall_Missile_Trap,
            {},// Special_Wall_Flame_Trap,
            {},// Special_Right_Wall_Missile_Trap,
            {},// Special_Right_Wall_Flame_Trap,
            {},// Special_Start_Location,
            {},// Powerup_Flag,
            {},// Powerup_Young_Chrysalis,
            {},// Powerup_Psi_Emitter,
            {},// Powerup_Data_Disk,
            {},// Powerup_Khaydarin_Crystal,
            {},// Powerup_Mineral_Cluster_Type_1,
            {},// Powerup_Mineral_Cluster_Type_2,
            {},// Powerup_Protoss_Gas_Orb_Type_1,
            {},// Powerup_Protoss_Gas_Orb_Type_2,
            {},// Powerup_Zerg_Gas_Sac_Type_1,
            {},// Powerup_Zerg_Gas_Sac_Type_2,
            {},// Powerup_Terran_Gas_Tank_Type_1,
            {},// Powerup_Terran_Gas_Tank_Type_2,

            {},// None,
            {},// AllUnits,
            {},// Men,
            {},// Buildings,
            {},// Factories,
            {}// Unknown,
    };

    static List<Map<UnitType, Integer>> reqUnitsMap = reqUnitsInit();

    private static List<Map<UnitType, Integer>> reqUnitsInit() {
        List<Map<UnitType, Integer>> req = new ArrayList<>(UnitType.values().length);
        // Add the whatBuilds types to the required units map
        for (UnitType i : UnitType.values()) {
            req.add(new HashMap<>());
            Map.Entry<UnitType, Integer> wb = i.whatBuilds();
            if ( wb.getKey() != UnitType.None ) {
                req.get(i.id).put(wb.getKey(), wb.getValue());
            }
        }

        req.get(Terran_Ghost.id).put(Terran_Academy, 1);
        req.get(Terran_Ghost.id).put(Terran_Covert_Ops, 1);
        req.get(Terran_Goliath.id).put(Terran_Armory, 1);
        req.get(Terran_Siege_Tank_Tank_Mode.id).put(Terran_Machine_Shop, 1);
        req.get(Terran_Science_Vessel.id).put(Terran_Control_Tower, 1);
        req.get(Terran_Science_Vessel.id).put(Terran_Science_Facility, 1);
        req.get(Terran_Dropship.id).put(Terran_Control_Tower, 1);
        req.get(Terran_Battlecruiser.id).put(Terran_Control_Tower, 1);
        req.get(Terran_Battlecruiser.id).put(Terran_Physics_Lab, 1);
        req.get(Terran_Siege_Tank_Siege_Mode.id).put(Terran_Machine_Shop, 1);
        req.get(Terran_Firebat.id).put(Terran_Academy, 1);
        req.get(Terran_Medic.id).put(Terran_Academy, 1);
        req.get(Zerg_Zergling.id).put(Zerg_Spawning_Pool, 1);
        req.get(Zerg_Hydralisk.id).put(Zerg_Hydralisk_Den, 1);
        req.get(Zerg_Ultralisk.id).put(Zerg_Ultralisk_Cavern, 1);
        req.get(Zerg_Mutalisk.id).put(Zerg_Spire, 1);
        req.get(Zerg_Guardian.id).put(Zerg_Greater_Spire, 1);
        req.get(Zerg_Queen.id).put(Zerg_Queens_Nest, 1);
        req.get(Zerg_Defiler.id).put(Zerg_Defiler_Mound, 1);
        req.get(Zerg_Scourge.id).put(Zerg_Spire, 1);
        req.get(Terran_Valkyrie.id).put(Terran_Control_Tower, 1);
        req.get(Terran_Valkyrie.id).put(Terran_Armory, 1);
        req.get(Zerg_Cocoon.id).put(Zerg_Greater_Spire, 1);
        req.get(Protoss_Dark_Templar.id).put(Protoss_Templar_Archives, 1);
        req.get(Zerg_Devourer.id).put(Zerg_Greater_Spire, 1);
        req.get(Protoss_Dragoon.id).put(Protoss_Cybernetics_Core, 1);
        req.get(Protoss_High_Templar.id).put(Protoss_Templar_Archives, 1);
        req.get(Protoss_Arbiter.id).put(Protoss_Arbiter_Tribunal, 1);
        req.get(Protoss_Carrier.id).put(Protoss_Fleet_Beacon, 1);
        req.get(Protoss_Reaver.id).put(Protoss_Robotics_Support_Bay, 1);
        req.get(Protoss_Observer.id).put(Protoss_Observatory, 1);
        req.get(Terran_Comsat_Station.id).put(Terran_Academy, 1);
        req.get(Terran_Nuclear_Silo.id).put(Terran_Science_Facility, 1);
        req.get(Terran_Nuclear_Silo.id).put(Terran_Covert_Ops, 1);
        req.get(Terran_Barracks.id).put(Terran_Command_Center, 1);
        req.get(Terran_Academy.id).put(Terran_Barracks, 1);
        req.get(Terran_Factory.id).put(Terran_Barracks, 1);
        req.get(Terran_Starport.id).put(Terran_Factory, 1);
        req.get(Terran_Science_Facility.id).put(Terran_Starport, 1);
        req.get(Terran_Engineering_Bay.id).put(Terran_Command_Center, 1);
        req.get(Terran_Armory.id).put(Terran_Factory, 1);
        req.get(Terran_Missile_Turret.id).put(Terran_Engineering_Bay, 1);
        req.get(Terran_Bunker.id).put(Terran_Barracks, 1);
        req.get(Zerg_Lair.id).put(Zerg_Spawning_Pool, 1);
        req.get(Zerg_Hive.id).put(Zerg_Queens_Nest, 1);
        req.get(Zerg_Nydus_Canal.id).put(Zerg_Hive, 1);
        req.get(Zerg_Hydralisk_Den.id).put(Zerg_Spawning_Pool, 1);
        req.get(Zerg_Defiler_Mound.id).put(Zerg_Hive, 1);
        req.get(Zerg_Queens_Nest.id).put(Zerg_Lair, 1);
        req.get(Zerg_Evolution_Chamber.id).put(Zerg_Hatchery, 1);
        req.get(Zerg_Ultralisk_Cavern.id).put(Zerg_Hive, 1);
        req.get(Zerg_Spire.id).put(Zerg_Lair, 1);
        req.get(Zerg_Greater_Spire.id).put(Zerg_Hive, 1);
        req.get(Zerg_Spawning_Pool.id).put(Zerg_Hatchery, 1);
        req.get(Zerg_Spore_Colony.id).put(Zerg_Evolution_Chamber, 1);
        req.get(Zerg_Sunken_Colony.id).put(Zerg_Spawning_Pool, 1);
        req.get(Protoss_Robotics_Facility.id).put(Protoss_Cybernetics_Core, 1);
        req.get(Protoss_Observatory.id).put(Protoss_Robotics_Facility, 1);
        req.get(Protoss_Gateway.id).put(Protoss_Nexus, 1);
        req.get(Protoss_Photon_Cannon.id).put(Protoss_Forge, 1);
        req.get(Protoss_Citadel_of_Adun.id).put(Protoss_Cybernetics_Core, 1);
        req.get(Protoss_Cybernetics_Core.id).put(Protoss_Gateway, 1);
        req.get(Protoss_Templar_Archives.id).put(Protoss_Citadel_of_Adun, 1);
        req.get(Protoss_Forge.id).put(Protoss_Nexus, 1);
        req.get(Protoss_Stargate.id).put(Protoss_Cybernetics_Core, 1);
        req.get(Protoss_Fleet_Beacon.id).put(Protoss_Stargate, 1);
        req.get(Protoss_Arbiter_Tribunal.id).put(Protoss_Templar_Archives, 1);
        req.get(Protoss_Arbiter_Tribunal.id).put(Protoss_Stargate, 1);
        req.get(Protoss_Robotics_Support_Bay.id).put(Protoss_Robotics_Facility, 1);
        req.get(Protoss_Shield_Battery.id).put(Protoss_Gateway, 1);

        return req;
    }
}
