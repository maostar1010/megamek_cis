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
package megamek.common.weapons.unofficial;

import megamek.common.AmmoType;
import megamek.common.weapons.autocannons.LBXACWeapon;

/**
 * @author Andrew Hunter
 * @since Oct 15, 2004
 */
public class ISTHBLB5XAC extends LBXACWeapon {
    private static final long serialVersionUID = 7410539709017064021L;

    public ISTHBLB5XAC() {
        super();
        this.name = "LB 5-X AC (THB)";
        this.setInternalName("ISTHBLBXAC5");
        this.addLookupName("IS LB 5-X AC (THB)");
        this.heat = 1;
        this.damage = 5;
        this.rackSize = 5;
        this.ammoType = AmmoType.AmmoTypeEnum.AC_LBX_THB;
        this.minimumRange = 3;
        this.shortRange = 8;
        this.mediumRange = 15;
        this.longRange = 22;
        this.extremeRange = 30;
        this.tonnage = 8.0;
        this.criticals = 6;
        this.shortAV = getBaseAeroDamage();
        this.medAV = this.shortAV;
        this.longAV = this.shortAV;
        this.extAV = this.shortAV;
        this.bv = 85;
        this.cost = 300000;
        // Since this are the Tactical Handbook Weapons I'm using the TM Stats.
        rulesRefs = "THB (Unofficial)";
        techAdvancement.setTechBase(TechBase.IS)
                .setIntroLevel(false)
                .setUnofficial(true)
                .setTechRating(TechRating.E)
                .setAvailability(AvailabilityValue.X, AvailabilityValue.X, AvailabilityValue.E, AvailabilityValue.D)
                .setISAdvancement(3055, 3058, 3060, DATE_NONE, DATE_NONE)
                .setISApproximate(true, false, false, false, false)
                .setPrototypeFactions(Faction.FS)
                .setProductionFactions(Faction.FS);
    }
}
