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

import { AspectWithQuantifiableWithoutUnit, } from './AspectWithQuantifiableWithoutUnit';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithQuantifiableWithoutUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithQuantifiableWithoutUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithQuantifiableWithoutUnit implements StaticMetaClass<AspectWithQuantifiableWithoutUnit>, PropertyContainer<AspectWithQuantifiableWithoutUnit> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithQuantifiableWithoutUnit';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithQuantifiableWithoutUnit();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithQuantifiableWithoutUnit, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithQuantifiableWithoutUnit';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultQuantifiable({
                    urn: this.NAMESPACE + 'TestQuantifiable',
                    preferredNames: [{
                        value: 'Test Quantifiable',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test Quantifiable',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                    ],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#float'), Optional.empty())
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithQuantifiableWithoutUnit';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithQuantifiableWithoutUnit.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithQuantifiableWithoutUnit';
    }

    getProperties(): Array<StaticProperty<AspectWithQuantifiableWithoutUnit, any>> {
        return [MetaAspectWithQuantifiableWithoutUnit.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithQuantifiableWithoutUnit, any>> {
        return this.getProperties();
    }


}


