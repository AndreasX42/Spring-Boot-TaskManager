INSERT INTO users (username, email, password, role)
VALUES ('John Casey17', 'john.casey17@example.com', 'J0hnD!123', 'USER');

INSERT INTO users (username, email, password, role)
VALUES ('Jane Smith123', 'jane.smith123@example.com', 'JaneS@321', 'USER');

INSERT INTO users (username, email, password, role)
VALUES ('Emily Johnson21', 'emily.johnson21@example.com', 'Em!lyJ456', 'USER');

INSERT INTO todos (name, priority, status, until_date, user_id)
VALUES ('Sample Todo of user John Casey17', 'MID', 'OPEN', DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), 1);

INSERT INTO todos (name, priority, status, until_date, user_id)
VALUES ('Sample Todo of user Jane Smith123', 'MID', 'OPEN', DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), 2);

INSERT INTO todos (name, priority, status, until_date, user_id)
VALUES ('Sample Todo of user Emily Johnson21', 'MID', 'OPEN', DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), 3);