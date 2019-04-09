package com.netcetera.magnolia.auto.config.updates.contentapp;

import com.google.common.collect.Lists;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.app.AppView;
import info.magnolia.ui.api.app.SubAppDescriptor;
import info.magnolia.ui.api.location.DefaultLocation;
import info.magnolia.ui.api.location.Location;
import info.magnolia.ui.contentapp.ContentApp;
import info.magnolia.ui.contentapp.browser.BrowserSubApp;
import info.magnolia.ui.contentapp.browser.BrowserSubAppDescriptor;
import info.magnolia.ui.vaadin.integration.contentconnector.JcrContentConnectorDefinition;

import javax.inject.Inject;
import java.util.List;

/**
 * The Config Checks that extends ContentApp and opens its' SubApps in separate tabs.
 */
public class ConfigChecksApp extends ContentApp {

  //CHECKSTYLE:OFF
  @Inject
  public ConfigChecksApp(AppContext appContext, AppView view) {
    // CHECKSTYLE:ON
    super(appContext, view);
  }

  @Override
  public void start(Location location) {
    super.start(location);

    String appName = appContext.getAppDescriptor().getName();
    List<SubAppDescriptor> subAppsDescriptors = Lists.newArrayList(appContext.getAppDescriptor().getSubApps().values());
    for (SubAppDescriptor subAppDescriptor : subAppsDescriptors) {
      openSubApp(appName, subAppDescriptor);
    }

    // Reopen first app in order to have focus on that one
    if (!subAppsDescriptors.isEmpty()) {
      openSubApp(appName, subAppsDescriptors.get(0));
    }
  }

  private void openSubApp(String appName, SubAppDescriptor subAppDescriptor) {
    if (subAppDescriptor.getSubAppClass().equals(BrowserSubApp.class)) {
      JcrContentConnectorDefinition connectorDefinition =
          (JcrContentConnectorDefinition) ((BrowserSubAppDescriptor) subAppDescriptor).getContentConnector();
      getAppContext().openSubApp(new DefaultLocation(Location.LOCATION_TYPE_APP, appName, subAppDescriptor.getName(),
          connectorDefinition.getRootPath()));
    }
  }
}
