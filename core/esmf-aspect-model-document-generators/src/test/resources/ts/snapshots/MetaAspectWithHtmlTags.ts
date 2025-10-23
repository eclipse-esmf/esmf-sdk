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

import { ReplacedAspectArtifact, } from './ReplacedAspectArtifact';


import { AspectWithHtmlTags, } from './AspectWithHtmlTags';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';


/*
* Generated class MetaAspectWithHtmlTags (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithHtmlTags).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithHtmlTags implements StaticMetaClass<AspectWithHtmlTags>, PropertyContainer<AspectWithHtmlTags> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithHtmlTags';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithHtmlTags();


    public static readonly TEST_ENTITY =

        new (class extends DefaultStaticProperty<AspectWithHtmlTags, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithHtmlTags';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testEntity',
                    preferredNames: [{
                        value: 'Preferred Name <input value=\'\'/><script>alert(\'Boom!\')</script>\'/>',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testEntity',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithHtmlTags';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithHtmlTags.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithHtmlTags';
    }

    getProperties(): Array<StaticProperty<AspectWithHtmlTags, any>> {
        return [MetaAspectWithHtmlTags.TEST_ENTITY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithHtmlTags, any>> {
        return this.getProperties();
    }


    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Aspect With <img src=xss.png onerror=alert('Boom!')> Entity', languageTag: 'en'},
        ];
    }


    getDescriptions(): Array<LangString> {
        return [
            {value: 'Aspect With <p>inside html tag</p> Entity', languageTag: 'en'},
        ];
    }


}


