package io.openmanufacturing.sds.aspectmodel.aas;

import java.util.Map;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;

import com.google.common.collect.ImmutableMap;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.NamedElement;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Type;

/**
 * Base interface for any class that can map a property to a {@link SubmodelElement}.
 *
 * @param <T> the concrete type of {@link SubmodelElement} the implementing mapper produces
 */
public interface PropertyMapper<T extends SubmodelElement> {
   static final String UNKNOWN_TYPE = "Unknown";

   static final String UNKNOWN_EXAMPLE = UNKNOWN_TYPE;

   static final LangStringMapper LANG_STRING_MAPPER = new LangStringMapper();

   /**
    * Maps Aspect types to DataTypeDefXsd Schema types, with no explicit mapping defaulting to
    * string
    */
   static final Map<Resource, DataTypeDefXsd> AAS_XSD_TYPE_MAP =
         ImmutableMap.<Resource, DataTypeDefXsd> builder()
                     .put( XSD.anyURI, DataTypeDefXsd.ANY_URI )
                     .put( XSD.yearMonthDuration, DataTypeDefXsd.YEAR_MONTH_DURATION )
                     .put( XSD.xboolean, DataTypeDefXsd.BOOLEAN )
                     .put( XSD.xbyte, DataTypeDefXsd.BYTE )
                     .put( XSD.date, DataTypeDefXsd.DATE )
                     .put( XSD.dateTime, DataTypeDefXsd.DATE_TIME )
                     .put( XSD.dateTimeStamp, DataTypeDefXsd.DATE_TIME_STAMP )
                     .put( XSD.dayTimeDuration, DataTypeDefXsd.DAY_TIME_DURATION )
                     .put( XSD.decimal, DataTypeDefXsd.DECIMAL )
                     .put( XSD.xdouble, DataTypeDefXsd.DOUBLE )
                     .put( XSD.duration, DataTypeDefXsd.DURATION )
                     .put( XSD.xfloat, DataTypeDefXsd.FLOAT )
                     .put( XSD.gMonth, DataTypeDefXsd.GMONTH )
                     .put( XSD.gMonthDay, DataTypeDefXsd.GMONTH_DAY )
                     .put( XSD.gYear, DataTypeDefXsd.GYEAR )
                     .put( XSD.gYearMonth, DataTypeDefXsd.GYEAR_MONTH )
                     .put( XSD.hexBinary, DataTypeDefXsd.HEX_BINARY )
                     .put( XSD.xint, DataTypeDefXsd.INT )
                     .put( XSD.integer, DataTypeDefXsd.INTEGER )
                     .put( XSD.xlong, DataTypeDefXsd.LONG )
                     .put( XSD.negativeInteger, DataTypeDefXsd.NEGATIVE_INTEGER )
                     .put( XSD.nonNegativeInteger, DataTypeDefXsd.NON_NEGATIVE_INTEGER )
                     .put( XSD.positiveInteger, DataTypeDefXsd.POSITIVE_INTEGER )
                     .put( XSD.xshort, DataTypeDefXsd.SHORT )
                     .put( XSD.normalizedString, DataTypeDefXsd.STRING )
                     .put( XSD.time, DataTypeDefXsd.TIME )
                     .put( XSD.unsignedByte, DataTypeDefXsd.UNSIGNED_BYTE )
                     .put( XSD.unsignedInt, DataTypeDefXsd.UNSIGNED_INT )
                     .put( XSD.unsignedLong, DataTypeDefXsd.UNSIGNED_LONG )
                     .put( XSD.unsignedShort, DataTypeDefXsd.UNSIGNED_SHORT )
                     .build();

   /**
    * Performs the mapping of the given property to a AAS {@link SubmodelElement}.
    *
    * @param type the type of the given property
    * @param property the property to map
    * @param context the current visitor context
    * @return the newly created {@link SubmodelElement}
    */
   T mapToAasProperty( Type type, Property property, Context context );

   /**
    * Whether this {@code PropertyMapper} can handle the given property.
    *
    * Defaults to {@code true}, implementors should override.
    *
    * @param property the property to test
    * @return {@code true} if this property mapper can handle the given property, {@code false} else
    */
   default boolean canHandle( Property property ) {
      return true;
   }

   /**
    * Maps the given URN to a {@link DataTypeDefXsd} schema type.
    *
    * @param urn the URN to map
    * @return the {@code DataTypeDefXsd} for the given URN
    */
   default DataTypeDefXsd mapAASXSDataType( String urn ) {
      final Resource resource = ResourceFactory.createResource( urn );
      return AAS_XSD_TYPE_MAP.getOrDefault( resource, DataTypeDefXsd.STRING );
   }

   /**
    * Builds a concept description reference for the given property.
    *
    * @param property the property to build the reference for
    * @return the newly created reference
    */
   default Reference buildReferenceToConceptDescription( Property property ) {
      final Key key =
            new DefaultKey.Builder()
                  .type( KeyTypes.CONCEPT_DESCRIPTION )
                  .value( determineIdentifierFor( property ) )
                  .build();
      return new DefaultReference.Builder().keys( key ).build();
   }

   /**
    * Determines the identifier for the given {@link NamedElement}.
    *
    * @param element the element to get the identifier for
    * @return the identifier
    */
   default String determineIdentifierFor( NamedElement element ) {
      return element.getAspectModelUrn()
                        .map( AspectModelUrn::toString )
                        .orElseGet( element::getName );
   }
}
