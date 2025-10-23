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

import { AspectWithUsedAndUnusedCollection, } from './AspectWithUsedAndUnusedCollection';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithUsedAndUnusedCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUsedAndUnusedCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithUsedAndUnusedCollection implements StaticMetaClass<AspectWithUsedAndUnusedCollection>, PropertyContainer<AspectWithUsedAndUnusedCollection> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUsedAndUnusedCollection';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithUsedAndUnusedCollection();


    public static readonly TEST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithUsedAndUnusedCollection, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithUsedAndUnusedCollection';
            }

            getContainedType(): AspectWithUsedAndUnusedCollection {
                return 'AspectWithUsedAndUnusedCollection';
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
                    urn: this.NAMESPACE + 'UsedTestCollection',
                    preferredNames: [{
                        value: 'Used Test Collection',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'Used Test Collection',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                        'http://example.com/me',
                    ],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#string')), Optional.empty())
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithUsedAndUnusedCollection';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithUsedAndUnusedCollection.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithUsedAndUnusedCollection';
    }

    getProperties(): Array<StaticProperty<AspectWithUsedAndUnusedCollection, any>> {
        return [MetaAspectWithUsedAndUnusedCollection.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithUsedAndUnusedCollection, any>> {
        return this.getProperties();
    }


}


