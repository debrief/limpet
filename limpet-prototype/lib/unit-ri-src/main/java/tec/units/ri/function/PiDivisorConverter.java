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

import tec.units.ri.AbstractConverter;


/**
 * <p> This class represents a converter dividing numeric values by π (Pi).</p>
 *
 * <p> This class is package private, instances are created
 *     using the {@link PiMultiplierConverter#inverse()} method.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 0.3, August 3, 2014
 */
final class PiDivisorConverter extends AbstractConverter 
	implements ValueSupplier<String> { //implements Immutable<String> {
	

	/**
	 * 
	 */
//	private static final long serialVersionUID = 5052794216568914141L;

	/**
     * Creates a Pi multiplier converter.
     */
    public PiDivisorConverter() {
    }

    @Override
    public double convert(double value) {
        return value / PI;
    }

//    @Override
//    public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
//        int nbrDigits = ctx.getPrecision();
//        if (nbrDigits == 0) throw new ArithmeticException("Pi multiplication with unlimited precision");
//        BigDecimal pi = PiMultiplierConverter.Pi.pi(nbrDigits);
//        return value.divide(pi, ctx).scaleByPowerOfTen(nbrDigits-1);
//    }

    @Override
    public AbstractConverter inverse() {
        return new PiMultiplierConverter();
    }

    @Override
    public final String toString() {
        return "(1/π)";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PiDivisorConverter);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean isLinear() {
        return true;
    }

    @Override
	public String getValue() {
		return toString();
	}

}
