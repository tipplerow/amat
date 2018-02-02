
package amat.signal;

import jam.chem.Langmuir;
import jam.math.Probability;

import amat.bcell.BCell;
import amat.bcell.IndependentApoptosisModel;

/**
 * Represents a manner of BCR signaling by which B cells receive
 * survival signals with probability {@code Q / (1.0 + Q)} (the
 * Langmuir isothem function), where {@code Q} is the amount of
 * antigen captured and internalized.
 */
public final class QuantityLangmuirSignaling extends IndependentApoptosisModel {
    private QuantityLangmuirSignaling() {}

    /**
     * The global Langmuir quantity signaling model.
     */
    public static final QuantityLangmuirSignaling INSTANCE = new QuantityLangmuirSignaling();

    @Override public boolean apoptose(BCell cell) {
        double quantity = cell.getAntigenQty();
        double langmuir = Langmuir.INSTANCE.evaluate(quantity);

        Probability survivalProb = Probability.valueOf(langmuir);
        Probability apoptosisProb = Probability.not(survivalProb);

        return apoptosisProb.accept();
    }
}
