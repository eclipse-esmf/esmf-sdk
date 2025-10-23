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

import { AspectWithSet, } from './AspectWithSet';
import { LangString, } from './core/langString';
import { StaticContainerProperty, } from './core/staticConstraintProperty';
import { string, } from 'asdas';


/*
* Generated class MetaAspectWithSet (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSet).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSet implements StaticMetaClass<AspectWithSet>, PropertyContainer<AspectWithSet> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSet';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithSet();


    public static readonly TEST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithSet, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithSet';
            }

            getContainedType(): AspectWithSet {
                return 'AspectWithSet';
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
                characteristic: new DefaultSet({
                    urn: this.NAMESPACE + 'TestSet',
                    preferredNames: [{
                        value: 'Test Set',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test set.',
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


    getModelClass(): string {
        return 'AspectWithSet';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithSet.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithSet';
    }

    getProperties(): Array<StaticProperty<AspectWithSet, any>> {
        return [MetaAspectWithSet.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithSet, any>> {
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


