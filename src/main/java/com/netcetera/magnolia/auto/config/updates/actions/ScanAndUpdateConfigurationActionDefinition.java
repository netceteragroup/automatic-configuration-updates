package com.netcetera.magnolia.auto.config.updates.actions;

import info.magnolia.ui.api.action.CommandActionDefinition;

/**
 * {@link ScanAndUpdateConfigurationAction} action definition.
 */
public class ScanAndUpdateConfigurationActionDefinition extends CommandActionDefinition {

  /**
   * Set implementation class in public constructor.
   */
  public ScanAndUpdateConfigurationActionDefinition() {
    setImplementationClass(ScanAndUpdateConfigurationAction.class);
  }
}
