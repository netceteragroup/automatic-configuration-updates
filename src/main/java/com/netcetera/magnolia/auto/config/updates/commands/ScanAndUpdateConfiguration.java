package com.netcetera.magnolia.auto.config.updates.commands;

import com.netcetera.magnolia.auto.config.updates.AdvancedConfigUpdatesConstants;
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

import static info.magnolia.repository.RepositoryConstants.CONFIG;

/**
 * Command that scans and updates the configuration properties configured in the content app.
 */
public class ScanAndUpdateConfiguration extends MgnlCommand {

	private final Logger logger = LoggerFactory.getLogger(ScanAndUpdateConfiguration.class);
	private boolean status;

	@Override
	public boolean execute(Context context) throws Exception {
		status = true;
		Session session = MgnlContext.getJCRSession(AdvancedConfigUpdatesConstants.WORKSPACE);
		Node root = session.getNode(AdvancedConfigUpdatesConstants.Definition.ROOT_PATH);


		//get path property value from config-checks node.
		//find the node in config workspace
		//scan
		//update if necessary.

		//emails to be sent!
//
		NodeUtil.collectAllChildren(root, new NodeTypePredicate(AdvancedConfigUpdatesConstants.Definition.NODE_TYPE))


						.forEach(this::scanAndUpdate);

		return status;
	}

	private void scanAndUpdate(Node configDefinition) {
		String path = PropertyUtil.getString(configDefinition, AdvancedConfigUpdatesConstants.Definition.Property.PATH);
		Node configNode = getNodeFromConfigurationWorkspaceFor(path);
		String propertyName = PropertyUtil.getString(configDefinition,
		                                             AdvancedConfigUpdatesConstants.Definition.Property.PROPERTY_NAME);
		String propertyValue = PropertyUtil.getString(configDefinition,
		                                              AdvancedConfigUpdatesConstants.Definition.Property.PROPERTY_VALUE);
		if (configNode != null) {

			if (shouldUpdate(configNode, propertyName, propertyValue)) {
				try {
					PropertyUtil.setProperty(configNode, propertyName, propertyValue);
					PropertyUtil.setProperty(configDefinition, NodeTypes.Activatable.ACTIVATION_STATUS, true);
					status = true;
				} catch (RepositoryException e) {
					logger.debug("Could not get node for path {}. Reason {}", path, e.getMessage());
					status = false;
				}
			}
		} else {
			final Node node = NodeUtil.createPath(SessionUtil.getNode(CONFIG, "/"),
			                                            path, NodeTypes.Content.NAME, true);
			PropertyUtil.setProperty(node, propertyName, propertyValue);
			PropertyUtil.setProperty(node, NodeTypes.Activatable.ACTIVATION_STATUS, true);
			status = true;

		}

		getConfigSession().save();

	}

	private boolean shouldUpdate(Node configNode, String propertyName, String propertyValue) {
		return !PropertyUtil.getString(configNode, propertyName).equals(propertyValue);
	}

	private Node getNodeFromConfigurationWorkspaceFor(String path) {
		try {
			return getConfigSession().getNode(path);
		} catch (RepositoryException e) {
			logger.debug("Could not get node for path {}. Reason {}", path, e.getMessage());
			return null;
		}
	}

	private Session getConfigSession() throws RepositoryException{
 		return MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
	}

}
