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

import { AspectWithCollections, } from './AspectWithCollections';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithCollections (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollections).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollections implements StaticMetaClass<AspectWithCollections>, PropertyContainer<AspectWithCollections> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollections';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithCollections();


    public static readonly SET_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithCollections, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithCollections';
            }

            getContainedType(): AspectWithCollections {
                return 'AspectWithCollections';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'setProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultSet({
                    isAnonymous: true,
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#string')), Optional.empty())
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'setProperty',
                isAbstract: false,
            });


    public static readonly LIST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithCollections, number, number[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithCollections';
            }

            getContainedType(): AspectWithCollections {
                return 'AspectWithCollections';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'listProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultList({
                    isAnonymous: true,
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#int')), Optional.empty())
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'listProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithCollections';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithCollections.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithCollections';
    }

    getProperties(): Array<StaticProperty<AspectWithCollections, any>> {
        return [MetaAspectWithCollections.SET_PROPERTY, MetaAspectWithCollections.LIST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithCollections, any>> {
        return this.getProperties();
    }


}


