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

import { AspectWithUnit, } from './AspectWithUnit';
import { StaticUnitProperty, Unit, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithUnit implements StaticMetaClass<AspectWithUnit>, PropertyContainer<AspectWithUnit> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUnit';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithUnit();


    public static readonly TEST_PROPERTY =


        new (class extends StaticUnitProperty<AspectWithUnit, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithUnit';
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
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#int'), Units.fromName('bitPerSecond'))
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithUnit';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithUnit.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithUnit';
    }

    getProperties(): Array<StaticProperty<AspectWithUnit, any>> {
        return [MetaAspectWithUnit.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithUnit, any>> {
        return this.getProperties();
    }


}


