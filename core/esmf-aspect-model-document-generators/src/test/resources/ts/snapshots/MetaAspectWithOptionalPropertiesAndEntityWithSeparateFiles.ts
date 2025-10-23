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

import { AspectWithOptionalPropertiesAndEntityWithSeparateFiles, } from './AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
import { LangString, } from './core/langString';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertiesAndEntityWithSeparateFiles).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles implements StaticMetaClass<AspectWithOptionalPropertiesAndEntityWithSeparateFiles>, PropertyContainer<AspectWithOptionalPropertiesAndEntityWithSeparateFiles> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles();


    public static readonly PRODUCTION_PERIOD =

        new (class extends StaticContainerProperty<AspectWithOptionalPropertiesAndEntityWithSeparateFiles, ProductionPeriodEntity, ProductionPeriodEntity> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
            }

            getContainedType(): AspectWithOptionalPropertiesAndEntityWithSeparateFiles {
                return 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'productionPeriod',
                    preferredNames: [{
                        value: 'Production Period',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'The production period of this vehicle type.',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                },
                characteristic: new DefaultSingleEntity({
                    urn: this.NAMESPACE + 'ProductionPeriodCharacteristic',
                    preferredNames: [{
                        value: 'Production Period Characteristic',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [],
                    see: [],
                }, DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'ProductionPeriodEntity',
                    preferredNames: [{
                        value: 'Production Period Entity',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [],
                    see: [],
                }, MetaProductionPeriodEntity.INSTANCE.getProperties(), Optional.empty()))
                ,
                exampleValue: {},
                optional: true,
                notInPayload: false,
                payloadName: 'productionPeriod',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
    }

    getProperties(): Array<StaticProperty<AspectWithOptionalPropertiesAndEntityWithSeparateFiles, any>> {
        return [MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles.PRODUCTION_PERIOD];
    }

    getAllProperties(): Array<StaticProperty<AspectWithOptionalPropertiesAndEntityWithSeparateFiles, any>> {
        return this.getProperties();
    }


    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Minimal Vehicle Type Aspect', languageTag: 'en'},
        ];
    }


    getDescriptions(): Array<LangString> {
        return [
            {value: 'Simplified aspect containing only ProductionPeriodEntity as an optional property.', languageTag: 'en'},
        ];
    }


}


