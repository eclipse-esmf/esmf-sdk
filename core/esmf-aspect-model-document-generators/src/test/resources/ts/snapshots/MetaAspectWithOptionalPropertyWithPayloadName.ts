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

import { AspectWithOptionalPropertyWithPayloadName, } from './AspectWithOptionalPropertyWithPayloadName';
import { LangString, } from './core/langString';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithOptionalPropertyWithPayloadName (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertyWithPayloadName).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithOptionalPropertyWithPayloadName implements StaticMetaClass<AspectWithOptionalPropertyWithPayloadName>, PropertyContainer<AspectWithOptionalPropertyWithPayloadName> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalPropertyWithPayloadName';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithOptionalPropertyWithPayloadName();


    public static readonly TEST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithOptionalPropertyWithPayloadName, string, string> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithOptionalPropertyWithPayloadName';
            }

            getContainedType(): AspectWithOptionalPropertyWithPayloadName {
                return 'AspectWithOptionalPropertyWithPayloadName';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
                    preferredNames: [{
                        value: 'Test Property',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test property.',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                        'http://example.com/me',
                    ],
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
                payloadName: 'test',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithOptionalPropertyWithPayloadName';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithOptionalPropertyWithPayloadName.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithOptionalPropertyWithPayloadName';
    }

    getProperties(): Array<StaticProperty<AspectWithOptionalPropertyWithPayloadName, any>> {
        return [MetaAspectWithOptionalPropertyWithPayloadName.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithOptionalPropertyWithPayloadName, any>> {
        return this.getProperties();
    }


    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Test Aspect', languageTag: 'en'},
        ];
    }


    getDescriptions(): Array<LangString> {
        return [
            {value: 'This is a test description', languageTag: 'en'},
        ];
    }

    getSee(): Array<String> {
        return [
            'http://example.com/',
        ];
    }

}


