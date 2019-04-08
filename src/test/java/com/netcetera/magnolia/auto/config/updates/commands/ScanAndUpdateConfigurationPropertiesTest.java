package com.netcetera.magnolia.auto.config.updates.commands;

import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Test {@link ScanAndUpdateConfigurationProperties}.
 */
public class ScanAndUpdateConfigurationPropertiesTest {

	private MockNode serverConfigNode;
	private MockNode configChecksNode;

	private MockWebContext context;

	private ScanAndUpdateConfigurationProperties scanAndUpdateConfigurationProperties;


	@BeforeEach
	void setup() {
		MockSession configChecksSession = new MockSession("config-checks");
		MockSession configSession = new MockSession("config");

		context = new MockWebContext();
		scanAndUpdateConfigurationProperties = new ScanAndUpdateConfigurationProperties();
		context.addSession("config-checks", configChecksSession);
		context.addSession("config", configSession);
		serverConfigNode = new MockNode(configSession);
		configChecksNode = new MockNode(configChecksSession);
	}


	@Test
	public void shouldReturnTrueWhenNoConfigChecksExists() throws Exception {
		//given
		//when
		boolean scanResult = scanAndUpdateConfigurationProperties.execute(context);
		//then
		Assertions.assertTrue(scanResult);
	}

	@Test
	public void shouldScanAndUpdateConfigurationProperties() throws Exception {
		//given

		MockNode configCheckNode = new MockNode("defaultBaseURL");
		configCheckNode.setPrimaryType("config-check");
		configCheckNode.setProperty("path", "/server");
		configCheckNode.setProperty("propertyName", "defaultBaseUrl").setValue("www.production.com");
		configCheckNode.setParent(configChecksNode);
		configChecksNode.addNode(configCheckNode);

		MockNode configNode = new MockNode("server");
		configNode.setProperty("propertyName", "defaultBaseUrl").setValue("localhost:8080");
		serverConfigNode.addNode(configNode);
		configNode.setParent(serverConfigNode);
		//when
		boolean scanResult = scanAndUpdateConfigurationProperties.execute(context);
		//then
		Assertions.assertTrue(scanResult);
	}
}
