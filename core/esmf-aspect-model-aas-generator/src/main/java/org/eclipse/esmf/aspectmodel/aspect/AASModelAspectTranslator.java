package org.eclipse.esmf.aspectmodel.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.AbstractLangString;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.ModellingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.characteristic.impl.DefaultCollection;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.impl.DefaultAspect;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.metamodel.impl.DefaultEntity;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.loader.AspectLoadingException;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.samm.KnownVersion;

public class AASModelAspectTranslator {

   private Map<AspectModelUrn, Object> cacheMap = new HashMap<>();

   public List<Aspect> visitAas( Environment environment ) {
      return environment.getSubmodels()
            .stream()
            .filter( submodel -> submodel.getKind().equals( ModellingKind.TEMPLATE ) )
            .map( this::toAspect )
            .toList();
   }

   public Aspect visitAasSingleSubmodel( Environment environment, String submodelName ) {
      Submodel submodel = environment.getSubmodels()
            .stream()
            .filter( sm -> sm.getKind().equals( ModellingKind.TEMPLATE ) && sm.getIdShort().equals( submodelName ) )
            .findFirst()
            .orElseThrow(
                  () -> new IllegalArgumentException( "Invalid submodel name: " + submodelName + " not found in the AASX model." )
            );
      return toAspect( submodel );
   }

   private Aspect toAspect( Submodel submodel ) {
      List<Operation> operationEntities = new ArrayList<>();

      Aspect newAspect;

      //Build MetaModelBaseAttributes for AspectEntity
      AspectModelUrn urn = AspectModelUrn.fromUrn( submodel.getSemanticId().getKeys().get( 0 ).getValue() );

      MetaModelBaseAttributes aspectMetaModelBaseAttributes = new MetaModelBaseAttributes(
            KnownVersion.getLatest(),
            urn,
            submodel.getIdShort(),
            toLangString( submodel.getDisplayName() ),
            toLangString( submodel.getDescription() ),
            List.of( "" ),
            false
      );

      newAspect = new DefaultAspect(
            aspectMetaModelBaseAttributes,
            submodel.getSubmodelElements().stream().map( element -> createProperty( element, urn ) ).toList(),
            operationEntities,
            new ArrayList<>(),
            true
      );
      return newAspect;
   }

   private Property createProperty( SubmodelElement element, AspectModelUrn urn ) {
      AspectModelUrn propertyUrn = urn.withName( element.getIdShort().split( "id_" )[1] );

      if ( !cacheMap.containsKey( propertyUrn ) ) {
         MetaModelBaseAttributes proprtyMetaModelBaseAttributes = new MetaModelBaseAttributes(
               KnownVersion.getLatest(),
               Optional.of( propertyUrn ).orElse( null ),
               element.getIdShort(),
               toLangString( element.getDisplayName() ),
               toLangString( element.getDescription() ),
               List.of( "" ),
               false
         );

         return new DefaultProperty(
               proprtyMetaModelBaseAttributes,
               Optional.of( createCharacteristic( element, propertyUrn ) ),
               Optional.empty(),
               false,
               false,
               Optional.of( "payload" ),
               false,
               Optional.empty()
         );
      }

      return (Property) cacheMap.get( propertyUrn );
   }

   private static Set<LangString> toLangString( List<? extends AbstractLangString> stringList ) {
      return stringList.stream()
            .map( d -> new LangString( d.getText(), Locale.forLanguageTag( d.getLanguage() ) ) ).collect( Collectors.toSet() );
   }

   private Characteristic createCharacteristic( SubmodelElement element, AspectModelUrn urn ) {
      Characteristic characteristic = null;

      AspectModelUrn characteristicUrn = urn.withName( element.getIdShort().split( "id_" )[1] );

      MetaModelBaseAttributes characteristicMetaModelBaseAttributes = new MetaModelBaseAttributes(
            KnownVersion.getLatest(),
            null,
            element.getIdShort(),
            toLangString( element.getDisplayName() ),
            toLangString( element.getDescription() ),
            List.of( "" ),
            false
      );

      if ( element instanceof SubmodelElementList ) {
         characteristic = new DefaultCollection(
               characteristicMetaModelBaseAttributes,
               Optional.ofNullable( getDataType( element, characteristicUrn ) ),
               Optional.empty()
         );
      } else {
         characteristic = new DefaultCharacteristic(
               characteristicMetaModelBaseAttributes,
               Optional.ofNullable( getDataType( element, characteristicUrn ) )
         );
      }

      return characteristic;
   }

   private Type getDataType(
         SubmodelElement element,
         AspectModelUrn characteristicUrn ) {

      Type datatype = null;

      if ( element instanceof SubmodelElementList convertedElement ) {
         datatype = createEntity( convertedElement.getValue().get( 0 ), characteristicUrn );
      }

      if ( element instanceof SubmodelElementCollection ) {
         datatype = createEntity( element, characteristicUrn );
      }

      if ( element instanceof Property ) {
         datatype = new DefaultScalar( characteristicUrn.toString(), KnownVersion.getLatest() );
      }

      return datatype;
   }

   private Entity createEntity( SubmodelElement element, AspectModelUrn urn ) {
      Entity entityEntity = null;

      MetaModelBaseAttributes entityEntityMetaModelBaseAttributes = new MetaModelBaseAttributes(
            KnownVersion.getLatest(),
            urn,
            element.getIdShort(),
            toLangString( element.getDisplayName() ),
            toLangString( element.getDescription() ),
            List.of( "" ),
            false
      );

      entityEntity = new DefaultEntity(
            entityEntityMetaModelBaseAttributes,
            ((SubmodelElementCollection) element).getValue().stream().map( p -> createProperty( p, urn ) ).toList()
      );
      return entityEntity;
   }
}
