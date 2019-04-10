package com.netcetera.magnolia.auto.config.updates.util;

import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class ContentUtil {

	private static final Logger logger = LoggerFactory.getLogger(ContentUtil.class);

	private ContentUtil() {
		// private constructor for utility class
	}

	/**
	 * Gets from or creates for parent node a child node of given type and type.
	 *
	 * @param parentNode parent node
	 * @param nodeName   name of the (potentially new) node
	 * @param nodeType   JCR primary node type name
	 * @return node
	 * @throws RepositoryException if that fails
	 */
	public static Node getOrCreateChildNode(Node parentNode, String nodeName, String nodeType)
					throws RepositoryException {
		Node childNode;
		if (parentNode.hasNode(nodeName)) {
			childNode = parentNode.getNode(nodeName);
		} else {
			childNode = NodeUtil.createPath(parentNode, nodeName, nodeType, true);
		}

		logger.debug("Returning {} child node '{}'.", childNode.isNew() ? "new" : "existing", nodeName);
		return childNode;
	}
}
