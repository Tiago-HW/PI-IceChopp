<?php
/* =====================================================================
   REGISTRAR PEDIDO
   Recebe (POST):
     nome, tel, tipo, data (opcional), msg (opcional)
     itens[ID_DO_PRODUTO] = quantidade      ex.: itens[3]=2  (2 unidades do produto 3)
   Grava o pedido, os itens comprados com a quantidade e desconta do estoque.
   Tudo dentro de uma transação: ou grava tudo, ou não grava nada.
   ===================================================================== */

declare(strict_types=1);

require_once __DIR__ . '/conexao.php';
require_once __DIR__ . '/funcoes.php';

exigir_post();

// ---------- 1) dados do cliente ----------
$nome     = campo('nome', 120);
$telefone = campo('tel', 30);
$tipo     = campo('tipo', 40);
$mensagem = campo('msg', COMENTARIO_MAX);
$dataEvt  = campo('data', 10);

if ($nome === '' || $telefone === '') {
    responder(false, 'Preencha nome e telefone.', PAGINA_CONTATO);
}

// data do evento: aceita só AAAA-MM-DD válida; vazio vira NULL
$dataEvento = null;
if ($dataEvt !== '') {
    $d = DateTime::createFromFormat('Y-m-d', $dataEvt);
    if (!$d || $d->format('Y-m-d') !== $dataEvt) {
        responder(false, 'Data do evento inválida.', PAGINA_CONTATO);
    }
    $dataEvento = $dataEvt;
}

// ---------- 2) itens: itens[id] = quantidade ----------
$itens = [];
$bruto = $_POST['itens'] ?? [];
if (is_array($bruto)) {
    foreach ($bruto as $idProduto => $qtd) {
        $id  = filter_var($idProduto, FILTER_VALIDATE_INT);
        $qtd = filter_var($qtd, FILTER_VALIDATE_INT);
        if ($id !== false && $qtd !== false && $qtd > 0) {
            $itens[$id] = min($qtd, PEDIDO_QTD_MAX_POR_ITEM);
        }
    }
}

// ---------- 3) grava tudo ----------
$pdo = conectar();

try {
    $pdo->beginTransaction();

    $stmt = $pdo->prepare(
        'INSERT INTO pedidos (nome_cliente, telefone, tipo_atendimento, data_evento, mensagem)
         VALUES (:nome, :tel, :tipo, :data, :msg)'
    );
    $stmt->execute([
        ':nome' => $nome,
        ':tel'  => $telefone,
        ':tipo' => $tipo,
        ':data' => $dataEvento,
        ':msg'  => $mensagem,
    ]);
    $pedidoId = (int) $pdo->lastInsertId();

    $buscar = $pdo->prepare('SELECT id, nome, preco, estoque FROM produtos WHERE id = :id AND ativo = 1 FOR UPDATE');
    $inserir = $pdo->prepare(
        'INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario)
         VALUES (:pedido, :produto, :qtd, :preco)'
    );
    $baixar = $pdo->prepare('UPDATE produtos SET estoque = estoque - :qtd WHERE id = :id');

    foreach ($itens as $idProduto => $qtd) {
        $buscar->execute([':id' => $idProduto]);
        $produto = $buscar->fetch();

        if (!$produto) {
            throw new RuntimeException('Produto não encontrado.');
        }
        if ((int) $produto['estoque'] < $qtd) {
            throw new RuntimeException('Estoque insuficiente para ' . $produto['nome'] . '.');
        }

        $inserir->execute([
            ':pedido'  => $pedidoId,
            ':produto' => $idProduto,
            ':qtd'     => $qtd,
            ':preco'   => $produto['preco'],   // guarda o preço da época da compra
        ]);
        $baixar->execute([':qtd' => $qtd, ':id' => $idProduto]);
    }

    $pdo->commit();
    responder(true, 'Pedido enviado! Entraremos em contato em breve.', PAGINA_CONTATO);

} catch (RuntimeException $e) {
    // erro "esperado" (estoque, produto): mostra a mensagem ao cliente
    $pdo->rollBack();
    responder(false, $e->getMessage(), PAGINA_CONTATO);

} catch (Throwable $e) {
    $pdo->rollBack();
    error_log('registrar_pedido: ' . $e->getMessage());
    responder(false, 'Não foi possível enviar o pedido agora.', PAGINA_CONTATO);
}
