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


import { AspectWithNestedEntityList, } from './AspectWithNestedEntityList';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithNestedEntityList (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntityList).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithNestedEntityList implements StaticMetaClass<AspectWithNestedEntityList>, PropertyContainer<AspectWithNestedEntityList> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNestedEntityList';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithNestedEntityList();


    public static readonly TEST_LIST =

        new (class extends StaticContainerProperty<AspectWithNestedEntityList, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithNestedEntityList';
            }

            getContainedType(): AspectWithNestedEntityList {
                return 'AspectWithNestedEntityList';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testList',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultList({
                    isAnonymous: true,
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, Optional.of(DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.empty())), Optional.empty())
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testList',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithNestedEntityList';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithNestedEntityList.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithNestedEntityList';
    }

    getProperties(): Array<StaticProperty<AspectWithNestedEntityList, any>> {
        return [MetaAspectWithNestedEntityList.TEST_LIST];
    }

    getAllProperties(): Array<StaticProperty<AspectWithNestedEntityList, any>> {
        return this.getProperties();
    }


}


