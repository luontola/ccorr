package jonelo.sugar.util;

public class GeneralProgram {

    /** Creates new GeneralUtil */
    public GeneralProgram() {
    }

  /** which Java Version is required?
   * @param version Java version (e. g. "1.3.1")
   */    
  public final static void requiresMinimumJavaVersion(final String version)
  {
    try {
      String ver = System.getProperty("java.version");
      if (ver.compareTo(version) < 0)
      {
         System.out.println("ERROR: a newer Java VM is required.\nVersion of your Java VM: "+ver+
                             "\nRequired version: "+ version);
         System.exit(1);                       
      }
    } catch (Throwable t) {
      System.out.println("uncaught exception: " + t);
      t.printStackTrace();
    }
  }

}
