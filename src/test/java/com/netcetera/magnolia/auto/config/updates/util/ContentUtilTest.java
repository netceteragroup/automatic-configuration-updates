package com.netcetera.magnolia.auto.config.updates.util;

import com.netcetera.magnolia.auto.config.updates.AdvancedConfigUpdatesConstants;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test {@link ContentUtil}
 */
public class ContentUtilTest {

  private MockNode rootNode;
  private MockNode childNode;

  @BeforeEach
  void setup() {
    MockSession advancedConfigUpdatesWorkspace = new MockSession(AdvancedConfigUpdatesConstants.WORKSPACE);
    MockWebContext context = new MockWebContext();
    context.addSession(AdvancedConfigUpdatesConstants.WORKSPACE, advancedConfigUpdatesWorkspace);

    rootNode = new MockNode(advancedConfigUpdatesWorkspace);
    childNode = new MockNode(AdvancedConfigUpdatesConstants.Definition.REL_ROOT_PATH);
    childNode.setName("childNode1");
    childNode.setParent(rootNode);
    rootNode.addNode(childNode);
    MgnlContext.setInstance(context);

  }

  /**
   * Read method name.
   *
   */
  @Test
  public void shouldGetChildNode() {
    //given
    //when
    Node node = ContentUtil.getOrCreateChildNode(rootNode, "childNode1", NodeTypes.Content.NAME);
    //then
    assertEquals(node, childNode);
    assertFalse(node.isNew());
  }

  /**
   * Read method name.
   *
   */
  @Test
  public void shouldCreateChildNode() throws RepositoryException {
    //given
    //when
    Node node2 = ContentUtil.getOrCreateChildNode(rootNode, "childNode2", NodeTypes.Content.NAME);
    //then
    assertEquals(node2.getName(), "childNode2");
    assertEquals(rootNode.getNode("childNode2"), node2);
  }

}