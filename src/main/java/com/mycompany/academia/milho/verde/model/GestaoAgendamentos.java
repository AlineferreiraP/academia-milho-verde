/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import com.mycompany.academia.milho.verde.dao.DAO; // interface genérica do DAO
import com.mycompany.academia.milho.verde.dao.DAOMemoria; // DAO em memória
import java.time.LocalDate; // data sem hora
import java.time.LocalDateTime; // data com hora
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

/**
 * Reúne as regras das aulas, dos agendamentos e dos pagamentos de diárias e
 * mensalidades da academia.
 *
 * <p>É a "porta" do subsistema de agenda: quem usa a classe não precisa
 * conhecer {@link Aula}, {@link AgendaSemanal} ou {@link VendaAgendamento}
 * por dentro. As regras de cada reserva (prazo, devolução) ficam no próprio
 * {@link Agendamento}.</p>
 *
 * @author Aline Ferreira
 */
public class GestaoAgendamentos { // regras das aulas, dos agendamentos e dos pagamentos de diárias e mensalidades

    /** Uma agenda por sala e por semana. */
    private final List<AgendaSemanal> agendas; // uma agenda por sala e por semana
    /** Todas as aulas cadastradas. */
    private final DAO<Aula> aulas; // todas as aulas cadastradas
    /** Todas as reservas feitas pelos clientes. */
    private final DAO<Agendamento> agendamentos; // todas as reservas
    /** Pagamentos das reservas confirmadas. */
    private final DAO<VendaAgendamento> pagamentos; // pagamentos das reservas confirmadas
    /** Diz qual agendamento vem antes (por dia e hora). */
    private final ComparadorAgendamento comparador; // diz qual agendamento vem antes

    /**
     * Cria a gestão vazia: sem agendas, aulas, agendamentos nem pagamentos.
     */
    public GestaoAgendamentos() { // construtor sem parâmetros
        this.agendas = new ArrayList<>(); // começa sem agendas
        this.aulas = new DAOMemoria<>(); // começa sem aulas
        this.agendamentos = new DAOMemoria<>(); // começa sem agendamentos
        this.pagamentos = new DAOMemoria<>(); // começa sem pagamentos
        this.comparador = new ComparadorAgendamento(); // cria o comparador por dia e hora
    } // fim do construtor

    /**
     * Cadastra uma aula nova na grade da sala.
     *
     * @param codigoSala código da sala (1 a 4)
     * @param dataHora dia e hora da aula
     * @param nomeInstrutor nome do instrutor
     * @return a aula criada
     * @throws IllegalArgumentException se a sala não existir ou os dados forem inválidos
     */
    public Aula cadastrarAula(int codigoSala, LocalDateTime dataHora, String nomeInstrutor) { // aula nova na grade
        Sala sala = buscarSala(codigoSala); // a sala precisa existir
        Aula aula = new Aula(aulas.gerarId(), sala, dataHora, nomeInstrutor); // o construtor valida os dados
        buscarOuCriarAgenda(sala, dataHora).adicionarAula(aula); // a agenda recusa domingo, horário fora ou ocupado
        aulas.inserir(aula); // guarda a aula
        return aula; // devolve a aula criada
    } // fim de cadastrarAula

    /**
     * Tira uma aula da grade, desde que nenhum agendamento a use.
     *
     * @param id identificador da aula
     * @throws IllegalArgumentException se a aula não existir
     * @throws IllegalStateException se algum agendamento (mesmo cancelado) usar a aula
     */
    public void removerAula(int id) { // tira uma aula da grade
        Aula aula = buscarAula(id); // a aula precisa existir
        if (contarReservas(aula, true) > 0) { // algum agendamento (até cancelado) usa essa aula?
            throw new IllegalStateException("A aula tem agendamentos e não pode ser removida."); // erro de estado
        } // fim do if
        buscarOuCriarAgenda(aula.getSala(), aula.getDataHora()).removerAula(aula); // libera o horário na agenda
        aulas.remover(id); // apaga do DAO
    } // fim de removerAula

    /**
     * Procura uma aula pelo identificador.
     *
     * @param id identificador da aula
     * @return a aula encontrada
     * @throws IllegalArgumentException se a aula não existir
     */
    public Aula buscarAula(int id) { // procura uma aula pelo ID
        Aula aula = aulas.buscarPorId(id); // busca no DAO
        if (aula == null) { // não achou?
            throw new IllegalArgumentException("Aula " + id + " não encontrada."); // erro de argumento
        } // fim do if
        return aula; // devolve a aula
    } // fim de buscarAula

    /**
     * Lista todas as aulas cadastradas.
     *
     * @return cópia da lista de aulas
     */
    public List<Aula> listarAulas() { // todas as aulas
        return aulas.listar(); // cópia da lista
    } // fim de listarAulas

    /**
     * Consulta a grade de uma sala numa semana.
     *
     * @param codigoSala código da sala (1 a 4)
     * @param inicioSemana qualquer dia da semana desejada
     * @return a agenda semanal da sala
     * @throws IllegalArgumentException se a sala não existir
     */
    public AgendaSemanal consultarAgendaSemanal(int codigoSala, LocalDate inicioSemana) { // grade de uma sala numa semana
        return buscarOuCriarAgenda(buscarSala(codigoSala), inicioSemana.atStartOfDay()); // a agenda da semana pedida
    } // fim de consultarAgendaSemanal

    /**
     * Calcula quantas vagas ainda há numa aula.
     *
     * @param idAula identificador da aula
     * @return capacidade da sala menos as reservas ativas
     * @throws IllegalArgumentException se a aula não existir
     */
    public int verificarVagas(int idAula) { // quantas vagas ainda há na aula
        Aula aula = buscarAula(idAula); // a aula precisa existir
        return aula.getSala().getCapacidade() - contarReservas(aula, false); // capacidade menos reservas ativas
    } // fim de verificarVagas

    /**
     * Calcula o preço pela tabela fixa da sala.
     *
     * @param idsAulas identificadores das aulas (todas da mesma sala)
     * @param tipo {@link Agendamento#DIARIA} ou {@link Agendamento#MENSALIDADE}
     * @return preço da mensalidade, ou preço da diária vezes o número de aulas
     * @throws IllegalArgumentException se as aulas forem inválidas ou o tipo não existir
     */
    public double calcularPrecoSugerido(List<Integer> idsAulas, String tipo) { // preço pela tabela da sala
        List<Aula> escolhidas = buscarAulas(idsAulas); // as aulas precisam existir e ser da mesma sala
        Sala sala = escolhidas.get(0).getSala(); // a sala de todas elas
        if (Agendamento.MENSALIDADE.equals(tipo)) { // mensalidade?
            return sala.getPrecoMensalidade(); // preço fixo do mês
        } // fim do if
        if (Agendamento.DIARIA.equals(tipo)) { // diária?
            return sala.getPrecoDiaria() * escolhidas.size(); // preço da diária vezes o número de aulas
        } // fim do if
        throw new IllegalArgumentException("Tipo de cobrança inválido."); // erro de argumento
    } // fim de calcularPrecoSugerido

    /**
     * Cria uma reserva preliminar para o cliente.
     *
     * @param cliente cliente ativo que está reservando
     * @param idsAulas identificadores das aulas (todas da mesma sala)
     * @param tipo {@link Agendamento#DIARIA} ou {@link Agendamento#MENSALIDADE}
     * @param preco valor a cobrar
     * @param numeroCartao número do cartão (só os 4 últimos dígitos são mostrados)
     * @return o agendamento criado, com status PRELIMINAR
     * @throws IllegalArgumentException se o cliente for inativo ou os dados forem inválidos
     * @throws IllegalStateException se o prazo de confirmação já passou ou o horário está lotado
     */
    public Agendamento agendar(Cliente cliente, List<Integer> idsAulas, String tipo,
            double preco, String numeroCartao) { // reserva preliminar
        if (cliente == null || !cliente.isAtivo()) { // precisa de cliente ativo
            throw new IllegalArgumentException("Informe um cliente ativo."); // erro de argumento
        } // fim do if
        List<Aula> escolhidas = buscarAulas(idsAulas); // as aulas precisam existir e ser da mesma sala
        Agendamento novo = new Agendamento(agendamentos.gerarId(), cliente, escolhidas,
                tipo, preco, numeroCartao); // o construtor valida tipo, preço e cartão
        if (LocalDate.now().isAfter(novo.calcularPrazoConfirmacao())) { // já não daria tempo de confirmar?
            throw new IllegalStateException("O prazo de confirmação dessa aula já passou."); // erro de estado
        } // fim do if
        exigirVaga(novo); // recusa se o horário já estiver lotado
        agendamentos.inserir(novo); // guarda a reserva
        return novo; // nasce PRELIMINAR
    } // fim de agendar

    /**
     * Confirma o agendamento e registra o pagamento.
     *
     * @param id identificador do agendamento
     * @param agora data e hora da confirmação
     * @return o pagamento registrado (quem chamou gera o extrato)
     * @throws IllegalArgumentException se o agendamento não existir
     * @throws IllegalStateException se estiver fora do prazo (o agendamento é cancelado)
     */
    public VendaAgendamento confirmarAgendamento(int id, LocalDateTime agora) { // cliente confirmou: debita e registra
        Agendamento agendamento = exigirAgendamento(id); // o agendamento precisa existir
        if (!agendamento.confirmar(agora.toLocalDate())) { // fora do prazo: o próprio agendamento se cancela
            throw new IllegalStateException("Fora do prazo: o agendamento foi cancelado."); // erro de estado
        } // fim do if
        VendaAgendamento pagamento = new VendaAgendamento(pagamentos.gerarId(), agendamento, agora); // registra o pagamento
        pagamentos.inserir(pagamento); // guarda o pagamento
        return pagamento; // quem chamou gera o extrato
    } // fim de confirmarAgendamento

    /**
     * Cancela o agendamento e guarda a devolução no pagamento, se houver.
     *
     * @param id identificador do agendamento
     * @param hoje data do cancelamento
     * @return valor devolvido ao cliente (50% ou zero)
     * @throws IllegalArgumentException se o agendamento não existir
     */
    public double cancelarAgendamento(int id, LocalDate hoje) { // cliente desistiu
        Agendamento agendamento = exigirAgendamento(id); // o agendamento precisa existir
        double aDevolver = agendamento.cancelar(hoje); // o agendamento calcula a devolução (50% ou 0)
        VendaAgendamento pagamento = buscarPagamento(id); // já tinha pago?
        if (pagamento != null) { // tinha pagamento
            pagamento.registrarDevolucao(aDevolver); // o pagamento guarda quanto voltou
        } // fim do if
        return aDevolver; // quem chamou gera o extrato da devolução
    } // fim de cancelarAgendamento

    /**
     * Cancela os agendamentos preliminares que perderam o prazo de confirmação.
     *
     * @param hoje data de referência
     * @return quantos agendamentos foram cancelados
     */
    public int cancelarVencidos(LocalDate hoje) { // cancela os preliminares que perderam o prazo
        int cancelados = 0; // quantos foram cancelados
        IteradorLista<Agendamento> iterador = new IteradorLista<>(agendamentos.listar()); // percorre os agendamentos
        while (iterador.hasNext()) { // enquanto houver agendamento
            if (iterador.next().cancelarSeVencido(hoje)) { // cancelou este?
                cancelados++; // conta mais um
            } // fim do if
        } // fim do while
        return cancelados; // devolve quantos foram cancelados
    } // fim de cancelarVencidos

    /**
     * Lista todas as reservas.
     *
     * @return cópia da lista de agendamentos
     */
    public List<Agendamento> listarAgendamentos() { // todas as reservas
        return agendamentos.listar(); // cópia da lista
    } // fim de listarAgendamentos

    /**
     * Acha a próxima reserva ativa de um cliente, usando o
     * {@link ComparadorAgendamento} para ver qual vem antes.
     *
     * @param idCliente identificador do cliente
     * @param hoje data de referência (reservas anteriores são ignoradas)
     * @return o agendamento mais cedo, ou {@code null} se não houver reserva futura
     */
    public Agendamento buscarProximoAgendamento(int idCliente, LocalDate hoje) { // a próxima reserva ativa do cliente
        Agendamento proximo = null; // o mais cedo encontrado até agora
        IteradorLista<Agendamento> iterador = new IteradorLista<>(agendamentos.listar()); // percorre os agendamentos
        while (iterador.hasNext()) { // enquanto houver agendamento
            Agendamento atual = iterador.next(); // pega o próximo
            boolean doCliente = atual.getCliente().getId() == idCliente; // é deste cliente?
            boolean ativo = !Agendamento.CANCELADO.equals(atual.getStatus()); // não está cancelado?
            boolean futuro = !atual.getPrimeiraData().isBefore(hoje); // ainda não passou?
            if (doCliente && ativo && futuro) { // candidato válido
                if (proximo == null || comparador.compare(atual, proximo) < 0) { // primeiro ou vem antes do guardado
                    proximo = atual; // passa a ser o mais cedo
                } // fim do if
            } // fim do if
        } // fim do while
        return proximo; // null se o cliente não tem reserva futura
    } // fim de buscarProximoAgendamento

    /**
     * Lista os pagamentos de diárias e mensalidades de um período (usado nos relatórios).
     *
     * @param inicio primeiro dia do período
     * @param fim último dia do período (inclusive)
     * @return pagamentos do período, como {@link Venda}
     * @throws IllegalArgumentException se o período for inválido
     */
    public List<Venda> listarPagamentos(LocalDate inicio, LocalDate fim) { // pagamentos de um período (relatórios)
        if (inicio == null || fim == null || fim.isBefore(inicio)) { // período inválido
            throw new IllegalArgumentException("Período inválido."); // erro de argumento
        } // fim do if
        List<Venda> doPeriodo = new ArrayList<>(); // resultado começa vazio
        IteradorLista<VendaAgendamento> iterador = new IteradorLista<>(pagamentos.listar()); // percorre os pagamentos
        while (iterador.hasNext()) { // enquanto houver pagamento
            VendaAgendamento pagamento = iterador.next(); // pega o próximo
            LocalDate dia = pagamento.getDataHora().toLocalDate(); // dia do pagamento
            if (!dia.isBefore(inicio) && !dia.isAfter(fim)) { // dentro do período (inclui as pontas)?
                doPeriodo.add(pagamento); // entra no resultado
            } // fim do if
        } // fim do while
        return doPeriodo; // lista de Venda: o relatório soma sem saber o tipo
    } // fim de listarPagamentos

    /**
     * Acesso de pacote: usado pelo {@code RepositorioJson} para gravar.
     *
     * @return cópia da lista de agendamentos
     */
    List<Agendamento> getAgendamentos() { // pacote: para o RepositorioJson gravar
        return agendamentos.listar(); // cópia da lista
    } // fim de getAgendamentos

    /**
     * Acesso de pacote: usado pelo {@code RepositorioJson} para gravar.
     *
     * @return cópia da lista de pagamentos
     */
    List<VendaAgendamento> getPagamentos() { // pacote: para o RepositorioJson gravar
        return pagamentos.listar(); // cópia da lista
    } // fim de getPagamentos

    /**
     * Acesso de pacote: devolve ao sistema o que foi lido do JSON.
     *
     * @param aulasLidas aulas lidas do arquivo
     * @param agendamentosLidos agendamentos lidos do arquivo
     * @param pagamentosLidos pagamentos lidos do arquivo
     */
    void carregar(List<Aula> aulasLidas, List<Agendamento> agendamentosLidos,
            List<VendaAgendamento> pagamentosLidos) { // pacote: devolve o que veio do JSON
        IteradorLista<Aula> iteradorAulas = new IteradorLista<>(aulasLidas); // percorre as aulas lidas
        while (iteradorAulas.hasNext()) { // enquanto houver aula
            Aula aula = iteradorAulas.next(); // pega a próxima
            buscarOuCriarAgenda(aula.getSala(), aula.getDataHora()).adicionarAula(aula); // volta para a grade
            aulas.inserir(aula); // volta para o DAO
        } // fim do while
        IteradorLista<Agendamento> iteradorAgendamentos = new IteradorLista<>(agendamentosLidos); // percorre os lidos
        while (iteradorAgendamentos.hasNext()) { // enquanto houver agendamento
            agendamentos.inserir(iteradorAgendamentos.next()); // volta para o DAO
        } // fim do while
        IteradorLista<VendaAgendamento> iteradorPagamentos = new IteradorLista<>(pagamentosLidos); // percorre os lidos
        while (iteradorPagamentos.hasNext()) { // enquanto houver pagamento
            pagamentos.inserir(iteradorPagamentos.next()); // volta para o DAO
        } // fim do while
    } // fim de carregar

    /**
     * Acha uma das 4 salas fixas, que ficam guardadas no {@link Sistema}.
     *
     * @param codigo código da sala
     * @return a sala
     * @throws IllegalArgumentException se não existir sala com esse código
     */
    private Sala buscarSala(int codigo) { // acha uma das 4 salas fixas
        Sala[] salas = Sistema.getSalas(); // as salas ficam no Sistema (static)
        for (int i = 0; i < salas.length; i++) { // percorre o vetor
            if (salas[i].getCodigo() == codigo) { // achou o código?
                return salas[i]; // devolve a sala
            } // fim do if
        } // fim do for
        throw new IllegalArgumentException("Sala " + codigo + " não existe."); // erro de argumento
    } // fim de buscarSala

    /**
     * Acha a agenda da sala na semana da data; se ainda não existe, cria.
     *
     * @param sala sala da agenda
     * @param dataHora qualquer momento da semana desejada
     * @return a agenda semanal
     */
    private AgendaSemanal buscarOuCriarAgenda(Sala sala, LocalDateTime dataHora) { // agenda da sala na semana da data
        IteradorLista<AgendaSemanal> iterador = new IteradorLista<>(agendas); // percorre as agendas
        while (iterador.hasNext()) { // enquanto houver agenda
            AgendaSemanal agenda = iterador.next(); // pega a próxima
            if (agenda.getSala().getCodigo() == sala.getCodigo() && agenda.contem(dataHora)) { // mesma sala e semana?
                return agenda; // já existe
            } // fim do if
        } // fim do while
        AgendaSemanal nova = new AgendaSemanal(dataHora.toLocalDate(), sala); // a agenda volta sozinha para a segunda-feira
        agendas.add(nova); // guarda a agenda nova
        return nova; // devolve a agenda criada
    } // fim de buscarOuCriarAgenda

    /**
     * Transforma IDs em aulas, exigindo que todas sejam da mesma sala.
     *
     * @param idsAulas identificadores das aulas
     * @return as aulas encontradas
     * @throws IllegalArgumentException se a lista for vazia, a aula não existir ou houver salas diferentes
     */
    private List<Aula> buscarAulas(List<Integer> idsAulas) { // transforma IDs em aulas, todas da mesma sala
        if (idsAulas == null || idsAulas.isEmpty()) { // precisa de pelo menos uma aula
            throw new IllegalArgumentException("Informe as aulas."); // erro de argumento
        } // fim do if
        List<Aula> escolhidas = new ArrayList<>(); // resultado começa vazio
        IteradorLista<Integer> iterador = new IteradorLista<>(idsAulas); // percorre os IDs
        while (iterador.hasNext()) { // enquanto houver ID
            Aula aula = buscarAula(iterador.next()); // a aula precisa existir
            if (!escolhidas.isEmpty() && escolhidas.get(0).getSala().getCodigo() != aula.getSala().getCodigo()) { // outra sala?
                throw new IllegalArgumentException("Todas as aulas do agendamento devem ser da mesma sala."); // regra
            } // fim do if
            escolhidas.add(aula); // entra na lista
        } // fim do while
        return escolhidas; // devolve as aulas
    } // fim de buscarAulas

    /**
     * Busca um agendamento e exige que ele exista.
     *
     * @param id identificador do agendamento
     * @return o agendamento
     * @throws IllegalArgumentException se não existir
     */
    private Agendamento exigirAgendamento(int id) { // busca e exige que exista
        Agendamento agendamento = agendamentos.buscarPorId(id); // procura pelo ID
        if (agendamento == null) { // não achou?
            throw new IllegalArgumentException("Agendamento " + id + " não encontrado."); // erro de argumento
        } // fim do if
        return agendamento; // devolve o agendamento
    } // fim de exigirAgendamento

    /**
     * Conta quantos agendamentos usam uma aula.
     *
     * @param aula aula procurada
     * @param contarCancelados {@code true} para contar também os cancelados
     * @return o total de reservas
     */
    private int contarReservas(Aula aula, boolean contarCancelados) { // quantos agendamentos usam esta aula
        int total = 0; // começa em zero
        IteradorLista<Agendamento> iterador = new IteradorLista<>(agendamentos.listar()); // percorre os agendamentos
        while (iterador.hasNext()) { // enquanto houver agendamento
            Agendamento agendamento = iterador.next(); // pega o próximo
            if (contarCancelados || !Agendamento.CANCELADO.equals(agendamento.getStatus())) { // conta este?
                IteradorLista<Aula> iteradorAulas = new IteradorLista<>(agendamento.getAulas()); // percorre as aulas dele
                while (iteradorAulas.hasNext()) { // enquanto houver aula
                    if (iteradorAulas.next().getId() == aula.getId()) { // é a aula procurada?
                        total++; // mais uma reserva
                    } // fim do if
                } // fim do while das aulas
            } // fim do if
        } // fim do while dos agendamentos
        return total; // devolve o total
    } // fim de contarReservas

    /**
     * Conta os agendamentos ativos na mesma sala, dia e hora do novo agendamento.
     * O {@link ComparadorAgendamento} devolve zero quando o dia e a hora coincidem.
     *
     * @param novo agendamento que se quer fazer
     * @return quantas vagas já estão ocupadas nesse horário
     */
    private int contarOcupados(Agendamento novo) { // quantos agendamentos ativos há na mesma sala, dia e hora
        int ocupados = 0; // começa em zero
        IteradorLista<Agendamento> iterador = new IteradorLista<>(agendamentos.listar()); // percorre os agendamentos
        while (iterador.hasNext()) { // enquanto houver agendamento
            Agendamento existente = iterador.next(); // pega o próximo
            boolean ativo = !Agendamento.CANCELADO.equals(existente.getStatus()); // cancelado libera a vaga
            boolean mesmaSala = existente.getSala().getCodigo() == novo.getSala().getCodigo(); // mesma sala?
            if (ativo && mesmaSala && comparador.compare(novo, existente) == 0) { // zero: mesmo dia e hora
                ocupados++; // mais uma vaga ocupada
            } // fim do if
        } // fim do while
        return ocupados; // devolve quantas vagas estão ocupadas
    } // fim de contarOcupados

    /**
     * Recusa o agendamento se o horário já estiver lotado.
     *
     * @param novo agendamento que se quer fazer
     * @throws IllegalStateException se as vagas ocupadas já chegaram à capacidade da sala
     */
    private void exigirVaga(Agendamento novo) { // recusa se o horário estiver lotado
        if (contarOcupados(novo) >= novo.getSala().getCapacidade()) { // chegou à capacidade da sala?
            throw new IllegalStateException("Esse horário já está lotado."); // erro de estado
        } // fim do if
    } // fim de exigirVaga

    /**
     * Procura o pagamento de um agendamento.
     *
     * @param idAgendamento identificador do agendamento
     * @return o pagamento, ou {@code null} se ainda não foi pago
     */
    private VendaAgendamento buscarPagamento(int idAgendamento) { // pagamento de um agendamento, se houver
        IteradorLista<VendaAgendamento> iterador = new IteradorLista<>(pagamentos.listar()); // percorre os pagamentos
        while (iterador.hasNext()) { // enquanto houver pagamento
            VendaAgendamento pagamento = iterador.next(); // pega o próximo
            if (pagamento.getAgendamento().getId() == idAgendamento) { // é deste agendamento?
                return pagamento; // achou
            } // fim do if
        } // fim do while
        return null; // ainda não foi pago
    } // fim de buscarPagamento

    /**
     * Resume a gestão em uma linha.
     *
     * @return quantidade de aulas, agendamentos e pagamentos
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "GestaoAgendamentos: " + aulas.listar().size() + " aula(s), "
                + agendamentos.listar().size() + " agendamento(s), "
                + pagamentos.listar().size() + " pagamento(s)"; // quantidades
    } // fim do toString
}