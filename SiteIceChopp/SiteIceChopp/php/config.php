<?php
/* =====================================================================
   CONFIGURAÇÃO DO SITE — Ice Chopp
   Todos os outros arquivos PHP carregam este primeiro.
   Aqui ficam: dados do banco, ambiente e limites usados nos formulários.
   ===================================================================== */

declare(strict_types=1);

/* ---------- 1) BANCO DE DADOS (MySQL / MariaDB) ----------
   Em XAMPP/Laragon o padrão é usuário "root" e senha vazia.
   Em hospedagem, troque pelos dados que o painel informar. */
const DB_HOST    = 'localhost';
const DB_NOME    = 'icechopp';
const DB_USUARIO = 'root';
const DB_SENHA   = '';
const DB_CHARSET = 'utf8mb4';

/* ---------- 2) AMBIENTE ----------
   'desenvolvimento' mostra os erros na tela (bom para testar).
   'producao' esconde os erros do visitante e grava no log do servidor. */
const AMBIENTE = 'desenvolvimento';

/* ---------- 3) SITE ---------- */
const SITE_NOME  = 'Ice Chopp';
const FUSO       = 'America/Sao_Paulo';

/* ---------- 4) LIMITES DOS FORMULÁRIOS ---------- */
const PEDIDO_QTD_MAX_POR_ITEM = 500;  // máximo de unidades de um mesmo produto por pedido
const PESQUISA_NOTA_MIN       = 1;    // notas da pesquisa de satisfação
const PESQUISA_NOTA_MAX       = 5;
const COMENTARIO_MAX          = 1000; // caracteres

/* ---------- 5) PÁGINAS PARA REDIRECIONAR APÓS O ENVIO ---------- */
const PAGINA_CONTATO  = '../pages/contato.html';
const PAGINA_PESQUISA = '../pages/pesquisa.html';   // crie esta página quando for usar a pesquisa


/* =====================================================================
   Ajustes automáticos (não precisa mexer daqui para baixo)
   ===================================================================== */
date_default_timezone_set(FUSO);

if (AMBIENTE === 'desenvolvimento') {
    ini_set('display_errors', '1');
    error_reporting(E_ALL);
} else {
    ini_set('display_errors', '0');
    ini_set('log_errors', '1');
    error_reporting(E_ALL);
}
