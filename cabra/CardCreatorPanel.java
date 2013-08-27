package cabra;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Neel
 */
//import java.beans.PropertyChangeListener;
import static cabra.CardCreatorPanel.ANSWER_WIDTH;
import static cabra.CardCreatorPanel.QUESTION_WIDTH;
import static cabra.CardCreatorPanel.TAB_SIZE;
import static cabra.CardCreatorPanel.TEXT_AREA_HEIGHT;
import cabra.SpringUtilities;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//import java.util.ArrayList;

public class CardCreatorPanel extends JPanel {
    //allows you to create a new card and add it to the project

    private JTextArea questionArea; //where question is typed
    private JTextArea answerArea; //where answer is typed  
    private JTextField wrongAnswerNo1 = new JTextField(20);
    private JTextField wrongAnswerNo2 = new JTextField(20);
    private JTextField wrongAnswerNo3 = new JTextField(20);
    private JTextField rightAnswer = new JTextField(20);
    private boolean isMultiple;
    private JButton createCard; //the button that's pressed to create a card
    // private JButton clear; //clears the question and answer text areas
    // private JCheckBox important; //gives you the option of making a question important, thereby making two copies of that card
    private PicturePreview preview; //shows a preview of the image
    private String picturePath = ""; //the path to the uploaded picture
    private JButton clearPictureField; //a button that clears the picture field
    private JButton radioBox;
    private JButton textType;
    private Controller controller;
    private TabPane tabPane; //needed to react to card creation events
    private GUI gui;
    public static final int MY_WIDTH = 380;
    public static final int MY_HEIGHT = 450;
    //for text areas
    public static final int TEXT_AREA_HEIGHT = 116;
    public static final int ANSWER_WIDTH = 325;
    public static final int QUESTION_WIDTH = 210;
    public static final int TAB_SIZE = 2; //the actual tab size is about twice this

    public CardCreatorPanel(TabPane tabPane, GUI gui, Controller controller) {
        this.tabPane = tabPane;
        this.gui = gui;
        this.controller = controller;

        setPreferredSize(new Dimension(MY_WIDTH, MY_HEIGHT));

        questionArea = createTextArea(true);
        answerArea = createTextArea(false);
        addBindings();

        createCard = new JButton();
        radioBox = new JButton();
        textType = new JButton();
    }

    public void createPanel() {
        //called when the panel needs to be set up
        add(new JLabel("<html><b>Question:</b>"));

        JPanel questionHolder = new JPanel();
        //create a scroll pane for question area
        JScrollPane questionScroller = new JScrollPane(questionArea);
        questionScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        questionScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        questionHolder.add(questionScroller);
        //add(createPictureUploader());

        //create picture uploader
        ImageChooser chooser;

        //a preview panel for the image
        preview = new PicturePreview();

        //JButton for inserting picture
        final JButton insertPicture = new JButton(GUI.createImageIcon("insertimage.png"));
        insertPicture.setToolTipText("Add a picture to this flashcard");
        insertPicture.addActionListener(new AddPictureListener());

        //JButton for clearing contents of text box
        clearPictureField = new JButton(GUI.createImageIcon("eraser.png"));
        clearPictureField.setToolTipText("Remove the picture from this flashcard");
        clearPictureField.setEnabled(false); //nothing in the text box
        clearPictureField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //reset contents of the image preview field
                preview.updatePicture(null);
                picturePath = "";
                clearPictureField.setEnabled(false); //disable it because there's no picture
                insertPicture.requestFocus();
            }
        });

        //finally assemble imagechooser
        chooser = new ImageChooser(insertPicture, clearPictureField, preview);

        questionHolder.add(chooser);
        add(questionHolder);

        final JPanel choseTypeOfAnswer = new JPanel();
        choseTypeOfAnswer.setLayout(new SpringLayout());

        choseTypeOfAnswer.add(textType);
        textType.setToolTipText("Type of answer: Text");
        textType.setIcon(GUI.createImageIcon("check.png"));
        textType.setAlignmentX(Component.CENTER_ALIGNMENT);
        textType.setPreferredSize(new Dimension(30, 20));
        textType.setVisible(true);

        final JPanel choseTypeOfAnswerText = new JPanel();
        choseTypeOfAnswerText.setLayout(new BorderLayout());
        final JPanel choseTypeOfAnswerRadio = new JPanel();


        textType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                choseTypeOfAnswerRadio.setVisible(false);
                JLabel label = new JLabel("<html><b>Answer:</b>");

                choseTypeOfAnswerText.add(label, BorderLayout.PAGE_START);
                //now a scroll pane for the answer area
                JScrollPane answerScroller = new JScrollPane(answerArea);
                answerScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                answerScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                choseTypeOfAnswerText.add(answerScroller);
                //add(answerScroller);
                choseTypeOfAnswerText.setVisible(true);
                add(choseTypeOfAnswerText);


                JPanel bottomContainer = new JPanel();
                bottomContainer.setLayout(new BorderLayout());

                createCard.setText("Finish and add card");
                createCard.setIcon(GUI.createImageIcon("check.png"));
                createCard.setAlignmentX(Component.CENTER_ALIGNMENT);
                createCard.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                createCard.setPreferredSize(new Dimension(180, 45));
                createCard.addActionListener(new cardListener());
                bottomContainer.add(createCard);

                //now add the bottom container
                add(bottomContainer, BorderLayout.SOUTH);

            }
        });
        /*here is the code that we added. The user can 
        chose between 2 types of answer: answer in text and multiple choice */
        choseTypeOfAnswer.add(radioBox);
        radioBox.setToolTipText("Type of answer: Multiple Choice");
        radioBox.setIcon(GUI.createImageIcon(""));
        radioBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        radioBox.setPreferredSize(new Dimension(30, 20));
        radioBox.setVisible(true);

        radioBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                choseTypeOfAnswerText.setVisible(false);

                GroupLayout layout = new GroupLayout(choseTypeOfAnswerRadio);
                choseTypeOfAnswerRadio.setLayout(layout);
                choseTypeOfAnswerRadio.setAlignmentX(Component.BOTTOM_ALIGNMENT);

                layout.setAutoCreateGaps(true);
                layout.setAutoCreateContainerGaps(true);

                JLabel label = new JLabel("<html><b>Wrong answer no1:</b>");
                label.setDisplayedMnemonic(KeyEvent.VK_N);
                
                JLabel label1 = new JLabel("<html><b>Wrong answer no2:</b>");
                label.setDisplayedMnemonic(KeyEvent.VK_N);

                JLabel label2 = new JLabel("<html><b>Wrong answer no3:</b>");
                label.setDisplayedMnemonic(KeyEvent.VK_N);
                
                JLabel label3 = new JLabel("<html><b>Right answer:</b>");
                label.setDisplayedMnemonic(KeyEvent.VK_N);
                
                GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

                hGroup.addGroup(layout.createParallelGroup().
                        addComponent(label).addComponent(label1).addComponent(label2).addComponent(label3));
                hGroup.addGroup(layout.createParallelGroup().
                        addComponent(wrongAnswerNo1).addComponent(wrongAnswerNo2).addComponent(wrongAnswerNo3).addComponent(rightAnswer));
                layout.setHorizontalGroup(hGroup);

                GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

                vGroup.addGroup(layout.createParallelGroup().
                        addComponent(label).addComponent(wrongAnswerNo1));
                vGroup.addGroup(layout.createParallelGroup().
                        addComponent(label1).addComponent(wrongAnswerNo2));
                vGroup.addGroup(layout.createParallelGroup().
                        addComponent(label2).addComponent(wrongAnswerNo3));
                vGroup.addGroup(layout.createParallelGroup().
                        addComponent(label3).addComponent(rightAnswer));
                layout.setVerticalGroup(vGroup);


                choseTypeOfAnswerRadio.setVisible(true);
                add(choseTypeOfAnswerRadio);

                JPanel bottomContainer = new JPanel();
                bottomContainer.setLayout(new BorderLayout());

                createCard.setText("Finish and add card");
                createCard.setIcon(GUI.createImageIcon("check.png"));
                createCard.setAlignmentX(Component.CENTER_ALIGNMENT);
                createCard.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                createCard.setPreferredSize(new Dimension(180, 45));
                createCard.addActionListener(new CardCreatorPanel.cardListener2());
                bottomContainer.add(createCard);

                //now add the bottom container
                add(bottomContainer, BorderLayout.SOUTH);

            }
        });
        SpringUtilities.makeCompactGrid(choseTypeOfAnswer, 1,
                choseTypeOfAnswer.getComponentCount(),
                6, 6, 6, 6);
        add(choseTypeOfAnswer);



    }//TO DO one panel or another must be displayed, not both

    public void refresh() {
        /*if(tabPane.getActiveProject() == null){
         //there is no active project
         //important.setEnabled(false);
         createCard.setEnabled(false);
         createCard.setText("You need to set a project as active.");
         createCard.setIcon(GUI.createImageIcon("x.png"));
         }
         else{*/
        //there is an active project
        //important.setEnabled(true);
        //}
    }

    private void clearFields() {
        //empty the question and answer fields
        questionArea.setText("");
        answerArea.setText("");
        wrongAnswerNo1.setText("");
        wrongAnswerNo2.setText("");
        wrongAnswerNo3.setText("");
        rightAnswer.setText("");
        preview.updatePicture(null);
        picturePath = "";
        clearPictureField.setEnabled(false);
        questionArea.requestFocus();
    }

    /**
     * Automates the creation of a text area (question or answer.)
     *
     * @param isQuestion true if it's the question area, false otherwise
     * @return the created JTextArea
     */
    private JTextArea createTextArea(boolean isQuestion) {
        if (isQuestion) {
            return createTextArea(QUESTION_WIDTH, TEXT_AREA_HEIGHT);
        } else {
            return createTextArea(ANSWER_WIDTH, TEXT_AREA_HEIGHT);
        }
    }

    private JTextArea createTextArea(int width, int height) {
        JTextArea textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(width, height));

        textArea.setLineWrap(true);
        textArea.setTabSize(TAB_SIZE);
        textArea.setWrapStyleWord(true);
        textArea.setFont(FontManager.PREFERRED_FONT);

        return textArea;
    }

    /**
     * Adds bindings to the text areas.
     *
     */
    private void addBindings() {

        //question area: Tab to jump to answer area       
        InputMap inputMap = questionArea.getInputMap(); //we'll add bindings here
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                answerArea.requestFocus();
            }
        };
        action.putValue(Action.NAME, "Jump to answer text area");
        inputMap.put(key, action);

        //answer area: Tab to add the card (click the button) 
        inputMap = answerArea.getInputMap(); //we'll add bindings here
        key = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                createCard.doClick();
            }
        };
        action.putValue(Action.NAME, "Jump to answer text area");
        inputMap.put(key, action);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Utils.drawEmblem(this, g);
    }

    private class ChangeTypeListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //empty panel of the button
            //  removeAll(); //that's the showAnswer JButton
            //mainPanel.add(knowPanel);
            //forward.setEnabled(false); 
            //repaint();
            //validate();
            //add(BorderLayout.CENTER,scroller); //add the text area (update() already put text in it)
        }
    }

    class AddPictureListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //get an image
            java.io.File imageFile = gui.requestImageFile();
            if (imageFile != null) {
                //update text in picture field to file's absolute path
                picturePath = imageFile.getAbsolutePath();
                preview.updatePicture(GUI.createImageIconFromFullPath(picturePath));
                //make the clear button enabled
                clearPictureField.setEnabled(true);
            }
            /*//turn it into an image
             ImageIcon imageToInsert = GUI.createImageIconFromFullPath(imageFile.getAbsolutePath()); 
             //resize it
             imageToInsert = new ImageIcon(GUI.scaleImage(imageToInsert.getImage(), 80, 80)); 
            
             //set picture on image button
             addPictureButton.setIcon(imageToInsert);
             addPictureButton.setToolTipText(imageFile.getAbsolutePath()); //store path to file in here
             */
        }
    }

    class cardListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //get text from the boxes
            String question = questionArea.getText();
            String answer = answerArea.getText();
            String picture = picturePath;


            //empty is no good!

            if (answer.trim().equals("") && question.trim().equals("")) {
                //nothing at all entered
                questionArea.requestFocus();
                return;
            }

            if (answer.trim().equals("")) {
                answerArea.requestFocus();
                return;
            }

            if (question.trim().equals("") && picture.equals("")) {
                //no question or picture; you're allowed to have just picture or just question
                questionArea.requestFocus();
                return;
            }

            //done checking, make card
            if (picture.equals("") == false) {
                //check that there's actually picture text and that it's a picture (proper extension)
                //they chose to upload a picture, so create a card with a picture
                controller.addCardToActiveProject(new Card(question, answer, picture, false));
            } else {
                //no picture
                controller.addCardToActiveProject(new Card(question, answer, false));
            }

            //reset for the next card
            clearFields();
        }
    }
    
    //the actionlistener for the multiple choice type of answer
        class cardListener2 implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //get text from the boxes
            String question = questionArea.getText();
            
            String picture = picturePath;
            String wrong1 = wrongAnswerNo1.getText();
            String wrong2 = wrongAnswerNo2.getText();
            String wrong3 = wrongAnswerNo3.getText();
            String right = rightAnswer.getText();

            //empty is no good!

            if (question.trim().equals("") && wrong1.trim().equals("") && wrong2.trim().equals("")
                    && wrong3.trim().equals("") && right.trim().equals("")) {
                //nothing at all entered
                questionArea.requestFocus();
                return;
            }

            if (wrong1.trim().equals("") && wrong2.trim().equals("")
                    && wrong3.trim().equals("")) {
                wrongAnswerNo1.requestFocus();
                return;
            }
            
            if (right.trim().equals("")) {
                rightAnswer.requestFocus();
                return;
            }

            if (question.trim().equals("") && picture.equals("")) {
                //no question or picture; you're allowed to have just picture or just question
                questionArea.requestFocus();
                return;
            }

            //done checking, make card
            if (picture.equals("") == false) {
                //check that there's actually picture text and that it's a picture (proper extension)
                //they chose to upload a picture, so create a card with a picture
                controller.addCardToActiveProject(new Card(question, wrong1, wrong2,
                        wrong3, right, picture, true));
            } else {
                
                //no picture
                controller.addCardToActiveProject(new Card(question,  wrong1, wrong2,
                        wrong3, right, true));
            }

            //reset for the next card
            clearFields();
        }
    }
    /**
     * Strictly for layout.
     *
     */
    private class ImageChooser extends JPanel {

        public ImageChooser(JButton choose, JButton clear, PicturePreview preview) {
            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            //put buttons side by side
            JPanel buttonContainer = new JPanel();
            buttonContainer.add(choose);
            buttonContainer.add(clear);

            add(preview);
            add(buttonContainer);

        }
    }

    private class PicturePreview extends JPanel {

        public static final int MY_WIDTH = 70;
        public static final int MY_HEIGHT = 70;
        private ImageIcon image;

        public PicturePreview() {
            setPreferredSize(new Dimension(MY_WIDTH, MY_HEIGHT));
        }

        /**
         * Updates the image.
         *
         * @param image the new image
         */
        public void updatePicture(ImageIcon image) {
            this.image = image;

            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (image == null) {
                //g.setFont(FontManager.PREFERRED_FONT); 
                //draw text
                g.drawString("Add a picture...", 10, getWidth() / 2 - 12);
            } else {
                //resize picture to appropriate size
                ImageIcon newPicture = GUI.scaleImage(image, MY_WIDTH, MY_HEIGHT);

                int width = newPicture.getIconWidth();
                int height = newPicture.getIconHeight();

                //get offsets in order to center picture
                int verticalOffset = (getHeight() - height) / 2;
                int horizontalOffset = (getWidth() - width) / 2;
                //if the image is thinner than this panel, offset it

                //paint the picture
                g.drawImage(newPicture.getImage(), horizontalOffset, verticalOffset, this);
                //offsets let the image be centered
            }
        }
    }
    /*
     class clearListener implements ActionListener{
     public void actionPerformed(ActionEvent e){
     //clear the question and answer fields
     clearFields();
     }
     }*/
}
