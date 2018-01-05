package it.okkam.updater;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App extends JPanel {

  public App() throws SAXException, IOException, ParserConfigurationException {
    super(new GridLayout(1, 0));


    List<LocalRepo> localRepos = Localizer.getRepositories();

    List<String> repoNames = new ArrayList<String>();
    List<String> repoIds = new ArrayList<String>();
    List<String> repoVersion = new ArrayList<String>();
    Map<String, PrintWriter> outMap = new HashMap<String, PrintWriter>();

    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    InputStream is = classloader.getResourceAsStream("repos.txt");
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    String line = null;
    while ((line = reader.readLine()) != null) {
      String[] parts = line.split("\\|");
      repoNames.add(parts[0]);
      repoIds.add(parts[1] + "." + parts[2]);
      repoVersion.add(MavenUtils.retireve(parts[1], parts[2]));
    }

    final JTable table = new JTable(repoNames.size(), localRepos.size() + 4);
    table.setAutoCreateRowSorter(true);

    table.getColumnModel().getColumn(0).setHeaderValue("Repo Name");
    table.getColumnModel().getColumn(1).setHeaderValue("Id");
    table.getColumnModel().getColumn(2).setHeaderValue("Latest version");

    for (int i = 3; i < localRepos.size() + 3; i++) {
      PrintWriter writer =
          new PrintWriter("out/" + localRepos.get(i - 3).getName() + ".txt",
              "UTF-8");
      outMap.put(localRepos.get(i - 3).getRelativePath(), writer);
      TableColumn tc = table.getColumnModel().getColumn(i);
      tc.setHeaderValue(localRepos.get(i - 3).getRelativePath());
    }
    for (int i = 0; i < repoNames.size(); i++) {
      String actualRepo = repoIds.get(i);
      String actualVersion = repoVersion.get(i);
      table.setValueAt(repoNames.get(i), i, 0);
      table.setValueAt(actualRepo, i, 1);
      table.setValueAt(actualVersion, i, 2);
      for (int j = 0; j < localRepos.size(); j++) {
        String version = localRepos.get(j).getDependencyMap().get(actualRepo);
        table.setValueAt(version, i, j + 3);
        if (version != null && !version.equals(actualVersion)) {
          outMap.get(localRepos.get(j).getRelativePath())
              .append("+ " + actualRepo + " should be upgraded from version " + version
                  + " to version " + actualVersion + "\n");
        }
      }
    }

    for (String key : outMap.keySet()) {
      outMap.get(key).close();
    }

    table.setPreferredScrollableViewportSize(new Dimension(1000, 700));
    table.setFillsViewportHeight(true);

    // Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    FixedColumnTable fct = new FixedColumnTable(3, scrollPane);

    // Add the scroll pane to this panel.
    add(scrollPane);
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be invoked from the
   * event-dispatching thread.
   * 
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  private static void createAndShowGUI()
      throws SAXException, IOException, ParserConfigurationException {

    JFrame frame = new JFrame("SimpleTableDemo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    App newContentPane = new App();
    newContentPane.setOpaque(true);
    frame.setContentPane(newContentPane);

    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          createAndShowGUI();
        } catch (SAXException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (ParserConfigurationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });
  }
}
