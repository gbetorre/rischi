INSERT INTO command (id, nome, token, jsp, labelweb, informativa) VALUES
(1, 'HomePage', 'home', 'index.jsp', 'Home', 'Command che gestisce la home page'),
(2, 'Process', 'pr', 'prElenco.jsp', 'Processi', 'Command che gestisce i processi'),
(4, 'Department', 'st', 'stElenco.jsp', 'Strutture', 'Command che gestisce le strutture'),
(5, 'Risk', 'ri', 'riElenco.jsp', 'Rischi', 'Command che gestisce i rischi corruttivi'),
(3, 'Report', 'mu', 'muElenco.jsp', 'Report', 'Command che gestisce report, ricerche e statistiche'),
(6, 'Audit', 'in', 'inElenco.jsp', 'Interviste', 'Command che gestisce le interviste'),
(7, 'Measure', 'ms', 'msElenco.jsp', 'Misure', 'Command che gestisce le misure di calmierazione del rischio'),
(8, 'Indicator', 'ic', 'icElenco.jsp', 'Indicatori', 'Command che gestisce gli indicatori di monitoraggio');
ALTER SEQUENCE command_id_seq RESTART WITH 9;
