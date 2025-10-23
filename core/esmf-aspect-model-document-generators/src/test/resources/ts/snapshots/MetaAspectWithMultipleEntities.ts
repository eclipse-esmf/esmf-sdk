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


import { AspectWithMultipleEntities, } from './AspectWithMultipleEntities';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';


/*
* Generated class MetaAspectWithMultipleEntities (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntities).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultipleEntities implements StaticMetaClass<AspectWithMultipleEntities>, PropertyContainer<AspectWithMultipleEntities> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEntities';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithMultipleEntities();


    public static readonly TEST_ENTITY_ONE =

        new (class extends DefaultStaticProperty<AspectWithMultipleEntities, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithMultipleEntities';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testEntityOne',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testEntityOne',
                isAbstract: false,
            });


    public static readonly TEST_ENTITY_TWO =

        new (class extends DefaultStaticProperty<AspectWithMultipleEntities, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithMultipleEntities';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testEntityTwo',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testEntityTwo',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithMultipleEntities';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithMultipleEntities.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithMultipleEntities';
    }

    getProperties(): Array<StaticProperty<AspectWithMultipleEntities, any>> {
        return [MetaAspectWithMultipleEntities.TEST_ENTITY_ONE, MetaAspectWithMultipleEntities.TEST_ENTITY_TWO];
    }

    getAllProperties(): Array<StaticProperty<AspectWithMultipleEntities, any>> {
        return this.getProperties();
    }


}


