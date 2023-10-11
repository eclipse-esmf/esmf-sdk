package org.eclipse.esmf.aspectmodel.aspect;

import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.entity.AspectEntity;
import org.eclipse.esmf.metamodel.entity.CharacteristicCollectionEntity;
import org.eclipse.esmf.metamodel.entity.CharacteristicEntity;
import org.eclipse.esmf.metamodel.entity.EntityEntity;
import org.eclipse.esmf.metamodel.entity.OperationEntity;
import org.eclipse.esmf.metamodel.entity.PropertyEntity;
import org.eclipse.esmf.metamodel.entity.ScalarEntity;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.samm.KnownVersion;

import java.util.*;
import java.util.stream.Collectors;


public class AASModelAspectTranslator {

    private Map<AspectModelUrn, Object> cacheMap = new HashMap<>();

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
        AspectModelUrn propertyUrn = urn.withName(element.getIdShort().split("id_")[1]);

        if (!cacheMap.containsKey(propertyUrn)) {
            MetaModelBaseAttributes proprtyMetaModelBaseAttributes = new MetaModelBaseAttributes(
                    KnownVersion.getLatest(),
                    Optional.of(propertyUrn).orElse(null),
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
                    createCharacteristic(element, propertyUrn),
                    null,
                    false,
                    false,
                    "payload",
                    false,
                    null
            );
        }

        return (PropertyEntity) cacheMap.get(propertyUrn);
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
                null,
                element.getIdShort(),
                toLangString(element.getDisplayName()),
                toLangString(element.getDescription()),
                List.of(""),
                false
        );

        if (element instanceof SubmodelElementList) {
            characteristic = new CharacteristicCollectionEntity(
                    characteristicMetaModelBaseAttributes,
                    getDataType(element, characteristicUrn),
                    null
            );
        } else {
            characteristic = new  CharacteristicEntity(
                    characteristicMetaModelBaseAttributes,
                    getDataType(element, characteristicUrn)
            );
        }

        return characteristic;
    }

    private Type getDataType(
            SubmodelElement element,
            AspectModelUrn characteristicUrn) {

        Type datatype = null;

        if (element instanceof SubmodelElementList convertedElement) {
            datatype = createEntityEntity(convertedElement.getValue().get(0), characteristicUrn);
        }

        if (element instanceof SubmodelElementCollection) {
            datatype = createEntityEntity(element, characteristicUrn);
        }

        if (element instanceof Property) {
            datatype = new ScalarEntity(characteristicUrn.toString(), KnownVersion.getLatest());
        }

        return datatype;
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
