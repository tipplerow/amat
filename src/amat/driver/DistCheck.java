
package amat.driver;

import jam.data.DataMatrix;
import jam.math.DoubleComparator;

import amat.epitope.Epitope;

/**
 * Ensures that all epitopes in a single configuraiton file have the
 * same relative mutational distance.
 */
public final class DistCheck {
    private static void usage() {
	System.err.println("Usage: amat.driver.DistCheck FILE DISTANCE");
	System.exit(1);
    }
    
    public static void main(String[] args) {
	if (args.length != 2)
	    usage();

        String fileName = args[0];
        double expected = Double.parseDouble(args[1]);

        Epitope.load(fileName);

	DataMatrix matrix = Epitope.mutationalDistance();
        String[] keys = matrix.rowKeys().toArray(new String[0]);

	for (int i = 0; i < keys.length; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                String rowKey = keys[i];
                String colKey = keys[j];
                double actual = matrix.get(rowKey, colKey);

                if (DoubleComparator.DEFAULT.NE(expected, actual)) {
                    System.err.println(String.format("Unexpected mutational distance: (%s, %s) => %f", rowKey, colKey, actual));
                    System.exit(1);
                }
            }
        }

        System.out.println("OKAY!");
    }
}
