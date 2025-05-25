CREATE TABLE IF NOT EXISTS tour_tags (
    id BIGINT AUTO_INCREMENT,
    tour_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY(id)
);

ALTER TABLE tour_tags
ADD CONSTRAINT fk_tour_tags_tag_id_tags FOREIGN KEY (tag_id) REFERENCES tags(id);

ALTER TABLE tour_tags
ADD CONSTRAINT fk_tour_tags_tour_id_tours FOREIGN KEY (tour_id) REFERENCES tours(id);

