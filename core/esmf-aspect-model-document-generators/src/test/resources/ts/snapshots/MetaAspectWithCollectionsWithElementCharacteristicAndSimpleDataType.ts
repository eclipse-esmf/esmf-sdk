/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

import { AspectWithCollectionsWithElementCharacteristicAndSimpleDataType, } from './AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionsWithElementCharacteristicAndSimpleDataType).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType implements StaticMetaClass<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType>, PropertyContainer<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType();


    public static readonly TEST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
            }

            getContainedType(): AspectWithCollectionsWithElementCharacteristicAndSimpleDataType {
                return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultCollection({
                    urn: this.NAMESPACE + 'TestCollection',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#string')), Optional.empty())
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    public static readonly TEST_PROPERTY_TWO =

        new (class extends StaticContainerProperty<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
            }

            getContainedType(): AspectWithCollectionsWithElementCharacteristicAndSimpleDataType {
                return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testPropertyTwo',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultCollection({
                    urn: this.NAMESPACE + 'TestCollectionTwo',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#string')), Optional.of({
                    metaModelBaseAttributes: {
                        urn: this.CHARACTERISTIC_NAMESPACE + '#Text',
                        preferredNames: [{
                            value: 'Text',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.',
                            languageTag: 'en',
                        },
                        ],
                        see: [],
                    },
                }))
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testPropertyTwo',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
    }

    getProperties(): Array<StaticProperty<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType, any>> {
        return [MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType.TEST_PROPERTY, MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType.TEST_PROPERTY_TWO];
    }

    getAllProperties(): Array<StaticProperty<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType, any>> {
        return this.getProperties();
    }


}


