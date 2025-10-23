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

import { ReplacedAspectArtifact, } from './ReplacedAspectArtifact';


import { AspectWithQuantity, } from './AspectWithQuantity';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';
import { MetaQuantity, } from './MetaQuantity';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';


/*
* Generated class MetaAspectWithQuantity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithQuantity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithQuantity implements StaticMetaClass<AspectWithQuantity>, PropertyContainer<AspectWithQuantity> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithQuantity';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithQuantity();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithQuantity, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithQuantity';
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
                characteristic: new DefaultSingleEntity({
                    isAnonymous: true,
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [{
                        value: 'Quantity',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'A numeric value and the physical unit of the value.',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity({
                    urn: 'urn:samm:org.eclipse.esmf.samm:entity:2.2.0#Quantity',
                    preferredNames: [{
                        value: 'Quantity',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'A numeric value and the physical unit of the value.',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, MetaQuantity.INSTANCE.getProperties(), Optional.empty(), List.of(AspectModelUrn.fromUrn('urn:samm:org.eclipse.esmf.test:1.0.0#ReplacedAspectArtifact'))))))
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithQuantity';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithQuantity.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithQuantity';
    }

    getProperties(): Array<StaticProperty<AspectWithQuantity, any>> {
        return [MetaAspectWithQuantity.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithQuantity, any>> {
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


