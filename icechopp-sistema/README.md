# Sistema icechopp

Sistema desktop em Java (Swing) para gestão da icechopp, com controle de
acesso por time (vendas, compras, admin).

## 1. Passo obrigatório: atualizar o banco

O banco que você exportou (`icechopp (1).sql`) **não tinha** tabela de venda
de verdade (a tabela `vendas` existente guardava dados de vendedor, não
transações) e **não tinha** tabela de pedido. Antes de rodar o sistema:

1. Importe o dump original `icechopp (1).sql` (se ainda não importou).
2. Rode o script **`sql/icechopp_alteracoes.sql`** (incluído neste pacote)
   sobre o mesmo banco. Ele:
   - Remove a tabela antiga `vendas` (redundante com `usuario`).
   - Cria a tabela `venda` (cabeçalho: cliente, vendedor, data, total).
   - Cria a tabela `pedido` (gerado a partir da venda; relacionado a produto,
     quantidade, data de entrega e motorista).
   - Adiciona `cpf_usuario` em `compra` (quem do time de compras registrou).
   - Corrige o typo `qauntidade` → `quantidade` em `compra`.

## 2. Configurar a conexão

Edite `src/main/java/com/icechopp/util/ConexaoBD.java` e ajuste `URL`,
`USUARIO` e `SENHA` conforme o seu MySQL/MariaDB local.

## 3. Abrir no IntelliJ

1. `File > Open` e selecione a pasta `icechopp-sistema` (o `pom.xml` na raiz).
2. Deixe o IntelliJ baixar a dependência do Maven (`mysql-connector-j`).
3. Rode a classe `com.icechopp.Main`.

## 4. Primeiro acesso

O dump original já traz um usuário administrador de exemplo — se ele não
tiver o campo `time` preenchido como `admin`, atualize manualmente:

```sql
UPDATE usuario SET time = 'admin' WHERE login = 'SEU_LOGIN_ADMIN';
```

Use esse usuário para logar e cadastrar os demais (vendas/compras) pela
tela **Usuários (admin)**.

## 5. Estrutura do projeto

```
model/       -> classes que representam as tabelas do banco
dao/         -> uma classe DAO por tabela (JDBC puro, PreparedStatement)
controller/  -> regras de negócio que cruzam mais de uma tabela
                (VendaController: venda + pedido + baixa de estoque em uma
                 transação; CompraController: compra + entrada em estoque)
view/        -> telas Swing (uma por funcionalidade)
util/        -> ConexaoBD (JDBC) e SessaoUsuario (usuário logado em memória)
```

## 6. Regras de negócio implementadas

- **Login por time**: `usuario.time` define o que aparece no menu
  (`vendas`, `compras` ou `admin`). Admin vê tudo.
- **Vendas**: cadastra clientes e realiza vendas. Cada venda gera 1
  pedido por item, relacionado a um motorista, com data de entrega,
  quantidade e nome do produto. Pode consultar estoque.
- **Compras**: registra compra de matéria-prima com um fornecedor. A
  matéria-prima entra no estoque apenas com quantidade e data de
  entrada (sem valor unitário).
- **Admin**: acesso total, cadastra/edita usuários e produtos.

## 7. Observação importante

Este código foi escrito e revisado manualmente (chaves balanceadas,
imports conferidos), mas **não foi compilado** neste ambiente porque não
há JDK/Maven disponível aqui. Ao abrir no IntelliJ, se o Maven acusar
algum erro de compilação, me avise a mensagem exata que eu corrijo.

## 8. Melhorias desta versão

- **Janela única com abas**: o sistema inteiro agora abre em uma única
  janela (`MainView`), com uma aba por funcionalidade (`JTabbedPane`).
  Nenhum botão abre mais uma janela nova — todas as telas antigas
  (`ClienteView`, `CompraView`, `EstoqueView` etc.) viraram `JPanel` e
  são incluídas como abas.
- **Busca multi-fator na tela "Realizar Venda"**: campo de pesquisa que
  filtra o cliente por nome, telefone OU e-mail ao mesmo tempo (conforme
  você digita), e outro campo que filtra o produto por nome ou tipo.
- **Seletor de data em calendário**: novo componente
  `view/components/DatePickerField.java`, sem dependências externas, com
  popup de calendário (mês/ano navegáveis, atalho "Hoje"/"Limpar"). Usado
  na data de entrega da venda e na tela de Pedidos/Entregas.
- **Fluxo de Caixa** (`view/CaixaView.java`): nova aba que cruza o
  histórico de vendas (entradas) e compras (saídas) em uma única linha do
  tempo, com saldo do período. Tem atalhos de filtro (7 dias, 30 dias,
  mês atual, tudo) além do seletor de data por calendário.
- **Identidade visual da marca**: nova classe `util/UITheme.java` com a
  paleta oficial (navy `#0B1D33`, azul `#1167B1`, azul claro `#4FC3F7`,
  azul pálido `#E6F4FF`, dourado `#FFC23C`, cinza `#F2F4F7`) e tipografia
  Montserrat (com fallback automático para SansSerif se a fonte não
  estiver instalada no sistema operacional). Aplicada em todas as telas
  via `UITheme.estilizar(this)`.

### Testes recomendados ao abrir no IntelliJ

1. Rode `mvn clean package` (ou deixe o IntelliJ compilar) e confira se
   não há erro — este código não pôde ser compilado neste ambiente sem
   JDK/Maven, então vale essa conferência extra.
2. Login → verifique se a janela principal já abre com as abas de acordo
   com o time do usuário.
3. Aba "Realizar Venda" → digite parte do nome/telefone/e-mail de um
   cliente cadastrado e confira se a lista filtra; clique no ícone de
   calendário da data de entrega e escolha uma data.
4. Aba "Fluxo de Caixa" → confira se vendas e compras já cadastradas
   aparecem, teste os atalhos de período e o filtro manual por
   calendário.
