-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 29/07/2026 às 00:35
-- Versão do servidor: 10.4.32-MariaDB
-- Versão do PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `icechopp`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `caminhao`
--

CREATE TABLE `caminhao` (
  `placa` varchar(7) NOT NULL,
  `marca` varchar(50) NOT NULL,
  `ano` int(11) NOT NULL,
  `modelo` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `caminhao`
--

INSERT INTO `caminhao` (`placa`, `marca`, `ano`, `modelo`) VALUES
('MD4F4', 'Mercedez', 2015, 'M200'),
('MD6G5', 'Scania', 2020, 'S200');

-- --------------------------------------------------------

--
-- Estrutura para tabela `cliente`
--

CREATE TABLE `cliente` (
  `id_cliente` int(11) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `endereco` varchar(50) DEFAULT NULL,
  `telefone` bigint(20) NOT NULL,
  `email` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `cliente`
--

INSERT INTO `cliente` (`id_cliente`, `nome`, `endereco`, `telefone`, `email`) VALUES
(2, 'Carlos', 'rua algodão 123', 51111111111, 'carlos@gmail.com');

-- --------------------------------------------------------

--
-- Estrutura para tabela `compra`
--

CREATE TABLE `compra` (
  `id_compra` int(11) NOT NULL,
  `data_compra` date NOT NULL,
  `valor` double(10,2) NOT NULL,
  `quantidade` int(11) NOT NULL,
  `id_fornecedor` int(11) NOT NULL,
  `id_produto` int(11) NOT NULL,
  `cpf_usuario` varchar(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `compra`
--

INSERT INTO `compra` (`id_compra`, `data_compra`, `valor`, `quantidade`, `id_fornecedor`, `id_produto`, `cpf_usuario`) VALUES
(2, '2026-07-21', 1000.00, 50, 1, 1, '00000000000'),
(4, '2026-07-23', 1200.00, 200, 1, 1, '00000000000'),
(5, '2026-07-23', 12000.00, 12, 1, 1, '00000000000'),
(6, '2026-07-23', 100.00, 1, 1, 1, '00000000000'),
(7, '2026-07-24', 120.00, 1, 1, 1, '00000000000'),
(8, '2026-07-24', 12.00, 1, 1, 1, '00000000000'),
(9, '2026-07-28', 1000.00, 20, 1, 1, '00000000000'),
(10, '2026-07-28', 1000.00, 100, 1, 1, '00000000000'),
(11, '2026-07-28', 1000.00, 100, 1, 1, '00000000000'),
(12, '2026-07-28', 1000.00, 100, 1, 1, '00000000000'),
(13, '2026-07-28', 10000000.00, 100000, 1, 1, '00000000000');

-- --------------------------------------------------------

--
-- Estrutura para tabela `estoque`
--

CREATE TABLE `estoque` (
  `id_produto` int(11) NOT NULL,
  `nome_produto` varchar(100) NOT NULL,
  `quantidade` int(11) NOT NULL DEFAULT 0,
  `valorUn` double(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `estoque`
--

INSERT INTO `estoque` (`id_produto`, `nome_produto`, `quantidade`, `valorUn`) VALUES
(1, 'Malte', 76448, NULL),
(4, 'Barril-Chopp 5L', 100, 50.00),
(5, 'Barril-Chopp 10L', 190, 100.00),
(6, 'Barril-Chopp 10L', 70, 200.00),
(7, 'Barril-Chopp 50L', 80, 500.00),
(8, 'Garrafa Chopp 1,5L', 61, 15.00);

-- --------------------------------------------------------

--
-- Estrutura para tabela `fornecedor`
--

CREATE TABLE `fornecedor` (
  `id_fornecedor` int(11) NOT NULL,
  `nome` varchar(30) NOT NULL,
  `email` varchar(30) DEFAULT NULL,
  `telefone` varchar(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `fornecedor`
--

INSERT INTO `fornecedor` (`id_fornecedor`, `nome`, `email`, `telefone`) VALUES
(1, 'João do Malte', 'joãoMALTE@gmail.com', '51333333333');

-- --------------------------------------------------------

--
-- Estrutura para tabela `motorista`
--

CREATE TABLE `motorista` (
  `id_motorista` int(11) NOT NULL,
  `cpf` varchar(11) NOT NULL,
  `endereco` varchar(50) NOT NULL,
  `telefone` bigint(20) NOT NULL,
  `placa_caminhao` varchar(7) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `salario` double(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `motorista`
--

INSERT INTO `motorista` (`id_motorista`, `cpf`, `endereco`, `telefone`, `placa_caminhao`, `nome`, `salario`) VALUES
(1, '22222222222', 'rua da caçamba 123', 51222222222, 'MD4F4', 'Clebão Caminhão', 60000.00),
(2, '44444444444', 'rua 1', 51444444444, 'MD6G5', 'Rojério', 30000.00);

-- --------------------------------------------------------

--
-- Estrutura para tabela `pedido`
--

CREATE TABLE `pedido` (
  `id_pedido` int(11) NOT NULL,
  `id_venda` int(11) NOT NULL,
  `id_produto` int(11) NOT NULL,
  `nome_produto` varchar(100) NOT NULL,
  `quantidade` int(11) NOT NULL,
  `data_entrega` date DEFAULT NULL,
  `id_motorista` int(11) DEFAULT NULL,
  `status` varchar(30) NOT NULL DEFAULT 'PENDENTE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `pedido`
--

INSERT INTO `pedido` (`id_pedido`, `id_venda`, `id_produto`, `nome_produto`, `quantidade`, `data_entrega`, `id_motorista`, `status`) VALUES
(1, 1, 5, 'Barril-Chopp 10L', 10, '2026-08-25', 2, 'PENDENTE'),
(2, 1, 7, 'Barril-Chopp 50L', 20, '2026-08-25', 1, 'PENDENTE'),
(3, 1, 6, 'Barril-Chopp 20L', 30, '2026-08-25', 1, 'EM_ROTA'),
(4, 1, 8, 'Garrafa Chopp 1,5L', 40, '2026-08-25', 2, 'PENDENTE');

-- --------------------------------------------------------

--
-- Estrutura para tabela `producao`
--

CREATE TABLE `producao` (
  `id` int(11) NOT NULL,
  `id_produto` int(11) NOT NULL,
  `quantidade` int(11) NOT NULL,
  `data_producao` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `producao`
--

INSERT INTO `producao` (`id`, `id_produto`, `quantidade`, `data_producao`) VALUES
(4, 8, 1, '2026-07-23 23:12:16'),
(5, 5, 200, '2026-07-28 22:30:23'),
(6, 6, 100, '2026-07-28 22:30:30'),
(7, 7, 100, '2026-07-28 22:30:35'),
(8, 4, 100, '2026-07-28 22:30:42'),
(9, 8, 100, '2026-07-28 22:30:47');

-- --------------------------------------------------------

--
-- Estrutura para tabela `produto`
--

CREATE TABLE `produto` (
  `id_produto` int(11) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `valorUn` double(10,2) DEFAULT NULL,
  `tipo` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `produto`
--

INSERT INTO `produto` (`id_produto`, `nome`, `valorUn`, `tipo`) VALUES
(1, 'Malte', NULL, 'MATERIA_PRIMA'),
(4, 'Barril-Chopp 5L', 50.00, 'PRODUTO_FINAL'),
(5, 'Barril-Chopp 10L', 100.00, 'PRODUTO_FINAL'),
(6, 'Barril-Chopp 20L', 200.00, 'PRODUTO_FINAL'),
(7, 'Barril-Chopp 50L', 500.00, 'PRODUTO_FINAL'),
(8, 'Garrafa Chopp 1,5L', 15.00, 'PRODUTO_FINAL');

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuario`
--

CREATE TABLE `usuario` (
  `cpf` varchar(11) NOT NULL,
  `nome_funcionario` varchar(50) NOT NULL,
  `telefone` bigint(20) NOT NULL,
  `endereco` varchar(50) NOT NULL,
  `salario` double(10,2) NOT NULL,
  `login` varchar(50) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `time` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `usuario`
--

INSERT INTO `usuario` (`cpf`, `nome_funcionario`, `telefone`, `endereco`, `salario`, `login`, `senha`, `time`) VALUES
('00000000000', 'Administrador', 5100000000, 'Sede icechopp', 5000.00, 'admin', 'admin123', 'admin'),
('11111111111', 'Funcionário Vendas', 5100000001, 'Rua A, 100', 2500.00, 'vendas1', '123', 'vendas'),
('22222222222', 'Funcionário Compras', 5100000002, 'Rua B, 200', 2500.00, 'compras1', '123', 'compras');

-- --------------------------------------------------------

--
-- Estrutura para tabela `venda`
--

CREATE TABLE `venda` (
  `id_venda` int(11) NOT NULL,
  `id_cliente` int(11) NOT NULL,
  `cpf_usuario` varchar(11) NOT NULL,
  `data_venda` datetime NOT NULL DEFAULT current_timestamp(),
  `valor_total` double(10,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `venda`
--

INSERT INTO `venda` (`id_venda`, `id_cliente`, `cpf_usuario`, `data_venda`, `valor_total`) VALUES
(1, 2, '00000000000', '2026-07-28 22:31:47', 17600.00);

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `caminhao`
--
ALTER TABLE `caminhao`
  ADD PRIMARY KEY (`placa`);

--
-- Índices de tabela `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`id_cliente`);

--
-- Índices de tabela `compra`
--
ALTER TABLE `compra`
  ADD PRIMARY KEY (`id_compra`),
  ADD KEY `fk_compra_fornecedor` (`id_fornecedor`),
  ADD KEY `fk_compra_produto` (`id_produto`),
  ADD KEY `fk_compra_usuario` (`cpf_usuario`);

--
-- Índices de tabela `estoque`
--
ALTER TABLE `estoque`
  ADD PRIMARY KEY (`id_produto`);

--
-- Índices de tabela `fornecedor`
--
ALTER TABLE `fornecedor`
  ADD PRIMARY KEY (`id_fornecedor`);

--
-- Índices de tabela `motorista`
--
ALTER TABLE `motorista`
  ADD PRIMARY KEY (`id_motorista`),
  ADD UNIQUE KEY `cpf` (`cpf`),
  ADD KEY `fk_motorista_caminhao` (`placa_caminhao`);

--
-- Índices de tabela `pedido`
--
ALTER TABLE `pedido`
  ADD PRIMARY KEY (`id_pedido`),
  ADD KEY `fk_pedido_venda` (`id_venda`),
  ADD KEY `fk_pedido_produto` (`id_produto`),
  ADD KEY `fk_pedido_motorista` (`id_motorista`);

--
-- Índices de tabela `producao`
--
ALTER TABLE `producao`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_produto` (`id_produto`);

--
-- Índices de tabela `produto`
--
ALTER TABLE `produto`
  ADD PRIMARY KEY (`id_produto`);

--
-- Índices de tabela `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`cpf`),
  ADD UNIQUE KEY `login` (`login`);

--
-- Índices de tabela `venda`
--
ALTER TABLE `venda`
  ADD PRIMARY KEY (`id_venda`),
  ADD KEY `fk_venda_cliente` (`id_cliente`),
  ADD KEY `fk_venda_usuario` (`cpf_usuario`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `cliente`
--
ALTER TABLE `cliente`
  MODIFY `id_cliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de tabela `compra`
--
ALTER TABLE `compra`
  MODIFY `id_compra` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de tabela `fornecedor`
--
ALTER TABLE `fornecedor`
  MODIFY `id_fornecedor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de tabela `motorista`
--
ALTER TABLE `motorista`
  MODIFY `id_motorista` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de tabela `pedido`
--
ALTER TABLE `pedido`
  MODIFY `id_pedido` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de tabela `producao`
--
ALTER TABLE `producao`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de tabela `produto`
--
ALTER TABLE `produto`
  MODIFY `id_produto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de tabela `venda`
--
ALTER TABLE `venda`
  MODIFY `id_venda` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `compra`
--
ALTER TABLE `compra`
  ADD CONSTRAINT `fk_compra_fornecedor` FOREIGN KEY (`id_fornecedor`) REFERENCES `fornecedor` (`id_fornecedor`),
  ADD CONSTRAINT `fk_compra_produto` FOREIGN KEY (`id_produto`) REFERENCES `produto` (`id_produto`),
  ADD CONSTRAINT `fk_compra_usuario` FOREIGN KEY (`cpf_usuario`) REFERENCES `usuario` (`cpf`);

--
-- Restrições para tabelas `estoque`
--
ALTER TABLE `estoque`
  ADD CONSTRAINT `fk_estoque_produto` FOREIGN KEY (`id_produto`) REFERENCES `produto` (`id_produto`) ON DELETE CASCADE;

--
-- Restrições para tabelas `motorista`
--
ALTER TABLE `motorista`
  ADD CONSTRAINT `fk_motorista_caminhao` FOREIGN KEY (`placa_caminhao`) REFERENCES `caminhao` (`placa`);

--
-- Restrições para tabelas `pedido`
--
ALTER TABLE `pedido`
  ADD CONSTRAINT `fk_pedido_motorista` FOREIGN KEY (`id_motorista`) REFERENCES `motorista` (`id_motorista`),
  ADD CONSTRAINT `fk_pedido_produto` FOREIGN KEY (`id_produto`) REFERENCES `produto` (`id_produto`),
  ADD CONSTRAINT `fk_pedido_venda` FOREIGN KEY (`id_venda`) REFERENCES `venda` (`id_venda`) ON DELETE CASCADE;

--
-- Restrições para tabelas `producao`
--
ALTER TABLE `producao`
  ADD CONSTRAINT `producao_ibfk_1` FOREIGN KEY (`id_produto`) REFERENCES `produto` (`id_produto`);

--
-- Restrições para tabelas `venda`
--
ALTER TABLE `venda`
  ADD CONSTRAINT `fk_venda_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  ADD CONSTRAINT `fk_venda_usuario` FOREIGN KEY (`cpf_usuario`) REFERENCES `usuario` (`cpf`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
