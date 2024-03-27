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
package org.moeaframework.problem.CEC2009;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class UF4Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new UF4();
		
		Assert.assertArrayEquals(new double[] { 0.027885, 1.030503 }, 
				evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.178014, 0.171358 }, 
				evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
	}

	@Test
	public void testJMetal() {
		testAgainstJMetal("UF4");
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("UF4", 2);
	}

}
