CREATE KEYSPACE IF NOT EXISTS juke_box_system WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE juke_box_system;

CREATE TABLE IF NOT EXISTS juke_boxes (
    model TEXT,
    setting UUID,
    id TEXT,
    components SET<TEXT>,
    PRIMARY KEY ((setting), model, id)
);
