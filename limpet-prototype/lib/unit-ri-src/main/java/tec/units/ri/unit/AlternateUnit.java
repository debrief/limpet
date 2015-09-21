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
package tec.units.ri.unit;

import java.util.Map;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import tec.units.ri.AbstractUnit;


/**
 * <p> This class represents units used in expressions to distinguish
 *     between quantities of a different nature but of the same dimensions.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 0.2, May 22, 2014
 */
public final class AlternateUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> {

	/**
	 * 
	 */
//	private static final long serialVersionUID = 4696690756456282705L;

	/**
     * Holds the parent unit (a system unit).
     */
    private final AbstractUnit<?> parentUnit;

    /**
     * Holds the symbol for this unit.
     */
    private final String symbol;

    /**
     * Creates an alternate unit for the specified system unit identified by the
     * specified name and symbol.
     *
     * @param parent the system unit from which this alternate unit is derived.
     * @param symbol the symbol for this alternate unit.
     * @throws IllegalArgumentException if the specified parent unit is not an
     *         {@link AbstractUnit#isSystemUnit() system unit}
     */
    public AlternateUnit(AbstractUnit<?> parentUnit, String symbol) {
        if (!parentUnit.isSystemUnit())
            throw new IllegalArgumentException("The parent unit: " +  parentUnit
                    + " is not an unscaled SI unit");
        this.parentUnit = (parentUnit instanceof AlternateUnit) ?
            ((AlternateUnit<?>)parentUnit).getParentUnit() : parentUnit;
        this.symbol = symbol;
        
        // Checks if the symbol is associated to a different unit. TODO verify if we want these checks
 /*       synchronized (AbstractUnit.SYMBOL_TO_UNIT) {
            AbstractUnit<?> unit = (AbstractUnit<?>) AbstractUnit.SYMBOL_TO_UNIT.get(symbol);
            if (unit == null) {
                AbstractUnit.SYMBOL_TO_UNIT.put(symbol, this);
                return;
            }
            if (unit instanceof AlternateUnit<?>) {
                AlternateUnit<?> existingUnit = (AlternateUnit<?>) unit;
                if (symbol.equals(existingUnit.getSymbol()) && this.parentUnit.equals(existingUnit.parentUnit))
                    return; // OK, same unit.
            }
            throw new IllegalArgumentException("Symbol " + symbol + " is associated to a different unit");
        } */
    }
    
    /**
     * Creates an alternate unit for the specified system unit identified by the
     * specified name and symbol.
     *
     * @param parent the system unit from which this alternate unit is derived.
     * @param symbol the symbol for this alternate unit.
     * @throws IllegalArgumentException if the specified parent unit is not an
     *         {@link AbstractUnit#isSystemUnit() system unit}
     * @throws ClassCastException if parentUnit is not a valid {@link Unit} implementation
     */
    public AlternateUnit(Unit<?> parentUnit, String symbol) {
    	this((AbstractUnit<?>) parentUnit, symbol);
    }

    /**
     * Returns the parent unit of this alternate unit, always a system unit and
     * never an alternate unit.
     *
     * @return the parent unit.
     */
    public AbstractUnit<?> getParentUnit() {
        return parentUnit;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public Dimension getDimension() {
        return parentUnit.getDimension();
    }

    @Override
    public UnitConverter getConverterToSI() {
        return parentUnit.getConverterToSI();
    }

    @Override
    public AbstractUnit<Q> toSystemUnit() {
        return this; // Alternate units are SI units.
    }

    @Override
    public Map<? extends Unit<?>, Integer> getProductUnits() {
        return parentUnit.getProductUnits();
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof AlternateUnit))
            return false;
        AlternateUnit<?> that = (AlternateUnit<?>) obj;
        return this.parentUnit.equals(that.parentUnit) &&
                this.symbol.equals(that.symbol);
    }

}
