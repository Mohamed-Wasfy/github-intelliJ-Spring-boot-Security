CREATE TABLE carts
(
    id           UUID DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    date_created DATE DEFAULT CURRENT_DATE      NOT NULL
);