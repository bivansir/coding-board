-- 007-create-feedback-scores-table.sql
--liquibase formatted sql


--changeset nanodass:007-create-feedback-scores-table
CREATE TABLE feedback_scores (
    feedback_score_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    feedback_id UUID NOT NULL,
    score VARCHAR(50) NOT NULL,
    rank INTEGER NOT NULL
);
--rollback DROP TABLE feedbacks;

--changeset nanodass:007-add-feedback_id_score_uq
ALTER TABLE feedback_scores
ADD CONSTRAINT uq_feedback_id_score UNIQUE (feedback_id, score);
--rollback ALTER TABLE feedbacks DROP CONSTRAINT uq_feedback_id_score;

--changeset nanodass:007-add-feedback_scores_feedbacks_fk
ALTER TABLE feedback_scores
ADD CONSTRAINT fk_feedback_scores_feedbacks
    FOREIGN KEY (feedback_id) REFERENCES feedbacks (feedback_id)
    ON DELETE CASCADE;
--rollback ALTER TABLE feedbacks DROP CONSTRAINT fk_feedbacks_sessions;