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

import { AspectWithEnglishDescription, } from './AspectWithEnglishDescription';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';
import { string, } from 'asdas';


/*
* Generated class MetaAspectWithEnglishDescription (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnglishDescription).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnglishDescription implements StaticMetaClass<AspectWithEnglishDescription>, PropertyContainer<AspectWithEnglishDescription> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnglishDescription';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEnglishDescription();


    public static readonly TEST_STRING =

        new (class extends DefaultStaticProperty<AspectWithEnglishDescription, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithEnglishDescription';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testString',
                    preferredNames: [{
                        value: 'testString',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'testString',
                        languageTag: 'en',
                    },
                    ],
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
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: 'Example Value Test',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testString',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithEnglishDescription';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithEnglishDescription.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithEnglishDescription';
    }

    getProperties(): Array<StaticProperty<AspectWithEnglishDescription, any>> {
        return [MetaAspectWithEnglishDescription.TEST_STRING];
    }

    getAllProperties(): Array<StaticProperty<AspectWithEnglishDescription, any>> {
        return this.getProperties();
    }


    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Test Aspect', languageTag: 'en'},
        ];
    }


    getDescriptions(): Array<LangString> {
        return [
            {value: 'Test Aspect', languageTag: 'en'},
        ];
    }


}


