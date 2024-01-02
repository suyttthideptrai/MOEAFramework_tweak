/* Copyright 2009-2023 David Hadka
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
package org.moeaframework.problem.LSMOP;

/**
 * The mixed-modality, mixed-separability LSMOP4 problem.
 */
public class LSMOP4 extends LSMOP {
	
	public LSMOP4(int M) {
		this(M, DEFAULT_N_k);
	}
	
	public LSMOP4(int M, int N_k) {
		super(M, N_k, ShapeFunction.Ackley, ShapeFunction.Griewank, LinkageFunction.Linear,
				CorrelationMatrix.Separable, ParetoFrontGeometry.Linear);
	}

}
