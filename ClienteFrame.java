package com.app.cliente.frame;

import com.app.chat.ChatMessege;
import com.app.chat.ChatMessege.Action;
import com.app.cliente.service.ClienteService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ClienteFrame extends javax.swing.JFrame {

    private Socket socket;
    private ChatMessege message;
    private ClienteService service;

    //método construtor
    public ClienteFrame() {
        initComponents();
    }

    //verificar se houve conexão
    private void connected(ChatMessege message) {
        if (message.getText().equals("No")) {
            this.textName.setText(""); //limpa o componete nome
            JOptionPane.showMessageDialog(this, "Conexão não realizada!\nTente novamente com um novo usuário.");
            return; //força saída do método
        }
        //caso haja conexão
        this.message = message;
        this.btnConnect.setEnabled(false); //desabilita
        this.textName.setEnabled(false); //desabilita

        this.btnExit.setEnabled(true);
        this.textAreaSend.setEnabled(true);
        this.textAreaReceive.setEditable(true);
        this.btnSend.setEnabled(true);
        this.btnClear.setEnabled(true);
        this.btnFile.setEnabled(true);

        JOptionPane.showMessageDialog(this, "Você está conectado no chat!");
    }

    private void disconnected() {
        this.btnConnect.setEnabled(true);
        this.textName.setEnabled(true);

        this.btnExit.setEnabled(false);
        this.textAreaSend.setEnabled(false);
        this.textAreaReceive.setEditable(false);
        this.btnSend.setEnabled(false);
        this.btnClear.setEnabled(false);
        this.btnFile.setEnabled(false);

        this.textAreaReceive.setText(""); //limpa
        this.textAreaSend.setText("");

        JOptionPane.showMessageDialog(this, "Você saiu do chat!");

    }

    private void receive(ChatMessege message) {
        this.textAreaReceive.append(message.getName() + " diz: " + message.getText() + "\n");
    }

    private void refleshOnlines(ChatMessege message) {
        System.out.println(message.getSetOnlines().toString());

        Set<String> names = message.getSetOnlines();

        names.remove(message.getName()); //remover o nome do usuário atual, da lista

        String[] array = (String[]) names.toArray(new String[names.size()]); //lista só aceita array

        this.listOnlines.setListData(array);
        this.listOnlines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //aceita uma única seleção, de nomes por vez
        this.listOnlines.setLayoutOrientation(JList.VERTICAL); //layout da lista
    }

    private void sendFile(ChatMessege message) {
        this.textAreaReceive.append("Você recebeu um arquivo de " + message.getName() + ".\n No diretório: C:\\downloads");
        try {
            FileInputStream fileInputStream = new FileInputStream(message.getFile());
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\downloads\\" + message.getFile().getName());
            
            //ajuda na manipulação de arquivos
            FileChannel fin = fileInputStream.getChannel();
            FileChannel fout = fileOutputStream.getChannel();
            long size = fin.size();
            fin.transferTo(0, size, fout); //salva o arquivo no HD
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClienteFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void ReadFile(ChatMessege message) {
        try {
            FileReader fileReader = new FileReader(message.getFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String linha;
            while ((linha = bufferedReader.readLine()) != null) {
                System.out.println(linha);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClienteFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class ListenerSocket implements Runnable {

        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {

                this.input = new ObjectInputStream(socket.getInputStream());

            } catch (IOException ex) {
                Logger.getLogger(ClienteFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {

            ChatMessege message = null;

            try {
                while ((message = (ChatMessege) input.readObject()) != null) {
                    Action action = message.getAction(); //pega a mensagem que foi recebida pela ouvinte

                    if (action.equals(Action.CONNECT)) {
                        connected(message);
                    } else if (action.equals(Action.DISCONNECT)) {
                        disconnected();
                        socket.close();
                    } else if (action.equals(Action.SEND_ONE)) {
                        receive(message);
                    } else if (action.equals(Action.USERS_ONLINE)) {
                        refleshOnlines(message);
                    } else if (action.equals(Action.FILE_ONE)) {
                        sendFile(message);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ClienteFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClienteFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        textName = new javax.swing.JTextField();
        btnConnect = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listOnlines = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaSend = new javax.swing.JTextArea();
        btnSend = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaReceive = new javax.swing.JTextArea();
        btnFile = new javax.swing.JToggleButton();
        textFile = new javax.swing.JTextField();
        btnSendFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Conectar"));

        btnConnect.setText("Conectar");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        btnExit.setText("Sair");
        btnExit.setEnabled(false);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConnect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExit)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConnect)
                    .addComponent(btnExit))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Onlines"));

        jScrollPane2.setViewportView(listOnlines);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        textAreaSend.setColumns(20);
        textAreaSend.setRows(5);
        textAreaSend.setEnabled(false);
        jScrollPane1.setViewportView(textAreaSend);

        btnSend.setText("Enviar");
        btnSend.setEnabled(false);
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        btnClear.setText("Limpar");
        btnClear.setEnabled(false);
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        textAreaReceive.setEditable(false);
        textAreaReceive.setColumns(20);
        textAreaReceive.setRows(5);
        textAreaReceive.setEnabled(false);
        jScrollPane3.setViewportView(textAreaReceive);

        btnFile.setText("...");
        btnFile.setEnabled(false);
        btnFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileActionPerformed(evt);
            }
        });

        textFile.setEnabled(false);
        textFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFileActionPerformed(evt);
            }
        });

        btnSendFile.setText(">");
        btnSendFile.setEnabled(false);
        btnSendFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnFile, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFile, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSendFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSend)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnSend)
                    .addComponent(btnFile)
                    .addComponent(textFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSendFile))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        String name = this.textName.getText();

        if (!name.isEmpty()) {
            this.message = new ChatMessege();
            this.message.setAction(Action.CONNECT);
            this.message.setName(name);

            this.service = new ClienteService();
            this.socket = this.service.connect();

            //Thread inicializará o ouvinte
            new Thread(new ListenerSocket(socket)).start();

            this.service.send(message);
        }
    }//GEN-LAST:event_btnConnectActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed

        ChatMessege message = new ChatMessege();
        message.setName(this.message.getName());
        message.setAction(Action.DISCONNECT); //set a ação desconecta
        this.service.send(message); //mensagem enviada ao servidor
        disconnected();

    }//GEN-LAST:event_btnExitActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        this.textAreaSend.setText("");
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        String text = this.textAreaSend.getText();
        String name = this.message.getName();

        this.message = new ChatMessege(); //limpar o message, para que não envie mensagem errada

        //verificar se tem usuário selecionado para mensagem reservada
        if (this.listOnlines.getSelectedIndex() > -1) { //sem seleção fica -1
            this.message.setNameReserved((String) this.listOnlines.getSelectedValue());
            this.message.setAction(Action.SEND_ONE);
            this.listOnlines.clearSelection(); //limpa a seleção da lista
        } else {
            this.message.setAction(Action.SEND_ALL);
        }

        if (!text.isEmpty()) { //só envia se o texto não estiver vazio
            this.message.setName(name);
            this.message.setText(text);

            this.textAreaReceive.append("Você disse: " + text + "\n");

            this.service.send(this.message); //método da classe clienteservice
        }
        this.textAreaSend.setText("");
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileActionPerformed
        JFileChooser file = new JFileChooser("Procurar arquivo");
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Texto", "txt");

        file.setFileFilter(filter);

        int retorno = file.showOpenDialog(this);

        if (retorno == JFileChooser.APPROVE_OPTION) {
            File directory = file.getSelectedFile();
            textFile.setText(directory.getPath());
            this.btnSendFile.setEnabled(true);
            this.message.setFile(file.getSelectedFile());
        }
    }//GEN-LAST:event_btnFileActionPerformed

    private void textFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFileActionPerformed

    private void btnSendFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendFileActionPerformed
        String text = this.textFile.getText();
        String name = this.message.getName();
        File file = this.message.getFile();

        this.message = new ChatMessege(); //limpar o message, para que não envie mensagem errada

        //verificar se tem usuário selecionado para mensagem reservada
        if (this.listOnlines.getSelectedIndex() > -1) { //sem seleção fica -1
            this.message.setNameReserved((String) this.listOnlines.getSelectedValue());
            this.message.setAction(Action.FILE_ONE);
            this.listOnlines.clearSelection(); //limpa a seleção da lista
        } else {
            this.message.setAction(Action.FILE_ALL);
        }

        if (!text.isEmpty()) { //só envia se o texto não estiver vazio
            this.message.setName(name);
            this.message.setText(text);
            this.message.setFile(file);

            this.textAreaReceive.append("Você enviou: " + text + "\n");

            this.service.send(this.message); //método da classe clienteservice
            ReadFile(message);
        }
        this.textAreaSend.setText("");
    }//GEN-LAST:event_btnSendFileActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnExit;
    private javax.swing.JToggleButton btnFile;
    private javax.swing.JButton btnSend;
    private javax.swing.JButton btnSendFile;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> listOnlines;
    private javax.swing.JTextArea textAreaReceive;
    private javax.swing.JTextArea textAreaSend;
    private javax.swing.JTextField textFile;
    private javax.swing.JTextField textName;
    // End of variables declaration//GEN-END:variables
}
