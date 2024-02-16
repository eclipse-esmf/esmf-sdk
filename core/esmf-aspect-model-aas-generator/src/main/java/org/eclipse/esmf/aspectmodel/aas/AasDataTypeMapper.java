package org.eclipse.esmf.aspectmodel.aas;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;

public class AasDataTypeMapper {

   private AasDataTypeMapper() {
   }

   /**
    * Maps Aspect types to DataTypeDefXsd Schema types, with no explicit mapping defaulting to string
    */
   static final Map<Resource, DataTypeDefXsd> ASPECT_TYPE_TO_AAS_XSD_TYPE_MAP =
         ImmutableMap.<Resource, DataTypeDefXsd> builder()
               .put( XSD.anyURI, DataTypeDefXsd.ANY_URI )
               .put( XSD.yearMonthDuration, DataTypeDefXsd.DURATION )
               .put( XSD.xboolean, DataTypeDefXsd.BOOLEAN )
               .put( XSD.xbyte, DataTypeDefXsd.BYTE )
               .put( XSD.date, DataTypeDefXsd.DATE )
               .put( XSD.dateTime, DataTypeDefXsd.DATE_TIME )
               .put( XSD.dateTimeStamp, DataTypeDefXsd.DATE_TIME )
               .put( XSD.dayTimeDuration, DataTypeDefXsd.DURATION )
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
               .put( XSD.xstring, DataTypeDefXsd.STRING )
               .put( XSD.time, DataTypeDefXsd.TIME )
               .put( XSD.unsignedByte, DataTypeDefXsd.UNSIGNED_BYTE )
               .put( XSD.unsignedInt, DataTypeDefXsd.UNSIGNED_INT )
               .put( XSD.unsignedLong, DataTypeDefXsd.UNSIGNED_LONG )
               .put( XSD.unsignedShort, DataTypeDefXsd.UNSIGNED_SHORT )
               .build();

   /**
    * Maps DataTypeDefXsd Schema types to Aspect types, with no explicit mapping defaulting to normalizedString.
    */
   static final Map<DataTypeDefXsd, Resource> AAS_XSD_TYPE_TO_ASPECT_TYPE_MAP =
         ImmutableMap.<DataTypeDefXsd, Resource> builder()
               .put( DataTypeDefXsd.ANY_URI, XSD.anyURI )
               .put( DataTypeDefXsd.BOOLEAN, XSD.xboolean )
               .put( DataTypeDefXsd.BYTE, XSD.xbyte )
               .put( DataTypeDefXsd.DATE, XSD.date )
               .put( DataTypeDefXsd.DATE_TIME, XSD.dateTime )
               .put( DataTypeDefXsd.DECIMAL, XSD.decimal )
               .put( DataTypeDefXsd.DOUBLE, XSD.xdouble )
               .put( DataTypeDefXsd.DURATION, XSD.duration )
               .put( DataTypeDefXsd.FLOAT, XSD.xfloat )
               .put( DataTypeDefXsd.GMONTH, XSD.gMonth )
               .put( DataTypeDefXsd.GMONTH_DAY, XSD.gMonthDay )
               .put( DataTypeDefXsd.GYEAR, XSD.gYear )
               .put( DataTypeDefXsd.GYEAR_MONTH, XSD.gYearMonth )
               .put( DataTypeDefXsd.HEX_BINARY, XSD.hexBinary )
               .put( DataTypeDefXsd.INT, XSD.xint )
               .put( DataTypeDefXsd.INTEGER, XSD.integer )
               .put( DataTypeDefXsd.LONG, XSD.xlong )
               .put( DataTypeDefXsd.NEGATIVE_INTEGER, XSD.negativeInteger )
               .put( DataTypeDefXsd.NON_NEGATIVE_INTEGER, XSD.nonNegativeInteger )
               .put( DataTypeDefXsd.POSITIVE_INTEGER, XSD.positiveInteger )
               .put( DataTypeDefXsd.SHORT, XSD.xshort )
               .put( DataTypeDefXsd.STRING, XSD.xstring )
               .put( DataTypeDefXsd.TIME, XSD.time )
               .put( DataTypeDefXsd.UNSIGNED_BYTE, XSD.unsignedByte )
               .put( DataTypeDefXsd.UNSIGNED_INT, XSD.unsignedInt )
               .put( DataTypeDefXsd.UNSIGNED_LONG, XSD.unsignedLong )
               .put( DataTypeDefXsd.UNSIGNED_SHORT, XSD.unsignedShort )
               .build();

   /**
    * Maps the given URN to a {@link DataTypeDefXsd} schema type.
    *
    * @param urn the URN to map
    * @return the {@code DataTypeDefXsd} for the given URN
    */
   public static DataTypeDefXsd mapAspectTypeToAasXsdDataType( final String urn ) {
      final Resource resource = ResourceFactory.createResource( urn );
      return ASPECT_TYPE_TO_AAS_XSD_TYPE_MAP.getOrDefault( resource, DataTypeDefXsd.STRING );
   }

   /**
    * Maps the given URN to a {@link Resource} schema type.
    *
    * @param dataTypeDefXsd the URN to map
    * @return the {@code Resource} for the given URN
    */
   public static Resource mapAasXsdDataTypeToAspectType( final DataTypeDefXsd dataTypeDefXsd ) {
      return AAS_XSD_TYPE_TO_ASPECT_TYPE_MAP.getOrDefault( dataTypeDefXsd, XSD.xstring );
   }
}
