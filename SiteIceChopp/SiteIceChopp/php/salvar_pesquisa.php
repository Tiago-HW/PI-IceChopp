<?php
/* =====================================================================
   SALVAR PESQUISA DE SATISFAÇÃO
   Recebe (POST):
     nota_atendimento, nota_produto, nota_entrega  (1 a 5)
     recomendaria (sim/nao)  comentario (opcional)
     nome (opcional)  pedido_id (opcional, para ligar a pesquisa a uma compra)
   ===================================================================== */

declare(strict_types=1);

require_once __DIR__ . '/conexao.php';
require_once __DIR__ . '/funcoes.php';

exigir_post();

// notas de 1 a 5
$notas = [];
foreach (['nota_atendimento', 'nota_produto', 'nota_entrega'] as $campoNota) {
    $n = campo_int($campoNota);
    if ($n === null || $n < PESQUISA_NOTA_MIN || $n > PESQUISA_NOTA_MAX) {
        responder(false, 'Informe todas as notas de ' . PESQUISA_NOTA_MIN . ' a ' . PESQUISA_NOTA_MAX . '.', PAGINA_PESQUISA);
    }
    $notas[$campoNota] = $n;
}

$recomendaria = campo('recomendaria', 3);
if (!in_array($recomendaria, ['sim', 'nao'], true)) {
    responder(false, 'Diga se recomendaria a Ice Chopp.', PAGINA_PESQUISA);
}

$nome       = campo('nome', 120);
$comentario = campo('comentario', COMENTARIO_MAX);
$pedidoId   = campo_int('pedido_id');   // pode ser null

try {
    $stmt = conectar()->prepare(
        'INSERT INTO pesquisa_satisfacao
            (pedido_id, nome, nota_atendimento, nota_produto, nota_entrega, recomendaria, comentario)
         VALUES (:pedido, :nome, :atend, :prod, :entrega, :rec, :coment)'
    );
    $stmt->execute([
        ':pedido'  => $pedidoId ?: null,
        ':nome'    => $nome !== '' ? $nome : null,
        ':atend'   => $notas['nota_atendimento'],
        ':prod'    => $notas['nota_produto'],
        ':entrega' => $notas['nota_entrega'],
        ':rec'     => $recomendaria === 'sim' ? 1 : 0,
        ':coment'  => $comentario !== '' ? $comentario : null,
    ]);
    responder(true, 'Obrigado pela sua avaliação!', PAGINA_PESQUISA);

} catch (Throwable $e) {
    error_log('salvar_pesquisa: ' . $e->getMessage());
    responder(false, 'Não foi possível salvar sua avaliação agora.', PAGINA_PESQUISA);
}
