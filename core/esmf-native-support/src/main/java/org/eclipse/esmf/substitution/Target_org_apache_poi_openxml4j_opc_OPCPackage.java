/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.substitution;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;

/**
 * This is a <a href="https://build-native-java-apps.cc/developer-guide/substitution/">GraalVM substitution class</a>
 * for {@link OPCPackage}.
 * Reason: The hard reference to a Log4J logger is not resolveable/instantiatable at runtime, so it is replaced with
 * a dummy logger.
 */
@TargetClass( OPCPackage.class )
@SuppressWarnings( {
      "unused",
      "squid:S00101", // Class name uses GraalVM substitution class naming schema, see
      // https://github.com/oracle/graal/tree/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk
      "checkstyle:TypeName",
      "NewClassNamingConvention" } )
public final class Target_org_apache_poi_openxml4j_opc_OPCPackage {
   @SuppressWarnings( { "NonConstantLogger", "NonConstantFieldWithUpperCaseName" } )
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   private static Logger LOG = new DummyLogger();
}
