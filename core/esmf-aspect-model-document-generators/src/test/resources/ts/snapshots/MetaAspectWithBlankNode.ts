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

import { AspectWithBlankNode, } from './AspectWithBlankNode';
import { LangString, } from './core/langString';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithBlankNode (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithBlankNode).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithBlankNode implements StaticMetaClass<AspectWithBlankNode>, PropertyContainer<AspectWithBlankNode> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithBlankNode';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithBlankNode();


    public static readonly LIST =

        new (class extends StaticContainerProperty<AspectWithBlankNode, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithBlankNode';
            }

            getContainedType(): AspectWithBlankNode {
                return 'AspectWithBlankNode';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'list',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultCollection({
                    isAnonymous: true,
                    preferredNames: [{
                        value: 'Blank Node Liste',
                        languageTag: 'de',
                    },
                        {
                            value: 'Blank Node Collection',
                            languageTag: 'en',
                        },
                    ],
                    descriptions: [],
                    see: [],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#string')), Optional.empty())
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'list',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithBlankNode';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithBlankNode.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithBlankNode';
    }

    getProperties(): Array<StaticProperty<AspectWithBlankNode, any>> {
        return [MetaAspectWithBlankNode.LIST];
    }

    getAllProperties(): Array<StaticProperty<AspectWithBlankNode, any>> {
        return this.getProperties();
    }


    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Aspekt mit anonymen Knoten', languageTag: 'de'},
            {value: 'Aspect With Blank Node', languageTag: 'en'},
        ];
    }


}


