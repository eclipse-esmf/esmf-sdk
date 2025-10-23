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

import { AspectWithDescriptions, } from './AspectWithDescriptions';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';


/*
* Generated class MetaAspectWithDescriptions (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDescriptions).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithDescriptions implements StaticMetaClass<AspectWithDescriptions>, PropertyContainer<AspectWithDescriptions> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDescriptions';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithDescriptions();


    public static readonly TEST_BOOLEAN =

        new (class extends DefaultStaticProperty<AspectWithDescriptions, boolean> {


            getPropertyType(): string {
                return 'boolean';
            }

            getContainingType(): string {
                return 'AspectWithDescriptions';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testBoolean',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'BooleanReplacedAspectArtifact',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testBoolean',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithDescriptions';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithDescriptions.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithDescriptions';
    }

    getProperties(): Array<StaticProperty<AspectWithDescriptions, any>> {
        return [MetaAspectWithDescriptions.TEST_BOOLEAN];
    }

    getAllProperties(): Array<StaticProperty<AspectWithDescriptions, any>> {
        return this.getProperties();
    }


    getDescriptions(): Array<LangString> {
        return [
            {value: 'Test Beschreibung', languageTag: 'de'},
            {value: 'Test Description', languageTag: 'en'},
        ];
    }


}


