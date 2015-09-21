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

/**
 * <p> This class represents an annotated unit.</p>
 * 
 * <p> Instances of this class are created through the
 *     {@link AbstractUnit#annotate(String)} method.</p>
 *
 * @param <Q> The type of the quantity measured by this unit.
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 0.4, January 18, 2015
 */
public final class AnnotatedUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> {

    /**
     * Holds the actual unit.
     */
    private final Unit<?> actualUnit;

    /**
     * Holds the annotation.
     */
    private final String annotation;

    /**
     * Creates an annotated unit equivalent to the specified unit.
     *
     * @param actualUnit the unit to be annotated.
     * @param annotation the annotation.
     * @return the annotated unit.
     */
    public AnnotatedUnit(Unit<?> actualUnit, String annotation) {
        this.actualUnit = (actualUnit instanceof AnnotatedUnit) ?
            ((AnnotatedUnit<Q>)actualUnit).actualUnit : actualUnit;
        this.annotation = annotation;
    }

    /**
     * Returns the actual unit of this annotated unit (never an annotated unit
     * itself).
     *
     * @return the actual unit.
     */
    public Unit<?> getActualUnit() {
        return actualUnit;
    }

    /**
     * Returns the annotation of this annotated unit.
     *
     * @return the annotation.
     */
     public String getAnnotation() {
        return annotation;
    }

    @Override
    public String getSymbol() {
        return actualUnit.getSymbol();
    }

    @Override
    public Map<? extends Unit<?>, Integer> getProductUnits() {
        return actualUnit.getProductUnits();
    }

    @Override
    public AbstractUnit<Q> toSystemUnit() {
        return (AbstractUnit<Q>) actualUnit.getSystemUnit();
    }

    @Override
    public Dimension getDimension() {
        return actualUnit.getDimension();
    }

    @Override
    public UnitConverter getConverterToSI() {
    	if (actualUnit instanceof AbstractUnit) {
    		return ((AbstractUnit<?>)actualUnit).getConverterToSI();
    	} else {
    		throw new ClassCastException("Unknown type " + actualUnit.getClass());
    	}
    }

    @Override
    public int hashCode() {
        return actualUnit.hashCode() + annotation.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof AnnotatedUnit<?>))
            return false;
        AnnotatedUnit<?> thatUnit = (AnnotatedUnit<?>) that;
        return this.actualUnit.equals(thatUnit.actualUnit) &&
                this.annotation.equals(thatUnit.annotation);
    }
}
