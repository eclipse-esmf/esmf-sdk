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


import { AspectWithReplacedAspectArtifactCollection, } from './AspectWithReplacedAspectArtifactCollection';

import { LangString, } from './core/langString';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithReplacedAspectArtifactCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithReplacedAspectArtifactCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithReplacedAspectArtifactCollection implements StaticMetaClass<AspectWithReplacedAspectArtifactCollection>, PropertyContainer<AspectWithReplacedAspectArtifactCollection> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithReplacedAspectArtifactCollection';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithReplacedAspectArtifactCollection();


    public static readonly ITEMS =

        new (class extends StaticContainerProperty<AspectWithReplacedAspectArtifactCollection, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithReplacedAspectArtifactCollection';
            }

            getContainedType(): AspectWithReplacedAspectArtifactCollection {
                return 'AspectWithReplacedAspectArtifactCollection';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'items',
                    preferredNames: [{
                        value: 'Items',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'A list of current active errors.',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                },
                characteristic: new DefaultCollection({
                    isAnonymous: true,
                    preferredNames: [{
                        value: 'ReplacedAspectArtifacts',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'A collection of ReplacedAspectArtifact Entities.',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, Optional.of(DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [{
                        value: 'ReplacedAspectArtifact Entity',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'The Entity describing an ReplacedAspectArtifact.',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.empty())), Optional.empty())
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'items',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithReplacedAspectArtifactCollection';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithReplacedAspectArtifactCollection.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithReplacedAspectArtifactCollection';
    }

    getProperties(): Array<StaticProperty<AspectWithReplacedAspectArtifactCollection, any>> {
        return [MetaAspectWithReplacedAspectArtifactCollection.ITEMS];
    }

    getAllProperties(): Array<StaticProperty<AspectWithReplacedAspectArtifactCollection, any>> {
        return this.getProperties();
    }


    getPreferredNames(): Array<LangString> {
        return [
            {value: 'ReplacedAspectArtifacts Aspect', languageTag: 'en'},
        ];
    }


    getDescriptions(): Array<LangString> {
        return [
            {value: 'The ReplacedAspectArtifacts Aspect delivers a list of the currently active errors for a specific machine.', languageTag: 'en'},
        ];
    }


}


