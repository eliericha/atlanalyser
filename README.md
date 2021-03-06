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
   
Publications
============

 * E. Richa, E. Borde, and L. Pautet. [_Translation of ATL to AGT and application
   to a code generator for Simulink_](https://link.springer.com/article/10.1007/s10270-017-0607-8).
   Software & Systems Modeling, July 2017.

 * E. Richa. [_Qualification of source code generators in the avionics domain :
   automated testing of model transformation chains_](https://pastel.archives-ouvertes.fr/tel-01331877).
   Télécom ParisTech, Dec 2015.

 * E. Richa, E. Borde, and L. Pautet. [_Translating ATL Model Transformations to
   Algebraic Graph Transformations_](https://link.springer.com/chapter/10.1007%2F978-3-319-21155-8_14).
   ICMT 2015: Theory and Practice of Model Transformations, July 2015.
