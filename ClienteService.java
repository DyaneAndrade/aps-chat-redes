package com.app.cliente.service;

import com.app.chat.ChatMessege;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

//responsável por enviar as mensagens ao servidor service
public class ClienteService {
    private Socket socket;
    private ObjectOutputStream output;//mensagem saída
    
    //inicialização do socket
    public Socket connect() {
        try {
            this.socket = new Socket("localhost", 5555); //caso tenha máquina em rede, basta informar o IP da máquina que contenha servidor
            this.output = new ObjectOutputStream(socket.getOutputStream()); //inicialização do output passando o socket/output como parametro
        } catch (IOException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }
            return socket;
    }
    //o ouvinte socket cliente fica no Frame
    //envia mensagem ao servidor > send = enviar
    public void send(ChatMessege message){
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
