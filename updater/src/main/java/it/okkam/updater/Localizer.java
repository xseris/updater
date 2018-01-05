package it.okkam.updater;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Localizer {
  static List<LocalRepo> repositories = new ArrayList<LocalRepo>();

  public static List<LocalRepo> getRepositories()
      throws SAXException, IOException, ParserConfigurationException {
    String home = System.getProperty("user.home");
    File[] files = new File(home + "/git").listFiles();
    getRepos(files);
    getDepVersions();
    return repositories;
  }

  public static void getRepos(File[] files) {
    for (File file : files) {
      if (file.isDirectory()) {
        File[] subfiles = new File(file.getAbsolutePath()).listFiles();
        if (isThereAPom(subfiles)) {
          LocalRepo local = new LocalRepo(file.getName(), file.getAbsolutePath());
          repositories.add(local);
        }
        if (!file.getName().contains("maven")) {
          getRepos(file.listFiles());
        }
      }
    }
  }

  public static boolean isThereAPom(File[] files) {
    for (File file : files) {
      if (file.getName().equals("pom.xml")) {
        return true;
      }
    }
    return false;
  }

  public static void getDepVersions()
      throws SAXException, IOException, ParserConfigurationException {
    for (LocalRepo repo : repositories) {
      File file = new File(repo.getPath() + "/pom.xml");
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(file);
      doc.getDocumentElement().normalize();

      NodeList nList = doc.getElementsByTagName("dependency");

      for (int i = 0; i < nList.getLength(); i++) {
        Node nNode = nList.item(i);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

          Element eElement = (Element) nNode;
          String groupId = eElement.getElementsByTagName("groupId").item(0).getTextContent();
          String artifactId = eElement.getElementsByTagName("artifactId").item(0).getTextContent();
          String version = "";
          if (eElement.getElementsByTagName("version").item(0) != null) {
            version = eElement.getElementsByTagName("version").item(0).getTextContent();
            if (version.startsWith("${")) {
              version = version.replace("${", "");
              version = version.replace("}", "");
              if (doc.getElementsByTagName(version).item(0) != null) {
                version = doc.getElementsByTagName(version).item(0).getTextContent();
              }

            }
          }
          String r = groupId + "." + artifactId;
          repo.getDependencyMap().put(r, version);

          System.out.println(groupId);
          System.out.println(artifactId);
          if (eElement.getElementsByTagName("version").item(0) != null) {
            System.out.println(version);
          }
        }
      }
    }
  }

}
