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

import { AspectWithEither, } from './AspectWithEither';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { Either, } from './core/Either';


/*
* Generated class MetaAspectWithEither (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEither).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEither implements StaticMetaClass<AspectWithEither>, PropertyContainer<AspectWithEither> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEither';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEither();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithEither, Either<string, boolean>> {


            getPropertyType(): string {
                return 'Either';
            }

            getContainingType(): string {
                return 'AspectWithEither';
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
                        urn: this.NAMESPACE + 'TestEither',
                        preferredNames: [{
                            value: 'Test Either',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'This is a test Either.',
                            languageTag: 'en',
                        },
                        ],
                        see: ['http://example.com/',
                        ],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithEither';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithEither.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithEither';
    }

    getProperties(): Array<StaticProperty<AspectWithEither, any>> {
        return [MetaAspectWithEither.TEST_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithEither, any>> {
        return this.getProperties();
    }


}


