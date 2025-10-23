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

import { AspectWithOperationWithSeeAttribute, } from './AspectWithOperationWithSeeAttribute';


/*
* Generated class MetaAspectWithOperationWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOperationWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithOperationWithSeeAttribute implements StaticMetaClass<AspectWithOperationWithSeeAttribute>, PropertyContainer<AspectWithOperationWithSeeAttribute> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOperationWithSeeAttribute';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithOperationWithSeeAttribute();


    getModelClass(): string {
        return 'AspectWithOperationWithSeeAttribute';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithOperationWithSeeAttribute.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithOperationWithSeeAttribute';
    }

    getProperties(): Array<StaticProperty<AspectWithOperationWithSeeAttribute, any>> {
        return [];
    }

    getAllProperties(): Array<StaticProperty<AspectWithOperationWithSeeAttribute, any>> {
        return this.getProperties();
    }


}


