package it.okkam.updater;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class MavenUtils {
  static String content = null;
  static URLConnection connection = null;

  public static String retireve(String baseUrl, String advUrl) {
    try {
      Thread.sleep(1000);
      connection = new URL("https://mvnrepository.com/artifact/" + baseUrl + "/" + advUrl + "/")
          .openConnection();
      Scanner scanner = new Scanner(connection.getInputStream());
      scanner.useDelimiter("\\Z");
      content = scanner.next();
      int i = content.indexOf("href=\"" + advUrl + "/");
      int j = content.indexOf("\"", advUrl.length() + 7 + i);
      scanner.close();
      return content.substring(advUrl.length() + 7 + i, j);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

}
