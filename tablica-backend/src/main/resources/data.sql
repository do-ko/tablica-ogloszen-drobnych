-- OFFER TAGS
INSERT INTO offer_tag (tag)
SELECT 'Electronics' WHERE NOT EXISTS (
  SELECT 1 FROM offer_tag WHERE LOWER(tag) = LOWER('Electronics')
);

INSERT INTO offer_tag (tag)
SELECT 'Books' WHERE NOT EXISTS (
  SELECT 1 FROM offer_tag WHERE LOWER(tag) = LOWER('Books')
);

INSERT INTO offer_tag (tag)
SELECT 'Clothing' WHERE NOT EXISTS (
  SELECT 1 FROM offer_tag WHERE LOWER(tag) = LOWER('Clothing')
);