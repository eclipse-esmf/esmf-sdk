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

import { AspectWithMeasurementWithUnit, } from './AspectWithMeasurementWithUnit';
import { StaticUnitProperty, Unit, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithMeasurementWithUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMeasurementWithUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMeasurementWithUnit implements StaticMetaClass<AspectWithMeasurementWithUnit>, PropertyContainer<AspectWithMeasurementWithUnit> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMeasurementWithUnit';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithMeasurementWithUnit();


    public static readonly TEST_PROPERTY =


        new (class extends StaticUnitProperty<AspectWithMeasurementWithUnit, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithMeasurementWithUnit';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultMeasurement({
                    urn: this.NAMESPACE + 'TestMeasurement',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#float'), Units.fromName('percent'))
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithMeasurementWithUnit';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithMeasurementWithUnit.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithMeasurementWithUnit';
    }

    getProperties(): Array<StaticProperty<AspectWithMeasurementWithUnit, any>> {
        return [MetaAspectWithMeasurementWithUnit.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithMeasurementWithUnit, any>> {
        return this.getProperties();
    }


}


