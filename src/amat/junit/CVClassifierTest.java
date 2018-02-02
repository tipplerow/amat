
package amat.junit;

import java.util.Arrays;

import amat.structure.CVClassifier;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class CVClassifierTest {
    @Test public void testClassify() {
        Structure s1 = Structure.parse("BitStructure(0000 0000)");
        Structure s2 = Structure.parse("BitStructure(0011 0000 11)");
        Structure s3 = Structure.parse("BitStructure(0000 0010 11)");
        Structure s4 = Structure.parse("BitStructure(0000 1000 1111)");
    
        CVClassifier classifier = CVClassifier.classify(Arrays.asList(s4, s3, s1, s2));

        int[] conserved = new int[] { 0, 1, 5, 7 };
        int[] variable  = new int[] { 2, 3, 4, 6, 8, 9, 10, 11 };

        assertEquals(4, classifier.countConserved());
        assertEquals(8, classifier.countVariable());

        assertTrue(Arrays.equals(conserved, classifier.getConserved()));
        assertTrue(Arrays.equals(variable, classifier.getVariable()));

        assertTrue(classifier.isConserved(0));
        assertFalse(classifier.isVariable(0));

        assertFalse(classifier.isConserved(2));
        assertTrue(classifier.isVariable(2));
    }

    @Test public void testCountMatching() {
        Structure e1 = Structure.parse("PottsStructure(4; AAAA AAAA)");
        Structure e2 = Structure.parse("PottsStructure(4; AAAA BBBB)");
        Structure e3 = Structure.parse("PottsStructure(4; AAAA CCCC)");
        Structure e4 = Structure.parse("PottsStructure(4; AAAA DDDD)");
    
        CVClassifier classifier = CVClassifier.classify(Arrays.asList(e1, e2, e3, e4));

        Structure r1 = Structure.parse("PottsStructure(4; ABCD ABCD)");
        Structure r2 = Structure.parse("PottsStructure(4; AABB BAAA)");

        assertEquals(1, classifier.countMatchingConserved(r1, e1));
        assertEquals(1, classifier.countMatchingVariable(r1, e1));

        assertEquals(2, classifier.countMatchingConserved(r2, e1));
        assertEquals(3, classifier.countMatchingVariable(r2, e1));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("amat.junit.CVClassifierTest");
    }
}
