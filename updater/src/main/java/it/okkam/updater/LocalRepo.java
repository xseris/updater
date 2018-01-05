package it.okkam.updater;

import java.util.HashMap;
import java.util.Map;

public class LocalRepo {
  private String name;
  private String path;
  private String relativePath;
  private Map<String, String> dependencyMap;

  LocalRepo(String name, String path) {
    this.name = name;
    this.relativePath = path.split("git/")[1];
    this.path = path;
    this.dependencyMap = new HashMap<String, String>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, String> getDependencyMap() {
    return dependencyMap;
  }

  public void setDependencyMap(Map<String, String> dependencyMap) {
    this.dependencyMap = dependencyMap;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getRelativePath() {
    return relativePath;
  }

  public void setRelativePath(String relativePath) {
    this.relativePath = relativePath;
  }

}
