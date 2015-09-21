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
package tec.units.ri.util;

import static org.junit.Assert.*;
import static tec.units.ri.unit.SI.KILOGRAM;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import tec.units.ri.quantity.NumberQuantity;
import tec.units.ri.util.Range;

public class RangeTest {
	private Quantity<Mass> min;
	private Quantity<Mass> max;
	private Quantity<Mass> res;
	@SuppressWarnings("rawtypes")
	private Range range;
	
	@Before
	public void init() {
		min = NumberQuantity.of(1d, KILOGRAM);
		max = NumberQuantity.of(10d, KILOGRAM);
		res = NumberQuantity.of(2d, KILOGRAM);
		
		range = QuantityRange.of(min, max, res);
	}
	
	@Test
	public void testGetMinimum() {
		assertEquals(min, range.getMinimum());
	}

	@Test
	public void testGetMaximum() {
		assertEquals(max, range.getMaximum());
	}

	@Test
	public void testGetResolution() {
		assertTrue(range.getClass().equals(QuantityRange.class));
		@SuppressWarnings("unchecked")
		QuantityRange<Mass> qr = (QuantityRange<Mass>) range;
		assertEquals(res, qr.getResolution());
	}

	@Test
	@Ignore
	public void testToString() {
		assertEquals("min= 1.0 kg, max= 10.0 kg, res= 2.0 kg", range.toString());
	}

}
