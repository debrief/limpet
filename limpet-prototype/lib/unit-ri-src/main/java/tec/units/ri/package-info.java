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
/**
 * This package provides support for physics units, in conformity with the
 * <a href="http://www.unitsofmeasurement.org/">Units of Measurement API</a>.
 *
 *
 * <h3>Usage:</h3>
 * <code>
 *
 * import javax.measure.quantity.*; // Holds quantity types.
 * 
 * import org.unitsofmeasurement.ri.AbstractUnit;
 * import org.unitsofmeasurement.ri.function.AbstractConverter;
 * 
 * import static org.unitsofmeasurement.ri.util.SI.*; // Standard Units.
 * import static org.unitsofmeasurement.ri.util.SIPrefix.*;
 * import static org.unitsofmeasurement.ri.util.UCUM.*; // Standard & Non-Standard Units.
 *
 * public class Main {
 *     public void main(String[] args) {
 *
 *         // Conversion between units (explicit way).
 *         AbstractUnit<Length> sourceUnit = KILO(METRE);
 *         AbstractUnit<Length> targetUnit = MILE;
 *         PhysicsConverter uc = sourceUnit.getConverterTo(targetUnit);
 *         System.out.println(uc.convert(10)); // Converts 10 km to miles.
 *
 *         // Same conversion than above, packed in one line.
 *         System.out.println(KILO(METRE).getConverterTo(MILE).convert(10));
 *
 *         // Retrieval of the SI unit (identifies the measurement type).
 *         System.out.println(REVOLUTION.divide(MINUTE).toSI());
 *
 *         // Dimension checking (allows/disallows conversions)
 *         System.out.println(ELECTRON_VOLT.isCompatible(WATT.times(HOUR)));
 *
 *         // Retrieval of the unit dimension (depends upon the current model).
 *         System.out.println(ELECTRON_VOLT.getDimension());
 *     }
 * }
 *
 * > 6.2137119223733395
 * > 6.2137119223733395
 * > rad/s
 * > true
 * > [L]²·[M]/[T]²
 * </code>
 *
 * <h3>Unit Parameterization</h3>
 *
 *     Units are parameterized enforce compile-time checks of units/measures consistency, for example:[code]
 *
 *     AbstractUnit<Time> MINUTE = SECOND.times(60); // Ok.
 *     AbstractUnit<Time> MINUTE = METRE.times(60); // Compile error.
 *
 *     AbstractUnit<Pressure> HECTOPASCAL = HECTO(PASCAL); // Ok.
 *     AbstractUnit<Pressure> HECTOPASCAL = HECTO(NEWTON); // Compile error.
 *
 *     Quantity<Time> duration = AbstractQuantity.of(2, MINUTE); // Ok.
 *     Quantity<Time> duration = AbstractQuantity.of(2, CELSIUS); // Compile error.
 *
 *     long milliseconds = duration.longValue(MILLI(SECOND)); // Ok.
 *     long milliseconds = duration.longValue(POUND); // Compile error.
 *     [/code]
 *
 *     Runtime checks of dimension consistency can be done for more complex cases.
 *
 *     [code]
 *     AbstractUnit<Area> SQUARE_FOOT = FOOT.times(FOOT).asType(Area.class); // Ok.
 *     AbstractUnit<Area> SQUARE_FOOT = FOOT.times(KELVIN).asType(Area.class); // Runtime error.
 *
 *     AbstractUnit<Temperature> KELVIN = AbstractUnit.of("K").asType(Temperature.class); // Ok.
 *     AbstractUnit<Temperature> KELVIN = AbstractUnit.of("kg").asType(Temperature.class); // Runtime error.
 *     [/code]
 *     </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 0.2
 */
package tec.units.ri;

