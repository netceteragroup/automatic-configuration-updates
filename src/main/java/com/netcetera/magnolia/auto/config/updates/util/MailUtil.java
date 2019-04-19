package com.netcetera.magnolia.auto.config.updates.util;

import com.netcetera.magnolia.auto.config.updates.AdvancedConfigUpdatesConstants;
import com.netcetera.magnolia.auto.config.updates.commands.ScanAndUpdateConfiguration;
import info.magnolia.jcr.predicate.NodeTypePredicate;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.mail.MailModule;
import info.magnolia.module.mail.MgnlMailFactory;
import info.magnolia.module.mail.templates.MgnlEmail;
import info.magnolia.objectfactory.Components;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MailUtil {

  private static final Logger logger = LoggerFactory.getLogger(ScanAndUpdateConfiguration.class);

  private static List<Node> listOfUpdatedNodes;

  /**
   * Finds all emails and calls {@link MailUtil#sendMail(Node)}.
   * @param root of all email node types.
   * @return is all email sent.
   */
  public static boolean sendMails(Node root)  {
    try {
      logger.debug("Getting emails.");
      NodeUtil.collectAllChildren(root, new NodeTypePredicate(AdvancedConfigUpdatesConstants.Email.NODE_TYPE))
        .forEach(MailUtil::sendMail);
      return true;
    } catch (RepositoryException e) {
      logger.error("Cannot retrieve emails. Reason {}", e.getMessage());
      return false;
    }
  }

  /**
   * Sends an email.
   * @param emailNode a node of type email.
   */
  public static void sendMail(Node emailNode) {
    String email = PropertyUtil.getString(emailNode, "email");
    MailModule mailModule = Components.getComponent(MailModule.class);
    MgnlMailFactory mgnlMailFactory = mailModule.getFactory();
    MgnlEmail mgnlEmail = mgnlMailFactory.getEmailFromType(new HashMap<String, Object>(), "freemarker");
    mgnlEmail.setFrom("config-checks@netcetera.com");
    try {
      mgnlEmail.setSubject("Automatic configuration update report");
      mgnlEmail.setToList(email);
      mgnlEmail.setBody(MailUtil.getEmailBody());
      mailModule.getHandler().sendMail(mgnlEmail);
    } catch (Exception e) {
      logger.error("Failed to send email to " + email);
    }
  }

  private static String getEmailBody() {
    String emailBody = "Dear admin user, <br/> <br/> "
        + "The following configuration properties have been successfully updated: "
        + "<br/> [CONFIG_PLACEHOLDER]";
    String configProperties = listOfUpdatedNodes.stream()
        .map(MailUtil::getPropertyNameAndValue)
        .collect(Collectors.joining("<br/>"));
    return emailBody.replace("[CONFIG_PLACEHOLDER]", configProperties)
        .concat(" <br/><br/> Sincerely, <br/> Your friendly neighborhood configuration scanner.");
  }

  private static String getPropertyNameAndValue(Node node) {
    return "<br/> Property Path: " + PropertyUtil.getString(node, "path")
        + "<br/> Property Name: "
        + PropertyUtil.getString(node, "propertyName")
        + "<br/> Property Value: " + PropertyUtil.getString(node, "propertyValue");
  }

  public static void setListOfUpdatedNodes(List<Node> listOfUpdatedNodes) {
    MailUtil.listOfUpdatedNodes = listOfUpdatedNodes;
  }
}