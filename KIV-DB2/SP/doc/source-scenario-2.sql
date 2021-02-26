-- označení miny
INSERT INTO MINA
    (POLE_ID)
    VALUES
    ((SELECT ID FROM POLE WHERE X = 5 AND Y = 4 AND OBLAST_ID = 0));

-- zrušení označení
DELETE FROM MINA WHERE POLE_ID = 
    ((SELECT ID FROM POLE WHERE X = 2 AND Y = 2 AND OBLAST_ID = 0));

-- označení všech min - pro testování
EXECUTE OZNAC_MINY(0);