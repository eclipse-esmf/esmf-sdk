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

import { AspectWithValidAnnotationTest, } from './AspectWithValidAnnotationTest';
import { DefaultStaticProperty, StaticContainerProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';


/*
* Generated class MetaAspectWithValidAnnotationTest (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithValidAnnotationTest).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithValidAnnotationTest implements StaticMetaClass<AspectWithValidAnnotationTest>, PropertyContainer<AspectWithValidAnnotationTest> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithValidAnnotationTest';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithValidAnnotationTest();


    public static readonly ENTITY =

        new (class extends DefaultStaticProperty<AspectWithValidAnnotationTest, TestEntity> {


            getPropertyType(): string {
                return 'TestEntity';
            }

            getContainingType(): string {
                return 'AspectWithValidAnnotationTest';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'entity',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultSingleEntity({
                    urn: this.NAMESPACE + 'EntityCharacteristic',
                    preferredNames: [{
                        value: 'Test Entity Characteristic',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [],
                    see: [],
                }, DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'TestEntity',
                    preferredNames: [{
                        value: 'Test Entity',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [],
                    see: [],
                }, MetaTestEntity.INSTANCE.getProperties(), Optional.empty()))
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'entity',
                isAbstract: false,
            });


    public static readonly COLLECTION_ENTITY =

        new (class extends StaticContainerProperty<AspectWithValidAnnotationTest, TestEntity, TestEntity[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithValidAnnotationTest';
            }

            getContainedType(): AspectWithValidAnnotationTest {
                return 'AspectWithValidAnnotationTest';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'collectionEntity',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultCollection({
                    urn: this.NAMESPACE + 'TestCollection',
                    preferredNames: [{
                        value: 'Test Collection',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [],
                    see: [],
                }, Optional.of(DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'TestEntity',
                    preferredNames: [{
                        value: 'Test Entity',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [],
                    see: [],
                }, MetaTestEntity.INSTANCE.getProperties(), Optional.empty())), Optional.empty())
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'collectionEntity',
                isAbstract: false,
            });


    public static readonly OPTIONAL_ENTITY =

        new (class extends StaticContainerProperty<AspectWithValidAnnotationTest, TestEntity, TestEntity> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithValidAnnotationTest';
            }

            getContainedType(): AspectWithValidAnnotationTest {
                return 'AspectWithValidAnnotationTest';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'optionalEntity',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultSingleEntity({
                    urn: this.NAMESPACE + 'EntityCharacteristic',
                    preferredNames: [{
                        value: 'Test Entity Characteristic',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [],
                    see: [],
                }, DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'TestEntity',
                    preferredNames: [{
                        value: 'Test Entity',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [],
                    see: [],
                }, MetaTestEntity.INSTANCE.getProperties(), Optional.empty()))
                ,
                exampleValue: {},
                optional: true,
                notInPayload: false,
                payloadName: 'optionalEntity',
                isAbstract: false,
            });


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithValidAnnotationTest, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithValidAnnotationTest';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
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
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    public static readonly COLLECTION_TEST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithValidAnnotationTest, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithValidAnnotationTest';
            }

            getContainedType(): AspectWithValidAnnotationTest {
                return 'AspectWithValidAnnotationTest';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'collectionTestProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultCollection({
                    isAnonymous: true,
                    preferredNames: [{
                        value: 'Collection Test Property',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [],
                    see: [],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#string')), Optional.empty())
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'collectionTestProperty',
                isAbstract: false,
            });


    public static readonly OPTIONAL_TEST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithValidAnnotationTest, string, string> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithValidAnnotationTest';
            }

            getContainedType(): AspectWithValidAnnotationTest {
                return 'AspectWithValidAnnotationTest';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'optionalTestProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
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
                }
                ,
                exampleValue: {},
                optional: true,
                notInPayload: false,
                payloadName: 'optionalTestProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithValidAnnotationTest';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithValidAnnotationTest.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithValidAnnotationTest';
    }

    getProperties(): Array<StaticProperty<AspectWithValidAnnotationTest, any>> {
        return [MetaAspectWithValidAnnotationTest.ENTITY, MetaAspectWithValidAnnotationTest.COLLECTION_ENTITY, MetaAspectWithValidAnnotationTest.OPTIONAL_ENTITY, MetaAspectWithValidAnnotationTest.TEST_PROPERTY, MetaAspectWithValidAnnotationTest.COLLECTION_TEST_PROPERTY, MetaAspectWithValidAnnotationTest.OPTIONAL_TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithValidAnnotationTest, any>> {
        return this.getProperties();
    }


    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Aspect with Valid Annotation Test', languageTag: 'en'},
        ];
    }


    getDescriptions(): Array<LangString> {
        return [
            {value: 'This aspect is used to test the @Valid annotation rules.', languageTag: 'en'},
        ];
    }


}


