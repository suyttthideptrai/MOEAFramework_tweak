/* Copyright 2009-2024 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.util.validate;

import org.junit.Test;

public class ValidatorTest {
	
	@Test(expected = IllegalArgumentException.class)
	public void testFails() {
		Validate.that("foo", 1).fails("This always fails");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailUnrecognizedOption() {
		Validate.that("foo", "value").failUnsupportedOption();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailUnrecognizedOptionWithSupportedValues() {
		Validate.that("foo", "value").failUnsupportedOption("other");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailUnsupportedOptionWithEnum() {
		Validate.that("foo", TestEnum.Foo).failUnsupportedOption();
	}
	
	private enum TestEnum {
		Foo,
		Bar
	}
	
}
