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
package org.moeaframework;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProblemFactoryTestWrapper;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.ZDT.ZDT5;

public class ProblemBuilderTest {
	
	@Test
	public void testIsProblemConfigured() {
		Assert.assertFalse(new ProblemBuilder().isProblemConfigured());
		Assert.assertTrue(new ProblemBuilder().withProblem("DTLZ2_2").isProblemConfigured());
		Assert.assertTrue(new ProblemBuilder().withProblem(new DTLZ2(2)).isProblemConfigured());
		Assert.assertTrue(new ProblemBuilder().withProblemClass(DTLZ2.class, 2).isProblemConfigured());
	}
	
	@Test(expected = FrameworkException.class)
	public void testNoProblemGetInstance() throws IOException {
		new ProblemBuilder().getProblemInstance();
	}
	
	@Test(expected = FrameworkException.class)
	public void testNoProblemGetReferenceSet() throws IOException {
		new ProblemBuilder().getReferenceSet();
	}
	
	@Test(expected = FrameworkException.class)
	public void testNoReferenceSet() throws IOException {
		//20D DTLZ2 has no reference set
		new ProblemBuilder().withProblem("DTLZ2_20").getReferenceSet();
	}
	
	@Test
	public void testProblemFactory() {
		ProblemFactoryTestWrapper problemFactory = new ProblemFactoryTestWrapper();
		ProblemBuilder builder = new ProblemBuilder()
				.usingProblemFactory(problemFactory)
				.withProblem("DTLZ2_2");
		
		builder.getReferenceSet();
		builder.getProblemInstance().close();
		
		Assert.assertEquals(1, problemFactory.getCloseCount());
	}
	
	@Test
	public void testGetProblemInstance() throws ClassNotFoundException {
		ProblemBuilder builder = new ProblemBuilder();
		
		Assert.assertNotNull(builder.withProblem("ZDT5").getProblemInstance());
		Assert.assertNotNull(builder.withProblemClass(ZDT5.class).getProblemInstance());
		Assert.assertNotNull(builder.withProblem(new ZDT5()).getProblemInstance());
		Assert.assertNotNull(builder.withProblemClass("org.moeaframework.problem.ZDT.ZDT5").getProblemInstance());
	}
	
	@Test
	public void testConstructorArguments() throws ClassNotFoundException {
		ProblemBuilder builder = new ProblemBuilder();
		
		Problem problem1 = builder.withProblemClass(DTLZ2.class, 4).getProblemInstance();
		Problem problem2 = builder.withProblemClass("org.moeaframework.problem.DTLZ.DTLZ2", 6).getProblemInstance();
		
		Assert.assertNotNull(problem1);
		Assert.assertNotNull(problem2);
		Assert.assertEquals(4, problem1.getNumberOfObjectives());
		Assert.assertEquals(6, problem2.getNumberOfObjectives());
	}
	
	@Test
	public void testConstructorArgumentsWidening() {
		ProblemBuilder builder = new ProblemBuilder();
		
		Assert.assertNotNull(builder.withProblemClass(DTLZ2.class, (byte)4).getProblemInstance());
		Assert.assertNotNull(builder.withProblemClass(DTLZ2.class, (char)4).getProblemInstance());
		Assert.assertNotNull(builder.withProblemClass(DTLZ2.class, (short)4).getProblemInstance());
	}
	
	@Test(expected = FrameworkException.class)
	public void testConstructorArgumentsException() {
		new ProblemBuilder().withProblemClass(DTLZ2.class, (long)4).getProblemInstance();
	}
	
	@Test
	public void testReferenceSetFromProblemFactory() throws IOException {
		File file = TestResources.asFile("pf/DTLZ2.2D.pf");
		
		NondominatedPopulation actual = new ProblemBuilder()
				.withEpsilon(0.01)
				.withProblem("DTLZ2_2")
				.getReferenceSet();

		NondominatedPopulation expected = new EpsilonBoxDominanceArchive(0.01,
				NondominatedPopulation.loadReferenceSet(file));
		
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testReferenceSetFromFile() throws IOException {
		//purposely load DTLZ1 reference set
		File file = TestResources.asFile("pf/DTLZ1.2D.pf");
		
		NondominatedPopulation actual = new ProblemBuilder()
				.withProblem("DTLZ2_2")
				.withReferenceSet(file)
				.getReferenceSet();
		
		NondominatedPopulation expected = NondominatedPopulation.loadReferenceSet(file);
		
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testReferenceSet() throws IOException {
		NondominatedPopulation expected = NondominatedPopulation.loadReferenceSet(new File("./pf/DTLZ1.2D.pf"));
		
		NondominatedPopulation actual = new ProblemBuilder()
				.withProblem("DTLZ2_2")
				.withReferenceSet(expected)
				.getReferenceSet();
		
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testWithSameProblemAs() {
		ProblemBuilder builder1 = new ProblemBuilder()
				.withProblem("DTLZ2_2")
				.withReferenceSet(new File("./pf/DTLZ1.2D.pf"));
		
		ProblemBuilder builder2 = new ProblemBuilder()
				.withSameProblemAs(builder1);
		
		Assert.assertEquals(builder1.problemName, builder2.problemName);
		Assert.assertArrayEquals(builder1.problemArguments, builder2.problemArguments);
		Assert.assertEquals(builder1.problemClass, builder2.problemClass);
		Assert.assertEquals(builder1.problemFactory, builder2.problemFactory);
		Assert.assertEquals(builder1.epsilons, builder2.epsilons);
		
		builder2.withProblemClass(DTLZ2.class, 4).usingProblemFactory(ProblemFactory.getInstance());
		builder1.withSameProblemAs(builder2);
		
		Assert.assertEquals(builder1.problemName, builder2.problemName);
		Assert.assertArrayEquals(builder1.problemArguments, builder2.problemArguments);
		Assert.assertEquals(builder1.problemClass, builder2.problemClass);
		Assert.assertEquals(builder1.problemFactory, builder2.problemFactory);
		Assert.assertEquals(builder1.epsilons, builder2.epsilons);
	}

}
