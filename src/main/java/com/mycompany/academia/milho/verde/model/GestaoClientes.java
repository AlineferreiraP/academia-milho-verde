/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import com.mycompany.academia.milho.verde.dao.DAO; // interface genérica do DAO: a gestão não sabe onde os clientes ficam
import com.mycompany.academia.milho.verde.dao.DAOMemoria; // DAO padrão, usado pelo construtor sem parâmetros
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

public class GestaoClientes { // regras de negócio dos clientes

    private static final int DIGITOS_CPF = 11; // um CPF tem 11 dígitos

    private final DAO<Cliente> dao; // onde os clientes ficam guardados; final: não troca depois de criado
    private final ComparadorCliente comparador; // compara clientes pelo CPF para impedir cadastro repetido

    public GestaoClientes() { // construtor sem parâmetros
        this(new DAOMemoria<>()); // usa o DAO em memória como padrão (o <> deduz o tipo Cliente)
    } // fim do construtor sem parâmetros

    public GestaoClientes(DAO<Cliente> novoDao) { // recebe o DAO que vai guardar os clientes
        if (novoDao == null) { // não aceita "nenhum DAO"
            throw new IllegalArgumentException("Informe o DAO de clientes."); // erro de argumento
        } // fim do if
        this.dao = novoDao; // guarda o DAO
        this.comparador = new ComparadorCliente(); // cria o comparador por CPF
    } // fim do construtor com parâmetros

    public Cliente cadastrarCliente(String nome, String endereco, String telefone,
            String email, String cpf) { // cadastra um cliente novo
        Validacao.exigirTexto(nome, "nome"); // confere tudo antes de gastar um ID
        Validacao.exigirTexto(endereco, "endereço"); // idem
        Validacao.exigirTexto(telefone, "telefone"); // idem
        Validacao.exigirTexto(email, "e-mail"); // idem
        String cpfPseudo = pseudonimizarCpf(cpf); // valida o CPF e o transforma; o original não é guardado
        exigirCpfLivre(cpfPseudo, 0); // ninguém pode ter o mesmo CPF (0: nenhum cliente a ignorar)
        Cliente cliente = new Cliente(dao.gerarId(), nome, endereco,
                telefone, email, cpfPseudo); // cria o cliente com um ID novo
        dao.inserir(cliente); // guarda no DAO
        return cliente; // devolve o cliente criado
    } // fim de cadastrarCliente

    public void editarCliente(int id, String nome, String endereco, String telefone,
            String email, String cpf) { // altera os dados de um cliente
        Cliente cliente = exigirCliente(id); // busca; se não existir, lança erro
        String nomeValido = Validacao.exigirTexto(nome, "nome"); // confere tudo antes de alterar
        String enderecoValido = Validacao.exigirTexto(endereco, "endereço"); // idem
        String telefoneValido = Validacao.exigirTexto(telefone, "telefone"); // idem
        String emailValido = Validacao.exigirTexto(email, "e-mail"); // idem
        String cpfPseudo = pseudonimizarCpf(cpf); // idem para o CPF
        exigirCpfLivre(cpfPseudo, id); // outro cliente não pode ter este CPF (ignora o próprio)
        cliente.setNome(nomeValido); // tudo válido: agora altera
        cliente.setEndereco(enderecoValido); // altera o endereço
        cliente.setTelefone(telefoneValido); // altera o telefone
        cliente.setEmail(emailValido); // altera o e-mail
        cliente.setCpfPseudo(cpfPseudo); // altera o CPF
        dao.atualizar(cliente); // avisa o DAO (no DAO de JSON, é aqui que grava o arquivo)
    } // fim de editarCliente

    public void removerCliente(int id) { // "excluir cliente": exclusão lógica
        Cliente cliente = exigirCliente(id); // busca; se não existir, lança erro
        cliente.desativar(); // desativa sem apagar o histórico
        dao.atualizar(cliente); // avisa o DAO
    } // fim de removerCliente

    public void reativarCliente(int id) { // desfaz a remoção
        Cliente cliente = exigirCliente(id); // busca; se não existir, lança erro
        cliente.ativar(); // volta a ficar ativo
        dao.atualizar(cliente); // avisa o DAO
    } // fim de reativarCliente

    public Cliente buscarCliente(int id) { // busca pelo ID
        return dao.buscarPorId(id); // devolve o cliente ou null
    } // fim de buscarCliente

    public List<Cliente> buscarPorNome(String trecho) { // busca pelo começo de qualquer palavra do nome
        String procurado = Validacao.exigirTexto(trecho, "nome para busca").toLowerCase(); // texto em minúsculas
        List<Cliente> encontrados = new ArrayList<>(); // resultado começa vazio
        for (Cliente cliente : dao.listar()) { // percorre todos os clientes
            for (String palavra : cliente.getNome().toLowerCase().split(" ")) { // percorre cada palavra do nome
                if (palavra.startsWith(procurado)) { // a palavra começa com o texto digitado?
                    encontrados.add(cliente); // achou: entra no resultado
                    break; // não precisa olhar as outras palavras deste cliente
                } // fim do if
            } // fim do for das palavras
        } // fim do for dos clientes
        return encontrados; // devolve quem foi encontrado (pode ser vazia)
    } // fim de buscarPorNome

    public List<Cliente> listarClientes() { // lista todos os clientes
        return dao.listar(); // na ordem em que estão guardados (ordem de ID)
    } // fim de listarClientes

    List<Cliente> getClientes() { // acesso de pacote: usado por outras classes do model
        return dao.listar(); // devolve uma cópia da lista
    } // fim de getClientes

    private Cliente exigirCliente(int id) { // busca e exige que exista
        Cliente cliente = dao.buscarPorId(id); // procura pelo ID
        if (cliente == null) { // não encontrou?
            throw new IllegalArgumentException("Cliente " + id + " não encontrado."); // erro de argumento
        } // fim do if
        return cliente; // devolve o cliente encontrado
    } // fim de exigirCliente

    private void exigirCpfLivre(String cpfPseudo, int idIgnorado) { // impede dois clientes com o mesmo CPF
        Cliente candidato = new Cliente(); // cliente "molde", sem ID, só para a comparação
        candidato.setCpfPseudo(cpfPseudo); // coloca nele o CPF que se quer usar
        for (Cliente existente : dao.listar()) { // percorre os clientes guardados
            boolean mesmoCpf = comparador.compare(existente, candidato) == 0; // o comparador diz se os CPFs são iguais
            if (mesmoCpf && existente.getId() != idIgnorado) { // CPF igual em OUTRO cliente?
                throw new IllegalArgumentException("Já existe cliente com este CPF (ID "
                        + existente.getId() + ")."); // não deixa repetir
            } // fim do if
        } // fim do for
    } // fim de exigirCpfLivre

    private static String pseudonimizarCpf(String cpf) { // transforma o CPF para não guardar o número real
        String digitos = Validacao.exigirTexto(cpf, "CPF").replaceAll("\\D", ""); // tira pontos, traço e espaços
        if (digitos.length() != DIGITOS_CPF) { // precisa ter 11 dígitos
            throw new IllegalArgumentException("O CPF deve ter 11 dígitos."); // erro de argumento
        } // fim do if
        return Integer.toHexString(digitos.hashCode()); // código do texto em hexadecimal; o CPF real não é guardado
    } // fim de pseudonimizarCpf

    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa a gestão
        return "GestaoClientes com " + dao.listar().size() + " cliente(s)"; // mostra quantos clientes há
    } // fim de toString
} // fim da classe GestaoClientes