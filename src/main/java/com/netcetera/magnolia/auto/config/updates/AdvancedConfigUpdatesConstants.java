package com.netcetera.magnolia.auto.config.updates;

public class AdvancedConfigUpdatesConstants {

  /**
   * Workspace name.
   */
  public static final String WORKSPACE = "advancedConfigUpdates";

  public static class Definition {
    /**
     * Node type name.
     */
    public static final String NODE_TYPE = "definition";
    public static final String ABS_ROOT_PATH = "/definitions";
    public static final String REL_ROOT_PATH = "definitions";

    public static class Property {
      /**
       * Property name.
       */
      public static final String NAME = "name";
      /**
       * Property name.
       */
      public static final String PATH = "path";
      /**
       * Property name.
       */
      public static final String PROPERTY_NAME = "propertyName";
      /**
       * Property name.
       */
      public static final String PROPERTY_VALUE = "propertyValue";

	    /**
	     * Property scanDate.
	     */
	    public static final String SCAN_DATE = "scanDate";
    }

  }

  public static class Email {
    /**
     * Node type name.
     */
    public static final String NODE_TYPE = "email";
    public static final String REL_ROOT_PATH = "emails";
    public static final String ABS_ROOT_PATH = "/emails";

    public static class Property {
      /**
       * Property name.
       */
      public static final String NAME = "name";
      /**
       * Property name.
       */
      public static final String EMAIL = "email";
    }
  }
}
