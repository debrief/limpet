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
package tec.units.ri.quantity;

import static org.junit.Assert.*;
import static tec.units.ri.unit.SI.METRE;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.junit.Before;
import org.junit.Test;

import tec.units.ri.spi.DefaultQuantityFactory;


public class ArithmeticTest {

	private Quantity<Length> sut;
	
	@Before
	public void init() {
		sut = DefaultQuantityFactory.getInstance(Length.class).create(10, METRE);
	}
	
	@Test
	public void testValue() {
		assertEquals(Integer.valueOf(10), sut.getValue());
	}
	
	@Test
	public void testToString() {
		assertEquals("10 m", sut.toString());
	}
	
	@Test
	public void testAdd() {
		Quantity<Length> len = DefaultQuantityFactory.getInstance(Length.class).create(5, METRE);
		Quantity<Length> result = sut.add(len);
		assertNotNull(result);
		assertEquals(METRE, result.getUnit());
		assertEquals(Double.valueOf(15), result.getValue());
	}

	@Test
	public void testSubtract() {
		Quantity<Length> len = DefaultQuantityFactory.getInstance(Length.class).create(5, METRE);
		Quantity<Length> result = sut.subtract(len);
		assertNotNull(result);
		assertEquals(METRE, result.getUnit());
		assertEquals(Double.valueOf(5), result.getValue());
	}
	
}
