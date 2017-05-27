import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.Box;

@SuppressWarnings("serial")
public class SongLibrary extends JFrame{
    private Object[][] DATA;
    private JTable table;
    private String [] HEADERS = new String[] { "Song", "Artist", "Album", "Year" };
    private JButton delete, add;
    private String fileNamePath;
    private DefaultTableModel model;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame f = new SongLibrary();
                f.setVisible(true);
            }
        });
    }
    
    //Constructor
    private SongLibrary() {
        super("SongLibrary");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addCloseListener();//sets the close button to doExit();
        
        //Creates and Adds Table with model
        addTable();
        
        //Creates buttons
        theAddButton();
        theDeleteButton();
        
        //Create Box
        addBox();
        
        //Creates MenuBar with items
        addMenuBar();
        
        //Pack and Center
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void addCloseListener() {
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                doExit();
            }
        });
    }
    
    private void addTable() {
        DATA = new Object[0][0];
        table = new JTable(DATA,HEADERS);
        table.setPreferredScrollableViewportSize(new Dimension(500, 100));
        table.setFillsViewportHeight(true);
        model = new DefaultTableModel(DATA, HEADERS);
        table.setModel(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void theAddButton() {
        add = new JButton("Add");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.addRow(new Object[]{"","","",""});
                delete.setEnabled(true);
            }
        });
    }
    
    private void theDeleteButton() {
        delete = new JButton("Delete");
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int pos = table.getSelectedRow();
                if (pos == -1) { JOptionPane.showMessageDialog(new JFrame("Oops"),"No row selected"); }
                else {
                    model.removeRow(pos);
                }
                if(model.getRowCount()==0) {
                    delete.setEnabled(false);
                }
            }
        });
        delete.setEnabled(false);
    }
    
    private void addBox() {
        Box box = Box.createVerticalBox();
        box.setPreferredSize(new Dimension(90, 100));
        box.add(add);
        
        Dimension size = new Dimension(10, 10);
        box.add(new Box.Filler(size, size, size));
        
        box.add(delete);
        box.setBackground(Color.gray);
        add(box, BorderLayout.EAST);
    }

    private void addMenuBar() {
        JMenuBar mainMenuBar = new JMenuBar();
        mainMenuBar.add(addSongLibraryMenu());//method returns JMenu
        mainMenuBar.add(addTableMenu());//method returns JMenu
        setJMenuBar(mainMenuBar);
    }
    
    private JMenu addSongLibraryMenu() {
        JMenu menuLib = new JMenu("SongLibrary");
        JMenuItem about = new JMenuItem("About...");
        menuLib.add(about);
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(new JFrame("About"),
                        "SongLibrary \n by Evan Rose");
            }
        });
        JMenuItem exit = new JMenuItem("Exit");
        menuLib.add(exit);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doExit();
            }
        });
        return menuLib;
    }
    
    private JMenu addTableMenu() {
        JMenu menuTable = new JMenu("Table");
        
        JMenuItem newTable = new JMenuItem("New");
        menuTable.add(newTable);
        newTable.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        new JFrame("Exit Menu"),
                        "Clear all table data?", "Select an option",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (result == JOptionPane.YES_OPTION){
                    model.setRowCount(0);
                    delete.setEnabled(false);
                    setTitle("SongLibrary");
                }
            }
        });
        
        JMenuItem open = new JMenuItem("Open...");
        menuTable.add(open);
        open.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override
                    public void run() {
                    JFileChooser chooser = new JFileChooser();
                    int result = chooser.showOpenDialog( SongLibrary.this );
                    if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    model.setRowCount(0);
                    fileNamePath = file.getAbsolutePath();
                    try {
                        Scanner scn = new Scanner(new BufferedReader(new FileReader(file)));
                        String line;
                        int lineNum = 0;
                        while(scn.hasNextLine()) {
                            model.addRow(new Object[]{"","","",""});
                            line = scn.nextLine();
                            String [] temp = line.split(",");
                            for(int i = 0; i < 4; i++) {
                                model.setValueAt(temp[i], lineNum, i);
                            }
                            lineNum++;
                        }
                        if(lineNum > 0) {
                        delete.setEnabled(true);
                        }
                        scn.close();
                        setTitle("SongLibrary " + "[" + fileNamePath + "]");
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    }
                    }
                   });
            }
        });
        
        JMenuItem saveAs = new JMenuItem("Save As...");
        menuTable.add(saveAs);
        saveAs.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override
                    public void run() {
                    JFileChooser chooser = new JFileChooser();
                    int result = chooser.showSaveDialog( SongLibrary.this );
                    if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try{
                        PrintWriter printer = new PrintWriter(file);
                        for(int i = 0; i < model.getRowCount(); i++) {
                            for(int c = 0; c < 3; c++){
                            printer.print(model.getValueAt(i, c) + ",");
                            }
                            printer.println(model.getValueAt(i, 3));
                        }
                        printer.close();
                        System.out.println(file.getName()+" Saved.");
                    }catch(FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    }
                    }
                   });
            }
        });
        return menuTable;
    }
    
    private void doExit(){
        int result = JOptionPane.showConfirmDialog(
                new JFrame("Exit Menu"),
                "Do you want to exit?", "Confirm Quit",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.YES_OPTION)
            dispose();
    }
}
