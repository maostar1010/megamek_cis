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
public class InfantryShotgunCombatWeapon extends InfantryWeapon {

	/**
	 *
	 */
	private static final long serialVersionUID = -3164871600230559641L;

	public InfantryShotgunCombatWeapon() {
		super();

		name = "Shotgun (Combat)";
		setInternalName(name);
		addLookupName("InfantryCombatShotgun");
		addLookupName("Combat Shotgun");
		ammoType = AmmoType.AmmoTypeEnum.INFANTRY;
		cost = 175;
		bv = 0.25;
		tonnage = .0045;
		flags = flags.or(F_NO_FIRES).or(F_DIRECT_FIRE).or(F_BALLISTIC);
		infantryDamage = 0.27;
		infantryRange = 0;
		ammoWeight = 0.00014;
		ammoCost = 2;
		shots = 8;
		rulesRefs = " 273, TM";
		techAdvancement.setTechBase(TechBase.ALL).setISAdvancement(1950, 1950, 1950, DATE_NONE, DATE_NONE)
		        .setISApproximate(false, false, false, false, false)
		        .setClanAdvancement(1950, 1950, 1950, DATE_NONE, DATE_NONE)
		        .setClanApproximate(false, false, false, false, false).setTechRating(TechRating.C)
		        .setAvailability(AvailabilityValue.B, AvailabilityValue.B, AvailabilityValue.B, AvailabilityValue.B);

	}
}
