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

import { AspectWithMarkdownDescription, } from './AspectWithMarkdownDescription';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithMarkdownDescription (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMarkdownDescription).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMarkdownDescription implements StaticMetaClass<AspectWithMarkdownDescription>, PropertyContainer<AspectWithMarkdownDescription> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMarkdownDescription';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithMarkdownDescription();


    public static readonly MY_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithMarkdownDescription, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithMarkdownDescription';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'myProperty',
                    preferredNames: [],
                    descriptions: [{
                        value: '\n      This is a sample concept demonstrating **Markdown** support in samm:description.\n\n      > NOTE: This is a note block.\n      > It supports multiple lines.\n      > Here\'s a second line of the note.\n\n      > EXAMPLE 1: This is the first example block.\n      > It can span several lines, and supports *italic* and **bold** text.\n\n      > EXAMPLE 2: This is the second example.\n      > Also multiline, for testing multiple example entries.\n\n      > SOURCE: ISO 12345:2023, section 4.2.1\n      > with an inline [link](https://www.example.com/spec).\n\n      Unordered list:\n        * Item A\n        * Item B\n        * Item C\n\n      Ordered list:\n        1. First\n        2. Second\n        3. Third\n\n      You can also include inline links like [Visit Example](https://example.com).\n\n      Another paragraph after a blank line to simulate text flow and paragraph breaks.\n   ',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.CHARACTERISTIC_NAMESPACE + '#Text',
                        preferredNames: [{
                            value: 'Text',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.',
                            languageTag: 'en',
                        },
                        ],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'myProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithMarkdownDescription';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithMarkdownDescription.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithMarkdownDescription';
    }

    getProperties(): Array<StaticProperty<AspectWithMarkdownDescription, any>> {
        return [MetaAspectWithMarkdownDescription.MY_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithMarkdownDescription, any>> {
        return this.getProperties();
    }


}


