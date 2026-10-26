/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.lsp.diagnostic;

import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;

public interface ResolutionStrategyAwareViolationProvider extends ViolationProvider {
   void setResolutionStrategyService( final ResolutionStrategyService resolutionStrategyService );
}
