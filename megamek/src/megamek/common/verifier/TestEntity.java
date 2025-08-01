/*
 * MegaMek -
 * Copyright (C) 2000-2005 Ben Mazur (bmazur@sev.org)
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
package megamek.common.verifier;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import megamek.common.*;
import megamek.common.annotations.Nullable;
import megamek.common.enums.MPBoosters;
import megamek.common.equipment.ArmorType;
import megamek.common.equipment.MiscMounted;
import megamek.common.equipment.WeaponMounted;
import megamek.common.util.StringUtil;
import megamek.common.weapons.battlearmor.BAFlamerWeapon;
import megamek.common.weapons.lasers.CLChemicalLaserWeapon;

/**
 * Abstract parent class for testing and validating instantiations of
 * <code> Entity</code> subclasses.
 *
 * @author Reinhard Vicinus
 */
public abstract class TestEntity implements TestEntityOption {
    public static enum Ceil {
        TON(1.0), HALFTON(2.0), QUARTERTON(4.0), TENTHTON(10.0), KILO(1000.0);

        public final double mult;

        private Ceil(double mult) {
            this.mult = mult;
        }
    }

    protected Engine engine = null;
    protected Structure structure = null;
    private TestEntityOption options = null;

    public abstract Entity getEntity();

    public abstract boolean isTank();

    public abstract boolean isMek();

    public abstract boolean isAero();

    public abstract boolean isSmallCraft();

    public abstract boolean isAdvancedAerospace();

    public abstract boolean isProtoMek();

    public abstract double getWeightControls();

    public abstract double getWeightMisc();

    public abstract double getWeightHeatSinks();

    public abstract boolean hasDoubleHeatSinks();

    public abstract int getCountHeatSinks();

    public abstract String printWeightMisc();

    public abstract String printWeightControls();

    public boolean correctEntity(StringBuffer buff) {
        return correctEntity(buff, getEntity().getTechLevel());
    }

    public abstract boolean correctEntity(StringBuffer buff, int ammoTechLvl);

    public abstract StringBuffer printEntity();

    public abstract String getName();

    public String fileString = null; // where the unit came from

    /**
     * @param unit The entity the supplied entity
     * @return a TestEntity instance for the supplied Entity.
     */
    public static TestEntity getEntityVerifier(Entity unit) {
        EntityVerifier entityVerifier = EntityVerifier.getInstance(new File(
                Configuration.unitsDir(), EntityVerifier.CONFIG_FILENAME));
        TestEntity testEntity = null;

        if (unit.hasETypeFlag(Entity.ETYPE_MEK)) {
            testEntity = new TestMek((Mek) unit, entityVerifier.mekOption, null);
        } else if (unit.hasETypeFlag(Entity.ETYPE_PROTOMEK)) {
            testEntity = new TestProtoMek((ProtoMek) unit, entityVerifier.protomekOption, null);
        } else if (unit.isSupportVehicle()) {
            testEntity = new TestSupportVehicle(unit, entityVerifier.tankOption, null);
        } else if (unit.hasETypeFlag(Entity.ETYPE_TANK) && !unit.hasETypeFlag(Entity.ETYPE_GUN_EMPLACEMENT)) {
            testEntity = new TestTank((Tank) unit, entityVerifier.tankOption, null);
        } else if (unit.hasETypeFlag(Entity.ETYPE_SMALL_CRAFT)) {
            testEntity = new TestSmallCraft((SmallCraft) unit, entityVerifier.aeroOption, null);
        } else if (unit.hasETypeFlag(Entity.ETYPE_JUMPSHIP)) {
            testEntity = new TestAdvancedAerospace((Jumpship) unit, entityVerifier.aeroOption, null);
        } else if (unit.hasETypeFlag(Entity.ETYPE_AERO)) {
            testEntity = new TestAero((Aero) unit, entityVerifier.aeroOption, null);
        } else if (unit.hasETypeFlag(Entity.ETYPE_BATTLEARMOR)) {
            testEntity = new TestBattleArmor((BattleArmor) unit, entityVerifier.baOption, null);
        } else if (unit.hasETypeFlag(Entity.ETYPE_INFANTRY)) {
            testEntity = new TestInfantry((Infantry) unit, entityVerifier.infOption, null);
        }
        return testEntity;
    }

    public TestEntity(TestEntityOption options, Engine engine, Structure structure) {
        this.options = options;
        this.engine = engine;
        this.structure = structure;
    }

    public boolean isClan() {
        return getEntity().isClan();
    }

    public boolean isClanArmor() {
        return getEntity().isClanArmor(0) && !getEntity().hasPatchworkArmor();
    }

    public double getWeight() {
        return getEntity().getWeight();
    }

    public int getTotalOArmor() {
        return getEntity().getTotalOArmor();
    }

    public String getLocationAbbr(int location) {
        return getEntity().getLocationAbbr(location);
    }

    @Override
    public Ceil getWeightCeilingEngine() {
        return options.getWeightCeilingEngine();
    }

    @Override
    public Ceil getWeightCeilingStructure() {
        return options.getWeightCeilingStructure();
    }

    @Override
    public Ceil getWeightCeilingArmor() {
        return options.getWeightCeilingArmor();
    }

    @Override
    public Ceil getWeightCeilingControls() {
        return options.getWeightCeilingControls();
    }

    @Override
    public Ceil getWeightCeilingWeapons() {
        return options.getWeightCeilingWeapons();
    }

    @Override
    public Ceil getWeightCeilingTargComp() {
        return options.getWeightCeilingTargComp();
    }

    @Override
    public Ceil getWeightCeilingGyro() {
        return options.getWeightCeilingGyro();
    }

    @Override
    public Ceil getWeightCeilingTurret() {
        return options.getWeightCeilingTurret();
    }

    @Override
    public Ceil getWeightCeilingLifting() {
        return options.getWeightCeilingLifting();
    }

    @Override
    public Ceil getWeightCeilingPowerAmp() {
        return options.getWeightCeilingPowerAmp();
    }

    @Override
    public double getMaxOverweight() {
        return options.getMaxOverweight();
    }

    @Override
    public boolean showOverweightedEntity() {
        return options.showOverweightedEntity();
    }

    @Override
    public double getMinUnderweight() {
        return options.getMinUnderweight();
    }

    @Override
    public boolean showUnderweightedEntity() {
        return options.showUnderweightedEntity();
    }

    @Override
    public boolean showCorrectArmor() {
        return options.showCorrectArmor();
    }

    @Override
    public boolean showCorrectCritical() {
        return options.showCorrectCritical();
    }

    @Override
    public boolean showFailedEquip() {
        return options.showFailedEquip();
    }

    @Override
    public boolean ignoreFailedEquip(String name) {
        return options.ignoreFailedEquip(name);
    }

    @Override
    public boolean showIncorrectIntroYear() {
        return !ignoreEquipmentIntroYear() && options.showIncorrectIntroYear();
    }

    @Override
    public int getIntroYearMargin() {
        return options.getIntroYearMargin();
    }

    @Override
    public boolean skip() {
        return !skipBuildValidation() && options.skip();
    }

    @Override
    public int getTargCompCrits() {
        return options.getTargCompCrits();
    }

    @Override
    public int getPrintSize() {
        return options.getPrintSize();
    }

    /**
     * Used to round values up based on the specified type.
     *
     * @param f    Value to round
     * @param type Specifies the number of decimals to round to, see
     *             TestEntity.CEIL_TON, etc.
     * @return Rounded value
     */
    public static double ceil(double f, Ceil type) {
        return Math.ceil(f * type.mult) / type.mult;
    }

    public static double ceilMaxHalf(double f, Ceil type) {
        if (type == Ceil.TON) {
            return TestEntity.ceil(f, Ceil.HALFTON);
        }
        return TestEntity.ceil(f, type);
    }

    public static double floor(double f, Ceil type) {
        return Math.floor(f * type.mult) / type.mult;
    }

    public static double round(double f, Ceil type) {
        return Math.round(f * type.mult) / type.mult;
    }

    static String makeWeightString(double weight) {
        return makeWeightString(weight, false);
    }

    static String makeWeightString(double weight, boolean kg) {
        if (kg) {
            weight *= 1000;
        }
        if (weight < 0.5) {
            // For small equipment show as many decimal places as needed.
            return DecimalFormat.getInstance().format(weight);
        } else {
            return String.format("%3.1f%s", weight, (kg ? " kg" : ""));
        }
    }

    /**
     * Allows a value to be truncuated to an arbitrary number of decimal places.
     *
     * @param value
     *                  The input value
     * @param precision
     *                  The number of decimals to truncate at
     *
     * @return The input value truncated to the number of decimal places
     *         supplied
     */
    public static double setPrecision(double value, int precision) {
        return Math.round(value * Math.pow(10, precision))
                / Math.pow(10, precision);
    }

    /**
     * Filters all armor according to given tech constraints
     *
     * @param etype        The entity type bit mask
     * @param industrial   For meks; industrial meks can only use certain armor
     *                     types
     *                     unless allowing experimental rules
     * @param primitive    Whether the unit is primitive/retrotech
     * @param movementMode For vehicles; hardened armor is illegal for some movement
     *                     modes
     * @param techManager  The constraints used to filter the armor types
     * @return A list of all armors that meet the tech constraints
     */
    public static List<ArmorType> legalArmorsFor(long etype, boolean industrial, boolean primitive,
            EntityMovementMode movementMode, ITechManager techManager) {
        if ((etype & Entity.ETYPE_BATTLEARMOR) != 0) {
            return TestBattleArmor.legalArmorsFor(techManager);
        } else if ((etype & Entity.ETYPE_SMALL_CRAFT) != 0) {
            return TestSmallCraft.legalArmorsFor(techManager);
        } else if ((etype & Entity.ETYPE_JUMPSHIP) != 0) {
            return TestAdvancedAerospace.legalArmorsFor(techManager, primitive);
        } else if ((etype
                & (Entity.ETYPE_FIXED_WING_SUPPORT | Entity.ETYPE_SUPPORT_TANK | Entity.ETYPE_SUPPORT_VTOL)) != 0) {
            return TestSupportVehicle.legalArmorsFor(techManager);
        } else if ((etype & Entity.ETYPE_AERO) != 0) {
            return TestAero.legalArmorsFor(techManager);
        } else if ((etype & Entity.ETYPE_TANK) != 0) {
            return TestTank.legalArmorsFor(movementMode, techManager);
        } else if ((etype & Entity.ETYPE_MEK) != 0) {
            return TestMek.legalArmorsFor(etype, industrial, techManager);
        } else {
            return Collections.emptyList();
        }
    }

    public static List<EquipmentType> validJumpJets(long entitytype, boolean industrial) {
        if ((entitytype & Entity.ETYPE_MEK) != 0) {
            return TestMek.MekJumpJets.allJJs(industrial);
        } else if ((entitytype & Entity.ETYPE_TANK) != 0) {
            return Collections.singletonList(EquipmentType.get(EquipmentTypeLookup.VEHICLE_JUMP_JET));
        } else if ((entitytype & Entity.ETYPE_BATTLEARMOR) != 0) {
            return TestBattleArmor.BAMotiveSystems.allSystems();
        } else if ((entitytype & Entity.ETYPE_PROTOMEK) != 0) {
            // Until we have a TestProtomek
            return Arrays.asList(new EquipmentType[] {
                    EquipmentType.get(EquipmentTypeLookup.PROTOMEK_JUMP_JET),
                    EquipmentType.get(EquipmentTypeLookup.EXTENDED_JUMP_JET_SYSTEM),
                    EquipmentType.get(EquipmentTypeLookup.PROTOMEK_UMU) });
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Additional crew requirements for vehicles and aerospace vessels for certain
     * types of
     * equipment.
     */
    public static int equipmentCrewRequirements(Mounted<?> mounted) {
        if (mounted.getType() instanceof MiscType) {
            if (mounted.getType().hasFlag(MiscType.F_MOBILE_FIELD_BASE)) {
                return 5;
            }
            if (mounted.getType().hasFlag(MiscType.F_MASH)) {
                return 5 * (int) mounted.getSize();
            }
            if (mounted.getType().hasFlag(MiscType.F_FIELD_KITCHEN)) {
                return 3;
            }
            if (mounted.getType().hasFlag(MiscType.F_COMMUNICATIONS)) {
                return (int) mounted.getTonnage();
            }
            if (mounted.getType().hasFlag(MiscType.F_MOBILE_HPG)) {
                // Mobile HPG has crew requirement of 10; ground-mobile has requirement of 1.
                return mounted.getType().hasFlag(MiscType.F_TANK_EQUIPMENT) ? 1 : 10;
            }
            if (mounted.getType().hasFlag(MiscType.F_SMALL_COMM_SCANNER_SUITE)) {
                return 6;
            }
            if (mounted.getType().hasFlag(MiscType.F_LARGE_COMM_SCANNER_SUITE)) {
                return 12;
            }
        }
        return 0;
    }

    /**
     * Determines whether a type of equipment requires a particular location on an
     * {@link Entity}.
     * What this means depends on the type of unit, but typically it does not take
     * up a slot or
     * is not assigned a firing arc.
     *
     * @param entity The Entity the equipment is to be placed on
     * @param eq     The equipment to place on the Entity
     * @return Whether the equipment requires a location
     * @see #getSystemWideLocation(Entity)
     */
    public static boolean eqRequiresLocation(Entity entity, EquipmentType eq) {
        if (entity.hasETypeFlag(Entity.ETYPE_AERO)) {
            return TestAero.eqRequiresLocation(eq, entity.isFighter());
        } else if (entity.hasETypeFlag(Entity.ETYPE_PROTOMEK)) {
            return TestProtoMek.requiresSlot(eq);
        } else if (entity.hasETypeFlag(Entity.ETYPE_TANK)) {
            return !TestTank.isBodyEquipment(eq);
        }
        return true;
    }

    /**
     * Determines where to place equipment that does not require a specific
     * location. What
     * this means varies by {@link Entity} type.
     *
     * @param entity The Entity to place the equipment in
     * @return The location to place equipment that is not required to be assigned a
     *         location,
     *         defaulting to Entity.LOC_NONE for unit types that do not have such a
     *         location.
     */
    public static int getSystemWideLocation(Entity entity) {
        if (entity.hasETypeFlag(Entity.ETYPE_JUMPSHIP)) {
            return Jumpship.LOC_HULL;
        } else if (entity.hasETypeFlag(Entity.ETYPE_SMALL_CRAFT)) {
            return SmallCraft.LOC_HULL;
        } else if (entity.hasETypeFlag(Entity.ETYPE_AERO)) {
            return Aero.LOC_FUSELAGE;
        } else if (entity.hasETypeFlag(Entity.ETYPE_TANK)) {
            return Tank.LOC_BODY;
        } else if (entity.hasETypeFlag(Entity.ETYPE_PROTOMEK)) {
            return ProtoMek.LOC_BODY;
        }
        return Entity.LOC_NONE;
    }

    public MPBoosters getMPBoosters() {
        return (getEntity() instanceof Mek) ? getEntity().getMPBoosters() : MPBoosters.NONE;
    }

    public String printShortMovement() {
        MPBoosters mpBoosters = getMPBoosters();
        return "Movement: " + getEntity().getOriginalWalkMP() + "/"
                + (int) Math.ceil(getEntity().getOriginalWalkMP() * 1.5)
                + (mpBoosters.isNone() ? "" : "(" + getEntity().getOriginalWalkMP() * 2 + ")")
                + (mpBoosters.isMASCAndSupercharger() ? "(" + getEntity().getOriginalWalkMP() * 2.5 + ")" : "")
                + (getEntity().getOriginalJumpMP() != 0 ? "/" + getEntity().getOriginalJumpMP() : "")
                + "\n";
    }

    public String printWeightHeatSinks() {
        return StringUtil.makeLength("Heat Sinks: " + getCountHeatSinks()
                + (hasDoubleHeatSinks() ? " [" + 2 * getCountHeatSinks() + "]" : ""),
                getPrintSize() - 5)
                + TestEntity.makeWeightString(getWeightHeatSinks(), usesKgStandard()) + "\n";
    }

    public String printWeightEngine() {
        return StringUtil.makeLength("Engine: " + ((null != engine) ? engine.getEngineName() : "---"),
                getPrintSize() - 5)
                + TestEntity.makeWeightString(getWeightEngine(), usesKgStandard()) + "\n";
    }

    public double getWeightEngine() {
        return ((null != engine) ? engine.getWeightEngine(getEntity()) : 0);
    }

    public String printWeightStructure() {
        return StringUtil.makeLength("Structure: " + getEntity().getTotalOInternal() + " " + structure.getShortName(),
                getPrintSize() - 5)
                + TestEntity.makeWeightString(getWeightStructure(), usesKgStandard()) + "\n";
    }

    public double getWeightStructure() {
        return structure.getWeightStructure(getWeight(),
                getWeightCeilingStructure());
    }

    public String printWeightArmor() {
        if (!getEntity().hasPatchworkArmor()) {
            return StringUtil.makeLength("Armor: " + getTotalOArmor() + " "
                    + ArmorType.forEntity(getEntity()).getShortName(), getPrintSize() - 5)
                    + TestEntity.makeWeightString(getWeightArmor(), usesKgStandard()) + "\n";
        } else {
            return StringUtil.makeLength("Armor: " + getTotalOArmor() + " " + "Patchwork",
                    getPrintSize() - 5)
                    + TestEntity.makeWeightString(getWeightArmor(), usesKgStandard()) + "\n";
        }

    }

    public double getWeightArmor() {
        return getEntity().getLabArmorTonnage();
    }

    public static double getRawWeightArmor(ArmorType armor, int totalOArmor) {
        double points = totalOArmor;
        double multiplier = armor.getArmorPointsMultiplier();
        points /= multiplier;
        double pointsPerTon = 16.0f;
        return points / pointsPerTon;
    }

    public static double getWeightArmor(ArmorType armor, int totalOArmor, TestEntity.Ceil roundWeight) {
        return TestEntity.ceilMaxHalf(getRawWeightArmor(armor, totalOArmor), roundWeight);
    }

    public double getWeightAllocatedArmor() {
        if (!getEntity().hasPatchworkArmor()) {
            return getWeightArmor(ArmorType.forEntity(getEntity()), getEntity().getTotalOArmor(),
                    getWeightCeilingArmor());
        } else {
            double armorWeight = 0;
            for (int loc = getEntity().firstArmorIndex(); loc < getEntity().locations(); loc++) {
                int points = getEntity().getOArmor(loc);
                if (getEntity().hasRearArmor(loc) &&
                        (getEntity().getOArmor(loc, true) > 0)) {
                    points += getEntity().getOArmor(loc, true);
                }
                armorWeight += getRawWeightArmor(ArmorType.forEntity(getEntity(), loc), points);
            }
            return TestEntity.ceilMaxHalf(armorWeight, getWeightCeilingArmor());
        }
    }

    /**
     * Gives subclasses a chance to exclude certain misc equipment if it is
     * accounted for in a different
     * category.
     *
     * @param misc The misc equipment type
     * @return Whether to include the equipment in the misc equipment category
     * @see #getWeightMiscEquip()
     */
    protected boolean includeMiscEquip(MiscType misc) {
        return true;
    }

    public double getWeightMiscEquip() {
        double weightSum = 0.0;
        for (Mounted<?> m : getEntity().getMisc()) {
            MiscType mt = (MiscType) m.getType();
            if (!includeMiscEquip(mt)
                    || mt.hasFlag(MiscType.F_ENDO_STEEL)
                    || mt.hasFlag(MiscType.F_ENDO_COMPOSITE)
                    || mt.hasFlag(MiscType.F_ENDO_STEEL_PROTO)
                    || mt.hasFlag(MiscType.F_ENDO_COMPOSITE)
                    || mt.hasFlag(MiscType.F_COMPOSITE)
                    || mt.hasFlag(MiscType.F_INDUSTRIAL_STRUCTURE)
                    || mt.hasFlag(MiscType.F_REINFORCED)
                    || mt.hasFlag(MiscType.F_FERRO_FIBROUS)
                    || mt.hasFlag(MiscType.F_FERRO_FIBROUS_PROTO)
                    || mt.hasFlag(MiscType.F_FERRO_LAMELLOR)
                    || mt.hasFlag(MiscType.F_LIGHT_FERRO)
                    || mt.hasFlag(MiscType.F_HEAVY_FERRO)
                    || mt.hasFlag(MiscType.F_REACTIVE)
                    || mt.hasFlag(MiscType.F_REFLECTIVE)
                    || mt.hasFlag(MiscType.F_HARDENED_ARMOR)
                    || mt.hasFlag(MiscType.F_PRIMITIVE_ARMOR)
                    || mt.hasFlag(MiscType.F_COMMERCIAL_ARMOR)
                    || mt.hasFlag(MiscType.F_INDUSTRIAL_ARMOR)
                    || mt.hasFlag(MiscType.F_HEAVY_INDUSTRIAL_ARMOR)
                    || mt.hasFlag(MiscType.F_ANTI_PENETRATIVE_ABLATIVE)
                    || mt.hasFlag(MiscType.F_HEAT_DISSIPATING)
                    || mt.hasFlag(MiscType.F_IMPACT_RESISTANT)
                    || mt.hasFlag(MiscType.F_BALLISTIC_REINFORCED)
                    || mt.hasFlag(MiscType.F_ELECTRIC_DISCHARGE_ARMOR)
                    || mt.hasFlag(MiscType.F_HEAT_SINK)
                    || mt.hasFlag(MiscType.F_DOUBLE_HEAT_SINK)
                    || mt.hasFlag(MiscType.F_IS_DOUBLE_HEAT_SINK_PROTOTYPE)) {
                continue;
            }
            weightSum += m.getTonnage();
        }
        return weightSum;
    }

    public StringBuffer printMiscEquip() {
        return printMiscEquip(new StringBuffer());
    }

    public StringBuffer printMiscEquip(StringBuffer buff) {
        return printMiscEquip(buff, 20, getPrintSize());
    }

    public StringBuffer printMiscEquip(StringBuffer buff, int posLoc,
            int posWeight) {
        for (Mounted<?> m : getEntity().getMisc()) {
            MiscType mt = (MiscType) m.getType();

            if (m.getLocation() == Entity.LOC_NONE) {
                continue;
            }
            if (mt.hasFlag(MiscType.F_ENDO_COMPOSITE)
                    || mt.hasFlag(MiscType.F_ENDO_STEEL)
                    || mt.hasFlag(MiscType.F_ENDO_STEEL_PROTO)
                    || mt.hasFlag(MiscType.F_REINFORCED)
                    || mt.hasFlag(MiscType.F_FERRO_FIBROUS)
                    || mt.hasFlag(MiscType.F_FERRO_FIBROUS_PROTO)
                    || mt.hasFlag(MiscType.F_LIGHT_FERRO)
                    || mt.hasFlag(MiscType.F_HEAVY_FERRO)
                    || mt.hasFlag(MiscType.F_REACTIVE)
                    || mt.hasFlag(MiscType.F_REFLECTIVE)
                    || mt.hasFlag(MiscType.F_FERRO_LAMELLOR)
                    || mt.hasFlag(MiscType.F_INDUSTRIAL_STRUCTURE)) {
                continue;
            }

            if (m.getTonnage() == 0f) {
                continue;
            }

            buff.append(StringUtil.makeLength(m.getName(), 20));
            buff.append(
                    StringUtil.makeLength(getLocationAbbr(m.getLocation()),
                            getPrintSize() - 5 - 20))
                    .append(
                            TestEntity.makeWeightString(m.getTonnage(), usesKgStandard()));
            buff.append("\n");
        }
        return buff;
    }

    public double getWeightWeapon() {
        double weight = 0.0;
        for (Mounted<?> m : getEntity().getTotalWeaponList()) {
            if (m.isWeaponGroup()) {
                continue;
            }
            weight += m.getTonnage();
        }
        return weight;
    }

    public StringBuffer printWeapon() {
        return printWeapon(new StringBuffer());
    }

    public StringBuffer printWeapon(StringBuffer buff) {
        return printWeapon(buff, 20, getPrintSize());
    }

    public StringBuffer printWeapon(StringBuffer buff, int posLoc, int posWeight) {
        for (Mounted<?> m : getEntity().getWeaponList()) {
            WeaponType mt = (WeaponType) m.getType();

            // Don't think this can happen, but ...
            if (m.getLocation() == Entity.LOC_NONE) {
                continue;
            }

            buff.append(StringUtil.makeLength(mt.getName(), 20));
            buff.append(
                    StringUtil.makeLength(getLocationAbbr(m.getLocation()),
                            getPrintSize() - 5 - 20))
                    .append(TestEntity.makeWeightString(m.getTonnage(), usesKgStandard())).append("\n");
        }
        return buff;
    }

    public double getWeightAmmo() {
        double weight = 0.0;
        for (Mounted<?> m : getEntity().getAmmo()) {

            // One Shot Ammo
            if (m.getLocation() == Entity.LOC_NONE) {
                continue;
            }

            // Bombs on ASF don't count!
            if ((getEntity() instanceof Aero) && (m.getType() instanceof BombType)) {
                continue;
            }

            weight += m.getTonnage();
        }
        return weight;
    }

    public abstract double getWeightPowerAmp();

    public StringBuffer printAmmo() {
        return printAmmo(new StringBuffer());
    }

    public StringBuffer printAmmo(StringBuffer buff) {
        return printAmmo(buff, 20, getPrintSize());
    }

    public StringBuffer printAmmo(StringBuffer buff, int posLoc, int posWeight) {
        for (Mounted<?> m : getEntity().getAmmo()) {
            AmmoType mt = (AmmoType) m.getType();

            // Don't think this can happen, but ...
            if (m.getLocation() == Entity.LOC_NONE) {
                continue;
            }

            buff.append(StringUtil.makeLength(mt.getName(), 20));
            buff.append(" ").append(
                    StringUtil.makeLength(getLocationAbbr(m.getLocation()),
                            getPrintSize() - 5 - 20))
                    .append(TestEntity.makeWeightString(m.getTonnage(), usesKgStandard())).append("\n");
        }
        return buff;
    }

    public String printLocations() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getEntity().locations(); i++) {
            String locationName = getEntity().getLocationName(i);
            sb.append(locationName).append(":");
            sb.append("\n");
            for (int j = 0; j < getEntity().getNumberOfCriticals(i); j++) {
                CriticalSlot slot = getEntity().getCritical(i, j);
                if (slot == null) {
                    sb.append(j).append(". -Empty-");
                    sb.append("\n");
                } else if (slot.getType() == CriticalSlot.TYPE_SYSTEM) {
                    if (isMek()) {
                        sb.append(j).append(". ")
                                .append(((Mek) getEntity()).getSystemName(slot.getIndex()))
                                .append("\n");
                    } else {
                        sb.append(j).append(". UNKNOWN SYSTEM NAME").append("\n");
                    }
                } else if (slot.getType() == CriticalSlot.TYPE_EQUIPMENT) {
                    EquipmentType e = getEntity().getEquipmentType(slot);
                    sb.append(j).append(". ").append(e.getInternalName()).append("\n");
                }
            }
        }
        return sb.toString();
    }

    public int calcMiscCrits(MiscType mt, double size) {
        if (mt.hasFlag(MiscType.F_CLUB)
                && (mt.hasSubType(MiscType.S_HATCHET)
                        || mt.hasSubType(MiscType.S_SWORD)
                        || mt.hasSubType(MiscType.S_CHAIN_WHIP))) {
            return (int) Math.ceil(getWeight() / 15.0);
        } else if (mt.hasFlag(MiscType.F_CLUB) && mt.hasSubType(MiscType.S_MACE)) {
            return (int) Math.ceil(getWeight() / 10.0);
        } else if (mt.hasFlag(MiscType.F_CLUB) && mt.hasSubType(MiscType.S_RETRACTABLE_BLADE)) {
            return 1 + (int) Math.ceil(getWeight() / 20.0);
        } else if (mt.hasFlag(MiscType.F_CLUB) && mt.hasSubType(MiscType.S_PILE_DRIVER)) {
            return 8;
        } else if (mt.hasFlag(MiscType.F_CLUB) && mt.hasSubType(MiscType.S_CHAINSAW)) {
            return 5;
        } else if (mt.hasFlag(MiscType.F_CLUB) && mt.hasSubType(MiscType.S_DUAL_SAW)) {
            return 7;
        } else if (mt.hasFlag(MiscType.F_CLUB) && mt.hasSubType(MiscType.S_BACKHOE)) {
            return 6;
        } else if (mt.hasFlag(MiscType.F_MASC)) {
            if (mt.getInternalName().equals("ISMASC")) {
                return (int) Math.round(getWeight() / 20.0);
            } else if (mt.getInternalName().equals("CLMASC")) {
                return (int) Math.round(getWeight() / 25.0);
            }
        } else if (mt.hasFlag(MiscType.F_TARGCOMP)) {
            double fTons = 0.0f;
            for (WeaponMounted mounted : getEntity().getWeaponList()) {
                if (mounted.getType().hasFlag(WeaponType.F_DIRECT_FIRE)) {
                    fTons += mounted.getTonnage();
                }
            }
            for (MiscMounted mounted : getEntity().getMisc()) {
               if (mounted.getType().hasFlag(MiscType.F_RISC_LASER_PULSE_MODULE)) {
                    fTons += mounted.getTonnage();
                }
            }
            double weight = 0.0f;
            if (mt.getInternalName().equals("ISTargeting Computer")) {
                weight = TestEntity.ceil(fTons / 4.0f,
                        getWeightCeilingTargComp());
            } else if (mt.getInternalName().equals("CLTargeting Computer")) {
                weight = TestEntity.ceil(fTons / 5.0f,
                        getWeightCeilingTargComp());
            }
            switch (getTargCompCrits()) {
                case CEIL_TARGCOMP_CRITS:
                    return (int) Math.ceil(weight);
                case ROUND_TARGCOMP_CRITS:
                    return (int) Math.round(weight);
                case FLOOR_TARGCOMP_CRITS:
                    return (int) Math.floor(weight);
            }
        } else if (EquipmentType.getArmorTypeName(
                EquipmentType.T_ARMOR_FERRO_FIBROUS).equals(
                        mt.getInternalName())) {
            if (isClanArmor()) {
                return 7;
            }
            return 14;
        } else if (EquipmentType.getArmorTypeName(
                EquipmentType.T_ARMOR_FERRO_FIBROUS_PROTO).equals(
                        mt.getInternalName())) {
            return 16;
        } else if (EquipmentType.getArmorTypeName(
                EquipmentType.T_ARMOR_LIGHT_FERRO).equals(mt.getInternalName())) {
            return 7;
        } else if (EquipmentType.getArmorTypeName(
                EquipmentType.T_ARMOR_HEAVY_FERRO).equals(mt.getInternalName())) {
            return 21;
        } else if (mt.hasFlag(MiscType.F_ENDO_STEEL)) {
            if (isClan()
                    || mt.getInternalName()
                            .equals("Clan "
                                    + EquipmentType
                                            .getStructureTypeName(EquipmentType.T_STRUCTURE_ENDO_STEEL))) {
                return 7;
            }
            return 14;
        } else if (mt.hasFlag(MiscType.F_ENDO_STEEL_PROTO)) {
            return 16;
        } else if (mt.hasFlag(MiscType.F_ENDO_COMPOSITE)) {
            if (isClan()
                    || mt.getInternalName()
                            .equals("Clan "
                                    + EquipmentType
                                            .getStructureTypeName(EquipmentType.T_STRUCTURE_ENDO_COMPOSITE))) {
                return 4;
            }
            return 7;
        } else if (mt.hasFlag(MiscType.F_REACTIVE)) {
            if (isClanArmor()) {
                return 7;
            }
            return 14;
        } else if (mt.hasFlag(MiscType.F_REFLECTIVE)) {
            if (isClanArmor()) {
                return 5;
            }
            return 10;
        }
        return mt.getCriticals(getEntity(), size);
    }

    /**
     * Computes heat sink requirement for heat-neutral units (vehicles, conventional
     * fighters,
     * protomeks). This is a total of energy weapons that don't use ammo and some
     * other miscellaneous
     * equipment.
     *
     * @return The number of heat sinks required in construction
     */
    protected int heatNeutralHSRequirement() {
        return calcHeatNeutralHSRequirement(getEntity());
    }

    /**
     * Computes heat sink requirement for heat-neutral units (vehicles, conventional
     * fighters,
     * protomeks). This is a total of energy weapons that don't use ammo and some
     * other miscellaneous
     * equipment.
     *
     * @return The number of heat sinks required in construction
     */
    public static int calcHeatNeutralHSRequirement(Entity entity) {
        int heat = 0;
        for (Mounted<?> m : entity.getWeaponList()) {
            WeaponType wt = (WeaponType) m.getType();
            if ((wt.hasFlag(WeaponType.F_LASER) && (wt.getAmmoType() == AmmoType.AmmoTypeEnum.NA))
                    || wt.hasFlag(WeaponType.F_PPC)
                    || wt.hasFlag(WeaponType.F_PLASMA)
                    || wt.hasFlag(WeaponType.F_PLASMA_MFUK)
                    || (wt.hasFlag(WeaponType.F_FLAMER) && (wt.getAmmoType() == AmmoType.AmmoTypeEnum.NA))) {
                heat += wt.getHeat();
            }
            // laser insulator reduce heat by 1, to a minimum of 1
            if (wt.hasFlag(WeaponType.F_LASER) && (m.getLinkedBy() != null)
                    && !m.getLinkedBy().isInoperable()
                    && m.getLinkedBy().getType().hasFlag(MiscType.F_LASER_INSULATOR)) {
                heat -= 1;
                if (heat == 0) {
                    heat++;
                }
            }

            if ((m.getLinkedBy() != null) && (m.getLinkedBy().getType() instanceof MiscType)
                    && m.getLinkedBy().getType().hasFlag(MiscType.F_PPC_CAPACITOR)) {
                heat += 5;
            }
        }
        for (Mounted<?> m : entity.getMisc()) {
            // Spot welders are treated as energy weapons on units that don't have a fusion
            // or fission engine
            if (m.getType().hasFlag(MiscType.F_CLUB) && m.getType().hasSubType(MiscType.S_SPOT_WELDER)
                    && entity.hasEngine() && (entity.getEngine().isFusion()
                            || (entity.getEngine().getEngineType() == Engine.FISSION))) {
                continue;
            }
            heat += m.getType().getHeat();
        }
        if (entity.hasStealth()) {
            heat += 10;
        }
        return heat;
    }

    /**
     * According to TM, unit weights are to be rounded up to the nearest half ton or
     * kilo. This method
     * returns the rounded weight.
     *
     * @return The weight of the unit, rounded up according to TM, p.22.
     */
    public double calculateWeight() {
        double weight = calculateWeightExact();
        // If the unit used kg standard, we just need to get rid of floating-point math
        // anomalies.
        // Otherwise accumulated kg-scale equipment needs to be rounded up to the
        // nearest half-ton.
        weight = round(weight, Ceil.KILO);
        if (usesKgStandard()) {
            return weight;
        } else {
            return ceil(weight, Ceil.HALFTON);
        }
    }

    /**
     * According to TM p.22, unit weights are to be rounded up to the nearest half ton or kilo, but in MML
     * for construction at least we should be able to show the exact weight. This method returns the unrounded
     * weight.
     *
     * @return The unrounded weight of the unit.
     */
    public double calculateWeightExact() {
        double weight = 0;
        weight += getWeightEngine();
        weight += getWeightStructure();
        weight += getWeightControls();
        weight += getWeightHeatSinks();
        if (getEntity().hasPatchworkArmor()) {
            weight += getWeightAllocatedArmor();
        } else {
            weight += getWeightArmor();
        }
        weight += getWeightMisc();

        weight += getWeightMiscEquip();
        weight += getWeightWeapon();
        weight += getWeightAmmo();
        weight += getWeightPowerAmp();

        weight += getWeightCarryingSpace();

        weight += getArmoredComponentWeight();
        return weight;
    }

    public String printWeightCalculation() {
        return printWeightEngine() + printWeightStructure()
                + printWeightControls() + printWeightHeatSinks()
                + printWeightArmor() + printWeightMisc()
                + printWeightCarryingSpace() + "Equipment:\n"
                + printMiscEquip() + printWeapon() + printAmmo();
    }

    public boolean correctWeight(StringBuffer buff) {
        return correctWeight(buff, showOverweightedEntity(),
                showUnderweightedEntity());
    }

    public boolean correctWeight(StringBuffer buff, boolean showO, boolean showU) {
        double weightSum = calculateWeight();
        double weight = getWeight();

        if (showO && ((weight + getMaxOverweight()) < weightSum)) {
            buff.append("Weight: ").append(calculateWeight())
                    .append(" is greater than ").append(getWeight())
                    .append("\n");
            // buff.append(printWeightCalculation()).append("\n");
            return false;
        }
        if (showU && ((weight - getMinUnderweight()) > weightSum)) {
            buff.append("Weight: ").append(calculateWeight())
                    .append(" is less than ").append(getWeight()).append("\n");
            // buff.append(printWeightCalculation()).append("\n");
            return false;
        }
        return true;
    }

    /**
     * For units without patchwork armor, tests if more armor is allocated on the unit's locations than it may have according to its armor
     * tonnage. If it is, returns false and appends an error message to the given error buffer. Otherwise or if the unit has patchwork
     * armor, returns true.
     *
     * @param entity The unit to test
     * @param buff   A StringBuffer to write error messages to
     * @return False when there is surplus armor allocated and the unit is invalid, true otherwise
     */
    protected boolean correctArmorOverAllocation(Entity entity, @Nullable StringBuffer buff) {
        if (!entity.hasPatchworkArmor() && (entity.getTotalOArmor() > getArmorPoints(entity))) {
            if (buff != null) {
                buff.append("More armor allocated than is available according to armor tonnage.\n");
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns the total number of armor points available to the unit for a given tonnage of armor. This does not round down the
     * calculation or take into account any maximum number of armor points or tonnage allowed to the unit. It also does not include any free
     * armor points due to SI on aerospace units.
     *
     * NOTE: only use for non-patchwork armor
     *
     * @return the number of armor points available for the armor tonnage
     */
    public static double getRawArmorPoints(Entity unit, double armorTons) {
        if (unit.hasETypeFlag(Entity.ETYPE_PROTOMEK)) {
            return Math.round(armorTons / ArmorType.forEntity(unit).getWeightPerPoint());
        } else if (unit.isSupportVehicle()) {
            return Math.floor(armorTons / TestSupportVehicle.armorWeightPerPoint(unit));
        } else if ((unit instanceof Jumpship)
            && unit.getArmorType(unit.firstArmorIndex()) == EquipmentType.T_ARMOR_PRIMITIVE_AERO) {
            // Because primitive JumpShip armor has an extra step of rounding we have to give it special treatment.
            // Standard armor value is computed first, rounded down, then the primitive armor mod is applied.
            return Math.floor(Math.floor(armorTons * TestAdvancedAerospace.armorPointsPerTon((Jumpship) unit,
                EquipmentType.T_ARMOR_AEROSPACE, false)) * 0.66);
        }
        return armorTons * getArmorPointsPerTon(unit);
    }

    /**
     * Returns the number of armor points the given unit has with the given armor tonnage. This includes bonus armor points for SI on DS/SC
     * and capital craft.
     *
     * NOTE: only use for non-patchwork armor
     *
     * @param unit      the unit
     * @param armorTons the armor tonnage
     * @return the armor point total of the unit, including SI bonus armor
     */
    public static int getArmorPoints(Entity unit, double armorTons) {
        int raw = (int) Math.floor(getRawArmorPoints(unit, armorTons) + TestEntity.getSIBonusArmorPoints(unit));
        return Math.min(raw, getMaximumArmorPoints(unit));
    }

    /**
     * Returns the number of armor points the given unit has. This includes bonus armor points for SI on DS/SC and capital craft.
     *
     * NOTE: only use for non-patchwork armor
     *
     * @param unit the unit
     * @return the armor point total of the unit, including SI bonus armor
     */
    public static int getArmorPoints(Entity unit) {
        return getArmorPoints(unit, unit.getLabArmorTonnage());
    }

    /**
     * Calculate the number of armor points per ton of armor for the given unit.
     *
     * @param en The unit
     * @return The number of armor points per ton
     */
    public static double getArmorPointsPerTon(Entity en) {
        ArmorType armor = ArmorType.forEntity(en);
        if (armor.hasFlag(MiscType.F_SUPPORT_VEE_BAR_ARMOR)) {
            return 1.0 / armor.getSVWeightPerPoint(en.getArmorTechRating());
        } else {
            return armor.getPointsPerTon(en);
        }
    }

    public static int getMaximumArmorPoints(Entity unit) {
        int points = 0;
        if (unit.hasETypeFlag(Entity.ETYPE_MEK)) {
            int headPoints = 3;
            if (unit.getWeightClass() == EntityWeightClass.WEIGHT_SUPER_HEAVY) {
                headPoints = 4;
            }
            points = (unit.getTotalInternal() * 2) + headPoints;
        } else if (unit.hasETypeFlag(Entity.ETYPE_PROTOMEK)) {
            points = TestProtoMek.maxArmorFactor((ProtoMek) unit);
        } else if (unit.isSupportVehicle()) {
            points = TestSupportVehicle.maxArmorFactor(unit);
        } else if (unit.hasETypeFlag(Entity.ETYPE_TANK)) {
            points = (int) Math.floor((unit.getWeight() * 3.5) + 40);
        } else if (unit.hasETypeFlag(Entity.ETYPE_BATTLEARMOR)) {
            points = (unit.getWeightClass() * 4) + 2;
        } else if (unit.hasETypeFlag(Entity.ETYPE_JUMPSHIP)) {
            points = TestAdvancedAerospace.maxArmorPoints((Jumpship) unit);
        } else if (unit.hasETypeFlag(Entity.ETYPE_SMALL_CRAFT)) {
            points = TestSmallCraft.maxArmorPoints((SmallCraft) unit);
        } else if (unit.hasETypeFlag(Entity.ETYPE_CONV_FIGHTER)) {
            points = (int) Math.floor(unit.getWeight());
        } else if (unit.hasETypeFlag(Entity.ETYPE_AERO)) {
            points = (int) Math.floor(unit.getWeight() * 8);
        }
        return points;
    }

    /**
     * Returns the number of free additional armor points provided for aerospace vessels based on their SI. This is usually a whole number
     * but may be a fractional amount for primitive JumpShips. It is the total number, which is usually divided evenly among armor facings.
     * For units other than SC/DS and capital craft, this is 0. See TM p.191, SO:AA p.140, IO:AE p.119-125
     *
     * @param entity The unit to compute bonus armor for
     * @return The total number of extra armor points received for SI
     */
    public static double getSIBonusArmorPoints(Entity entity) {
        double points = 0;
        if (entity instanceof SmallCraft smallCraft) {
            points = smallCraft.getSI() * (entity.locations() - 1);
        } else if (entity instanceof Jumpship jumpship) {
            points = Math.round(jumpship.getSI() / 10.0) * 6;
        }
        if (entity.isPrimitive()) {
            return points * ArmorType.of(EquipmentType.T_ARMOR_PRIMITIVE_AERO, false).getArmorPointsMultiplier();
        } else {
            return points;
        }
    }

    public boolean hasIllegalTechLevels(StringBuffer buff) {
        return hasIllegalTechLevels(buff, getEntity().getTechLevel());
    }

    public boolean hasIllegalTechLevels(StringBuffer buff, int ammoTechLvl) {
        /*
         * A large number of units have official tech levels lower than their components
         * at the
         * intro date. We test instead whether the stated tech level is ever possible
         * based on the
         * equipment. We also test for mixed IS/Clan tech in units that are not
         * designated as mixed.
         */
        boolean retVal = false;
        int eTechLevel = SimpleTechLevel.convertCompoundToSimple(getEntity().getTechLevel()).ordinal();
        int ammoRulesLevel = SimpleTechLevel.convertCompoundToSimple(ammoTechLvl).ordinal();
        int eRulesLevel = getEntity().findMinimumRulesLevel().ordinal();
        if ((eTechLevel >= eRulesLevel) && (getEntity().getEarliestTechDate() <= getEntity().getYear())) {
            return false;
        }

        int eTLYear = getEntity().getTechLevelYear();
        for (Mounted<?> mounted : getEntity().getEquipment()) {
            EquipmentType nextE = mounted.getType();
            int eqRulesLevel = getEntity().isMixedTech()
                    ? nextE.findMinimumRulesLevel().ordinal()
                    : nextE.findMinimumRulesLevel(getEntity().isClan()).ordinal();
            boolean illegal = eqRulesLevel > eRulesLevel;
            if (!getEntity().isMixedTech()) {
                illegal |= getEntity().isClan() && nextE.getTechBase() == ITechnology.TechBase.IS;
                illegal |= !getEntity().isClan() && nextE.getTechBase() == ITechnology.TechBase.CLAN;
            }
            int eqTechLevel = TechConstants.convertFromSimplelevel(eqRulesLevel, nextE.isClan());
            if (nextE instanceof AmmoType) {
                if (eqRulesLevel > ammoRulesLevel) {
                    if (!retVal) {
                        buff.append("Ammo illegal at unit's tech level (");
                        buff.append(TechConstants
                                .getLevelDisplayableName(ammoTechLvl));
                        buff.append(", ");
                        buff.append(eTLYear);
                        buff.append("):\n");
                    }
                    retVal = true;
                    buff.append(nextE.getName());
                    buff.append(", (");
                    buff.append(TechConstants
                            .getLevelDisplayableName(eqTechLevel));
                    buff.append(")\n");
                }
            } else if (illegal) {
                if (!retVal) {
                    buff.append("Equipment illegal at unit's tech level ");
                    buff.append(TechConstants
                            .getLevelDisplayableName(ammoTechLvl));
                    buff.append(", ");
                    buff.append(eTLYear);
                    buff.append("):\n");
                }
                retVal = true;
                buff.append(nextE.getName());
                buff.append(", (");
                buff.append(TechConstants
                        .getLevelDisplayableName(eqTechLevel));
                buff.append(")\n");
            }
        }
        // Check cockpit TL
        ITechnology cockpit = null;
        String cockpitName = null;
        if (getEntity().hasETypeFlag(Entity.ETYPE_AERO)) {
            cockpit = ((Aero) getEntity()).getCockpitTechAdvancement();
            cockpitName = ((Aero) getEntity()).getCockpitTypeString();
        } else if (getEntity() instanceof Mek) {
            cockpit = ((Mek) getEntity()).getCockpitTechAdvancement();
            cockpitName = ((Mek) getEntity()).getCockpitTypeString();
        }
        if (cockpit != null) {
            int eqRulesLevel = getEntity().isMixedTech()
                    ? cockpit.findMinimumRulesLevel().ordinal()
                    : cockpit.findMinimumRulesLevel(getEntity().isClan()).ordinal();
            boolean illegal = eqRulesLevel > eRulesLevel;
            if (!getEntity().isMixedTech()) {
                illegal |= getEntity().isClan() && cockpit.getTechBase() == ITechnology.TechBase.IS;
                illegal |= !getEntity().isClan() && cockpit.getTechBase() == ITechnology.TechBase.CLAN;
            }
            if (illegal) {
                buff.append("Cockpit is illegal at unit's tech level (");
                buff.append(TechConstants
                        .getLevelDisplayableName(eTechLevel));
                buff.append(", ");
                buff.append(eTLYear);
                buff.append("): ");
                buff.append(cockpitName);
                buff.append(" (");
                buff.append(TechConstants
                        .getLevelDisplayableName(TechConstants.convertFromSimplelevel(eqRulesLevel, cockpit.isClan())));
                buff.append(")\n");
                retVal = true;
            }
        }
        if (getEntity() instanceof Mek) {
            ITechnology gyro = ((Mek) getEntity()).getGyroTechAdvancement();
            if (gyro != null) {
                int eqRulesLevel = getEntity().isMixedTech()
                        ? gyro.findMinimumRulesLevel().ordinal()
                        : gyro.findMinimumRulesLevel(getEntity().isClan()).ordinal();
                boolean illegal = eqRulesLevel > eRulesLevel;
                if (!getEntity().isMixedTech()) {
                    illegal |= getEntity().isClan() && gyro.getTechBase() == ITechnology.TechBase.IS;
                    illegal |= !getEntity().isClan() && gyro.getTechBase() == ITechnology.TechBase.CLAN;
                }
                if (illegal) {
                    buff.append("Gyro is illegal at unit's tech level (");
                    buff.append(TechConstants
                            .getLevelDisplayableName(eTechLevel));
                    buff.append(", ");
                    buff.append(eTLYear);
                    buff.append("): ");
                    buff.append(((Mek) getEntity()).getGyroTypeString());
                    buff.append(" (");
                    buff.append(TechConstants
                            .getLevelDisplayableName(TechConstants.convertFromSimplelevel(eqRulesLevel,
                                    gyro.isClan())));
                    buff.append(")\n");
                    retVal = true;
                }
            }
        }
        if (getEntity().hasEngine()) {
            ITechnology engine = getEntity().getEngine().getTechAdvancement();
            int eqRulesLevel = getEntity().isMixedTech()
                    ? engine.findMinimumRulesLevel().ordinal()
                    : engine.findMinimumRulesLevel(getEntity().isClan()).ordinal();
            boolean illegal = eqRulesLevel > eRulesLevel;
            if (!getEntity().isMixedTech()) {
                illegal |= getEntity().isClan() && engine.getTechBase() == ITechnology.TechBase.IS;
                illegal |= !getEntity().isClan() && engine.getTechBase() == ITechnology.TechBase.CLAN;
            }
            if (illegal) {
                buff.append("Engine is illegal at unit's tech level (");
                buff.append(TechConstants
                        .getLevelDisplayableName(eTechLevel));
                buff.append(", ");
                buff.append(eTLYear);
                buff.append("): ");
                buff.append(getEntity().getEngine().getShortEngineName());
                buff.append(" (");
                buff.append(TechConstants
                        .getLevelDisplayableName(TechConstants.convertFromSimplelevel(eqRulesLevel,
                                engine.isClan())));
                buff.append(")\n");
                buff.append("\n");
                retVal = true;
            }
        }
        Set<String> armors;
        if (!getEntity().hasPatchworkArmor()) {
            armors = Collections.singleton(EquipmentType.getArmorTypeName(getEntity().getArmorType(1),
                    TechConstants.isClan(getEntity().getArmorTechLevel(1))));
        } else {
            int eqRulesLevel = getEntity().isMixedTech()
                    ? Entity.getPatchworkArmorAdvancement().findMinimumRulesLevel().ordinal()
                    : Entity.getPatchworkArmorAdvancement().findMinimumRulesLevel(getEntity().isClan()).ordinal();
            if (eqRulesLevel > eRulesLevel) {
                buff.append("Armor is illegal at unit's tech level (");
                buff.append(TechConstants
                        .getLevelDisplayableName(eTechLevel));
                buff.append(", ");
                buff.append(eTLYear);
                buff.append("): Patchwork (");
                buff.append(TechConstants
                        .getLevelDisplayableName(TechConstants.convertFromSimplelevel(eqRulesLevel,
                                getEntity().isClan())));
                buff.append(")\n");
                buff.append("\n");
                retVal = true;
            }

            armors = new HashSet<>();
            for (int loc = 0; loc < getEntity().locations(); loc++) {
                armors.add(EquipmentType.getArmorTypeName(getEntity().getArmorType(loc),
                        TechConstants.isClan(getEntity().getArmorTechLevel(loc))));
            }
        }
        for (String atName : armors) {
            EquipmentType at = EquipmentType.get(atName);
            // Can be null in the case of vehicle body or asf wings.
            if (at == null) {
                continue;
            }
            int eqRulesLevel = getEntity().isMixedTech() ? at.findMinimumRulesLevel().ordinal()
                    : at.findMinimumRulesLevel(getEntity().isClan()).ordinal();
            boolean illegal = eqRulesLevel > eRulesLevel;
            if (!getEntity().isMixedTech()) {
                illegal |= getEntity().isClan() && at.getTechBase() == ITechnology.TechBase.IS;
                illegal |= !getEntity().isClan() && at.getTechBase() == ITechnology.TechBase.CLAN;
            }
            if (illegal) {
                buff.append("Armor is illegal at unit's tech level (");
                buff.append(TechConstants
                        .getLevelDisplayableName(eTechLevel));
                buff.append(", ");
                buff.append(eTLYear);
                buff.append("): ");
                buff.append(atName);
                buff.append(" (");
                buff.append(TechConstants.getLevelDisplayableName(TechConstants.convertFromSimplelevel(
                        eqRulesLevel, at.isClan())));
                buff.append(")\n");
                buff.append("\n");
                retVal = true;
            }
        }

        return retVal;
    }

    /**
     * Compares intro dates of all components to the unit intro year.
     *
     * @param buff Descriptions of problems will be added to the buffer.
     * @return Whether the unit has an intro year equal to or later than all the
     *         components.
     */
    public boolean hasIncorrectIntroYear(StringBuffer buff) {
        boolean retVal = false;
        if (getEntity().getEarliestTechDate() <= getEntity().getYear() + getIntroYearMargin()) {
            return false;
        }
        int useIntroYear = getEntity().getYear() + getIntroYearMargin();
        if (getEntity().isOmni()) {
            int introDate = Entity.getOmniAdvancement(getEntity()).getIntroductionDate(
                    getEntity().isClan() || getEntity().isMixedTech());
            if (useIntroYear < introDate) {
                retVal = true;
                buff.append("Omni technology has intro date of ");
                buff.append(introDate);
                buff.append("\n");
            }
        }
        Set<EquipmentType> checked = new HashSet<>();
        for (Mounted<?> mounted : getEntity().getEquipment()) {
            final EquipmentType nextE = mounted.getType();
            if (checked.contains(nextE) || (nextE instanceof AmmoType)) {
                continue;
            }
            checked.add(nextE);
            int introDate = nextE.getIntroductionDate(getEntity().isClan());
            if (getEntity().isMixedTech()) {
                introDate = nextE.getIntroductionDate();
            }

            if (introDate > useIntroYear) {
                retVal = true;
                buff.append(nextE.getName());
                buff.append(" has intro date of ");
                buff.append(introDate);
                buff.append("\n");
            }
        }
        Set<String> armors;
        if (!getEntity().hasPatchworkArmor()) {
            armors = Collections.singleton(EquipmentType.getArmorTypeName(getEntity().getArmorType(1),
                    TechConstants.isClan(getEntity().getArmorTechLevel(1))));
        } else {
            int intro = getEntity().isMixedTech()
                    ? Entity.getPatchworkArmorAdvancement().getIntroductionDate()
                    : Entity.getPatchworkArmorAdvancement().getIntroductionDate(getEntity().isClan());
            if (useIntroYear < intro) {
                retVal = true;
                buff.append("Patchwork armor has intro date of ");
                buff.append(intro);
                buff.append("\n");
            }
            armors = new HashSet<>();
            for (int loc = 0; loc < getEntity().locations(); loc++) {
                armors.add(EquipmentType.getArmorTypeName(getEntity().getArmorType(loc),
                        TechConstants.isClan(getEntity().getArmorTechLevel(loc))));
            }
        }
        for (String atName : armors) {
            EquipmentType at = EquipmentType.get(atName);
            if (checked.contains(at)) {
                continue;
            }
            checked.add(at);
            // Can be null in the case of vehicle body or asf wings.
            if (at == null) {
                continue;
            }
            int introDate = at.getIntroductionDate(getEntity().isClan());
            if (getEntity().isMixedTech()) {
                introDate = at.getIntroductionDate();
            }
            if (introDate > useIntroYear) {
                retVal = true;
                buff.append(at.getName());
                buff.append(" armor has intro date of ");
                buff.append(introDate);
                buff.append("\n");
            }
        }
        // Check cockpit TL
        ITechnology cockpit = null;
        String cockpitName = null;
        if ((getEntity() instanceof Aero) && !getEntity().isSupportVehicle()) {
            cockpit = ((Aero) getEntity()).getCockpitTechAdvancement();
            cockpitName = ((Aero) getEntity()).getCockpitTypeString();
        } else if (getEntity() instanceof Mek) {
            cockpit = ((Mek) getEntity()).getCockpitTechAdvancement();
            cockpitName = ((Mek) getEntity()).getCockpitTypeString();
        }
        if (null != cockpit) {
            int introDate = cockpit.getIntroductionDate(getEntity().isClan());
            if (getEntity().isMixedTech()) {
                introDate = cockpit.getIntroductionDate();
            }
            if (introDate > useIntroYear) {
                retVal = true;
                buff.append(cockpitName);
                buff.append(" has intro date of ");
                buff.append(introDate);
                buff.append("\n");
            }
        }
        if (getEntity() instanceof Mek) {
            ITechnology gyro = ((Mek) getEntity()).getGyroTechAdvancement();
            if (null != gyro) {
                int introDate = gyro.getIntroductionDate(getEntity().isClan());
                if (getEntity().isMixedTech()) {
                    introDate = gyro.getIntroductionDate();
                }
                if (introDate > useIntroYear) {
                    retVal = true;
                    buff.append(((Mek) getEntity()).getGyroTypeString());
                    buff.append(" has intro date of ");
                    buff.append(introDate);
                    buff.append("\n");
                }
            }
        }
        if (getEntity().hasEngine()) {
            ITechnology engine = getEntity().getEngine().getTechAdvancement();
            int introDate = engine.getIntroductionDate(getEntity().isClan());
            if (getEntity().isMixedTech()) {
                introDate = engine.getIntroductionDate();
            }
            if (introDate > useIntroYear) {
                retVal = true;
                buff.append(getEntity().getEngine().getShortEngineName());
                buff.append(" has intro date of ");
                buff.append(introDate);
                buff.append("\n");
            }
        }

        return retVal;
    }

    public boolean hasFailedEquipment(StringBuffer buff) {
        boolean hasFailedEquipment = false;
        for (Iterator<String> e = getEntity().getFailedEquipment(); e.hasNext();) {
            String name = e.next();
            if (!ignoreFailedEquip(name)) {
                if (!hasFailedEquipment) {
                    buff.append("Equipment that Failed to Load:\n");
                }
                buff.append(name).append("\n");
                hasFailedEquipment = true;
            }
        }

        return hasFailedEquipment;
    }

    /**
     * Check if the unit has combinations of equipment which are not allowed in
     * the construction rules.
     *
     * @param buff
     *             diagnostics are appended to this
     * @return true if the entity is illegal
     */
    public boolean hasIllegalEquipmentCombinations(StringBuffer buff) {
        boolean illegal = false;
        int fieldKitchenCount = 0;
        int minesweeperCount = 0;
        boolean hasHarjelII = false;
        boolean hasHarjelIII = false;
        boolean hasCoolantPod = false;
        int emergencyCoolantCount = 0;
        int networks = 0;
        boolean countedC3 = false;
        int robotics = 0;
        boolean hasExternalFuelTank = false;
        int liftHoists = 0;
        int artemisIV = 0;
        int artemisV = 0;
        int artemisP = 0;
        int apollo = 0;
        Map<Integer, Integer> bridgeLayersByLocation = new HashMap<>();
        Map<Integer, List<EquipmentType>> physicalWeaponsByLocation = new HashMap<>();

        for (Mounted<?> m : getEntity().getAmmo()) {
            if (((AmmoType) m.getType()).getAmmoType() == AmmoType.AmmoTypeEnum.COOLANT_POD) {
                hasCoolantPod = true;
            }
        }
        for (Mounted<?> m : getEntity().getMisc()) {
            if (m.getType().hasFlag(MiscType.F_EMERGENCY_COOLANT_SYSTEM)) {
                emergencyCoolantCount++;
            }
            if (m.getType().hasFlag(MiscType.F_FIELD_KITCHEN)) {
                fieldKitchenCount++;
            }
            if (m.getType().hasFlag(MiscType.F_MINESWEEPER)) {
                minesweeperCount++;
            }

            if (m.getType().hasFlag(MiscType.F_LIGHT_FLUID_SUCTION_SYSTEM)) {
                if (getEntity() instanceof ProtoMek) {
                    illegal = true;
                    buff.append("ProtoMek can't mount light fluid suction system\n");
                }
            }
            if (m.getType().hasFlag(MiscType.F_VOIDSIG)
                    && !getEntity().hasWorkingMisc(MiscType.F_ECM)) {
                illegal = true;
                buff.append("void signature system needs ECM suite\n");
            }
            if (m.getType().hasFlag(MiscType.F_HARJEL_II)) {
                hasHarjelII = true;
            }
            if (m.getType().hasFlag(MiscType.F_HARJEL_III)) {
                hasHarjelIII = true;
            }
            if (m.getType().hasFlag(MiscType.F_FUEL)) {
                hasExternalFuelTank = true;
            }
            if ((m.getType().hasFlag(MiscType.F_C3S) || m.getType().hasFlag(MiscType.F_C3SBS)) && !countedC3) {
                networks++;
                countedC3 = true;
            }
            if (m.getType().hasFlag(MiscType.F_C3I) || m.getType().hasFlag(MiscType.F_NOVA)) {
                networks++;
            }
            if (m.is(Sensor.NOVA) && (!getEntity().hasEngine() || !getEntity().getEngine().isFusion())) {
                buff.append("Nova CEWS may only be used on units with a fusion engine\n");
                illegal = true;
            }
            if (m.getType().hasFlag(MiscType.F_SRCS) || m.getType().hasFlag(MiscType.F_SASRCS)
                    || m.getType().hasFlag(MiscType.F_CASPAR) || m.getType().hasFlag(MiscType.F_CASPARII)) {
                robotics++;
            }
            if (m.getType().hasFlag(MiscType.F_LIFTHOIST)) {
                liftHoists++;
            } else if ((m.getLocation() > 0)
                    && ((m.getType().hasFlag(MiscType.F_CLUB) && !((MiscType) m.getType()).isShield())
                            || m.getType().hasFlag(MiscType.F_BULLDOZER)
                            || m.getType().hasFlag(MiscType.F_HAND_WEAPON))) {
                physicalWeaponsByLocation.computeIfAbsent(m.getLocation(), ArrayList::new).add(m.getType());
            } else if (m.getType().hasFlag(MiscType.F_LIGHT_BRIDGE_LAYER)
                    || m.getType().hasFlag(MiscType.F_MEDIUM_BRIDGE_LAYER)
                    || m.getType().hasFlag(MiscType.F_HEAVY_BRIDGE_LAYER)) {
                bridgeLayersByLocation.merge(m.getLocation(), 1, Integer::sum);
            }

            if (m.getType().hasFlag(MiscType.F_ARTEMIS)) {
                artemisIV++;
            } else if (m.getType().hasFlag(MiscType.F_ARTEMIS_V)) {
                artemisV++;
            } else if (m.getType().hasFlag(MiscType.F_ARTEMIS_PROTO)) {
                artemisP++;
            } else if (m.getType().hasFlag(MiscType.F_APOLLO)) {
                apollo++;
            }

            if (m.getType().hasFlag(MiscType.F_PPC_CAPACITOR)) {
                if (m.getLinked() == null) {
                    buff
                        .append(m.getType().getName())
                        .append(" in ")
                        .append(getEntity().getLocationAbbr(m.getLocation()))
                        .append(" has no linked PPC\n");
                    illegal = true;
                }
            }

            if (m.getType().hasFlag(MiscType.F_LASER_INSULATOR) &&
                    ((m.getLinked() == null)
                            || (m.getLinked().getLocation() != m.getLocation())
                            || !(m.getLinked().getType() instanceof WeaponType)
                            || !(
                                    m.getLinked().getType().hasFlag(WeaponType.F_LASER)
                                    || m.getLinked().getType() instanceof CLChemicalLaserWeapon
                                ))) {
                buff.append("Laser insulator requires a laser in the same location.\n");
                illegal = true;
            }
            if (m.getType().hasFlag(MiscType.F_RISC_LASER_PULSE_MODULE) &&
                    ((m.getLinked() == null)
                            || (m.getLinked().getLocation() != m.getLocation())
                            || !(m.getLinked().getType() instanceof WeaponType)
                            || !m.getLinked().getType().hasFlag(WeaponType.F_LASER)
                            || m.getLinked().getType().hasFlag(WeaponType.F_PULSE))) {
                buff.append("RISC Laser Pulse Module requires a non-pulse laser in the same location.\n");
                illegal = true;
            }
        }
        if ((networks > 0) && !countedC3) {
            for (Mounted<?> m : getEntity().getIndividualWeaponList()) {
                if (m.getType().hasFlag(WeaponType.F_C3M) || m.getType().hasFlag(WeaponType.F_C3MBS)) {
                    networks++;
                }
            }
        }

        Engine engine = getEntity().getEngine();
        if (!getEntity().hasEngine() || !(engine.isFusion() || engine.isFission())) {
            for (WeaponMounted m : getEntity().getWeaponList()) {
                if ((m.getType().getAmmoType() == AmmoType.AmmoTypeEnum.GAUSS_HEAVY)
                        || (m.getType().getAmmoType() == AmmoType.AmmoTypeEnum.IGAUSS_HEAVY)) {
                    buff.append("Heavy Gauss Rifles require a fusion or fission engine\n");
                    illegal = true;
                } else if (m.getType().hasFlag(WeaponType.F_FLAMER)
                        && (m.getType().getAmmoType() == AmmoType.AmmoTypeEnum.NA)
                        && !(m.getType() instanceof BAFlamerWeapon)) {
                    buff.append("Standard flamers require a fusion or fission engine\n");
                    illegal = true;
                } else if (m.getType().hasFlag(WeaponType.F_HYPER)) {
                    buff.append("RISC Hyper Lasers require a fusion or fission engine\n");
                    illegal = true;
                }
            }
        }

        if (hasExternalFuelTank
                && (!getEntity().hasEngine() || ((getEntity().getEngine().getEngineType() != Engine.COMBUSTION_ENGINE)
                        && (getEntity().getEngine().getEngineType() != Engine.FUEL_CELL)))) {
            illegal = true;
            buff.append("Extended fuel tanks can only be used with internal combustion or fuel cell engines.\n");
        }

        if (minesweeperCount > 1) {
            buff.append("Unit has more than one minesweeper!\n");
            illegal = true;
        }
        if (fieldKitchenCount > 3) {
            buff.append("Unit has more than three Field Kitchens\n");
            illegal = true;
        }

        if (hasCoolantPod && (emergencyCoolantCount > 0)) {
            buff.append("Unit has coolant pod and RISC emergency coolant system\n");
            illegal = true;
        }
        if (emergencyCoolantCount > 1) {
            buff.append("Unit has more than one RISC emergency coolant system\n");
            illegal = true;
        }
        if (!(getEntity() instanceof Mek) && (hasHarjelII || hasHarjelIII)) {
            buff.append("Cannot mount HarJel repair system on non-Mek\n");
            illegal = true;
        }
        if (networks > 1) {
            buff.append("Cannot have multiple network types on the same unit.\n");
            illegal = true;
        }
        if (robotics > 1) {
            buff.append("Unit has multiple drone control systems.\n");
            illegal = true;
        }
        if (getEntity().hasStealth() && !getEntity().hasWorkingMisc(MiscType.F_ECM)) {
            buff.append("Stealth armor requires an ECM generator.\n");
            illegal = true;
        }
        if ((getEntity() instanceof Mek) && (liftHoists > 2)) {
            illegal = true;
            buff.append("Can mount a maximum of two lift hoists.\n");
        } else if ((getEntity().isSupportVehicle() || (getEntity() instanceof Tank)) && (liftHoists > 4)) {
            illegal = true;
            buff.append("Can mount a maximum of four lift hoists.\n");
        }
        for (List<EquipmentType> list : physicalWeaponsByLocation.values()) {
            if (list.size() > 1) {
                illegal = true;
                buff.append(list.stream().map(EquipmentType::getName).collect(Collectors.joining(", ")))
                        .append(" cannot be mounted in the same location.\n");
            }
        }
        for (int count : bridgeLayersByLocation.values()) {
            if (count > 1) {
                illegal = true;
                buff.append("Cannot mount more than one bridge builder in the same location.\n");
            }
        }

        if (getEntity().isOmni()) {
            for (Mounted<?> m : getEntity().getEquipment()) {
                if (m.isOmniPodMounted() && m.getType().isOmniFixedOnly()) {
                    illegal = true;
                    buff.append(m.getType().getName()).append(" cannot be pod mounted.");
                }
            }
        } else {
            for (Mounted<?> m : getEntity().getEquipment()) {
                if (m.isOmniPodMounted()) {
                    buff.append(m.getType().getName()).append(" is pod mounted in non-omni unit\n");
                    illegal = true;
                }
            }
            for (Transporter t : getEntity().getTransports()) {
                if (getEntity().isPodMountedTransport(t)) {
                    buff.append("Pod mounted troop space in non-omni unit\n");
                    illegal = true;
                }
            }
        }
        for (Mounted<?> mounted : getEntity().getEquipment()) {
            if (mounted.getLocation() > Entity.LOC_NONE) {
                illegal |= !isValidLocation(getEntity(), mounted.getType(), mounted.getLocation(), buff);
            }
        }

        // Find all locations with modular armor and map the number in that location to
        // the location index.
        Map<Integer, Long> modArmorByLocation = getEntity().getMisc().stream()
                .filter(m -> m.getType().hasFlag(MiscType.F_MODULAR_ARMOR))
                .filter(m -> m.getLocation() != Entity.LOC_NONE)
                .collect(Collectors.groupingBy(Mounted::getLocation, Collectors.counting()));
        for (Integer loc : modArmorByLocation.keySet()) {
            if (modArmorByLocation.get(loc) > 1) {
                buff.append("Only one modular armor slot may be mounted in a single location (")
                        .append(getEntity().getLocationName(loc)).append(")\n");
                illegal = true;
            }
        }

        if (artemisIV + artemisV + artemisP > 0) {
            if (((artemisIV > 0) && (artemisV + artemisP > 0))
                    || ((artemisV > 0) && (artemisP > 0))) {
                buff.append("All Artemis systems must be of the same type.\n");
                illegal = true;
            }
            illegal |= checkIllegalArtemisApolloLinks(buff, artemisIV + artemisV + artemisP,
                    "Artemis", w -> w.hasFlag(WeaponType.F_ARTEMIS_COMPATIBLE));
        }
        if (apollo > 0) {
            illegal |= checkIllegalArtemisApolloLinks(buff, apollo, "Apollo",
                    w -> w.getAmmoType() == AmmoType.AmmoTypeEnum.MRM);
        }

        if (getEntity() instanceof IBomber bomber) {
            if (bomber.getBombPoints(true) > bomber.getMaxExtBombPoints()) {
                buff.append("External bomb storage capacity exceeded\n");
                illegal = true;
            } else if (bomber.getBombPoints() - bomber.getBombPoints(true) > bomber.getMaxIntBombPoints()) {
                buff.append("Internal bomb storage capacity exceeded\n");
                illegal = true;
            }
        }

        return illegal;
    }

    private boolean checkIllegalArtemisApolloLinks(StringBuffer buffer, int expected,
            String testingEquipment,
            Predicate<WeaponType> compatibility) {
        int linkedCount = 0;
        /*
         * Besides tracking the number required we also want to check that they are all
         * linked.
         * This will find situations where the number matches but they're not in the
         * same
         * locations as the launcher.
         */
        boolean hasUnlinked = false;
        for (Mounted<?> mount : getEntity().getTotalWeaponList()) {
            if (!mount.isWeaponGroup() &&
                    compatibility.test((WeaponType) mount.getType())) {
                linkedCount++;
                if (mount.getLinkedBy() == null) {
                    hasUnlinked = true;
                }
            }
        }
        if (linkedCount != expected) {
            buffer.append("There must be one ").append(testingEquipment).append(" for each compatible weapon.\n");
            return true;
        } else if (hasUnlinked) {
            buffer.append(testingEquipment).append(" must be in the same location as the weapon.\n");
            return true;
        }
        return false;
    }

    /**
     * @param entity   The entity
     * @param eq       The equipment
     * @param location A location index on the Entity
     * @param buffer   If non-null and the location is invalid, will be appended
     *                 with an explanation
     * @return Whether the equipment can be mounted in the location on the Entity
     */
    public static boolean isValidLocation(Entity entity, EquipmentType eq, int location,
            @Nullable StringBuffer buffer) {
        if (entity instanceof Mek) {
            return TestMek.isValidMekLocation((Mek) entity, eq, location, buffer);
        } else if (entity instanceof Tank) {
            return TestTank.isValidTankLocation((Tank) entity, eq, location, buffer);
        } else if (entity instanceof ProtoMek) {
            return TestProtoMek.isValidProtoMekLocation((ProtoMek) entity, eq, location, buffer);
        } else if (entity.isFighter()) {
            return TestAero.isValidAeroLocation(eq, location, buffer);
        }
        return true;
    }

    public StringBuffer printFailedEquipment(StringBuffer buff) {
        if (getEntity().getFailedEquipment().hasNext()) {
            buff.append("Equipment that Failed to Load:\n");
        }
        for (Iterator<String> e = getEntity().getFailedEquipment(); e.hasNext();) {
            buff.append(e.next()).append("\n");
        }
        return buff;
    }

    public double getWeightCarryingSpace() {
        double weight = getEntity().getTroopCarryingSpace();
        for (Bay bay : getEntity().getTransportBays()) {
            if (!bay.isQuarters()) {
                TestEntity.ceil(weight += bay.getWeight(), Ceil.KILO);
            }
        }
        return weight;
    }

    public String printWeightCarryingSpace() {
        String carryingSpace = "";
        if (getEntity().getTroopCarryingSpace() != 0) {
            carryingSpace = StringUtil.makeLength("Carrying Capacity:",
                    getPrintSize() - 5)
                    + TestEntity.makeWeightString(getEntity()
                            .getTroopCarryingSpace(), usesKgStandard())
                    + "\n";
        }
        String cargoWeightString = "";
        double cargoWeight = 0;
        for (Bay bay : getEntity().getTransportBays()) {
            cargoWeight += bay.getWeight();
        }
        if (cargoWeight > 0) {
            cargoWeightString = StringUtil.makeLength("Cargo Weight:",
                    getPrintSize() - 5)
                    + TestEntity.makeWeightString(cargoWeight, usesKgStandard()) + "\n";
        }
        return carryingSpace + cargoWeightString;
    }

    public String printArmorLocation(int loc) {
        if (getEntity().hasRearArmor(loc)) {
            return StringUtil.makeLength(
                    getEntity().getLocationAbbr(loc) + ":", 5)
                    + StringUtil.makeLength(getEntity().getOInternal(loc), 4)
                    + StringUtil.makeLength(getEntity().getOArmor(loc), 3)
                    + " / "
                    + StringUtil
                            .makeLength(getEntity().getOArmor(loc, true), 2);
        }
        return StringUtil.makeLength(getEntity().getLocationAbbr(loc) + ":", 5)
                + StringUtil.makeLength(getEntity().getOInternal(loc), 4)
                + StringUtil.makeLength(getEntity().getOArmor(loc), 6) + "  ";
    }

    public String printArmorPlacement() {
        StringBuffer buff = new StringBuffer();
        buff.append("Armor Placement:\n");
        for (int loc = 0; loc < getEntity().locations(); loc++) {
            buff.append(printArmorLocation(loc)).append("\n");
        }
        return buff.toString();
    }

    public String printSource() {
        return "Source: " + getEntity().getSource() + "\n";
    }

    public String printTechLevel() {
        return "Chassis: " + getEntity().getDisplayName() + " - "
                + TechConstants.getLevelName(getEntity().getTechLevel()) + " ("
                + getEntity().getYear() + ")\n";
    }

    /**
     * @return The total additional weight due to critical slot armoring. Does not include the weight of the equipment
     * itself.
     */
    public double getArmoredComponentWeight() {
        return 0;
    }

    public static boolean usesKgStandard(Entity entity) {
        return entity.hasETypeFlag(Entity.ETYPE_BATTLEARMOR)
                || entity.hasETypeFlag(Entity.ETYPE_PROTOMEK)
                || (EntityWeightClass.getWeightClass(entity.getWeight(),
                        entity) == EntityWeightClass.WEIGHT_SMALL_SUPPORT);
    }

    boolean usesKgStandard() {
        return usesKgStandard(getEntity());
    }

    public int totalCritSlotCount() {
        int slotCount = 0;
        for (int i = 0; i < getEntity().locations(); i++) {
            slotCount += getEntity().getNumberOfCriticals(i);
        }
        return slotCount;
    }

    boolean ignoreSlotCount() {
        var entity = getEntity();
        return entity.getInvalidSourceBuildReasons().contains(Entity.InvalidSourceBuildReason.NOT_ENOUGH_SLOT_COUNT);
    }

    boolean ignoreEquipmentIntroYear() {
        var entity = getEntity();
        return entity.getInvalidSourceBuildReasons().contains(Entity.InvalidSourceBuildReason.UNIT_OLDER_THAN_EQUIPMENT_INTRO_YEAR);
    }

    boolean allowOverweightConstruction() {
        var entity = getEntity();
        return entity.getInvalidSourceBuildReasons().contains(Entity.InvalidSourceBuildReason.UNIT_OVERWEIGHT);
    }

    boolean skipBuildValidation() {
        var entity = getEntity();
        return entity.getInvalidSourceBuildReasons().contains(Entity.InvalidSourceBuildReason.INVALID_OR_OUTDATED_BUILD);
    }

} // End class TestEntity
