-- =====================================================================
-- SCRIPT DE ALTERAÇÃO DO BANCO icechopp
-- Rode este script sobre o banco já existente (após importar o dump atual)
-- =====================================================================

USE `icechopp`;

-- ---------------------------------------------------------------------
-- 1) Remover a tabela `vendas` antiga (era, na verdade, dado de vendedor
--    e é redundante com `usuario`, que já guarda cpf/telefone/endereco/
--    salario/time). Ela não representa uma transação de venda.
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `vendas`;

-- ---------------------------------------------------------------------
-- 2) Nova tabela `venda`: cabeçalho da venda (cliente + vendedor + data)
-- ---------------------------------------------------------------------
CREATE TABLE `venda` (
  `id_venda` int(11) NOT NULL AUTO_INCREMENT,
  `id_cliente` int(11) NOT NULL,
  `cpf_usuario` varchar(11) NOT NULL,
  `data_venda` datetime NOT NULL DEFAULT current_timestamp(),
  `valor_total` double(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id_venda`),
  KEY `fk_venda_cliente` (`id_cliente`),
  KEY `fk_venda_usuario` (`cpf_usuario`),
  CONSTRAINT `fk_venda_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `fk_venda_usuario` FOREIGN KEY (`cpf_usuario`) REFERENCES `usuario` (`cpf`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ---------------------------------------------------------------------
-- 3) Nova tabela `pedido`: gerado a partir de uma venda, relacionado a
--    um motorista (entrega), com produto/quantidade/data de entrega.
--    Cada linha = 1 produto entregue dentro de uma venda.
-- ---------------------------------------------------------------------
CREATE TABLE `pedido` (
  `id_pedido` int(11) NOT NULL AUTO_INCREMENT,
  `id_venda` int(11) NOT NULL,
  `id_produto` int(11) NOT NULL,
  `nome_produto` varchar(100) NOT NULL,
  `quantidade` int(11) NOT NULL,
  `data_entrega` date DEFAULT NULL,
  `id_motorista` int(11) DEFAULT NULL,
  `status` varchar(30) NOT NULL DEFAULT 'PENDENTE',
  PRIMARY KEY (`id_pedido`),
  KEY `fk_pedido_venda` (`id_venda`),
  KEY `fk_pedido_produto` (`id_produto`),
  KEY `fk_pedido_motorista` (`id_motorista`),
  CONSTRAINT `fk_pedido_venda` FOREIGN KEY (`id_venda`) REFERENCES `venda` (`id_venda`) ON DELETE CASCADE,
  CONSTRAINT `fk_pedido_produto` FOREIGN KEY (`id_produto`) REFERENCES `produto` (`id_produto`),
  CONSTRAINT `fk_pedido_motorista` FOREIGN KEY (`id_motorista`) REFERENCES `motorista` (`id_motorista`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ---------------------------------------------------------------------
-- 4) `compra`: registrar quem (usuário do time compras) fez a compra
-- ---------------------------------------------------------------------
ALTER TABLE `compra`
  ADD COLUMN `cpf_usuario` varchar(11) DEFAULT NULL AFTER `id_produto`,
  ADD CONSTRAINT `fk_compra_usuario` FOREIGN KEY (`cpf_usuario`) REFERENCES `usuario` (`cpf`);

-- corrigindo o typo de coluna existente (qauntidade -> quantidade)
ALTER TABLE `compra` CHANGE `qauntidade` `quantidade` int(6) NOT NULL;

-- ---------------------------------------------------------------------
-- 5) `produto`: usar `tipo` para distinguir matéria-prima de produto final
--    Valores esperados: 'MATERIA_PRIMA' ou 'PRODUTO_FINAL'
--    (aplicação já usa isso; nenhuma alteração estrutural necessária,
--    apenas documentando a convenção)
-- ---------------------------------------------------------------------

-- ---------------------------------------------------------------------
-- 6) `estoque`: matéria-prima entra sem valorUn (fica NULL), então a
--    coluna já aceita NULL — nenhuma alteração estrutural necessária.
-- ---------------------------------------------------------------------

-- Fim do script.
