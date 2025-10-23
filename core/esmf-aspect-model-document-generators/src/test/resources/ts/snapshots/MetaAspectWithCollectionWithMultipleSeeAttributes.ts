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

import { AspectWithCollectionWithMultipleSeeAttributes, } from './AspectWithCollectionWithMultipleSeeAttributes';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithCollectionWithMultipleSeeAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionWithMultipleSeeAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionWithMultipleSeeAttributes implements StaticMetaClass<AspectWithCollectionWithMultipleSeeAttributes>, PropertyContainer<AspectWithCollectionWithMultipleSeeAttributes> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionWithMultipleSeeAttributes';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithCollectionWithMultipleSeeAttributes();


    public static readonly TEST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithCollectionWithMultipleSeeAttributes, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithCollectionWithMultipleSeeAttributes';
            }

            getContainedType(): AspectWithCollectionWithMultipleSeeAttributes {
                return 'AspectWithCollectionWithMultipleSeeAttributes';
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
                    preferredNames: [{
                        value: 'Test Collection',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'Test Collection',
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
        return 'AspectWithCollectionWithMultipleSeeAttributes';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithCollectionWithMultipleSeeAttributes.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithCollectionWithMultipleSeeAttributes';
    }

    getProperties(): Array<StaticProperty<AspectWithCollectionWithMultipleSeeAttributes, any>> {
        return [MetaAspectWithCollectionWithMultipleSeeAttributes.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithCollectionWithMultipleSeeAttributes, any>> {
        return this.getProperties();
    }


}


