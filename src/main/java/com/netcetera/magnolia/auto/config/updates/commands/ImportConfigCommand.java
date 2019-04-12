package com.netcetera.magnolia.auto.config.updates.commands;

import com.netcetera.magnolia.auto.config.updates.AdvancedConfigUpdatesConstants;

import info.magnolia.importexport.command.JcrImportCommand;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.templating.functions.TemplatingFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import info.magnolia.context.Context;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command for importing definition for configuration update.
 */
public class ImportConfigCommand extends JcrImportCommand {

  private TemplatingFunctions cmsfn;
  private final Logger logger = LoggerFactory.getLogger(ImportConfigCommand.class);

  @Inject
  public ImportConfigCommand(TemplatingFunctions templatingFunctions) {
    this.cmsfn = templatingFunctions;
  }

  @Override
  public boolean execute(Context context) throws Exception {
    try {
      createNodes();
    } catch (Exception e) {
      logger.error("There was problem a with creation of new nodes. Restoring from backup.");
      throw new Exception(e);
    }
    return true;
  }

  private void createNodes() throws RepositoryException {
    Node definitionsNode =
        cmsfn.nodeByPath("/" + AdvancedConfigUpdatesConstants.Definition.ROOT_PATH, AdvancedConfigUpdatesConstants.WORKSPACE);
    String line;
    String splitBy = ",";
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getStream()));
      while ((line = bufferedReader.readLine()) != null) {
        String[] nodeProperties = line.split(splitBy);
        createNodeAndSetProperties(definitionsNode, nodeProperties);
      }
    } catch (IOException e) {
      logger.error("Was not able to read from file.");
    }
    definitionsNode.getSession().save();
    logger.debug("New config definition nodes are successfully created and saved on path '{}'.",
        definitionsNode.getPath());
  }

  private void createNodeAndSetProperties(Node definitionsNode, String[] nodeProperties) throws RepositoryException {

    Node definitionNode =
      NodeUtil.createPath(definitionsNode, nodeProperties[0], AdvancedConfigUpdatesConstants.Definition.NODE_TYPE);
    definitionNode.setProperty(AdvancedConfigUpdatesConstants.Definition.Property.NAME, nodeProperties[0].trim());
    definitionNode.setProperty(AdvancedConfigUpdatesConstants.Definition.Property.PATH, nodeProperties[1].trim());
    definitionNode.setProperty(AdvancedConfigUpdatesConstants.Definition.Property.PROPERTY_NAME,
      nodeProperties[2].trim());
    definitionNode.setProperty(AdvancedConfigUpdatesConstants.Definition.Property.PROPERTY_VALUE,
      nodeProperties[3].trim());
  }

}
