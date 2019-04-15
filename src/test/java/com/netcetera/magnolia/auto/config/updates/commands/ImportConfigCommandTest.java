package com.netcetera.magnolia.auto.config.updates.commands;

import com.netcetera.magnolia.auto.config.updates.AdvancedConfigUpdatesConstants;
import info.magnolia.context.MgnlContext;
import info.magnolia.templating.functions.TemplatingFunctions;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockSession;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;

import javax.jcr.RepositoryException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Test {@link ImportConfigCommand}
 */
public class ImportConfigCommandTest {

  private MockNode definitionNode;
  private MockWebContext context;

  private ImportConfigCommand importConfigCommand;

  private ClassLoader classLoader = getClass().getClassLoader();

  @Mock
  private TemplatingFunctions templatingFunctions;


  @BeforeEach
  void setup() {
    MockSession advancedConfigUpdatesSession = new MockSession(AdvancedConfigUpdatesConstants.WORKSPACE);
    context = new MockWebContext();
    context.addSession(AdvancedConfigUpdatesConstants.WORKSPACE, advancedConfigUpdatesSession);

    MockNode rootNode = new MockNode(advancedConfigUpdatesSession);
    definitionNode = new MockNode(AdvancedConfigUpdatesConstants.Definition.REL_ROOT_PATH);
    definitionNode.setParent(rootNode);
    rootNode.addNode(definitionNode);

    MgnlContext.setInstance(context);

    templatingFunctions = new TemplatingFunctions(null, null, () -> context);
    importConfigCommand = new ImportConfigCommand(templatingFunctions);
  }

  /**
   * Read method name.
   * @throws Exception  e
   */
  @Test
  public void shouldCreateNodeWithTheCorrectProperties() throws Exception {
    //given
    File file = new File(classLoader.getResource("advanced-config-updates-samples/definitions.csv").getFile());
    importConfigCommand.setStream(FileUtils.openInputStream(file));

    //when
    importConfigCommand.execute(context);

    //than
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(file)));
    String line;

    while ((line = bufferedReader.readLine()) != null) {
      String nodeProperties[] = line.split(",");
      String path = nodeProperties[0];

      assertNotNull(definitionNode.getNode(path));
      assertEquals(nodeProperties[1].trim(), getDefinitionProperty(definitionNode, path, AdvancedConfigUpdatesConstants.Definition.Property.PATH));
      assertEquals(nodeProperties[2].trim(), getDefinitionProperty(definitionNode, path, AdvancedConfigUpdatesConstants.Definition.Property.PROPERTY_NAME));
      assertEquals(nodeProperties[3].trim(), getDefinitionProperty(definitionNode, path, AdvancedConfigUpdatesConstants.Definition.Property.PROPERTY_VALUE));
    }

  }

  private String getDefinitionProperty(MockNode definitionNode, String path, String propertyName) throws RepositoryException {
     return definitionNode.getNode(path).getProperty(propertyName).getString();
    }
}
