package org.eclipse.esmf.aspectmodel.aspect;

import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.entity.*;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.samm.KnownVersion;

import java.util.*;
import java.util.stream.Collectors;

public class AASModelAspectVisitor {

    public List<AspectEntity> visitAas(Environment environment) {
        return environment.getSubmodels()
                .stream()
                .filter(submodel -> submodel.getKind().equals(ModellingKind.TEMPLATE))
                .map(this::toAspect)
                .toList();
    }

    private AspectEntity toAspect( Submodel submodel) {
        List<OperationEntity> operationEntities = new ArrayList<>();

        AspectEntity newAspect;

        //Build MetaModelBaseAttributes for AspectEntity
        AspectModelUrn urn = AspectModelUrn.fromUrn(submodel.getSemanticID().getKeys().get(0).getValue());

        MetaModelBaseAttributes aspectMetaModelBaseAttributes = new MetaModelBaseAttributes(
                KnownVersion.getLatest(),
                urn,
                submodel.getIdShort(),
                toLangString(submodel.getDisplayName()),
                toLangString(submodel.getDescription()),
                List.of(""),
                false
        );

        newAspect = new AspectEntity(
            aspectMetaModelBaseAttributes,
                submodel.getSubmodelElements().stream().map(element -> createProperty(element, urn)).toList(),
                operationEntities,
            new ArrayList<>(),
            true
        );
        return newAspect;
    }

    private PropertyEntity createProperty(SubmodelElement element, AspectModelUrn urn) {
        Optional<AspectModelUrn> propertyUrn = Optional.of(urn.withName(element.getIdShort().split("id_")[1]));

        MetaModelBaseAttributes proprtyMetaModelBaseAttributes = new MetaModelBaseAttributes(
                KnownVersion.getLatest(),
                propertyUrn.orElse(null),
                element.getIdShort(),
                toLangString(element.getDisplayName()),
                toLangString(element.getDescription()),
                List.of(""),
                false
        );

//        ScalarValueEntity propertyExampleValue = new ScalarValueEntity(
//        new Object(),
//        new DefaultScalar(element.getSemanticID().getKeys().get(0).getValue(), KnownVersion.getLatest())
//        );

        return new PropertyEntity(
                proprtyMetaModelBaseAttributes,
                createCharacteristic(element, urn),
                null,
                false,
                false,
                "payload",
                false,
                null
        );
    }

    private static Set<LangString> toLangString(List<? extends AbstractLangString> stringList) {
        return stringList.stream()
                .map(d -> new LangString(d.getText(), Locale.forLanguageTag(d.getLanguage()))).collect(Collectors.toSet());
    }

    private CharacteristicEntity createCharacteristic (SubmodelElement element, AspectModelUrn urn) {
        CharacteristicEntity characteristic = null;

        AspectModelUrn characteristicUrn = urn.withName(element.getIdShort().split("id_")[1]);

        MetaModelBaseAttributes characteristicMetaModelBaseAttributes = new MetaModelBaseAttributes(
                KnownVersion.getLatest(),
                characteristicUrn,
                element.getIdShort(),
                toLangString(element.getDisplayName()),
                toLangString(element.getDescription()),
                List.of(""),
                false
        );

        if (element instanceof SubmodelElementList convertedElement) {
            characteristic = new CharacteristicCollectionEntity(
                    characteristicMetaModelBaseAttributes,
                    null,
                    convertedElement.getValue()
                                    .stream()
                                    .map(el -> getComplexOrScalar(element, characteristicUrn, characteristicMetaModelBaseAttributes)).collect(Collectors.toList())
            );
        } else {
            characteristic = getComplexOrScalar(element, characteristicUrn, characteristicMetaModelBaseAttributes);
        }

        return characteristic;
    }

    private CharacteristicEntity getComplexOrScalar(
            SubmodelElement element,
            AspectModelUrn characteristicUrn,
            MetaModelBaseAttributes characteristicMetaModelBaseAttributes) {

        CharacteristicEntity characteristic = null;

        if (element instanceof SubmodelElementCollection) {
            characteristic = new CharacteristicEntity(
                    characteristicMetaModelBaseAttributes,
                    createEntityEntity(element, characteristicUrn)
            );
        }

        if (element instanceof Property) {
            characteristic = new CharacteristicEntity(
                    characteristicMetaModelBaseAttributes,
                    new ScalarEntity(characteristicUrn.toString(), KnownVersion.getLatest())
            );
        }

        return characteristic;
    }

    private EntityEntity createEntityEntity(SubmodelElement element, AspectModelUrn urn) {
        EntityEntity entityEntity = null;

        MetaModelBaseAttributes entityEntityMetaModelBaseAttributes = new MetaModelBaseAttributes(
                KnownVersion.getLatest(),
                urn,
                element.getIdShort(),
                toLangString(element.getDisplayName()),
                toLangString(element.getDescription()),
                List.of(""),
                false
        );

        entityEntity = new EntityEntity(
                entityEntityMetaModelBaseAttributes,
                ((SubmodelElementCollection) element).getValue().stream().map(p -> createProperty(p, urn)).toList()
        );
        return entityEntity;
    }
}
