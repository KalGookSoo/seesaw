-- 제약조건(보관용)
ALTER TABLE tb_menu_role ADD CONSTRAINT fk_tb_menu_role FOREIGN KEY (menu_id) REFERENCES tb_menu (id);
ALTER TABLE tb_menu_role ADD CONSTRAINT fk_tb_role_menu FOREIGN KEY (role_id) REFERENCES tb_role (id);
ALTER TABLE tb_role_mapping ADD CONSTRAINT fk_tb_mapping_user FOREIGN KEY (user_id) REFERENCES tb_user (id);
ALTER TABLE tb_role_mapping ADD CONSTRAINT fk_tb_mapping_site FOREIGN KEY (site_id) REFERENCES tb_site (id);
ALTER TABLE tb_role_mapping ADD CONSTRAINT fk_tb_mapping_role FOREIGN KEY (role_id) REFERENCES tb_role (id);
ALTER TABLE tb_category ADD CONSTRAINT fk_tb_category_site FOREIGN KEY (site_id) REFERENCES tb_site (id);
ALTER TABLE tb_article ADD CONSTRAINT fk_tb_article_category FOREIGN KEY (category_id) REFERENCES tb_category (id);
ALTER TABLE tb_reply ADD CONSTRAINT fk_tb_reply_article FOREIGN KEY (article_id) REFERENCES tb_article (id);
ALTER TABLE tb_view ADD CONSTRAINT fk_tb_view_article FOREIGN KEY (article_id) REFERENCES tb_article (id);
ALTER TABLE tb_site ADD CONSTRAINT fk_tb_site_parent FOREIGN KEY (parent_id) REFERENCES tb_site (id);
ALTER TABLE tb_category ADD CONSTRAINT fk_tb_category_parent FOREIGN KEY (parent_id) REFERENCES tb_category (id);
ALTER TABLE tb_article ADD CONSTRAINT fk_tb_article_parent FOREIGN KEY (parent_id) REFERENCES tb_article (id);
ALTER TABLE tb_reply ADD CONSTRAINT fk_tb_reply_parent FOREIGN KEY (parent_id) REFERENCES tb_reply (id);
ALTER TABLE tb_menu ADD CONSTRAINT fk_tb_menu_parent FOREIGN KEY (parent_id) REFERENCES tb_menu (id);
ALTER TABLE tb_code ADD CONSTRAINT fk_tb_code_parent FOREIGN KEY (parent_id) REFERENCES tb_code (id);

ALTER TABLE public.tb_role ADD CONSTRAINT uq_tb_role_name UNIQUE (name);
