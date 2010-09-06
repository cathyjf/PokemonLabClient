/////////////////////////////////////////////////////////
//  Bare Bones Browser Launch                          //
//  Version 1.5.99.3 (April 6, 2009)                   //
//  By Dem Pilafian and Willem van Engen               //
//  Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
//  Example Usage:                                     //
//     String url = "http://www.centerkey.com/";       //
//     BareBonesBrowserLaunch.openURL(url);            //
//  Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

package shoddybattleclient.utils;

import java.awt.Component;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import javax.swing.JOptionPane;



/** Class to open the system's default web browser.
* <p>
* Until Java 1.6, there was no standard way to open an internet page with the
* user's default web browser. This class aims to be a simple just-works solution
* to this. It tries to use Java 1.6's method first, and falls back to running
* an executable (which requires full system permissions, of course), which is
* different for each platform.
* <p>
* This code is an adaptation of
* <a href="http://www.centerkey.com/java/browser/">http://www.centerkey.com/java/browser/</a>
*/
public class BareBonesBrowserLaunch {

   private static final String errMsg = "Error attempting to launch web browser";

   /** Open a URL
    *
    * @param url {@link URL} to open
    * @param parent parent component for error message dialog
    */
   public static void openURL(URL url, Component parent) {
        openURL(url.toExternalForm(), parent);
   }

   /** Open a string URL
    *
    * @param surl URL to open (as String)
    * @param parent parent component for error message dialog
    */
   @SuppressWarnings("unchecked") // to support older java compilers
   public static void openURL(String surl, Component parent) {
    // Try java desktop API first (new in Java 1.6)
    // basically: java.awt.Desktop.getDesktop().browse(new URI(url));
    try {
        Class desktop = Class.forName("java.awt.Desktop");
        Method getDesktop = desktop.getDeclaredMethod("getDesktop", new Class[] {});
        Object desktopInstance = getDesktop.invoke(null, new Object[] {});
        Method browse = desktop.getDeclaredMethod("browse", new Class[] {URI.class});
        URI uri = new URI(surl);
        //logger.fine("Using Java Desktop API to open URL '"+url+"'");
        browse.invoke(desktopInstance, new Object[] {uri});
        return;
    } catch(Exception e) { }

    // Failed, resort to executing the browser manually
    String osName = System.getProperty("os.name");
    try {
        // Mac OS has special Java class
        if (osName.startsWith("Mac OS")) {
            Class fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL",
                    new Class[] {String.class});
            //logger.fine("Using "+fileMgr+" to open URL '"+url+"'");
            openURL.invoke(null, new Object[] {surl});
            return;
        }

        String[] cmd = null;

        // Windows execs url.dll
        if (osName.startsWith("Windows")) {
            cmd = new String[] { "rundll32", "url.dll,FileProtocolHandler", surl };

        // else assume unix/linux: call one of the available browsers
        } else {
            String[] browsers = {
                    // Freedesktop, http://portland.freedesktop.org/xdg-utils-1.0/xdg-open.html
                    "xdg-open",
                    // Debian
                    "sensible-browser",
                    // Otherwise call browsers directly
                    "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++)
                if (Runtime.getRuntime().exec(
                        new String[] {"which", browsers[count]}).waitFor() == 0)
                    browser = browsers[count];

            if (browser == null) {
                //logger.warning("No web browser found");
                throw new Exception("Could not find web browser");
            }

            cmd = new String[] { browser, surl };
        }

        if (Runtime.getRuntime().exec(cmd).waitFor() !=0 )
            throw new Exception("Error opening page: "+surl);
    }
    catch (Exception e) {
        JOptionPane.showMessageDialog(parent, errMsg + ":\n" + e.getLocalizedMessage());
    }
   }

   /** Open a URL
    * <p>
    * This is equal to {@link #openURL(URL, Component) openURL(url, null)}
    * so any error dialog will have no parent.
    *
    * @param url {@link URL} to open
    */
   public static void openURL(URL url) {
        openURL(url, null);
   }

   /** Open a string URL
    * <p>
    * This is equal to {@link #openURL(String, Component) openURL(surl, null)}
    * so any error dialog will have no parent.
    *
    * @param surl URL to open (as String)
    */
   public static void openURL(String surl) {
        openURL(surl, null);
   }
}