ATL Analyser
============

A tool to analyze ATL transformations using the formal framework of Algebraic
Graph Transformation (AGT).

The tool is composed of two main components:

 * ATL2AGT: a translator of ATL transformations to the Henshin AGT framework.
 * ATLPost2Pre: an analysis tool to transform postconditions of ATL
   transformations (expressed as Henshin nested conditions) to equivalent
   preconditions.

Usage examples of both components can be found in the form of JUnit tests in
fr.tpt.atlanalyser.examples/src/fr/tpt/atlanalyser/tests. For example: 
 * TestClass2Relational.java : Translation of ATL transformation
   Class2Relational to a Henshin equivalent transformation.
 * TestClass2RelationalExec.java : Execution of both ATL and AGT versions over
   the same set of models, and validation that the results are equivalent using
   EMFCompare.