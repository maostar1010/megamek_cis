/**
 * MegaMek - Copyright (C) 2004,2005 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */
/*
 * Created on Sep 7, 2005
 *
 */
package megamek.common.weapons.infantry;

import megamek.common.AmmoType;

/**
 * @author Ben Grills
 */
public class InfantryPistolHoldOutLaserPistolWeapon extends InfantryWeapon {

	/**
	 *
	 */
	private static final long serialVersionUID = -3164871600230559641L;

	public InfantryPistolHoldOutLaserPistolWeapon() {
		super();

		name = "Laser Pistol (Hold-Out)";
		setInternalName(name);
		addLookupName("InfantryHoldoutLaserpistol");
		addLookupName("Holdout Laser Pistol");
		ammoType = AmmoType.AmmoTypeEnum.INFANTRY;
		cost = 100;
		bv = 0.13;
		tonnage = .00005;
		flags = flags.or(F_NO_FIRES).or(F_DIRECT_FIRE).or(F_ENERGY);
		infantryDamage = 0.14;
		infantryRange = 0;
		ammoWeight = 0.00002;
		shots = 6;
		rulesRefs = "273, TM";
		techAdvancement.setTechBase(TechBase.ALL).setISAdvancement(2318, 2320, 2350, DATE_NONE, DATE_NONE)
				.setISApproximate(true, false, false, false, false)
				.setClanAdvancement(2318, 2320, 2350, DATE_NONE, DATE_NONE)
				.setClanApproximate(true, false, false, false, false).setPrototypeFactions(Faction.LC)
				.setProductionFactions(Faction.LC).setTechRating(TechRating.D)
				.setAvailability(AvailabilityValue.B, AvailabilityValue.B, AvailabilityValue.B, AvailabilityValue.B);

	}
}
