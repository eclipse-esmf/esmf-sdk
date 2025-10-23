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

import { AspectWithCharacteristicWithSeeAttribute, } from './AspectWithCharacteristicWithSeeAttribute';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithCharacteristicWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCharacteristicWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCharacteristicWithSeeAttribute implements StaticMetaClass<AspectWithCharacteristicWithSeeAttribute>, PropertyContainer<AspectWithCharacteristicWithSeeAttribute> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCharacteristicWithSeeAttribute';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithCharacteristicWithSeeAttribute();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithCharacteristicWithSeeAttribute, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithCharacteristicWithSeeAttribute';
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
                        urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                        preferredNames: [{
                            value: 'Test Characteristic',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Test Characteristic',
                            languageTag: 'en',
                        },
                        ],
                        see: ['http://example.com/',
                        ],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    public static readonly TEST_PROPERTY_TWO =

        new (class extends DefaultStaticProperty<AspectWithCharacteristicWithSeeAttribute, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithCharacteristicWithSeeAttribute';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testPropertyTwo',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'ReplacedAspectArtifactTwo',
                        preferredNames: [{
                            value: 'Test Characteristic Two',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Test Characteristic Two',
                            languageTag: 'en',
                        },
                        ],
                        see: ['http://example.com/',
                            'http://example.com/me',
                        ],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testPropertyTwo',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithCharacteristicWithSeeAttribute';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithCharacteristicWithSeeAttribute.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithCharacteristicWithSeeAttribute';
    }

    getProperties(): Array<StaticProperty<AspectWithCharacteristicWithSeeAttribute, any>> {
        return [MetaAspectWithCharacteristicWithSeeAttribute.TEST_PROPERTY, MetaAspectWithCharacteristicWithSeeAttribute.TEST_PROPERTY_TWO];
    }

    getAllProperties(): Array<StaticProperty<AspectWithCharacteristicWithSeeAttribute, any>> {
        return this.getProperties();
    }


}


