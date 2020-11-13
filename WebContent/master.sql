CREATE TABLE daily_report_system.departments (
  id INT NOT NULL PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

CREATE TABLE daily_report_system.authorities (
  id INT NOT NULL PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

INSERT INTO daily_report_system.departments (id, name) VALUES
  (0, "-"),
  (1, "総務課"),
  (2, "経理課"),
  (3, "人事課")
;

INSERT INTO daily_report_system.authorities (id, name) VALUES
  (0, "-"),
  (1, "管理者"),
  (2, "部長"),
  (3, "課長")
;
