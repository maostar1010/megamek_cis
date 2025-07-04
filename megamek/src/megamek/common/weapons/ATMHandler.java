/*
 * MegaMek - Copyright (C) 2005 Ben Mazur (bmazur@sev.org)
 * Copyright (c) 2024 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMek.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MegaMek. If not, see <http://www.gnu.org/licenses/>.
 */
package megamek.common.weapons;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import megamek.common.AmmoType;
import megamek.common.BattleArmor;
import megamek.common.Compute;
import megamek.common.ComputeECM;
import megamek.common.Coords;
import megamek.common.Entity;
import megamek.common.Game;
import megamek.common.Infantry;
import megamek.common.Mek;
import megamek.common.Minefield;
import megamek.common.MiscType;
import megamek.common.Mounted;
import megamek.common.RangeType;
import megamek.common.Report;
import megamek.common.Tank;
import megamek.common.Targetable;
import megamek.common.ToHitData;
import megamek.common.WeaponType;
import megamek.common.actions.WeaponAttackAction;
import megamek.common.options.OptionsConstants;
import megamek.server.totalwarfare.TWGameManager;

/**
 * @author Sebastian Brocks
 */
public class ATMHandler extends MissileWeaponHandler {
    @Serial
    private static final long serialVersionUID = -2536312899803153911L;

    public ATMHandler(ToHitData t, WeaponAttackAction w, Game g, TWGameManager m) {
        super(t, w, g, m);
    }

    @Override
    protected int calcDamagePerHit() {
        double toReturn;
        AmmoType atype = (AmmoType) ammo.getType();
        if (atype.getMunitionType().contains(AmmoType.Munitions.M_HIGH_EXPLOSIVE)) {
            sSalvoType = " high-explosive missile(s) ";
            toReturn = 3;
        } else if (atype.getMunitionType().contains(AmmoType.Munitions.M_EXTENDED_RANGE)) {
            sSalvoType = " extended-range missile(s) ";
            toReturn = 1;
        } else {
            toReturn = 2;
        }
        if (target.isConventionalInfantry()) {
            toReturn = Compute.directBlowInfantryDamage(
                    wtype.getRackSize(), bDirect ? toHit.getMoS() / 3 : 0,
                    wtype.getInfantryDamageClass(),
                    ((Infantry) target).isMechanized(),
                    toHit.getThruBldg() != null, ae.getId(), calcDmgPerHitReport);
            toReturn = applyGlancingBlowModifier(toReturn, true);
        }

        return (int) toReturn;
    }

    @Override
    protected int calcHits(Vector<Report> vPhaseReport) {
        // conventional infantry gets hit in one lump
        // don't need to check for BAs, because BA can't mount ATMs
        if (target.isConventionalInfantry()) {
            return 1;
        }
        int hits;
        AmmoType atype = (AmmoType) ammo.getType();
        // TacOPs p.84 Cluster Hit Penalites will only effect ATM HE
        if (atype.getMunitionType().contains(AmmoType.Munitions.M_HIGH_EXPLOSIVE)) {
            hits = super.calcHits(vPhaseReport);
        } else {
            hits = calcStandardAndExtendedAmmoHits(vPhaseReport);
        }
        // change to 5 damage clusters here, after AMS has been done
        hits = nDamPerHit * hits;
        nDamPerHit = 1;
        return hits;
    }

    /**
     * Calculate the attack value based on range
     *
     * @return an <code>int</code> representing the attack value at that range.
     */
    @Override
    protected int calcAttackValue() {
        int av = 0;
        int counterAV;
        int range = RangeType.rangeBracket(nRange, wtype.getATRanges(), true, false);
        AmmoType atype = (AmmoType) ammo.getType();
        if (atype.getMunitionType().contains(AmmoType.Munitions.M_HIGH_EXPLOSIVE)) {
            if (range == WeaponType.RANGE_SHORT) {
                av = wtype.getRoundShortAV();
                av = av + (int) Math.ceil(av / 2.0);
            }
        } else if (atype.getMunitionType().contains(AmmoType.Munitions.M_EXTENDED_RANGE)) {
            av = (int) Math.ceil(wtype.getRoundMedAV() / 2.0);
        } else {
            if (range == WeaponType.RANGE_SHORT) {
                av = wtype.getRoundShortAV();
            } else if (range == WeaponType.RANGE_MED) {
                av = wtype.getRoundMedAV();
            } else if (range == WeaponType.RANGE_LONG) {
                av = wtype.getRoundLongAV();
            } else if (range == WeaponType.RANGE_EXT) {
                av = wtype.getRoundExtAV();
            }
        }

        // Point Defenses engage the missiles still aimed at us
        counterAV = calcCounterAV();
        av = av - counterAV;

        if (bDirect) {
            av = Math.min(av + (toHit.getMoS() / 3), av * 2);
        }
        av = applyGlancingBlowModifier(av, false);
        av = (int) Math.floor(getBracketingMultiplier() * av);
        return av;
    }

    protected int calcStandardAndExtendedAmmoHits(Vector<Report> vPhaseReport) {
        // conventional infantry gets hit in one lump
        // BAs do one lump of damage per BA suit
        if (target.isConventionalInfantry()) {
            if (ae instanceof BattleArmor) {
                bSalvo = true;
                Report r = new Report(3325);
                r.subject = subjectId;
                r.add(wtype.getRackSize()
                        * ((BattleArmor) ae).getShootingStrength());
                r.add(sSalvoType);
                r.add(toHit.getTableDesc());
                vPhaseReport.add(r);
                return ((BattleArmor) ae).getShootingStrength();
            }
            Report r = new Report(3325);
            r.subject = subjectId;
            r.add(wtype.getRackSize());
            r.add(sSalvoType);
            r.add(toHit.getTableDesc());
            vPhaseReport.add(r);
            return 1;
        }
        Entity entityTarget = (target.getTargetType() == Targetable.TYPE_ENTITY) ? (Entity) target
                : null;
        int missilesHit;

        boolean bMekTankStealthActive = false;
        if ((ae instanceof Mek) || (ae instanceof Tank)) {
            bMekTankStealthActive = ae.isStealthActive();
        }
        Mounted<?> mLinker = weapon.getLinkedBy();
        AmmoType atype = (AmmoType) ammo.getType();

        int nMissilesModifier = getClusterModifiers(
                atype.getMunitionType().contains(AmmoType.Munitions.M_HIGH_EXPLOSIVE));

        // is any hex in the flight path of the missile ECM affected?
        boolean bECMAffected = ComputeECM.isAffectedByECM(ae, ae.getPosition(), target.getPosition());
        // if the attacker is affected by ECM or the target is protected by ECM
        // then act as if affected.

        if (((mLinker != null) && (mLinker.getType() instanceof MiscType)
                && !mLinker.isDestroyed() && !mLinker.isMissing()
                && !mLinker.isBreached() && mLinker.getType().hasFlag(
                        MiscType.F_ARTEMIS))
                && (atype.getMunitionType().contains(AmmoType.Munitions.M_ARTEMIS_CAPABLE))) {
            if (bECMAffected) {
                // ECM prevents bonus
                Report r = new Report(3330);
                r.subject = subjectId;
                r.newlines = 0;
                vPhaseReport.addElement(r);
            } else if (bMekTankStealthActive) {
                // stealth prevents bonus
                Report r = new Report(3335);
                r.subject = subjectId;
                r.newlines = 0;
                vPhaseReport.addElement(r);
            } else {
                nMissilesModifier += 2;
            }
        } else if (atype.getAmmoType() == AmmoType.AmmoTypeEnum.ATM) {
            if (bECMAffected) {
                // ECM prevents bonus
                Report r = new Report(3330);
                r.subject = subjectId;
                r.newlines = 0;
                vPhaseReport.addElement(r);
            } else if (bMekTankStealthActive) {
                // stealth prevents bonus
                Report r = new Report(3335);
                r.subject = subjectId;
                r.newlines = 0;
                vPhaseReport.addElement(r);
            } else {
                nMissilesModifier += 2;
            }
        } else if ((entityTarget != null)
                && (entityTarget.isNarcedBy(ae.getOwner().getTeam()) || entityTarget
                        .isINarcedBy(ae.getOwner().getTeam()))) {
            // only apply Narc bonus if we're not suffering ECM effect
            // and we are using narc ammo, and we're not firing indirectly.
            // narc capable missiles are only affected if the narc pod, which
            // sits on the target, is ECM affected
            boolean bTargetECMAffected = false;
            bTargetECMAffected = ComputeECM.isAffectedByECM(ae,
                    target.getPosition(), target.getPosition());
            if (((atype.getAmmoType() == AmmoType.AmmoTypeEnum.LRM) || (atype
                    .getAmmoType() == AmmoType.AmmoTypeEnum.SRM))
                    || ((atype.getAmmoType() == AmmoType.AmmoTypeEnum.MML)
                            && (atype.getMunitionType().contains(AmmoType.Munitions.M_NARC_CAPABLE)) && ((weapon
                                    .curMode() == null) || !weapon.curMode().equals(
                                            "Indirect")))) {
                if (bTargetECMAffected) {
                    // ECM prevents bonus
                    Report r = new Report(3330);
                    r.subject = subjectId;
                    r.newlines = 0;
                    vPhaseReport.addElement(r);
                } else {
                    nMissilesModifier += 2;
                }
            }
        }

        // add AMS mods
        nMissilesModifier += getAMSHitsMod(vPhaseReport);

        if (game.getOptions().booleanOption(OptionsConstants.ADVAERORULES_AERO_SANITY)
                && entityTarget != null && entityTarget.isLargeCraft()) {
            nMissilesModifier -= getAeroSanityAMSHitsMod();
        }

        if (allShotsHit()) {
            // We want buildings and large craft to be able to affect this number with AMS
            // treat as a Streak launcher (cluster roll 11) to make this happen
            missilesHit = Compute.missilesHit(wtype.getRackSize(),
                    nMissilesModifier, weapon.isHotLoaded(), true,
                    isAdvancedAMS());
        } else {
            if (ae instanceof BattleArmor) {
                missilesHit = Compute.missilesHit(wtype.getRackSize()
                        * ((BattleArmor) ae).getShootingStrength(),
                        nMissilesModifier, weapon.isHotLoaded(), false,
                        isAdvancedAMS());
            } else {
                missilesHit = Compute.missilesHit(wtype.getRackSize(),
                        nMissilesModifier, weapon.isHotLoaded(), false,
                        isAdvancedAMS());
            }
        }

        if (missilesHit > 0) {
            Report r = new Report(3325);
            r.subject = subjectId;
            r.add(missilesHit);
            r.add(sSalvoType);
            r.add(toHit.getTableDesc());
            r.newlines = 0;
            vPhaseReport.addElement(r);
            if (nMissilesModifier != 0) {
                if (nMissilesModifier > 0) {
                    r = new Report(3340);
                } else {
                    r = new Report(3341);
                }
                r.subject = subjectId;
                r.add(nMissilesModifier);
                r.newlines = 0;
                vPhaseReport.addElement(r);
            }
        }
        Report r = new Report(3345);
        r.subject = subjectId;
        vPhaseReport.addElement(r);
        bSalvo = true;
        return missilesHit;
    }

    @Override
    protected boolean specialResolution(Vector<Report> vPhaseReport, Entity entityTarget) {
        if (!bMissed
                && (target.getTargetType() == Targetable.TYPE_MINEFIELD_CLEAR)) {
            Report r = new Report(3255);
            r.indent(1);
            r.subject = subjectId;
            vPhaseReport.addElement(r);
            Coords coords = target.getPosition();

            Enumeration<Minefield> minefields = game.getMinefields(coords).elements();
            ArrayList<Minefield> mfRemoved = new ArrayList<>();
            while (minefields.hasMoreElements()) {
                Minefield mf = minefields.nextElement();
                if (gameManager.clearMinefield(mf, ae,
                        Minefield.CLEAR_NUMBER_WEAPON, vPhaseReport)) {
                    mfRemoved.add(mf);
                }
            }
            // we have to do it this way to avoid a concurrent error problem
            for (Minefield mf : mfRemoved) {
                gameManager.removeMinefield(mf);
            }
            return true;
        }
        return false;
    }

}
