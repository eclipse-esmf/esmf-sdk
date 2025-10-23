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

import { AspectWithCharacteristicWithoutSeeAttribute, } from './AspectWithCharacteristicWithoutSeeAttribute';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithCharacteristicWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCharacteristicWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCharacteristicWithoutSeeAttribute implements StaticMetaClass<AspectWithCharacteristicWithoutSeeAttribute>, PropertyContainer<AspectWithCharacteristicWithoutSeeAttribute> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCharacteristicWithoutSeeAttribute';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithCharacteristicWithoutSeeAttribute();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithCharacteristicWithoutSeeAttribute, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithCharacteristicWithoutSeeAttribute';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                        preferredNames: [{
                            value: 'Test Characteristic',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Test Characteristic',
                            languageTag: 'en',
                        },
                        ],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithCharacteristicWithoutSeeAttribute';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithCharacteristicWithoutSeeAttribute.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithCharacteristicWithoutSeeAttribute';
    }

    getProperties(): Array<StaticProperty<AspectWithCharacteristicWithoutSeeAttribute, any>> {
        return [MetaAspectWithCharacteristicWithoutSeeAttribute.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithCharacteristicWithoutSeeAttribute, any>> {
        return this.getProperties();
    }


}


