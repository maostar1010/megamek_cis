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
public class InfantrySupportMagshotGaussRifleWeapon extends InfantryWeapon {

	/**
	 *
	 */
	private static final long serialVersionUID = -3164871600230559641L;

	public InfantrySupportMagshotGaussRifleWeapon() {
		super();

		name = "Gauss Rifle (Magshot)";
		setInternalName(name);
		addLookupName("InfantryMagshot");
		addLookupName("InfantryMagshotGaussRifle");
		addLookupName("Infantry Magshot Gauss Rifle");
		ammoType = AmmoType.AmmoTypeEnum.INFANTRY;
		cost = 8500;
		bv = 3.78;
		flags = flags.or(F_NO_FIRES).or(F_DIRECT_FIRE).or(F_BALLISTIC).or(F_INF_SUPPORT);
		infantryDamage = 0.74;
		infantryRange = 2;
		crew = 2;
		tonnage = .045;
		ammoWeight = 0.0165;
		ammoCost = 10;
		shots = 20;
		rulesRefs = "273, TM";
		techAdvancement.setTechBase(TechBase.IS).setISAdvancement(3058, 3059, 3065, DATE_NONE, DATE_NONE)
		        .setISApproximate(true, false, false, false, false)
		        .setPrototypeFactions(Faction.FS)
		        .setProductionFactions(Faction.FS).setTechRating(TechRating.E)
		        .setAvailability(AvailabilityValue.X, AvailabilityValue.X, AvailabilityValue.E, AvailabilityValue.D);

	}
}
