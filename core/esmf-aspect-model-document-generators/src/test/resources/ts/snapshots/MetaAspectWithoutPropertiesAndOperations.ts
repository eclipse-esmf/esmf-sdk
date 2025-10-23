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

import { AspectWithoutPropertiesAndOperations, } from './AspectWithoutPropertiesAndOperations';


/*
* Generated class MetaAspectWithoutPropertiesAndOperations (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithoutPropertiesAndOperations).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithoutPropertiesAndOperations implements StaticMetaClass<AspectWithoutPropertiesAndOperations>, PropertyContainer<AspectWithoutPropertiesAndOperations> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithoutPropertiesAndOperations';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithoutPropertiesAndOperations();


    getModelClass(): string {
        return 'AspectWithoutPropertiesAndOperations';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithoutPropertiesAndOperations.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithoutPropertiesAndOperations';
    }

    getProperties(): Array<StaticProperty<AspectWithoutPropertiesAndOperations, any>> {
        return [];
    }

    getAllProperties(): Array<StaticProperty<AspectWithoutPropertiesAndOperations, any>> {
        return this.getProperties();
    }


}


