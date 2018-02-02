
package amat.structure;

import jam.lang.JamException;
import jam.spin.SpinVector;
import jam.util.RegexUtil;
import jam.util.StringUtil;
import jam.vector.BitVector;
import jam.vector.JamVector;

final class StructureParser {

    static Structure parse(String str) {
        // Save the original string for error messages...
        String workspace = str.trim();

        // Remove the closing parenthesis...
        if (!workspace.endsWith(")"))
            throw JamException.runtime("Invalid structure: [%s].", str);

        workspace = StringUtil.chop(workspace);

        // Split on the opening parenthesis...
        String[] fields = RegexUtil.OPEN_PAREN.split(workspace);

        if (fields.length != 2)
            throw JamException.runtime("Invalid structure: [%s].", str);

        String className = fields[0];
        String structRep = StringUtil.removeWhiteSpace(fields[1]);

        switch (className) {
        case "BitStructure":
            return new BitStructure(BitVector.parse(structRep));

        case "PottsStructure":
            return PottsStructure.parse(structRep);

        case "ShapeStructure":
            return new ShapeStructure(JamVector.parseCSV(structRep));

        case "SpinStructure":
            return new SpinStructure(SpinVector.parse(structRep));

        default:
            throw JamException.runtime("Unknown structure type: [%s].", className);
        }
    }
}
