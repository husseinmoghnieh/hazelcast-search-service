SET SCHEMA public;

DROP TABLE IF EXISTS invoice;
DROP TABLE IF EXISTS payee;
DROP TABLE IF EXISTS payor;

CREATE TABLE invoice (
  invoice_id INT AUTO_INCREMENT  PRIMARY KEY,
  invoice_number VARCHAR(250) NOT NULL,
  payor_id INT,
  payee_id INT,
  amount decimal default 0,
  invoice_date Date
);

--
 CREATE TABLE payee (
   payee_id INT AUTO_INCREMENT  PRIMARY KEY,
   name VARCHAR(250) NOT NULL
 );

 CREATE TABLE payor (
   payor_id INT AUTO_INCREMENT  PRIMARY KEY,
   name VARCHAR(250) NOT NULL
 );

INSERT INTO payee (payee_id, name) VALUES
(1,'Sonobi'),
(2,'Adrenalin'),
(3,'2 be');

INSERT INTO payor (payor_id, name) VALUES
(1,'Sinclair'),
(2,'Alpha-Agency'),
(3,'Agency-3');

INSERT INTO invoice (invoice_number, payor_id, payee_id, amount, invoice_date) VALUES
('111', 1, 1, 100.12, parsedatetime('03/01/2020', 'MM/dd/yyyy')),
('222', 1, 2, 41.86, parsedatetime('03/10/2019', 'MM/dd/yyyy')),
('333', 1, 2, 2000.00, parsedatetime('04/01/2018', 'MM/dd/yyyy')),
('444', 2, 2, 3000.12, parsedatetime('11/04/2018', 'MM/dd/yyyy')),
('555', 2, 1, 5328.36, parsedatetime('05/01/2020', 'MM/dd/yyyy')),
('666', 2, 3, 341.00, parsedatetime('04/01/2017', 'MM/dd/yyyy')),
('777', 2, 3, 351.00, parsedatetime('10/01/2019', 'MM/dd/yyyy')),
('888', 2, 1, 161.00, parsedatetime('02/01/2020', 'MM/dd/yyyy')),
('999', 3, 2, 96.98, parsedatetime('06/01/2020', 'MM/dd/yyyy'));





-- INSERT INTO invoice (invoice_number, payor_name, payee_name, amount) VALUES
--   ('12344', 'Sonobi', 'google', parsedatetime('03/01/2020', 'MM/dd/yyyy'), 3000.12),
--   ('Folrunsho', 'Alakija', 'Billionaire Oil Magnate', parsedatetime('03/01/2020', 'MM/dd/yyyy'), 22201.10),
--   ('1213', 'sdsa', 'dsa', parsedatetime('03/01/2020', 'MM/dd/yyyy'), 2222.12);


