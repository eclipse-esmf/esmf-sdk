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


import { AspectWithRecursivePropertyWithOptional, } from './AspectWithRecursivePropertyWithOptional';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';


/*
* Generated class MetaAspectWithRecursivePropertyWithOptional (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRecursivePropertyWithOptional).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRecursivePropertyWithOptional implements StaticMetaClass<AspectWithRecursivePropertyWithOptional>, PropertyContainer<AspectWithRecursivePropertyWithOptional> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRecursivePropertyWithOptional';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithRecursivePropertyWithOptional();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithRecursivePropertyWithOptional, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithRecursivePropertyWithOptional';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultSingleEntity({
                    urn: this.NAMESPACE + 'TestItemCharacteristic',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.empty()))
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithRecursivePropertyWithOptional';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithRecursivePropertyWithOptional.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithRecursivePropertyWithOptional';
    }

    getProperties(): Array<StaticProperty<AspectWithRecursivePropertyWithOptional, any>> {
        return [MetaAspectWithRecursivePropertyWithOptional.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithRecursivePropertyWithOptional, any>> {
        return this.getProperties();
    }


}


