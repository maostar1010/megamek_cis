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
public class InfantryPistolMakeshiftPistolWeapon extends InfantryWeapon {

	/**
	 *
	 */
	private static final long serialVersionUID = -3164871600230559641L;

	public InfantryPistolMakeshiftPistolWeapon() {
		super();

		name = "Pistol (Makeshift)";
		setInternalName(name);
		addLookupName("InfantryMakeshiftpistol");
		addLookupName("Makeshift Pistol");
		addLookupName("ClanInfantryMakeshiftpistol");
		addLookupName("Makeshift Pistol(Clan)");
		ammoType = AmmoType.AmmoTypeEnum.INFANTRY;
		cost = 15;
		bv = 0.02;
		tonnage = .001;
		flags = flags.or(F_NO_FIRES).or(F_DIRECT_FIRE).or(F_BALLISTIC);
		infantryDamage = 0.02;
		infantryRange = 0;
		ammoWeight = 0.00001;
		ammoCost = 1;
		shots = 1;
		rulesRefs = " 273, TM";
		techAdvancement.setTechBase(TechBase.ALL).setISAdvancement(1950, 1950, 1950, DATE_NONE, DATE_NONE)
				.setISApproximate(false, false, false, false, false)
				.setClanAdvancement(1950, 1950, 1950, DATE_NONE, DATE_NONE)
				.setClanApproximate(false, false, false, false, false).setTechRating(TechRating.B)
				.setAvailability(AvailabilityValue.A, AvailabilityValue.A, AvailabilityValue.A, AvailabilityValue.A);

	}
}
