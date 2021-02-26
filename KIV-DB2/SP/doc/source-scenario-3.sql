-- odkrytí políčka
INSERT INTO TAH
    (POLE_ID)
    VALUES
    ((SELECT ID FROM POLE WHERE X = 3 AND Y = 5 AND OBLAST_ID = 0));
    
-- odkrytí všech nezaminovaných polí - pro testování
EXECUTE ODKRYJ_OBLAST(0);