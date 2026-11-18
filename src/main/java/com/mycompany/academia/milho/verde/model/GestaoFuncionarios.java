/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import com.mycompany.academia.milho.verde.dao.DAO; // interface genérica do DAO
import com.mycompany.academia.milho.verde.dao.DAOMemoria; // DAO em memória
import java.util.List; // tipo da lista

/**
 * Cadastro de colaboradores e administradores da academia.
 *
 * <p>O e-mail é o login, por isso não pode se repetir. Também não é permitido
 * que um funcionário remova a si mesmo nem que o último administrador seja removido.</p>
 *
 * @author Aline Ferreira
 */
public class GestaoFuncionarios { // cadastro de colaboradores e administradores

    /** Todos os funcionários (colaboradores e administradores). */
    private final DAO<Funcionario> funcionarios; // todos os funcionários

    /**
     * Cria a gestão sem nenhum funcionário.
     */
    public GestaoFuncionarios() { // construtor sem parâmetros
        this.funcionarios = new DAOMemoria<>(); // começa sem funcionários
    } // fim do construtor

    /**
     * Cadastra um colaborador.
     *
     * @param nome nome completo
     * @param endereco endereço
     * @param telefone telefone
     * @param email e-mail, que também é o login
     * @param senha senha de acesso
     * @return o colaborador criado
     * @throws IllegalArgumentException se algum dado for inválido ou o e-mail já estiver em uso
     */
    public Colaborador cadastrarColaborador(String nome, String endereco, String telefone,
            String email, String senha) { // colaborador novo
        exigirEmailLivre(email, 0); // o e-mail é o login: não pode repetir
        Colaborador colaborador = new Colaborador(funcionarios.gerarId(), nome, endereco, telefone, email, senha); // o construtor valida
        funcionarios.inserir(colaborador); // guarda
        return colaborador; // devolve o colaborador criado
    } // fim de cadastrarColaborador

    /**
     * Cadastra um administrador.
     *
     * @param nome nome completo
     * @param endereco endereço
     * @param telefone telefone
     * @param email e-mail, que também é o login
     * @param senha senha de acesso
     * @return o administrador criado
     * @throws IllegalArgumentException se algum dado for inválido ou o e-mail já estiver em uso
     */
    public Administrador cadastrarAdministrador(String nome, String endereco, String telefone,
            String email, String senha) { // administrador novo
        exigirEmailLivre(email, 0); // o e-mail é o login: não pode repetir
        Administrador administrador = new Administrador(funcionarios.gerarId(), nome, endereco, telefone, email, senha); // o construtor valida
        funcionarios.inserir(administrador); // guarda
        return administrador; // devolve o administrador criado
    } // fim de cadastrarAdministrador

    /**
     * Altera os dados de um funcionário (a senha tem método próprio).
     *
     * @param id identificador do funcionário
     * @param nome novo nome
     * @param endereco novo endereço
     * @param telefone novo telefone
     * @param email novo e-mail
     * @throws IllegalArgumentException se o funcionário não existir, algum dado for inválido ou o e-mail já for de outro
     */
    public void editarFuncionario(int id, String nome, String endereco, String telefone, String email) { // altera os dados
        Funcionario funcionario = exigirFuncionario(id); // precisa existir
        exigirEmailLivre(email, id); // outro funcionário não pode ter este e-mail
        funcionario.setNome(nome); // os setters validam cada dado
        funcionario.setEndereco(endereco); // altera o endereço
        funcionario.setTelefone(telefone); // altera o telefone
        funcionario.setEmail(email); // altera o e-mail
        funcionarios.atualizar(funcionario); // avisa o DAO
    } // fim de editarFuncionario

    /**
     * Remove um funcionário.
     *
     * @param id identificador do funcionário a remover
     * @param idLogado identificador de quem está pedindo
     * @throws IllegalArgumentException se o funcionário não existir
     * @throws IllegalStateException se tentar remover a si mesmo ou o último administrador
     */
    public void removerFuncionario(int id, int idLogado) { // apaga um funcionário
        Funcionario funcionario = exigirFuncionario(id); // precisa existir
        if (id == idLogado) { // quer remover a si mesmo?
            throw new IllegalStateException("Você não pode remover o seu próprio usuário."); // erro de estado
        } // fim do if
        if (funcionario.podeAcessarFinanceiro() && contarAdministradores() == 1) { // é o último administrador?
            throw new IllegalStateException("O último administrador não pode ser removido."); // erro de estado
        } // fim do if
        funcionarios.remover(id); // apaga do DAO
    } // fim de removerFuncionario

    /**
     * Troca a senha de um funcionário; quem confere a senha atual é o próprio funcionário.
     *
     * @param id identificador do funcionário
     * @param senhaAtual senha atual
     * @param novaSenha nova senha
     * @return {@code true} se trocou, {@code false} se a senha atual estava errada
     * @throws IllegalArgumentException se o funcionário não existir
     */
    public boolean alterarSenha(int id, String senhaAtual, String novaSenha) { // troca a senha
        Funcionario funcionario = exigirFuncionario(id); // precisa existir
        boolean trocou = funcionario.alterarSenha(senhaAtual, novaSenha); // o funcionário confere a senha atual
        if (trocou) { // trocou?
            funcionarios.atualizar(funcionario); // avisa o DAO
        } // fim do if
        return trocou; // false se a senha atual estava errada
    } // fim de alterarSenha

    /**
     * Procura um funcionário pelo e-mail, ignorando maiúsculas e minúsculas (usado no login).
     *
     * @param email e-mail procurado
     * @return o funcionário, ou {@code null} se ninguém usa esse e-mail
     */
    public Funcionario buscarPorEmail(String email) { // usado no login
        IteradorLista<Funcionario> iterador = new IteradorLista<>(funcionarios.listar()); // percorre os funcionários
        while (iterador.hasNext()) { // enquanto houver funcionário
            Funcionario funcionario = iterador.next(); // pega o próximo
            if (funcionario.getEmail().equalsIgnoreCase(email)) { // mesmo e-mail?
                return funcionario; // achou
            } // fim do if
        } // fim do while
        return null; // ninguém com esse e-mail
    } // fim de buscarPorEmail

    /**
     * Lista todos os funcionários.
     *
     * @return cópia da lista de funcionários
     */
    public List<Funcionario> listarFuncionarios() { // todos os funcionários
        return funcionarios.listar(); // cópia da lista
    } // fim de listarFuncionarios

    /**
     * Confere se já existe algum funcionário cadastrado.
     *
     * @return {@code false} no primeiro acesso, quando ainda não há ninguém
     */
    public boolean temFuncionarios() { // já existe alguém cadastrado?
        return !funcionarios.listar().isEmpty(); // false no primeiro acesso
    } // fim de temFuncionarios

    /**
     * Acesso de pacote: devolve à gestão o que foi lido do JSON.
     *
     * @param lidos funcionários lidos do arquivo
     */
    void carregar(List<Funcionario> lidos) { // pacote: devolve o que veio do JSON
        IteradorLista<Funcionario> iterador = new IteradorLista<>(lidos); // percorre os lidos
        while (iterador.hasNext()) { // enquanto houver funcionário
            funcionarios.inserir(iterador.next()); // volta para o DAO
        } // fim do while
    } // fim de carregar

    /**
     * Busca um funcionário e exige que ele exista.
     *
     * @param id identificador do funcionário
     * @return o funcionário
     * @throws IllegalArgumentException se não existir
     */
    private Funcionario exigirFuncionario(int id) { // busca e exige que exista
        Funcionario funcionario = funcionarios.buscarPorId(id); // procura pelo ID
        if (funcionario == null) { // não achou?
            throw new IllegalArgumentException("Funcionário " + id + " não encontrado."); // erro de argumento
        } // fim do if
        return funcionario; // devolve o funcionário
    } // fim de exigirFuncionario

    /**
     * Impede dois funcionários com o mesmo e-mail.
     *
     * @param email e-mail a conferir
     * @param idIgnorado ID do próprio funcionário, para a edição não acusar o e-mail dele mesmo (0 no cadastro)
     * @throws IllegalArgumentException se o e-mail estiver vazio ou já for de outro funcionário
     */
    private void exigirEmailLivre(String email, int idIgnorado) { // impede dois funcionários com o mesmo e-mail
        String emailValido = Validacao.exigirTexto(email, "e-mail"); // o e-mail é obrigatório
        Funcionario existente = buscarPorEmail(emailValido); // alguém já usa?
        if (existente != null && existente.getId() != idIgnorado) { // outro funcionário usa este e-mail?
            throw new IllegalArgumentException("Já existe funcionário com este e-mail."); // erro de argumento
        } // fim do if
    } // fim de exigirEmailLivre

    /**
     * Conta quantos administradores existem, perguntando a cada funcionário
     * se pode acessar o financeiro (polimorfismo).
     *
     * @return quantidade de administradores
     */
    private int contarAdministradores() { // quantos administradores existem
        int total = 0; // começa em zero
        IteradorLista<Funcionario> iterador = new IteradorLista<>(funcionarios.listar()); // percorre os funcionários
        while (iterador.hasNext()) { // enquanto houver funcionário
            if (iterador.next().podeAcessarFinanceiro()) { // é administrador? (polimorfismo)
                total++; // conta mais um
            } // fim do if
        } // fim do while
        return total; // devolve o total
    } // fim de contarAdministradores

    /**
     * Resume a gestão em uma linha.
     *
     * @return quantidade de funcionários
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "GestaoFuncionarios: " + funcionarios.listar().size() + " funcionário(s)"; // quantidade
    } // fim do toString
}