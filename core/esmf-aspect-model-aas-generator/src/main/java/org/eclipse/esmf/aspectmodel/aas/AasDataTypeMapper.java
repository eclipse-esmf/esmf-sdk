package org.eclipse.esmf.aspectmodel.aas;

import com.google.common.collect.ImmutableMap;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXSD;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeIec61360;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Type;

import java.util.Map;
import java.util.Optional;

public class AasDataTypeMapper {

   private AasDataTypeMapper() {
   }

   /**
    * Maps Aspect types to DataTypeDefXSD Schema types, with no explicit mapping defaulting to
    * string
    */
   static final Map<Resource, DataTypeDefXSD> ASPECT_TYPE_TO_AAS_XSD_TYPE_MAP =
         ImmutableMap.<Resource, DataTypeDefXSD> builder()
               .put( XSD.anyURI, DataTypeDefXSD.ANY_URI )
               .put( XSD.yearMonthDuration, DataTypeDefXSD.DURATION )
               .put( XSD.xboolean, DataTypeDefXSD.BOOLEAN )
               .put( XSD.xbyte, DataTypeDefXSD.BYTE )
               .put( XSD.date, DataTypeDefXSD.DATE )
               .put( XSD.dateTime, DataTypeDefXSD.DATE_TIME )
               .put( XSD.dateTimeStamp, DataTypeDefXSD.DATE_TIME )
               .put( XSD.dayTimeDuration, DataTypeDefXSD.DURATION )
               .put( XSD.decimal, DataTypeDefXSD.DECIMAL )
               .put( XSD.xdouble, DataTypeDefXSD.DOUBLE )
               .put( XSD.duration, DataTypeDefXSD.DURATION )
               .put( XSD.xfloat, DataTypeDefXSD.FLOAT )
               .put( XSD.gMonth, DataTypeDefXSD.GMONTH )
               .put( XSD.gMonthDay, DataTypeDefXSD.GMONTH_DAY )
               .put( XSD.gYear, DataTypeDefXSD.GYEAR )
               .put( XSD.gYearMonth, DataTypeDefXSD.GYEAR_MONTH )
               .put( XSD.hexBinary, DataTypeDefXSD.HEX_BINARY )
               .put( XSD.xint, DataTypeDefXSD.INT )
               .put( XSD.integer, DataTypeDefXSD.INTEGER )
               .put( XSD.xlong, DataTypeDefXSD.LONG )
               .put( XSD.negativeInteger, DataTypeDefXSD.NEGATIVE_INTEGER )
               .put( XSD.nonNegativeInteger, DataTypeDefXSD.NON_NEGATIVE_INTEGER )
               .put( XSD.positiveInteger, DataTypeDefXSD.POSITIVE_INTEGER )
               .put( XSD.xshort, DataTypeDefXSD.SHORT )
               .put( XSD.normalizedString, DataTypeDefXSD.STRING )
               .put( XSD.time, DataTypeDefXSD.TIME )
               .put( XSD.unsignedByte, DataTypeDefXSD.UNSIGNED_BYTE )
               .put( XSD.unsignedInt, DataTypeDefXSD.UNSIGNED_INT )
               .put( XSD.unsignedLong, DataTypeDefXSD.UNSIGNED_LONG )
               .put( XSD.unsignedShort, DataTypeDefXSD.UNSIGNED_SHORT )
               .build();

   /**
    * Maps DataTypeDefXSD Schema types to Aspect types, with no explicit mapping defaulting to
    * normalizedString.
    */
   static final Map<DataTypeDefXSD, Resource> AAS_XSD_TYPE_TO_ASPECT_TYPE_MAP =
         ImmutableMap.<DataTypeDefXSD, Resource> builder()
               .put( DataTypeDefXSD.ANY_URI, XSD.anyURI )
               .put( DataTypeDefXSD.BOOLEAN, XSD.xboolean )
               .put( DataTypeDefXSD.BYTE, XSD.xbyte )
               .put( DataTypeDefXSD.DATE, XSD.date )
               .put( DataTypeDefXSD.DATE_TIME, XSD.dateTime )
               .put( DataTypeDefXSD.DECIMAL, XSD.decimal )
               .put( DataTypeDefXSD.DOUBLE, XSD.xdouble )
               .put( DataTypeDefXSD.DURATION, XSD.duration )
               .put( DataTypeDefXSD.FLOAT, XSD.xfloat )
               .put( DataTypeDefXSD.GMONTH, XSD.gMonth )
               .put( DataTypeDefXSD.GMONTH_DAY, XSD.gMonthDay )
               .put( DataTypeDefXSD.GYEAR, XSD.gYear )
               .put( DataTypeDefXSD.GYEAR_MONTH, XSD.gYearMonth )
               .put( DataTypeDefXSD.HEX_BINARY, XSD.hexBinary )
               .put( DataTypeDefXSD.INT, XSD.xint )
               .put( DataTypeDefXSD.INTEGER, XSD.integer )
               .put( DataTypeDefXSD.LONG, XSD.xlong )
               .put( DataTypeDefXSD.NEGATIVE_INTEGER, XSD.negativeInteger )
               .put( DataTypeDefXSD.NON_NEGATIVE_INTEGER, XSD.nonNegativeInteger )
               .put( DataTypeDefXSD.POSITIVE_INTEGER, XSD.positiveInteger )
               .put( DataTypeDefXSD.SHORT, XSD.xshort )
               .put( DataTypeDefXSD.STRING, XSD.normalizedString )
               .put( DataTypeDefXSD.TIME, XSD.time )
               .put( DataTypeDefXSD.UNSIGNED_BYTE, XSD.unsignedByte )
               .put( DataTypeDefXSD.UNSIGNED_INT, XSD.unsignedInt )
               .put( DataTypeDefXSD.UNSIGNED_LONG, XSD.unsignedLong )
               .put( DataTypeDefXSD.UNSIGNED_SHORT, XSD.unsignedShort )
               .build();

   /**
    * Maps Aspect types to DataTypeIEC61360 Schema types, with no explicit mapping defaulting to
    * string
    */
   private static final Map<Resource, DataTypeIec61360> ASPECT_TYPE_TO_AAX_DATA_TYPE_IEC_61360 =
         ImmutableMap.<Resource, DataTypeIec61360> builder()
               .put( XSD.xboolean, DataTypeIec61360.BOOLEAN )
               .put( XSD.decimal, DataTypeIec61360.INTEGER_MEASURE )
               .put( XSD.integer, DataTypeIec61360.INTEGER_MEASURE )
               .put( XSD.xfloat, DataTypeIec61360.REAL_MEASURE )
               .put( XSD.xdouble, DataTypeIec61360.REAL_MEASURE )
               .put( XSD.xbyte, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.xshort, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.xint, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.xlong, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.unsignedByte, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.unsignedShort, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.unsignedInt, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.unsignedLong, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.positiveInteger, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.nonPositiveInteger, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.negativeInteger, DataTypeIec61360.INTEGER_COUNT )
               .put( XSD.nonNegativeInteger, DataTypeIec61360.INTEGER_COUNT )
               .put( RDF.langString, DataTypeIec61360.STRING )
               .build();

   /**
    * Maps the given URN to a {@link DataTypeDefXSD} schema type.
    *
    * @param urn the URN to map
    * @return the {@code DataTypeDefXSD} for the given URN
    */
   public static DataTypeDefXSD mapAspectTypeToAASXSDataType( final String urn ) {
      final Resource resource = ResourceFactory.createResource( urn );
      return ASPECT_TYPE_TO_AAS_XSD_TYPE_MAP.getOrDefault( resource, DataTypeDefXSD.STRING );
   }

   /**
    * Maps the given URN to a {@link Resource} schema type.
    *
    * @param dataTypeDefXSD the URN to map
    * @return the {@code Resource} for the given URN
    */
   public static Resource mapAASXSDataTypeToAspectType( final DataTypeDefXSD dataTypeDefXSD ) {
      return AAS_XSD_TYPE_TO_ASPECT_TYPE_MAP.getOrDefault( dataTypeDefXSD, XSD.normalizedString );
   }

   /**
    * Maps the given URN to a {@link DataTypeIec61360} schema type.
    *
    * @param urn the URN to map
    * @return the {@code DataTypeIec61360} for the given URN
    */
   public static DataTypeIec61360 mapIEC61360DataType( final String urn ) {
      final Resource resource = ResourceFactory.createResource( urn );
      return ASPECT_TYPE_TO_AAX_DATA_TYPE_IEC_61360.getOrDefault( resource, DataTypeIec61360.STRING );
   }

   /**
    * Maps the given URN to a {@link DataTypeIec61360} schema type.
    *
    * @param characteristic the URN to map
    * @return the {@code DataTypeIec61360} for the given URN
    */
   public static DataTypeIec61360 mapIEC61360DataType( final Optional<Characteristic> characteristic ) {
      return mapIEC61360DataType( characteristic.flatMap( Characteristic::getDataType ).map( Type::getUrn ).orElse( RDF.langString.getURI() ) );
   }

   /**
    * Maps the given URN to a {@link DataTypeIec61360} schema type.
    *
    * @param characteristic the URN to map
    * @return the {@code DataTypeIec61360} for the given URN
    */
   public static DataTypeIec61360 mapIEC61360DataType( final Characteristic characteristic ) {
      return mapIEC61360DataType( Optional.of( characteristic ) );
   }
}
