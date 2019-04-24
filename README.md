# magnolia-auto-config-updates
Automatic configuration updates for Magnolia installations

This project allows the user to automatically check and repair configuration settings and ensure their correctness pre 
and post deployment for Test|Stage|Prod environments.

###Getting started

1. Add a dependency to your project's pom.xml

    `<groupId>com.netcetera</groupId>`<br>
    `<artifactId>magnolia-auto-config-updates</artifactId>`<br>
    `<version>0.0.1</version>`
    
2. run clean install
    
3. Go to the Magnolia JCR app, select the advancedConfigUpdates workspace and add the following content nodes on root 
level:

    * definitions
    * emails
  
4. Configure the following commands in **Configuration** > /modules/ui-admincentral/commands/default
   * scanAndUpdateConfiguration
     * class com.netcetera.magnolia.auto.config.updates.commands.ScanAndUpdateConfiguration
   * importDefinition
     * class com.netcetera.magnolia.auto.config.updates.commands.ImportConfigCommand
     
5. For testing locally your should configure the smtp server in **Configuration** > /modules/mail/config/smtp

6. Configure the app in **Configuration** > /modules/ui-admincentral/config/appLauncherLayout/groups/tools

### Using the app

The application consists of two sub-apps: Definitions and Email. In the definitions sub-app you can easily define 
the configurations that need to be updated or repaired. In the email sub-app you can add email addresses which users will 
be informed when there is a new configuration update. The configuration updates will be applied in the Config app by 
clicking on the Run a Scan action in the actionbar, available on a root level.

When adding a definition the path field should not start with "/".
