package org.eclipse.esmf.aspectmodel.aspect;

import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.ModellingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.entity.*;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.loader.AttributeValueRetriever;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.samm.KnownVersion;

import java.util.*;
import java.util.stream.Collectors;

public class AASModelAspectVisitor {

    public AspectEntity visitAAS (Environment environment) {

        AspectEntity newAspect = null;
        List<PropertyEntity> propertyEntities = new ArrayList<>();
        List<OperationEntity> operationEntities = new ArrayList<>();

        List<Submodel> submodels = environment.getSubmodels()
                .stream().filter(submodel -> submodel.getKind().equals(ModellingKind.TEMPLATE)).toList();

        for (Submodel submodel : submodels) {

            //Build MetaModelBaseAttributes for AspectEntity
            String aspectMetaModelName = submodel.getIdShort();
            Set<LangString> metaModelAspectPreferredName = submodel.getDisplayName().stream()
                    .map(n -> new LangString(n.getText(), Locale.forLanguageTag(n.getLanguage()))).collect(Collectors.toSet());
            Set<LangString> metaModelAspectDescription = submodel.getDescription().stream()
                    .map(d -> new LangString(d.getText(), Locale.forLanguageTag(d.getLanguage()))).collect(Collectors.toSet());
            AspectModelUrn aspectModelUrn = AspectModelUrn.fromUrn(submodel.getSemanticID().getKeys().get(0).getValue());

            MetaModelBaseAttributes aspectMetaModelBaseAttributes = new MetaModelBaseAttributes(
                    KnownVersion.getLatest(),
                    aspectModelUrn,
                    aspectMetaModelName,
                    metaModelAspectPreferredName,
                    metaModelAspectDescription,
                    List.of(""),
                    false
            );

            // Visit subModel for getting properties
            for ( SubmodelElement element : submodel.getSubmodelElements()) {

                // Build Properties for AspectEntity
                if (element instanceof SubmodelElementList convertedElement) {

                    String metaModelPropertyName = element.getIdShort();
                    Set<LangString> metaModelPropertyPreferredName = element.getDisplayName().stream()
                            .map(n -> new LangString(n.getText(), Locale.forLanguageTag(n.getLanguage()))).collect(Collectors.toSet());
                    Set<LangString> metaModelPropertyDescription = element.getDescription().stream()
                            .map(d -> new LangString(d.getText(), Locale.forLanguageTag(d.getLanguage()))).collect(Collectors.toSet());
                    Optional<AspectModelUrn> propertyUrn = Optional.of(AspectModelUrn.fromUrn("urn:samm:test:0.0.1#" + element.getIdShort().split("id_")[1]));

                    MetaModelBaseAttributes proprtyMetaModelBaseAttributes = new MetaModelBaseAttributes(
                            KnownVersion.getLatest(),
                            propertyUrn.orElse(null),
//                            null,
                            metaModelPropertyName,
                            metaModelPropertyPreferredName,
                            metaModelPropertyDescription,
                            List.of(""),
                            false
                    );

                    // Build PropertyEntity
//                    ScalarValueEntity propertyExampleValue = new ScalarValueEntity(
//                            new Object(),
//                            new DefaultScalar(element.getSemanticID().getKeys().get(0).getValue(), KnownVersion.getLatest())
//                    );
//                    CharacteristicEntity characteristic = new CharacteristicEntity(
//                      proprtyMetaModelBaseAttributes,
//                            element
//                    );

                    PropertyEntity aspectProperty = new PropertyEntity(
                            proprtyMetaModelBaseAttributes,
                            Optional.empty(),
                            Optional.empty(),
//                            Optional.of(propertyExampleValue),
                            Optional.empty(),
                            false,
                            false,
                            Optional.of("payload"),
                            false,
                            Optional.empty()
                    );

                    propertyEntities.add(aspectProperty);

                    if (!convertedElement.getValue().isEmpty()) {
                        for (SubmodelElement submodelElementCollection : convertedElement.getValue()) {

                            for (SubmodelElement submodelElement : ( (SubmodelElementCollection) submodelElementCollection).getValue()) {

                                PropertyEntity currentProperty = visitProperty(submodelElement, null);

                                propertyEntities.add(currentProperty);
                            }
                        }
                    }
                }
            }

            newAspect = new AspectEntity(
                aspectMetaModelBaseAttributes,
                propertyEntities,
                operationEntities,
                new ArrayList<>(),
                true
            );
        }

        return newAspect;
    }

    private PropertyEntity visitProperty (SubmodelElement property, PropertyEntity extends_) {
        Property currentAasProperty = ((Property) property);

        //Build metaModelBaseAttributes
        String metaModelPropertyName = property.getIdShort();
        Set<LangString> metaModelPropertyPreferredName = property.getDisplayName().stream()
                .map(n -> new LangString(n.getText(), Locale.forLanguageTag(n.getLanguage()))).collect(Collectors.toSet());
        Set<LangString> metaModelPropertyDescription = property.getDescription().stream()
                .map(d -> new LangString(d.getText(), Locale.forLanguageTag(d.getLanguage()))).collect(Collectors.toSet());
        Optional<AspectModelUrn> propertyUrn = Optional.of(AspectModelUrn.fromUrn(currentAasProperty.getSemanticID().getKeys().get(0).getValue()));

        MetaModelBaseAttributes proprtyMetaModelBaseAttributes = new MetaModelBaseAttributes(
                KnownVersion.getLatest(),
                propertyUrn.orElse(null),
                metaModelPropertyName,
                metaModelPropertyPreferredName,
                metaModelPropertyDescription,
                List.of(""),
                false
        );

        // Build PropertyEntity
        ScalarValueEntity propertyExampleValue = new ScalarValueEntity(
                new Object(),
                new DefaultScalar(property.getSemanticID().getKeys().get(0).getValue(), KnownVersion.getLatest())
        );

        SAMM samm = new SAMM(KnownVersion.SAMM_2_1_0);
        AttributeValueRetriever attributeValueRetriever = new AttributeValueRetriever(samm);

//        final Optional<org.eclipse.esmf.metamodel.Property> extends_ = attributeValueRetriever.optionalAttributeValue( currentProperty, samm._extends() )
//                .map( Statement::getResource )
//                .map( superElementResource -> modelElementFactory.create( org.eclipse.esmf.metamodel.Property.class, superElementResource ) );
//        final boolean isAbstract = property.getModel().contains( property, RDF.type, samm.AbstractProperty() );

        Optional<CharacteristicEntity> characteristic = visitCharacteristic(currentAasProperty);

        return new PropertyEntity(
                proprtyMetaModelBaseAttributes,
                characteristic,
                Optional.empty(),
                Optional.of(propertyExampleValue),
                false,
                false,
                Optional.of("payload"),
                false,
                Optional.ofNullable(extends_)
        );
    }

    private Optional<CharacteristicEntity> visitCharacteristic (Property aasProperty) {

        String metaModelPropertyName = aasProperty.getIdShort();
        Set<LangString> metaModelPropertyPreferredName = aasProperty.getDisplayName().stream()
                .map(n -> new LangString(n.getText(), Locale.forLanguageTag(n.getLanguage()))).collect(Collectors.toSet());
        Set<LangString> metaModelPropertyDescription = aasProperty.getDescription().stream()
                .map(d -> new LangString(d.getText(), Locale.forLanguageTag(d.getLanguage()))).collect(Collectors.toSet());
        Optional<AspectModelUrn> propertyUrn = Optional.of(AspectModelUrn.fromUrn("urn:samm:test:0.0.1#" + aasProperty.getValueType().toString()));

        MetaModelBaseAttributes proprtyMetaModelBaseAttributes = new MetaModelBaseAttributes(
                KnownVersion.getLatest(),
                propertyUrn.orElse(null),
                metaModelPropertyName,
                metaModelPropertyPreferredName,
                metaModelPropertyDescription,
                List.of(""),
                false
        );

        TypeEntity typeEntity = new TypeEntity(
                KnownVersion.getLatest(),
                propertyUrn
        );

        return Optional.of(new CharacteristicEntity(proprtyMetaModelBaseAttributes, Optional.of(typeEntity)));
    }
}
