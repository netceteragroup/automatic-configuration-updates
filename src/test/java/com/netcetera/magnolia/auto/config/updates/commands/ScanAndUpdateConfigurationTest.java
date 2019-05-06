package com.netcetera.magnolia.auto.config.updates.commands;

import com.netcetera.magnolia.auto.config.updates.apps.AdvancedConfigUpdates;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jcr.RepositoryException;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test {@link ScanAndUpdateConfiguration}.
 */
public class ScanAndUpdateConfigurationTest {

	private MockNode configRootNode;
	private MockNode configDefinitionsNode;

	private MockWebContext context;

	private ScanAndUpdateConfiguration scanAndUpdateConfiguration;


	@BeforeEach
	void setup() {
		MockSession advancedConfigUpdatesWorkspace = new MockSession(AdvancedConfigUpdates.WORKSPACE);
		MockSession configSession = new MockSession(RepositoryConstants.CONFIG);
		context = new MockWebContext();
		scanAndUpdateConfiguration = new ScanAndUpdateConfiguration();
		context.addSession(AdvancedConfigUpdates.WORKSPACE, advancedConfigUpdatesWorkspace);
		context.addSession(RepositoryConstants.CONFIG, configSession);
		configRootNode = new MockNode(configSession);
		MockNode configDefinitionRoot = new MockNode(advancedConfigUpdatesWorkspace);

		configDefinitionsNode = new MockNode(AdvancedConfigUpdates.Definition.REL_ROOT_PATH);
		configDefinitionsNode.setParent(configDefinitionRoot);
		configDefinitionRoot.addNode(configDefinitionsNode);

		MockNode emailNode =  new MockNode(AdvancedConfigUpdates.Email.REL_ROOT_PATH);
		emailNode.setParent(configDefinitionRoot);
		configDefinitionRoot.addNode(emailNode);


		MgnlContext.setInstance(context);
	}

	@Test
	 void shouldNotScanWhenNoConfigurationDefinitionsExist() throws Exception {
		//given
		//when
		boolean scanResult = scanAndUpdateConfiguration.execute(context);
		//then
		assertTrue(scanResult);
		assertTrue(configDefinitionsNode.getChildren().isEmpty());
	}

	@Test
	 void shouldCreateNodeOrPropertyInConfigWorkspace() throws Exception {
		//given
		createAdvancedConfigDefinitionNode("activeVersion", "server/version", "active", "true");
		//when
		scanAndUpdateConfiguration.execute(context);
		//then
		assertEquals(PropertyUtil.getString(configRootNode.getNode("/server/version"), "active"), "true");
	}

	@Test
	 void shouldUpdateConfigurationNodeOrProperty() throws Exception {
		//given
		createAdvancedConfigDefinitionNode("isAdmin", "/server", "admin", "true");
		createConfigChildNode("admin", "false");
		//when
		scanAndUpdateConfiguration.execute(context);
		//then
		assertEquals(PropertyUtil.getString(configRootNode.getNode("server"), "admin"), "true");
		assertTrue(PropertyUtil.getBoolean(configDefinitionsNode.getNode("isAdmin"),
		                                   NodeTypes.Activatable.ACTIVATION_STATUS, false));
	}

	@Test
	 void shouldNotUpdateWhenConfigurationIsCorrect() throws Exception {
		//given
		createAdvancedConfigDefinitionNode("defaultBaseUrl", "/server", "defaultBaseUrl", "www.production.com");
		createConfigChildNode("defaultBaseUrl", "www.production.com");
		//when
		boolean scanResult = scanAndUpdateConfiguration.execute(context);
		//then
		assertEquals(PropertyUtil.getString(configRootNode.getNode("server"), "defaultBaseUrl"), "www.production.com");
		assertEquals(PropertyUtil.getString(configDefinitionsNode.getNode("defaultBaseUrl"), "propertyValue"),
		             "www.production.com");
		assertTrue(scanResult);
	}

	private void createAdvancedConfigDefinitionNode(String name, String path, String propertyName,
	                                                String propertyValue) throws RepositoryException {
		MockNode configDefChildNode = new MockNode(name);
		configDefChildNode.setPrimaryType(AdvancedConfigUpdates.Definition.NODE_TYPE);
		configDefChildNode.setProperty(AdvancedConfigUpdates.Definition.Property.PATH, path);
		configDefChildNode.setProperty(AdvancedConfigUpdates.Definition.Property.PROPERTY_NAME, propertyName);
		configDefChildNode.setProperty(AdvancedConfigUpdates.Definition.Property.PROPERTY_VALUE, propertyValue);
		configDefChildNode.setParent(this.configDefinitionsNode);
		this.configDefinitionsNode.addNode(configDefChildNode);
	}

	private void createConfigChildNode(String propertyName, String propertyValue) throws RepositoryException {
		MockNode configChildNode = new MockNode("server");
		configChildNode.setProperty(propertyName, propertyValue);
		configRootNode.addNode(configChildNode);
		configChildNode.setParent(configRootNode);
	}

}
