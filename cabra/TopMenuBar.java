/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

/**
 *
 * @author Neel
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static cabra.About.*;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TopMenuBar extends JMenuBar{
    //the menu bar that goes at the top of the screen
    
    private Controller controller; //we need to call events in the controller
    private GUI gui; //needed to call some methods
    
    private ThemeButtonCreator buttonCreator = new ThemeButtonCreator();  
    private JDialog settingsDialog;
    private JLabel pointsEarnedBadge;
    
    public static final int MS_TO_SHOW_BADGE = 2500;
    
    public TopMenuBar(final Controller controller,final GUI gui){
        this.controller = controller;
        this.gui = gui;

        //create JMenus
        
        //Settings
        JMenu options = new JMenu("Options");
            options.setMnemonic(KeyEvent.VK_S);
            JMenuItem settingsMenuItem = new JMenuItem("Settings",GUI.createImageIcon("settings.png"));
                settingsMenuItem.addActionListener(new ActionListener(){
                   public void actionPerformed(ActionEvent e){
                       settingsDialog = Utils.putPanelInDialog(
                            (new SettingsPanel(controller, gui)).getPanel(),
                            gui.getFrame(),
                            "Cabra Settings - changes autosaved",
                            "settings.png",
                            SettingsPanel.MY_WIDTH,
                            SettingsPanel.MY_HEIGHT
                        );
                       settingsDialog.setVisible(true);
                   }
                });
                options.add(settingsMenuItem);        
        
        //print
        //JMenu print = new JMenu("Print");
         //   print.setMnemonic(KeyEvent.VK_P);
            JMenuItem printCards = new JMenuItem("Print flashcards",GUI.createImageIcon("printer.png"));
                printCards.addActionListener(new ActionListener(){
                   public void actionPerformed(ActionEvent e){ 
                       Project project = InputManager.getProject("Which project's cards do you want to print?",
                               controller.getAllProjects().toArray(new Project[0]), gui.getFrame());
                       if(project != null){
                           project.print(controller);
                       }
                   }
                });
                options.add(printCards);
        
        //import and export
        JMenu Projects = new JMenu("Projects");
            Projects.setMnemonic(KeyEvent.VK_I);
            JMenuItem addProject = new JMenuItem("New project",GUI.createImageIcon("plus.png"));
                addProject.addActionListener(new addProjectListener());
                Projects.add(addProject);
            JMenuItem Import = new JMenuItem("Import a project",GUI.createImageIcon("import.png"));
                Import.addActionListener(new ImportListener());
                Projects.add(Import);
            JMenuItem export = new JMenuItem("Export a project",GUI.createImageIcon("export.png"));
                export.addActionListener(new ExportListener());
                Projects.add(export);     
            /*JMenuItem textImport = new JMenuItem("Text import",GUI.createImageIcon("import.png"));
                textImport.addActionListener(new TextImportListener());
                importExport.add(textImport);
            JMenuItem textExport = new JMenuItem("Text export",GUI.createImageIcon("export.png"));
                textExport.addActionListener(new TextExportListener());
                importExport.add(textExport);*/
            Projects.addSeparator(); 
            if(Desktop.isDesktopSupported()){
                Desktop desktop = Desktop.getDesktop();
                //ensure that you can browse
                if(desktop.isSupported(Desktop.Action.BROWSE)){
                    JMenuItem visit = new JMenuItem("Download projects online",GUI.createImageIcon("globe.png"));
                        visit.addActionListener(new ActionListener(){
                           public void actionPerformed(ActionEvent e){ 
                               Utils.browse("http://www.cabra.hathix.com/share.php" + TRACKING_URL_ADDON);
                           }
                        });
                        JMenuItem mobile = new JMenuItem("Get Cabra on your phone",GUI.createImageIcon("mobile.png"));
                        mobile.addActionListener(new ActionListener(){
                           public void actionPerformed(ActionEvent e){ 
                               Utils.browse("http://cabra.hathix.com/chevre.php" + TRACKING_URL_ADDON);
                           }
                        });
                        visit.setToolTipText("cabra.hathix.com/share");
                        mobile.setToolTipText("cabra.hathix.com/chevre");
                         
                        Projects.add(visit); 
                        Projects.add(mobile);              
                    }
            }       
        
        //enter code menu
        JMenu code = new JMenu("Codes");
            code.setMnemonic(KeyEvent.VK_C);
            JMenuItem enterCode = new JMenuItem("Enter a code",GUI.createImageIcon("barcode.png"));
                enterCode.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        String input = InputManager.getUserInput("Enter a 6-character code:", "" , true, gui.getFrame());
                        if(input != null){
                            CodeScanner.scan(input, controller);
                        }
                    }
                });
                code.add(enterCode);
            JMenuItem codesRedeemed = new JMenuItem("Codes redeemed",GUI.createImageIcon("check.png"));
                codesRedeemed.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        String rawCodeString = UserData.getString("Codes");
                        
                        String toShow;
                        if(rawCodeString.trim().length() == 0){
                            //no codes redeemed
                            toShow = "You haven't redeemed any codes yet.";
                        }
                        else{
                            toShow = "You have redeemed the following codes:<br><i>" + 
                                rawCodeString.replaceAll(" ", "<br>");                            
                        }
                        
                        Utils.showDialog(gui.getFrame(), 
                                "<html><center>" + toShow,
                                "Your redeemed codes");
                    }
                });
                code.add(codesRedeemed);            
            
        //cabra/help menu
        JMenu cabra = new JMenu("Cabra");
            cabra.setMnemonic(KeyEvent.VK_A);

            //desktop integration: open cabra's site in web browser
            //only do this if it's possible
            if(Desktop.isDesktopSupported()){
                Desktop desktop = Desktop.getDesktop();
                //ensure that you can browse
                if(desktop.isSupported(Desktop.Action.BROWSE)){
                    JMenuItem visit = new JMenuItem("Visit Cabra's website",GUI.createImageIcon("globe.png"));
                        visit.addActionListener(new ActionListener(){
                           public void actionPerformed(ActionEvent e){ 
                               Utils.browse("http://www.cabra.hathix.com/" + TRACKING_URL_ADDON);
                           }
                        });
                        visit.setToolTipText("getcabra.com");
                        cabra.add(visit); 
                        
                    /*JMenuItem help = new JMenuItem("View help online",GUI.createImageIcon("help.png"));
                       help.addActionListener(new ActionListener(){
                               public void actionPerformed(ActionEvent e){ 
                                   Utils.browse("http://www.cabra.hathix.com/help" + TRACKING_URL_ADDON);
                               }
                            });
                            help.setToolTipText("cabra.hathix.com/help");
                            /cabra.add(help);*/
                    //cabra.addSeparator();    
                    }
            }
            
            JMenuItem about = new JMenuItem("About Cabra " + About.VERSION,GUI.createImageIcon("about.png"));
                about.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        About.showDialog(gui.getFrame());
                    }
                });
                cabra.add(about);    
                
            JMenuItem updates = new JMenuItem("Check for updates", ImageManager.createImageIcon("update.png"));
                updates.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        Updates.checkForUpdates(gui);
                }});
                cabra.add(updates);
                
         pointsEarnedBadge = new JLabel(ImageManager.createImageIcon("coins-plus.png"));
         pointsEarnedBadge.setPreferredSize(new Dimension(60,16));
         pointsEarnedBadge.setVisible(false);           

         add(options);
         add(Projects);
         add(buttonCreator); //themes
         add(code);
         add(cabra);
         add(Box.createGlue());
         add(pointsEarnedBadge);
         refresh();
    }

    public void refresh(){
        buttonCreator.removeAll();
        for(Themes theme : Themes.getAllThemes()){
            if(theme != Themes.RANDOM && theme != Themes.RANDOM_COLORS && theme != Themes.SEASONS){
                //these themes dealt with separately
                if(theme.isUnlocked()){
                    buttonCreator.add(theme);
                }
                else{
                    buttonCreator.addLocked(theme);
                }
            }
        }
   
        buttonCreator.addSeparator();
        if(Themes.RANDOM_COLORS.isUnlocked())
            buttonCreator.add(Themes.RANDOM_COLORS);
        if(Themes.SEASONS.isUnlocked())
            buttonCreator.add(Themes.SEASONS);        
        if(Themes.RANDOM.isUnlocked())
            buttonCreator.add(Themes.RANDOM,"Random Theme"); //override the default displayed text, which would be "Random"
    }
    
    /**
     * Updates the badge on the right side of the menu to show how many points were just earned.
     * @param pointsEarned how many points were just earned
     */
    public void updatePointsEarnedBadge(int pointsEarned){
        pointsEarnedBadge.setText("" + pointsEarned);
        pointsEarnedBadge.setToolTipText("You just earned " + pointsEarned + " points!");
        pointsEarnedBadge.setVisible(true);
        
        new Thread(new Runnable(){
            public void run(){
                try{
                    Thread.sleep(MS_TO_SHOW_BADGE);                   
                }
                catch(InterruptedException e){
                    
                }
                finally{
                    pointsEarnedBadge.setVisible(false);
                }
            }
        }).start();
    }
    
    /** A convenience class for making theme buttons on the menu bar... it's a menu button itself
     * 
     */
    class ThemeButtonCreator extends JMenu{
        private ButtonGroup buttonGroup; //just a logical group
        private themeSelectionListener selectionListener; //listens to the selection of all the theme buttons
        
        public ThemeButtonCreator(){
            super("Themes");
            //this.setIcon(GUI.createImageIcon("shirts.png"));
            setMnemonic(KeyEvent.VK_T);
            
            buttonGroup = new ButtonGroup();
            selectionListener = new themeSelectionListener();
        }
        
        /**Adds a JMenuItem for the given theme.
         * 
         * @param theme the theme that will be activated when the button is clicked.
         * @param displayedText the text shown on the menu item
         */
        public void add(Themes theme,String displayedText){
            //get icon from theme
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(displayedText,theme.getImageIcon());
            menuItem.addActionListener(selectionListener); 
            menuItem.setActionCommand(theme.getName()); //the action command is identical to the theme name, most of the time
            buttonGroup.add(menuItem);
            add(menuItem);
        }
        
        /** An overload for add(Themes,String) - the String defaults to the theme name
         * 
         * @param theme the theme that will be activated when the button is clicked.
         */
        public void add(Themes theme){
            add(theme,theme.getName());
        }
        
        /**
         * Adds a "locked" placeholder to the menu. This is used when there's an unavailable theme.
         * @param theme the theme that's locked. Its icon will show up, grayed out.
         */
        public void addLocked(Themes theme){
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem("Locked", theme.getImageIcon());
            menuItem.setEnabled(false);
            menuItem.setToolTipText("This theme is locked; you need to earn more points to rank up and unlock it.");
            buttonGroup.add(menuItem);
            add(menuItem);            
        }
    }
    
    /** Tells the given theme to change the GUI's color.
     * 
     * @param theme the theme that's now active
     */
    
    public void setTheme(Themes theme){
        controller.setTheme(theme);
    }    
    //listeners down here
    
    class themeSelectionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            String themeName = e.getActionCommand(); //each button specified its own action command with the name of a theme
            setTheme(Themes.getThemeByName(themeName));
        }
    }
    private boolean confirmProjectOverwrite(String input,String whatToSay){
    //check if the name conflicts with any existing projects' names
        for(final Project project : controller.getAllProjects()){
            if(input.equals(project.getName())){
                //there's already a project with the same name
                //ask for confirmation "do you want to delete the old one?"
                String ask = whatToSay;   

                if(InputManager.confirm(ask, gui.getFrame()) == false){
                    //the user decided not to overwrite the old note
                    return false;
                }  
                else{
                    //the user decided to delete that project
                    controller.removeProject(project);           
                    //now refresh the list
                    //listModel.remove(listModel.indexOf(project)); 
                    return true;//no point looping through any more since we've found a match
                }
            }
        }
        //if you reached this point, no names conflict
        return true;
    }
    
    
    private class addProjectListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            //when you click on the add project button
            //create a pop-up box to show something in
            //requestInput();
            String input = InputManager.getUserInput("Name of project:","",true,gui.getFrame());
            if(input == null)
                return; //that means they canceled
            
            //is there overwrite? check for it
            if(confirmProjectOverwrite(input,
                "<html><center>A project named <b>" + input + "</b> already exists.<br>Do you want to delete the old project and replace it with an empty one?")){
                //now make a project with that
                controller.addProject(input,true);  //the controller takes care of the view           
                //controller will call addProject(project)
                
                //gain points
                controller.gainPoints(PointEnums.Activity.CREATE_PROJECT);
            }
        }
    }
    
    class ImportListener implements ActionListener{
        @Override
                public void actionPerformed(ActionEvent e){
            try {
                ImportExport.Import(controller, gui);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TopMenuBar.class.getName()).log(Level.SEVERE, null, ex);
            }
                }
    }
    
    class ExportListener implements ActionListener{
        @Override
                public void actionPerformed(ActionEvent e){
                    ImportExport.export(controller, gui);
                }
    }        
}

