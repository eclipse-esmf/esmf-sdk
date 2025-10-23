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

import { AspectWithOperation, } from './AspectWithOperation';


/*
* Generated class MetaAspectWithOperation (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOperation).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithOperation implements StaticMetaClass<AspectWithOperation>, PropertyContainer<AspectWithOperation> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOperation';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithOperation();


    getModelClass(): string {
        return 'AspectWithOperation';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithOperation.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithOperation';
    }

    getProperties(): Array<StaticProperty<AspectWithOperation, any>> {
        return [];
    }

    getAllProperties(): Array<StaticProperty<AspectWithOperation, any>> {
        return this.getProperties();
    }


}


