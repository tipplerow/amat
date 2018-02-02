
package amat.junit;

import jam.junit.NumericTestBase;

import amat.epitope.Epitope;
import amat.receptor.Receptor;
import amat.structure.Structure;

import org.junit.*;
import static org.junit.Assert.*;

public class AffinityModelTestBase extends NumericTestBase {
    protected static final Structure bitStructure1 = Structure.parse("BitStructure(00001110)");
    protected static final Structure bitStructure2 = Structure.parse("BitStructure(11110001)");
    protected static final Structure bitStructure3 = Structure.parse("BitStructure(00000000)");
    protected static final Structure bitStructure4 = Structure.parse("BitStructure(11111111)");

    protected static final Structure pottsStructure1 = Structure.parse("PottsStructure(4; AAAA ABCD)");
    protected static final Structure pottsStructure2 = Structure.parse("PottsStructure(4; ABCD ABCD)");
    protected static final Structure pottsStructure3 = Structure.parse("PottsStructure(4; BBBB ABCD)");
    protected static final Structure pottsStructure4 = Structure.parse("PottsStructure(4; DCBA DCBA)");

    protected static final Structure spinStructure1 = Structure.parse("SpinStructure(----+++-)");
    protected static final Structure spinStructure2 = Structure.parse("SpinStructure(++++---+)");
    protected static final Structure spinStructure3 = Structure.parse("SpinStructure(--------)");
    protected static final Structure spinStructure4 = Structure.parse("SpinStructure(++++++++)");

    protected static final Receptor bitReceptor1 = new Receptor(bitStructure1);
    protected static final Receptor bitReceptor2 = new Receptor(bitStructure2);
    protected static final Receptor bitReceptor3 = new Receptor(bitStructure3);
    protected static final Receptor bitReceptor4 = new Receptor(bitStructure4);

    protected static final Receptor pottsReceptor1 = new Receptor(pottsStructure1);
    protected static final Receptor pottsReceptor2 = new Receptor(pottsStructure2);
    protected static final Receptor pottsReceptor3 = new Receptor(pottsStructure3);
    protected static final Receptor pottsReceptor4 = new Receptor(pottsStructure4);

    protected static final Receptor spinReceptor1 = new Receptor(spinStructure1);
    protected static final Receptor spinReceptor2 = new Receptor(spinStructure2);
    protected static final Receptor spinReceptor3 = new Receptor(spinStructure3);
    protected static final Receptor spinReceptor4 = new Receptor(spinStructure4);
}
