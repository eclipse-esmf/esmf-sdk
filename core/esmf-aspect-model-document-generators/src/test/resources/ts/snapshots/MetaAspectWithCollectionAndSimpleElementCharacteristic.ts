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

import { AspectWithCollectionAndSimpleElementCharacteristic, } from './AspectWithCollectionAndSimpleElementCharacteristic';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithCollectionAndSimpleElementCharacteristic (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionAndSimpleElementCharacteristic).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionAndSimpleElementCharacteristic implements StaticMetaClass<AspectWithCollectionAndSimpleElementCharacteristic>, PropertyContainer<AspectWithCollectionAndSimpleElementCharacteristic> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionAndSimpleElementCharacteristic';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithCollectionAndSimpleElementCharacteristic();


    public static readonly ITEMS =

        new (class extends StaticContainerProperty<AspectWithCollectionAndSimpleElementCharacteristic, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithCollectionAndSimpleElementCharacteristic';
            }

            getContainedType(): AspectWithCollectionAndSimpleElementCharacteristic {
                return 'AspectWithCollectionAndSimpleElementCharacteristic';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'items',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultCollection({
                    isAnonymous: true,
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
                payloadName: 'items',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithCollectionAndSimpleElementCharacteristic';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithCollectionAndSimpleElementCharacteristic.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithCollectionAndSimpleElementCharacteristic';
    }

    getProperties(): Array<StaticProperty<AspectWithCollectionAndSimpleElementCharacteristic, any>> {
        return [MetaAspectWithCollectionAndSimpleElementCharacteristic.ITEMS];
    }

    getAllProperties(): Array<StaticProperty<AspectWithCollectionAndSimpleElementCharacteristic, any>> {
        return this.getProperties();
    }


}


