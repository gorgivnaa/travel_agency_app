CREATE TABLE IF NOT EXISTS manager_tours (
    id BIGINT AUTO_INCREMENT,
    manager_id BIGINT NOT NULL,
    tour_id BIGINT NOT NULL,
    PRIMARY KEY(id)
);

ALTER TABLE manager_tours
ADD CONSTRAINT fk_manager_tours_manager_id_users FOREIGN KEY (manager_id) REFERENCES users(id);

ALTER TABLE manager_tours
ADD CONSTRAINT fk_manager_tours_tour_id_users FOREIGN KEY (tour_id) REFERENCES tours(id);

