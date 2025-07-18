/*
 * MegaMek - Copyright (C) 2004, 2005 Ben Mazur (bmazur@sev.org)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package megamek.common.weapons.gaussrifles;

import megamek.common.*;
import megamek.common.AmmoType;
import megamek.common.Game;
import megamek.common.ToHitData;
import megamek.common.actions.WeaponAttackAction;
import megamek.common.alphaStrike.AlphaStrikeElement;
import megamek.common.weapons.AttackHandler;
import megamek.common.weapons.HGRHandler;
import megamek.server.totalwarfare.TWGameManager;

/**
 * @author Andrew Hunter
 * @since Oct 19, 2004
 */
public class ISHGaussRifle extends GaussWeapon {
    private static final long serialVersionUID = -2379383217525139478L;

    public ISHGaussRifle() {
        super();

        name = "Heavy Gauss Rifle";
        setInternalName("ISHeavyGaussRifle");
        addLookupName("IS Heavy Gauss Rifle");
        sortingName = "Gauss D";
        heat = 2;
        damage = DAMAGE_VARIABLE;
        ammoType = AmmoType.AmmoTypeEnum.GAUSS_HEAVY;
        minimumRange = 4;
        shortRange = 6;
        mediumRange = 13;
        longRange = 20;
        extremeRange = 30;
        damageShort = 25;
        damageMedium = 20;
        damageLong = 10;
        tonnage = 18.0;
        criticals = 11;
        bv = 346;
        cost = 500000;
        shortAV = 25;
        medAV = 20;
        longAV = 10;
        maxRange = RANGE_LONG;
        explosionDamage = 25;
        rulesRefs = "218, TM";
        techAdvancement.setTechBase(TechBase.IS)
                .setIntroLevel(false)
                .setUnofficial(false)
                .setTechRating(TechRating.E)
                .setAvailability(AvailabilityValue.X, AvailabilityValue.X, AvailabilityValue.E, AvailabilityValue.D)
                .setISAdvancement(3051, 3061, 3067, DATE_NONE, DATE_NONE)
                .setISApproximate(true, false, false, false, false)
                .setPrototypeFactions(Faction.FW)
                .setProductionFactions(Faction.FC);
    }

    @Override
    public int getDamage(int range) {
        if (range <= shortRange) {
            return damageShort;
        }

        if (range <= mediumRange) {
            return damageMedium;
        }

        return damageLong;
    }

    @Override
    protected AttackHandler getCorrectHandler(ToHitData toHit, WeaponAttackAction waa, Game game,
            TWGameManager manager) {
        return new HGRHandler(toHit, waa, game, manager);
    }

    @Override
    public double getBattleForceDamage(int range, Mounted<?> fcs) {
        if (range == AlphaStrikeElement.LONG_RANGE) {
            return 1;
        } else if (range == AlphaStrikeElement.MEDIUM_RANGE) {
            return 2;
        } else if (range == AlphaStrikeElement.SHORT_RANGE) {
            return 1.65;
        } else {
            return 0;
        }
    }
}
