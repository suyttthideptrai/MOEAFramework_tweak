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
package org.moeaframework.problem.mock;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;

public class MockGrammarProblem extends MockProblem {
	
	public MockGrammarProblem() {
		this(1);
	}
	
	public MockGrammarProblem(int numberOfObjectives) {
		super(1, numberOfObjectives);
	}

	@Override
	public Solution newSolution() {
		Solution solution = super.newSolution();
		solution.setVariable(0, new Grammar(10));
		return solution;
	}

}
