package bwapi.types;

public enum BulletType {
    Melee(0),

    Fusion_Cutter_Hit(141),
    Gauss_Rifle_Hit(142),
    C_10_Canister_Rifle_Hit(143),
    Gemini_Missiles(144),
    Fragmentation_Grenade(145),
    Longbolt_Missile(146),
    Unused_Lockdown(147),
    ATS_ATA_Laser_Battery(148),
    Burst_Lasers(149),
    Arclite_Shock_Cannon_Hit(150),
    EMP_Missile(151),
    Dual_Photon_Blasters_Hit(152),
    Particle_Beam_Hit(153),
    Anti_Matter_Missile(154),
    Pulse_Cannon(155),
    Psionic_Shockwave_Hit(156),
    Psionic_Storm(157),
    Yamato_Gun(158),
    Phase_Disruptor(159),
    STA_STS_Cannon_Overlay(160),
    Sunken_Colony_Tentacle(161),
    Venom_Unused(162),
    Acid_Spore(163),
    Plasma_Drip_Unused(164),
    Glave_Wurm(165),
    Seeker_Spores(166),
    Queen_Spell_Carrier(167),
    Plague_Cloud(168),
    Consume(169),
    Ensnare(170),
    Needle_Spine_Hit(171),
    Invisible(172),

    Optical_Flare_Grenade(201),
    Halo_Rockets(202),
    Subterranean_Spines(203),
    Corrosive_Acid_Shot(204),
    Corrosive_Acid_Hit(205),
    Neutron_Flare(206),

    None(209),
    Unknown(210);

    private int value;

    public int getValue(){
        return value;
    }

    BulletType(int value){
        this.value = value;
    }
}
