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

import { AspectWithCode, } from './AspectWithCode';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithCode (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCode).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCode implements StaticMetaClass<AspectWithCode>, PropertyContainer<AspectWithCode> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCode';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithCode();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithCode, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithCode';
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
                        urn: this.NAMESPACE + 'TestCode',
                        preferredNames: [{
                            value: 'Test Code',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'This is a test code.',
                            languageTag: 'en',
                        },
                        ],
                        see: ['http://example.com/',
                        ],
                    },
                    dataType: new DefaultScalar('http://www.w3.org/2001/XMLSchema#int'),
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithCode';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithCode.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithCode';
    }

    getProperties(): Array<StaticProperty<AspectWithCode, any>> {
        return [MetaAspectWithCode.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithCode, any>> {
        return this.getProperties();
    }


}


