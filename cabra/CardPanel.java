/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** A small panel with a card's information on it
 *
 * @author Neel
 */
public class CardPanel extends JPanel{
    
    private JFrame frame; //GUI's frame
    private Controller controller;
    
    /** The project that this panel's card is part of.
     * 
     */
    private Project project;

    /** The card that this panel represents.
     * 
     */
    private Card card; 
    
    //COMPONENTS
    /** The card's picture
     * 
     */
    private Picture picture;
    private JTextField question;
    private JTextField answer;
    private JTextField wrongText1;
    private JTextField wrongText2;
    private JTextField wrongText3;
    private JTextField rightText;
    private boolean multiple;
    
    /** Label with text on rank and reps left
     * 
     */
    private JLabel rankInfo;
    
    //constants
    private static final int TEXT_FIELD_WIDTH = 190; //width (px) of text field
    public static final int MAX_WIDTH = 310;
    public static final int MAX_HEIGHT = 80;
    
    //METHODS
    
    /** Creates a CardPanel.
     * 
     * @param card the card this panel represents
     * @param owner the project that owns that card
     */
    public CardPanel(Card card,Project owner,Controller controller,JFrame frame){
        super(new BorderLayout());
        
        this.frame = frame;
        this.controller = controller;
        this.project = owner;
        this.multiple = card.getIsMultiple();
        buildComponents();
        assemble();
        setCard(card,owner);
        
        setBorder(BorderFactory.createLineBorder(Color.black));       
    }
  
    /** Changes the card that this panel represents.
     * 
     * @param card the new card
     * @param owner the project that owns the card
     */
    public void setCard(Card card, Project owner){
        this.card = card;
        //update picture area
        if(card.hasPicture()){
            //make an image and show it
            picture.updatePicture(owner.getImageIcon(card.getPictureName()));
        }
        else{
            //no picture
            picture.updatePicture(null);
        }
            question.setText(Card.replaceNewlines(card.getQuestion()));
            question.setCaretPosition(0);
        //and the text
        //replace newlines with -nl-'s; once the card is saved it'll get reverted to normal
        if(multiple == false){
            answer.setText(Card.replaceNewlines(card.getAnswer()));
            
        //setting the text moves the caret to the far left; move it back to the front
            answer.setCaretPosition(0);
        } else {
            if(wrongText1 != null){//performing nullcheck because user can enter less than 3 wrong answers
            wrongText1.setText(Card.replaceNewlines(card.getRadioWrongAnswer()));
            wrongText1.setCaretPosition(0);
            }
            if(wrongText2 != null){//performing nullcheck because user can enter less than 3 wrong answers
            wrongText2.setText(Card.replaceNewlines(card.getRadioWrongAnswer2()));
            wrongText2.setCaretPosition(0);
            }
            if(wrongText3 != null){//performing nullcheck because user can enter less than 3 wrong answers
            wrongText3.setText(Card.replaceNewlines(card.getRadioWrongAnswer3()));
            wrongText3.setCaretPosition(0);
            }
            if(rightText != null){//performing nullcheck because user can enter less than 3 wrong answers
            rightText.setText(Card.replaceNewlines(card.getRadioRightAnswer()));
            rightText.setCaretPosition(0);
            }
        }
        //rankInfo.setText("");
        
        setBackground(card.getStatus().getColor());
    }
    
    /**Builds the components of this panel.
     * 
     */
    private void buildComponents(){
        picture = new Picture();
        question = createTextField();
        if(multiple == false){
            answer = createTextField();
        } else {
            wrongText1 = createTextField();
            wrongText2 = createTextField();
            wrongText3 = createTextField();
            rightText = createTextField();
        }
        //rankInfo = new JLabel();
    }
    
    /** Creates the question or answer text field
     * 
     * @return the created text field
     */
    private JTextField createTextField(){
        Font font = FontManager.PREFERRED_FONT;
        JTextField text = new JTextField();
        text.setPreferredSize(new Dimension(TEXT_FIELD_WIDTH, font.getSize() + 15));
        text.setEditable(false);
        text.setFont(font);
        return text;
    }
    /** Assembles this panel.
     * 
     */
    private void assemble(){
        //picture first; then question; then answer; then tool buttons
        
        //create panel containing question and area
        JPanel questionHolder = new JPanel();
        JLabel q = new JLabel("Q:");
            q.setToolTipText("Question");
        questionHolder.add(q);
        questionHolder.add(question);
        //add components        
        add(BorderLayout.WEST,picture);
        JPanel textHolder = new JPanel();
            
        if(multiple == false){
            textHolder.setLayout(new GridLayout(2,1));
            textHolder.add(/*BorderLayout.NORTH,*/questionHolder);
            JLabel a = new JLabel("A:");
            a.setToolTipText("Answer");
            JPanel answerHolder = new JPanel();
            answerHolder.add(a);
            answerHolder.add(answer);
            textHolder.add(/*BorderLayout.SOUTH,*/answerHolder);
        } else {
            textHolder.setLayout(new GridLayout(5,1));
            textHolder.add(/*BorderLayout.NORTH,*/questionHolder);
            if(wrongText1 != null){
            JLabel a = new JLabel("A:");
            a.setToolTipText("Answer");
            JPanel wrongHolder = new JPanel();
            wrongHolder.add(a);
            wrongHolder.add(wrongText1);
            textHolder.add(/*BorderLayout.SOUTH,*/wrongHolder);
            }
            if(wrongText2 != null){
            JLabel b = new JLabel("A:");
            b.setToolTipText("Answer");
            JPanel wrongHolder2 = new JPanel();
            wrongHolder2.add(b);
            wrongHolder2.add(wrongText2);
            textHolder.add(/*BorderLayout.SOUTH,*/wrongHolder2);
            }
            if(wrongText3 != null){
            JLabel c = new JLabel("A:");
            c.setToolTipText("Answer");
            JPanel wrongHolder3 = new JPanel();
            wrongHolder3.add(c);
            wrongHolder3.add(wrongText3);
            textHolder.add(/*BorderLayout.SOUTH,*/wrongHolder3);
            }
            if(rightText != null){
            JLabel d = new JLabel("A:");
            d.setToolTipText("Answer");
            JPanel rightHolder = new JPanel();
            rightHolder.add(d);
            rightHolder.add(rightText);
            textHolder.add(/*BorderLayout.SOUTH,*/rightHolder);
            }//me pinaka an ginetai
        }
        
            //textHolder.add(rankInfo);
        add(BorderLayout.CENTER,textHolder);
        
        //add tools to the far left in a toolbar
        JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
        toolbar.setFloatable(false);
        
        JButton delete = new JButton(GUI.createImageIcon("trash.png"));
            delete.setToolTipText("Delete this card");
            delete.addActionListener(new DeleteListener());
            toolbar.add(delete);
        JButton edit = new JButton(GUI.createImageIcon("pencil.png"));
            edit.setToolTipText("Edit this card");
            edit.addActionListener(new EditListener());
            toolbar.add(edit);
        add(BorderLayout.EAST,toolbar);
        if(multiple == false){
            setPreferredSize(new Dimension(MAX_WIDTH,MAX_HEIGHT));
        } else {
            setPreferredSize(new Dimension(MAX_WIDTH, 200));
        }
    }
    

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Font font = FontManager.PREFERRED_FONT;
        g.setFont(font);
        g.drawString(card.getStatus().name(),getWidth() - font.getSize(),getHeight() - 8); //12x8 is dims of letter
    }
    
    /** Shows the picture.
     * 
     */
    private class Picture extends JPanel{
        
        public static final int MY_WIDTH = 50;
        public static final int MY_HEIGHT = 50;
        
        private ImageIcon image;
        
        public Picture(){
            setPreferredSize(new Dimension(MY_WIDTH,MY_HEIGHT));
        }
        
        /** Updates the image.
         * 
         * @param image the new image
         */
        public void updatePicture(final ImageIcon image){
            this.image = image;
            
            if(hasImage()){
                //show full size on click
                setToolTipText("Click to view full size");
                addMouseListener(new MouseAdapter(){
                   @Override
                   public void mouseClicked(MouseEvent e){
                       ImageManager.showImage(image, controller.getGUI().getFrame());
                   }
                });                
            }
            else{
                //remove tool tip text & mouse listener
                setToolTipText(null);
                if(getMouseListeners().length > 0)
                    removeMouseListener(getMouseListeners()[0]);
            }
            
            repaint();
        }
        
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            
            if(hasImage()){
                //resize picture to appropriate size
                ImageIcon newPicture = ImageManager.scaleImage(image, MY_WIDTH, MY_HEIGHT); 

                int width = newPicture.getIconWidth();
                int height = newPicture.getIconHeight();

                //get offsets in order to center picture
                int verticalOffset = (getHeight() - height) / 2;
                int horizontalOffset = (getWidth() - width) / 2;
                    //if the image is thinner than this panel, offset it

                //paint the picture
                g.drawImage(newPicture.getImage(),horizontalOffset,verticalOffset,this);
                    //offsets let the image be centered
            }
        }
        
        /**
         * Returns if this Picture actually has an image inside it.
         * @return 
         */
        private boolean hasImage(){
            return !(image == null || (image.getIconHeight() == -1 && image.getIconWidth() == -1));
        }
        
    }    
    
    // LISTENERS
    /** When trash is clicked.
     * 
     */
    private class DeleteListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            //get confirmation
            if(InputManager.confirm("Are you sure you want to delete this card?", frame)){
                project.removeCard(card);
                controller.refresh();
            }
        }
    }
    
    /** When the user wants to edit the card.
     * 
     */
    private class EditListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            //make things editable
                question.setEditable(true);
            if(multiple == false){
                answer.setEditable(true);
                //change the background color to let the user know it's editable
                Color bg = ColorManager.createColor("FFF4C1");
                question.setBackground(bg);
                answer.setBackground(bg);

            } else {
                wrongText1.setEditable(true);
                wrongText2.setEditable(true);
                wrongText3.setEditable(true);
                rightText.setEditable(true);
            }
                question.requestFocus();
            
            //turn the edit button into a save button and have it save the card when it's done
            JButton edit = (JButton)e.getSource(); //hackwork, but it works
            edit.setIcon(GUI.createImageIcon("floppy.png"));
            edit.setToolTipText("Save your changes");
            
            edit.removeActionListener(this); //don't let it edit once it's clicked
            edit.addActionListener(new SaveListener());
        }
    }
    
    /** After the user starts editing, they click this once they're done.
     * 
     */
    private class SaveListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            //edit card's info
            //newlines have been replaced with the -nl-; bring them back
            card.setQuestion(Card.bringBackNewlines(question.getText()));
            //make question/answer uneditable
            question.setEditable(false);
            //return the fields to original color
            question.setBackground(Color.white);
            
            if(multiple == false){
                card.setAnswer(Card.bringBackNewlines(answer.getText()));
                answer.setEditable(false);
                answer.setBackground(Color.white);
            } else {
                card.setRadioWrongAnswer(Card.bringBackNewlines(wrongText1.getText()));
                wrongText1.setEditable(false);
                wrongText1.setBackground(Color.white);
                card.setRadioWrongAnswer2(Card.bringBackNewlines(wrongText2.getText()));
                wrongText2.setEditable(false);
                wrongText2.setBackground(Color.white);
                card.setRadioWrongAnswer3(Card.bringBackNewlines(wrongText3.getText()));
                wrongText3.setEditable(false);
                wrongText3.setBackground(Color.white);
                card.setRadioRightAnswer(Card.bringBackNewlines(rightText.getText()));
                rightText.setEditable(false);
                rightText.setBackground(Color.white);
            }
            
            //save it
            project.saveCards();
            
            
            
            //make the button back into the edit button
            JButton edit = (JButton)e.getSource();
            edit.setIcon(GUI.createImageIcon("pencil.png"));
            edit.setToolTipText("Edit this card");
            
            edit.removeActionListener(this); //no more saving for now
            edit.addActionListener(new EditListener());         
        }
    }
}

