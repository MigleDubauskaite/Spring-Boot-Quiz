INSERT INTO tema (id, nombre) VALUES (1, 'Matemáticas');

INSERT INTO preguntas (id, enunciado, tipo_pregunta, tema_id, activa)VALUES (1, '¿2+2=4?', 'VF', 1, true);

INSERT INTO pregunta_vf (id) VALUES (1);

INSERT INTO respuesta (texto, correcta, pregunta_id) VALUES ('Verdadero', true, 1);

INSERT INTO respuesta (texto, correcta, pregunta_id)VALUES ('Falso', false, 1);
