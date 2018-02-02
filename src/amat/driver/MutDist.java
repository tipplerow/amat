
package amat.driver;

import java.util.Arrays;
import jam.data.DataMatrix;
import amat.epitope.Epitope;

/**
 * Reports the mutational distance between epitopes.
 */
public final class MutDist {
    private static void usage() {
	System.err.println("Usage: amat.driver.MutDist FILE1 [FILE2 ...]");
	System.exit(1);
    }
    
    public static void main(String[] args) {
	if (args.length < 1)
	    usage();

	for (String fileName : args)
	    Epitope.load(fileName);

	String[] keys = Epitope.keys().toArray(new String[0]);
	Arrays.sort(keys);

	DataMatrix matrix = Epitope.mutationalDistance();

	for (int i = 0; i < keys.length; i++) {
	    System.out.println();
	    String key1 = keys[i];

	    for (int j = i + 1; j < keys.length; j++) {
		String key2 = keys[j];
		System.out.println("(" + key1 + ", " + key2 + ") => " + matrix.get(key1, key2));
	    }
	}
    }
}
