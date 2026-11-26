<?php
/* =====================================================================
   CONEXÃO COM O BANCO (PDO)
   Uso em qualquer arquivo:
       require_once __DIR__ . '/conexao.php';
       $pdo = conectar();
   ===================================================================== */

declare(strict_types=1);

require_once __DIR__ . '/config.php';

function conectar(): PDO
{
    // guarda a conexão para não abrir uma nova a cada chamada
    static $pdo = null;
    if ($pdo instanceof PDO) {
        return $pdo;
    }

    $dsn = 'mysql:host=' . DB_HOST . ';dbname=' . DB_NOME . ';charset=' . DB_CHARSET;

    try {
        $pdo = new PDO($dsn, DB_USUARIO, DB_SENHA, [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,   // erros viram exceções
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,                    // prepared statements de verdade
        ]);
    } catch (PDOException $e) {
        error_log('Falha na conexão: ' . $e->getMessage());
        http_response_code(500);
        exit(AMBIENTE === 'desenvolvimento'
            ? 'Erro ao conectar no banco: ' . $e->getMessage()
            : 'Serviço indisponível no momento. Tente novamente mais tarde.');
    }

    return $pdo;
}
