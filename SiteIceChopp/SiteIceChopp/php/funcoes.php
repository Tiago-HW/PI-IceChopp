<?php
/* =====================================================================
   FUNÇÕES AUXILIARES usadas pelos formulários
   ===================================================================== */

declare(strict_types=1);

require_once __DIR__ . '/config.php';

/** Lê um campo de texto do POST, sem espaços nas pontas. */
function campo(string $nome, int $max = 255): string
{
    $valor = trim((string) ($_POST[$nome] ?? ''));
    return mb_substr($valor, 0, $max);
}

/** Lê um número inteiro do POST. Retorna null se não for número. */
function campo_int(string $nome): ?int
{
    $valor = filter_var($_POST[$nome] ?? null, FILTER_VALIDATE_INT);
    return $valor === false ? null : $valor;
}

/** Escapa texto antes de imprimir em HTML (evita XSS). */
function e(?string $texto): string
{
    return htmlspecialchars((string) $texto, ENT_QUOTES, 'UTF-8');
}

/** Só aceita requisições POST. */
function exigir_post(): void
{
    if (($_SERVER['REQUEST_METHOD'] ?? '') !== 'POST') {
        http_response_code(405);
        exit('Método não permitido.');
    }
}

/**
 * Termina a requisição.
 * - Se o formulário mandar formato=json (ex.: via fetch), devolve JSON.
 * - Senão, volta para a página informada com ?status=ok ou ?status=erro&msg=...
 */
function responder(bool $ok, string $mensagem, string $paginaDestino): void
{
    if (($_POST['formato'] ?? '') === 'json') {
        header('Content-Type: application/json; charset=utf-8');
        http_response_code($ok ? 200 : 400);
        echo json_encode(['ok' => $ok, 'mensagem' => $mensagem], JSON_UNESCAPED_UNICODE);
        exit;
    }

    $query = http_build_query(['status' => $ok ? 'ok' : 'erro', 'msg' => $mensagem]);
    header('Location: ' . $paginaDestino . '?' . $query);
    exit;
}
