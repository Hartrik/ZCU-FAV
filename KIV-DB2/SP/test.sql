----------------------------------------------------------------------------
--- ZAHÁJENÍ HRY
----------------------------------------------------------------------------
-- pozn. první vytvoøená oblast bude mít ID=0

INSERT INTO OBLAST
    (OBTIZNOST_ID)
    VALUES
    ((SELECT ID FROM OBTIZNOST WHERE NAZEV = 'zaèáteèník'));

INSERT INTO OBLAST
    (RADKY, SLOUPCE, MINY)
    VALUES
    (10, 10, 5);

----------------------------------------------------------------------------
--- OZNAÈENÍ MIN
----------------------------------------------------------------------------

INSERT INTO MINA
    (POLE_ID)
    VALUES
    ((SELECT ID FROM POLE WHERE X = 5 AND Y = 4 AND OBLAST_ID = 0));

DELETE FROM MINA WHERE
    POLE_ID = ((SELECT ID FROM POLE WHERE X = 2 AND Y = 2 AND OBLAST_ID = 0));

-- oznaèení všech min - pro testování
EXECUTE OZNAC_MINY(0);

----------------------------------------------------------------------------
--- ODKRYTÍ POLÍÈKA
----------------------------------------------------------------------------

INSERT INTO TAH
    (POLE_ID)
    VALUES
    ((SELECT ID FROM POLE WHERE X = 3 AND Y = 5 AND OBLAST_ID = 0));

-- odkrytí všech nezaminovaných polí - pro testování
EXECUTE ODKRYJ_OBLAST(0);

----------------------------------------------------------------------------
--- TISK OBLASTI
----------------------------------------------------------------------------

SELECT RADKA FROM OBLAST_TISK WHERE OBLAST_ID = 0;

----------------------------------------------------------------------------
--- PO SKOÈENÍ, PØEHLEDY
----------------------------------------------------------------------------

-- chybnì oznaèené miny
SELECT X, Y FROM CHYBNE_MINY WHERE OBLAST_ID = 0;

-- pøehledy
SELECT * FROM HRA;
SELECT * FROM VITEZOVE;
SELECT * FROM PORAZENI;
