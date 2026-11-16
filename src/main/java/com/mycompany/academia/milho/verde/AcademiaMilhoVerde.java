/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.academia.milho.verde; // pacote raiz do projeto

import com.mycompany.academia.milho.verde.dao.DAO; // interface genérica do DAO
import com.mycompany.academia.milho.verde.dao.DAOMemoria; // DAO que guarda na memória
import com.mycompany.academia.milho.verde.dao.DaoException; // exceção do DAO
import com.mycompany.academia.milho.verde.model.Administrador; // perfil administrador
import com.mycompany.academia.milho.verde.model.Cliente; // cliente da academia
import com.mycompany.academia.milho.verde.model.Colaborador; // perfil colaborador
import com.mycompany.academia.milho.verde.model.ComparadorCliente; // compara clientes pelo CPF
import com.mycompany.academia.milho.verde.model.Funcionario; // superclasse dos funcionários
import com.mycompany.academia.milho.verde.model.GestaoClientes; // regras dos clientes
import java.io.IOException; // usada só para simular a causa de uma DaoException
import com.mycompany.academia.milho.verde.model.Sala;
import com.mycompany.academia.milho.verde.model.Aula;
import com.mycompany.academia.milho.verde.model.Agendamento;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
public class AcademiaMilhoVerde { // classe só com o main

    private AcademiaMilhoVerde() { // construtor privado: esta classe não deve ser instanciada
    } // fim do construtor

    public static void main(String[] args) { // ponto de entrada do programa
        System.out.println("=== Clientes: ==="); // título do teste
        Cliente teste = new Cliente(1, "Ana Souza", "Rua das Flores, 10",
                "(38) 99999-0000", "ana@gmail.com", "6604556811088d08"); // cria um cliente válido
        System.out.println(teste); // mostra o cliente (toString)
        teste.desativar(); // exclusão lógica
        System.out.println(teste); // agora aparece desativado
        try { // tenta uma alteração inválida
            teste.setEmail("   "); // e-mail vazio
        } catch (IllegalArgumentException erro) { // a validação recusou
            System.out.println("Erro: " + erro.getMessage()); // mostra a mensagem
        } // fim do try

        System.out.println("\n\n=== Funcionarios:  Colaborador e Administrador ==="); // título do teste
        Funcionario[] equipe = { // vetor do tipo da superclasse
            new Administrador(1, "Aline Ferreira Pena", "Rua da Glória, 187",
                    "(38) 99800-0001", "aline.pena@ufvjm.edu.br", "novoadmin123"), // um administrador
            new Colaborador(3, "Carlos Pereira", "Rua do Bonfim, 12",
                    "(38) 99800-0003", "carlos@academia.com", "carlos123") // um colaborador
        }; // fim do vetor
        for (Funcionario f : equipe) { // mesma chamada para todos
            System.out.println(f + " | financeiro: " + f.podeAcessarFinanceiro()); // cada um responde do seu jeito (polimorfismo)
        } // fim do for

        
        
        System.out.println("\n\n===  DaoException ==="); // título do teste
        try { // simula um erro de arquivo
            throw new DaoException("Erro ao ler clientes.json.",
                    new IOException("arquivo não encontrado")); // exceção encadeada
        } catch (DaoException erro) { // captura o erro do DAO
            System.out.println(erro.getMessage()); // mensagem do DAO
            System.out.println("Causa: " + erro.getCause().getMessage()); // erro original preservado
        } // fim do try
        
        

        System.out.println("\n\n===  DAO<T> e DAOMemoria<T> Genérico ==="); // título do teste
        DAO<Cliente> dao = new DAOMemoria<>(); // um DAO genérico, aqui guardando clientes
        dao.inserir(new Cliente(dao.gerarId(), "Ana Souza", "Rua das Flores, 10",
                "(38) 99999-0000", "ana@gmail.com", "6604556811088d08")); // recebe o ID 1
        dao.inserir(new Cliente(dao.gerarId(), "João Lima", "Av. Central, 5",
                "(38) 98888-1111", "joao@gmail.com", "11fc2f60793c0a00")); // recebe o ID 2
        Cliente ana = dao.buscarPorId(1); // busca pelo ID
        ana.setTelefone("(38) 90000-0000"); // altera o telefone
        dao.atualizar(ana); // avisa o DAO da alteração
        System.out.println(dao); // quantos registros há
        for (Cliente c : dao.listar()) { // percorre a cópia da lista
            System.out.println(c); // mostra cada cliente
        } // fim do for
        System.out.println(dao.buscarPorId(99)); // não existe: mostra null
        System.out.println("Remover 2: " + dao.remover(2)); // true: apagou
        System.out.println("Remover 2 de novo: " + dao.remover(2)); // false: já não existia

        System.out.println("\n=== ComparadorCliente e GestaoClientes ==="); // título do teste
        ComparadorCliente porCpf = new ComparadorCliente(); // compara pelo CPF
        System.out.println("Mesmo CPF? " + (porCpf.compare(ana, ana) == 0)); // true: zero quer dizer igual
        GestaoClientes gestao = new GestaoClientes(); // usa um DAOMemoria de clientes
        gestao.cadastrarCliente("Ana Souza", "Rua das Flores, 10", "(38) 99999-0000",
                "ana@gmail.com", "123.456.789-09"); // ID 1
        try { // mesmo CPF, escrito sem pontos
            gestao.cadastrarCliente("Ana Repetida", "Rua C", "(38) 90000-0000",
                    "outra@gmail.com", "12345678909"); // deve ser recusado
        } catch (IllegalArgumentException erro) { // o comparador achou o CPF igual
            System.out.println("Erro: " + erro.getMessage()); // "Já existe cliente com este CPF (ID 1)."
        } // fim do try
        gestao.removerCliente(1); // desativa
        gestao.reativarCliente(1); // reativa
        System.out.println(gestao.listarClientes()); // só a Ana, ativa
        System.out.println("Busca 'an': " + gestao.buscarPorNome("an")); // acha pelo começo do nome
        
        
        System.out.println("\n\n=== Salas disponíveis por modalidade: ==="); // título do teste
        Sala[] salas = { // as quatro salas do enunciado
           new Sala(1, Sala.SPINNING, 20, 30, 150), // sala 1
           new Sala(2, Sala.MUSCULACAO, 40, 25, 120), // sala 2
           new Sala(3, Sala.FIT_DANCE, 25, 25, 130), // sala 3
           new Sala(4, Sala.PILATES, 15, 35, 180) // sala 4
        }; // fim do vetor
        for (Sala s : salas) { // percorre o vetor
            System.out.println(s); // mostra cada sala
        } // fim do for
        try { // modalidade que não existe
            new Sala(5, "YOGA", 10, 30, 150); // deve ser recusada: YOGA não é modalidade da academia
        } catch (IllegalArgumentException erro) { // validação recusou
            System.out.println("Erro: " + erro.getMessage()); // "Modalidade inválida: YOGA."
        } // fim do try
          
    
                System.out.println("\n\n=== Aula: ==="); // título do teste
        DAO<Aula> aulas = new DAOMemoria<>(); // o mesmo DAO genérico, agora guardando aulas
        aulas.inserir(new Aula(aulas.gerarId(), salas[0],
                LocalDateTime.of(2026, 11, 16, 6, 0), "Rafa")); // spinning, segunda 16/11 às 6h
        aulas.inserir(new Aula(aulas.gerarId(), salas[0],
                LocalDateTime.of(2026, 11, 17, 6, 0), "Rafa")); // spinning, terça 17/11 às 6h
        for (Aula aula : aulas.listar()) { // percorre as aulas
            System.out.println(aula); // mostra cada aula
        } // fim do for
        System.out.println("Modalidade da aula 2: " + aulas.buscarPorId(2).getModalidade()); // vem da sala
        try { // aula sem sala
            new Aula(3, null, LocalDateTime.of(2026, 11, 18, 6, 0), "Rafa"); // deve ser recusada
        } catch (IllegalArgumentException erro) { // validação recusou
            System.out.println("Erro: " + erro.getMessage()); // "Informe a sala da aula."
        } // fim do try
        
    
                System.out.println("\n\n Agendamento: ====="); // título do teste
        Sala salaTeste = new Sala(1,Sala.SPINNING, 20, 30, 150); // sala 1
        Cliente clienteAg = new Cliente(1, "Ana Souza", "Rua A, 10", "38999990000", "ana@email.com", "abc123"); // cliente de teste
        Aula seg = new Aula(109, salaTeste, LocalDateTime.of(2026, 11, 16, 6, 0), "Rafa"); // segunda 16/11 às 6h
        Aula ter = new Aula(118, salaTeste, LocalDateTime.of(2026, 11, 17, 6, 0), "Rafa"); // terça 17/11 às 6h

        Agendamento ag1 = new Agendamento(1, clienteAg, List.of(ter, seg), "diaria", 30, "4111 1111 1111 1234"); // nasce preliminar
        System.out.println(ag1); // cartão aparece mascarado
        System.out.println("Primeira aula: " + ag1.getPrimeiraData() + " | prazo: " + ag1.calcularPrazoConfirmacao()); // 16/11 e 09/11
        System.out.println("Confirma em 06/11? " + ag1.confirmar(LocalDate.of(2026, 11, 6)) + " -> " + ag1.getStatus()); // true, CONFIRMADO
        System.out.println("Cancela em 12/11, devolve R$ " + ag1.cancelar(LocalDate.of(2026, 11, 12)) + " -> " + ag1.getStatus()); // 15.0, CANCELADO

        Agendamento ag2 = new Agendamento(2, clienteAg, List.of(seg), "MENSALIDADE", 150, "5555666677778888"); // outro agendamento
        System.out.println("Confirma em 15/11? " + ag2.confirmar(LocalDate.of(2026, 11, 15)) + " -> " + ag2.getStatus()); // false, CANCELADO (passou do prazo)

        Agendamento ag3 = new Agendamento(3, clienteAg, List.of(seg), "DIARIA", 30, "5555666677778888"); // preliminar esquecido
        System.out.println("Vencido em 10/11? " + ag3.cancelarSeVencido(LocalDate.of(2026, 11, 10)) + " -> " + ag3.getStatus()); // true, CANCELADO

        try { // tenta um tipo que não existe
            new Agendamento(4, clienteAg, List.of(seg), "ANUAL", 30, "5555666677778888"); // ANUAL não existe
        } catch (IllegalArgumentException e) { // erro esperado
            System.out.println("Erro: " + e.getMessage()); // tipo de cobrança inválido
        } // fim do try
        try { // tenta um cartão curto
            new Agendamento(5, clienteAg, List.of(seg), "DIARIA", 30, "123"); // cartão curto demais
        } catch (IllegalArgumentException e) { // erro esperado
            System.out.println("Erro: " + e.getMessage()); // número de cartão inválido
        } // fim do try
    } // fim do main
} // fim da classe AcademiaMilhoVerde
