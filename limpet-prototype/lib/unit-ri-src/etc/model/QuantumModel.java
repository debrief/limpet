/**
 *  Unit-API - Units of Measurement API for Java
 *  Copyright (c) 2005-2014, Jean-Marie Dautelle, Werner Keil, V2COM.
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
package org.unitsofmeasurement.ri.model;

/**
 * This class represents the quantum model.
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 12, 2010
 */
public class QuantumModel extends HighEnergyModel {

    /**
     * Default constructor.
     */
    public QuantumModel() {
    }
    
// TODO: Allow more conversion.
//        // ENERGY = m²·kg/s² = kg·c²
//        SI.KILOGRAM.setDimension(SI.GIGA(NonSI.ELECTRON_VOLT),
//                new MultiplyConverter(1E-9 * c * c / ePlus));
//
//        // H_BAR (SECOND * JOULE = SECOND * (KILOGRAM / C^2 )) = 1
//        SI.SECOND.setDimension(Unit.ONE.divide(SI.GIGA(NonSI.ELECTRON_VOLT)),
//                new MultiplyConverter(1E9 * ePlus / hBar));
//
//        // SPEED_OF_LIGHT (METRE / SECOND) = 1
//        SI.METRE.setDimension(Unit.ONE.divide(SI.GIGA(NonSI.ELECTRON_VOLT)),
//                new MultiplyConverter(1E9 * ePlus / (c * hBar)));
//
//        // BOLTZMANN (JOULE / KELVIN = (KILOGRAM / C^2 ) / KELVIN) = 1
//        SI.KELVIN.setDimension(SI.GIGA(NonSI.ELECTRON_VOLT),
//                new MultiplyConverter(1E-9 * k / ePlus));
//
//        // MAGNETIC CONSTANT (NEWTON / AMPERE^2) = 1
//        SI.AMPERE.setDimension(SI.GIGA(NonSI.ELECTRON_VOLT),
//                new MultiplyConverter(1E-9 * MathLib.sqrt(µ0 * c * hBar) / ePlus));
//
//        SI.MOLE.setDimension(SI.MOLE, Converter.IDENTITY);
//        SI.CANDELA.setDimension(SI.CANDELA, Converter.IDENTITY);
    
}
