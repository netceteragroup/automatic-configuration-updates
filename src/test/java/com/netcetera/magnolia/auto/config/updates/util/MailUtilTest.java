package com.netcetera.magnolia.auto.config.updates.util;

import com.netcetera.magnolia.auto.config.updates.apps.AdvancedConfigUpdates;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.mail.MailModule;
import info.magnolia.module.mail.MgnlMailFactory;
import info.magnolia.module.mail.handlers.MgnlMailHandler;
import info.magnolia.module.mail.templates.MgnlEmail;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.objectfactory.Components;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MailUtilTest {

  private static final Logger logger = LoggerFactory.getLogger(MailUtilTest.class);

  private static final String DEFINITION_NAME = "server";
  private static final String PATH = "server";
  private static final String PROPERY_NAME = "admin";
  private static final String PROPERY_VALUE = "false";

  private static final String EMAIL_NAME = "Emilija Stefanovska";
  private static final String EMAIL = "emilija.stefanovska@netcetera.com";
  private static final String EMAIL_SUBJECT = "Automatic configuration update report";

  private MockNode definitionNode;
  private MockNode emailNode;

  private MockWebContext context;
  private List<Node> updateConfigurationNodes;
  private MgnlEmail mgnlEmail;
  private MailModule mailModule;

  @BeforeEach
  void setup() {
    updateConfigurationNodes = new ArrayList<>();
    MockSession advancedConfigUpdatesWorkspace = new MockSession(AdvancedConfigUpdates.WORKSPACE);
    context = new MockWebContext();
    context.addSession(AdvancedConfigUpdates.WORKSPACE, advancedConfigUpdatesWorkspace);

    MockNode rootNode = new MockNode(advancedConfigUpdatesWorkspace);
    definitionNode = new MockNode(AdvancedConfigUpdates.Definition.REL_ROOT_PATH);
    definitionNode.setParent(rootNode);
    rootNode.addNode(definitionNode);

    emailNode = new MockNode(AdvancedConfigUpdates.Email.REL_ROOT_PATH);
    emailNode.setParent(rootNode);
    rootNode.addNode(emailNode);

    MgnlContext.setInstance(context);

    configureMailModule();
  }

  /**
   * Read method name.
   * @throws Exception e
   */
  @Test
  public void shouldEmailsBeSent() throws Exception {

    //given
    createDefinitionNode();
    createEmailNode();

    //when
    MailUtil.setListOfUpdatedNodes(updateConfigurationNodes);
    boolean isEmailSent = MailUtil.sendMails(emailNode);

    //then
    assertTrue(isEmailSent);
  }


  /**
   * See method name.
   */
  @Test
  public void shouldThrowErrorWhenNoHandlerIfConfigured()  {

    given(mailModule.getHandler()).willReturn(null);

    Assertions.assertThrows(Exception.class, () -> {
      MailUtil.sendMail(emailNode);
    });
  }


  private void createDefinitionNode()
      throws Exception {
    MockNode definitionChildNode = new MockNode(DEFINITION_NAME);
    definitionChildNode.setPrimaryType(AdvancedConfigUpdates.Definition.NODE_TYPE);
    definitionChildNode.setProperty(AdvancedConfigUpdates.Definition.Property.PATH, PATH);
    definitionChildNode.setProperty(AdvancedConfigUpdates.Definition.Property.PROPERTY_NAME, PROPERY_NAME);
    definitionChildNode.setProperty(AdvancedConfigUpdates.Definition.Property.PROPERTY_VALUE, PROPERY_VALUE);

    definitionChildNode.setParent(definitionNode);
    definitionNode.addNode(definitionChildNode);
  }

  private void createEmailNode () throws RepositoryException {
    MockNode emailChildNode = new MockNode(EMAIL_NAME);
    emailChildNode.setPrimaryType(AdvancedConfigUpdates.Email.NODE_TYPE);
    emailChildNode.setProperty(AdvancedConfigUpdates.Email.Property.NAME, EMAIL_NAME);
    emailChildNode.setProperty(AdvancedConfigUpdates.Email.Property.EMAIL, EMAIL);

    emailChildNode.setParent(emailNode);
    emailNode.addNode(emailChildNode);
  }

  private void configureMailModule() {
    ComponentProvider componentProvider = mock(ComponentProvider.class);

    mailModule = mock(MailModule.class);
    MgnlMailFactory mgnlMailFactory = mock(MgnlMailFactory.class);
    mgnlEmail = mock(MgnlEmail.class);

    given(componentProvider.getComponent(MailModule.class)).willReturn(mailModule);
    given(mailModule.getFactory()).willReturn(mgnlMailFactory);
    given(mgnlMailFactory.getEmailFromType(new HashMap<>(), "freemarker")).willReturn(mgnlEmail);
    given(mailModule.getHandler()).willReturn(mock(MgnlMailHandler.class));
    Components.setComponentProvider(componentProvider);
  }
}