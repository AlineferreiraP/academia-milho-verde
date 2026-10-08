# 🏋️ Academia Milho Verde

> Sistema de gerenciamento para uma academia no distrito de Milho Verde (MG): diárias, mensalidades, agenda de aulas, lojinha, lanchonete, catraca e finanças, tudo em um só lugar.

---

## 📖 A história

Milho Verde é um pequeno distrito na Serra do Espinhaço que recebe muitos turistas ao longo do ano. A ideia deste projeto nasceu de um cenário simples: alguém que, ao se aposentar, quer abrir ali uma academia com **quatro salas** (spinning, musculação, fit dance e pilates), uma **lanchonete** e uma **lojinha** de artigos esportivos.

Como o público é formado em boa parte por visitantes de passagem, o foco está nas **diárias**, sem deixar de lado as **mensalidades** de quem mora na região. O cliente liga, o funcionário confere a agenda, faz uma reserva preliminar e, se o cliente confirmar a tempo, a cobrança é feita no cartão. Se desistir com antecedência, recebe metade do valor de volta.

Além disso, a dona da academia quer saber **quem entrou, quem saiu e quantas pessoas estão lá dentro** a qualquer momento (por meio de uma catraca externa), lançar as despesas do mês e fechar um balanço de receitas e despesas.

Este repositório é a implementação desse sistema em Java, desenvolvida como projeto da disciplina de **Programação Orientada a Objetos** do curso de Sistemas de Informação da **UFVJM**, dando sequência à modelagem feita no Trabalho Prático I.

---

## ✨ Funcionalidades planejadas

| Área | O que o sistema faz |
|---|---|
| 👤 **Clientes** | Cadastro, edição e remoção com ID único e CPF pseudonimizado |
| 🧑‍💼 **Funcionários** | Colaboradores e administradores com login e senha protegida por hash |
| 📅 **Agenda** | Aulas nas 4 salas, reserva preliminar, confirmação até 5 dias úteis antes e cancelamento com estorno de 50% |
| 🛒 **Lojinha e lanchonete** | Produtos, estoque, reposição, vendas e extrato para o cliente |
| 🚪 **Catraca** | Recebe entradas e saídas automaticamente e gera relatórios diários, mensais e anuais |
| 💰 **Financeiro** | Despesas, relatório de vendas e balanço mensal com estatísticas (restrito ao administrador) |
| 💾 **Persistência** | Todos os dados salvos e recuperados em um arquivo JSON |

---

## 🧱 Arquitetura

O projeto segue o padrão **MVC (Model–View–Controller)**. Uma solicitação percorre o caminho View → Controller → Sistema (fachada do Model) → classes de domínio, e os dados são gravados no arquivo `academia.json`.

- **Model**: dados e regras de negócio (prazos, estornos, estoque, permissões, cálculos). Não sabe nada sobre telas.
- **View**: lê o que o usuário digita e mostra os resultados.
- **Controller**: faz a ponte entre a View e o Model.

```
src/main/java/com/mycompany/academia/milho/verde/
├── AcademiaMilhoVerde.java   → classe principal
├── model/                    → regras de negócio e persistência
├── controller/               → um controller por área do sistema
└── view/                     → interface com o usuário
```

---

## 🛠️ Tecnologias

- **Java 21**
- **Maven** para build e dependências
- **[org.json](https://github.com/stleary/JSON-java)** (versão 20260522) para ler e gravar JSON
- **Apache NetBeans** como IDE
- **JavaDoc** para a documentação do código
- **LaTeX** para a documentação de modelagem (casos de uso, cenários e diagrama de classes)

---

## ▶️ Como executar

**Pré-requisitos:** JDK 21 e Maven (ou o NetBeans, que já traz o Maven).

```bash
git clone https://github.com/AlineferreiraP/academia-milho-verde.git
cd academia-milho-verde
mvn clean package
mvn exec:java
```

No NetBeans: abra a pasta do projeto, use **Clean and Build** (Shift+F11) e depois **Run** (F6).

Os dados são gravados no arquivo `academia.json`, criado na pasta do projeto na primeira execução. Ele não é versionado, pois contém apenas dados de uso.

### Gerar a documentação

```bash
mvn javadoc:javadoc
```

O resultado fica em `target/site/apidocs/index.html`.

---

## 🚧 Andamento

O desenvolvimento está sendo feito classe por classe, com um commit para cada etapa.

- [x] Estrutura do projeto Maven e dependência JSON
- [x] `Cliente`: atributos, construtores e getters
- [x] `RepositorioJson`: gravação e leitura de clientes
- [ ] Enumerações
- [ ] Funcionários, autenticação e sessão
- [ ] Lojinha e lanchonete
- [ ] Agenda, reservas e pagamentos
- [ ] Catraca e relatórios de acesso
- [ ] Despesas, relatório de vendas e balanço mensal
- [ ] Fachada `Sistema` e persistência completa
- [ ] Controllers e interface de console
- [ ] JavaDoc completo

---

## 🔒 Cuidados com os dados

- O **CPF** é pseudonimizado antes de ser armazenado; o número original nunca é salvo.
- **Senhas** são guardadas apenas como hash.
- O **número do cartão** não é gravado: o sistema guarda somente a referência devolvida pelo serviço de pagamento.

---

## 👩‍💻 Autoria

Desenvolvido por **Aline Ferreira Pena**.

Bacharelado em Sistemas de Informação, Universidade Federal dos Vales do Jequitinhonha e Mucuri (UFVJM), Diamantina, 2026.
