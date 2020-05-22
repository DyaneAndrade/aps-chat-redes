package com.app.chat;

import java.awt.Desktop.Action;
import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

//ao invés de o chat trabalhar com mensagem do tipo string, trabalhará com objeto do tipo chatmessege
public class ChatMessege implements Serializable {

    private String name; //nome cliente
    private String text; //mensagem de texto
    private String nameReserved; //cliente mensagem reservada > cliente destino
    private Set<String> setOnlines = new HashSet<String>(); //clientes onlines no servidor em tempo de execução
    private Action action; //para cada mensaegm enviada ao servidor, ele irá dizer qual ação o cliente deseja executar
    private File file;

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameReserved() {
        return nameReserved;
    }

    public void setNameReserved(String nameReserved) {
        this.nameReserved = nameReserved;
    }

    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    /*ações do cliente - conexão > sair > enviar mensagem reservada > mensagem para todos > atualiza lista
    conjunto fixo de constantes, lista de valores pré definidos (static final)
    o servidor responde utilizando uma dessas ações*/
    public enum Action {

        CONNECT, DISCONNECT, SEND_ONE, SEND_ALL, USERS_ONLINE, FILE_ONE, FILE_ALL

    }
}
