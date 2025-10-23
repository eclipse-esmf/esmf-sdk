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

import { AspectWithCollectionOfSimpleType, } from './AspectWithCollectionOfSimpleType';
import { StaticContainerProperty, } from './core/staticConstraintProperty';
import { number, } from 'asdas';


/*
* Generated class MetaAspectWithCollectionOfSimpleType (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionOfSimpleType).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionOfSimpleType implements StaticMetaClass<AspectWithCollectionOfSimpleType>, PropertyContainer<AspectWithCollectionOfSimpleType> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionOfSimpleType';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithCollectionOfSimpleType();


    public static readonly TEST_LIST =

        new (class extends StaticContainerProperty<AspectWithCollectionOfSimpleType, number, number[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithCollectionOfSimpleType';
            }

            getContainedType(): AspectWithCollectionOfSimpleType {
                return 'AspectWithCollectionOfSimpleType';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testList',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultList({
                    urn: this.NAMESPACE + 'IntegerList',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#int')), Optional.empty())
                ,
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: Integer.valueOf('35'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#int'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testList',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithCollectionOfSimpleType';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithCollectionOfSimpleType.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithCollectionOfSimpleType';
    }

    getProperties(): Array<StaticProperty<AspectWithCollectionOfSimpleType, any>> {
        return [MetaAspectWithCollectionOfSimpleType.TEST_LIST];
    }

    getAllProperties(): Array<StaticProperty<AspectWithCollectionOfSimpleType, any>> {
        return this.getProperties();
    }


}


