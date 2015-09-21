/**
 *  Unit-API - Units of Measurement API for Java
 *  Copyright (c) 2005-2015, Jean-Marie Dautelle, Werner Keil, V2COM.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-363 nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tec.units.ri.unit;

import static tec.units.ri.AbstractUnit.ONE;

import java.util.HashMap;
import java.util.Map;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.*;

import tec.units.ri.AbstractSystemOfUnits;
import tec.units.ri.AbstractUnit;
import tec.units.ri.function.AddConverter;
import tec.units.ri.function.LogConverter;
import tec.units.ri.function.MultiplyConverter;
import tec.units.ri.function.PiMultiplierConverter;
import tec.units.ri.function.RationalConverter;
import tec.units.ri.quantity.QuantityDimension;


/**
 * <p> This class defines all SI (Système International d'Unités) base units and
 *     derived units as well as units that are accepted for use with the
 *     SI units.</p>
 *
 * @see <a href="http://en.wikipedia.org/wiki/International_System_of_Units">Wikipedia: International System of Units</a>
 * @see <a href="http://physics.nist.gov/cuu/Units/outside.html>Units outside the SI that are accepted for use with the SI</a>
 * @see <a href="http://www.bipm.org/utils/common/pdf/si_brochure_8.pdf>SI 2006 - Official Specification</a>
 * @see MetricPrefix
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 0.5.6, May 15, 2015
 * @deprecated see https://java.net/jira/browse/UNITSOFMEASUREMENT-100 relevant units moved to @link Units
*/
public final class SI extends AbstractSystemOfUnits {

    /**
     * The singleton instance.
     */
    private static final SI INSTANCE = new SI();

    /**
     * Holds the mapping quantity to unit.
     */
    private final Map<Class<? extends Quantity>, AbstractUnit>
            quantityToUnit = new HashMap<Class<? extends Quantity>, AbstractUnit>();

    /**
     * Default constructor (prevents this class from being instantiated).
     */
    private SI() {
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the metric system instance.
     */
    public static SI getInstance() {
        return INSTANCE;
    }


    ////////////////
    // BASE UNITS //
    ////////////////

    /**
     * The SI base unit for electric current quantities (standard name <code>A</code>).
     * The Ampere is that constant current which, if maintained in two straight
     * parallel conductors of infinite length, of negligible circular
     * cross-section, and placed 1 meter apart in vacuum, would produce between
     * these conductors a force equal to 2 * 10-7 newton per meter of length.
     * It is named after the French physicist Andre Ampere (1775-1836).
     */
    public static final Unit<ElectricCurrent> AMPERE 
            = addUnit(new BaseUnit<ElectricCurrent>("A", QuantityDimension.ELECTRIC_CURRENT), ElectricCurrent.class);

    /**
     * The SI base unit for luminous intensity quantities (standard name <code>cd</code>).
     * The candela is the luminous intensity, in a given direction,
     * of a source that emits monochromatic radiation of frequency
     * 540 * 1012 hertz and that has a radiant intensity in that
     * direction of 1/683 watt per steradian
     * @see <a href="http://en.wikipedia.org/wiki/Candela">
     *      Wikipedia: Candela</a>
     */
    public static final Unit<LuminousIntensity> CANDELA 
            = addUnit(new BaseUnit<LuminousIntensity>("cd", QuantityDimension.LUMINOUS_INTENSITY), LuminousIntensity.class);

    /**
     * The SI base unit for thermodynamic temperature quantities (standard name <code>K</code>).
     * The kelvin is the 1/273.16th of the thermodynamic temperature of the
     * triple point of water. It is named after the Scottish mathematician and
     * physicist William Thomson 1st Lord Kelvin (1824-1907)
     */
    public static final Unit<Temperature> KELVIN
            = addUnit(new BaseUnit<Temperature>("K", QuantityDimension.TEMPERATURE), Temperature.class);

    /**
     * The SI base unit for mass quantities (standard name <code>kg</code>).
     * It is the only SI unit with a prefix as part of its name and symbol.
     * The kilogram is equal to the mass of an international prototype in the
     * form of a platinum-iridium cylinder kept at Sevres in France.
     * @see   #GRAM
     */
    public static final Unit<Mass> KILOGRAM
            = addUnit(new BaseUnit<Mass>("kg", QuantityDimension.MASS), Mass.class);

    /**
     * The SI base unit for length quantities (standard name <code>m</code>).
     * One metre was redefined in 1983 as the distance traveled by light in
     * a vacuum in 1/299,792,458 of a second.
     */
    public static final Unit<Length> METRE
            = addUnit(new BaseUnit<Length>("m", QuantityDimension.LENGTH), Length.class);

    /**
     * The SI base unit for amount of substance quantities (standard name <code>mol</code>).
     * The mole is the amount of substance of a system which contains as many
     * elementary entities as there are atoms in 0.012 kilogram of carbon 12.
     */
    public static final Unit<AmountOfSubstance> MOLE
            = addUnit(new BaseUnit<AmountOfSubstance>("mol", QuantityDimension.AMOUNT_OF_SUBSTANCE), AmountOfSubstance.class);

    /**
     * The SI base unit for duration quantities (standard name <code>s</code>).
     * It is defined as the duration of 9,192,631,770 cycles of radiation
     * corresponding to the transition between two hyperfine levels of
     * the ground state of cesium (1967 Standard).
     */
    public static final Unit<Time> SECOND 
            = addUnit(new BaseUnit<Time>("s", QuantityDimension.TIME), Time.class);


    ////////////////////////////////
    // SI DERIVED ALTERNATE UNITS //
    ////////////////////////////////

    /**
     * The SI derived unit for mass quantities (standard name <code>g</code>).
     * The base unit for mass quantity is {@link #KILOGRAM}.
     */
    public static final Unit<Mass> GRAM = 
    		 addUnit(KILOGRAM.divide(1000));
//    		 addUnit(new AlternateUnit<Mass>(
//    				KILOGRAM.divide(1000), "g"), Mass.class);

    /**
     * The SI unit for plane angle quantities (standard name <code>rad</code>).
     * One radian is the angle between two radii of a circle such that the
     * length of the arc between them is equal to the radius.
     */
    public static final Unit<Angle> RADIAN
            = addUnit(new AlternateUnit<Angle>(ONE, "rad"), Angle.class);

    /**
     * The SI unit for solid angle quantities (standard name <code>sr</code>).
     * One steradian is the solid angle subtended at the center of a sphere by
     * an area on the surface of the sphere that is equal to the radius squared.
     * The total solid angle of a sphere is 4*Pi steradians.
     */
    public static final AlternateUnit<SolidAngle> STERADIAN
            = addUnit(new AlternateUnit<SolidAngle>(ONE, "sr"), SolidAngle.class);

    /**
     * The SI unit for binary information (standard name <code>bit</code>).
     * @deprecated see https://java.net/jira/browse/UNITSOFMEASUREMENT-100
     */
    public static final AlternateUnit<Information> BIT
            = addUnit(new AlternateUnit<Information>(ONE, "bit"), Information.class);

    /**
     * The SI unit for frequency (standard name <code>Hz</code>).
     * A unit of frequency equal to one cycle per second.
     * After Heinrich Rudolf Hertz (1857-1894), German physicist who was the
     * first to produce radio waves artificially.
     */
    public static final AlternateUnit<Frequency> HERTZ
            = addUnit(new AlternateUnit<Frequency>(ONE.divide(SECOND), "Hz"), Frequency.class);

    /**
     * The SI unit for force (standard name <code>N</code>).
     * One newton is the force required to give a mass of 1 kilogram an Force
     * of 1 metre per second per second. It is named after the English
     * mathematician and physicist Sir Isaac Newton (1642-1727).
     */
    public static final Unit<Force> NEWTON
            = addUnit(new AlternateUnit<Force>(
              METRE.multiply(KILOGRAM).divide(SECOND.pow(2)), "N"), Force.class);

    /**
     * The SI unit for pressure, stress (standard name <code>Pa</code>).
     * One pascal is equal to one newton per square meter. It is named after
     * the French philosopher and mathematician Blaise Pascal (1623-1662).
     */
    public static final Unit<Pressure> PASCAL
            = addUnit(new AlternateUnit<Pressure>(
             NEWTON.divide(METRE.pow(2)), "Pa"), Pressure.class);

    /**
     * The SI unit for energy, work, quantity of heat (<code>J</code>).
     * One joule is the amount of work done when an applied force of 1 newton
     * moves through a distance of 1 metre in the direction of the force.
     * It is named after the English physicist James Prescott Joule (1818-1889).
     */
    public static final Unit<Energy> JOULE
            = addUnit(new AlternateUnit<Energy>(
            NEWTON.multiply(METRE), "J"), Energy.class);

    /**
     * The SI unit for power, radiant, flux (standard name <code>W</code>).
     * One watt is equal to one joule per second. It is named after the British
     * scientist James Watt (1736-1819).
     */
    public static final Unit<Power> WATT
            = addUnit(new AlternateUnit<Power>(JOULE.divide(SECOND), "W"), Power.class);

    /**
     * The SI unit for electric charge, quantity of electricity
     * (standard name <code>C</code>).
     * One Coulomb is equal to the quantity of charge transferred in one second
     * by a steady current of one ampere. It is named after the French physicist
     * Charles Augustin de Coulomb (1736-1806).
     */
    public static final Unit<ElectricCharge> COULOMB
            = addUnit(new AlternateUnit<ElectricCharge>(
            SECOND.multiply(AMPERE), "C"), ElectricCharge.class);

    /**
     * The SI unit for electric potential difference, electromotive force
     * (standard name <code>V</code>).
     * One Volt is equal to the difference of electric potential between two
     * points on a conducting wire carrying a constant current of one ampere
     * when the power dissipated between the points is one watt. It is named
     * after the Italian physicist Count Alessandro Volta (1745-1827).
     */
    public static final Unit<ElectricPotential> VOLT
            = addUnit(new AlternateUnit<ElectricPotential>(
             WATT.divide(AMPERE), "V"), ElectricPotential.class);

    /**
     * The SI unit for capacitance (standard name <code>F</code>).
     * One Farad is equal to the capacitance of a capacitor having an equal
     * and opposite charge of 1 coulomb on each plate and a potential difference
     * of 1 volt between the plates. It is named after the British physicist
     * and chemist Michael Faraday (1791-1867).
     */
    public static final Unit<ElectricCapacitance> FARAD
            = addUnit(new AlternateUnit<ElectricCapacitance>(
           COULOMB.divide(VOLT), "F"), ElectricCapacitance.class);

    /**
     * The SI unit for electric resistance (standard name <code>Ohm</code>).
     * One Ohm is equal to the resistance of a conductor in which a current of
     * one ampere is produced by a potential of one volt across its terminals.
     * It is named after the German physicist Georg Simon Ohm (1789-1854).
     */
    public static final Unit<ElectricResistance> OHM
            = addUnit(new AlternateUnit<ElectricResistance>(
             VOLT.divide(AMPERE), "Ω"), ElectricResistance.class);

    /**
     * The SI unit for electric conductance (standard name <code>S</code>).
     * One Siemens is equal to one ampere per volt. It is named after
     * the German engineer Ernst Werner von Siemens (1816-1892).
     */
    public static final AlternateUnit<ElectricConductance> SIEMENS
            = addUnit(new AlternateUnit<ElectricConductance>(
            AMPERE.divide(VOLT), "S"), ElectricConductance.class);

    /**
     * The SI unit for magnetic flux (standard name <code>Wb</code>).
     * One Weber is equal to the magnetic flux that in linking a circuit of one
     * turn produces in it an electromotive force of one volt as it is uniformly
     * reduced to zero within one second. It is named after the German physicist
     * Wilhelm Eduard Weber (1804-1891).
     */
    public static final AlternateUnit<MagneticFlux> WEBER
            = addUnit(new AlternateUnit<MagneticFlux>(
            VOLT.multiply(SECOND), "Wb"), MagneticFlux.class);

    /**
     * The alternate unit for magnetic flux density (standard name <code>T</code>).
     * One Tesla is equal equal to one weber per square metre. It is named
     * after the Serbian-born American electrical engineer and physicist
     * Nikola Tesla (1856-1943).
     */
    public static final AlternateUnit<MagneticFluxDensity> TESLA
            = addUnit(new AlternateUnit<MagneticFluxDensity>(
            WEBER.divide(METRE.pow(2)), "T"), MagneticFluxDensity.class);

    /**
     * The alternate unit for inductance (standard name <code>H</code>).
     * One Henry is equal to the inductance for which an induced electromotive
     * force of one volt is produced when the current is varied at the rate of
     * one ampere per second. It is named after the American physicist
     * Joseph Henry (1791-1878).
     */
    public static final AlternateUnit<ElectricInductance> HENRY
            = addUnit(new AlternateUnit<ElectricInductance>(
            WEBER.divide(AMPERE), "H"), ElectricInductance.class);

    /**
     * The SI unit for Celsius temperature (standard name <code>Cel</code>).
     * This is a unit of temperature such as the freezing point of water
     * (at one atmosphere of pressure) is 0 Cel, while the boiling point is
     * 100 Cel.
     */
    public static final Unit<Temperature> CELSIUS
            = addUnit(new TransformedUnit<Temperature>(KELVIN, new AddConverter(273.15)));
            // Not mapping to Temperature since temperature is mapped to Kelvin.

    /**
     * The SI unit for luminous flux (standard name <code>lm</code>).
     * One Lumen is equal to the amount of light given out through a solid angle
     * by a source of one candela intensity radiating equally in all directions.
     */
    public static final AlternateUnit<LuminousFlux> LUMEN
            = addUnit(new AlternateUnit<LuminousFlux>(
            CANDELA.multiply(STERADIAN), "lm"), LuminousFlux.class);

    /**
     * The SI unit for illuminance (standard name <code>lx</code>).
     * One Lux is equal to one lumen per square metre.
     */
    public static final AlternateUnit<Illuminance> LUX
            = addUnit(new AlternateUnit<Illuminance>(
            LUMEN.divide(METRE.pow(2)), "lx"), Illuminance.class);

    /**
     * The SI unit for activity of a radionuclide (standard name <code>Bq</code>).
     * One becquerel is the radiation caused by one disintegration per second.
     * It is named after the French physicist, Antoine-Henri Becquerel
     * (1852-1908).
     */
    public static final AlternateUnit<Radioactivity> BECQUEREL
            = addUnit(new AlternateUnit<Radioactivity>(
            ONE.divide(SECOND), "Bq"), Radioactivity.class);

    /**
     * The SI unit for absorbed dose, specific energy (imparted), kerma
     * (standard name <code>Gy</code>).
     * One gray is equal to the dose of one joule of energy absorbed per one
     * kilogram of matter. It is named after the British physician
     * L. H. Gray (1905-1965).
     */
    public static final AlternateUnit<RadiationDoseAbsorbed> GRAY
            = addUnit(new AlternateUnit<RadiationDoseAbsorbed>(
            JOULE.divide(KILOGRAM), "Gy"), RadiationDoseAbsorbed.class);

    /**
     * The SI unit for dose equivalent (standard name <code>Sv</code>).
     * One Sievert is equal  is equal to the actual dose, in grays, multiplied
     * by a "quality factor" which is larger for more dangerous forms of
     * radiation. It is named after the Swedish physicist Rolf Sievert
     * (1898-1966).
     */
    public static final AlternateUnit<RadiationDoseEffective> SIEVERT
            = addUnit(new AlternateUnit<RadiationDoseEffective>(
            JOULE.divide(KILOGRAM), "Sv"), RadiationDoseEffective.class);

    /**
     * The SI unit for catalytic activity (standard name <code>kat</code>).
     */
    public static final AlternateUnit<CatalyticActivity> KATAL
            = addUnit(new AlternateUnit<CatalyticActivity>(
            MOLE.divide(SECOND), "kat"), CatalyticActivity.class);

    //////////////////////////////
    // SI DERIVED PRODUCT UNITS //
    //////////////////////////////

    /**
     * The SI unit for speed quantities (standard name <code>m/s</code>).
     */
    public static final Unit<Speed> METRES_PER_SECOND
            = addUnit(new ProductUnit<Speed>(
            METRE.divide(SECOND)), Speed.class);

    /**
     * The SI unit for acceleration quantities (standard name <code>m/s2</code>).
     */
    public static final Unit<Acceleration> METRES_PER_SQUARE_SECOND
            = addUnit(new ProductUnit<Acceleration>(
            METRES_PER_SECOND.divide(SECOND)), Acceleration.class);

    /**
     * The SI unit for area quantities (standard name <code>m2</code>).
     */
    public static final Unit<Area> SQUARE_METRE
            = addUnit(new ProductUnit<Area>(METRE.multiply(METRE)), Area.class);

    /**
     * The SI unit for volume quantities (standard name <code>m3</code>).
     */
    public static final Unit<Volume> CUBIC_METRE
            = addUnit(new ProductUnit<Volume>(
            SQUARE_METRE.multiply(METRE)), Volume.class);   

    /**
     * The SI unit for action quantities (standard name <code>j.s</code>).
     */
    public static final Unit<Action> JOULE_SECOND
            = addUnit(new ProductUnit<Action>(
            JOULE.multiply(SECOND)), Action.class);

    /**
     * The SI unit for electric permittivity quantities (standard name <code>F/m</code>).
     */
    public static final Unit<ElectricPermittivity> FARADS_PER_METRE
            = addUnit(new ProductUnit<ElectricPermittivity>(
            FARAD.divide(METRE)), ElectricPermittivity.class);

    /**
     * The SI unit for magnetic permeability quantities (standard name <code>N/A2</code>).
     *  @deprecated see https://java.net/jira/browse/UNITSOFMEASUREMENT-100
     */
    public static final Unit<MagneticPermeability> NEWTONS_PER_SQUARE_AMPERE
            = addUnit(new ProductUnit<MagneticPermeability>(
            NEWTON.divide(AMPERE.pow(2))), MagneticPermeability.class);

    /**
     * The SI unit for wave number quantities (standard name <code>1/m</code>).
     */
    public static final Unit<WaveNumber> RECIPROCAL_METRE
            = addUnit(new ProductUnit<WaveNumber>(
            METRE.pow(-1)), WaveNumber.class);

    /**
     * The SI unit for dynamic viscosity quantities (standard name <code>Pa.s</code>).
     * @deprecated see https://java.net/jira/browse/UNITSOFMEASUREMENT-100
     */
    public static final Unit<DynamicViscosity> PASCAL_SECOND
            = addUnit(new ProductUnit<DynamicViscosity>(
            PASCAL.multiply(SECOND)), DynamicViscosity.class);

    /**
     * The SI unit for luminance quantities (standard name <code>cd/m2</code>).
     */
    public static final Unit<Luminance> CANDELAS_PER_SQUARE_METRE
            = addUnit(new ProductUnit<Luminance>(
            CANDELA.divide(SQUARE_METRE)), Luminance.class);

    /**
     * The SI unit for kinematic viscosity quantities (standard name <code>m2/s"</code>).
     * @deprecated see https://java.net/jira/browse/UNITSOFMEASUREMENT-100
     */
    public static final Unit<KinematicViscosity> SQUARE_METRES_PER_SECOND
            = addUnit(new ProductUnit<KinematicViscosity>(
            SQUARE_METRE.divide(SECOND)), KinematicViscosity.class);

    /**
     * The SI unit for magnetic field strength quantities (standard name <code>A/m"</code>).
     */
    public static final Unit<MagneticFieldStrength> AMPERES_PER_METRE
            = addUnit(new ProductUnit<MagneticFieldStrength>(
            AMPERE.divide(METRE)), MagneticFieldStrength.class);

    /**
     * The SI unit for ionizing radiation quantities (standard name <code>C/kg"</code>).
     * @deprecated see https://java.net/jira/browse/UNITSOFMEASUREMENT-100
     */
    public static final Unit<IonizingRadiation> COULOMBS_PER_KILOGRAM
            = addUnit(new ProductUnit<IonizingRadiation>(
            COULOMB.divide(KILOGRAM)), IonizingRadiation.class);

    /**
     * The SI unit for binary information rate (standard name <code>bit/s</code>).
     * @deprecated see https://java.net/jira/browse/UNITSOFMEASUREMENT-100
     */
    public static final Unit<InformationRate> BITS_PER_SECOND
            = addUnit(new ProductUnit<InformationRate>(BIT.divide(SECOND)), InformationRate.class);

    /////////////////////////////////////////////////////////////////
    // Units outside the SI that are accepted for use with the SI. //
    /////////////////////////////////////////////////////////////////

    /**
     * A dimensionless unit accepted for use with SI units (standard name <code>%</code>).
     */
    public static final Unit<Dimensionless> PERCENT
        = new TransformedUnit<Dimensionless>(ONE, new RationalConverter(1, 100));

    /**
     * A time unit accepted for use with SI units (standard name <code>min</code>).
     */
    public static final Unit<Time> MINUTE
        = new TransformedUnit<Time>(SECOND, new RationalConverter(60, 1));

    /**
     * A time unit accepted for use with SI units (standard name <code>h/code>).
     */
    public static final Unit<Time> HOUR
        = new TransformedUnit<Time>(SECOND, new RationalConverter(60 * 60, 1));

    /**
     * A time unit accepted for use with SI units (standard name <code>d/code>).
     */
    public static final Unit<Time> DAY
        = new TransformedUnit<Time>(SECOND, new RationalConverter(24 * 60 * 60, 1));

    /**
     * An angle unit accepted for use with SI units (standard name <code>deg/code>).
     */
    public static final Unit<Angle> DEGREE_ANGLE
        = new TransformedUnit<Angle>(RADIAN, new PiMultiplierConverter().concatenate(new RationalConverter(1, 180)));

    /**
     * An angle unit accepted for use with SI units (standard name <code>'/code>).
     */
    public static final Unit<Angle> MINUTE_ANGLE
        = new TransformedUnit<Angle>(RADIAN, new PiMultiplierConverter().concatenate(new RationalConverter(1, 180 * 60)));

    /**
     * An angle unit accepted for use with SI units (standard name <code>''</code>).
     */
    public static final Unit<Angle> SECOND_ANGLE
        = new TransformedUnit<Angle>(RADIAN,  new PiMultiplierConverter().concatenate(new RationalConverter(1, 180 * 60 * 60)));

    /**
     * A volume unit accepted for use with SI units (standard name <code>l</code>).
     */
    public static final Unit<Volume> LITRE
        = new TransformedUnit<Volume>(CUBIC_METRE, new RationalConverter(1, 1000));

    /**
     * A mass unit accepted for use with SI units (standard name <code>t</code>).
     */
    public static final Unit<Mass> TONNE
        = new TransformedUnit<Mass>(KILOGRAM, new RationalConverter(1000, 1));

    /**
     * A dimensionless unit accepted for use with SI units (standard name <code>Np</code>).
     * Although the neper is coherent with SI units and is accepted by the CIPM,
     * it has not been adopted by the General Conference on Weights and Measures
     * (CGPM, Conférence Générale des Poids et Mesures) and is thus not an SI unit.
     */
    public static final Unit<Dimensionless> NEPER
        = new TransformedUnit<Dimensionless>(ONE, new LogConverter(E).inverse());

    /**
     * A dimensionless unit accepted for use with SI units (standard name <code>B</code>).
     * The bel is most commonly used with the SI prefix deci: 1 dB = 0.1 B
     */
    public static final Unit<Dimensionless> BEL
        = new TransformedUnit<Dimensionless>(ONE, new LogConverter(10).inverse());

    /**
     * An energy unit accepted for use with SI units (standard name <code>eV</code>).
     * The electronvolt is the kinetic energy acquired by an electron passing
     * through a potential difference of 1 V in vacuum. 
     * The value must be obtained by experiment, and is therefore not known exactly.
     */
    public static final Unit<Energy> ELECTRON_VOLT
        = new TransformedUnit<Energy>(JOULE, new MultiplyConverter(1.602176487E-19));
        // CODATA 2006 - http://physics.nist.gov/cuu/Constants/codata.pdf
            
    /**
     * A mass unit accepted for use with SI units (standard name <code>u</code>).
     *  The unified atomic mass unit is equal to 1/12 of the mass of an unbound
     * atom of the nuclide 12C, at rest and in its ground state. The value must
     * be obtained by experiment, and is therefore not known exactly.
     */
    public static final Unit<Mass> UNIFIED_ATOMIC_MASS
        = new TransformedUnit<Mass>(KILOGRAM, new MultiplyConverter(1.660538782E-27));
        // CODATA 2006 - http://physics.nist.gov/cuu/Constants/codata.pdf

    /**
     * A length unit accepted for use with SI units (standard name <code>UA</code>).
     * The astronomical unit is a unit of length. Its value is such that,
     * when used to describe the motion of bodies in the solar system,
     * the heliocentric gravitation constant is (0.017 202 098 95)2 ua3·d-2.
     * The value must be obtained by experiment, and is therefore not known exactly.
     */
    public static final Unit<Length> ASTRONOMICAL_UNIT
        = addUnit(new TransformedUnit<Length>(METRE, new MultiplyConverter(149597871000.0)));
        // Best estimate source: http://maia.usno.navy.mil/NSFA/CBE.html
    
    /**
     *  An angle unit accepted for use with SI units (standard name <code>rev</code>).
     */
    public static final Unit<Angle> REVOLUTION
            = new TransformedUnit<Angle>(RADIAN, new PiMultiplierConverter().concatenate(new RationalConverter(2, 1)));

    /**
     *  An angle unit accepted for use with SI units (standard name <code>ha</code>).
     */
    public static final Unit<Area> HECTARE
            = new TransformedUnit<Area>(SQUARE_METRE, new RationalConverter(10000, 1));

    /////////////////////
    // Collection View //
    /////////////////////

    @Override
    public String getName() {
        return "SI";
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public <Q extends Quantity<Q>> AbstractUnit<Q> getUnit(Class<Q> quantityType) {
        return quantityToUnit.get(quantityType);
    }

    /**
     * Adds a new unit not mapped to any specified quantity type.
     *
     * @param  unit the unit being added.
     * @return <code>unit</code>.
     */
    private static <U extends Unit<?>>  U addUnit(U unit) {
        INSTANCE.units.add(unit);
        return unit;
    }

    /**
     * Adds a new unit and maps it to the specified quantity type.
     *
     * @param  unit the unit being added.
     * @param type the quantity type.
     * @return <code>unit</code>.
     */
    private static <U extends AbstractUnit<?>>  U addUnit(U unit, Class<? extends Quantity<?>> type) {
        INSTANCE.units.add(unit);
        INSTANCE.quantityToUnit.put(type, unit);
        return unit;
    }
}
