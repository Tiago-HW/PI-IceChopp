<?php
/* =====================================================================
   PRODUTOS E ESTOQUE
   Funções para ler o catálogo e consultar quantidades.
   O desconto de estoque acontece em registrar_pedido.php
   ===================================================================== */

declare(strict_types=1);

require_once __DIR__ . '/conexao.php';

/** Lista os produtos ativos. Filtre por categoria: 'chopp', 'barril', 'equipamento' ou 'insumo'. */
function listar_produtos(?string $categoria = null): array
{
    $pdo = conectar();

    if ($categoria === null) {
        return $pdo->query('SELECT * FROM produtos WHERE ativo = 1 ORDER BY categoria, nome')->fetchAll();
    }

    $stmt = $pdo->prepare('SELECT * FROM produtos WHERE ativo = 1 AND categoria = :cat ORDER BY nome');
    $stmt->execute([':cat' => $categoria]);
    return $stmt->fetchAll();
}

/** Busca um produto pelo id (ou null). */
function buscar_produto(int $id): ?array
{
    $stmt = conectar()->prepare('SELECT * FROM produtos WHERE id = :id');
    $stmt->execute([':id' => $id]);
    return $stmt->fetch() ?: null;
}

/** Quantidade em estoque de um produto (0 se não existir). */
function estoque_do_produto(int $id): int
{
    $stmt = conectar()->prepare('SELECT estoque FROM produtos WHERE id = :id');
    $stmt->execute([':id' => $id]);
    return (int) ($stmt->fetchColumn() ?: 0);
}
