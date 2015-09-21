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
package tec.units.ri.function;

import javax.measure.UnitConverter;

import tec.units.ri.AbstractConverter;


/**
 * <p> This class represents a converter adding a constant offset
 *     to numeric values (<code>double</code> based).</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author Werner Keil
 * @version 0.3, Sep 06, 2014
 */
public final class AddConverter extends AbstractConverter implements ValueSupplier<Double> {

    /**
	 * 
	 */
//	private static final long serialVersionUID = -2981335308595652284L;
	/**
     * Holds the offset.
     */
    private double offset;

    /**
     * Creates an additive converter having the specified offset.
     *
     * @param  offset the offset value.
     * @throws IllegalArgumentException if offset is <code>0.0</code>
     *         (would result in identity converter).
     */
    public AddConverter(double offset) {
        if (offset == 0.0)
            throw new IllegalArgumentException("Would result in identity converter");
        this.offset = offset;
    }

    /**
     * Returns the offset value for this add converter.
     *
     * @return the offset value.
     */
    public double getOffset() {
        return offset;
    }

    @Override
    public UnitConverter concatenate(UnitConverter converter) {
        if (!(converter instanceof AddConverter))
            return super.concatenate(converter);
        double newOffset = offset + ((AddConverter) converter).offset;
        return newOffset == 0.0 ? IDENTITY : new AddConverter(newOffset);
    }

    @Override
    public AddConverter inverse() {
        return new AddConverter(-offset);
    }

    @Override
    public double convert(double value) {
        return value + offset;
    }

//    @Override
//    public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
//        return value.add(BigDecimal.valueOf(offset), ctx);
//    }

    @Override
    public final String toString() {
        return "AddConverter(" + offset + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AddConverter)) {
            return false;
        }
        AddConverter that = (AddConverter) obj;
        return this.offset == that.offset;
    }

    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(offset);
        return (int) (bits ^ (bits >>> 32));
    }

    @Override
    public boolean isLinear() {
        return false;
    }

	public Double getValue() {
		return offset;
	}
    
}
