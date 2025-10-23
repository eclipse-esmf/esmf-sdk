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

import { AspectWithTwoLists, } from './AspectWithTwoLists';
import { LangString, } from './core/langString';
import { StaticContainerProperty, } from './core/staticConstraintProperty';
import { string, } from 'asdas';


/*
* Generated class MetaAspectWithTwoLists (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTwoLists).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithTwoLists implements StaticMetaClass<AspectWithTwoLists>, PropertyContainer<AspectWithTwoLists> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithTwoLists';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithTwoLists();


    public static readonly TEST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithTwoLists, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithTwoLists';
            }

            getContainedType(): AspectWithTwoLists {
                return 'AspectWithTwoLists';
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
                characteristic: new DefaultList({
                    urn: this.NAMESPACE + 'TestList',
                    preferredNames: [{
                        value: 'Test List',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test list.',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                    ],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#string')), Optional.empty())
                ,
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: 'Example Value',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    public static readonly TEST_PROPERTY_TWO =

        new (class extends StaticContainerProperty<AspectWithTwoLists, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithTwoLists';
            }

            getContainedType(): AspectWithTwoLists {
                return 'AspectWithTwoLists';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testPropertyTwo',
                    preferredNames: [{
                        value: 'Test Property Two',
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
                characteristic: new DefaultList({
                    urn: this.NAMESPACE + 'TestList',
                    preferredNames: [{
                        value: 'Test List',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test list.',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                    ],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#string')), Optional.empty())
                ,
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: 'Example Value',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testPropertyTwo',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithTwoLists';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithTwoLists.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithTwoLists';
    }

    getProperties(): Array<StaticProperty<AspectWithTwoLists, any>> {
        return [MetaAspectWithTwoLists.TEST_PROPERTY, MetaAspectWithTwoLists.TEST_PROPERTY_TWO];
    }

    getAllProperties(): Array<StaticProperty<AspectWithTwoLists, any>> {
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


