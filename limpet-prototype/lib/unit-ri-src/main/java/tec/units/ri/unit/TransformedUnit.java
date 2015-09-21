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

import java.util.Map;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import tec.units.ri.AbstractUnit;
import tec.units.ri.function.UnitConverterSupplier;


/**
 * <p> This class represents the units derived from other units using
 *     {@linkplain UnitConverter converters}.</p>
 *
 * <p> Examples of transformed units:<code>
 *         CELSIUS = KELVIN.shift(273.15);
 *         FOOT = METRE.multiply(3048).divide(10000);
 *         MILLISECOND = MILLI(SECOND);
 *     </code></p>
 *
 * <p> Transformed units have no label. But like any other units,
 *     they may have labels attached to them (see {@link tec.units.ri.format.SymbolMap
 *     SymbolMap}</p>
 *
 * <p> Instances of this class are created through the {@link AbstractUnit#transform} method.</p>
 *
 * @param <Q> The type of the quantity measured by this unit.
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 0.5.1, January 17, 2015
 */
public final class TransformedUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> 
 implements UnitConverterSupplier {
  
	/**
	 * 
	 */
//	private static final long serialVersionUID = 1L;

	/**
     * Holds the parent unit (always a system unit).
     */
    private final AbstractUnit<Q> parentUnit;

    /**
     * Holds the converter to the parent unit.
     */
    private final UnitConverter converter;
    
    /**
     * Holds the symbol.
     */
    private String symbol;

    /**
     * Creates a transformed unit from the specified system unit.
     *
     * @param parentUnit the system unit from which this unit is derived.
     * @param toParentUnit the converter to the parent units.
     * @throws IllegalArgumentException if the specified parent unit is not an
     *         {@link AbstractUnit#isSystemUnit() system unit}
     */
    public TransformedUnit(AbstractUnit<Q> parentUnit, UnitConverter toParentUnit) {
        if (!parentUnit.isSystemUnit())
            throw new IllegalArgumentException("The parent unit: " +  parentUnit
                    + " is not a system unit");
        this.parentUnit = parentUnit;
        this.converter = toParentUnit;
//        this.symbol = parentUnit.getSymbol();
    }
    
    /**
     * Creates a transformed unit from the specified system unit.
     *
     * @param parentUnit the system unit from which this unit is derived.
     * @param toParentUnit the converter to the parent units.
     * @throws IllegalArgumentException if the specified parent unit is not an
     *         {@link AbstractUnit#isSystemUnit() system unit}
     * @throws ClassCastException if parentUnit is not a valid {@link Unit} implementation
     */
    public TransformedUnit(Unit<Q> parentUnit, UnitConverter toParentUnit) {
    		this((AbstractUnit<Q>) parentUnit, toParentUnit);
    }

    @Override
    public Dimension getDimension() {
        return parentUnit.getDimension();
    }

    @Override
    public UnitConverter getConverterToSI() {
        return parentUnit.getConverterToSI().concatenate(converter);
    }

    @Override
    public AbstractUnit<Q> toSystemUnit() {
        return parentUnit.getSystemUnit();
    }

    @Override
    public Map<? extends Unit<?>, Integer> getProductUnits() {
        return parentUnit.getProductUnits();
    }

    @Override
    public int hashCode() {
        return parentUnit.hashCode() + converter.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (!(that instanceof TransformedUnit))
            return false;
        TransformedUnit<?> thatUnit = (TransformedUnit<?>) that;
        return this.parentUnit.equals(thatUnit.parentUnit) &&
                this.converter.equals(thatUnit.converter);
    }

	@Override
	//TODO clarify JavaDoc or adjust to what the Unit JavaDoc says.
	public String getSymbol() {
		return symbol;
	}
	
	public Unit<Q> getParentUnit() {
		return parentUnit;
	}

	@Override
	public UnitConverter getConverter() {
		return converter;
	}
}
