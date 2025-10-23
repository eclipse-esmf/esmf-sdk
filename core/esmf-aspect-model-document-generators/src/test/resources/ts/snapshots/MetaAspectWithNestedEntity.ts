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

import { AspectWithNestedEntity, } from './AspectWithNestedEntity';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { Entity, } from './Entity';
import { MetaEntity, } from './MetaEntity';


/*
* Generated class MetaAspectWithNestedEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithNestedEntity implements StaticMetaClass<AspectWithNestedEntity>, PropertyContainer<AspectWithNestedEntity> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNestedEntity';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithNestedEntity();


    public static readonly ENTITY =

        new (class extends DefaultStaticProperty<AspectWithNestedEntity, Entity> {


            getPropertyType(): string {
                return 'Entity';
            }

            getContainingType(): string {
                return 'AspectWithNestedEntity';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'entity',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'EntityCharacteristic',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'entity',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithNestedEntity';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithNestedEntity.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithNestedEntity';
    }

    getProperties(): Array<StaticProperty<AspectWithNestedEntity, any>> {
        return [MetaAspectWithNestedEntity.ENTITY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithNestedEntity, any>> {
        return this.getProperties();
    }


}


