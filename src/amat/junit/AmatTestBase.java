
package amat.junit;

import jam.junit.NumericTestBase;

import amat.capture.EpitopeCaptureModel;
import amat.divide.DZDivisionModel;
import amat.occupy.OccupationModel;
import amat.signal.BCRSignalingModel;
import amat.tcell.TCellCompetitionModel;

import org.junit.*;
import static org.junit.Assert.*;

public class AmatTestBase extends NumericTestBase {
    static {
        System.setProperty(EpitopeCaptureModel.MODEL_TYPE_PROPERTY, "LANGMUIR");
        System.setProperty(DZDivisionModel.MODEL_TYPE_PROPERTY, "MEAN_CAPTURE_RATIO");
        System.setProperty(OccupationModel.MODEL_TYPE_PROPERTY, "LANGMUIR");
        System.setProperty(BCRSignalingModel.MODEL_TYPE_PROPERTY, "QUANTITY_THRESHOLD");
        System.setProperty(BCRSignalingModel.QUANTITY_THRESHOLD_PROPERTY, "10");
        System.setProperty(TCellCompetitionModel.MODEL_TYPE_PROPERTY, "ANTIGEN_QTY_MEAN_RATIO");
    }
}

