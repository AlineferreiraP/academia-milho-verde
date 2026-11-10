/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.dao;

import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;

// Programa de uso único: copia a lista de clientes de dentro do academia-antigo.json
// para o clientes.json, que é o arquivo usado pelo ClienteDAOJson.
public class MigrarClientes {

    public static void main(String[] args) throws Exception {
        Path origem = Path.of("academia.json");
        Path destino = Path.of("clientes.json");

        // Não sobrescreve um clientes.json que já tenha dados.
        if (Files.exists(destino)) {
            System.out.println("clientes.json já existe; nada foi feito.");
            return;
        }

        JSONObject raiz = new JSONObject(Files.readString(origem));
        // O formato dos clientes é o mesmo nos dois arquivos.
        Files.writeString(destino, raiz.getJSONArray("clientes").toString(2));
        System.out.println("Clientes copiados: " + raiz.getJSONArray("clientes").length());
    }
}