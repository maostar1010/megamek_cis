/*
 * MegaMek - Copyright (C) 2000-2005 Ben Mazur (bmazur@sev.org)
 * Copyright (c) 2018-2024 - The MegaMek Team. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package megamek.common.weapons.infantry;

import megamek.common.AmmoType;

public class InfantryPulseLaserPistolMedusaIII extends InfantryWeapon {

    private static final long serialVersionUID = 1L; // Update for each unique class

    public InfantryPulseLaserPistolMedusaIII() {
        super();

        name = "Pulse Laser Pistol (Medusa III)";
        setInternalName(name);
        addLookupName("MEDUSAIII");
        ammoType = AmmoType.AmmoTypeEnum.INFANTRY;
        cost = 950;
        bv = 0.0315;
        tonnage = 0.0011;
        infantryDamage = 0.11;
        infantryRange = 1;
        shots = 2;
        bursts = 1;
        flags = flags.or(F_NO_FIRES).or(F_DIRECT_FIRE).or(F_LASER).or(F_ENERGY);
        rulesRefs = "Shrapnel #9";

        techAdvancement
                .setTechBase(TechBase.IS)
                .setTechRating(TechRating.D) // Assuming X-D-D-C simplifies to D
                .setAvailability(AvailabilityValue.X, AvailabilityValue.D, AvailabilityValue.D, AvailabilityValue.C)
                .setISAdvancement(DATE_NONE, DATE_NONE, 2800, DATE_NONE, DATE_NONE)
                .setISApproximate(false, false, true, false, false)
                .setProductionFactions(Faction.MC);
    }
}
