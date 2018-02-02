
package amat.driver;

import java.util.Arrays;

import amat.epitope.Epitope;
import amat.structure.CVClassifier;

/**
 * Identifies structural elements that are conserved across all
 * epitopes in a collection of configuration files.
 */
public final class CVClass {
    private static void usage() {
	System.err.println("Usage: amat.driver.CVClass FILE1 [FILE2 ...]");
	System.exit(1);
    }
    
    public static void main(String[] args) {
	if (args.length < 1)
	    usage();

	for (String fileName : args)
	    Epitope.load(fileName);

        CVClassifier classifier = Epitope.classify();

        System.out.println("CONSERVED: " + Arrays.toString(classifier.getConserved()));
        System.out.println("VARIABLE:  " + Arrays.toString(classifier.getVariable()));
    }
}
