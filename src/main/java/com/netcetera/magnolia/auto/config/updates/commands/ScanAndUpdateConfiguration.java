package com.netcetera.magnolia.auto.config.updates.commands;

import com.netcetera.magnolia.auto.config.updates.AdvancedConfigUpdatesConstants;
import com.netcetera.magnolia.auto.config.updates.util.MailUtil;
import com.netcetera.magnolia.auto.config.updates.util.ContentUtil;
import info.magnolia.commands.MgnlCommand;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.predicate.NodeTypePredicate;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.util.SessionUtil;
import info.magnolia.repository.RepositoryConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static info.magnolia.repository.RepositoryConstants.CONFIG;

/**
 * Command that scans and updates the configuration properties configured in the content app.
 */
public class ScanAndUpdateConfiguration extends MgnlCommand {

	private final Logger logger = LoggerFactory.getLogger(ScanAndUpdateConfiguration.class);
	public static final String COMMAND_NAME = "scanAndUpdateConfiguration";

	@Override
	public boolean execute(Context context) throws Exception {

		List<Node> listOfUpdatedNodes = new ArrayList<>();
		Session session = getSession(AdvancedConfigUpdatesConstants.WORKSPACE);
		Node root = session.getNode(AdvancedConfigUpdatesConstants.Definition.ABS_ROOT_PATH);
		NodeUtil.collectAllChildren(root, new NodeTypePredicate(AdvancedConfigUpdatesConstants.Definition.NODE_TYPE))
						.forEach(configNode -> scanAndUpdate(listOfUpdatedNodes, configNode));

		if (!listOfUpdatedNodes.isEmpty()) {
      MailUtil.setListOfUpdatedNodes(listOfUpdatedNodes);
      MailUtil.sendMails(session.getNode(AdvancedConfigUpdatesConstants.Email.ABS_ROOT_PATH));
		}
		return listOfUpdatedNodes.isEmpty();
	}

	private void scanAndUpdate(List<Node> listOfUpdatedNodes, Node configDefinition) {
		String path = PropertyUtil.getString(configDefinition, AdvancedConfigUpdatesConstants.Definition.Property.PATH);
		Node configNode = getOrCreateNodeInConfigWorkspaceGiven(path);
		String propertyName = PropertyUtil.getString(configDefinition,
		                                            AdvancedConfigUpdatesConstants.Definition.Property.PROPERTY_NAME);
		String propertyValue = PropertyUtil.getString(configDefinition,
		                                              AdvancedConfigUpdatesConstants.Definition.Property.PROPERTY_VALUE);

		if (shouldUpdateConfiguration(configNode, propertyName, propertyValue)) {
			try {
				PropertyUtil.setProperty(configNode, propertyName, propertyValue);
				PropertyUtil.setProperty(configDefinition, NodeTypes.Activatable.ACTIVATION_STATUS, true);
				PropertyUtil.setProperty(configDefinition, AdvancedConfigUpdatesConstants.Definition.Property.SCAN_DATE,
				                         Calendar.getInstance());
				getSession(RepositoryConstants.CONFIG).save();
				getSession(AdvancedConfigUpdatesConstants.WORKSPACE).save();
				listOfUpdatedNodes.add(configDefinition);
			} catch (RepositoryException e) {
				logger.debug("Could not get node for path {}. Reason {}", path, e.getMessage());
			}
		}
	}

	private boolean shouldUpdateConfiguration(Node configNode, String propertyName, String propertyValue) {
		return configNode != null && !propertyValue.equals(PropertyUtil.getString(configNode, propertyName));
	}

	private Node getOrCreateNodeInConfigWorkspaceGiven(String path) {
		try {
			return ContentUtil.getOrCreateChildNode(SessionUtil.getNode(CONFIG, "/"),
			                                        path, NodeTypes.Content.NAME);
		} catch (RepositoryException e) {
			logger.error("Could not get or create node given path {}. Reason {}", path, e.getMessage());
			return null;
		}
	}

	private Session getSession(String name) throws RepositoryException {
		return MgnlContext.getJCRSession(name);
	}

}

