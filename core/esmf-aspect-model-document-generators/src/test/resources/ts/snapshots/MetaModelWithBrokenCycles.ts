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


import { DefaultStaticProperty, } from './core/staticConstraintProperty';

import { Either, } from './core/Either';

import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';
import { ModelWithBrokenCycles, } from './ModelWithBrokenCycles';


/*
* Generated class MetaModelWithBrokenCycles (urn:samm:org.eclipse.esmf.test:1.0.0#ModelWithBrokenCycles).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaModelWithBrokenCycles implements StaticMetaClass<ModelWithBrokenCycles>, PropertyContainer<ModelWithBrokenCycles> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'ModelWithBrokenCycles';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaModelWithBrokenCycles();


    public static readonly A =

        new (class extends DefaultStaticProperty<ModelWithBrokenCycles, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'ModelWithBrokenCycles';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'a',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'aCharacteristic',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'a',
                isAbstract: false,
            });


    public static readonly E =

        new (class extends DefaultStaticProperty<ModelWithBrokenCycles, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'ModelWithBrokenCycles';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'e',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'eCharacteristic',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'e',
                isAbstract: false,
            });


    public static readonly H =

        new (class extends DefaultStaticProperty<ModelWithBrokenCycles, Either<ReplacedAspectArtifact, ReplacedAspectArtifact>> {


            getPropertyType(): string {
                return 'Either';
            }

            getContainingType(): string {
                return 'ModelWithBrokenCycles';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'h',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'hCharacteristic',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'h',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'ModelWithBrokenCycles';
    }

    getAspectModelUrn(): string {
        return MetaModelWithBrokenCycles.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'ModelWithBrokenCycles';
    }

    getProperties(): Array<StaticProperty<ModelWithBrokenCycles, any>> {
        return [MetaModelWithBrokenCycles.A, MetaModelWithBrokenCycles.E, MetaModelWithBrokenCycles.H];
    }

    getAllProperties(): Array<StaticProperty<ModelWithBrokenCycles, any>> {
        return this.getProperties();
    }


}


