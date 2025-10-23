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


import { AspectWithEntityWithoutProperty, } from './AspectWithEntityWithoutProperty';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';
import { MetaAbstractEntity, } from './MetaAbstractEntity';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';


/*
* Generated class MetaAspectWithEntityWithoutProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityWithoutProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityWithoutProperty implements StaticMetaClass<AspectWithEntityWithoutProperty>, PropertyContainer<AspectWithEntityWithoutProperty> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityWithoutProperty';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEntityWithoutProperty();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithEntityWithoutProperty, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithEntityWithoutProperty';
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
                    urn: this.NAMESPACE + 'EntityCharacteristic',
                    preferredNames: [{
                        value: 'Test Entity Characteristic',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test Entity Characteristic',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                    ],
                }, DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [{
                        value: 'Test Entity',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test entity',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity({
                    urn: this.NAMESPACE + 'AbstractEntity',
                    preferredNames: [{
                        value: 'Abstract test Entity',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is an abstract test entity',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, MetaAbstractEntity.INSTANCE.getProperties(), Optional.empty(), List.of(AspectModelUrn.fromUrn('urn:samm:org.eclipse.esmf.test:1.0.0#ReplacedAspectArtifact'))))))
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithEntityWithoutProperty';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithEntityWithoutProperty.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithEntityWithoutProperty';
    }

    getProperties(): Array<StaticProperty<AspectWithEntityWithoutProperty, any>> {
        return [MetaAspectWithEntityWithoutProperty.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithEntityWithoutProperty, any>> {
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


