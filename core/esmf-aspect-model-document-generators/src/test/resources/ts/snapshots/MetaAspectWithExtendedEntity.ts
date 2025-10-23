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


import { AspectWithExtendedEntity, } from './AspectWithExtendedEntity';
import { MetaParentOfParentEntity, } from './MetaParentOfParentEntity';
import { MetaParentReplacedAspectArtifact, } from './MetaParentReplacedAspectArtifact';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithExtendedEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithExtendedEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithExtendedEntity implements StaticMetaClass<AspectWithExtendedEntity>, PropertyContainer<AspectWithExtendedEntity> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithExtendedEntity';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithExtendedEntity();


    public static readonly TEST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithExtendedEntity, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithExtendedEntity';
            }

            getContainedType(): AspectWithExtendedEntity {
                return 'AspectWithExtendedEntity';
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
                characteristic: new DefaultSortedSet({
                    isAnonymous: true,
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, Optional.of(DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity({
                    urn: this.NAMESPACE + 'ParentReplacedAspectArtifact',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, MetaParentReplacedAspectArtifact.INSTANCE.getProperties(), Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity({
                    urn: this.NAMESPACE + 'ParentOfParentEntity',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, MetaParentOfParentEntity.INSTANCE.getProperties(), Optional.empty(), List.of(AspectModelUrn.fromUrn('urn:samm:org.eclipse.esmf.test:1.0.0#ParentReplacedAspectArtifact')))), List.of(AspectModelUrn.fromUrn('urn:samm:org.eclipse.esmf.test:1.0.0#ReplacedAspectArtifact')))))), Optional.empty())
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithExtendedEntity';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithExtendedEntity.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithExtendedEntity';
    }

    getProperties(): Array<StaticProperty<AspectWithExtendedEntity, any>> {
        return [MetaAspectWithExtendedEntity.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithExtendedEntity, any>> {
        return this.getProperties();
    }


}


