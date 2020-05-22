package com.app.servidor.service;

import com.app.chat.ChatMessege;
import com.app.chat.ChatMessege.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>(); //todo cliente que se conectar ao servidor(chat), será adcionado nesta lista

    public ServidorService() {
        try {
            serverSocket = new ServerSocket(5555); //porta de conexão do cliente ao servidor
            System.out.println("Servidor on!");
            /*o serversockete sempre irá esperar  por novas conexões, enquanto o servidor estiver rodando
            é criado cada socket, thread, input e output para cada cliente*/

            while (true) {
                socket = serverSocket.accept(); //quando o cliente se conecta, o accept passa o socket que o cliente criou no app cliente, para var socket  
                new Thread(new ListenerSocket(socket)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //ouvinte do servidor 
    private class ListenerSocket implements Runnable {

        /*output - executam a saída e envio de mensagem do servidor 
        input - recebe as mensagens envia pelos clientes*/
        private ObjectOutputStream output;
        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //inicia a execução da thread
        @Override
        public void run() {

            ChatMessege message = null;

            try {
                //recebe as mensagens enviadas pelo cliente
                while ((message = (ChatMessege) input.readObject()) != null) {
                    Action action = message.getAction(); //recebe a action que está sendo enviada

                    //teste para saber qual mensagem está sendo enviada, e objeto de saída
                    if (action.equals(action.CONNECT)) {
                        boolean isConnect = connect(message, output);
                        //se o return for true, iremos adicionar os usuários na lista de onlines
                        if (isConnect) {
                            mapOnlines.put(message.getName(), output);
                            sendOnlines(); //após se conectar, envia a lista de usuários
                        }
                    } else if (action.equals(action.DISCONNECT)) {
                        disconnect(message, output); //recebe mensagem do botão sair do usuário e chama o método
                        sendOnlines(); //atualiza lista
                        return; //força a saída do while
                    } else if (action.equals(action.SEND_ONE)) {
                        sendOne(message);
                    } else if (action.equals(action.SEND_ALL)) {
                        ServidorService.this.sendAll(message);
                    } else if (action.equals(action.FILE_ONE)){
                        sendOne(message);
                    }else if (action.equals(action.FILE_ALL)){
                        ServidorService.this.fileAll(message);
                    }
                }
            } catch (IOException ex) {
                ChatMessege cm = new ChatMessege();
                cm.setName(message.getName());
                disconnect(cm, output);
                sendOnlines();
                System.out.println(message.getName() + " deixou o chat!");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(
                        ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //só consegue se conectar a lista, se for o primeiro e for diferente dos nomes da lista
    private boolean connect(ChatMessege message, ObjectOutputStream output) {

        if (mapOnlines.size() == 0) { //lista vazia. sem clientes
            message.setText("YES");
            send(message, output);
            return true;
        }
        //for para percorrer a lista / kv - chave/objeto atual
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) { //lista com usuários, percorrer a lista para não exibir usuários repetidos
            if (kv.getKey().equals(message.getName())) { //nome igual ao do usuário do chat
                message.setText("No");
                send(message, output);
                return false;
            } else {
                message.setText("Yes");
                send(message, output);
                return true; //nome diferente ao do usuário do chat
            }
        }   
        return false;
    }

    private void disconnect(ChatMessege message, ObjectOutputStream output) {
        mapOnlines.remove(message.getName()); //remove o cliente da lista
        message.setText("deixou o chat!");

        message.setAction(Action.SEND_ONE);
        sendAll(message); //avisar todos usuários, que a pessoa saiu da sala

        System.out.println("Users " + message.getName() + " saiu da sala");
    }

    private void sendOne(ChatMessege message) { //irá pegar o output do usuário da lista
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) { //pegar todos os usuários onlines, string - getkey. objectoutput - getvaleu
            if (kv.getKey().equals(message.getNameReserved())) {
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void send(ChatMessege message, ObjectOutputStream output) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendAll(ChatMessege message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) { //pegar todos os usuários onlines, string - getkey. objectoutput - getvaleu
            //se a chave for diferente do nome da pessoa que está enviando a mensagem, será enviado para o usuário da chave
            if (!kv.getKey().equals(message.getName())) {
                message.setAction(Action.SEND_ONE);
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
        private void fileAll(ChatMessege message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) { //pegar todos os usuários onlines, string - getkey. objectoutput - getvaleu
            //se a chave for diferente do nome da pessoa que está enviando a mensagem, será enviado para o usuário da chave
            if (!kv.getKey().equals(message.getName())) {
                message.setAction(Action.FILE_ONE);
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void sendOnlines() { //lista de usuários onlines

        Set<String> setNames = new HashSet<String>(); //uma lista do tipo set, para add os usuários onlines
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            setNames.add(kv.getKey()); //set os nomes
        }

        ChatMessege message = new ChatMessege();
        message.setAction(Action.USERS_ONLINE);
        message.setSetOnlines(setNames);

        //pegar todos os usuários onlines, string - getkey. objectoutput - getvaleu
        //enviar a lista, para cada usuário
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            message.setName(kv.getKey());
            try {
                kv.getValue().writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
