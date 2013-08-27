/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cabra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** A panel for the answer. See StudyTextPanel.
 * @see cabra.abstracts.StudyTextPanel.java
 *
 * @author Neel
 */
public class AnswerPanel extends cabra.abstracts.StudyTextPanel{

    private JButton showAnswer; //you can click it to show the answer
    private JTextField enterAnswer; //user enters answer here
    private JRadioButton wrong1 = new JRadioButton();
    private JRadioButton wrong2 = new JRadioButton();
    private JRadioButton wrong3 = new JRadioButton();
    private JRadioButton right = new JRadioButton();
    private Boolean isMultiple;
    
    public AnswerPanel(StudyPanel studyPanel,Controller controller,GUI gui){
        super(studyPanel,controller,gui);
        
        enterAnswer = new JTextField(18);
    }
    
    public void focusTextField(){
        enterAnswer.requestFocus();
        enterAnswer.requestFocusInWindow();
    }
    
    @Override
            public void update(Card card){
                if(card == null){
                    //no card being viewed...
                }
                else{
                    
                    isMultiple = card.getIsMultiple();
                    JPanel enterAnswerHolder = new JPanel();
                    if(isMultiple == false){
                        //set the text area's contents, we just won't show it yet
                        textArea.setText(card.getAnswer());  
                        enterAnswer.setText(""); 
                    } else {
                        textArea.setText(card.getRadioRightAnswer());
                        wrong1.setText(card.getRadioWrongAnswer());
                        wrong2.setText(card.getRadioWrongAnswer2());
                        wrong3.setText(card.getRadioWrongAnswer3());
                        right.setText(card.getRadioRightAnswer());
                    }
                    //a little different. Get rid of the text area & fill with a JButton that, when clicked, shows this card
                    //remove the scroller since that's what we put in
                    removeAll(); //takes care of everything
                    showAnswer = new JButton("Show answer",GUI.createImageIcon("eye.png"));
                    showAnswer.setMnemonic(KeyEvent.VK_S);
                    //GUI.makeLarge(showAnswer);
                    showAnswer.addActionListener(new ShowAnswerListener());

                    //this holds answer field
                        enterAnswerHolder.add(new JLabel("Your answer:"));
                    if(isMultiple == false){
                        enterAnswerHolder.add(enterAnswer);
                    } else {
                        if(!wrong1.getText().equals("")){
                        enterAnswerHolder.add(wrong1);
                        }
                        if(!wrong2.getText().equals("")){
                        enterAnswerHolder.add(wrong2);
                        }
                        if(!wrong3.getText().equals("")){
                        enterAnswerHolder.add(wrong3);
                        }
                        if(!right.getText().equals("")){
                        enterAnswerHolder.add(right);
                        }
                    }
                    add(BorderLayout.CENTER,enterAnswerHolder);
                    add(BorderLayout.SOUTH,showAnswer);

                    //refresh view
                    validate();
                    repaint();
                }                
            }
    
    private class ShowAnswerListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            //empty panel of the button
            removeAll(); //that's the showAnswer JButton
            
            add(BorderLayout.CENTER,scroller); //add the text area (update() already put text in it)
            validate(); //without this, the JButton doesn't go away
            repaint();
            
            //notify the study panel that the answer has now been viewed
            studyPanel.answerShown();
           
        } 
    }
}
