/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import com.mycompany.academia.milho.verde.dao.DAO; // interface genérica do DAO: a gestão não sabe onde os clientes ficam
import com.mycompany.academia.milho.verde.dao.DAOMemoria; // DAO padrão, usado pelo construtor sem parâmetros
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

/**
 * Regras de negócio dos clientes: cadastro, edição, remoção lógica, busca,
 * extratos e conta.
 *
 * <p>A gestão não sabe onde os clientes ficam guardados: ela usa um {@link DAO},
 * que pode ser em memória ou em arquivo JSON. O CPF é guardado pseudonimizado e
 * não pode se repetir entre clientes.</p>
 *
 * @author Aline Ferreira
 */
public class GestaoClientes { // regras de negócio dos clientes

    /** Quantidade de dígitos de um CPF. */
    private static final int DIGITOS_CPF = 11; // um CPF tem 11 dígitos

    /** Onde os clientes ficam guardados; não troca depois de criado. */
    private final DAO<Cliente> dao; // onde os clientes ficam guardados; final: não troca depois de criado
    /** Compara clientes pelo CPF para impedir cadastro repetido. */
    private final ComparadorCliente comparador; // compara clientes pelo CPF para impedir cadastro repetido

    /**
     * Cria a gestão guardando os clientes só na memória.
     */
    public GestaoClientes() { // construtor sem parâmetros
        this(new DAOMemoria<>()); // usa o DAO em memória como padrão (o <> deduz o tipo Cliente)
    } // fim do construtor sem parâmetros

    /**
     * Cria a gestão com o DAO informado.
     *
     * @param novoDao onde os clientes ficam guardados
     * @throws IllegalArgumentException se o DAO for nulo
     */
    public GestaoClientes(DAO<Cliente> novoDao) { // recebe o DAO que vai guardar os clientes
        if (novoDao == null) { // não aceita "nenhum DAO"
            throw new IllegalArgumentException("Informe o DAO de clientes."); // erro de argumento
        } // fim do if
        this.dao = novoDao; // guarda o DAO
        this.comparador = new ComparadorCliente(ComparadorCliente.CPF); // cria o comparador por CPF
    } // fim do construtor com parâmetros

    /**
     * Cadastra um cliente novo.
     *
     * @param nome nome completo
     * @param endereco endereço
     * @param telefone telefone
     * @param email e-mail
     * @param cpf CPF com 11 dígitos (o número real não é guardado)
     * @return o cliente criado
     * @throws IllegalArgumentException se algum dado for inválido ou o CPF já estiver cadastrado
     */
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

    /**
     * Altera os dados de um cliente.
     *
     * @param id identificador do cliente
     * @param nome novo nome
     * @param endereco novo endereço
     * @param telefone novo telefone
     * @param email novo e-mail
     * @param cpf novo CPF com 11 dígitos
     * @throws IllegalArgumentException se o cliente não existir, algum dado for inválido ou o CPF for de outro cliente
     */
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

    /**
     * Remove um cliente por exclusão lógica: ele é desativado e o histórico é mantido.
     *
     * @param id identificador do cliente
     * @throws IllegalArgumentException se o cliente não existir
     */
    public void removerCliente(int id) { // "excluir cliente": exclusão lógica
        Cliente cliente = exigirCliente(id); // busca; se não existir, lança erro
        cliente.desativar(); // desativa sem apagar o histórico
        dao.atualizar(cliente); // avisa o DAO
    } // fim de removerCliente

    /**
     * Desfaz a remoção lógica de um cliente.
     *
     * @param id identificador do cliente
     * @throws IllegalArgumentException se o cliente não existir
     */
    public void reativarCliente(int id) { // desfaz a remoção
        Cliente cliente = exigirCliente(id); // busca; se não existir, lança erro
        cliente.ativar(); // volta a ficar ativo
        dao.atualizar(cliente); // avisa o DAO
    } // fim de reativarCliente

    /**
     * Busca um cliente pelo identificador.
     *
     * @param id identificador do cliente
     * @return o cliente, ou {@code null} se não existir
     */
    public Cliente buscarCliente(int id) { // busca pelo ID
        return dao.buscarPorId(id); // devolve o cliente ou null
    } // fim de buscarCliente

    /**
     * Busca clientes pelo começo de qualquer palavra do nome, sem diferenciar maiúsculas.
     *
     * @param trecho texto digitado
     * @return os clientes encontrados (a lista pode ser vazia)
     * @throws IllegalArgumentException se o texto estiver vazio
     */
    public List<Cliente> buscarPorNome(String trecho) { // busca pelo começo de qualquer palavra do nome
        String procurado = Validacao.exigirTexto(trecho, "nome para busca").toLowerCase(); // texto em minúsculas
        List<Cliente> encontrados = new ArrayList<>(); // resultado começa vazio
        IteradorLista<Cliente> iterador = new IteradorLista<>(dao.listar()); // nosso iterador percorre os clientes
        while (iterador.hasNext()) { // enquanto houver cliente
            Cliente cliente = iterador.next(); // pega o próximo
            for (String palavra : cliente.getNome().toLowerCase().split(" ")) { // percorre as palavras (vetor, não lista)
                if (palavra.startsWith(procurado)) { // a palavra começa com o texto digitado?
                    encontrados.add(cliente); // achou: entra no resultado
                    break; // não precisa olhar as outras palavras deste cliente
                } // fim do if
            } // fim do for das palavras
        } // fim do while dos clientes
        return encontrados; // devolve quem foi encontrado (pode ser vazia)
    } // fim de buscarPorNome

    /**
     * Lista todos os clientes, ativos e desativados.
     *
     * @return cópia da lista, na ordem em que estão guardados (ordem de ID)
     */
    public List<Cliente> listarClientes() { // lista todos os clientes
        return dao.listar(); // na ordem em que estão guardados (ordem de ID)
    } // fim de listarClientes

    /**
     * Guarda um extrato dentro do cliente a que ele pertence e grava a alteração.
     *
     * @param extrato extrato a guardar
     * @throws IllegalArgumentException se o extrato for nulo ou o cliente não estiver cadastrado
     */
    public void guardarExtrato(Extrato extrato) { // guarda o extrato no cliente e grava
        if (extrato == null) { // não aceita "nenhum extrato"
            throw new IllegalArgumentException("Informe o extrato."); // erro de argumento
        } // fim do if
        Cliente cliente = exigirCliente(extrato.getCliente().getId()); // o cliente precisa estar cadastrado
        cliente.adicionarExtrato(extrato); // o cliente guarda o extrato
        dao.atualizar(cliente); // avisa o DAO (no DAO de JSON, grava o extrato junto com o cliente)
    } // fim de guardarExtrato

    /**
     * Devolve todos os extratos de um cliente.
     *
     * @param idCliente identificador do cliente
     * @return cópia da lista de extratos
     * @throws IllegalArgumentException se o cliente não existir
     */
    public List<Extrato> emitirConta(int idCliente) { // todos os extratos de um cliente
        return exigirCliente(idCliente).getExtratos(); // busca o cliente e devolve a cópia dos extratos
    } // fim de emitirConta

    /**
     * Soma o valor de todos os extratos do cliente; a devolução entra com valor negativo.
     *
     * @param idCliente identificador do cliente
     * @return o total que o cliente já pagou
     * @throws IllegalArgumentException se o cliente não existir
     */
    public double calcularTotalConta(int idCliente) { // soma o que o cliente já pagou
        double total = 0; // começa em zero
        IteradorLista<Extrato> iterador = new IteradorLista<>(emitirConta(idCliente)); // nosso iterador percorre os extratos
        while (iterador.hasNext()) { // enquanto houver extrato
            total = total + iterador.next().getValor(); // soma o valor (devolução entra negativa)
        } // fim do while
        return total; // devolve o total
    } // fim de calcularTotalConta

    /**
     * Acesso de pacote: usado por outras classes do model.
     *
     * @return cópia da lista de clientes
     */
    List<Cliente> getClientes() { // acesso de pacote: usado por outras classes do model
        return dao.listar(); // devolve uma cópia da lista
    } // fim de getClientes

    /**
     * Busca um cliente e exige que ele exista.
     *
     * @param id identificador do cliente
     * @return o cliente encontrado
     * @throws IllegalArgumentException se não existir
     */
    private Cliente exigirCliente(int id) { // busca e exige que exista
        Cliente cliente = dao.buscarPorId(id); // procura pelo ID
        if (cliente == null) { // não encontrou?
            throw new IllegalArgumentException("Cliente " + id + " não encontrado."); // erro de argumento
        } // fim do if
        return cliente; // devolve o cliente encontrado
    } // fim de exigirCliente

    /**
     * Impede dois clientes com o mesmo CPF, usando o {@link ComparadorCliente}.
     *
     * @param cpfPseudo CPF pseudonimizado que se quer usar
     * @param idIgnorado ID do próprio cliente, para a edição não acusar o CPF dele mesmo (0 no cadastro)
     * @throws IllegalArgumentException se o CPF já for de outro cliente
     */
    private void exigirCpfLivre(String cpfPseudo, int idIgnorado) { // impede dois clientes com o mesmo CPF
        Cliente candidato = new Cliente(); // cliente "molde", sem ID, só para a comparação
        candidato.setCpfPseudo(cpfPseudo); // coloca nele o CPF que se quer usar
        IteradorLista<Cliente> iterador = new IteradorLista<>(dao.listar()); // nosso iterador percorre os clientes
        while (iterador.hasNext()) { // enquanto houver cliente
            Cliente existente = iterador.next(); // pega o próximo
            boolean mesmoCpf = comparador.compare(existente, candidato) == 0; // o comparador diz se os CPFs são iguais
            if (mesmoCpf && existente.getId() != idIgnorado) { // CPF igual em OUTRO cliente?
                throw new IllegalArgumentException("Já existe cliente com este CPF (ID "
                        + existente.getId() + ")."); // não deixa repetir
            } // fim do if
        } // fim do while
    } // fim de exigirCpfLivre

    /**
     * Transforma o CPF para não guardar o número real.
     *
     * @param cpf CPF digitado, com ou sem pontos e traço
     * @return código do CPF em hexadecimal
     * @throws IllegalArgumentException se o CPF não tiver 11 dígitos
     */
    private static String pseudonimizarCpf(String cpf) { // transforma o CPF para não guardar o número real
        String digitos = Validacao.exigirTexto(cpf, "CPF").replaceAll("\\D", ""); // tira pontos, traço e espaços
        if (digitos.length() != DIGITOS_CPF) { // precisa ter 11 dígitos
            throw new IllegalArgumentException("O CPF deve ter 11 dígitos."); // erro de argumento
        } // fim do if
        return Integer.toHexString(digitos.hashCode()); // código do texto em hexadecimal; o CPF real não é guardado
    } // fim de pseudonimizarCpf

    /**
     * Descreve a gestão.
     *
     * @return texto com quantos clientes há
     */
    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa a gestão
        return "GestaoClientes com " + dao.listar().size() + " cliente(s)"; // mostra quantos clientes há
    } // fim de toString
} // fim da classe GestaoClientes