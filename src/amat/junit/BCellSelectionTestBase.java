
package amat.junit;

import java.util.HashSet;
import java.util.Set;

import jam.junit.NumericTestBase;

import amat.antigen.Antigen;
import amat.antigen.AntigenPool;
import amat.bcell.BCell;
import amat.binding.AffinityModel;
import amat.binding.HammingAffinity;
import amat.epitope.Epitope;
import amat.structure.Structure;
import amat.vaccine.Vaccine;

import org.junit.*;
import static org.junit.Assert.*;

public class BCellSelectionTestBase extends AmatTestBase {
    static {
        System.setProperty(AffinityModel.MODEL_TYPE_PROPERTY, "HAMMING");
        System.setProperty(HammingAffinity.MATCH_GAIN_PROPERTY, "2.0");
    }

    protected static final Epitope epitope = 
        Epitope.add("E1", Structure.parse("SpinStructure(---- ++++ ---- ++++)"));

    protected static final Antigen antigen = Antigen.add("A1", epitope);
    protected static final Vaccine vaccine = Vaccine.parse("A1, 10.0");

    protected static final AntigenPool antigenPool = new AntigenPool(vaccine);

    protected Set<BCell> generate(int count) {
        Set<BCell> cells = new HashSet<BCell>(count);

        while (cells.size() < count)
            cells.add(BCell.germline());

        return cells;
    }

    protected Set<BCell> prepare(int count) {
        Set<BCell> cells = generate(count);

        for (BCell cell : cells)
            cell.bind(antigenPool);

        return cells;
    }
}
