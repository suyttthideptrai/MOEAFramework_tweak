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
package org.moeaframework.problem.single;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Settings;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.ProblemTest;

public class HimmelblauTest extends ProblemTest {
	
	@Test
	public void test() {		
		Himmelblau problem = new Himmelblau();
		
		// local minima
		Assert.assertEquals(0.0, MockSolution.of(problem).at(3.0, 2.0).evaluate().getObjective(0), Settings.EPS);
		Assert.assertEquals(0.0, MockSolution.of(problem).at(-2.805118, 3.131312).evaluate().getObjective(0), Settings.EPS);
		Assert.assertEquals(0.0, MockSolution.of(problem).at(-3.779310, -3.283186).evaluate().getObjective(0), Settings.EPS);
		Assert.assertEquals(0.0, MockSolution.of(problem).at(3.584428, -1.848126).evaluate().getObjective(0), Settings.EPS);
		
		// local maximum
		Assert.assertEquals(181.617, MockSolution.of(problem).at(-0.270845, -0.923039).evaluate().getObjective(0), 0.001);
	}

}
