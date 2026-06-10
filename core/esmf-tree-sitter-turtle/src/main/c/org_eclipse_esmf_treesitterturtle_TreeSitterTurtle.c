/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

#include <jni.h>
void *tree_sitter_turtle();
/*
 * Class:     org_treesitter_TreeSitterTurtle
 * Method:    tree_sitter_turtle
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_eclipse_esmf_treesitterturtle_TreeSitterTurtle_tree_1sitter_1turtle
  (JNIEnv *env, jclass clz){
   return (jlong) tree_sitter_turtle();
}
