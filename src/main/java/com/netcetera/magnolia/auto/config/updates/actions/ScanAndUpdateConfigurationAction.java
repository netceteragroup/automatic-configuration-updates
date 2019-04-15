package com.netcetera.magnolia.auto.config.updates.actions;

import com.netcetera.magnolia.auto.config.updates.commands.ScanAndUpdateConfiguration;
import info.magnolia.commands.CommandsManager;
import info.magnolia.commands.chain.Command;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Action to scan and update configurations.
 */
public class ScanAndUpdateConfigurationAction extends AbstractAction<ScanAndUpdateConfigurationActionDefinition> {

  private static final String COMMAND_EXECUTED_MESSAGE_KEY = "configChecks.command.executed";
  private final Logger log = LoggerFactory.getLogger(ScanAndUpdateConfigurationAction.class);

  private final CommandsManager commandsManager;
  private final SimpleTranslator i18n;
  private final UiContext uiContext;

  /**
   * Constructor.
   *
   * @param definition      The action definition.
   * @param commandsManager The commands manager to execute the command performing the actual export.
   * @param i18n            The translation support.
   * @param uiContext       The UI context to show messages to the user regarding the export.
   */
  @Inject
  public ScanAndUpdateConfigurationAction(final ScanAndUpdateConfigurationActionDefinition definition,
                                      CommandsManager commandsManager,
                                      SimpleTranslator i18n,
                                      UiContext uiContext) {
    super(definition);
    this.commandsManager = commandsManager;
    this.i18n = i18n;
    this.uiContext = uiContext;
  }

  @Override
  public void execute() {
    Command command = commandsManager.getCommand(ScanAndUpdateConfiguration.COMMAND_NAME);
    try {
      commandsManager.executeCommand(command, null);
      uiContext.openNotification(MessageStyleTypeEnum.INFO, true, i18n.translate(COMMAND_EXECUTED_MESSAGE_KEY));
    } catch (Exception e) {
      log.error("Command execution failed. Reason {}", e.getMessage());
      uiContext.openNotification(MessageStyleTypeEnum.ERROR, true, e.getMessage());
    }
  }
}
