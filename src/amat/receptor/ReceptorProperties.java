
package amat.receptor;

import jam.app.JamProperties;
import jam.lang.JamException;

import amat.epitope.Epitope;
import amat.structure.StructureType;

/**
 * Manages receptor characteristics defined through global properties.
 *
 * <p><b>{@code amat.Receptor.structureType:}</b> Enumerated type for
 * the receptor structure. If this property is not set, the type will
 * default to the unique structure type of the epitopes in the global
 * registry.
 *
 * <p><b>{@code amat.Receptor.length:}</b> Number of elements (or
 * dimensions) in the receptor structure.  If this property is not
 * set, the length will default to the unique length of the epitopes
 * in the global registry.
 *
 * <p><b>{@code amat.Receptor.cardinality:}</b> For discrete receptor
 * structures, the number of unique structural elements.  If this
 * property is not set, the cardinality will default to the unique
 * cardinality of epitopes in the global registry.
 */
public final class ReceptorProperties {
    //
    // Prevent instantiation...
    //
    private ReceptorProperties() {
    }

    private static StructureType structureType = null;
    private static Integer length = null;
    private static Integer cardinality = null;

    /**
     * Name of the global property which defines the receptor structure
     * type (a valid {@code amat.structure.StructureType} name).
     */
    public static final String STRUCTURE_TYPE_PROPERTY = "amat.Receptor.structureType";

    /**
     * Name of the global property which defines the number of
     * elements or dimensions in each receptor.
     */
    public static final String LENGTH_PROPERTY = "amat.Receptor.length";

    /**
     * Name of the global property which defines the number of unique
     * structural elements in discrete structures.
     */
    public static final String CARDINALITY_PROPERTY = "amat.Receptor.cardinality";

    /**
     * Returns the enumerated type for the receptor structure.
     *
     * <p>If the {@link ReceptorProperties#STRUCTURE_TYPE_PROPERTY} is
     * not set, the receptor type will default to the unique structure
     * type of epitopes in the global registry.
     *
     * @return the enumerated type for the receptor structure.
     *
     * @throws RuntimeException unless a valid property is defined in
     * the global property space or the epitopes have a unique type
     * (in which case the receptors will match it).
     */
    public static StructureType getStructureType() {
        if (structureType == null)
            resolveStructureType();

        return structureType;
    }

    private static void resolveStructureType() {
        if (JamProperties.isSet(STRUCTURE_TYPE_PROPERTY))
            structureType = JamProperties.getRequiredEnum(STRUCTURE_TYPE_PROPERTY, StructureType.class);
        else
            structureType = Epitope.resolveType();
    }

    /**
     * Returns the number of elements or dimensions in each receptor.
     *
     * <p>If the {@link ReceptorProperties#LENGTH_PROPERTY} is not
     * set, the receptor length will default to the unique length of
     * epitopes in the global registry.
     *
     * @return the number of elements or dimensions in each receptor.
     *
     * @throws RuntimeException unless a valid property is defined in
     * the global property space or the epitopes have a unique length
     * (in which case the receptors will match it).
     */
    public static int getLength() {
        if (length == null)
            resolveLength();

        return length.intValue();
    }

    private static void resolveLength() {
        if (JamProperties.isSet(LENGTH_PROPERTY))
            length = JamProperties.getRequiredInt(LENGTH_PROPERTY);
        else
            length = Epitope.resolveLength();

        if (length.intValue() < 1)
            throw JamException.runtime("Invalid receptor length: [%s].", length);
    }

    /**
     * Returns the number of unique structural element types for
     * discrete receptors.
     *
     * <p>If the {@link ReceptorProperties#CARDINALITY_PROPERTY} is
     * not set, the receptor cardinality will default to the unique
     * cardinality of epitopes in the global registry.
     *
     * @return the number of unique structural element types for
     * discrete receptors.
     *
     * @throws RuntimeException unless a valid property is defined
     * in the global property space or the epitopes have a unique
     * cardinality (in which case the receptors will match it).
     */
    public static int getCardinality() {
        if (cardinality == null)
            resolveCardinality();

        return cardinality.intValue();
    }

    private static void resolveCardinality() {
        if (JamProperties.isSet(CARDINALITY_PROPERTY))
            cardinality = JamProperties.getRequiredInt(CARDINALITY_PROPERTY);
        else
            cardinality = Epitope.resolveCardinality();

        if (cardinality.intValue() < 2)
            throw JamException.runtime("Invalid receptor cardinality: [%s].", cardinality);
    }
}
