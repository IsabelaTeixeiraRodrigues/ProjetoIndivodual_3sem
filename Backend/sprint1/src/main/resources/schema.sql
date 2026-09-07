CREATE TABLE tipo (
id INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE ataque (
id INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE time (
id INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(50) NOT NULL
);

CREATE TABLE pokemon (
id INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(20) NOT NULL,
nivel INT NOT NULL,
data_captura DATE NOT NULL,
shiny BOOLEAN NOT NULL DEFAULT FALSE,
tipo_id INT NOT NULL,
time_id INT NOT NULL,
FOREIGN KEY (tipo_id) REFERENCES tipo(id),
FOREIGN KEY (time_id) REFERENCES `time`(id)
);

CREATE TABLE pokemon_ataque (
pokemon_id INT NOT NULL,
ataque_id INT NOT NULL,
PRIMARY KEY (pokemon_id, ataque_id),
FOREIGN KEY (pokemon_id) REFERENCES pokemon(id) ON DELETE CASCADE,
FOREIGN KEY (ataque_id) REFERENCES ataque(id)
);

INSERT INTO tipo (nome) VALUES
('Normal'),
('Fogo'),
('Água'),
('Grama'),
('Elétrico'),
('Gelo'),
('Lutador'),
('Venenoso'),
('Terrestre'),
('Voador'),
('Psíquico'),
('Inseto'),
('Pedra'),
('Fantasma'),
('Dragão'),
('Sombrio'),
('Aço'),
('Fada');

INSERT INTO ataque (nome) VALUES
('Investida'),
('Lança-Chamas'),
('Hidrobomba'),
('Trovada'),
('Folha Navalha'),
('Corte'),
('Bola Sombria'),
('Terremoto'),
('Voar'),
('Fúria');

INSERT INTO time (nome) VALUES
('Meu Time');