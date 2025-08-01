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
public class InfantryRifleSunbeamStarfireERLaserWeapon extends InfantryWeapon {

	/**
	 *
	 */
	private static final long serialVersionUID = -3164871600230559641L;

	public InfantryRifleSunbeamStarfireERLaserWeapon() {
		super();

		name = "Laser Rifle (ER [Sunbeam Starfire])";
		setInternalName(name);
		addLookupName("InfantrySunbeamStarfire");
		addLookupName("Sunbeam Starfire ER Laser Rifle");
		ammoType = AmmoType.AmmoTypeEnum.INFANTRY;
		cost = 2500;
		bv = 2.01;
		tonnage = .005;
		flags = flags.or(F_NO_FIRES).or(F_DIRECT_FIRE).or(F_LASER).or(F_ENERGY);
		infantryDamage = 0.28;
		infantryRange = 3;
		ammoWeight = 0.0003;
		shots = 5;
		rulesRefs = "273, TM";
		techAdvancement.setTechBase(TechBase.IS).setISAdvancement(3050, 3052, 3075, DATE_NONE, DATE_NONE)
		        .setISApproximate(true, false, false, false, false)
		        .setPrototypeFactions(Faction.FW)
		        .setProductionFactions(Faction.FW).setTechRating(TechRating.E)
		        .setAvailability(AvailabilityValue.X, AvailabilityValue.X, AvailabilityValue.E, AvailabilityValue.D);

	}
}
