package view.jview;

import Objects.FileWithNeededBaza8Sklad;
import Objects.FileWithOstatki;
import Objects.FileWithOstatkiBaza8Sklad;
import Objects.FileWithOstatkiCentralSklad;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainForm extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JTextArea textArea3;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JTable table1;
    private JPanel tabxls;
    private JPanel tabfortransfer;
    private JButton OKButton;

    public MainForm() throws HeadlessException {
        super("Albert"); //Заголовок окна
        setBounds(50, 50, 1600, 900); //Если не выставить
        //размер и положение
        //то окно будет мелкое и незаметное
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //это нужно для того чтобы при
        //закрытии окна закрывалась и программа,
        //иначе она останется висеть в процессах

    }

    public void action() {

//        JPanel panelxlsFiles = new JPanel();

//        panelxlsFiles.setLayout(new GridLayout(0, 3));

        FileWithOstatki fileWithOstatkiCentralny = new FileWithOstatkiCentralSklad();
        textArea1.setText(fileWithOstatkiCentralny.getFileName());
        // TextArea nameCentralFile = new TextArea("ц  " + fileWithOstatkiCentralny.getFileName());
//        panelxlsFiles.add(textArea1);

        FileWithOstatki fileWithOstatkiBaza8 = new FileWithOstatkiBaza8Sklad();
        textArea2.setText(fileWithOstatkiBaza8.getFileName());
        // TextArea nameBaza8File = new TextArea("b8" + fileWithOstatkiBaza8.getFileName());
 //       panelxlsFiles.add(textArea2);

        FileWithOstatki fileWithNeededOstatkiBaza8 = new FileWithNeededBaza8Sklad();
        textArea3.setText(fileWithNeededOstatkiBaza8.getFileName());
        // TextArea nameNeededBaza8File = new TextArea("o+" + fileWithNeededOstatkiBaza8.getFileName());
 //       panelxlsFiles.add(textArea3);


        //  JButton buttonOstatkiCentral = new JButton("Центральному складу");
        button1.setText("Центральному складу");
//        panelxlsFiles.add(button1);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser(fileWithOstatkiCentralny.getPATHTOFILES());
                fileopen.setFileFilter(new FileNameExtensionFilter("Excell", "xls"));
                int ret = fileopen.showDialog(null, "Открыть файл с остатками по Центральному");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    fileWithOstatkiCentralny.setFile(file);

                    textArea1.setText(fileWithOstatkiCentralny.getFileName());

                }
            }
        });


        //JButton buttonOstatkiBaza8 = new JButton("Сантехбаза8");
        button2.setText("Сантехбаза8");
 //       panelxlsFiles.add(button2);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser(fileWithOstatkiBaza8.getPATHTOFILES());
                fileopen.setFileFilter(new FileNameExtensionFilter("Excell", "xls"));
                int ret = fileopen.showDialog(null, "Открыть файл с остатками по Сантехбазе");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    fileWithOstatkiBaza8.setFile(file);

                    textArea2.setText(fileWithOstatkiBaza8.getFileName());

                }
            }
        });


        //  JButton buttonNeededOstatkiBaza8 = new JButton("Файл необходимого остатка по Сантехбаза8");
        button3.setText("Файл необходимого остатка по Сантехбаза8");
//        panelxlsFiles.add(button3);
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser(fileWithNeededOstatkiBaza8.getPATHTOFILES());
                fileopen.setFileFilter(new FileNameExtensionFilter("Excell", "xls"));
                int ret = fileopen.showDialog(null, "Открыть файл со спиком нужного количества");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    fileWithNeededOstatkiBaza8.setFile(file);

                    textArea3.setText(fileWithNeededOstatkiBaza8.getFileName());
                    /*
                     * Какие-то действия.
                     */
                }
            }
        });

        button4.setText("Расчет перемещений и заказа");
 //       panelxlsFiles.add(button4);


//        tabbedPane1.addTab("XLS FORM", panelxlsFiles);
        tabbedPane1.addTab("save", new

                JPanel());
        tabbedPane1.addTab("save", new

                JPanel());

        add(tabbedPane1);
//        OKButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                JFileChooser fileopen = new JFileChooser();
//                int ret = fileopen.showDialog(null, "Открыть файл");
//                if (ret == JFileChooser.APPROVE_OPTION) {
//                    File file = fileopen.getSelectedFile();
//                    /*
//                     * Какие-то действия.
//                     */
//                }
//            }
//        });


    }

    public void setData(dataForTransferBean data) {
    }

    public void getData(dataForTransferBean data) {
    }

    public boolean isModified(dataForTransferBean data) {
        return false;
    }


}
