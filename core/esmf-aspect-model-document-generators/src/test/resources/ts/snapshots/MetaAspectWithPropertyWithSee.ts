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

import { AspectWithPropertyWithSee, } from './AspectWithPropertyWithSee';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithPropertyWithSee (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithSee).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithPropertyWithSee implements StaticMetaClass<AspectWithPropertyWithSee>, PropertyContainer<AspectWithPropertyWithSee> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithPropertyWithSee';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithPropertyWithSee();


    public static readonly TEST_BOOLEAN =

        new (class extends DefaultStaticProperty<AspectWithPropertyWithSee, boolean> {


            getPropertyType(): string {
                return 'boolean';
            }

            getContainingType(): string {
                return 'AspectWithPropertyWithSee';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testBoolean',
                    preferredNames: [],
                    descriptions: [],
                    see: ['http://example.com/',
                        'http://example.com/me',
                    ],
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
        return 'AspectWithPropertyWithSee';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithPropertyWithSee.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithPropertyWithSee';
    }

    getProperties(): Array<StaticProperty<AspectWithPropertyWithSee, any>> {
        return [MetaAspectWithPropertyWithSee.TEST_BOOLEAN];
    }

    getAllProperties(): Array<StaticProperty<AspectWithPropertyWithSee, any>> {
        return this.getProperties();
    }


}


